package Common;

import Common.Constructors.*;

import javax.swing.*;

/**
 * This class uses a MarketplaceDataSource and its methods to retrieve data.
 */
public class MarketplaceData {

    DefaultListModel userModel;
    DefaultListModel orgsModel;
    DefaultListModel assetModel;
    DefaultListModel orgsAssetModel;
    DefaultListModel buyOrdersModel;
    DefaultListModel sellOrdersModel;
    DefaultListModel buyAssetOrdersModel;
    DefaultListModel sellAssetOrdersModel;
    DefaultListModel allOrdersModel;
    DefaultListModel oldAssetOrdersModel;
    DefaultListModel allOldOrdersModel;

    MarketplaceDataSource marketplaceData;

    /**
     * Constructs list models from the data source.
     * @param dataSource the data source to send and receive from
     */
    public MarketplaceData(MarketplaceDataSource dataSource) {
        userModel = new DefaultListModel();
        orgsModel = new DefaultListModel();
        assetModel = new DefaultListModel();
        orgsAssetModel = new DefaultListModel();
        buyOrdersModel = new DefaultListModel();
        sellOrdersModel = new DefaultListModel();
        buyAssetOrdersModel = new DefaultListModel();
        sellAssetOrdersModel = new DefaultListModel();
        allOrdersModel = new DefaultListModel();
        allOldOrdersModel = new DefaultListModel();
        oldAssetOrdersModel = new DefaultListModel();

        marketplaceData = dataSource;

        for (String user : marketplaceData.userSet()) {
            userModel.addElement(user);
        }

        for (String org : marketplaceData.orgSet()) {
            orgsModel.addElement(org);
        }

        for (String asset : marketplaceData.assetSet()) {
            assetModel.addElement(asset);
        }

        for (String orgAsset : marketplaceData.orgAssetSet()) {
            orgsAssetModel.addElement(orgAsset);
        }

        for (String buyOrder : marketplaceData.orderSet("BUY")) {
            buyOrdersModel.addElement(buyOrder);
        }

        for (String sellOrder : marketplaceData.orderSet("SELL")) {
            sellOrdersModel.addElement(sellOrder);
        }

        for (String order : marketplaceData.allOrderSet()) {
            allOrdersModel.addElement(order);
        }

        for (String oldOrder : marketplaceData.allOldOrderSet()) {
            allOldOrdersModel.addElement(oldOrder);
        }
    }


    ////////////////////// USER FUNCTIONALITY ///////////////////

    /**
     * Adds a user to the marketplace.
     *
     * @param u A user to add to be added.
     */
    public void addUser(User u) {

        // check to see if the user is already in the model
        // if not add to the marketplace and the list model
        if (!userModel.contains(u.getUser())) {
            userModel.addElement(u.getUser());
            marketplaceData.addUser(u);
        }
    }

    /**
     * Based on the name of the user in the marketplace, delete the user.
     *
     * @param key user to be removed.
     */
    public void removeUser(Object key) {

        // remove from both list and marketplace
        userModel.removeElement(key);
        marketplaceData.deleteUser((String) key);
    }

    /**
     * Retrieves User details from the data source.
     *
     * @param user the user to retrieve.
     * @return the user object related to the username.
     */
    public User getUser(Object user) {
        return marketplaceData.getUser((String) user);
    }

    /**
     * Accessor for the user listmodel.
     *
     * @return the user listModel.
     */
    public ListModel getUserModel() {
        userModel.removeAllElements();
        for (String user : marketplaceData.userSet()) {
            userModel.addElement(user);
        }
        return userModel;
    }

    /**
     * Based on the name of the organisation in the marketplace,
     * update the organisation with the number of credits.
     *
     * @param newUser new username to be updated.
     * @param pwd user's password to be updated.
     * @param accType user's account type to be updated.
     * @param org user's organisation to be updated.
     * @param oldUser user to udpate details for.
     */
    public void updateUser(Object newUser, Object pwd, Object accType, Object org, Object oldUser) {
        marketplaceData.updateUser((String) newUser, (String) pwd, (String) accType, (String) org, (String) oldUser);
    }


