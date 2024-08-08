package unitTesting;

import Common.Constructors.HashPwd;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashPwdTesting {

    HashPwd hashPwd;

    @BeforeEach
    public void initUser() {
        hashPwd = new HashPwd();
    }

    // Test 1. Tests the hashing of a password
    @Test
    public void testHashPwd() {
        String pwd = hashPwd.HashPwd("password");
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", pwd);
    }
}
