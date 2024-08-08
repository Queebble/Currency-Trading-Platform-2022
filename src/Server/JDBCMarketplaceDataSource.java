package Server;

import Common.Constructors.*;
import Common.MarketplaceDataSource;
import Server.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class for retrieving data from the connected database.
 */
public class JDBCMarketplaceDataSource implements MarketplaceDataSource {

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS users ("
                    + "username VARCHAR(45),"
                    + "password VARCHAR(255),"
                    + "accType VARCHAR(45),"
                    + "orgUnit VARCHAR(45),"
                    + "PRIMARY KEY (`username`),"
                    + "FOREIGN KEY (`orgUnit`) REFERENCES orgs(`orgUnit`)" + ");";

    private static final String CREATE_ORG_TABLE =
            "CREATE TABLE IF NOT EXISTS orgs ("
                    + "orgUnit VARCHAR(45),"
                    + "credits DOUBLE(30,8),"
                    + "PRIMARY KEY (`orgUnit`)" + ");";

    private static final String CREATE_ORG_ASSET_TABLE =
            "CREATE TABLE IF NOT EXISTS orgAssets ("
                    + "orgUnit VARCHAR(45),"
                    + "assetName VARCHAR(45),"
                    + "assetQty DOUBLE(30,8),"
                    + "PRIMARY KEY (`orgUnit`, `assetName`),"
                    + "FOREIGN KEY (`orgUnit`) REFERENCES orgs(`orgUnit`),"
                    + "FOREIGN KEY (`assetName`) REFERENCES assets(`assetName`)" + ");";

    private static final String CREATE_ASSET_TABLE =
            "CREATE TABLE IF NOT EXISTS assets ("
                    + "assetName VARCHAR(45),"
                    + "PRIMARY KEY (`assetName`)" + ");";

    private static final String CREATE_TRADE_TABLE =
            "CREATE TABLE IF NOT EXISTS orders ("
                    + "orderID VARCHAR(45),"
                    + "orderType VARCHAR(45),"
                    + "orgUnit VARCHAR(45),"
                    + "assetName VARCHAR(45),"
                    + "assetQty DOUBLE(30,8),"
                    + "price DOUBLE(30,8),"
                    + "date DATETIME,"
                    + "PRIMARY KEY (`orderID`),"
                    + "FOREIGN KEY (`orgUnit`) REFERENCES orgs(`orgUnit`),"
                    + "FOREIGN KEY (`assetName`) REFERENCES assets(`assetName`)" + ");";

    private static final String CREATE_TRADE_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS orderHistory ("
                    + "orderID VARCHAR(45),"
                    + "orderType VARCHAR(45),"
                    + "orgUnit VARCHAR(45),"
                    + "assetName VARCHAR(45),"
                    + "assetQty DOUBLE(30,8),"
                    + "price DOUBLE(30,8),"
                    + "date DATETIME,"
                    + "PRIMARY KEY (`orderID`),"
                    + "FOREIGN KEY (`orgUnit`) REFERENCES orgs(`orgUnit`),"
                    + "FOREIGN KEY (`assetName`) REFERENCES assets(`assetName`)" + ");";

    private static final String CREATE_ADMIN =
            "INSERT IGNORE INTO users (username, password, accType, orgUnit) "
                    + "VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 'admin', 'admin');";

    private static final String CREATE_ADMIN_ORG =
            "INSERT IGNORE INTO orgs (orgUnit, credits)"
                    + "VALUES ('admin', 0);";

    // prepared statements for users table
    private static final String INSERT_USER = "INSERT INTO users (username, password, accType, orgUnit) VALUES (?, ?, ?, ?);";
    private static final String GET_USERS = "SELECT username FROM users";
    private static final String GET_USER = "SELECT * FROM users WHERE username=?";
    private static final String DELETE_USER = "DELETE FROM users WHERE username=?";
    private static final String UPDATE_USER = "UPDATE users SET username=?, password=?, accType=?, orgUnit=? WHERE username=?";
    private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";

    // prepared statements for orgs table
    private static final String INSERT_ORG = "INSERT INTO orgs (orgUnit, credits) VALUES (?, ?);";
    private static final String GET_ORGS = "SELECT orgUnit FROM orgs";
    private static final String GET_ORG = "SELECT * FROM orgs WHERE orgUnit=?";
    private static final String DELETE_ORG = "DELETE FROM orgs WHERE orgUnit=?";
    private static final String UPDATE_CREDITS = "UPDATE orgs SET credits=? WHERE orgUnit=?";
    private static final String COUNT_ORGS = "SELECT COUNT(*) FROM orgs";

    // prepared statements for assets table
    private static final String INSERT_ASSET = "INSERT INTO assets (assetName) VALUES (?);";
    private static final String GET_ASSETS = "SELECT assetName FROM assets";
    private static final String GET_ASSET = "SELECT * FROM assets WHERE assetName=?";
    private static final String DELETE_ASSET = "DELETE FROM assets WHERE assetName=?";
    private static final String COUNT_ASSETS = "SELECT COUNT(*) FROM assets";

    // prepared statements for orgAssets table
    private static final String INSERT_ORG_ASSET = "INSERT INTO orgAssets (orgUnit, assetName, assetQty) VALUES (?, ?, ?);";
    private static final String GET_ORG_ASSETS = "SELECT * FROM orgAssets";
    private static final String GET_ORG_ASSET = "SELECT * FROM orgAssets WHERE orgUnit=? AND assetName=?";
    private static final String CHECK_ORG_ASSET = "SELECT * FROM orgAssets WHERE assetName=?";
    private static final String DELETE_ORG_ASSET = "DELETE FROM orgAssets WHERE orgUnit=? AND assetName=?";
    private static final String UPDATE_ASSET_QUANTITY = "UPDATE orgAssets SET assetQty=? WHERE orgUnit=? AND assetName=?";
    private static final String COUNT_ORG_ASSETS = "SELECT COUNT(*) FROM orgAssets";
    private static final String COUNT_ORGS_ASSETS = "SELECT COUNT(*) FROM orgAssets WHERE orgUnit=?";

    // prepared statements for orders table
    private static final String INSERT_ORDER = "INSERT INTO orders (orderID, orderType, orgUnit, assetName, assetQty, price, date) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_ORDERS = "SELECT * FROM orders WHERE orderType=? ORDER BY date";
    private static final String GET_ALL_ORDERS = "SELECT * FROM orders ORDER BY date";
    private static final String GET_ORDER = "SELECT * FROM orders WHERE orderID=?";
    private static final String GET_ASSET_ORDERS = "SELECT * FROM orders WHERE assetName=? AND orderType=? ORDER BY date";
    private static final String DELETE_ORDER = "DELETE FROM orders WHERE orderID=?";
    private static final String UPDATE_ORDER = "UPDATE orders SET assetQty=? WHERE orderID=?";
    private static final String COUNT_ORDERS = "SELECT COUNT(*) FROM orders where orderType=?";

    // prepared statements for orderHistory table
    private static final String INSERT_OLD_ORDER = "INSERT INTO orderHistory (orderID, orderType, orgUnit, assetName, assetQty, price, date) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_OLD_ORDERS = "SELECT * FROM orderHistory WHERE assetName=? ORDER BY date DESC";
    private static final String GET_OLD_ORDER = "SELECT * FROM orderHistory WHERE orderID=?";
    private static final String GET_ALL_OLD_ORDERS = "SELECT * FROM orderHistory ORDER BY date DESC";

    private Connection connection;

    private PreparedStatement addUser;
    private PreparedStatement getUserList;
    private PreparedStatement getUser;
    private PreparedStatement deleteUser;
    private PreparedStatement updateUser;
    private PreparedStatement userCount;

    private PreparedStatement addOrg;
    private PreparedStatement getOrgList;
    private PreparedStatement getOrg;
    private PreparedStatement deleteOrg;
    private PreparedStatement updateCredits;
    private PreparedStatement orgCount;

    private PreparedStatement addAsset;
    private PreparedStatement getAssetList;
    private PreparedStatement getAsset;
    private PreparedStatement deleteAsset;
    private PreparedStatement assetCount;

    private PreparedStatement addOrgAsset;
    private PreparedStatement getOrgAssetList;
    private PreparedStatement getOrgAsset;
    private PreparedStatement checkOrgAsset;
    private PreparedStatement deleteOrgAsset;
    private PreparedStatement updateAssetQuantity;
    private PreparedStatement orgAssetCount;
    private PreparedStatement orgsAssetCount;

    private PreparedStatement addOrder;
    private PreparedStatement getOrderList;
    private PreparedStatement getAllOrderList;
    private PreparedStatement getOrder;
    private PreparedStatement getOrderAssets;
    private PreparedStatement deleteOrder;
    private PreparedStatement updateOrder;
    private PreparedStatement orderCount;

    private PreparedStatement addOldOrder;
    private PreparedStatement getOldOrderList;
    private PreparedStatement getOldOrder;
    private PreparedStatement getAllOldOrdersList;

    /**
     * Initializes database connection, executes
     * initial create queries for database and
     * prepares database statements.
     */
    public JDBCMarketplaceDataSource() {
        connection = DBConnection.getInstance();
        try {
            // setup database connection + create tables
            Statement st = connection.createStatement();
            st.execute(CREATE_ORG_TABLE);
            st.execute(CREATE_ASSET_TABLE);
            st.execute(CREATE_USER_TABLE);
            st.execute(CREATE_ORG_ASSET_TABLE);
            st.execute(CREATE_TRADE_TABLE);
            st.execute(CREATE_TRADE_HISTORY_TABLE);
            st.execute(CREATE_ADMIN_ORG);
            st.execute(CREATE_ADMIN);

            // assign user statements
            addUser = connection.prepareStatement(INSERT_USER);
            getUserList = connection.prepareStatement(GET_USERS);
            getUser = connection.prepareStatement(GET_USER);
            deleteUser = connection.prepareStatement(DELETE_USER);
            updateUser = connection.prepareStatement(UPDATE_USER);
            userCount = connection.prepareStatement(COUNT_USERS);

            // assign org statements
            addOrg = connection.prepareStatement(INSERT_ORG);
            getOrgList = connection.prepareStatement(GET_ORGS);
            getOrg = connection.prepareStatement(GET_ORG);
            deleteOrg = connection.prepareStatement(DELETE_ORG);
            updateCredits = connection.prepareStatement(UPDATE_CREDITS);
            orgCount = connection.prepareStatement(COUNT_ORGS);

            // assign asset statements
            addAsset = connection.prepareStatement(INSERT_ASSET);
            getAssetList = connection.prepareStatement(GET_ASSETS);
            getAsset = connection.prepareStatement(GET_ASSET);
            deleteAsset = connection.prepareStatement(DELETE_ASSET);
            assetCount = connection.prepareStatement(COUNT_ASSETS);

            // assign orgAsset statements
            addOrgAsset = connection.prepareStatement(INSERT_ORG_ASSET);
            getOrgAssetList = connection.prepareStatement(GET_ORG_ASSETS);
            getOrgAsset = connection.prepareStatement(GET_ORG_ASSET);
            checkOrgAsset = connection.prepareStatement(CHECK_ORG_ASSET);
            deleteOrgAsset = connection.prepareStatement(DELETE_ORG_ASSET);
            updateAssetQuantity = connection.prepareStatement(UPDATE_ASSET_QUANTITY);
            orgAssetCount = connection.prepareStatement(COUNT_ORG_ASSETS);
            orgsAssetCount = connection.prepareStatement(COUNT_ORGS_ASSETS);

            addOrder= connection.prepareStatement(INSERT_ORDER);
            getOrderList = connection.prepareStatement(GET_ORDERS);
            getAllOrderList = connection.prepareStatement(GET_ALL_ORDERS);
            getOrder = connection.prepareStatement(GET_ORDER);
            getOrderAssets = connection.prepareStatement(GET_ASSET_ORDERS);
            deleteOrder = connection.prepareStatement(DELETE_ORDER);
            updateOrder = connection.prepareStatement(UPDATE_ORDER);
            orderCount = connection.prepareStatement(COUNT_ORDERS);

            addOldOrder= connection.prepareStatement(INSERT_OLD_ORDER);
            getOldOrderList = connection.prepareStatement(GET_OLD_ORDERS);
            getOldOrder = connection.prepareStatement(GET_OLD_ORDER);
            getAllOldOrdersList = connection.prepareStatement(GET_ALL_OLD_ORDERS);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////// USER FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addUser(User)
     */
    public void addUser(User u) {
        try {
            addUser.setString(1, u.getUser());
            addUser.setString(2, u.getPassword());
            addUser.setString(3, u.getAccType());
            addUser.setString(4, u.getOrgUnit());
            addUser.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#userSet()
     */
    public Set<String> userSet() {
        Set<String> users = new TreeSet<String>();
        ResultSet rs = null;

        try {
            rs = getUserList.executeQuery();
            while (rs.next()) {
                users.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }

    /**
     * @see MarketplaceDataSource#getUser(String)
     */
    public User getUser(String user) {
        User u = new User();
        ResultSet rs = null;
        try {
            getUser.setString(1, user);
            rs = getUser.executeQuery();
            if (rs.next()) {
                u.setUser(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setAccType(rs.getString("accType"));
                u.setOrgUnit(rs.getString("orgUnit"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return u;
    }

    /**
     * @see MarketplaceDataSource#getUsersSize()
     */
    public int getUsersSize() {
        ResultSet rs = null;
        int rows = 0;

        try {
            rs = userCount.executeQuery();
            rs.next();
            rows = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * @see MarketplaceDataSource#deleteUser(String)
     */
    public void deleteUser(String user) {
        try {
            deleteUser.setString(1, user);
            deleteUser.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateUser(String, String, String, String, String)
     */
    public void updateUser(String newUser, String pwd, String accType, String org, String oldUser) {
        try {
            updateUser.setString(1, newUser);
            updateUser.setString(2, pwd);
            updateUser.setString(3, accType);
            updateUser.setString(4, org);
            updateUser.setString(5, oldUser);
            updateUser.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////// ORGANISATION FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addOrg(Org)
     */
    public void addOrg(Org org) {
        String orgName = org.getOrgUnit();
        try {
            if (getOrg(orgName).getOrgUnit() == null) {
                addOrg.setString(1, org.getOrgUnit());
                addOrg.setDouble(2, org.getCredits());
                addOrg.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#orgSet()
     */
    public Set<String> orgSet() {
        Set<String> orgs = new TreeSet<String>();
        ResultSet rs = null;

        try {
            rs = getOrgList.executeQuery();
            while (rs.next()) {
                orgs.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return orgs;
    }

    /**
     * @see MarketplaceDataSource#getOrg(String) 
     */
    public Org getOrg(String org) {
        Org o = new Org();
        ResultSet rs = null;
        try {
            getOrg.setString(1, org);
            rs = getOrg.executeQuery();
            if (rs.next()) {
                o.setOrgUnit(rs.getString("orgUnit"));
                o.setCredits(rs.getDouble("credits"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return o;
    }

    /**
     * @see MarketplaceDataSource#getOrgSize() 
     */
    public int getOrgSize() {
        ResultSet rs = null;
        int rows = 0;

        try {
            rs = orgCount.executeQuery();
            rs.next();
            rows = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rows;
    }

    /**
     * @see MarketplaceDataSource#deleteOrg(String)
     */
    public void deleteOrg(String org) {
        try {
            deleteOrg.setString(1, org);
            deleteOrg.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ignored) {
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateCredits(String, String)
     */
    public void updateCredits(String org, String credits) {
        try {
            updateCredits.setString(1, credits);
            updateCredits.setString(2, org);
            updateCredits.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////// ASSET FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addAsset(Asset)
     */
    public void addAsset(Asset a) {
        String assetName = a.getAssetName();
        try {
            if (getAsset(assetName).getAssetName() == null) {
                addAsset.setString(1, assetName);
                addAsset.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#assetSet()
     */
    public Set<String> assetSet() {
        Set<String> assets = new TreeSet<String>();
        ResultSet rs = null;

        try {
            rs = getAssetList.executeQuery();
            while (rs.next()) {
                assets.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return assets;
    }

    /**
     * @see MarketplaceDataSource#getAsset(String)
     */
    public Asset getAsset(String asset) {
        Asset a = new Asset();
        ResultSet rs = null;
        try {
            getAsset.setString(1, asset);
            rs = getAsset.executeQuery();
            if (rs.next()) {
                a.setAssetName(rs.getString("assetName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return a;
    }

    /**
     * @see MarketplaceDataSource#getAssetSize()
     */
    public int getAssetSize() {
        ResultSet rs = null;
        int rows = 0;

        try {
            rs = assetCount.executeQuery();
            if (rs.next()) {
                rows = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rows;
    }

    /**
     * @see MarketplaceDataSource#deleteAsset(String) 
     */
    public void deleteAsset(String asset) {
        try {
            deleteAsset.setString(1, asset);
            deleteAsset.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////// ORGANISATIONS ASSET FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addOrgAsset(OrgAsset)
     */
    public void addOrgAsset(OrgAsset a) {
        try {
            addOrgAsset.setString(1, a.getOrgUnit());
            addOrgAsset.setString(2, a.getAssetName());
            addOrgAsset.setDouble(3, a.getAssetQty());
            addOrgAsset.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOrgAsset(String, String)
     */
    public OrgAsset getOrgAsset(String org, String asset) {
        OrgAsset oA = new OrgAsset();
        ResultSet rs = null;
        try {
            getOrgAsset.setString(1, org);
            getOrgAsset.setString(2, asset);
            rs = getOrgAsset.executeQuery();
            if (rs.next()) {
                oA.setOrgUnit(rs.getString("orgUnit"));
                oA.setAssetName(rs.getString("assetName"));
                oA.setAssetQty(rs.getDouble("assetQty"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return oA;
    }

    /**
     * @see MarketplaceDataSource#orgAssetSet()
     */
    public Set<String> orgAssetSet() {
        Set<String> orgs = new HashSet<>();
        ResultSet rs = null;

        try {
            rs = getOrgAssetList.executeQuery();
            while (rs.next()) {
                String orgAsset = new String();
                orgAsset += rs.getString(1);
                orgAsset += ",,";
                orgAsset += rs.getString(2);
                orgs.add(orgAsset);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return orgs;
    }

    /**
     * @see MarketplaceDataSource#getOrgAssetSize()
     */
    public int getOrgAssetSize() {
        ResultSet rs = null;
        int rows = 0;
        try {
            rs = orgAssetCount.executeQuery();
            rs.next();
            rows = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * @see MarketplaceDataSource#getCurrentOrgAssetSize(String)
     */
    public int getCurrentOrgAssetSize(String org) {
        ResultSet rs = null;
        int rows = 0;
        try {
            orgsAssetCount.setString(1, org);
            rs = orgsAssetCount.executeQuery();
            rs.next();
            rows = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * @see MarketplaceDataSource#checkOrgAsset(String)
     */
    public boolean checkOrgAsset(String asset) {
        ResultSet rs = null;
        try {
            checkOrgAsset.setString(1, asset);
            rs = checkOrgAsset.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * @see MarketplaceDataSource#deleteOrgAsset(String, String)
     */
    public void deleteOrgAsset(String org, String asset) {
        try {
            deleteOrgAsset.setString(1, org);
            deleteOrgAsset.setString(2, asset);
            deleteOrgAsset.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateAssetQuantity(String, String, String)
     */
    public void updateAssetQuantity(String qty, String org, String asset) {
        try {
            updateAssetQuantity.setString(1, qty);
            updateAssetQuantity.setString(2, org);
            updateAssetQuantity.setString(3, asset);
            updateAssetQuantity.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////// ORDER FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addOrder(Order) 
     */
    @Override
    public void addOrder(Order order) {
        try {
            addOrder.setString(1, order.getOrderID());
            addOrder.setString(2, order.getOrderType());
            addOrder.setString(3, order.getOrgUnit());
            addOrder.setString(4, order.getAssetName());
            addOrder.setString(5, order.getAssetQty().toString());
            addOrder.setString(6, order.getPrice().toString());
            addOrder.setString(7, order.getDate());
            addOrder.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOrder(String) 
     */
    @Override
    public Order getOrder(String orderID) {
        Order o = new Order();
        ResultSet rs = null;
        try {
            getOrder.setString(1, orderID);
            rs = getOrder.executeQuery();
            if (rs.next()) {
                o.setOrderID(rs.getString("orderID"));
                o.setOrderType(rs.getString("orderType"));
                o.setOrgUnit(rs.getString("orgUnit"));
                o.setAssetName(rs.getString("assetName"));
                o.setAssetQty(Double.valueOf(rs.getString("assetQty")));
                o.setPrice(Double.valueOf(rs.getString("price")));
                o.setDate(rs.getString("date"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return o;
    }

    /**
     * @see MarketplaceDataSource#getOrderSize(String)
     */
    @Override
    public int getOrderSize(String orderType) {
        ResultSet rs = null;
        int rows = 0;
        try {
            orderCount.setString(1, orderType);
            rs = orderCount.executeQuery();
            rs.next();
            rows = rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * @see MarketplaceDataSource#deleterOrder(String) 
     */
    @Override
    public void deleterOrder(String orderID) {
        try {
            deleteOrder.setString(1, orderID);
            deleteOrder.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateOrder(String, String) 
     */
    @Override
    public void updateOrder(String assetQty, String orderID) {
        try {
            updateOrder.setString(1, assetQty);
            updateOrder.setString(2, orderID);
            updateOrder.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> orderSet(String orderType) {
        Set<String> orders = new LinkedHashSet<>();
        ResultSet rs = null;

        try {
            getOrderList.setString(1, orderType);
            rs = getOrderList.executeQuery();
            while (rs.next()) {
                orders.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    /**
     * @see MarketplaceDataSource#orderAssetSet(String, String)
     */
    @Override
    public Set<String> orderAssetSet(String asset, String orderType) {
        Set<String> assetOrders = new LinkedHashSet<>();
        ResultSet rs = null;

        try {
            getOrderAssets.setString(1, asset);
            getOrderAssets.setString(2, orderType);
            rs = getOrderAssets.executeQuery();
            while (rs.next()) {
                assetOrders.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return assetOrders;
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> allOrderSet() {
        Set<String> orders = new LinkedHashSet<>();
        ResultSet rs = null;
        try {
            rs = getAllOrderList.executeQuery();
            while (rs.next()) {
                orders.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    ////////////////////// ORDER HISTORY FUNCTIONALITY ///////////////////

    /**
     * @see MarketplaceDataSource#addOrder(Order)
     */
    @Override
    public void addOldOrder(Order order) {
        try {
            addOldOrder.setString(1, order.getOrderID());
            addOldOrder.setString(2, order.getOrderType());
            addOldOrder.setString(3, order.getOrgUnit());
            addOldOrder.setString(4, order.getAssetName());
            addOldOrder.setString(5, order.getAssetQty().toString());
            addOldOrder.setString(6, order.getPrice().toString());
            addOldOrder.setString(7, order.getDate());
            addOldOrder.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOldOrder(String) 
     */
    @Override
    public Order getOldOrder(String orderID) {
        Order o = new Order();
        ResultSet rs = null;
        try {
            getOldOrder.setString(1, orderID);
            rs = getOldOrder.executeQuery();
            if (rs.next()) {
                o.setOrderID(rs.getString("orderID"));
                o.setOrderType(rs.getString("orderType"));
                o.setOrgUnit(rs.getString("orgUnit"));
                o.setAssetName(rs.getString("assetName"));
                o.setAssetQty(Double.valueOf(rs.getString("assetQty")));
                o.setPrice(Double.valueOf(rs.getString("price")));
                o.setDate(rs.getString("date"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return o;
    }

    /**
     * @see MarketplaceDataSource#oldOrderSet(String)
     */
    @Override
    public Set<String> oldOrderSet(String assetName) {
        Set<String> orders = new LinkedHashSet<>();
        ResultSet rs = null;

        try {
            getOldOrderList.setString(1, assetName);
            rs = getOldOrderList.executeQuery();
            while (rs.next()) {
                orders.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> allOldOrderSet() {
        Set<String> orders = new LinkedHashSet<>();
        ResultSet rs = null;
        try {
            rs = getAllOldOrdersList.executeQuery();
            while (rs.next()) {
                orders.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    /**
     * @see MarketplaceDataSource#close()
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
