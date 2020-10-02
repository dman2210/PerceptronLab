// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------
package toolkit;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Random;
import static toolkit.LazyUtils.print;

/**
 * For nominal labels, this model simply returns the majority class. For
 * continuous labels, it returns the mean value.
 * If the learning model you're using doesn't do as well as this one,
 * it's time to find a new learning model.
 */
public class Perceptron extends SupervisedLearner {
	Random rand;
	double learningRate = 0.3;
//	double[] predictedLabels ;
	double[] weights;
	double lastAccuracy = 0;
	Boolean debug = false;

	public Perceptron(Random rand) {
	this.rand=rand;
	}


	public void train(DataMatrix featuresOnlyDataMatrix, DataMatrix labelsOnlyDataMatrix) throws Exception {
		//initialize weights random;y between 0 and 1
//		for(double q = 0; q<1; q +=0.19){
//		this.learningRate = q;
		this.weights = new double[featuresOnlyDataMatrix.getColCount()+1];
		for(int i = 0; i < weights.length; i++){
			if(!debug){
			weights[i] = rand.nextDouble();
				}else{
//				weights[i] = 0.1+(double)i/100;
				weights[i] = 0;
			}
		}
		Integer epoch = 1;
		trainEpoch(featuresOnlyDataMatrix, labelsOnlyDataMatrix);
		double newAccuracy = this.measurePredictiveAccuracy(featuresOnlyDataMatrix, labelsOnlyDataMatrix, new DataMatrix());
		print("Epoch: "+epoch+" Accuracy: "+newAccuracy);
		int shutoff = 0;
		while(shutoff!=2){
			epoch++;
			print("Epoch: "+epoch+" Accuracy: "+newAccuracy);
			lastAccuracy = newAccuracy;
			trainEpoch(featuresOnlyDataMatrix, labelsOnlyDataMatrix);
			newAccuracy = this.measurePredictiveAccuracy(featuresOnlyDataMatrix, labelsOnlyDataMatrix, new DataMatrix());
			if(newAccuracy - lastAccuracy>0.005){
				shutoff = 0;
			}else{
				shutoff++;
			}
		}
		for (double w:weights) {
			print(w);
		}
		print("learningRate:"+learningRate);
//		}
	}


	public void trainEpoch(DataMatrix featuresOnlyDataMatrix, DataMatrix labelsOnlyDataMatrix) throws Exception{

		//loop through instances
		for(int i = 0; i < featuresOnlyDataMatrix.getRowCount(); i++) {
			//calculate net for activation
			//then if net > 1 activate
			int activation = discernActivation(getNet(featuresOnlyDataMatrix.matrixData.get(i)));

			//activation switch
			if(labelsOnlyDataMatrix.matrixData.get(i)[0] - activation ==0){
//				predictedLabels[i]= 1;
				//pass
			}else{
//				predictedLabels[i]= 0;
				//alter weights
				for(int j=0; j<featuresOnlyDataMatrix.matrixData.get(i).length; j++){
					weights[j] += this.getWeightDelta(activation, featuresOnlyDataMatrix.matrixData.get(i)[j], labelsOnlyDataMatrix.matrixData.get(i)[0]);
				}
				//adjust bias weight
				weights[weights.length-1] += this.getWeightDelta(activation, 1,labelsOnlyDataMatrix.matrixData.get(i)[0]);
			}
		}
	}


	public double getWeightDelta(double activation,double input, double target){
		return learningRate*(target-activation)*input;
	}


	public void predictInstanceLabelsFromFeatures(double[] featuresForInstance, double[] arrayInWhichToPutLabels) throws Exception {
		//implement this somehow
		double activated = (int)discernActivation(getNet(featuresForInstance));
		arrayInWhichToPutLabels[0] = activated;
	}

	private double getNet(double[] featuresOnlyDataMatrixRow){
		double net = 0.0;
		for(int j =0; j<featuresOnlyDataMatrixRow.length; j++){
			net+=featuresOnlyDataMatrixRow[j]*this.weights[j];
		}
//		Add bias
		net+=1*this.weights[weights.length-1];
		return net;
	}

	private int discernActivation(double net){
		int activated=0;
		if(net>=0){
			activated = 1;
		}
		return activated;
	}

}
