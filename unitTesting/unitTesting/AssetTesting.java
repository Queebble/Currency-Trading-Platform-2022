package unitTesting;

import Common.Constructors.Asset;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetTesting {

    Asset asset;

    @BeforeEach
    public void initUser() {
        asset = new Asset();
    }

    // Test 1. Tests the creation of a new Asset
    @Test
    public void testCreateOrgAsset() {
        asset = new Asset("Asset");
        assertEquals("Asset", asset.getAssetName());
    }

    // Test 2. Tests set and get for asset parameter
    @Test
    public void testSetGet() {
        asset.setAssetName("NewAsset");
        assertEquals("NewAsset", asset.getAssetName());
    }

    // Test 3. Tests get for parameters of a non-existent asset
    @Test
    public void testOrgAssetNotExists() {
        assertEquals(null, asset.getAssetName());
    }
}