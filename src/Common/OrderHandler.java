package Common;

import Common.Constructors.Org;
import Common.Constructors.User;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for handling all incoming and outstanding
 * buy and sell orders.
 */
public class OrderHandler {

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs the order handler with the marketplace data
     */
    public OrderHandler(){ }

    /**
     * Creates an order object with a unique OrderID and the
     * associated parameters.
     *
     * @param orderType the type of order (BUY/SELL)
     * @param orgUnit the organisation making the order
     * @param assetQty number of asset to be bought
     * @param assetName the asset being bought
     * @param price the buy price per 1 asset
     * @param date the date and time the order was placed
     * @return returns the order
     */
    public Object[] createOrder(String orderType, String orgUnit, String assetName, double assetQty,
                                double price, LocalDateTime date) {

        Object[] order;

        UUID orderID = UUID.randomUUID();

        order = new Object[]{
                orderID,
                orderType,
                orgUnit,
                assetName,
                assetQty,
                price,
                dtf.format(date)
        };
        return order;
    }
}
