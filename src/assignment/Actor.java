package assignment;

import java.time.Duration;

// Actor is the abstract behaviour model
// for the nodes. Currently, we distinguish between
// student and company actors.
public abstract class Actor {
    // Our current state
    protected ActorState state;
    // Our current activity
    protected Activity currentActivity;
    // When will the current activity be finished?
    protected double busyUntil;

    public Actor() {
        this.state = ActorState.IDLE;
        this.busyUntil = 0.0;
    }

    public ActorState getState() {
        return state;
    }

    public double getBusyUntil() { return busyUntil; }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    // Make the decision about our next move
    public abstract ActorDecision decide(double currentTime);

    protected static Duration timeTill(Activity activity, double currentTime) {
        return Duration.ofSeconds((long) (activity.getStart() - currentTime));
    }
}
