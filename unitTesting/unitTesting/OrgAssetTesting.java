package unitTesting;

import Common.Constructors.OrgAsset;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrgAssetTesting {

    OrgAsset orgAsset;

    @BeforeEach
    public void initUser() {
        orgAsset = new OrgAsset();
    }

    // Test 1. Tests the creation of a new OrgAsset
    @Test
    public void testCreateOrgAsset() {
        orgAsset = new OrgAsset(
                "Unit1",
                "Asset",
                10);
        assertEquals("Unit1", orgAsset.getOrgUnit());
        assertEquals("Asset", orgAsset.getAssetName());
        assertEquals(10, orgAsset.getAssetQty());
    }

    // Test 2. Tests set and get for each orgAsset parameter
    @Test
    public void testSetGet() {
        orgAsset.setOrgUnit("NewUnit");
        orgAsset.setAssetName("NewAsset");
        orgAsset.setAssetQty(100);
        assertEquals("NewUnit", orgAsset.getOrgUnit());
        assertEquals("NewAsset", orgAsset.getAssetName());
        assertEquals(100, orgAsset.getAssetQty());
    }

    // Test 3. Tests get for parameters of a non-existent orgAsset
    @Test
    public void testOrgAssetNotExists() {
        assertEquals(null, orgAsset.getOrgUnit());
        assertEquals(null, orgAsset.getAssetName());
        assertEquals(0.0, orgAsset.getAssetQty());
    }
}