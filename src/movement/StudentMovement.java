package movement;

import assignment.*;
import assignment.distribution.ContinuousRandomVariable;
import assignment.distribution.PowerLaw;
import core.Coord;
import core.Settings;
import core.SimClock;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class StudentMovement extends MovementModel {
    // Current area we're in
    private Area lastArea;
    // Our last waypoint
    private Coord lastWaypoint;
    // Last decision we've made
    private ActorDecision lastDecision;
    // Map decisions to areas for convenience
    private final static Map<ActorDecision, Area> areaMap = Map.of(
        ActorDecision.GO_HOME, Area.ENTRANCE,
        ActorDecision.VISIT_MENSA, Area.ENTRANCE,
        ActorDecision.VISIT_BOOTH, Area.BOOTH_AREA,
        ActorDecision.VISIT_LIBRARY, Area.LIBRARY,
        ActorDecision.VISIT_CAFETERIA, Area.CAFETERIA,
        ActorDecision.VISIT_COMPUTER_HALLE, Area.COMPUTER_HALLE
    );
    // Random instance
    private final static Random rand = new Random();
    private double inBoothUntil;

    public StudentMovement(Settings settings) {
        super(settings);
    }

    protected StudentMovement(StudentMovement studentMovement) {
        super(studentMovement);
        lastArea = studentMovement.lastArea;
        lastDecision = studentMovement.lastDecision;
        lastWaypoint = studentMovement.lastWaypoint;
        inBoothUntil = 0.0;
    }

    @Override
    public Coord getInitialLocation() {
        this.lastWaypoint = Area.geometry.get(Area.ENTRANCE).entrance().clone();
        return this.lastWaypoint;
    }

    @Override
    public Path getPath() {
        Area r;
        Path p = new Path(generateSpeed());
        lastDecision = ActorSystem.withActor(getHost().getAddress(), s -> s.decide(SimClock.getTime()));

        if (areaMap.containsKey(lastDecision)) {
            // If we're within the well-known area, just use it
            r = areaMap.get(lastDecision);
        } else if (lastDecision == ActorDecision.VISIT_AREA) {
            // If the decision is to visit the area, set the new room
            r = ActorSystem.withActor(getHost().getAddress(), s -> s.getCurrentActivity().getRoom());
        } else {
            // If we're in the booth area,
            if (lastArea.isBooth()) {
                if (inBoothUntil > SimClock.getTime()) {
                    // Jitter around the booth to indicate interest
                    p.addWaypoint(lastWaypoint.clone());
                    Coord c = Area.geometry.get(lastArea).entrance().clone();
                    c.translate(rand.nextDouble() * 10, rand.nextDouble() * 10);
                    p.addWaypoint(c.clone());
                    this.lastWaypoint = c.clone();
                } else if (rand.nextDouble() < 0.75) {
                    double busyUntil = ActorSystem.withActor(getHost().getAddress(), Actor::getBusyUntil);
                    if (busyUntil > 3.0) {
                        CompanyPreference toPreference = ActorSystem.withActor(getHost().getAddress(), actor -> ((Student) actor).getPreference());
                        Optional<Company> pick = toPreference.pick(ActorSystem.getCompanies());

                        if (pick.isPresent()) {
                            ContinuousRandomVariable boothVisitTime = new PowerLaw(0.25, busyUntil / 2);
                            inBoothUntil = boothVisitTime.sample();
                            lastArea = pick.get().getBooth();

                            p.addWaypoint(lastWaypoint.clone());
                            Coord c = Area.geometry.get(lastArea).entrance().clone();
                            p.addWaypoint(c.clone());
                            this.lastWaypoint = c.clone();
                        }
                    }
                } else {
                    p.addWaypoint(lastWaypoint.clone());
                    p.addWaypoint(Area.geometry.get(Area.BOOTH_AREA).entrance().clone());
                    Coord c = getRandomInsidePolygon(Area.geometry.get(Area.BOOTH_AREA).bounds());
                    p.addWaypoint(c.clone());
                    this.lastWaypoint = c.clone();
                }

                return p;
            }

            return null;
        }

        p.addWaypoint(lastWaypoint.clone());
        if (lastArea != null) {
            p.addWaypoint(Area.geometry.get(lastArea).entrance().clone());
        }

        Area.Bounds bounds = Area.geometry.get(r);
        p.addWaypoint(bounds.entrance().clone());

        Coord c;
        if (bounds.bounds().isEmpty()) {
            c = bounds.entrance();
        } else {
            c = getRandomInsidePolygon(bounds.bounds());
        }

        this.lastWaypoint = c.clone();
        p.addWaypoint(c.clone());

        lastArea = r;

        return p;
    }

    @Override
    public StudentMovement replicate() {
        return new StudentMovement(this);
    }

    public record Bounds(Coord min, Coord max) {
        public List<Coord> getPolygon() {
            return List.of(
                new Coord(min.getX(), min.getY()),
                new Coord(max.getX(), min.getY()),
                new Coord(max.getX(), max.getY()),
                new Coord(min.getX(), max.getY()),
                new Coord(min.getX(), min.getY())
            );
        }

        public Coord getRandom() {
            return new Coord(
                min.getX() + rng.nextDouble() * (max.getX() - min.getX()),
                min.getY() + rng.nextDouble() * (max.getY() - min.getY())
            );
        }
    }

    public static Coord getRandomInsidePolygon(List<Coord> coords) {
        Bounds b = getBounds(coords);
        Coord c;

        do {
            c = b.getRandom();
        } while (!isInside(coords, c));

        return c;
    }

    private static boolean isInside(final List<Coord> polygon, final Coord point) {
        int count = countIntersectedEdges(polygon, point, new Coord(0, 0));
        return ((count % 2) != 0);
    }

    private static int countIntersectedEdges(final List<Coord> polygon, final Coord start, final Coord end) {
        int count = 0;

        for (int i = 0; i < polygon.size() - 1; i++) {
            final Coord polyP1 = polygon.get(i);
            final Coord polyP2 = polygon.get(i + 1);

            final Coord intersection = intersection(start, end, polyP1, polyP2);
            if (intersection == null) continue;

            if (isOnSegment(polyP1, polyP2, intersection)
                && isOnSegment(start, end, intersection)) {
                count++;
            }
        }

        return count;
    }

    private static boolean isOnSegment(final Coord L0, final Coord L1, final Coord point) {
        double crossProduct
            = (point.getY() - L0.getY()) * (L1.getX() - L0.getX())
            - (point.getX() - L0.getX()) * (L1.getY() - L0.getY());
        if (Math.abs(crossProduct) > 0.0000001) return false;

        final double dotProduct
            = (point.getX() - L0.getX()) * (L1.getX() - L0.getX())
            + (point.getY() - L0.getY()) * (L1.getY() - L0.getY());
        if (dotProduct < 0) return false;

        final double squaredLength
            = (L1.getX() - L0.getX()) * (L1.getX() - L0.getX())
            + (L1.getY() - L0.getY()) * (L1.getY() - L0.getY());
        return !(dotProduct > squaredLength);
    }

    private static Coord intersection(final Coord L0_p0, final Coord L0_p1, final Coord L1_p0, final Coord L1_p1) {
        double[] p0 = getParams(L0_p0, L0_p1);
        double[] p1 = getParams(L1_p0, L1_p1);
        double D = p0[1] * p1[0] - p0[0] * p1[1];

        if (D == 0.0) return null;

        final double x = (p0[2] * p1[1] - p0[1] * p1[2]) / D;
        final double y = (p0[2] * p1[0] - p0[0] * p1[2]) / D;

        return new Coord(x, y);
    }

    private static double[] getParams(final Coord c0, final Coord c1) {
        double A = c0.getY() - c1.getY();
        double B = c0.getX() - c1.getX();
        double C = c0.getX() * c1.getY() - c0.getY() * c1.getX();

        return new double[]{A, B, C};
    }

    private static Bounds getBounds(List<Coord> coords) {
        double xmin = coords.get(0).getX(),
            xmax = coords.get(0).getX(),
            ymin = coords.get(0).getY(),
            ymax = coords.get(0).getY();

        for (Coord c : coords) {
            if (c.getX() < xmin) {
                xmin = c.getX();
            }

            if (c.getX() > xmax) {
                xmax = c.getX();
            }

            if (c.getY() < ymin) {
                ymin = c.getY();
            }

            if (c.getY() > ymax) {
                ymax = c.getY();
            }
        }

        return new Bounds(new Coord(xmin, ymin), new Coord(xmax, ymax));
    }
}