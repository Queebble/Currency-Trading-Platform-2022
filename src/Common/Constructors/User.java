package Common.Constructors;

import java.io.Serializable;

/**
 * Constructs a new instance of a user
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1075079598207507673L;

    private String user;
    private String password;
    private String accType;
    private String orgUnit;

    /**
     * No args constructor
     */
    public User(){
    }

    /**
     * Initialises a new user object
     * @param user the users username
     * @param password the password of the user
     * @param accType the account type of the user(admin or not admin)
     * @param orgUnit the organisation to which the user belongs
     */
    public User(String user, String password, String accType, String orgUnit) {
        this.user = user;
        this.password = password;
        this.accType = accType;
        this.orgUnit = orgUnit;
    }

    /**
     * Retrieves the account type of the user
     * @return the account type
     */
    public String getAccType() { return accType; }

    /**
     * Sets the account type of the user
     * @param accType the account type to be set
     */
    public void setAccType(String accType) { this.accType = accType; }


    /**
     * Retrieves the organisation unit of a user
     * @return the user's organisation unit
     */
    public String getOrgUnit() { return orgUnit; }

    /**
     * Sets the organisation unit of a user
     * @param orgUnit the organisation unit to be set
     */
    public void setOrgUnit(String orgUnit) { this.orgUnit = orgUnit; }

    /**
     * Retrieves the password of a user
     * @return the password
     */
    public String getPassword() { return password; }

    /**
     * Sets the password of a user
     * @param password the password to be set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Retrieves the name of a user
     * @return the users username
     */
    public String getUser() { return user; }

    /**
     * Sets the name of a user
     * @param user the username to be set
     */
    public void setUser(String user) { this.user = user; }

}
