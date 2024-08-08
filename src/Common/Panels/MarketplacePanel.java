package Common.Panels;

import Common.Constructors.*;
import Common.MarketplaceData;
import Common.OrderHandler;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.abs;

/**
 * Initiates Marketplace Panel for the Marketplace Trading application's main view.
 * All listeners for marketplace page are included as inner classes of this class.
 */
public class MarketplacePanel {
    private DefaultTableModel assetTableModel;
    private DefaultTableModel buyOrdersModel;
    private DefaultTableModel sellOrdersModel;
    private DefaultTableModel orderHistoryModel;
    private DefaultTableModel orgOpenOrderModel;
    private DefaultTableModel orgOrderHistoryModel;
    private ListModel assetList;
    private ListModel buyOrdersList;
    private ListModel sellOrdersList;
    private ListModel orderHistoryList;
    private ListModel orgOpenOrdersList;
    private ListModel orgOrderHistoryList;
    private User user;
    private Org org;
    private OrgAsset oA;
    private String currentAsset;
    private String currentOrg;
    private double currentOrgCredits;
    private double orgAssetQty;
    private MarketplaceData data;
    private JLabel buyLabel0;
    private JLabel buyLabel1;
    private JLabel sellLabel0;
    private JLabel sellLabel1;
    private String buyPrice;
    private String buyAmount;
    private String sellPrice;
    private String sellAmount;
    private JTextField buyPriceText;
    private JTextField buyAmountText;
    private JTextField buyTotalText;
    private JTextField sellPriceText;
    private JTextField sellAmountText;
    private JTextField sellTotalText;
    private JButton buyButton;
    private JButton sellButton;
    private OrderHandler orderHandler;
    private JPanel marketplacePanel;
    private JTable buyOrdersTable;
    private JTable sellOrdersTable;
    private JTable orderHistoryTable;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ArrayList<Double> assetPriceHistory = new ArrayList<>();
    private ArrayList<LocalDateTime> assetDateHistory = new ArrayList<>();
    private java.util.List<Double> prices;
    private java.util.List<LocalDateTime> dates;
    private GraphPanel graph;

