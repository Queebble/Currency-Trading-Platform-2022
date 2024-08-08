package Common.Constructors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Constructs a new instance of an orders history
 */
public class Order implements Serializable {
    private static final long serialVersionUID = -7892320595830391535L;

    private String orderID;
    private String orderType;
    private String orgUnit;
    private String assetName;
    private Double assetQty;
    private Double price;
    private String date;

    /**
     * No args constructor
     */
    public Order(){
    }

    /**
     * initialises a order history
     * @param orderID the ID of the order
     * @param orderType the type of the order, Buy or Sell
     * @param orgUnit the name of the organisation
     * @param assetName the name of the asset
     * @param assetQty the quantity of the asset
     * @param price the price per asset
     * @param date the time and date the order was placed
     */
    public Order(String orderID, String orderType, String  orgUnit, String assetName, Double assetQty, Double price, String date) {
        this.orderID = orderID;
        this.orderType = orderType;
        this.orgUnit = orgUnit;
        this.assetName = assetName;
        this.assetQty = assetQty;
        this.price = price;
        this.date = date;
    }

    /**
     * gets the order ID
     * @return the orderType
     */
    public String getOrderID() { return orderID; }

    /**
     * sets the order ID
     * @param orderID Sets the orderID
     */
    public void setOrderID(String orderID) { this.orderID = orderID; }

    /**
     * gets the order type BUY or SELL
     * @return the orderType
     */
    public String getOrderType() { return orderType; }

    /**
     * sets the order type BUY or SELL
     * @param orderType Sets the order type
     */
    public void setOrderType(String orderType) { this.orderType = orderType; }

    /**
     * gets the organisation unit
     * @return the organisation unit
     */
    public String getOrgUnit() { return orgUnit; }

    /**
     * sets the organisation unit
     * @param orgUnit the organisation unit to be set
     */
    public void setOrgUnit(String orgUnit) { this.orgUnit = orgUnit; }

    /**
     * gets the asset names
     * @return the asset names
     */
    public String getAssetName() { return assetName; }

    /**
     * sets the asset names
     * @param assetName the asset name to be set
     */
    public void setAssetName(String assetName) { this.assetName = assetName; }

    /**
     * gets the asset quantity
     * @return the number of assets
     */
    public Double getAssetQty() { return assetQty; }

    /**
     * sets the asset quantity
     * @param assetQty the assets to be set
     */
    public void setAssetQty(Double assetQty) { this.assetQty = assetQty; }

    /**
     * gets the price per asset
     * @return the price per asset
     */
    public Double getPrice() { return price; }

    /**
     * sets the price per asset
     * @param price to be set
     */
    public void setPrice(Double price) { this.price = price; }

    /**
     * gets the date of the transaction
     * @return the date and time of the order
     */
    public String getDate() { return date; }

    /**
     * sets the date of the transaction
     * @param date of the order to be set
     */
    public void setDate(String date) { this.date = date; }

}