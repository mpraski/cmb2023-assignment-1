package assignment.distribution;

import assignment.distribution.ContinuousRandomVariable;

import java.util.Random;

public class PowerLaw implements ContinuousRandomVariable {
    private final Random random;
    private final double exponent;
    private final double minValue;

    public PowerLaw(double exponent, double minValue) {
        this.random = new Random();
        this.exponent = exponent;
        this.minValue = minValue;
    }

    @Override
    public double sample() {
        return minValue * Math.pow(1.0 - random.nextDouble(), -1.0 / (exponent - 1.0));
    }
}
