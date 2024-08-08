package Server;

import Common.Command;
import Common.Constructors.*;
import Common.MarketplaceDataSource;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Initialises new Server socket and connects server
 * to database. Handles all network commands for parsing
 * commands to database.
 */
public class NetworkServer {
    private static final int PORT = 10000;
    private static final int SOCKET_ACCEPT_TIMEOUT = 100;
    private static final int SOCKET_READ_TIMEOUT = 5000;
    private AtomicBoolean running = new AtomicBoolean(true);
    private MarketplaceDataSource database;

    /**
     * Handles the connection received from ServerSocket
     * @param socket The socket used to communicate with the currently connected client
     */
    private void handleConnection(Socket socket) {
        try {

            final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    final Command command = (Command) ois.readObject();
                    handleCommand(socket, ois, oos, command);
                } catch (SocketTimeoutException e){
                    continue;
                }
            }
        } catch (IOException | ClassCastException | ClassNotFoundException e) {
            System.out.println(String.format("Connection %s closed", socket.toString()));
        }
    }

    /**
     * Handles a request from the client.
     * @param socket socket for the client
     * @param inputStream input stream to read objects from
     * @param outputStream output stream to write objects to
     * @param command command we're handling
     * @throws IOException if the client has disconnected
     * @throws ClassNotFoundException if the client sends an invalid object
     */
    private void handleCommand(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                               Command command) throws IOException, ClassNotFoundException {
        /**
         * Remember this is happening on separate threads for each client. Therefore access to the database
         * must be thread-safe in some way. The easiest way to achieve thread safety is to just put a giant
         * lock around all database operations, in this case with a synchronized block on the database object.
         */
        switch (command) {
            case ADD_USER: {
                // client is sending us a new user
                final User u = (User) inputStream.readObject();
                synchronized (database) {
                    database.addUser(u);
                }
            }
            break;

            case GET_USER: {
                // client sends us the name of the user to retrieve
                final String user = (String) inputStream.readObject();
                synchronized (database) {
                    // synchronize both the get as well as the send, that way
                    // we don't send a half updated user
                    final User u = database.getUser(user);
                    // send the client back the user's details, or null
                    outputStream.writeObject(u);
                }
                outputStream.flush();
            }
            break;

            case GET_USER_SIZE: {
                // send the client back the size of the users in the database
                synchronized (database) {
                    outputStream.writeInt(database.getUsersSize());
                }
                outputStream.flush();
            }
            break;

            case DELETE_USER: {
                // one parameter - the user's name
                final String user = (String) inputStream.readObject();
                synchronized (database) {
                    database.deleteUser(user);
                }
            }
            break;

            case GET_USERS: {
                // send the client back the users set
                synchronized (database) {
                    outputStream.writeObject(database.userSet());
                }
                outputStream.flush();
            }
            break;

            case UPDATE_USER: {
                // client is sending us details for an updated user
                String updatedDetails = (String) inputStream.readObject();
                String newUser = updatedDetails.split(",,")[0];
                String newPwd = updatedDetails.split(",,")[1];
                String newAccType = updatedDetails.split(",,")[2];
                String newOrg = updatedDetails.split(",,")[3];
                String oldUser = updatedDetails.split(",,")[4];
                synchronized (database) {
                    database.updateUser(newUser, newPwd, newAccType, newOrg, oldUser);
                }
            }
            break;

            case ADD_ORG: {
                // client is sending us a new org
                final Org o = (Org) inputStream.readObject();
                synchronized (database) {
                    database.addOrg(o);
                }
            }
            break;

            case GET_ORG: {
                // client sends us the name of the org to retrieve
                final String org = (String) inputStream.readObject();
                synchronized (database) {
                    final Org o = database.getOrg(org);
                    outputStream.writeObject(o);
                }
                outputStream.flush();
            }
            break;

            case GET_ORG_SIZE: {
                // send the client back the size of the organisations in the database
                synchronized (database) {
                    outputStream.writeInt(database.getOrgSize());
                }
                outputStream.flush();
            }
            break;

            case DELETE_ORG: {
                // one parameter - the org's name
                final String org = (String) inputStream.readObject();
                synchronized (database) {
                    database.deleteOrg(org);
                }
            }
            break;

            case GET_ORGS: {
                // send the client back the org set
                synchronized (database) {
                    outputStream.writeObject(database.orgSet());
                }
                outputStream.flush();
            }
            break;

            case UPDATE_CREDITS: {
                // client is sending us updated credits for an organisation
                String updatedDetails = (String) inputStream.readObject();
                String org = updatedDetails.split(",,")[0];
                String credits = updatedDetails.split(",,")[1];
                synchronized (database) {
                    database.updateCredits(org, credits);
                }
            }
            break;

            case GET_ASSET_SIZE: {
                // send the client back the size of the assets in the database
                synchronized (database) {
                    outputStream.writeInt(database.getAssetSize());
                }
                outputStream.flush();
            }
            break;

            case ADD_ASSET: {
                // client is sending us a new asset
                final Asset a = (Asset) inputStream.readObject();
                synchronized (database) {
                    database.addAsset(a);
                }
            }
            break;

            case GET_ASSET: {
                // client sends us the name of the asset to retrieve
                final String asset = (String) inputStream.readObject();
                synchronized (database) {
                    final Asset a = database.getAsset(asset);
                    outputStream.writeObject(a);
                }
                outputStream.flush();
            }
            break;

            case DELETE_ASSET: {
                // one parameter - the asset's name
                final String asset = (String) inputStream.readObject();
                synchronized (database) {
                    database.deleteAsset(asset);
                }
            }
            break;

            case GET_ASSETS: {
                // send the client back the asset set
                synchronized (database) {
                    outputStream.writeObject(database.assetSet());
                }
                outputStream.flush();
            }
            break;


            case ADD_ORG_ASSET: {
                // client is sending us a new org-asset
                final OrgAsset oA = (OrgAsset) inputStream.readObject();
                synchronized (database) {
                    database.addOrgAsset(oA);
                }
            }
            break;

            case GET_ORG_ASSET: {
                // client sends us the name of the org asset to retrieve
                final String orgAsset = (String) inputStream.readObject();
                String org = orgAsset.split(",,")[0];
                String asset = orgAsset.split(",,")[1];
                synchronized (database) {
                    final OrgAsset oA = database.getOrgAsset(org, asset);
                    outputStream.writeObject(oA);
                }
                outputStream.flush();
            }
            break;

            case GET_ORG_ASSET_SIZE: {
                // send the client back the size of the org assets in the database
                synchronized (database) {
                    outputStream.writeInt(database.getOrgAssetSize());
                }
                outputStream.flush();
            }
            break;

            case COUNT_ORGS_ASSETS: {
                final String org = (String) inputStream.readObject();
                // send the client back the size of the org assets in the database
                synchronized (database) {
                    outputStream.writeInt(database.getCurrentOrgAssetSize(org));
                }
                outputStream.flush();
            }
            break;

            case DELETE_ORG_ASSET: {
                // two parameters - the org's name and asset
                final String orgAsset = (String) inputStream.readObject();
                String org = orgAsset.split(",,")[0];
                String asset = orgAsset.split(",,")[1];
                synchronized (database) {
                    database.deleteOrgAsset(org, asset);
                }
            }
            break;

            case GET_ORG_ASSETS: {
                // send the client back the org-asset set
                synchronized (database) {
                    outputStream.writeObject(database.orgAssetSet());
                }
                outputStream.flush();
            }
            break;

            case UPDATE_ORG_ASSET_QUANTITY: {
                // updates the quantity of an org's asset
                String updatedDetails = (String) inputStream.readObject();
                String qty = updatedDetails.split(",,")[0];
                String org = updatedDetails.split(",,")[1];
                String asset = updatedDetails.split(",,")[2];
                synchronized (database) {
                    database.updateAssetQuantity(qty, org, asset);
                }
            }
            break;

            case CHECK_ORG_ASSET: {
                // checks if an asset is owned by an org
                String asset = (String) inputStream.readObject();
                synchronized (database) {
                    boolean result = database.checkOrgAsset(asset);
                    outputStream.writeBoolean(result);
                }
                outputStream.flush();
            }
            break;

            case ADD_ORDER: {
                // client is sending us a new order
                final Order o = (Order) inputStream.readObject();
                synchronized (database) {
                    database.addOrder(o);
                }
            }
            break;

            case GET_ORDER: {
                // client sends us the orderID of the order to retrieve
                final String orderID = (String) inputStream.readObject();
                synchronized (database) {
                    // synchronize both the get as well as the send, that way
                    // we don't send a half updated user
                    final Order o = database.getOrder(orderID);
                    // send the client back the order's details, or null
                    outputStream.writeObject(o);
                }
                outputStream.flush();
            }
            break;

            case GET_ORDER_SIZE: {
                // send the client back the size of the order in the database
                // for a given asset and order type
                String orderType = (String) inputStream.readObject();
                synchronized (database) {
                    outputStream.writeInt(database.getOrderSize(orderType));
                }
                outputStream.flush();
            }
            break;

            case DELETE_ORDER: {
                // one parameter - the orderID
                final String orderID = (String) inputStream.readObject();
                synchronized (database) {
                    database.deleterOrder(orderID);
                }
            }
            break;

            case GET_ORDERS: {
                // send the client back the order set for an order type
                String orderType = (String) inputStream.readObject();
                synchronized (database) {
                    outputStream.writeObject(database.orderSet(orderType));
                }
                outputStream.flush();
            }
            break;

            case GET_ASSET_ORDERS: {
                // send the client back the order set for an order type
                String orderDetails = (String) inputStream.readObject();
                String asset = orderDetails.split(",,")[0];
                String orderType = orderDetails.split(",,")[1];
                synchronized (database) {
                    outputStream.writeObject(database.orderAssetSet(asset, orderType));
                }
                outputStream.flush();
            }
            break;

            case GET_ALL_ORDERS: {
                // send the client back the order set for all orders
                synchronized (database) {
                    outputStream.writeObject(database.allOrderSet());
                }
                outputStream.flush();
            }
            break;

            case UPDATE_ORDER: {
                // quantity of asset remaining for a given order is updated
                String updateDetails = (String) inputStream.readObject();
                String assetQty = updateDetails.split(",,")[0];
                String orderID = updateDetails.split(",,")[1];
                synchronized (database) {
                    database.updateOrder(assetQty, orderID);
                }
            }
            break;

            case ADD_OLD_ORDER: {
                // client is sending us a new old order
                final Order o = (Order) inputStream.readObject();
                synchronized (database) {
                    database.addOldOrder(o);
                }
            }
            break;

            case GET_OLD_ORDER: {
                // client sends us the orderID of the order to retrieve
                final String orderID = (String) inputStream.readObject();
                synchronized (database) {
                    // synchronize both the get as well as the send, that way
                    // we don't send a half updated user
                    final Order o = database.getOldOrder(orderID);
                    // send the client back the order's details, or null
                    outputStream.writeObject(o);
                }
                outputStream.flush();
            }
            break;

            case GET_OLD_ASSET_ORDERS: {
                // send the client back the order set for an order type
                String assetName = (String) inputStream.readObject();
                synchronized (database) {
                    outputStream.writeObject(database.oldOrderSet(assetName));
                }
                outputStream.flush();
            }
            break;

            case GET_OLD_ORDERS: {
                // send the client back the org-asset set
                synchronized (database) {
                    outputStream.writeObject(database.allOldOrderSet());
                }
                outputStream.flush();
            }
            break;
        }
    }

    /**
     * Returns the port the server is configured to use
     *
     * @return The port number
     */
    public static int getPort() {
        return PORT;
    }

    /**
     * Starts the server running on the default port
     * @throws IOException throws IO exception
     */
    public void start() throws IOException {
        // Connect to the database.
        database = new JDBCMarketplaceDataSource();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(SOCKET_ACCEPT_TIMEOUT);
            for (;;) {
                if (!running.get()) {
                    // The server is no longer running
                    break;
                }
                try {
                    final Socket socket = serverSocket.accept();
                    socket.setSoTimeout(SOCKET_READ_TIMEOUT);

                    // We have a new connection from a client. Use a runnable and thread to handle
                    // the client. The lambda here wraps the functional interface (Runnable).
                    final Thread clientThread = new Thread(() -> handleConnection(socket));
                    clientThread.start();
                } catch (SocketTimeoutException ignored) {
                    // Do nothing. A timeout is normal- we just want the socket to
                    // occasionally timeout so we can check if the server is still running
                } catch (Exception e) {
                    // We will report other exceptions by printing the stack trace, but we
                    // will not shut down the server. A exception can happen due to a
                    // client malfunction (or malicious client)
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // If we get an error starting up, show an error dialog then exit
            e.printStackTrace();
            System.exit(1);
        }
        // Close down the server
        System.exit(0);
    }

    /**
     * Requests the server to shut down
     */
    public void shutdown() {
        // Shut the server down
        running.set(false);
    }

}