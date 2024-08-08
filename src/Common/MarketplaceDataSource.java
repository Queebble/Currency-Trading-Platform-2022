package Common;

import Common.Constructors.*;

import java.util.Set;

/**
 * Provides functionality needed by any data source for the Marketplace.
 */
public interface MarketplaceDataSource {

    ////////////////////// USER FUNCTIONALITY ///////////////////

    /**
     * Adds a user to the marketplace, if they are not already in it.
     *
     * @param u User to add
     */
    void addUser(User u);

    /**
     * Extracts all the details of a User from the marketplace based
     * on the name passed in.
     *
     * @param user The user as a String to search for.
     * @return all details in a User object for the name.
     */
    User getUser(String user);

    /**
     * Gets the number of users in the marketplace.
     *
     * @return number of marketplace users.
     */
    int getUsersSize();

    /**
     * Deletes a user from the marketplace
     *
     * @param user The user to be deleted.
     */
    void deleteUser(String user);

    /**
     * Updates an organisations asset quantity in the marketplace.
     *
     * @param newUser The username to update.
     * @param pwd The password to update for the user.
     * @param accType The account type to update for the user.
     * @param org The organisation to update for the user.
     * @param oldUser The user to update details for.
     */
    void updateUser(String newUser, String pwd, String accType, String org, String oldUser);

    /**
     * Retrieves a set of users from the data source that are used
     * in the user list.
     *
     * @return set of users.
     */
    Set<String> userSet();

    ////////////////////// ORGANISATION FUNCTIONALITY ///////////////////

    /**
     * Adds an organisation to the marketplace, if they are not already in it.
     *
     * @param org organisation to add.
     */
    void addOrg(Org org);

    /**
     * Extracts all the details of an organisation from the marketplace based
     * on the name passed in.
     *
     * @param org The organisation as a String to search for.
     * @return all details in an organisations object for the name.
     */
    Org getOrg(String org);

    /**
     * Gets the number of organisations in the marketplace.
     *
     * @return number of marketplace organisations.
     */
    int getOrgSize();

    /**
     * Deletes an organisations from the marketplace.
     *
     * @param org The organisation to be deleted.
     */
    void deleteOrg(String org);

    /**
     * Updates an organisations credits in the marketplace.
     *
     * @param org The organisation getting updated.
     * @param credits The amount of credits to update
     */
    void updateCredits(String org, String credits);

    /**
     * Retrieves a set of organisations from the data source that are used
     * in the organisation list.
     *
     * @return set of organisations.
     */
    Set<String> orgSet();

    ////////////////////// ASSETS FUNCTIONALITY ///////////////////

    /**
     * Adds an asset to the marketplace, if they are not already in it.
     *
     * @param a organisation to add.
     */
    void addAsset(Asset a);

    /**
     * Extracts all the details of an asset from the marketplace based
     * on the name passed in.
     *
     * @param asset The asset as a String to search for.
     * @return all details of an asset object for the name.
     */
    Asset getAsset(String asset);

    /**
     * Gets the number of assets in the marketplace.
     *
     * @return number of marketplace assets.
     */
    int getAssetSize();

    /**
     * Deletes an asset from the marketplace.
     *
     * @param asset The asset to be deleted.
     */
    void deleteAsset(String asset);

    /**
     * Retrieves a set of assets from the data source that are used
     * in the assets list.
     *
     * @return set of assets.
     */
    Set<String> assetSet();

    ////////////////////// ORGANISATIONS ASSETS FUNCTIONALITY ///////////////////

    /**
     * Adds an asset to an organisation to the marketplace, if they do not already own it.
     *
     * @param a organisation asset to add.
     */
    void addOrgAsset(OrgAsset a);

    /**
     * Gets the number of asset types organisations own in the marketplace.
     *
     * @return number of asset types owned by organisations.
     */
    int getOrgAssetSize();

