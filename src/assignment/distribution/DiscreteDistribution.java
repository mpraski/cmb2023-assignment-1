package assignment.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiscreteDistribution<T> implements DiscreteRandomVariable<T> {
    private final List<Double> probabilities;
    private final List<T> events;
    private double sumProb;
    private final Random random;

    public DiscreteDistribution(Map<T, Double> events) {
        this.sumProb = 0;
        this.random = new Random();
        this.probabilities = new ArrayList<>();
        this.events = new ArrayList<>();

        for (Map.Entry<T, Double> entry : events.entrySet()) {
            this.sumProb += entry.getValue();
            this.events.add(entry.getKey());
            this.probabilities.add(entry.getValue());
        }
    }

    @Override
    public T sample() {
        int i;
        double prob = random.nextDouble() * sumProb;

        for (i = 0; prob > 0; i++) {
            prob -= probabilities.get(i);
        }

        return events.get(i - 1);
    }
}

