package assignment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.Map;

public class Company extends Actor {
    // Unique company id
    private final int id;
    // The booth allocated to this company
    private final Area booth;
    // Domain in which this company operates
    private final CompanyDomain domain;
    // Relative appeal to the student
    private final double appeal;
    // A convenience mapping of company ID to booth area
    private final static Map<Integer, Area> boothMap = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(0, Area.BOOTH_1),
        new AbstractMap.SimpleEntry<>(1, Area.BOOTH_2),
        new AbstractMap.SimpleEntry<>(2, Area.BOOTH_3),
        new AbstractMap.SimpleEntry<>(3, Area.BOOTH_4),
        new AbstractMap.SimpleEntry<>(4, Area.BOOTH_5),
        new AbstractMap.SimpleEntry<>(5, Area.BOOTH_6),
        new AbstractMap.SimpleEntry<>(6, Area.BOOTH_7),
        new AbstractMap.SimpleEntry<>(7, Area.BOOTH_8),
        new AbstractMap.SimpleEntry<>(8, Area.BOOTH_9),
        new AbstractMap.SimpleEntry<>(9, Area.BOOTH_10),
        new AbstractMap.SimpleEntry<>(10, Area.BOOTH_11),
        new AbstractMap.SimpleEntry<>(11, Area.BOOTH_12)
    );

    public Company(int id, CompanyDomain domain, double appeal) {
        super();

        if (!boothMap.containsKey(id)) {
            throw new IllegalArgumentException("company ID is not valid");
        }

        this.id = id;
        this.booth = boothMap.get(id);
        this.domain = domain;
        this.appeal = appeal;
        this.currentActivity = Activity.busy(booth, Duration.of(10, ChronoUnit.HOURS));
    }

    public int getID() {
        return id;
    }

    public CompanyDomain getDomain() {
        return domain;
    }

    public double getAppeal() {
        return appeal;
    }

    public Area getBooth() {
        return booth;
    }

    @Override
    public ActorDecision decide(double currentTime) {
        // Let the company just stand in the booth
        // for the entire day
        if (currentTime < busyUntil) {
            return ActorDecision.CONTINUE;
        }

        busyUntil = currentActivity.getEnd();

        return ActorDecision.VISIT_AREA;
    }
}