    /**
     * Gets the number of asset types organisations own in the marketplace.
     * @param org The organisation as a String to search for.
     * @return number of asset types owned by organisations.
     */
    int getCurrentOrgAssetSize(String org);

    /**
     * Extracts all the details for an organisation's asset from the marketplace based
     * on the organisation and asset passed in.
     *
     * @param org The organisation as a String to search for.
     * @param asset The asset as a String to search for.
     * @return all details for an organisation's asset object.
     */
    OrgAsset getOrgAsset(String org, String asset);

    /**
     * Checks if any organisations in the marketplace own a particular asset.
     * @param asset the asset to be checked.
     * @return true or false if an organisation owns the asset.
     */
    boolean checkOrgAsset(String asset);

    /**
     * Deletes an organisations asset from the marketplace.
     *
     * @param org the organisation for the asset to be deleted.
     * @param asset the asset to be deleted.
     */
    void deleteOrgAsset(String org, String asset);

    /**
     * Updates an organisations asset quantity in the marketplace.
     *
     * @param qty The quantity of the asset to update.
     * @param org The organisation to update asset quantity for.
     * @param asset The asset to update quantity of.
     */
    void updateAssetQuantity(String qty, String org, String asset);

    /**
     * Retrieves a set of organisations-asset from the data source that are used
     * in the organisations-asset list.
     *
     * @return set of organisations-asset.
     */
    Set<String> orgAssetSet();

    ////////////////////// ORDER FUNCTIONALITY ///////////////////

    /**
     * Adds an order to the marketplace.
     *
     * @param order order to add.
     */
    void addOrder(Order order);

    /**
     * Extracts all the details of an order from the marketplace based
     * on the orderID passed in.
     *
     * @param orderID The orderID to search for.
     * @return all details in an organisations object for the name.
     */
    Order getOrder(String orderID);

    /**
     * Gets the number of orders in the marketplace.
     * @param orderType the order type BUY or SELL
     * @return number of marketplace orders.
     */
    int getOrderSize(String orderType);

    /**
     * Deletes an order from the marketplace.
     *
     * @param orderID The order to be deleted.
     */
    void deleterOrder(String orderID);

    /**
     * Updates an order in the marketplace with the
     * remaining asset quantity to buy/sell.
     *
     * @param assetQty The remaining asset quantity.
     * @param orderID The orderID to update
     */
    void updateOrder(String assetQty, String orderID);

    /**
     * Retrieves a set of orders from the data source that are used
     * in an orders list for order types of an asset.
     *
     * @param orderType the type of order (BUY/SELL)
     * @return set of orders of particular type.
     */
    Set<String> orderSet(String orderType);

    /**
     * Retrieves a set of orders from the data source that are used
     * in an list for orders of an asset.
     *
     * @param asset the asset to retrieve orders for
     * @param orderType the type of order (BUY/SELL)
     * @return set of orders of a given type for a given asset.
     */
    Set<String> orderAssetSet(String asset, String orderType);

    /**
     * Retrieves a set of all orders from the data source.
     *
     * @return set of all orders.
     */
    Set<String> allOrderSet();

    ////////////////////// ORDER HISTORY FUNCTIONALITY ///////////////////

    /**
     * Adds an old order to the history in the marketplace.
     *
     * @param order order to add.
     */
    void addOldOrder(Order order);

    /**
     * Extracts all the details of an old order from the marketplace based
     * on the orderID passed in.
     *
     * @param orderID The orderID to search for.
     * @return all details in an organisations object for the name.
     */
    Order getOldOrder(String orderID);

    /**
     * Retrieves a set of old orders from the data source that are used
     * in an list for orders of an asset.
     *
     * @param asset the asset to retrieve orders for
     * @return set of orders of a given type for a given asset.
     */
    Set<String> oldOrderSet(String asset);

    /**
     * Retrieves a set of all old orders from the data source.
     *
     * @return set of all old orders.
     */
    Set<String> allOldOrderSet();

    /**
     * Finalizes any resources used by the data source and ensures data
     * is persisted.
     */
    void close();

}
