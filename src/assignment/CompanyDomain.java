package assignment;

import java.util.Random;

// The domain of the company
public enum CompanyDomain {
    CONSULTING,
    MACHINE_LEARNING,
    SOFTWARE_ENGINEERING,
    DATA_SCIENCE,
    RENEWABLE_ENERGY,
    HEALTH_CARE,
    STARTUP;

    private static final Random rand = new Random();

    public static CompanyDomain random()  {
        CompanyDomain[] directions = values();
        return directions[rand.nextInt(directions.length)];
    }
}
