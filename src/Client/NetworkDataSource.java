package Client;

import Common.Command;
import Common.Constructors.*;
import Common.MarketplaceDataSource;


import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Implements marketplace data methods for Network Source.
 * Processes input and output streams to write data to a
 * database across the server for persistent storage.
 */
public class NetworkDataSource implements MarketplaceDataSource {

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    /**
     * Opens a new persistent socket for a client
     */
    public NetworkDataSource() {
        try {
            Properties props = new Properties();
            FileInputStream in = null;
            in = new FileInputStream("src/Client/serverConfig.props");
            props.load(in);
            in.close();
            // specify the data source, username and password
            String HOSTNAME = props.getProperty("serverConfig.HOST");
            String PORT = props.getProperty("serverConfig.PORT");

            // Persist a single connection through the whole lifetime of the application.
            // We will re-use this same connection/socket, rather than repeatedly opening
            // and closing connections.
            socket = new Socket(HOSTNAME, Integer.parseInt(PORT));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
            System.exit(1);
        }
    }

    /**
     * @see MarketplaceDataSource#addUser(User)
     */
    public void addUser(User u) {
        if (u == null)
            throw new IllegalArgumentException("User cannot be null");

        try {
            objectOutputStream.writeObject(Command.ADD_USER);
            objectOutputStream.writeObject(u);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getUser(String)
     */
    public User getUser(String u) {
        try {

            objectOutputStream.writeObject(Command.GET_USER);
            objectOutputStream.writeObject(u);
            objectOutputStream.flush();
            return (User) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#getUsersSize()
     */
    public int getUsersSize() {
        try {
            objectOutputStream.writeObject(Command.GET_USER_SIZE);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#deleteUser(String)
     */
    public void deleteUser(String u) {
        try {
            objectOutputStream.writeObject(Command.DELETE_USER);
            objectOutputStream.writeObject(u);
            objectOutputStream.flush();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateUser(String, String, String, String, String)
     */
    public void updateUser(String newUser, String pwd, String accType, String org, String oldUser) {
        try {
            String updatedUser = newUser + ",," + pwd + ",," + accType + ",," + org + ",," + oldUser;
            objectOutputStream.writeObject(Command.UPDATE_USER);
            objectOutputStream.writeObject(updatedUser);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#userSet()
     */
    public Set<String> userSet() {
        try {
            objectOutputStream.writeObject(Command.GET_USERS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#addOrg(Org)
     */
    public void addOrg(Org o) {
        if (o == null)
            throw new IllegalArgumentException("Organisation cannot be null");
        try {
            objectOutputStream.writeObject(Command.ADD_ORG);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOrg(String)
     */
    public Org getOrg(String o) {
        try {
            objectOutputStream.writeObject(Command.GET_ORG);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            return (Org) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#getOrgSize()
     */
    public int getOrgSize() {
        try {
            objectOutputStream.writeObject(Command.GET_ORG_SIZE);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#deleteOrg(String)
     */
    public void deleteOrg(String o) {
        try {
            objectOutputStream.writeObject(Command.DELETE_ORG);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateCredits(String, String)
     */
    public void updateCredits(String org, String credits) {
        try {
            String updatedCredits = org + ",," + credits;
            objectOutputStream.writeObject(Command.UPDATE_CREDITS);
            objectOutputStream.writeObject(updatedCredits);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#orgSet()
     */
    public Set<String> orgSet() {
        try {
            objectOutputStream.writeObject(Command.GET_ORGS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#addAsset(Asset)
     */
    public void addAsset(Asset a) {
        if (a == null)
            throw new IllegalArgumentException("Asset cannot be null");
        try {
            objectOutputStream.writeObject(Command.ADD_ASSET);
            objectOutputStream.writeObject(a);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getAsset(String)
     */
    public Asset getAsset(String asset) {
        try {
            objectOutputStream.writeObject(Command.GET_ASSET);
            objectOutputStream.writeObject(asset);
            objectOutputStream.flush();
            return (Asset) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#getAssetSize()
     */
    public int getAssetSize() {
        try {
            objectOutputStream.writeObject(Command.GET_ASSET_SIZE);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#deleteAsset(String)
     */
    public void deleteAsset(String asset) {
        try {
            objectOutputStream.writeObject(Command.DELETE_ASSET);
            objectOutputStream.writeObject(asset);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#assetSet()
     */
    public Set<String> assetSet() {
        try {
            objectOutputStream.writeObject(Command.GET_ASSETS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#addOrgAsset(OrgAsset)
     */
    public void addOrgAsset(OrgAsset oa) {
        if (oa == null)
            throw new IllegalArgumentException("Organisation-Asset cannot be null");
        try {
            objectOutputStream.writeObject(Command.ADD_ORG_ASSET);
            objectOutputStream.writeObject(oa);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOrgAssetSize()
     */
    public int getOrgAssetSize() {
        try {
            objectOutputStream.writeObject(Command.GET_ORG_ASSET_SIZE);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#getCurrentOrgAssetSize(String)
     */
    public int getCurrentOrgAssetSize(String org) {
        try {
            objectOutputStream.writeObject(Command.COUNT_ORGS_ASSETS);
            objectOutputStream.writeObject(org);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#getOrgAsset(String, String)
     */
    public OrgAsset getOrgAsset(String org, String asset) {
        try {
            String orgAsset = org + ",," + asset;
            objectOutputStream.writeObject(Command.GET_ORG_ASSET);
            objectOutputStream.writeObject(orgAsset);
            objectOutputStream.flush();
            return (OrgAsset) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#checkOrgAsset(String)
     */
    public boolean checkOrgAsset(String asset) {
        try {
            objectOutputStream.writeObject(Command.CHECK_ORG_ASSET);
            objectOutputStream.writeObject(asset);
            objectOutputStream.flush();
            return objectInputStream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @see MarketplaceDataSource#addUser(User)
     */
    public void deleteOrgAsset(String org, String asset) {
        try {
            String orgAsset = org + ",," + asset;
            objectOutputStream.writeObject(Command.DELETE_ORG_ASSET);
            objectOutputStream.writeObject(orgAsset);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateAssetQuantity(String, String, String)
     */
    public void updateAssetQuantity(String qty, String org, String asset) {
        try {
            String updatedQty = qty + ",," + org + ",," + asset;
            objectOutputStream.writeObject(Command.UPDATE_ORG_ASSET_QUANTITY);
            objectOutputStream.writeObject(updatedQty);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#orgAssetSet()
     */
    public Set<String> orgAssetSet() {
        try {
            objectOutputStream.writeObject(Command.GET_ORG_ASSETS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#addOrder(Order)
     */
    @Override
    public void addOrder(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        try {
            objectOutputStream.writeObject(Command.ADD_ORDER);
            objectOutputStream.writeObject(order);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOrder(String)
     */
    @Override
    public Order getOrder(String orderID) {
        try {

            objectOutputStream.writeObject(Command.GET_ORDER);
            objectOutputStream.writeObject(orderID);
            objectOutputStream.flush();
            return (Order) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#getOrderSize(String)
     */
    @Override
    public int getOrderSize(String orderType) {
        try {
            objectOutputStream.writeObject(Command.GET_ORDER_SIZE);
            objectOutputStream.writeObject(orderType);
            objectOutputStream.flush();
            return objectInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @see MarketplaceDataSource#deleterOrder(String)
     */
    @Override
    public void deleterOrder(String orderID) {
        try {
            objectOutputStream.writeObject(Command.DELETE_ORDER);
            objectOutputStream.writeObject(orderID);
            objectOutputStream.flush();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#updateOrder(String, String)
     */
    @Override
    public void updateOrder(String assetQty, String orderID) {
        try {
            String orderDetails = assetQty + ",," + orderID;
            objectOutputStream.writeObject(Command.UPDATE_ORDER);
            objectOutputStream.writeObject(orderDetails);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> orderSet(String orderType) {
        try {
            objectOutputStream.writeObject(Command.GET_ORDERS);
            objectOutputStream.writeObject(orderType);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#orderAssetSet(String, String)
     */
    @Override
    public Set<String> orderAssetSet(String asset, String orderType) {
        try {
            String orderDetails = asset + ",," + orderType;
            objectOutputStream.writeObject(Command.GET_ASSET_ORDERS);
            objectOutputStream.writeObject(orderDetails);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#allOrderSet()
     */
    @Override
    public Set<String> allOrderSet() {
        try {
            objectOutputStream.writeObject(Command.GET_ALL_ORDERS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#addOldOrder(Order)
     */
    @Override
    public void addOldOrder(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        try {
            objectOutputStream.writeObject(Command.ADD_OLD_ORDER);
            objectOutputStream.writeObject(order);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see MarketplaceDataSource#getOldOrder(String)
     */
    @Override
    public Order getOldOrder(String orderID) {
        try {
            objectOutputStream.writeObject(Command.GET_OLD_ORDER);
            objectOutputStream.writeObject(orderID);
            objectOutputStream.flush();
            return (Order) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see MarketplaceDataSource#orderSet(String)
     */
    @Override
    public Set<String> oldOrderSet(String assetName) {
        try {
            objectOutputStream.writeObject(Command.GET_OLD_ASSET_ORDERS);
            objectOutputStream.writeObject(assetName);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#allOldOrderSet()
     */
    @Override
    public Set<String> allOldOrderSet() {
        try {
            objectOutputStream.writeObject(Command.GET_OLD_ORDERS);
            objectOutputStream.flush();
            return (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /**
     * @see MarketplaceDataSource#close()
     */
    public void close() {
        try {
            if (objectInputStream !=null) objectInputStream.close();
            if (objectOutputStream !=null) objectOutputStream.close();
            if (socket != null) socket.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}