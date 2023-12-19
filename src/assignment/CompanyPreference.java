package assignment;

import assignment.distribution.ContinuousRandomVariable;
import assignment.distribution.Normal;

import java.util.*;
import java.util.stream.Collectors;

// CompanyPreference encapsulates the preference students
// have for companies at the exhibition booths. It factors in
// "a prori" appeal by company domain and word of mouth, as well
// as actual impressions after the student has visited the booth.
// It is used to guide student actions at the booth as well as
// create the word-of-mouth gossip between students.
public class CompanyPreference {
    // Appeal by domain of the company
    private final Map<CompanyDomain, Double> appealByDomain;
    // Appeal by word of mouth, heard from another student
    private Map<Integer, Double> appealByWordOfMouth;
    // Companies we've visited so far, along with their appeal as we remember it
    private Map<Integer, Double> visitedCompanies;
    // Normal distribution of the company appeal (a priori)
    private final static ContinuousRandomVariable appealDist = new Normal(10, 5);
    // Normal distribution of the company appeal modifier (our actual impression with the company at the booth)
    private final static ContinuousRandomVariable appealModifierDist = new Normal(2, 1);
    // Random instance
    private final static Random rand = new Random();

    public CompanyPreference() {
        appealByDomain = new HashMap<>();
        appealByWordOfMouth = new HashMap<>();
        visitedCompanies = new HashMap<>();
        for (CompanyDomain d : CompanyDomain.values()) {
            appealByDomain.put(d, appealDist.sample());
        }
    }

    // Update the word of mouth appeal (e.g. talking with students who visited the company)
    public void updateCompanyAppeal(int companyId, double appeal) {
        appealByWordOfMouth.put(companyId, appeal);
    }

    // Get the companies we've visited with highest subjective appeal
    public Map<Integer, Double> getSubjectivelyBestCompanies() {
        return visitedCompanies.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(rand.nextInt(3))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Pick the next unvisited company according to its appeal
    public Optional<Company> pick(List<Company> companies) {
        Company bestCompany = null;
        double highestAppeal = 0.0;

        for (Company c : companies) {
            // If company was visited, don't visit it again
            if (visitedCompanies.containsKey(c.getID())) {
                continue;
            }

            // Get the students appeal by the domain of this company
            double appeal = appealByDomain.getOrDefault(c.getDomain(), 0.0);
            if (appeal > highestAppeal) {
                highestAppeal = appeal;
                bestCompany = c;
            }

            // Get the appeal of the company by the word of mouth heard from other students
            appeal = appealByWordOfMouth.getOrDefault(c.getID(), 0.0);
            if (appeal > highestAppeal) {
                highestAppeal = appeal;
                bestCompany = c;
            }
        }

        // If we do have our choice, add it to the visited map
        // along with a variable modifier to simulate subjective opinion
        if (bestCompany != null) {
            visitedCompanies.put(bestCompany.getID(), highestAppeal + appealModifierDist.sample());
        }

        return Optional.ofNullable(bestCompany);
    }
}
