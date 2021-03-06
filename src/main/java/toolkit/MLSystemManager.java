// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------
package toolkit;

import java.util.Random;


public class MLSystemManager {
	
	/**
	 *  When you make a new learning algorithm, you should add a line for it to this method.
	 *  Note that rand is a random seed generator that can be fixed to facilitate debugging.
	 *  This parameter is controlled in the run method, but should be passed as a parameter 
	 *  to any learner that requires randomization.
	 */
	public SupervisedLearner getLearner(String model, Random rand) throws Exception
	{
		if (model.equals("baseline")) return new BaselineLearner();
		 else if (model.equals("perceptron")) return new Perceptron(rand);
		// else if (model.equals("neuralnet")) return new NeuralNet(rand);
		// else if (model.equals("decisiontree")) return new DecisionTree();
		// else if (model.equals("knn")) return new InstanceBasedLearner();
		else throw new Exception("Unrecognized model: " + model);
	}

	public void run(String[] args) throws Exception {

		//args = new String[]{"-L", "baseline", "-A", "data/iris.arff", "-E", "cross", "10", "-N"};

		//Random rand = new Random(1234); // Use a seed for deterministic results (makes debugging easier)
		Random rand = new Random(); // No seed for non-deterministic results

		//Parse the command line arguments
		ArgParser parser = new ArgParser(args);
		String fileName = parser.getARFF(); //File specified by the user
		String learnerName = parser.getLearner(); //Learning algorithm specified by the user
		String evalMethod = parser.getEvaluation(); //Evaluation method specified by the user
		String evalParameter = parser.getEvalParameter(); //Evaluation parameters specified by the user
		boolean printConfusionMatrix = parser.getVerbose(); 
		boolean normalize = parser.getNormalize();
		

		// Load the ARFF file
		DataMatrix fullDataMatrix = new DataMatrix();
		fullDataMatrix.loadArff(fileName);
		double[][] normalizationRanges = null;
		if (normalize)
		{
			System.out.println("Using normalized data\n");
			normalizationRanges = fullDataMatrix.normalize();
		}

		// Print some stats
		System.out.println();
		System.out.println("Dataset name: " + fileName);
		System.out.println("Number of instances: " + fullDataMatrix.getRowCount());
		System.out.println("Number of attributes: " + fullDataMatrix.getColCount());
		System.out.println("Learning algorithm: " + learnerName);
		System.out.println("Evaluation method: " + evalMethod);
		System.out.println();

		if(learnerName.equals("kmeans") || learnerName.equals("hac")) {
			// Create the unsupervised learning model
			if(learnerName.equals("kmeans")) {
				// new KMeansClusterer(fullDataMatrix);
			} else if (learnerName.equals("hac")){
				// new HierarchicalAgglomerativeClusterer(fullDataMatrix, rand);
			}
		} else {
			// Load the supervised learning model
			SupervisedLearner supervisedLearner = getLearner(learnerName, rand);
	
			if (evalMethod.equals("training"))
			{
				System.out.println("Calculating accuracy on training set...");
				DataMatrix featuresOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, 0, fullDataMatrix.getRowCount(), fullDataMatrix.getColCount() - 1);
				DataMatrix labelsLabelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, fullDataMatrix.getColCount() - 1, fullDataMatrix.getRowCount(), 1);
				DataMatrix confusionMatrix = new DataMatrix();
				double startTime = System.currentTimeMillis();
				supervisedLearner.train(featuresOnlyDataMatrix, labelsLabelsOnlyDataMatrix);
				double elapsedTime = System.currentTimeMillis() - startTime;
				System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
				double predictiveAccuracy = supervisedLearner.measurePredictiveAccuracy(featuresOnlyDataMatrix, labelsLabelsOnlyDataMatrix, confusionMatrix);
				System.out.println("Training set accuracy: " + predictiveAccuracy);
				if(printConfusionMatrix) {
					System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
					confusionMatrix.print();
					System.out.println("\n");
				}
			}
			else if (evalMethod.equals("static"))
			{
				DataMatrix testSetDataMatrix = new DataMatrix();
				testSetDataMatrix.loadArff(evalParameter);
				if (normalize) {
					testSetDataMatrix.normalize(normalizationRanges);
				}
	
				System.out.println("Calculating accuracy on separate test set...");
				System.out.println("Test set name: " + evalParameter);
				System.out.println("Number of test instances: " + testSetDataMatrix.getRowCount());
				DataMatrix featuresOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, 0, fullDataMatrix.getRowCount(), fullDataMatrix.getColCount() - 1);
				DataMatrix labelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, fullDataMatrix.getColCount() - 1, fullDataMatrix.getRowCount(), 1);
				double startTime = System.currentTimeMillis();
				supervisedLearner.train(featuresOnlyDataMatrix, labelsOnlyDataMatrix);
				double elapsedTime = System.currentTimeMillis() - startTime;
				System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
				double predictiveAccuracyOnTrainingDataset = supervisedLearner.measurePredictiveAccuracy(featuresOnlyDataMatrix, labelsOnlyDataMatrix, null);
				System.out.println("Training set accuracy: " + predictiveAccuracyOnTrainingDataset);
				DataMatrix testSetFeaturesOnlyDataMatrix = new DataMatrix(testSetDataMatrix, 0, 0, testSetDataMatrix.getRowCount(), testSetDataMatrix.getColCount() - 1);
				DataMatrix testSetLabelsOnlyDataMatrix = new DataMatrix(testSetDataMatrix, 0, testSetDataMatrix.getColCount() - 1, testSetDataMatrix.getRowCount(), 1);
				DataMatrix confusionMatrix = new DataMatrix();
				double predictiveAccuracyOnTestingDataset = supervisedLearner.measurePredictiveAccuracy(testSetFeaturesOnlyDataMatrix, testSetLabelsOnlyDataMatrix, confusionMatrix);
				System.out.println("Test set accuracy: " + predictiveAccuracyOnTestingDataset);
				if(printConfusionMatrix) {
					System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
					confusionMatrix.print();
					System.out.println("\n");
				}
			}
			else if (evalMethod.equals("random"))
			{
				double lastAccuracy = 0;
				double currentAccuracy = 100;
				System.out.println("Calculating accuracy on a random hold-out set...");
				while(lastAccuracy-currentAccuracy!=0) {
					lastAccuracy=currentAccuracy;
					double percentUsedForTraining = Double.parseDouble(evalParameter);
					if (percentUsedForTraining < 0 || percentUsedForTraining > 1) {
						throw new Exception("Percentage for random evaluation must be between 0 and 1");
					}
					System.out.println("Percentage used for training: " + percentUsedForTraining);
					System.out.println("Percentage used for testing: " + (1 - percentUsedForTraining));
					fullDataMatrix.shuffleRowOrder(rand);
					int trainingInstanceCount = (int) (percentUsedForTraining * fullDataMatrix.getRowCount());
					DataMatrix trainingSetFeaturesOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, 0, trainingInstanceCount, fullDataMatrix.getColCount() - 1);
					DataMatrix trainingSetLabelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, fullDataMatrix.getColCount() - 1, trainingInstanceCount, 1);
					DataMatrix testSetFeaturesOnlyDataMatrix = new DataMatrix(fullDataMatrix, trainingInstanceCount, 0, fullDataMatrix.getRowCount() - trainingInstanceCount, fullDataMatrix.getColCount() - 1);
					DataMatrix testSetLabelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, trainingInstanceCount, fullDataMatrix.getColCount() - 1, fullDataMatrix.getRowCount() - trainingInstanceCount, 1);
					double startTime = System.currentTimeMillis();
					supervisedLearner.train(trainingSetFeaturesOnlyDataMatrix, trainingSetLabelsOnlyDataMatrix);
					double elapsedTime = System.currentTimeMillis() - startTime;
					System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
					double predictiveAccuracyOnTrainingDataset = supervisedLearner.measurePredictiveAccuracy(trainingSetFeaturesOnlyDataMatrix, trainingSetLabelsOnlyDataMatrix, null);
					System.out.println("Training set accuracy: " + predictiveAccuracyOnTrainingDataset);
					DataMatrix confusionMatrix = new DataMatrix();
					double predictiveAccuracyOnTestDataset = supervisedLearner.measurePredictiveAccuracy(testSetFeaturesOnlyDataMatrix, testSetLabelsOnlyDataMatrix, confusionMatrix);
					System.out.println("Test set accuracy: " + predictiveAccuracyOnTestDataset);
					if (printConfusionMatrix) {
						System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
						confusionMatrix.print();
						System.out.println("\n");
					}
					currentAccuracy = predictiveAccuracyOnTestDataset;
				}
			}
			else if (evalMethod.equals("cross"))
			{
				System.out.println("Calculating accuracy using cross-validation...");
				int foldCount = Integer.parseInt(evalParameter);
				if (foldCount <= 0) {
					throw new Exception("Number of folds must be greater than 0");
				}
				System.out.println("Number of folds: " + foldCount);
				int repetitions = 1;
				double sumOfAccuracies = 0.0;
				double elapsedTime = 0.0;
				for(int j = 0; j < repetitions; j++) {
					fullDataMatrix.shuffleRowOrder(rand);
					for (int i = 0; i < foldCount; i++) {
						int firstFoldInstanceIndex = i * fullDataMatrix.getRowCount() / foldCount;
						int endFoldInstanceIndex = (i + 1) * fullDataMatrix.getRowCount() / foldCount;
						DataMatrix trainingSetFeaturesOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, 0, firstFoldInstanceIndex, fullDataMatrix.getColCount() - 1);
						DataMatrix trainingSetLabelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, 0, fullDataMatrix.getColCount() - 1, firstFoldInstanceIndex, 1);
						DataMatrix testSetFeaturesOnlyDataMatrix = new DataMatrix(fullDataMatrix, firstFoldInstanceIndex, 0, endFoldInstanceIndex - firstFoldInstanceIndex, fullDataMatrix.getColCount() - 1);
						DataMatrix testSetLabelsOnlyDataMatrix = new DataMatrix(fullDataMatrix, firstFoldInstanceIndex, fullDataMatrix.getColCount() - 1, endFoldInstanceIndex - firstFoldInstanceIndex, 1);
						trainingSetFeaturesOnlyDataMatrix.add(fullDataMatrix, endFoldInstanceIndex, 0, fullDataMatrix.getRowCount() - endFoldInstanceIndex);
						trainingSetLabelsOnlyDataMatrix.add(fullDataMatrix, endFoldInstanceIndex, fullDataMatrix.getColCount() - 1, fullDataMatrix.getRowCount() - endFoldInstanceIndex);
						double startTime = System.currentTimeMillis();
						supervisedLearner.train(trainingSetFeaturesOnlyDataMatrix, trainingSetLabelsOnlyDataMatrix);
						elapsedTime += System.currentTimeMillis() - startTime;
						double predictiveAccuracyOnTestSetForFold = supervisedLearner.measurePredictiveAccuracy(testSetFeaturesOnlyDataMatrix, testSetLabelsOnlyDataMatrix, null);
						sumOfAccuracies += predictiveAccuracyOnTestSetForFold;
						System.out.println("Rep=" + j + ", Fold=" + i + ", Accuracy=" + predictiveAccuracyOnTestSetForFold);
					}
				}
				elapsedTime /= (repetitions * foldCount);
				System.out.println("Average time to train (in seconds): " + elapsedTime / 1000.0);
				System.out.println("Mean accuracy=" + (sumOfAccuracies / (repetitions * foldCount)));
			}
		}
	}

	/**
	 * Class for parsing out the command line arguments
	 */
	private class ArgParser {
	
		String arff;
		String learner;
		String evaluation;
		String evalExtra;
		boolean verbose;
		boolean normalize;

		//You can add more options for specific learning models if you wish
		public ArgParser(String[] argv) {
			try{
	
			 	for (int i = 0; i < argv.length; i++) {

			 		if (argv[i].equals("-V"))
			 		{
			 			verbose = true;
			 		}
			 		else if (argv[i].equals("-N"))
			 		{
			 			normalize = true;
			 		}
						else if (argv[i].equals("-A"))
						{
							arff = argv[++i];
						}
						else if (argv[i].equals("-L"))
						{
							learner = argv[++i];
						}
						else if (argv[i].equals("-E"))
						{
							evaluation = argv[++i];
							if (argv[i].equals("static"))
							{
								//expecting a test set name
								evalExtra = argv[++i];
							}
							else if (argv[i].equals("random"))
							{
								//expecting a double representing the percentage for testing
								//Note stratification is NOT done
								evalExtra = argv[++i];
							}
							else if (argv[i].equals("cross"))
							{
								//expecting the number of folds
								evalExtra = argv[++i];
							}
							else if (!argv[i].equals("training"))
							{
								System.out.println("Invalid Evaluation Method: " + argv[i]);
								System.exit(0);
							}
						}
						else
						{
							System.out.println("Invalid parameter: " + argv[i]);
							System.exit(0);
						}
			  	}
		 
				}
				catch (Exception e) {
					System.out.println("Usage:");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E [evaluationMethod] {[extraParamters]} [OPTIONS]\n");
					System.out.println("OPTIONS:");
					System.out.println("-V Print the confusion matrix and learner accuracy on individual class values\n");
					
					System.out.println("Possible evaluation methods are:");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E training");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E static [testARFF_File]");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E random [%_ForTraining]");
				  	System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E cross [numOfFolds]\n");
					System.exit(0);
				}
				
				if (arff == null || learner == null || evaluation == null)
				{
					System.out.println("Usage:");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E [evaluationMethod] {[extraParamters]} [OPTIONS]\n");
					System.out.println("OPTIONS:");
					System.out.println("-V Print the confusion matrix and learner accuracy on individual class values");
					System.out.println("-N Use normalized data");
					System.out.println();
					System.out.println("Possible evaluation methods are:");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E training");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E static [testARFF_File]");
					System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E random [%_ForTraining]");
				  	System.out.println("MLSystemManager -L [learningAlgorithm] -A [ARFF_File] -E cross [numOfFolds]\n");
					System.exit(0);
				}
			}
	 
		//The getter methods
		public String getARFF(){ return arff; }	
		public String getLearner(){ return learner; }	 
		public String getEvaluation(){ return evaluation; }	
		public String getEvalParameter() { return evalExtra; }
		public boolean getVerbose() { return verbose; } 
		public boolean getNormalize() { return normalize; }
	}

	public static void main(String[] args) throws Exception
	{
		MLSystemManager ml = new MLSystemManager();
		ml.run(args);
	}
}
