package unitTesting;

import Common.Constructors.Order;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTesting {

    Order order;

    @BeforeEach
    public void initOrder() {
        order = new Order();
    }



    // Test 1. Tests the creation of a new Order
    @Test
    public void testCreateOrder() {
        order = new Order("tempId", "BUY", "Admin", "BTC", 1000.0, 20.0, "tempDate");
        assertEquals("tempId", order.getOrderID());
        assertEquals("BUY", order.getOrderType());
        assertEquals("Admin", order.getOrgUnit());
        assertEquals("BTC", order.getAssetName());
        assertEquals(1000.0, order.getAssetQty());
        assertEquals(20.0, order.getPrice());
        assertEquals("tempDate", order.getDate());
    }

    // Test 2. Tests set and get for each order parameter
    @Test
    public void testSetGet() {
        order.setOrderID("tempId2");
        order.setOrderType("SELL");
        order.setOrgUnit("Microsoft");
        order.setAssetName("ADA");
        order.setAssetQty(100.0);
        order.setPrice(100.0);
        order.setDate("tempDate2");
        assertEquals("tempId2", order.getOrderID());
        assertEquals("SELL", order.getOrderType());
        assertEquals("Microsoft", order.getOrgUnit());
        assertEquals("ADA", order.getAssetName());
        assertEquals(100.0, order.getAssetQty());
        assertEquals(100.0, order.getPrice());
        assertEquals("tempDate2", order.getDate());
    }

    // Test 3. Tests get for parameters of a non-existent Order
    @Test
    public void testOrderNotExists() {
        assertEquals(null, order.getOrderID());
        assertEquals(null, order.getOrderType());
        assertEquals(null, order.getOrgUnit());
        assertEquals(null, order.getAssetName());
        assertEquals(null, order.getAssetQty());
        assertEquals(null, order.getPrice());
        assertEquals(null, order.getDate());
    }
}
