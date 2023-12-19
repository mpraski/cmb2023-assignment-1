package assignment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class Timetables {
    // A list of predefined timetables.
    // Each student is assigned to one of these
    private static final List<Timetable> timetables = List.of(
        new Timetable(
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS)),
            Activity.busy(Area.HS3, Duration.of(90, ChronoUnit.MINUTES))
        ),
        new Timetable(
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS))
        ),
        new Timetable(
            Activity.busy(Area.HS3, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS))
        ),
        new Timetable(
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS3, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS))
        ),
        new Timetable(
            Activity.free(Duration.of(180, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(60, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES))
        ),
        new Timetable(
            Activity.free(Duration.of(180, ChronoUnit.MINUTES)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS3, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS))
        ),
        new Timetable(
            Activity.free(Duration.of(180, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(60, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS))
        ),
        new Timetable(
            Activity.free(Duration.of(270, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(20, ChronoUnit.MINUTES)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES))
        ),
        new Timetable(
            Activity.free(Duration.of(270, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS2, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(90, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS3, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES))
        ),
        new Timetable(
            Activity.free(Duration.of(270, ChronoUnit.MINUTES)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(30, ChronoUnit.MINUTES)),
            Activity.busy(Area.HS1, Duration.of(90, ChronoUnit.MINUTES)),
            Activity.free(Duration.of(1, ChronoUnit.HOURS)),
            Activity.busy(Area.S_00_13_009A, Duration.of(90, ChronoUnit.MINUTES))
        )
    );

    private static final Random random = new Random();

    public static Timetable getRandom() {
        return timetables.get(random.nextInt(timetables.size()));
    }
}
