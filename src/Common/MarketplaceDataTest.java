package Common;

import Common.Constructors.*;

import javax.swing.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class uses a MarketplaceDataSource and its methods to retrieve data.
 */
public class MarketplaceDataTest implements MarketplaceDataSource {

    DefaultListModel userModel;
    DefaultListModel orgsModel;
    DefaultListModel assetModel;
    DefaultListModel orgsAssetModel;
    DefaultListModel buyOrdersModel;
    DefaultListModel sellOrdersModel;
    DefaultListModel buyAssetOrdersModel;
    DefaultListModel sellAssetOrdersModel;
    DefaultListModel allOrdersModel;
    DefaultListModel allOldOrdersModel;

    /**
     * Constructs list models to act as a mock database
     */
    public MarketplaceDataTest() {
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
    }

    //////////////// USER METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addUser(User)
     */
    @Override
    public void addUser(User u) {
        if (!userModel.contains(u)) {
            userModel.addElement(u);
        }
    }

    /**
     * @see MarketplaceDataSource#getUser(String)
     */
    public User getUser(String user) {
        User u;
        for (int i = 0; i < userModel.getSize(); i++) {
            u = (User) userModel.get(i);
            if (u.getUser().equals(user)) {
                return u;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#getUsersSize()
     */
    public int getUsersSize() {
        return userModel.size();
    }

    /**
     * @see MarketplaceDataSource#deleteUser(String)
     */
    public void deleteUser(String user) {
        User u = getUser(user);
        userModel.removeElement(u);
    }

    /**
     * @see MarketplaceDataSource#updateUser(String, String, String, String, String)
     */
    public void updateUser(String newUser, String pwd, String accType, String org, String oldUser) {
        User u;
        for (int i = 0; i < userModel.getSize(); i++) {
            u = (User) userModel.get(i);
            if (u.getUser().equals(newUser)) {
                return;
            }
        }
        for (int i = 0; i < userModel.getSize(); i++) {
            u = (User) userModel.get(i);
            if (u.getUser().equals(oldUser)) {
                u.setUser(newUser);
                u.setPassword(pwd);
                u.setAccType(accType);
                u.setOrgUnit(org);
            }
        }
    }

    /**
     * @see MarketplaceDataSource#userSet()
     */
    public Set<String> userSet() {
        Set<String> users = new TreeSet<String>();
        User u;
        for (int i = 0; i < userModel.getSize(); i++) {
            u = (User) userModel.get(i);
            users.add(u.getUser());
        }
        return users;
    }

    //////////////// ORG METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addOrg(Org)
     */
    @Override
    public void addOrg(Org org) {
        if (!orgsModel.contains(org)) {
            orgsModel.addElement(org);
        }
    }

    /**
     * @see MarketplaceDataSource#getOrg(String)
     */
    @Override
    public Org getOrg(String org) {
        Org o;
        for (int i = 0; i < orgsModel.getSize(); i++) {
            o = (Org) orgsModel.get(i);
            if (o.getOrgUnit().equals(org)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#getOrgSize()
     */
    @Override
    public int getOrgSize() {
        return orgsModel.size();
    }

    /**
     * @see MarketplaceDataSource#deleteOrg(String)
     */
    @Override
    public void deleteOrg(String org) {
        Org o = getOrg(org);
        orgsModel.removeElement(o);
    }

    /**
     * @see MarketplaceDataSource#updateCredits(String, String)
     */
    @Override
    public void updateCredits(String org, String credits) {
        Org o;
        Double newCredits = Double.parseDouble(credits);
        for (int i = 0; i < orgsModel.getSize(); i++) {
            o = (Org) orgsModel.get(i);
            if (o.getOrgUnit().equals(org)) {
                o.setCredits(newCredits);
            }
        }
    }

    /**
     * @see MarketplaceDataSource#orgSet()
     */
    @Override
    public Set<String> orgSet() {
        Set<String> orgs = new TreeSet<String>();
        Org o;
        for (int i = 0; i < orgsModel.getSize(); i++) {
            o = (Org) orgsModel.get(i);
            orgs.add(o.getOrgUnit());
        }
        return orgs;
    }

    //////////////// ASSET METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addAsset(Asset)
     */
    @Override
    public void addAsset(Asset a) {
        if (!assetModel.contains(a)) {
            assetModel.addElement(a);
        }
    }

    /**
     * @see MarketplaceDataSource#getAsset(String)
     */
    @Override
    public Asset getAsset(String asset) {
        Asset a;
        for (int i = 0; i < assetModel.getSize(); i++) {
            a = (Asset) assetModel.get(i);
            if (a.getAssetName().equals(asset)) {
                return a;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#getAssetSize()
     */
    @Override
    public int getAssetSize() {
        return assetModel.size();
    }

    /**
     * @see MarketplaceDataSource#deleteAsset(String)
     */
    @Override
    public void deleteAsset(String asset) {
        Asset a = getAsset(asset);
        assetModel.removeElement(a);
    }

    /**
     * @see MarketplaceDataSource#assetSet()
     */
    @Override
    public Set<String> assetSet() {
        Set<String> assets = new TreeSet<String>();
        Asset a;
        for (int i = 0; i < assetModel.getSize(); i++) {
            a = (Asset) assetModel.get(i);
            assets.add(a.getAssetName());
        }
        return assets;
    }

    //////////////// ORG-ASSET METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addOrgAsset(OrgAsset)
     */
    @Override
    public void addOrgAsset(OrgAsset oA) {
        if (!orgsAssetModel.contains(oA)) {
            orgsAssetModel.addElement(oA);
        }
    }

    /**
     * @see MarketplaceDataSource#getOrgAssetSize()
     */
    @Override
    public int getOrgAssetSize() {
        return orgsAssetModel.size();
    }

    @Override
    public int getCurrentOrgAssetSize(String org) {
        OrgAsset oA;
        int x = 0;
        for (int i = 0; i < orgsAssetModel.getSize(); i++) {
            oA = (OrgAsset) orgsAssetModel.get(i);
            if (oA.getOrgUnit().equals(org)) {
                x++;
            }
        }
        return x;
    }

    /**
     * @see MarketplaceDataSource#getOrgAsset(String, String)
     */
    @Override
    public OrgAsset getOrgAsset(String org, String asset) {
        OrgAsset oA;
        for (int i = 0; i < orgsAssetModel.getSize(); i++) {
            oA = (OrgAsset) orgsAssetModel.get(i);
            if (oA.getOrgUnit().equals(org) && oA.getAssetName().equals(asset)) {
                return oA;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#checkOrgAsset(String)
     */
    @Override
    public boolean checkOrgAsset(String asset) {
        OrgAsset oA;
        for (int i = 0; i < orgsAssetModel.getSize(); i++) {
            oA = (OrgAsset) orgsAssetModel.get(i);
            if (oA.getAssetName().equals(asset)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see MarketplaceDataSource#deleteOrgAsset(String, String)
     */
    @Override
    public void deleteOrgAsset(String org, String asset) {
        OrgAsset oA = getOrgAsset(org, asset);
        orgsAssetModel.removeElement(oA);
    }

    /**
     * @see MarketplaceDataSource#updateAssetQuantity(String, String, String)
     */
    @Override
    public void updateAssetQuantity(String qty, String org, String asset) {
        OrgAsset oA;
        Double assetQty = Double.parseDouble(qty);
        for (int i = 0; i < orgsAssetModel.getSize(); i++) {
            oA = (OrgAsset) orgsAssetModel.get(i);
            if (oA.getOrgUnit().equals(org) && oA.getAssetName().equals(asset)) {
                oA.setOrgUnit(org);
                oA.setAssetName(asset);
                oA.setAssetQty(assetQty);
            }
        }
    }

    /**
     * @see MarketplaceDataSource#orgAssetSet()
     */
    @Override
    public Set<String> orgAssetSet() {
        Set<String> orgAssets = new TreeSet<String>();
        OrgAsset oA;
        for (int i = 0; i < orgsAssetModel.getSize(); i++) {
            oA = (OrgAsset) orgsAssetModel.get(i);
            orgAssets.add(oA.getOrgUnit() + " " + oA.getAssetName());
        }
        return orgAssets;
    }

    //////////////// ORDER METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addOrder(Order)
     */
    @Override
    public void addOrder(Order order) {
        if (!allOrdersModel.contains(order)) {
            allOrdersModel.addElement(order);
            if (order.getOrderType().equals("BUY")) {
                    buyOrdersModel.addElement(order.getOrderID());
            }
            if (order.getOrderType().equals("SELL")) {
                    sellOrdersModel.addElement(order.getOrderID());
            }
        }
    }

    /**
     * @see MarketplaceDataSource#getOrder(String)
     */
    @Override
    public Order getOrder(String orderID) {
        Order o;
        for (int i = 0; i < allOrdersModel.getSize(); i++) {
            o = (Order) allOrdersModel.get(i);
            if (o.getOrderID().equals(orderID)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#getOrderSize(String)
     */
    @Override
    public int getOrderSize(String orderType) {
        if (orderType.equals("BUY")) {
            return buyOrdersModel.size();
        }
        if (orderType.equals("SELL")) {
            return sellOrdersModel.size();
        }
        return 0;
    }

    /**
     * @see MarketplaceDataSource#deleterOrder(String)
     */
    @Override
    public void deleterOrder(String orderID) {
        if (buyOrdersModel.contains(orderID)) {
            buyOrdersModel.removeElement(orderID);
        }
        if (sellOrdersModel.contains(orderID)) {
            sellOrdersModel.removeElement(orderID);
        }
    }

    /**
     * @see MarketplaceDataSource#updateOrder(String, String)
     */
    @Override
    public void updateOrder(String assetQty, String orderID) {
        Order o;
        for (int i = 0; i < allOrdersModel.getSize(); i++) {
            o = (Order) allOrdersModel.get(i);
            if (o.getOrderID().equals(orderID)) {
                o.setAssetQty(Double.parseDouble(assetQty));
            }
        }
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> orderSet(String orderType) {
        Set<String> typeOrders = new TreeSet<String>();
        if (orderType.equals("BUY")) {
            for (int i = 0; i < buyOrdersModel.getSize(); i++) {
                typeOrders.add((String) buyOrdersModel.get(i));
            }
        }
        if (orderType.equals("SELL")) {
            for (int i = 0; i < sellOrdersModel.getSize(); i++) {
                typeOrders.add((String) sellOrdersModel.get(i));
            }
        }
        return typeOrders;
    }

    /**
     * @see MarketplaceDataSource#orderAssetSet(String, String)
     */
    @Override
    public Set<String> orderAssetSet(String asset, String orderType) {
        Set<String> typeOrders = new TreeSet<String>();
        Order o;
        if (orderType.equals("BUY")) {
            for (int i = 0; i < buyOrdersModel.getSize(); i++) {
                String order = (String) buyOrdersModel.get(i);
                o = getOrder(order);
                if (o.getAssetName().equals(asset)) {
                    typeOrders.add(o.getOrderID());
                }
            }
        }
        if (orderType.equals("SELL")) {
            for (int i = 0; i < sellOrdersModel.getSize(); i++) {
                String order = (String) sellOrdersModel.get(i);
                o = getOrder(order);
                if (o.getAssetName().equals(asset)) {
                    typeOrders.add(o.getOrderID());
                }
            }
        }
        return typeOrders;
    }

    /**
     * @see MarketplaceDataSource#allOrderSet()
     */
    @Override
    public Set<String> allOrderSet() {
        Set<String> allOrders = new TreeSet<String>();
        Order o;
        for (int i = 0; i < allOrdersModel.getSize(); i++) {
            o = (Order) allOrdersModel.get(i);
            allOrders.add(o.getOrderID());
        }
        return allOrders;
    }

    //////////////// ORDER HISTORY METHODS /////////////////

    /**
     * @see MarketplaceDataSource#addOldOrder(Order)
     */
    @Override
    public void addOldOrder(Order order) {
        allOldOrdersModel.addElement(order);
    }

    /**
     * @see MarketplaceDataSource#getOldOrder(String)
     */
    @Override
    public Order getOldOrder(String orderID) {
        Order o;
        for (int i = 0; i < allOldOrdersModel.getSize(); i++) {
            o = (Order) allOldOrdersModel.get(i);
            if (o.getOrderID().equals(orderID)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @see MarketplaceDataSource#oldOrderSet(String)
     */
    @Override
    public Set<String> oldOrderSet(String asset) {
        Set<String> oldAssetOrders = new TreeSet<String>();
        Order o;
        for (int i = 0; i < allOldOrdersModel.getSize(); i++) {
            o = (Order) allOldOrdersModel.get(i);
            if (o.getAssetName().equals(asset)) {
                oldAssetOrders.add(o.getOrderID());
            }
        }
        return oldAssetOrders;
    }

    /**
     * @see MarketplaceDataSource#allOldOrderSet()
     */
    @Override
    public Set<String> allOldOrderSet() {
        Set<String> allOldOrders = new TreeSet<String>();
        Order o;
        for (int i = 0; i < allOldOrdersModel.getSize(); i++) {
            o = (Order) allOldOrdersModel.get(i);
            allOldOrders.add(o.getOrderID());
        }
        return allOldOrders;
    }

    @Override
    public void close() {
        // ignore
    }

}