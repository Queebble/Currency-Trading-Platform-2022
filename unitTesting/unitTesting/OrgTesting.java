package unitTesting;

import Common.Constructors.Org;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrgTesting {

    Org org;

    @BeforeEach
    public void initOrg() {
        org = new Org();
    }

    // Test 1. Tests the creation of a new organisation
    @Test
    public void testCreateOrg() {
        org = new Org("MyOrg", 100);
        assertEquals("MyOrg", org.getOrgUnit());
        assertEquals(100, org.getCredits());
    }

    // Test 2. Tests set and get for each org parameter
    @Test
    public void testSetGet() {
        org.setOrgUnit("NewOrg");
        org.setCredits(0);
        assertEquals("NewOrg", org.getOrgUnit());
        assertEquals(0, org.getCredits());

    }

    // Test 3. Tests get for parameters of a non-existent org
    @Test
    public void testOrgNotExists() {
        assertEquals(null, org.getOrgUnit());
        assertEquals(0.0, org.getCredits());
    }
}