    /**
     * @return the number of users in the marketplace.
     */
    public int getUserSize() {
        return marketplaceData.getUsersSize();
    }



    ////////////////////// ORGANISATION FUNCTIONALITY ///////////////////

    /**
     * Adds an organisation to the marketplace.
     *
     * @param org the organisation to be added.
     */
    public void addOrg(Org org) {

        // check to see if the org is already in the model
        // if not add to the marketplace and the list model
        if (!orgsModel.contains(org.getOrgUnit())) {
            orgsModel.addElement(org.getOrgUnit());
            marketplaceData.addOrg(org);
        }
    }

    /**
     * Based on the name of the organisation in the marketplace,
     * delete the organisation.
     *
     * @param org organisation to be removed.
     */
    public void removeOrg(Object org) {
        marketplaceData.deleteOrg((String) org);

        if (!marketplaceData.orgSet().contains((String) org)) {
            // remove from both list and marketplace
            orgsModel.removeElement(org);
        }
    }

    /**
     * Based on the name of the organisation in the marketplace,
     * update the organisation with the number of credits.
     *
     * @param org organisation to be updated.
     * @param credits credits to be updated.
     */
    public void updateCredits(Object org, Object credits) {
        marketplaceData.updateCredits((String) org, (String) credits);
    }

    /**
     * Retrieves Organisation details from the data source.
     *
     * @param org the organisation to retrieve.
     * @return the organisation object related to the organisation.
     */
    public Org getOrg(Object org) {
        return marketplaceData.getOrg((String) org);
    }

    /**
     * Accessor for the organisation listmodel.
     *
     * @return the organisation listModel.
     */
    public ListModel getOrgModel() {
        orgsModel.removeAllElements();
        for (String org : marketplaceData.orgSet()) {
            orgsModel.addElement(org);
        }
        return orgsModel;
    }

    /**
     * @return the number of organisations in the marketplace.
     */
    public int getOrgSize() {
        return marketplaceData.getOrgSize();
    }



    ////////////////////// ASSET FUNCTIONALITY ///////////////////

    /**
     * Adds an asset to the marketplace.
     *
     * @param a the asset to be added.
     */
    public void addAsset(Asset a) {
            // check to see if the asset is already in the model
            // if not add to the marketplace and the list model
            if (!assetModel.contains(a.getAssetName())) {
                assetModel.addElement(a.getAssetName());
                marketplaceData.addAsset(a);
            }
    }

    /**
     * Based on the name of the asset in the marketplace,
     * delete the asset.
     *
     * @param asset asset to be removed.
     */
    public void removeAsset(Object asset) {
        //check if asset is associated with any org
        if (!checkOrgAsset(asset)) {
            // remove from both list and marketplace
            assetModel.removeElement(asset);
            marketplaceData.deleteAsset((String) asset);
        }
    }

    /**
     * Retrieves asset name from the data source.
     *
     * @param asset the asset to retrieve.
     * @return the asset object related to the asset.
     */
    public Asset getAsset(Object asset) {
        return marketplaceData.getAsset((String) asset);
    }

    /**
     * Accessor for the asset listmodel.
     *
     * @return the asset listModel.
     */
    public ListModel getAssetModel() {
        assetModel.removeAllElements();
        for (String asset : marketplaceData.assetSet()) {
            assetModel.addElement(asset);
        }
        return assetModel;
    }

    /**
     * @return the number of assets in the marketplace.
     */
    public int getAssetSize() {
        return marketplaceData.getAssetSize();
    }

    ////////////////////// ORGANISATIONS ASSET FUNCTIONALITY ///////////////////

    /**
     * Adds an asset to an organisation in the marketplace.
     *
     * @param newOrgAsset the new organisation-asset pair to add to the model.
     * @param orgAsset the organisation-asset object to add to the database.
     */
    public void addOrgAsset(String newOrgAsset, OrgAsset orgAsset) {
        // check to see if the asset is already in the model
        // if not add to the marketplace and the list model
        if (!orgsAssetModel.contains(newOrgAsset)) {
            orgsAssetModel.addElement(newOrgAsset);
            marketplaceData.addOrgAsset(orgAsset);
        }
    }

