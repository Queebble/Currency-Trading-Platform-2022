package Common.Panels;

import Common.Constructors.Asset;
import Common.MarketplaceData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Initialises the asset manager panel for the admin view.
 * All listeners for asset page are included as inner classes of this class.
 */
public class CreateAssetPanel {

    private JPanel assetPanel;
    private JButton addButton;
    private JButton removeButton;
    private JTextField assetJTextField;
    private ListModel assetList;
    private MarketplaceData data;
    private String asset = "";

    /**
     * initialising a default table model
     */
    public DefaultTableModel assetTableModel;

    /**
     * creates the asset panel for the admin display
     * @param panel panel for the create assets panel to be placed inside
     * @param data Data to be taken from the database
     */
    public CreateAssetPanel(JPanel panel, MarketplaceData data) {
        this.data = data;

        /**
         * Checks regularly if new assets have been added and
         * updates asset table when required.
         */
        ActionListener checkAssets = e -> {
            if (assetTableModel.getRowCount() != data.getAssetSize()) {
                getAssetModel();
            }
        };
        new Timer(1000, checkAssets).start();

        assetPanel = new JPanel();
        assetPanel.setPreferredSize(new Dimension(1000, 1000));
        assetPanel.setLayout(new BoxLayout(assetPanel, BoxLayout.Y_AXIS));

        // Displays Title for User Panel
        JPanel assetTitlePanel = new JPanel();
        assetTitlePanel.setMaximumSize(new Dimension(400, 100));
        JLabel assetTitleLabel = new JLabel("Manage Assets");
        assetTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        assetTitlePanel.add(assetTitleLabel);
        assetPanel.add(Box.createVerticalStrut(50));
        assetPanel.add(assetTitlePanel);
        assetPanel.add(Box.createVerticalStrut(200));
        assetPanel.add(getAssetPanel());
        assetPanel.add(Box.createVerticalStrut(-50));
        assetPanel.add(makeButtonsPanel());
        assetPanel.add(Box.createVerticalStrut(20));

        addButtonListeners(new ButtonListener());

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
        // makes the table responsive the what the user selects in the asset table
        ListSelectionModel cellSelectionModel = assetTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = assetTable.getSelectedRow();
                if (row != -1) {
                    asset = assetTable.getValueAt(row, 0).toString();
                }
            }
        });
        assetTable.setAutoCreateRowSorter(true);
        assetTable.getTableHeader().setReorderingAllowed(false);
        assetTable.setRowHeight(30);

        JScrollPane assetScroller = new JScrollPane(assetTable);
        assetScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        assetScroller.setMaximumSize(new Dimension(600, 400));

        assetPanel.add(assetScroller);
        assetPanel.add(Box.createVerticalStrut(20));

        panel.add(assetPanel);
    }

    /**
     * Method for generating the asset panel
     *
     * @return The panel containing text fields
     * for managing assets.
     */
    private JPanel getAssetPanel() {
        JPanel assetDetails = new JPanel();
        assetDetails.setLayout(new BoxLayout(assetDetails, BoxLayout.X_AXIS));
        assetDetails.add(Box.createVerticalStrut(20));
        JPanel assetSettings = new JPanel();
        assetSettings.setMaximumSize(new Dimension(100, 150));
        GroupLayout assetLayout = new GroupLayout(assetSettings);
        assetSettings.setLayout(assetLayout);
        assetLayout.setAutoCreateGaps(true);
        assetLayout.setAutoCreateContainerGaps(true);

        JLabel assetLabel = new JLabel("Asset");

        assetJTextField = new JTextField(20);

        GroupLayout.SequentialGroup hGroup = assetLayout.createSequentialGroup();

        hGroup.addGroup(assetLayout.createParallelGroup()
                .addComponent(assetLabel));

        hGroup.addGroup(assetLayout.createParallelGroup()
                .addComponent(assetJTextField));

        assetLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = assetLayout.createSequentialGroup();

        vGroup.addGroup(assetLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(assetLabel).addComponent(assetJTextField));
        assetLayout.setVerticalGroup(vGroup);


        assetDetails.add(assetSettings);
        assetDetails.add(Box.createVerticalStrut(20));
        return assetDetails;
    }

    /**
     * Adds the New, Save and Delete buttons to the asset panel
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
     * Adds a listener to the new, save and delete buttons
     */
    private void addButtonListeners(ActionListener listener) {
        addButton.addActionListener(listener);
        removeButton.addActionListener(listener);
    }

    /**
     * Handles events for the three buttons on the UI.
     *
     * @author Malcolm Corney
     * @version $Id: Exp $
     *
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
            }
        }


        /**
         * When the save button is pressed, check that the name field contains
         * something. If it does, create a new Person object and attempt to add it
         * to the data model. Change the fields back to not editable and make the
         * save button inactive.
         *
         * Check the list size to see if the delete button should be enabled.
         */
        private void addPressed() {
            if (assetJTextField.getText() != null && !assetJTextField.getText().isBlank()) {
                Asset a = new Asset(assetJTextField.getText());
                data.addAsset(a);

                int beforeRemoval = assetTableModel.getRowCount();
                getAssetModel();
                int afterRemoval = assetTableModel.getRowCount();

                if (beforeRemoval == afterRemoval) {
                    JOptionPane.showMessageDialog(assetPanel,
                            "Warning: Unable to add asset. This asset already exists.");
                }

            } else {
                JOptionPane.showMessageDialog(assetPanel,
                        "Warning: Please enter an asset to add it.");
            }
            assetJTextField.setText("");
            checkListSize();
        }

        /**
         * When the delete button is pressed remove the selected name from the
         * data model.
         *
         * Clear the fields that were displayed and check to see if the delete
         * button should be displayed.
         *
         * The index here handles cases where the first element of the list is
         * deleted.
         */
        private void removePressed() {
            assetJTextField.setText("");
            data.removeAsset(asset);

            if (asset.equals("")) {
                JOptionPane.showMessageDialog(assetPanel,
                        "Warning: You must select an asset to remove it.");
            } else {

                asset = "";

                int beforeRemoval = assetTableModel.getRowCount();
                getAssetModel();
                int afterRemoval = assetTableModel.getRowCount();

                if (beforeRemoval == afterRemoval) {
                    JOptionPane.showMessageDialog(assetPanel,
                            "Warning: Unable to remove asset. This asset may " +
                                    "belong to organisation(s) and still be involved in open orders.");
                }

                checkListSize();
            }
        }
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


    /**
     * Checks size of data/model and enables/disables the delete button
     *
     */
    private void checkListSize() {
        removeButton.setEnabled(data.getAssetSize() != 0);
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
}