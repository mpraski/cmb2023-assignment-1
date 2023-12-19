package assignment;

import java.time.Duration;

public class Activity {
    enum Kind {
        BUSY,
        FREE
    }

    Kind kind;
    Area area;
    Duration duration;
    private double start;
    private double end;

    public static Activity busy(Area area, Duration duration) {
        return new Activity(Kind.BUSY, area, duration);
    }

    public static Activity free(Duration duration) {
        return new Activity(Kind.FREE, null, duration);
    }

    private Activity(Kind kind, Area area, Duration duration) {
        this.kind = kind;
        this.area = area;
        this.duration = duration;
    }

    public Area getRoom() {
        return area;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public void computePoints(double currentTime) {
        this.start = currentTime;
        this.end = currentTime + duration.getSeconds();
    }

    public boolean within(double currentTime) {
        return this.start <= currentTime && this.end > currentTime;
    }
}