package unitTesting;

import Common.Constructors.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTesting {

    User user;

    @BeforeEach
    public void initUser() {
        user = new User();
    }

    // Test 1. Tests the creation of a new user
    @Test
    public void testCreateUser() {
        user = new User(
                "Username",
                "Password",
                "Admin",
                "Unit1");
        assertEquals("Username", user.getUser());
        assertEquals("Password", user.getPassword());
        assertEquals("Admin", user.getAccType());
        assertEquals("Unit1", user.getOrgUnit());
    }

    // Test 2. Tests set and get for each user parameter
    @Test
    public void testSetGet() {
        user.setUser("user");
        user.setPassword("pwd");
        user.setAccType("general");
        user.setOrgUnit("general");
        assertEquals("user", user.getUser());
        assertEquals("pwd", user.getPassword());
        assertEquals("general", user.getAccType());
        assertEquals("general", user.getOrgUnit());
    }

    // Test 3. Tests get for parameters of a non-existent user
    @Test
    public void testUserNotExists() {
        assertEquals(null, user.getUser());
        assertEquals(null, user.getPassword());
        assertEquals(null, user.getAccType());
        assertEquals(null, user.getOrgUnit());
    }
}
