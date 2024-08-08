package Common.Constructors;

import java.io.Serializable;

/**
 * Constructs a new instance of an organisation
 */
public class Org implements Serializable {
    private static final long serialVersionUID = -5192686837155213633L;

    private String orgUnit;
    private double credits;

    /**
     * No args constructor
     */
    public Org(){
    }

    /**
     * Initialises an organisation object
     * @param org the name of the organisation
     * @param credits the amount of credits
     */
    public Org(String org, double credits) {
        this.orgUnit = org;
        this.credits = credits;
    }

    /**
     * Retrieves the organisations unit name
     * @return the organisation unit
     */
    public String getOrgUnit() { return orgUnit; }

    /**
     * Sets the organisations unit name
     * @param orgUnit the organisation unit to be set
     */
    public void setOrgUnit(String orgUnit) { this.orgUnit = orgUnit; }


    /**
     * Retrieves the number of credits an organisation has
     * @return the number of credits
     */
    public double getCredits() { return credits; }

    /**
     * Sets the number of credits an organisation has
     * @param credits the credits to be set
     */
    public void setCredits(double credits) { this.credits = credits; }

}
