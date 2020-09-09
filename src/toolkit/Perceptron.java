// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------
package toolkit;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Random;

/**
 * For nominal labels, this model simply returns the majority class. For
 * continuous labels, it returns the mean value.
 * If the learning model you're using doesn't do as well as this one,
 * it's time to find a new learning model.
 */
public class Perceptron extends SupervisedLearner {
	Random rand;
	double learningRate = 0.2;
	double[] predictedLabels ;
	double[] weights;

	public Perceptron(Random rand) {
	this.rand=rand;
	}

	public void train(DataMatrix featuresOnlyDataMatrix, DataMatrix labelsOnlyDataMatrix) throws Exception {
		//initialize weights random;y between 0 and 1
		this.weights = new double[labelsOnlyDataMatrix.getColCount()+1];
		for(int i = 0; i < weights.length; i++){
			weights[i] = rand.nextDouble();
		}
		//loop through instances
		for(int i = 0; i < labelsOnlyDataMatrix.getColCount(); i++) {
		//calculate net for activation

			int activated = discernActivation(getNet(featuresOnlyDataMatrix.matrixData.get(i)));

			//basic activation switch
			if(labelsOnlyDataMatrix.matrixData.get(i)[0] - activated ==0){
//				predictedLabels[i]= 1;
				//pass
			}else{
//				predictedLabels[i]= 0;
				//alter weights
				for(int j=0; j<featuresOnlyDataMatrix.matrixData.get(i).length; j++){
					weights[j] += this.getWeightDelta(activated, featuresOnlyDataMatrix.matrixData.get(i)[j]);
				}
			}
		}
		this.weights=weights;
	}




	public double getWeightDelta(double net,double input){
		return learningRate*(net)*input;
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
		net+=1*this.weights[weights.length-1];
		return net;
	}

	private int discernActivation(double net){
		int activated=0;
		if(net>0){
			activated = 1;
		}
		return activated;
	}

}
