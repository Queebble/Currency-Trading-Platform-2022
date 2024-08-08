package unitTesting;

import Common.Constructors.*;
import Common.MarketplaceDataSource;
import Common.MarketplaceDataTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class MockDatabaseTesting {

    MarketplaceDataSource data;
    User user;
    Org org;
    Asset asset;
    OrgAsset orgAsset;
    Order buyOrder;
    Order sellOrder;

    @BeforeEach
    public void newDataSource() {
        data = new MarketplaceDataTest();
        user = new User(
                "Username",
                "password",
                "accType",
                "orgUnit");
        org = new Org(
                "OrgUnit",
                100);
        asset = new Asset("MyAsset");
        orgAsset = new OrgAsset(
                "OrgUnit",
                "MyAsset",
                10);
        buyOrder = new Order(
                "order1",
                "BUY",
                "orgUnit",
                "asset1",
                5.0,
                5.0,
                "2021-06-20 14:33:12"
        );
        sellOrder = new Order(
                "order2",
                "SELL",
                "orgUnit",
                "asset2",
                10.0,
                5.0,
                "2021-06-20 14:33:12"
        );
    }

    //////////////// USER TESTS /////////////////

    // Test 1. Tests the addition of a new user
    @Test
    public void testAddUser() {
        data.addUser(user);
        assertEquals(user, data.getUser(user.getUser()));
    }

    // Test 2. Tests get of a non-existent user
    @Test
    public void testGetNotExistUser() {
        assertEquals(null , data.getUser("NonExistentUser"));
    }

    // Test 3. Tests get of user size
    @Test
    public void testGetUserSize() {
        data.addUser(user);
        assertEquals(1 , data.getUsersSize());
    }

    // Test 4. Tests the addition of a duplicate user
    @Test
    public void testAddDuplicateUser() {
        data.addUser(user);
        data.addUser(user);
        assertEquals(1 , data.getUsersSize());
    }

    // Test 5. Tests deleting a user
    @Test
    public void testDeleteUser() {
        data.addUser(user);
        assertEquals(1 , data.getUsersSize());
        data.deleteUser(user.getUser());
        assertEquals(0, data.getUsersSize());
    }

    // Test 6. Tests getting user set
    @Test
    public void testUsersSet() {
        Set<String> users = new TreeSet<String>();
        users.add(user.getUser());
        data.addUser(user);
        assertEquals(users , data.userSet());
    }

    // Test 7. Tests getting an empty user set
    @Test
    public void testEmptyUserAssetSet() {
        Set<String> users = new TreeSet<String>();
        assertEquals(users , data.userSet());
    }

    // Test 8. Tests updating a user
    @Test
    public void testUserUpdate() {
        data.addUser(user);
        data.updateUser("NewUser",
                "NewPwd",
                "NewAccType",
                "NewOrg",
                "Username");
        assertEquals("NewUser" , data.getUser("NewUser").getUser());
        assertEquals("NewPwd" , data.getUser("NewUser").getPassword());
        assertEquals("NewAccType" , data.getUser("NewUser").getAccType());
        assertEquals("NewOrg" , data.getUser("NewUser").getOrgUnit());
        assertEquals(null , data.getUser("Username"));
    }

    // Test 9. Tests updating a user that doesn't exist
    @Test
    public void testBadUserUpdate() {
        data.updateUser("NewUser",
                "NewPwd",
                "NewAccType",
                "NewOrg",
                "Username");
        assertEquals(null , data.getUser("NewUser"));
    }

    // Test 10. Tests updating a user with a username that already exists
    @Test
    public void testInvalidUserUpdate() {
        data.addUser(user);
        User user1 = new User(
                "TakenUsername",
                "password",
                "accType",
                "orgUnit");
        data.addUser(user1);
        data.updateUser("TakenUsername",
                "NewPwd",
                "NewAccType",
                "NewOrg",
                "Username");
        assertEquals("Username" , data.getUser("Username").getUser());
    }

    //////////////// ORG TESTS /////////////////

    // Test 11. Tests the addition of a new org
    @Test
    public void testAddOrg() {
        data.addOrg(org);
        assertEquals(org, data.getOrg(org.getOrgUnit()));
    }

    // Test 12. Tests get of a non-existent org
    @Test
    public void testGetNotExistOrg() {
        assertEquals(null , data.getOrg("NonExistentOrg"));
    }

    // Test 13. Tests get of org size
    @Test
    public void testGetOrgSize() {
        data.addOrg(org);
        assertEquals(1 , data.getOrgSize());
    }

    // Test 14. Tests the addition of a duplicate org
    @Test
    public void testAddDuplicateOrg() {
        data.addOrg(org);
        data.addOrg(org);
        assertEquals(1 , data.getOrgSize());
    }

    // Test 15. Tests deleting an org
    @Test
    public void testDeleteOrg() {
        data.addOrg(org);
        assertEquals(1 , data.getOrgSize());
        data.deleteOrg(org.getOrgUnit());
        assertEquals(0, data.getOrgSize());
    }

    // Test 16. Tests getting org set
    @Test
    public void testOrgSet() {
        Set<String> orgs = new TreeSet<String>();
        orgs.add(org.getOrgUnit());
        data.addOrg(org);
        assertEquals(orgs , data.orgSet());
    }

    // Test 17. Tests getting an empty org set
    @Test
    public void testInvalidOrgAssetSet() {
        Set<String> orgs = new TreeSet<String>();
        assertEquals(orgs , data.orgSet());
    }

    // Test 18. Tests updating the credits of an org
    @Test
    public void testOrgUpdate() {
        data.addOrg(org);
        data.updateCredits("OrgUnit", "50");
        assertEquals(50 , data.getOrg("OrgUnit").getCredits());
    }

    // Test 19. Tests updating an org that doesn't exist
    @Test
    public void testBadOrgUpdate() {
        data.updateCredits("OrgUnit", "50");
        assertEquals(null , data.getOrg("OrgUnit"));
    }

    //////////////// ASSET TESTS /////////////////

    // Test 20. Tests the addition of a new asset
    @Test
    public void testAddAsset() {
        data.addAsset(asset);
        assertEquals(asset, data.getAsset(asset.getAssetName()));
    }

    // Test 21. Tests get of a non-existent org
    @Test
    public void testGetNotExistAsset() {
        assertEquals(null , data.getAsset("NonExistentAsset"));
    }

    // Test 22. Tests get of asset size
    @Test
    public void testGetAssetSize() {
        data.addAsset(asset);
        assertEquals(1 , data.getAssetSize());
    }

    // Test 23. Tests the addition of a duplicate asset
    @Test
    public void testAddDuplicateAsset() {
        data.addAsset(asset);
        data.addAsset(asset);
        assertEquals(1 , data.getAssetSize());
    }

    // Test 24. Tests deleting an asset
    @Test
    public void testDeleteAsset() {
        data.addAsset(asset);
        assertEquals(1 , data.getAssetSize());
        data.deleteAsset(asset.getAssetName());
        assertEquals(0, data.getAssetSize());
    }

    // Test 25. Tests getting asset set
    @Test
    public void testAssetSet() {
        Set<String> assets = new TreeSet<String>();
        assets.add(asset.getAssetName());
        data.addAsset(asset);
        assertEquals(assets , data.assetSet());
    }

    // Test 26. Tests getting an empty asset set
    @Test
    public void testEmptyAssetSet() {
        Set<String> assets = new TreeSet<String>();
        assertEquals(assets , data.assetSet());
    }

    //////////////// ORG-ASSET TESTS /////////////////

    // Test 27. Tests the addition of a new organisation asset
    @Test
    public void testAddOrgAsset() {
        data.addOrgAsset(orgAsset);
        assertEquals(orgAsset, data.getOrgAsset("OrgUnit", "MyAsset"));
    }

    // Test 28. Tests get of a non-existent org-asset
    @Test
    public void testGetNotExistOrgAsset() {
        assertEquals(null , data.getOrgAsset("NonExistentOrg", "NonExistentAsset"));
    }

    // Test 29. Tests get of org-asset size
    @Test
    public void testGetOrgAssetSize() {
        data.addOrgAsset(orgAsset);
        assertEquals(1 , data.getOrgAssetSize());
    }

    // Test 30. Tests the addition of a duplicate org-asset
    @Test
    public void testAddDuplicateOrgAsset() {
        data.addOrgAsset(orgAsset);
        data.addOrgAsset(orgAsset);
        assertEquals(1 , data.getOrgAssetSize());
    }

    // Test 31. Tests deleting an org-asset
    @Test
    public void testDeleteOrgAsset() {
        data.addOrgAsset(orgAsset);
        assertEquals(1 , data.getOrgAssetSize());
        data.deleteOrgAsset("OrgUnit", "MyAsset");
        assertEquals(0, data.getOrgAssetSize());
    }

    // Test 32. Tests updating the quantity of an orgs asset
    @Test
    public void testUpdateOrgAssetQty() {
        data.addOrgAsset(orgAsset);
        data.updateAssetQuantity("50", "OrgUnit", "MyAsset");
        assertEquals(50 , data.getOrgAsset("OrgUnit", "MyAsset").getAssetQty());
    }

    // Test 33. Tests getting asset set
    @Test
    public void testOrgAssetSet() {
        Set<String> orgAssets = new TreeSet<String>();
        orgAssets.add(orgAsset.getOrgUnit() + " " + orgAsset.getAssetName());
        data.addOrgAsset(orgAsset);
        assertEquals(orgAssets , data.orgAssetSet());
    }

    // Test 34. Tests getting an empty org-asset set
    @Test
    public void testEmptyOrgAssetSet() {
        Set<String> orgAssets = new TreeSet<String>();
        assertEquals(orgAssets , data.orgAssetSet());
    }

    // Test 35. Tests checking for asset possession - true
    @Test
    public void testCheckAssetTrue() {
        data.addOrgAsset(orgAsset);
        assertEquals(true , data.checkOrgAsset("MyAsset"));
    }

    // Test 36. Tests checking for asset possession - false
    @Test
    public void testCheckAssetFalse() {
        data.addOrgAsset(orgAsset);
        assertEquals(false , data.checkOrgAsset("NewAsset"));
    }

    //////////////// ORDER AND ORDER HISTORY TESTS /////////////////

    // Test 37. Tests the addition of a new user
    @Test
    public void testAddOrder() {
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        assertEquals(buyOrder, data.getOrder(buyOrder.getOrderID()));
        assertEquals(sellOrder, data.getOrder(sellOrder.getOrderID()));
    }

    // Test 38. Tests get of a non-existent order
    @Test
    public void testGetNotExistOrder() {
        assertEquals(null , data.getOrder("NonExistentOrderID"));
    }

    // Test 39. Tests get of order size
    @Test
    public void testGetOrderSize() {
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        assertEquals(1 , data.getOrderSize("BUY"));
        assertEquals(1 , data.getOrderSize("SELL"));
    }

    // Test 40. Tests get of order size
    @Test
    public void testGetEmptyOrderSize() {
        assertEquals(0 , data.getOrderSize("BUY"));
        assertEquals(0 , data.getOrderSize("SELL"));
    }
    // Test 41 Tests get of order size for non-existent order type
    @Test
    public void testGetWrongOrderSize() {
        assertEquals(0 , data.getOrderSize("TRADE"));
    }

    // Test 42. Tests the addition of a duplicate order - not possible in practice
    // due to generate of unique ID
    @Test
    public void testAddDuplicateOrder() {
        data.addOrder(buyOrder);
        data.addOrder(buyOrder);
        assertEquals(1 , data.getOrderSize("BUY"));
    }

    // Test 43. Tests deleting a fulfilled order from open order
    // and adding to order history
    @Test
    public void testFulfilOrder() {
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        if (buyOrder.getPrice() >= (sellOrder.getPrice())) {
            assertEquals(1 , data.getOrderSize("BUY"));
            assertEquals(1 , data.getOrderSize("SELL"));
            data.deleterOrder(buyOrder.getOrderID());
            data.deleterOrder(sellOrder.getOrderID());
            assertEquals(0 , data.getOrderSize("BUY"));
            assertEquals(0 , data.getOrderSize("SELL"));
            data.addOldOrder(buyOrder);
            data.addOldOrder(sellOrder);
            assertEquals(buyOrder, data.getOldOrder(buyOrder.getOrderID()));
            assertEquals(sellOrder, data.getOldOrder(sellOrder.getOrderID()));
        }
    }

    // Test 44. Tests updating a partially filled order
    @Test
    public void testUpdateOrder() {
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        if (buyOrder.getPrice() >= (sellOrder.getPrice())) {
            if (buyOrder.getAssetQty() <= sellOrder.getAssetQty()) {
                data.deleterOrder(buyOrder.getOrderID());
                assertEquals(10.0, data.getOrder(sellOrder.getOrderID()).getAssetQty());
                data.updateOrder(String.valueOf(
                        sellOrder.getAssetQty() - buyOrder.getAssetQty()),
                        sellOrder.getOrderID());
                assertEquals(5.0, data.getOrder(sellOrder.getOrderID()).getAssetQty());
            }
        }
    }

    // Test 45. Tests getting order type set
    @Test
    public void testOrderSet() {
        Set<String> buyOrders = new TreeSet<String>();
        Set<String> sellOrders = new TreeSet<String>();
        buyOrders.add(buyOrder.getOrderID());
        sellOrders.add(sellOrder.getOrderID());
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        assertEquals(buyOrders , data.orderSet("BUY"));
        assertEquals(sellOrders , data.orderSet("SELL"));
    }

    // Test 46. Tests getting an empty order set
    @Test
    public void testInvalidOrderSet() {
        Set<String> orders = new TreeSet<String>();
        assertEquals(orders , data.orderSet("TRADES"));
    }

    // Test 47. Tests getting asset order set
    @Test
    public void testAssetOrderSet() {
        Set<String> buyOrders = new TreeSet<String>();
        Set<String> sellOrders = new TreeSet<String>();
        buyOrders.add(buyOrder.getOrderID());
        sellOrders.add(sellOrder.getOrderID());
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        assertEquals(buyOrders , data.orderAssetSet("asset1", "BUY"));
        assertEquals(sellOrders , data.orderAssetSet("asset2", "SELL"));
    }

    // Test 48 Tests getting an empty asset order set
    @Test
    public void testInvalidAssetOrderSet() {
        Set<String> orders = new TreeSet<String>();
        assertEquals(orders , data.orderAssetSet("asset", "TRADE"));
    }

    // Test 49. Tests getting all orders set
    @Test
    public void testAllOrderSet() {
        Set<String> allOrders = new TreeSet<String>();
        allOrders.add(buyOrder.getOrderID());
        allOrders.add(sellOrder.getOrderID());
        data.addOrder(buyOrder);
        data.addOrder(sellOrder);
        assertEquals(allOrders , data.allOrderSet());
    }

    // Test 50 Tests getting an invalid orders set
    @Test
    public void testInvalidAllOrderSet() {
        Set<String> allOrders = new TreeSet<String>();
        assertEquals(allOrders , data.allOrderSet());
    }

    // Test 51. Tests getting asset order set
    @Test
    public void testOldOrderSet() {
        Set<String> buyOrders = new TreeSet<String>();
        Set<String> sellOrders = new TreeSet<String>();
        buyOrders.add(buyOrder.getOrderID());
        sellOrders.add(sellOrder.getOrderID());
        data.addOldOrder(buyOrder);
        data.addOldOrder(sellOrder);
        assertEquals(buyOrders , data.oldOrderSet("asset1"));
        assertEquals(sellOrders , data.oldOrderSet("asset2"));
    }

    // Test 52 Tests getting an empty asset order set
    @Test
    public void testInvalidOldOrderSet() {
        Set<String> orders = new TreeSet<String>();
        assertEquals(orders , data.oldOrderSet("asset"));
    }

    // Test 53. Tests getting all orders set
    @Test
    public void testAllOldOrderSet() {
        Set<String> allOldOrders = new TreeSet<String>();
        allOldOrders.add(buyOrder.getOrderID());
        allOldOrders.add(sellOrder.getOrderID());
        data.addOldOrder(buyOrder);
        data.addOldOrder(sellOrder);
        assertEquals(allOldOrders , data.allOldOrderSet());
    }

    // Test 54 Tests getting an invalid orders set
    @Test
    public void testInvalidAllOldOrderSet() {
        Set<String> allOldOrders = new TreeSet<String>();
        assertEquals(allOldOrders , data.allOldOrderSet());
    }

    // Test 55. Tests get of a non-existent old order
    @Test
    public void testGetNotExistOldOrder() {
        assertEquals(null , data.getOldOrder("NonExistentOrderID"));
    }

    // Test 55. Tests get of a non-existent old order
    @Test
    public void testGetCurrentOrgAssetSize() {
        data.addOrgAsset(orgAsset);
        assertEquals(1 , data.getCurrentOrgAssetSize("OrgUnit"));
    }

}