    /**
     * Based on the names of the organisation and asset in the marketplace,
     * delete the organisations asset.
     *
     * @param oldOrgAsset organisation for asset to be removed from.
     * @param orgAsset asset to be removed
     */
    public void removeOrgAsset(String oldOrgAsset, OrgAsset orgAsset) {
        String org = orgAsset.getOrgUnit();
        String asset = orgAsset.getAssetName();
        // remove from both list and marketplace
        orgsAssetModel.removeElement(oldOrgAsset);
        marketplaceData.deleteOrgAsset(org, asset);
    }

    /**
     * Retrieves Organisations asset details from the data source.
     *
     * @param key the organisation to retrieve.
     * @param key2 the asset to retrieve
     * @return the organisation-asset object related to the organisation and asset.
     */
    public OrgAsset getOrgAsset(Object key, Object key2) {
        return marketplaceData.getOrgAsset((String) key, (String) key2);
    }

    /**
     * Checks if an organisation possesses an asset.
     *
     * @param key the asset to query
     * @return true or false depending on if the organisations
     * has the asset.
     */
    public boolean checkOrgAsset(Object key) {
        return marketplaceData.checkOrgAsset((String) key);
    }

    /**
     * Based on the name of the organisation in the marketplace,
     * update the quantity of an asset for the organisation.
     *
     * @param qty quantity of asset to be updated.
     * @param org organisation to be updated.
     * @param asset asset for quantity to be updated.
     */
    public void updateAssetQuantity(Object qty, Object org, Object asset) {
        marketplaceData.updateAssetQuantity((String) qty, (String) org, (String) asset);
    }

    /**
     * Accessor for the organisations assets listmodel.
     *
     * @return the organisation assets listModel.
     */
    public ListModel getOrgAssetModel() {
        orgsAssetModel.removeAllElements();
        for (String orgAsset : marketplaceData.orgAssetSet()) {
            orgsAssetModel.addElement(orgAsset);
        }
        return orgsAssetModel;
    }

    /**
     * @return the number of assets (type) all organisations posses in the marketplace.
     */
    public int getOrgAssetSize() {
        return marketplaceData.getOrgAssetSize();
    }

    /**
     * @param org Organisations asset size to be grabbed
     * @return the number of assets (type) all organisations posses in the marketplace.
     */
    public int getCurrentOrgAssetSize(String org) {
        return marketplaceData.getCurrentOrgAssetSize(org);
    }

    ////////////////////// ORDER FUNCTIONALITY ///////////////////

    /**
     * Adds an order to the marketplace.
     *
     * @param o An order to add to be added.
     */
    public void addOrder(Order o) {

        // check to see if the order is of type 'BUY' or 'SELL"
        // if not add to the marketplace and the list model
        if (o.getOrderType().equals("BUY")) {
            buyOrdersModel.addElement(o.getOrderID());
        }
        if (o.getOrderType().equals("SELL")) {
            sellOrdersModel.addElement(o.getOrderID());
        }
        marketplaceData.addOrder(o);
    }

    /**
     * Based on the orderID in the marketplace, delete the order.
     *
     * @param orderID orderID of order to be removed.
     */
    public void removeOrder(Object orderID) {

        // remove the order from its orderType list and the marketplace
        if (buyOrdersModel.contains(orderID)) {
            buyOrdersModel.removeElement(orderID);
        }
        if (sellOrdersModel.contains(orderID)) {
            sellOrdersModel.removeElement(orderID);
        }
        marketplaceData.deleterOrder((String) orderID);
    }

    /**
     * Retrieves order details from the data source.
     *
     * @param orderID the user to retrieve.
     * @return the user object related to the username.
     */
    public Order getOrder(Object orderID) {
        return marketplaceData.getOrder((String) orderID);
    }

