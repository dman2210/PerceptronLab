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
	double[] predictedLabels;
	double[] weights;

	public Perceptron(Random rand) {
	this.rand=rand;
	}

	public void train(DataMatrix featuresOnlyDataMatrix, DataMatrix labelsOnlyDataMatrix) throws Exception {
		this.weights = new double[labelsOnlyDataMatrix.getColCount()+1];
		for(int i = 0; i < weights.length; i++){
			weights[i] = rand.nextDouble();
		}
		for(int i = 0; i < labelsOnlyDataMatrix.getColCount(); i++) {

			double net = getNet(featuresOnlyDataMatrix.matrixData.get(i));
			if(labelsOnlyDataMatrix.matrixData.get(i)[0] - net ==0){
				//pass
			}else{
				//implement delta function on weights
			}
		}
		this.weights=weights;
	}

	public void predictInstanceLabelsFromFeatures(double[] featuresForInstance, double[] arrayInWhichToPutLabels) throws Exception {
		//implement this somehow
		for(int i = 0; i < predictedLabels.length; i++) {
			arrayInWhichToPutLabels[i] = predictedLabels[i];
		}
	}

	private double getNet(double[] featuresOnlyDataMatrixRow){
		double net = 0.0;
		for(int j =0; j<featuresOnlyDataMatrixRow.length; j++){
			net+=featuresOnlyDataMatrixRow[j]*this.weights[j];
		}
		net+=1*this.weights[weights.length-1];
		return net;
	}

}
