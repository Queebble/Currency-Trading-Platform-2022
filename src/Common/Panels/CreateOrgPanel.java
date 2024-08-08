package Common.Panels;

import Common.Constructors.Asset;
import Common.Constructors.Org;
import Common.Constructors.OrgAsset;
import Common.MarketplaceData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Initialises the organisation manager panel for the admin view.
 * All listeners for organisation page are included as inner classes of this class.
 */
public class CreateOrgPanel {

    private JPanel orgPanel;
    private String currentOrg = "";
    private String currentCredits = "0";
    private String currentAsset = "";
    private String assetQty = "0";
    private String addAsset = "";
    private ListModel orgAssetsList;
    private ListModel orgsList;
    private ListModel assetList;
    private MarketplaceData data;
    private JButton addButton;
    private JButton removeButton;
    private JButton addAssetButton;
    private JButton removeAssetButton;
    private JTextField orgName;
    private JTextField orgCredits;
    private DefaultTableModel assetModel;
    private DefaultTableModel orgAssetsModel;
    private DefaultTableModel orgTableModel;
    private JTable orgTable;
    private JTable orgAssetTable;
    private JTable assetTable;

    /**
     * creates the organisation units panel and adds to the admin display
     * @param panel the admin panel to add to
     * @param data the data source to retrieve and send panel data
     */
    public CreateOrgPanel(JPanel panel, MarketplaceData data) {
        this.data = data;
        orgPanel = new JPanel();
        orgPanel.setPreferredSize(new Dimension(1400, 1000));
        orgPanel.setLayout(new BoxLayout(orgPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(550, 100));
        JLabel orgTitle = new JLabel("Manage Organisations");
        orgTitle.setFont(new Font("SansSerif", Font.BOLD, 50));
        titlePanel.add(orgTitle);

        orgPanel.add(Box.createVerticalStrut(50));
        orgPanel.add(titlePanel);
        orgPanel.add(Box.createVerticalStrut(150));
        orgPanel.add(getOrgPanel());
        orgPanel.add(Box.createVerticalStrut(-50));
        orgPanel.add(makeButtonsPanel());
        orgPanel.add(makeButtonsPanel1());

        addButtonListeners(new ButtonListener());

        JPanel orgTables = new JPanel();
        orgTables.setMaximumSize(new Dimension(1200, 400));

        // Create table models for organisation data
        orgTableModel = new DefaultTableModel();
        orgTableModel.addColumn("Organisation Unit");
        orgTableModel.addColumn("Credits");

        orgAssetsModel = new DefaultTableModel();
        orgAssetsModel.addColumn("Asset Name");
        orgAssetsModel.addColumn("Asset Quantity");

        assetModel = new DefaultTableModel();
        assetModel.addColumn("Asset Name");

        // Creates table models
        getOrgTableModel();
        getAssetModel();

        /**
         * Checks regularly if the server contains new entries and
         * updates tables when required.
         */
        ActionListener checkAssets = e -> {
            if (orgTableModel.getRowCount() != data.getOrgSize()) {
                getOrgTableModel();
            }
            if (!currentOrg.isBlank()) {
                if (orgAssetsModel.getRowCount() != data.getCurrentOrgAssetSize(currentOrg)) {
                    getOrgAssetModel();
                }
            }
            if (assetModel.getRowCount() != data.getAssetSize()) {
                getAssetModel();
            }
        };
        new Timer(1000, checkAssets).start();

        // Creates tables
        JTable orgTable = createOrgTable();
        JTable orgAssetTable = createOrgAssetTable();
        JTable assetTable = createAssetTable();

        JScrollPane orgScroller = new JScrollPane(orgTable);
        JScrollPane orgAssetScroller = new JScrollPane(orgAssetTable);
        JScrollPane assetScroller = new JScrollPane(assetTable);
        orgScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orgScroller.setPreferredSize(new Dimension(480, 400));
        orgAssetScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orgAssetScroller.setPreferredSize(new Dimension(350, 400));
        assetScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        assetScroller.setPreferredSize(new Dimension(150, 400));

        orgTables.add(orgScroller);
        orgTables.add(orgAssetScroller);
        orgTables.add(assetScroller);

        orgPanel.add(orgTables);
        orgPanel.add(Box.createVerticalStrut(20));

        panel.add(orgPanel);
    }

    /**
     * Creates the table displaying organisations/credits
     * @return the organisation table
     */
    private JTable createOrgTable() {
        // Table containing organisations units + credits
        orgTable = new JTable(orgTableModel) {
            public boolean isCellEditable(int row, int column){
                if (column == 1) { return true; }
                return false;
            }
        };
        // makes the table respond to what the user selects in the org table
        ListSelectionModel cellSelectionModel = orgTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = orgTable.getSelectedRow();
                if (row != -1) {
                    currentOrg = orgTable.getValueAt(row, 0).toString();
                    currentCredits = orgTable.getValueAt(row, 1).toString();
                    if (orgAssetTable.getCellEditor() != null) {
                        orgAssetTable.getCellEditor().stopCellEditing();
                    }
                    getOrgAssetModel();
                }
            }
        });
        orgTable.setRowHeight(30);
        orgTable.setAutoCreateRowSorter(true);
        orgTable.getTableHeader().setReorderingAllowed(false);
        // Sets the keybindings for the table
        createKeybindings(orgTable);
        // Sets the cell editor for the credits column
        TableColumnModel orgColumnModel = orgTable.getColumnModel();
        TableColumn col = orgColumnModel.getColumn(1);
        col.setCellEditor(new DoubleEditor());
        // Adds a cell listener for when an orgs credits are updated
        Action orgAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                currentCredits = orgTable.getValueAt(tcl.getRow(), 1).toString();
                data.updateCredits(currentOrg, currentCredits);
            }
        };
        TableCellListener orgTCL = new TableCellListener(orgTable, orgAction);
        return orgTable;
    }

    /**
     * Creates the table displaying assets/quantity for an organisations
     * @return the organisation's asset table
     */
    private JTable createOrgAssetTable() {
        // Table containing an organisation's assets
        orgAssetTable = new JTable(orgAssetsModel) {
            public boolean isCellEditable(int row, int column){
                if (column == 1) { return true; }
                return false;
            }
        };
        orgAssetTable.setRowHeight(30);
        orgAssetTable.getTableHeader().setReorderingAllowed(false);
        orgAssetTable.setAutoCreateRowSorter(true);
        // makes the table respond to what the user selects in the org asset table
        ListSelectionModel cellSelectionModel = orgAssetTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = orgAssetTable.getSelectedRow();
                if (row != -1) {
                    currentAsset = orgAssetTable.getValueAt(row, 0).toString();
                    assetQty = orgAssetTable.getValueAt(row, 1).toString();
                }
            }
        });
        // Sets the keybindings for the table
        createKeybindings(orgAssetTable);
        // Sets the cell editor for the credits column
        TableColumnModel orgColumnModel = orgAssetTable.getColumnModel();
        TableColumn col = orgColumnModel.getColumn(1);
        col.setCellEditor(new DoubleEditor());
        // Adds a listener for when an orgs asset quantity is updated
        Action orgAssetAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                assetQty = orgAssetTable.getValueAt(tcl.getRow(), 1).toString();
                data.updateAssetQuantity(assetQty, currentOrg, currentAsset);
            }
        };
        TableCellListener orgAssetTCL = new TableCellListener(orgAssetTable, orgAssetAction);
        return orgAssetTable;
    }


    /**
     * Creates the table displaying assets in the marketplace
     * @return the asset table
     */
    private JTable createAssetTable() {
        // Table containing all available assets
        assetTable = new JTable(assetModel) {
            public boolean isCellEditable(int row, int column){ return false; }
        };
        assetTable.setRowHeight(30);
        assetTable.setAutoCreateRowSorter(true);
        assetTable.getTableHeader().setReorderingAllowed(false);
        // makes the table respond to what the user selects in the asset table
        ListSelectionModel cellSelectionModel = assetTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = assetTable.getSelectedRow();
                if (row != -1) {
                    addAsset = assetTable.getValueAt(row, 0).toString();
                }
            }
        });
        return assetTable;
    }

    /**
     * Method for generating the organisations panel
     *
     * @return The panel containing text fields
     * for managing organisations.
     */
    private JPanel getOrgPanel() {
        JPanel orgDetails = new JPanel();
        orgDetails.setLayout(new BoxLayout(orgDetails, BoxLayout.X_AXIS));
        orgDetails.add(Box.createVerticalStrut(20));
        JPanel userSettings = new JPanel();
        userSettings.setMaximumSize(new Dimension(100, 150));
        GroupLayout userLayout = new GroupLayout(userSettings);
        userSettings.setLayout(userLayout);
        userLayout.setAutoCreateGaps(true);
        userLayout.setAutoCreateContainerGaps(true);

        JLabel orgNameLabel = new JLabel("Organisation Unit");
        JLabel orgCreditLabel = new JLabel("Credits");

        orgName = new JTextField(20);
        orgCredits = new JTextField(20);
        numbersOnlyTextField(orgCredits);

        GroupLayout.SequentialGroup hGroup = userLayout.createSequentialGroup();

        hGroup.addGroup(userLayout.createParallelGroup()
                .addComponent(orgNameLabel)
                .addComponent(orgCreditLabel));

        hGroup.addGroup(userLayout.createParallelGroup()
                .addComponent(orgName)
                .addComponent(orgCredits));

        userLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = userLayout.createSequentialGroup();

        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(orgNameLabel).addComponent(orgName));
        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(orgCreditLabel).addComponent(orgCredits));
        userLayout.setVerticalGroup(vGroup);


        orgDetails.add(userSettings);
        orgDetails.add(Box.createVerticalStrut(20));
        return orgDetails;
    }

    /**
     * limits the input of a textfield to only numbers
     *
     * @param JTextField the text field to limit input
     */
    private void numbersOnlyTextField(JTextField JTextField) {
        JTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
    }

    /**
     * Creates the Add and Remove buttons for the organisations
     */
    private JPanel makeButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createHorizontalStrut(50));
        return buttonPanel;
    }

    /**
     * Creates the Add and Remove asset buttons for an organisation's assets
     */
    private JPanel makeButtonsPanel1() {
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setMaximumSize(new Dimension(1000,20));
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.X_AXIS));
        addAssetButton = new JButton("Add Asset");
        removeAssetButton = new JButton("Remove Asset");
        buttonPanel1.add(Box.createHorizontalStrut(490));
        buttonPanel1.add(removeAssetButton);
        buttonPanel1.add(Box.createHorizontalStrut(250));
        buttonPanel1.add(addAssetButton);
        return buttonPanel1;
    }

    /**
     * Adds a listener to all the buttons.
     */
    private void addButtonListeners(ActionListener listener) {
        addButton.addActionListener(listener);
        removeButton.addActionListener(listener);
        addAssetButton.addActionListener(listener);
        removeAssetButton.addActionListener(listener);
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
            if (source == addButton) {
                addPressed();
            } else if (source == removeButton) {
                removePressed();
            } else if (source == addAssetButton) {
                addAssetPressed();
            } else if (source == removeAssetButton) {
                removeAssetPressed();
            }
        }

        /**
         * When the add button is pressed, check that the name field contains
         * something. If it does, create a new organisation object, attempt to add it
         * to the data model and redraw the organisations table with the new data.
         */
        private void addPressed() {
            if (orgName.getText() != null && !orgName.getText().isBlank()
            && orgCredits.getText() != null && !orgCredits.getText().isBlank()) {

                // Checks to see if the organisation exists, adds the org if it doesn't
                if (data.getOrg(orgName.getText()).getOrgUnit() == null) {
                    double credits = Double.parseDouble(orgCredits.getText());
                    Org o = new Org(orgName.getText(), credits);
                    data.addOrg(o);

                    // Redraws the table containing the new data
                    if (orgTable.getCellEditor() != null) {
                        orgTable.getCellEditor().stopCellEditing();
                    }
                    getOrgTableModel();

                } else {
                    JOptionPane.showMessageDialog(orgPanel,
                            "Warning: Unable to add organisation. " +
                                    "This organisation already exists.");
                }
            } else {
                JOptionPane.showMessageDialog(orgPanel,
                        "Warning: Please enter an organisation unit and " +
                                "credits amount to add a new organisation.");
            }
            orgName.setText("");
            orgCredits.setText("");
        }

        /**
         * When the remove button is pressed, check that an organisation is selected
         * If it is, remove an organisation object from the data model
         * and redraw the organisations table with the new data.
         */
        private void removePressed() {
            orgName.setText("");
            orgCredits.setText("");
            data.removeOrg(currentOrg);

            if (currentOrg.equals("")) {
                JOptionPane.showMessageDialog(orgPanel,
                        "Warning: You must select an organisation to remove it.");
            } else {
                currentOrg = "";
                if (orgTable.getCellEditor() != null) {
                    orgTable.getCellEditor().stopCellEditing();
                }
                getOrgAssetModel();

                int beforeRemoval = orgTableModel.getRowCount();
                // Redraws the table containing the new data
                getOrgTableModel();
                int afterRemoval = orgTableModel.getRowCount();

                if (beforeRemoval == afterRemoval) {
                    JOptionPane.showMessageDialog(orgPanel,
                            "Warning: Unable to remove organisation. This organisation " +
                                    "still has users or assets associated with it.");
                }
            }
        }

        /**
         * When the add asset button is pressed, check that an asset is selected.
         * If one is, create a new organisation object, attempt to add it
         * to the data model and redraw the organisations table with the new data.
         */
        private void addAssetPressed() {
            if (!addAsset.equals("") && !currentOrg.equals("")) {
                String newOrgAsset = currentOrg + ",," + addAsset;
                OrgAsset orgasset = new OrgAsset(currentOrg, addAsset, 0);
                data.addOrgAsset(newOrgAsset, orgasset);
                int beforeRemoval = orgAssetsModel.getRowCount();
                if (orgAssetTable.getCellEditor() != null) {
                    orgAssetTable.getCellEditor().stopCellEditing();
                }
                getOrgAssetModel();
                int afterRemoval = orgAssetsModel.getRowCount();
                if (beforeRemoval == afterRemoval) {
                    JOptionPane.showMessageDialog(orgPanel,
                            "Warning: Unable to add asset. This organisation " +
                                    "already possesses this asset.");
                }
            } else {
                JOptionPane.showMessageDialog(orgPanel,
                        "Warning: Please select an organisation and an asset " +
                                "before attempting to add an asset.");
            }
        }

        /**
         * When the remove asset button is pressed, check that an organisation
         * and asset are selected. If they are, remove an organisation object from the data model
         * and redraw the organisations table with the new data.
         */
        private void removeAssetPressed() {
            if (currentOrg.equals("") || currentAsset.equals("")) {
                JOptionPane.showMessageDialog(orgPanel,
                        "Warning: You must select an organisation's asset to remove it.");
            } else {
                String removingOrgAsset = currentOrg + ",," + currentAsset;
                OrgAsset orgasset = data.getOrgAsset(currentOrg, currentAsset);
                data.removeOrgAsset(removingOrgAsset, orgasset);

                int beforeRemoval = orgAssetsModel.getRowCount();
                if (orgAssetTable.getCellEditor() != null) {
                    orgAssetTable.getCellEditor().stopCellEditing();
                }
                getOrgAssetModel();
                int afterRemoval = orgAssetsModel.getRowCount();
                currentAsset = "";

                if (beforeRemoval == afterRemoval) {
                    JOptionPane.showMessageDialog(orgPanel,
                            "Warning: Unable to remove asset. This organisation " +
                                    "still has open orders for this asset. Please resolve any open orders " +
                                    "before removing it.");
                }
            }
        }
    }

    /**
     * Generates the table for the list of organisations
     */
    private void getOrgTableModel() {
        orgTableModel.setRowCount(0);
        orgsList = data.getOrgModel();
        int orgSize = data.getOrgSize();

        for (int i = 0; i < orgSize; i++) {
            Org org = data.getOrg(orgsList.getElementAt(i));
            Object[] assetDetails = displayOrg(org);
            orgTableModel.insertRow(i, assetDetails);
        }
    }

    /**
     * generates a table for a selected orgs assets
     */
    private void getOrgAssetModel() {
        orgAssetsModel.setRowCount(0);
        orgAssetsList = data.getOrgAssetModel();
        int orgAssetSize = data.getOrgAssetSize();

        for (int i = 0; i < orgAssetSize; i++) {
            String oa = (String) orgAssetsList.getElementAt(i);
            String[] orgsAsset = oa.split(",,");
            if (orgsAsset[0].equalsIgnoreCase(currentOrg)){
                OrgAsset orgAssets = data.getOrgAsset(orgsAsset[0], orgsAsset[1]);
                Object[] orgAssetDetails = displayOrgAssets(orgAssets);
                orgAssetsModel.addRow(orgAssetDetails);
            }
        }
    }

    /**
     * Generates the table for the list of available assets
     */
    private void getAssetModel() {
        assetModel.setRowCount(0);
        assetList = data.getAssetModel();
        int assetSize = data.getAssetSize();

        for (int i = 0; i < assetSize; i++) {
            Asset asset = data.getAsset(assetList.getElementAt(i));
            Object[] assetDetails = displayAsset(asset);
            assetModel.insertRow(i, assetDetails);
        }
    }

    /**
     * used to grab an object array of org details for table display
     *
     * @param org the org to grab details for
     * @return an object array containing the org details
     */
    private Object[] displayOrg(Org org) {
        return new Object[] {
                org.getOrgUnit(),
                org.getCredits()
        };
    }

    /**
     * used to grab an object array of an organisations asset name and quantity
     *
     * @param orgAsset the asset name to grab
     * @return an object array containing the asset
     */
    private Object[] displayOrgAssets(OrgAsset orgAsset) {
        return new Object[] {
                orgAsset.getAssetName(),
                orgAsset.getAssetQty()
        };
    }

    /**
     * used to grab an object array of asset names available
     *
     * @param asset the asset name to grab
     * @return an object array containing the asset
     */
    private Object[] displayAsset(Asset asset) {
        return new Object[] {
                asset.getAssetName()
        };
    }

    /**
     * Handles the editor for any number columns to prevent string or invalid values
     */
    private class DoubleEditor extends AbstractCellEditor implements TableCellEditor {

        JTextField textField = new JTextField();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textField.setText(String.valueOf(value));
            return textField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = textField.getText();
            if(s.equals(""))
                s = "0.0";
            try
            {
                double d = Double.parseDouble(s);
                if (d < 0.0 || d == -0.0) {
                    Double.parseDouble("Throw");
                }
            }
            catch(NumberFormatException nfe)
            {
                JOptionPane.showMessageDialog(orgPanel,
                        "Warning: Update failed. " +
                                "Please input a valid number (non-negative).");
                return Double.valueOf(currentCredits);
            }
            return Double.valueOf(s);
        }
    }

    /**
     * Maps the "Enter" keybinding for the tables - disables enter
     * key from moving to the next row
     * @param table the table to map key binding
     */
    private void createKeybindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            // If the enter key is pressed while editing a cell
            // the editing is stopped firing any event changes
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
            }
        });
    }
}
