package Common.Constructors;

import java.util.Objects;

/**
 * Constructs a new login instance and processes login information.
 */
public class Login {

    private String loginOutcome;

    /**
     * Constructs a new login instance and processed login information.
     * @param userText the username that was entered
     * @param hashPwdText the hash of the password that was entered
     * @param user the user in the database
     * @param pwd the hash password in the database
     * @param accType the account type of the user
     * @return Success or failure depending if user input matches database information
     */
    public String Login(String userText, String hashPwdText, String user, String pwd, String accType) {
        if (!userText.equals("")) {
            if (userText.equals(user) && hashPwdText.equals(pwd)) {
                this.loginOutcome = "Success";
                if (accType.equalsIgnoreCase("admin")) {
                    this.loginOutcome = "SuccessAdmin";
                }
            } else {
                this.loginOutcome = "Failure";
            }
        } else {
            this.loginOutcome = "Failure";
        }
        return this.loginOutcome;
    }
}
