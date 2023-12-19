package assignment;

import assignment.distribution.ContinuousRandomVariable;
import assignment.distribution.DiscreteDistribution;
import assignment.distribution.DiscreteRandomVariable;
import assignment.distribution.PowerLaw;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class Student extends Actor {
    // The timetable of this student
    private final Timetable timetable;
    // Subjective preference for companies of this student
    private final CompanyPreference preference;
    // Probability distribution of actions when we have a long break between classes
    private final static DiscreteRandomVariable<ActorDecision> lotsOfTime = new DiscreteDistribution<>(Map.of(
            ActorDecision.VISIT_BOOTH, 0.2,
            ActorDecision.VISIT_MENSA, 0.3,
            ActorDecision.VISIT_LIBRARY, 0.2,
            ActorDecision.VISIT_CAFETERIA, 0.15,
            ActorDecision.VISIT_COMPUTER_HALLE, 0.15
    ));
    // Probability distribution of actions when we have a short break between classes
    private final static DiscreteRandomVariable<ActorDecision> littleTime = new DiscreteDistribution<>(Map.of(
            ActorDecision.VISIT_BOOTH, 0.3,
            ActorDecision.VISIT_LIBRARY, 0.1,
            ActorDecision.VISIT_CAFETERIA, 0.4,
            ActorDecision.VISIT_COMPUTER_HALLE, 0.2
    ));

    public Student(Timetable timetable) {
        super();
        this.timetable = timetable;
        this.preference = new CompanyPreference();
    }

    public CompanyPreference getPreference() {
        return preference;
    }

    @Override
    public ActorDecision decide(double currentTime) {
        // If we're busy, just continue doing the same thing
        if (currentTime < busyUntil) {
            return ActorDecision.CONTINUE;
        }

        // Retrieve the current activity
        Optional<Activity> activity = timetable.getCurrentActivity(currentTime);
        if (activity.isEmpty()) {
            return ActorDecision.GO_HOME;
        }

        currentActivity = activity.get();

        switch (currentActivity.kind) {
            case BUSY:
                // If we're busy, adjust the state accordingly
                busyUntil = currentActivity.getEnd();
                return switch (state) {
                    case IN_AREA -> ActorDecision.CONTINUE;
                    case IDLE -> {
                        state = ActorState.IN_AREA;
                        yield ActorDecision.VISIT_AREA;
                    }
                };
            case FREE:
                // If we're free, find the next activity
                Optional<Activity> nextActivity = timetable.getNextActivity(currentTime);
                if (nextActivity.isEmpty()) {
                    return ActorDecision.GO_HOME;
                }

                // Next activity according to the timetable
                Activity nextAct = nextActivity.get();
                // Next decision
                ActorDecision nextDecision;
                // Time till next activity
                Duration timeTillNext = timeTill(nextAct, currentTime);
                // Power law distribution of next sub-activity time
                ContinuousRandomVariable freeActivityTime = new PowerLaw(0.25, timeTillNext.getSeconds() / 1.5);

                // If we have more than 60 minutes, choose from "lots of time" activities
                if (timeTillNext.compareTo(Duration.ofMinutes(60)) > 0) {
                    nextDecision = lotsOfTime.sample();
                    busyUntil += freeActivityTime.sample();
                    // If we have more than 30 minutes, choose from "little of time" activities
                } else if (timeTillNext.compareTo(Duration.ofMinutes(30)) > 0) {
                    nextDecision = littleTime.sample();
                    busyUntil += freeActivityTime.sample();
                    // If we have less than that, choose from "little of time"
                    // activities and just do it till the end of free time
                } else {
                    nextDecision = littleTime.sample();
                    busyUntil = currentActivity.getEnd();
                }

                // We're now in some area
                state = ActorState.IN_AREA;

                return nextDecision;
        }

        throw new IllegalStateException("Unreachable student state");
    }
}
