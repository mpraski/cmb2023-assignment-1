package assignment.distribution;

import assignment.distribution.ContinuousRandomVariable;

import java.util.Random;

public class Normal implements ContinuousRandomVariable {
    private final double average;
    private final double standardDeviation;

    private final static Random rand = new Random();

    public Normal(double average, double standardDeviation) {
        this.average = average;
        this.standardDeviation = standardDeviation;
    }

    @Override
    public double sample() {
        return rand.nextGaussian() * standardDeviation + average;
    }
}
