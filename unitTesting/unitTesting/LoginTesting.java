package unitTesting;

import Common.Constructors.Login;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTesting {

    Login login;

    @BeforeEach
    public void initLogin() {
        login = new Login();
    }

    @Test
    public void testLoginSuccess() {
        String outcome = login.Login(
                "user",
                "password",
                "user",
                "password",
                "org1");
        assertEquals("Success", outcome);
    }

    @Test
    public void testLoginSuccessAdmin() {
        String outcome = login.Login(
                "user",
                "password",
                "user",
                "password",
                "admin");
        assertEquals("SuccessAdmin", outcome);
    }

    @Test
    public void testIncorrectPassword() {
        String outcome = login.Login(
                "user",
                "pwd",
                "user",
                "password",
                "org1");
        assertEquals("Failure", outcome);
    }

    @Test
    public void testIncorrectUser() {
        String outcome = login.Login(
                "user123",
                "password",
                "user",
                "password",
                "org1");
        assertEquals("Failure", outcome);
    }

    @Test
    public void testIncorrectUserAndPassword() {
        String outcome = login.Login(
                "user123",
                "pwd",
                "user",
                "password",
                "org1");
        assertEquals("Failure", outcome);
    }

    @Test
    public void testInvalidUserName() {
        String outcome = login.Login(
                "",
                "pwd",
                "user",
                "password",
                "org1");
        assertEquals("Failure", outcome);
    }
}
