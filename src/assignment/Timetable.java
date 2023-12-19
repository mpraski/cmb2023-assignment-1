package assignment;

import core.DTN2Manager;
import core.Settings;
import fi.tkk.netlab.dtn.ecla.Bundle;
import input.ExternalEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Timetable {
    // List of activities in the timetable
    private final List<Activity> activities;

    public Timetable(Activity... activities) {
        this.activities = List.of(activities);

        double currentTime = 0;

        for (Activity a : this.activities) {
            a.computePoints(currentTime);

            currentTime += a.duration.getSeconds();
        }
    }

    // Get activity at the current point in time
    public Optional<Activity> getCurrentActivity(double currentTime) {
        for (Activity a : activities) {
            if (a.within(currentTime)) {
                return Optional.of(a);
            }
        }

        return Optional.empty();
    }

    // Get the activity immediatelly following the current one
    public Optional<Activity> getNextActivity(double currentTime) {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).within(currentTime) && i < activities.size() - 1) {
                return Optional.of(activities.get(i + 1));
            }
        }

        return Optional.empty();
    }
}