    /**
     * Accessor for the buy order listmodels.
     *
     * @return the buy order listModel.
     */
    public ListModel getBuyOrderModel() {
        buyOrdersModel.removeAllElements();
        for (String buyOrder : marketplaceData.orderSet("BUY")) {
            buyOrdersModel.addElement(buyOrder);
        }
        return buyOrdersModel;
    }

    /**
     * Accessor for the sell order listmodel.
     *
     * @return the sell order listModel.
     */
    public ListModel getSellOrderModel() {
        sellOrdersModel.removeAllElements();
        for (String sellOrder : marketplaceData.orderSet("SELL")) {
            sellOrdersModel.addElement(sellOrder);
        }
        return sellOrdersModel;
    }

    /**
     * Accessor for the asset order listmodel.
     * @param asset asset to get
     * @param orderType order type BUY or SELL
     * @return the asset order listModel.
     */
    public ListModel getAssetOrderModel(String asset, String orderType) {
        if (orderType.equals("BUY")) {
            buyAssetOrdersModel.removeAllElements();
            for (String assetOrder : marketplaceData.orderAssetSet(asset, orderType)) {
                buyAssetOrdersModel.addElement(assetOrder);
            }
            return buyAssetOrdersModel;
        }
        if (orderType.equals("SELL")) {
            sellAssetOrdersModel.removeAllElements();
            for (String assetOrder : marketplaceData.orderAssetSet(asset, orderType)) {
                sellAssetOrdersModel.addElement(assetOrder);
            }
            return sellAssetOrdersModel;
        }
        return null;
    }

    /**
     * Accessor for the all orders listmodel.
     *
     * @return the all orders listModel.
     */
    public ListModel getAllOrdersModel() {
        allOrdersModel.removeAllElements();
        for (String order : marketplaceData.allOrderSet()) {
            allOrdersModel.addElement(order);
        }
        return allOrdersModel;
    }

    /**
     * Based on the name of the organisation in the marketplace,
     * update the organisation with the number of credits.
     *
     * @param assetQty new asset quantity to be updated.
     * @param orderID orderID to be updated.
     */
    public void updateOrder(Object assetQty, Object orderID) {
        marketplaceData.updateOrder((String) assetQty, (String) orderID);
    }

    /**
     * 
     * @param orderType order type BUY or SELL
     * @return the number of orders in the marketplace.
     */
    public int getOrderSize(String orderType) {
        return marketplaceData.getOrderSize(orderType);
    }

    ////////////////////// ORDER HISTORY FUNCTIONALITY ///////////////////

    /**
     * Adds an old order to the marketplace history.
     *
     * @param o An old order to add to be added.
     */
    public void addOldOrder(Order o) {
        allOldOrdersModel.addElement(o.getOrderID());
        marketplaceData.addOldOrder(o);
    }

    /**
     * Retrieves order details from the data source.
     *
     * @param orderID the user to retrieve.
     * @return the user object related to the username.
     */
    public Order getOldOrder(Object orderID) {
        return marketplaceData.getOldOrder((String) orderID);
    }

    /**
     * Accessor for the old order asset listmodels.
     * @param assetName the asset name
     * @return the old asset order listModel.
     */
    public ListModel getOldOrderModel(String assetName) {
        oldAssetOrdersModel.removeAllElements();
        for (String oldAssetOrder : marketplaceData.oldOrderSet(assetName)) {
            oldAssetOrdersModel.addElement(oldAssetOrder);
        }
        return oldAssetOrdersModel;
    }

    /**
     * Accessor for the all orders listmodel.
     *
     * @return the all orders listModel.
     */
    public ListModel getAllOldOrderModel() {
        allOldOrdersModel.removeAllElements();
        for (String oldOrder : marketplaceData.allOldOrderSet()) {
            allOldOrdersModel.addElement(oldOrder);
        }
        return allOldOrdersModel;
    }

    /**
     * Saves the data in the marketplace using a persistence
     * mechanism.
     */
    public void close() {
        marketplaceData.close();
    }

}
