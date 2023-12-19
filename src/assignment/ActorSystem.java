package assignment;

import assignment.distribution.ContinuousRandomVariable;
import assignment.distribution.Normal;
import core.DTNSim;

import java.util.*;
import java.util.function.Function;

public class ActorSystem {
    // Maximum number of companies
    private final static int MAX_COMPANIES = 12;
    // Mapping of host addresses (host->address) to actors
    private static Map<Integer, Actor> actors = new HashMap<>();
    // List of company actors
    private final static List<Company> companies = new ArrayList<>();
    // The distribution of company appeal
    private final static ContinuousRandomVariable appeal = new Normal(10, 5);

    static {
        DTNSim.registerForReset(ActorSystem.class.getCanonicalName());
        reset();
    }

    // Pregenerate the company actors
    static {
        for (int i = 0; i < MAX_COMPANIES; i++) {
            companies.add((Company) getActor(i));
        }
    }

    public static void reset() {
        actors.clear();
        companies.clear();
    }

    public static List<Company> getCompanies() {
        return companies;
    }

    // Apply a function on the actor and return the result to caller
    public static <R> R withActor(int id, Function<? super Actor, R> func) {
        return func.apply(getActor(id));
    }

    // Get the lazily generated actors
    // To simulate company actors, we allocate first 12 IDs (for the 12 booths)
    // to company actors, the rest goes to student actors
    private static Actor getActor(int id) {
        return actors.computeIfAbsent(id, i -> {
            if (i < MAX_COMPANIES) {
                return new Company(i, CompanyDomain.random(), appeal.sample());
            } else {
                return new Student(Timetables.getRandom());
            }
        });
    }
}