    /**
     * Constructs the marketplacePanel panel display for UI
     *
     * @param marketplacePanel reference to main panel display in main UI
     * @param data the marketplace data to retrieve from and send to
     * @param username the username of the user currently logged in
     */
    public MarketplacePanel(JPanel marketplacePanel, MarketplaceData data, String username) {
        this.data = data;
        this.marketplacePanel = marketplacePanel;
        user = data.getUser(username);
        currentOrg = user.getOrgUnit();
        org = data.getOrg(currentOrg);
        currentOrgCredits = org.getCredits();

        // Initialises a new order handler
        orderHandler = new OrderHandler();

        // Create table models for order data
        buyOrdersModel = new DefaultTableModel();
        buyOrdersModel.addColumn("Price");
        buyOrdersModel.addColumn("Amount");
        buyOrdersModel.addColumn("Total");

        sellOrdersModel = new DefaultTableModel();
        sellOrdersModel.addColumn("Price");
        sellOrdersModel.addColumn("Amount");
        sellOrdersModel.addColumn("Total");

        orderHistoryModel = new DefaultTableModel();
        orderHistoryModel.addColumn("Price");
        orderHistoryModel.addColumn("Amount");
        orderHistoryModel.addColumn("Type");
        orderHistoryModel.addColumn("Date");

        orgOpenOrderModel = new DefaultTableModel();
        orgOpenOrderModel.addColumn("Date");
        orgOpenOrderModel.addColumn("Asset");
        orgOpenOrderModel.addColumn("Type");
        orgOpenOrderModel.addColumn("Price");
        orgOpenOrderModel.addColumn("Amount");
        orgOpenOrderModel.addColumn("Total");
        orgOpenOrderModel.addColumn("");
        orgOpenOrderModel.addColumn("orderID");

        orgOrderHistoryModel = new DefaultTableModel();
        orgOrderHistoryModel.addColumn("Date");
        orgOrderHistoryModel.addColumn("Asset");
        orgOrderHistoryModel.addColumn("Type");
        orgOrderHistoryModel.addColumn("Price");
        orgOrderHistoryModel.addColumn("Amount");
        orgOrderHistoryModel.addColumn("Total");
        orgOrderHistoryModel.addColumn("orderID");

        getOrgOrdersModel();

        // main center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // creates the buy and sell panels and adds them to the center panel
        createBuyPanel(centerPanel);
        createSellPanel(centerPanel);

        // create table models for order data

        // adds button listeners to the buy and sell buttons
        addButtonListeners(new ButtonListener());

        marketplacePanel.setLayout(new BorderLayout());

        /**
         * Checks regularly if any changes have been made to the database
         * and updates view when required.
         */
        ActionListener checkUpdates = e -> {
            checkOrders();
            if (assetTableModel.getRowCount() != data.getAssetSize()) {
                getAssetModel();
            }
            org = data.getOrg(currentOrg);
            if (currentOrgCredits != org.getCredits()) {
                currentOrgCredits = org.getCredits();
                buyLabel1.setText("BALANCE " + currentOrgCredits + " CREDITS" );
            }
            if (orgAssetQty != data.getOrgAsset(currentOrg, currentAsset).getAssetQty()) {
                orgAssetQty = data.getOrgAsset(currentOrg, currentAsset).getAssetQty();
                sellLabel1.setText("BALANCE " + orgAssetQty + " " + currentAsset );
            }

            getBuyOrdersModel();
            getSellOrdersModel();
            getOrgOrdersModel();
            getOrgOrderHistoryModel();
            getAssetOrderHistoryModel();
        };
        new Timer(500, checkUpdates).start();

        // calls the east, west and south panels
        eastSidePanels();
        westSidePanels();
        southSidePanels();

        // Placeholder data for order history graph if an asset is not selected
        prices = Arrays.asList(0.0, 100.0);
        dates =  Arrays.asList(LocalDateTime.now());

        graph = new GraphPanel(prices, dates);
        JPanel graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(1270, 580));
        graphPanel.setLayout(new BorderLayout());
        graphPanel.add(graph);
        centerPanel.add(graphPanel, BorderLayout.NORTH);
        marketplacePanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createSellPanel(JPanel centerPanel) {
        // main sell panel to hold other panels
        JPanel sellPanel = new JPanel();
        sellPanel.setPreferredSize(new Dimension(635, 230));

        // panel for the Label above the sell menu
        JPanel sellP0 = new JPanel();
        sellP0.setLayout(new BorderLayout());
        sellP0.setPreferredSize(new Dimension(380, 30));
        sellLabel0 = new JLabel("SELL" );
        sellLabel1 = new JLabel("BALANCE" );
        sellP0.add(sellLabel0, BorderLayout.WEST);
        sellP0.add(sellLabel1, BorderLayout.EAST);

        // panel for the first sell text field
        JPanel sellP1 = new JPanel();
        sellP1.setLayout(new BorderLayout());
        sellP1.setPreferredSize(new Dimension(380, 30));
        JLabel sellPriceLabel = new JLabel("Price:" );
        sellPriceText = new JTextField();
        numbersOnlyTextField(sellPriceText);
        sellPriceText.setPreferredSize(new Dimension(335, 30));
        sellP1.add(sellPriceLabel, BorderLayout.WEST);
        sellP1.add(sellPriceText, BorderLayout.EAST);

        // panel for the second sell text field
        JPanel sellP2 = new JPanel();
        sellP2.setLayout(new BorderLayout());
        sellP2.setPreferredSize(new Dimension(380, 30));
        JLabel sellAmountLabel = new JLabel("Amount:" );
        sellAmountText = new JTextField();
        numbersOnlyTextField(sellAmountText);
        sellAmountText.setPreferredSize(new Dimension(335, 30));
        sellP2.add(sellAmountLabel, BorderLayout.WEST);
        sellP2.add(sellAmountText, BorderLayout.EAST);

        // sell slider
        JSlider sellSlider = new JSlider(0, 1000);
        sellSlider.setPreferredSize(new Dimension(380, 30));
        sellSlider.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e) {
            double sellPercentage = sellSlider.getValue();
            sellPrice = sellPriceText.getText();
            if (!sellPrice.isEmpty()) {
                double price = Double.parseDouble(sellPrice);
                double amount = (sellPercentage / 1000) * orgAssetQty;
                sellAmountText.setText(String.valueOf(amount));
                sellTotalText.setText(String.valueOf(price * amount));
            }
        }
        });

        // panel for the third sell text field
        JPanel sellP3 = new JPanel();
        sellP3.setLayout(new BorderLayout());
        sellP3.setPreferredSize(new Dimension(380, 30));
        JLabel sellTotalLabel = new JLabel("Total:" );
        sellTotalText = new JTextField();
        numbersOnlyTextField(sellTotalText);
        sellTotalText.setEditable(false);
        sellTotalText.setPreferredSize(new Dimension(335, 30));
        sellP3.add(sellTotalLabel, BorderLayout.WEST);
        sellP3.add(sellTotalText, BorderLayout.EAST);

        // handles the updates to buy text fields
        sellPriceText.getDocument().addDocumentListener(new textFieldListener());
        sellAmountText.getDocument().addDocumentListener(new textFieldListener());

        // sell button
        sellButton = new JButton("SELL");
        sellButton.setPreferredSize(new Dimension(380, 40));
        sellButton.setBackground(new Color(248,73,96));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFont(new Font("Arial", Font.PLAIN, 15));

        // adds all above widgets to the sell panel to be added to the center panel and pushed east
        sellPanel.add(sellP0);
        sellPanel.add(sellP1);
        sellPanel.add(sellP2);
        sellPanel.add(sellSlider);
        sellPanel.add(sellP3);
        sellPanel.add(sellButton);
        centerPanel.add(sellPanel, BorderLayout.EAST);
    }

    private void createBuyPanel(JPanel centerPanel) {
        // main buy panel to hold other panels
        JPanel buyPanel = new JPanel();
        buyPanel.setPreferredSize(new Dimension(635, 230));

        // panel for the Label above the buy menu
        JPanel buyP0 = new JPanel();
        buyP0.setLayout(new BorderLayout());
        buyP0.setPreferredSize(new Dimension(380, 30));
        buyLabel0 = new JLabel("BUY");
        buyLabel1 = new JLabel("BALANCE " + currentOrgCredits + " CREDITS" );
        buyP0.add(buyLabel0, BorderLayout.WEST);
        buyP0.add(buyLabel1, BorderLayout.EAST);

        // panel for the first buy text field
        JPanel buyP1 = new JPanel();
        buyP1.setLayout(new BorderLayout());
        buyP1.setPreferredSize(new Dimension(380, 30));
        JLabel buyPriceLabel = new JLabel("Price:" );
        buyPriceText = new JTextField();
        numbersOnlyTextField(buyPriceText);
        buyPriceText.setPreferredSize(new Dimension(335, 30));
        buyP1.add(buyPriceLabel, BorderLayout.WEST);
        buyP1.add(buyPriceText, BorderLayout.EAST);

        // panel for the second buy text field
        JPanel buyP2 = new JPanel();
        buyP2.setLayout(new BorderLayout());
        buyP2.setPreferredSize(new Dimension(380, 30));
        JLabel buyAmountLabel = new JLabel("Amount:" );
        buyAmountText = new JTextField();
        numbersOnlyTextField(buyAmountText);
        buyAmountText.setPreferredSize(new Dimension(335, 30));
        buyP2.add(buyAmountLabel, BorderLayout.WEST);
        buyP2.add(buyAmountText, BorderLayout.EAST);

        // buy slider
        JSlider buySlider = new JSlider(0, 1000);
        buySlider.setPreferredSize(new Dimension(380, 30));
        // listener to print the value that the slider is currently on
        buySlider.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e) {
            double buyPercentage = buySlider.getValue();
            buyPrice = buyPriceText.getText();
            if (!buyPrice.isEmpty()) {
                double price = Double.parseDouble(buyPrice);
                double amount = (currentOrgCredits / price) * (buyPercentage / 1000);
                buyAmountText.setText(String.valueOf(amount));
                buyTotalText.setText(String.valueOf(price * amount));
            }
            }
        });

        // panel for the third buy text field
        JPanel buyP3 = new JPanel();
        buyP3.setLayout(new BorderLayout());
        buyP3.setPreferredSize(new Dimension(380, 30));
        JLabel buyTotalLabel = new JLabel("Total:" );
        buyTotalText = new JTextField();
        numbersOnlyTextField(buyTotalText);
        buyTotalText.setEditable(false);
        buyTotalText.setPreferredSize(new Dimension(335, 30));
        buyP3.add(buyTotalLabel, BorderLayout.WEST);
        buyP3.add(buyTotalText, BorderLayout.EAST);

        // handles the updates to buy text fields
        buyPriceText.getDocument().addDocumentListener(new textFieldListener());
        buyAmountText.getDocument().addDocumentListener(new textFieldListener());

        // buy button
        buyButton = new JButton("BUY");
        buyButton.setPreferredSize(new Dimension(380, 40));
        buyButton.setBackground(new Color(2,192,118));
        buyButton.setForeground(Color.WHITE);
        buyButton.setFont(new Font("Arial", Font.PLAIN, 15));

        // adding all the widgets from above to the buyPanel to be placed inside the center panel and pushed west
        buyPanel.add(buyP0);
        buyPanel.add(buyP1);
        buyPanel.add(buyP2);
        buyPanel.add(buySlider);
        buyPanel.add(buyP3);
        buyPanel.add(buyButton);
        centerPanel.add(buyPanel, BorderLayout.WEST);
    }

    /**
     * function to make JTextFields uneditable
     * @param JTextField takes the JTextField and makes it
     */
    private void numbersOnlyTextField(JTextField JTextField) {
        JTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                    if(!((JTextField.getText()+e.getKeyChar()).matches("[0-9]*(\\.[0-9]*)?"))){
                        e.consume();
                    }
            }
        });
    }

    /**
     * Sets the Tables and Panes for the south side of the marketplace
     */
    private void southSidePanels() {
        JTabbedPane  viewOrdersPane = new JTabbedPane();
        JPanel openOrganisationalOrdersPanel = new JPanel();
        JPanel openOrganisationalOrderHistoryPanel = new JPanel();

        //tables
        JTable openOrganisationalOrdersTable = new JTable(orgOpenOrderModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        // disables row selection
        openOrganisationalOrdersTable.setRowSelectionAllowed(false);
        openOrganisationalOrdersTable.setRowHeight(30);
        openOrganisationalOrdersTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModel tcm = openOrganisationalOrdersTable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(7));
        // makes the application react to the cancel column being clicked
        openOrganisationalOrdersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = openOrganisationalOrdersTable.rowAtPoint(e.getPoint());
                int col= openOrganisationalOrdersTable.columnAtPoint(e.getPoint());
                if (openOrganisationalOrdersTable.getValueAt(row, col).toString().equals("Cancel")) {
                    // Grabs the orderID for the row that cancel was clicked
                    String orderID = openOrganisationalOrdersTable.getModel().getValueAt(row, col+1).toString();
                    // Grabs relevant order details from the database using orderID
                    Order cancelledOrder = data.getOrder(orderID);
                    String orderType = cancelledOrder.getOrderType();
                    String orderAsset = cancelledOrder.getAssetName();
                    double orderAssetQty = cancelledOrder.getAssetQty();
                    OrgAsset orgAsset = data.getOrgAsset(currentOrg, orderAsset);
                    double assetQty = orgAsset.getAssetQty();
                    // If the order type is buy, any remaining credits are given back to the org and the view is updated
                    // If the order type is sell, any remaining assets are given back to the org and the view is updated
                    if (orderType.equals("BUY")) {
                        double total = cancelledOrder.getPrice() * orderAssetQty;
                        data.updateCredits(currentOrg, String.valueOf(currentOrgCredits + total));
                    } else if (orderType.equals("SELL")) {
                        data.updateAssetQuantity(String.valueOf(orderAssetQty + assetQty), currentOrg, orderAsset);
                        if (currentAsset != null && currentAsset.equals(orderAsset)) {
                            orgAssetQty = oA.getAssetQty();
                            sellLabel1.setText("BALANCE " + orgAssetQty + " " + currentAsset );
                        }
                    }
                    // The order is removed from the database and all views are updated
                    data.removeOrder(orderID);
                    getBuyOrdersModel();
                    getSellOrdersModel();
                    getOrgOrdersModel();
                }
            }
        });
        // makes the final row in the table change text color to orange so cancel can better represent a button
        openOrganisationalOrdersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(column - 6 == 0 ? Color.orange : Color.white);
                return c;
            }
        });

        JTable organisationalOrderHistoryTable = new JTable(orgOrderHistoryModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        // disables row selection
        organisationalOrderHistoryTable.setRowSelectionAllowed(false);
        organisationalOrderHistoryTable.setRowHeight(30);
        organisationalOrderHistoryTable.getTableHeader().setReorderingAllowed(false);
        // sets header in table to align content to center
        centerTableHeaders(openOrganisationalOrdersTable);
        centerTableHeaders(organisationalOrderHistoryTable);

        JScrollPane scrollOpenOrganisationalOrdersTable = new JScrollPane(openOrganisationalOrdersTable);
        JScrollPane scrollOpenOrganisationalOrderHistoryTable = new JScrollPane(organisationalOrderHistoryTable);

        scrollOpenOrganisationalOrdersTable.setPreferredSize(new Dimension(1880, 160));
        scrollOpenOrganisationalOrderHistoryTable.setPreferredSize(new Dimension(1880, 160));
        openOrganisationalOrdersPanel.add(scrollOpenOrganisationalOrdersTable);
        openOrganisationalOrderHistoryPanel.add(scrollOpenOrganisationalOrderHistoryTable);
        viewOrdersPane.add("Open Organisational Orders",openOrganisationalOrdersPanel);
        viewOrdersPane.add("Organisational Order History",openOrganisationalOrderHistoryPanel);
        marketplacePanel.add(viewOrdersPane,BorderLayout.SOUTH);

    }

    /**
     * aligns the table headers text
     * @param table table header to be centered
     */
    private void centerTableHeaders(JTable table) {
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Sets the Tables and Panes for the east side of the marketplace
     */
    private void eastSidePanels() {
        JTabbedPane assetsPane = new JTabbedPane();
        assetTableModel = new DefaultTableModel();
        assetTableModel.addColumn("Asset");

        assetList = data.getAssetModel();
        int assetSize = data.getAssetSize();


        for (int i = 0; i < assetSize; i++) {
            Asset asset = data.getAsset(assetList.getElementAt(i));
            Object[] assetDetails = display(asset);
            assetTableModel.insertRow(i, assetDetails);
        }

        JTable assetTable = new JTable(assetTableModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        assetTable.setAutoCreateRowSorter(true);
        assetTable.getTableHeader().setReorderingAllowed(false);
        assetTable.setRowHeight(30);
        // makes the table responsive the what the user selects in the asset table
        ListSelectionModel cellSelectionModel = assetTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = assetTable.getSelectedRow();
                if (row != -1) {
                    currentAsset = assetTable.getValueAt(row, 0).toString();
                    orgAssetQty = data.getOrgAsset(currentOrg, currentAsset).getAssetQty();
                    oA = data.getOrgAsset(currentOrg, currentAsset);
                    buyLabel0.setText("BUY " + currentAsset );
                    sellLabel0.setText("SELL " + currentAsset );
                    sellLabel1.setText("BALANCE " + orgAssetQty + " " + currentAsset );
                    buyButton.setText("BUY " + currentAsset);
                    sellButton.setText("SELL " + currentAsset);
                    getBuyOrdersModel();
                    getSellOrdersModel();
                }
            }
        });

        JTabbedPane marketTradesPane = new JTabbedPane();
        JPanel assetsPanel = new JPanel();
        JPanel marketTradesPanel = new JPanel();
        JPanel eastTables = new JPanel();
        eastTables.setLayout(new BorderLayout());
        // makes the cells inside the table uneditable
        orderHistoryTable = new JTable(orderHistoryModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        // disables row selection
        orderHistoryTable.setRowSelectionAllowed(false);
        orderHistoryTable.setRowHeight(30);
        orderHistoryTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollAssetTable = new JScrollPane(assetTable);
        JScrollPane scrollMarketTrades = new JScrollPane(orderHistoryTable);
        scrollAssetTable.setPreferredSize(new Dimension(350, 360));
        scrollMarketTrades.setPreferredSize(new Dimension(350, 350));

        assetsPanel.add(scrollAssetTable);
        marketTradesPanel.add(scrollMarketTrades);
        assetsPane.add("Assets List",assetsPanel);
        marketTradesPane.add("Market Trades",marketTradesPanel);
        eastTables.add(assetsPane,BorderLayout.NORTH);
        eastTables.add(marketTradesPane,BorderLayout.SOUTH);
        marketplacePanel.add(eastTables,BorderLayout.EAST);
    }

    /**
     * Sets the Tables and Panes for the west side of the marketplace
     */
    private void westSidePanels() {
        JTabbedPane sellOrdersPane = new JTabbedPane();
        JTabbedPane buyOrdersPane = new JTabbedPane();
        JPanel sellOrdersPanel = new JPanel();
        JPanel buyOrdersPanel = new JPanel();
        JPanel westTables = new JPanel();
        westTables.setLayout(new BorderLayout());
        // makes the cells inside the table uneditable
        sellOrdersTable = new JTable(sellOrdersModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        // makes the cells inside the table uneditable
        buyOrdersTable = new JTable(buyOrdersModel) {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        sellOrdersTable.getTableHeader().setReorderingAllowed(false);
        sellOrdersTable.setRowSelectionAllowed(false);
        sellOrdersTable.setRowHeight(30);

        buyOrdersTable.getTableHeader().setReorderingAllowed(false);
        buyOrdersTable.setRowSelectionAllowed(false);
        buyOrdersTable.setRowHeight(30);
        JScrollPane scrollSellOrdersTable = new JScrollPane(sellOrdersTable);
        JScrollPane scrollBuyOrdersTable = new JScrollPane(buyOrdersTable);
        scrollSellOrdersTable.setPreferredSize(new Dimension(300, 355));
        scrollBuyOrdersTable.setPreferredSize(new Dimension(300, 350));
        sellOrdersPanel.add(scrollSellOrdersTable);
        buyOrdersPanel.add(scrollBuyOrdersTable);
        sellOrdersPane.add("Current Sell Orders ",sellOrdersPanel);
        buyOrdersPane.add("Current Buy Orders",buyOrdersPanel);
        westTables.add(sellOrdersPane,BorderLayout.NORTH);
        westTables.add(buyOrdersPane,BorderLayout.SOUTH);
        marketplacePanel.add(westTables,BorderLayout.WEST);
    }

    /**
     * used to grab an object array of asset name for table display
     *
     * @param asset the asset name to grab
     * @return an object array containing the asset
     */
    private Object[] display(Asset asset) {
        return new Object[] {
                asset.getAssetName()
        };
    }

    /**
     * Generates the table for the list of available assets
     */
    private void getAssetModel() {
        assetTableModel.setRowCount(0);
        assetList = data.getAssetModel();
        int assetSize = data.getAssetSize();

        for (int i = 0; i < assetSize; i++) {
            Asset asset = data.getAsset(assetList.getElementAt(i));
            Object[] assetDetails = display(asset);
            assetTableModel.insertRow(i, assetDetails);
        }
    }

    private class textFieldListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            Object doc = e.getDocument();
            if(doc == buyAmountText.getDocument() || doc == buyPriceText.getDocument()) {
                checkBuyFields();
            }
            if(doc == sellAmountText.getDocument() || doc == sellPriceText.getDocument()) {
                checkSellFields();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            Object doc = e.getDocument();
            if(doc == buyAmountText.getDocument() || doc == buyPriceText.getDocument()) {
                checkBuyFields();
            }
            if(doc == sellAmountText.getDocument() || doc == sellPriceText.getDocument()) {
                checkSellFields();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            Object doc = e.getDocument();
            if(doc == buyAmountText.getDocument() || doc == buyPriceText.getDocument()) {
                checkBuyFields();
            }
            if(doc == sellAmountText.getDocument() || doc == sellPriceText.getDocument()) {
                checkSellFields();
            }
        }

    }

    /**
     * check to see if the sell JTextFields are empty
     * if not they are * by each other and sent to the total JTextField
     */
    public void checkSellFields() {
        sellPrice = sellPriceText.getText();
        sellAmount = sellAmountText.getText();
        if (!sellPrice.isEmpty() && !sellAmount.isEmpty()) {
            double price = Double.parseDouble(sellPrice);
            double amount = Double.parseDouble(sellAmount);
            sellTotalText.setText(String.valueOf(price * amount));
        }
    }

    /**
     * check to see if the buy JTextFields are empty
     * if not they are * by each other and sent to the total JTextField
     */
    public void checkBuyFields() {
        buyPrice = buyPriceText.getText();
        buyAmount = buyAmountText.getText();
        if (!buyPrice.isEmpty() && !buyAmount.isEmpty()) {
            double price = Double.parseDouble(buyPrice);
            double amount = Double.parseDouble(buyAmount);
            buyTotalText.setText(String.valueOf(price * amount));
        }
    }

    /**
     * Adds a listener to all the buttons.
     */
    private void addButtonListeners(ActionListener listener) {
        buyButton.addActionListener(listener);
        sellButton.addActionListener(listener);
    }

    /**
     * Handles events for the buttons in the UI.
     */
    private class ButtonListener implements ActionListener {

        /**
         * @see ActionListener#actionPerformed(ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            JButton source = (JButton) e.getSource();
            if (source == buyButton) {
                buyPressed();
            } else if (source == sellButton) {
                sellPressed();
            }
        }

        /*
         * When the buy button is pressed, check that an asset is selected and the buy
         * fields contain something. If they do, create a new order object, attempt to
         * add it to the data model and redraw the orders tables with the new data.
         */
        private void buyPressed() {
            buyPrice = buyPriceText.getText();
            buyAmount = buyAmountText.getText();
            if (!buyPrice.isEmpty() && !buyAmount.isEmpty() & currentAsset != (null)) {
                double price = Double.parseDouble(buyPrice);
                double amount = Double.parseDouble(buyAmount);
                double total = price * amount;

                if (total <= currentOrgCredits) {
                    LocalDateTime dateNow = LocalDateTime.now();
                    Object[] myOrder = orderHandler.createOrder(
                            "BUY", currentOrg, currentAsset,amount, price, dateNow);
                    Order order = new Order(
                            myOrder[0].toString(),
                            myOrder[1].toString(),
                            myOrder[2].toString(),
                            myOrder[3].toString(),
                            (double) myOrder[4],
                            (double) myOrder[5],
                            myOrder[6].toString()
                    );
                    order.getOrderID();
                    data.addOrder(order);
                    double newBalance = currentOrgCredits - total;
                    data.updateCredits(currentOrg, String.valueOf(newBalance));
                    currentOrgCredits = org.getCredits();
                    buyLabel1.setText("BALANCE " + currentOrgCredits + " CREDITS" );
                    getBuyOrdersModel();
                    getOrgOrdersModel();
                } else {
                    JOptionPane.showMessageDialog(marketplacePanel,
                            "Warning: Insufficient Credits.");
                }
            } else {
                JOptionPane.showMessageDialog(marketplacePanel,
                        "Warning: Please select an asset and input a valid price and amount. ");
            }
        }

        /*
         * When the sell button is pressed, check that an asset is selected and the sell
         * fields contain something. If they do, create a new order object, attempt to
         * add it to the data model and redraw the orders tables with the new data.
         */
        private void sellPressed() {
            sellPrice = sellPriceText.getText();
            sellAmount = sellAmountText.getText();
            if (!sellPrice.isEmpty() && !sellAmount.isEmpty() & currentAsset != (null)) {
                double price = Double.parseDouble(sellPrice);
                double amount = Double.parseDouble(sellAmount);
                oA = data.getOrgAsset(currentOrg, currentAsset);
                double availAssets = oA.getAssetQty();

                if (amount <= availAssets) {
                    LocalDateTime dateNow = LocalDateTime.now();
                    Object[] myOrder = orderHandler.createOrder(
                            "SELL", currentOrg, currentAsset,amount, price, dateNow);
                    Order order = new Order(
                            myOrder[0].toString(),
                            myOrder[1].toString(),
                            myOrder[2].toString(),
                            myOrder[3].toString(),
                            (double) myOrder[4],
                            (double) myOrder[5],
                            myOrder[6].toString()
                    );
                    data.addOrder(order);
                    double newAssetQty = availAssets - amount;
                    data.updateAssetQuantity(String.valueOf(newAssetQty), currentOrg, currentAsset);
                    orgAssetQty = oA.getAssetQty();
                    sellLabel1.setText("BALANCE " + orgAssetQty + " " + currentAsset );
                    getSellOrdersModel();
                    getOrgOrdersModel();
                } else {
                    JOptionPane.showMessageDialog(marketplacePanel,
                            "Warning: Insufficient Assets Available.");
                }
            } else {
                JOptionPane.showMessageDialog(marketplacePanel,
                        "Warning: Please select an asset and input a valid price and amount. ");
            }
        }
    }

    /**
     * Generates the table model for the buy orders
     */
    private void getBuyOrdersModel() {
        buyOrdersModel.setRowCount(0);
        buyOrdersList = data.getBuyOrderModel();
        int buyOrderSize = data.getOrderSize("BUY");

        for (int i = 0; i < buyOrderSize; i++) {
            Order order = data.getOrder(buyOrdersList.getElementAt(i));
            if (order.getAssetName().equals(currentAsset)) {
                Object[] orderDetails = displayOrder(order);
                buyOrdersModel.addRow(orderDetails);
            }
        }
    }

    /**
     * Generates the table model for the sell orders
     */
    private void getSellOrdersModel() {
        sellOrdersModel.setRowCount(0);
        sellOrdersList = data.getSellOrderModel();
        int sellOrderSize = data.getOrderSize("SELL");

        for (int i = 0; i < sellOrderSize; i++) {
            Order order = data.getOrder(sellOrdersList.getElementAt(i));
            if (order.getAssetName().equals(currentAsset)) {
                Object[] orderDetails = displayOrder(order);
                sellOrdersModel.addRow(orderDetails);
            }
        }
    }

    /**
     * Generates the table for the list of organisations orders
     */
    private void getOrgOrdersModel() {
        orgOpenOrderModel.setRowCount(0);
        orgOpenOrdersList = data.getAllOrdersModel();
        int buyOrderSize = data.getOrderSize("BUY");
        int sellOrderSize = data.getOrderSize("SELL");
        int totalOrderSize = buyOrderSize + sellOrderSize;

        for (int i = 0; i < totalOrderSize; i++) {
            Order order = data.getOrder(orgOpenOrdersList.getElementAt(i));
            if (order.getOrgUnit().equals(currentOrg)) {
                Object[] orderDetails = displayAllOrgOrders(order);
                orgOpenOrderModel.addRow(orderDetails);
            }
        }
    }

    /**
     * Generates the table for the list of organisations order history
     */
    private void getOrgOrderHistoryModel() {
        orgOrderHistoryModel.setRowCount(0);
        orgOrderHistoryList = data.getAllOldOrderModel();
        int orderHistory = orgOrderHistoryList.getSize();

        for (int i = 0; i < orderHistory; i++) {
            Order order = data.getOldOrder(orgOrderHistoryList.getElementAt(i));
            if (order.getOrgUnit().equals(currentOrg)) {
                Object[] orderDetails = displayOrgOrderHistory(order);
                orgOrderHistoryModel.addRow(orderDetails);
            }
        }
    }

    /**
     * Generates the table model for the sell orders
     */
    private void getAssetOrderHistoryModel() {
        orderHistoryModel.setRowCount(0);
        assetPriceHistory.clear();
        assetDateHistory.clear();
        orderHistoryList = data.getOldOrderModel(currentAsset);
        int orderHistory = orderHistoryList.getSize();

        for (int i = 0; i < orderHistory; i++) {
            Order order = data.getOldOrder(orderHistoryList.getElementAt(i));
            if (order.getAssetName().equals(currentAsset)) {
                assetPriceHistory.add(order.getPrice());
                String date = order.getDate().replace(".0", "");
                assetDateHistory.add(LocalDateTime.parse(date, dtf));
                Object[] orderDetails = displayOrderHistory(order);
                orderHistoryModel.addRow(orderDetails);
            }
        }
        if (assetPriceHistory.size() >= 3) {
            Collections.reverse(assetPriceHistory);
            Collections.reverse(assetDateHistory);
            graph.setPrices(assetPriceHistory);
            graph.setDates(assetDateHistory);
        } else {
            graph.setPrices(Arrays.asList(0.0, 100.0));
            graph.setDates(Arrays.asList(LocalDateTime.now()));
        }
    }

    /**
     * used to grab an object array of order details for table display
     *
     * @param order the order to grab details for
     * @return an object array containing the order details
     */
    private Object[] displayOrder(Order order) {
        return new Object[] {
                order.getPrice(),
                order.getAssetQty(),
                order.getPrice() * order.getAssetQty()
        };
    }

    /**
     * used to grab an object array of an organisations
     * order details for table display
     *
     * @param order the order to grab details for
     * @return an object array containing the order details
     */
    private Object[] displayAllOrgOrders(Order order) {
        return new Object[] {
                order.getDate(),
                order.getAssetName(),
                order.getOrderType(),
                order.getPrice(),
                order.getAssetQty(),
                order.getPrice() * order.getAssetQty(),
                "Cancel",
                order.getOrderID()
        };
    }

    /**
     * used to grab an object array of order details for table display
     *
     * @param order the order to grab details for
     * @return an object array containing the order details
     */
    private Object[] displayOrderHistory(Order order) {
        return new Object[] {
                order.getPrice(),
                order.getAssetQty(),
                order.getOrderType(),
                order.getDate()
        };
    }

    /**
     * used to grab an object array of order details for table display
     *
     * @param order the order to grab details for
     * @return an object array containing the order details
     */
    private Object[] displayOrgOrderHistory(Order order) {
        return new Object[] {
                order.getDate(),
                order.getAssetName(),
                order.getOrderType(),
                order.getPrice(),
                order.getAssetQty(),
                order.getPrice() * order.getAssetQty(),
                order.getOrderID()
        };
    }

    private void checkOrders() {
        ListModel assets = data.getAssetModel();
        ListModel assetBuyOrders;
        ListModel assetSellOrders;
        Order currentBuyOrder = new Order();
        Order currentSellOrder = new Order();
        Order bestSellOrder = new Order();
        double sellPrice;
        int buySize;
        int sellSize;

        // Loops through the list of assets, comparing their buy and sell orders
        for (int i = 0; i < assets.getSize(); i++) {
            String asset = (String) assets.getElementAt(i);

            assetBuyOrders = data.getAssetOrderModel(asset, "BUY");
            assetSellOrders = data.getAssetOrderModel(asset, "SELL");
            buySize = assetBuyOrders.getSize();
            sellSize = assetSellOrders.getSize();

            // continues only if the buy and sell orders are not 0
            if (buySize != 0 && sellSize != 0) {
                for (int x = 0; x < buySize; x++) {
                    // set the buy order to check -> already sorted by date
                    Order buyOrder = data.getOrder(assetBuyOrders.getElementAt(x));
                    currentBuyOrder = buyOrder;
                    // set the sell orders to check buy order against (sorted by date)
                    for (int y = 0; y < sellSize; y++) {

                        currentSellOrder = data.getOrder(assetSellOrders.getElementAt(y));
                        if (currentSellOrder.getPrice() != null) {
                            sellPrice = currentSellOrder.getPrice();
                            // finds the best sell price to process trade
                            if (bestSellOrder.getPrice() == null || bestSellOrder.getPrice() > sellPrice) {
                                bestSellOrder = currentSellOrder;
                                currentSellOrder = null;
                            }
                        }
                    }

                    // continues if there is both a valid buy and sell order for the asset
                    if (bestSellOrder.getOrderID() != null && currentBuyOrder.getOrderID() != null) {
                        if (currentBuyOrder.getPrice() >= bestSellOrder.getPrice()) {

                            // buy order details
                            String buyID = currentBuyOrder.getOrderID();
                            double buyQty = currentBuyOrder.getAssetQty();
                            double buyerPrice = currentBuyOrder.getPrice();
                            String buyOrg = currentBuyOrder.getOrgUnit();
                            OrgAsset buyingOrgAsset = data.getOrgAsset(buyOrg, asset);
                            double orgAssetQty = buyingOrgAsset.getAssetQty();
                            double refundPrice = 0;
                            // sell order details
                            String sellID = bestSellOrder.getOrderID();
                            double sellQty = bestSellOrder.getAssetQty();
                            double sellerPrice = bestSellOrder.getPrice();
                            String sellOrg = bestSellOrder.getOrgUnit();
                            Org orgSeller = data.getOrg(sellOrg);
                            double orgSellerCredits = orgSeller.getCredits();

                            // gets the asset quantity remaining after the transaction
                            double quantityDiff = buyQty - sellQty;
                            double priceDiff = buyerPrice - sellerPrice;

                            // buyer offered more - refund difference
                            if (priceDiff > 0) {
                                // buyer requested more asset than seller could offer or
                                // buyer qty request = seller qty offer
                                if (quantityDiff >= 0) {
                                    refundPrice = priceDiff * sellQty;
                                } else { // buyer requested less assets than seller offered
                                    refundPrice = priceDiff * buyQty;
                                }
                                data.updateCredits(currentOrg, String.valueOf(currentOrgCredits + refundPrice));
                            } // else buyer offered what seller requested - no compensation

                            // the sellers updated credits from their sale
                            String newSellerCredits = String.valueOf(orgSellerCredits + (sellQty * sellerPrice));
                            String newBuyerAssets = String.valueOf(orgAssetQty + sellQty);
                            // determine if any - the remaining quantity of asset remaining in either order
                            if (quantityDiff > 0) {
                                // updates the buy order, fulfils the seller order
                                // removes it from the open orders and adds it to the order history
                                data.updateOrder(String.valueOf(quantityDiff), buyID);
                                data.updateAssetQuantity(newBuyerAssets, buyOrg, asset);
                                data.updateCredits(sellOrg, newSellerCredits);
                                data.removeOrder(sellID);
                                bestSellOrder.setDate(LocalDateTime.now().format(dtf));
                                data.addOldOrder(bestSellOrder);
                            } else if (quantityDiff == 0) { // buyer qty request = seller qty offer
                                // fulfils the buyer and seller order and removes them
                                // from the open orders and adds them to the order history
                                data.updateCredits(sellOrg, newSellerCredits);
                                data.updateAssetQuantity(newBuyerAssets, buyOrg, asset);
                                data.removeOrder(buyID);
                                data.removeOrder(sellID);
                                bestSellOrder.setDate(LocalDateTime.now().format(dtf));
                                currentBuyOrder.setDate(LocalDateTime.now().format(dtf));
                                data.addOldOrder(bestSellOrder);
                                data.addOldOrder(currentBuyOrder);
                            } else { // buyer requested less assets than seller offered
                                // updates the sell order, fulfils the buyer order and
                                // removes it from the open orders and adds it to the order history
                                data.updateOrder(String.valueOf(abs(quantityDiff)), sellID);
                                data.updateCredits(sellOrg, String.valueOf(orgSellerCredits + (buyQty * sellerPrice)));
                                data.updateAssetQuantity(String.valueOf(orgAssetQty + buyQty), buyOrg, asset);
                                data.removeOrder(buyID);
                                currentBuyOrder.setDate(LocalDateTime.now().format(dtf));
                                data.addOldOrder(currentBuyOrder);
                            }
                            bestSellOrder = new Order();
                            getBuyOrdersModel();
                            getSellOrdersModel();
                            getOrgOrdersModel();

                        } // else buyer offered less than what sellers are requesting - continue to next buy offer
                    }
                }
            }
        }
    }
}