package toolkit;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PerceptronTest {

    @org.junit.jupiter.api.Test
    void getWeightDelta() {
        Random rand = new Random();
        Perceptron ptron = new Perceptron(rand);
        double learningRate = 0.2;
        double activation = 1.0;
        double target = 2.0;
        double input = 0.87;
        double result = learningRate*(target-activation)*input;
        assertEquals(ptron.getWeightDelta(activation, input, target),result);
    }
}