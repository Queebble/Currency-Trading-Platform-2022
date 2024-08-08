package Common.Panels;

import Common.Constructors.HashPwd;
import Common.MarketplaceData;
import Common.Constructors.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Initialises the user manager panel for the admin view.
 * All listeners for user page are included as inner classes of this class.
 */
public class CreateUserPanel {

    private JPanel userPanel;
    private String currentUser = "";
    private String updatedUser = "";
    private String currentPwd = "";
    private String currentAccType = "";
    private String currentOrgUnit = "";
    private String updatedOrgUnit = "";
    private JButton addButton;
    private JButton removeButton;
    private JTextField username;
    private JTextField password;
    private JTextField accType;
    private JTextField orgUnit;
    private DefaultTableModel userTableModel;
    private MarketplaceData data;
    private ListModel userList;
    private HashPwd hashPwd = new HashPwd();
    private JTable userTable;

    /**
     * creates the user panel for the admin display
     * @param panel panel for the create users panel to be placed inside
     * @param data Data to be taken from the database
     */
    public CreateUserPanel(JPanel panel, MarketplaceData data) {
        this.data = data;

        /**
         * Checks regularly if new assets have been added and
         * updates asset table when required.
         */
        ActionListener checkAssets = e -> {
            if (userTableModel.getRowCount() != data.getUserSize()) {
                getUserTableModel();
            }
        };
        new Timer(1000, checkAssets).start();

        userPanel = new JPanel();
        userPanel.setPreferredSize(new Dimension(1000, 1000));
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(400, 100));
        JLabel userTitle = new JLabel("Manage Users");
        userTitle.setFont(new Font("SansSerif", Font.BOLD, 50));
        titlePanel.add(userTitle);

        userPanel.add(Box.createVerticalStrut(50));
        userPanel.add(titlePanel);
        userPanel.add(Box.createVerticalStrut(50));
        userPanel.add(getUserPanel());
        userPanel.add(Box.createVerticalStrut(-150));
        userPanel.add(makeButtonsPanel());
        userPanel.add(Box.createVerticalStrut(20));

        addButtonListeners(new ButtonListener());

        userTableModel = new DefaultTableModel();
        userTableModel.addColumn("Username");
        userTableModel.addColumn("Password");
        userTableModel.addColumn("Account Type");
        userTableModel.addColumn("Organisation Unit");

        getUserTableModel();
        JTable userTable = createUserTable();

        JScrollPane userScroller = new JScrollPane(userTable);
        userScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userScroller.setMaximumSize(new Dimension(1000, 400));

        userPanel.add(userScroller);
        userPanel.add(Box.createVerticalStrut(20));

        panel.add(userPanel);
    }

    private JTable createUserTable() {
        userTable = new JTable(userTableModel);
        // gets the user and organisation of the selected row
        ListSelectionModel cellSelectionModel = userTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = userTable.getSelectedRow();
                if (row != -1) {
                    currentUser = userTable.getValueAt(row, 0).toString();
                    currentOrgUnit = userTable.getValueAt(row, 3).toString();
                }
            }
        });
        userTable.setAutoCreateRowSorter(true);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setRowHeight(30);
        // Sets the keybindings for the table
        createKeybindings(userTable);
        // Sets the cell editor for the user & organisation column
        TableColumnModel userColumnModel = userTable.getColumnModel();
        TableColumn userCol = userColumnModel.getColumn(0);
        TableColumn orgCol = userColumnModel.getColumn(3);
        orgCol.setCellEditor(new orgEditor());
        userCol.setCellEditor(new userEditor());
        // Adds a cell listener for when a user's details are updated
        Action orgAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                updatedUser = userTable.getValueAt(tcl.getRow(), 0).toString();
                currentPwd = hashPwd.HashPwd(userTable.getValueAt(tcl.getRow(), 1).toString());
                currentAccType = userTable.getValueAt(tcl.getRow(), 2).toString();
                updatedOrgUnit = userTable.getValueAt(tcl.getRow(), 3).toString();
                // Attempts to update the user if the organisation exists, otherwise ignores.
                if (data.getOrg(updatedOrgUnit).getOrgUnit() != null) {
                    data.updateUser(updatedUser, currentPwd, currentAccType, updatedOrgUnit, currentUser);
                    currentOrgUnit = updatedOrgUnit;
                    currentUser = updatedUser;
                }
            }
        };
        TableCellListener userTCL = new TableCellListener(userTable, orgAction);
        return userTable;
    }

    /**
     * Creates the table model for the user table
     */
    private void getUserTableModel() {
        userTableModel.setRowCount(0);
        userList = data.getUserModel();
        int userSize = data.getUserSize();

        for (int i = 0; i < userSize; i++) {
            User name = data.getUser(userList.getElementAt(i));
            Object[] userDetails = display(name);
            userTableModel.insertRow(i, userDetails);
        }
    }

    /**
     * Generates the user panel
     *
     * @return The panel containing text fields
     * for managing users.
     */
    private JPanel getUserPanel() {
        JPanel userDetails = new JPanel();
        userDetails.setLayout(new BoxLayout(userDetails, BoxLayout.X_AXIS));
        userDetails.add(Box.createVerticalStrut(20));
        JPanel userSettings = new JPanel();
        userSettings.setMaximumSize(new Dimension(100, 150));
        GroupLayout userLayout = new GroupLayout(userSettings);
        userSettings.setLayout(userLayout);
        userLayout.setAutoCreateGaps(true);
        userLayout.setAutoCreateContainerGaps(true);

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JLabel accTypeLabel = new JLabel("Account Type");
        JLabel orgUnitLabel = new JLabel("Organisation Unit");

        username = new JTextField(20);
        password = new JTextField(20);
        accType = new JTextField(20);
        orgUnit = new JTextField(20);

        GroupLayout.SequentialGroup hGroup = userLayout.createSequentialGroup();

        hGroup.addGroup(userLayout.createParallelGroup()
                .addComponent(usernameLabel)
                .addComponent(passwordLabel)
                .addComponent(accTypeLabel)
                .addComponent(orgUnitLabel));
        hGroup.addGroup(userLayout.createParallelGroup()
                .addComponent(username)
                .addComponent(password)
                .addComponent(accType)
                .addComponent(orgUnit));
        userLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = userLayout.createSequentialGroup();

        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(usernameLabel).addComponent(username));
        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(passwordLabel).addComponent(password));
        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(accTypeLabel).addComponent(accType));
        vGroup.addGroup(userLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(orgUnitLabel).addComponent(orgUnit));
        userLayout.setVerticalGroup(vGroup);


        userDetails.add(userSettings);
        userDetails.add(Box.createVerticalStrut(20));
        return userDetails;
    }

    /**
     * Creates the Add and Remove buttons for the users
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
     * Adds a listener to all the buttons.
     */
    private void addButtonListeners(ActionListener listener) {
        addButton.addActionListener(listener);
        removeButton.addActionListener(listener);
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
            }
        }

        /**
         * When the add button is pressed, check that all the fields contain
         * something. If they do, create a new user object, attempt to add it
         * to the data model and redraw the users table with the new data.
         */
        private void addPressed() {
            if (username.getText() != null && !username.getText().isBlank()
                    && password.getText() != null && !password.getText().isBlank()
                    && accType.getText() != null && !accType.getText().isBlank()
                    && orgUnit.getText() != null && !orgUnit.getText().isBlank()
            ) {

                if (data.getOrg(orgUnit.getText()).getOrgUnit() != null) {
                    if (data.getUser(username.getText()).getUser() == null) {
                        String hashedPwd = hashPwd.HashPwd(password.getText());
                        User u = new User(username.getText(), hashedPwd, accType.getText(), orgUnit.getText());
                        data.addUser(u);

                        // Redraws the table containing the new data
                        if (userTable.getCellEditor() != null) {
                            userTable.getCellEditor().stopCellEditing();
                        }
                        getUserTableModel();
                    } else {
                        JOptionPane.showMessageDialog(userPanel,
                                "Warning: Unable to add user. " +
                                        "This user already exists.");
                    }
                } else {
                    JOptionPane.showMessageDialog(userPanel,
                            "Warning: The organisation that you specified does " +
                                    "not exist. Please input an existing organisation.");
                }
            } else {
                JOptionPane.showMessageDialog(userPanel,
                        "Warning: Please enter valid inputs for " +
                                "all fields to add a new user.");
            }
            username.setText("");
            password.setText("");
            accType.setText("");
            orgUnit.setText("");
        }

        /**
         * When the remove button is pressed, check that a user is selected
         * If it is, remove a user object from the data model
         * and redraw the users table with the new data.
         */
        private void removePressed() {
            username.setText("");
            password.setText("");
            accType.setText("");
            orgUnit.setText("");
            data.removeUser(currentUser);

            if (currentUser.equals("")) {
                JOptionPane.showMessageDialog(userPanel,
                        "Warning: You must select a user to remove it.");
            } else {
                currentUser = "";
                currentPwd = "";
                currentAccType = "";
                currentOrgUnit = "";

                // Redraws the table containing the new data
                if (userTable.getCellEditor() != null) {
                    userTable.getCellEditor().stopCellEditing();
                }
                getUserTableModel();

            }
        }
    }

    /**
     * used to grab an object array of user details for table display
     *
     * @param user the user to grab details for
     * @return an object array containing the users details
     */
    private Object[] display(User user) {
        return new Object[] {
                user.getUser(),
                user.getPassword(),
                user.getAccType(),
                user.getOrgUnit()
        };
    }

    /**
     * Handles the editor for organisation column to prevent invalid values
     */
    private class orgEditor extends AbstractCellEditor implements TableCellEditor {

        JTextField textField = new JTextField();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textField.setText(String.valueOf(value));
            return textField;
        }

        // Checks to see if the updated organisation exists, if it doesn't
        // then a warning is displayed and the organisation is set back to its
        // previous state.
        @Override
        public Object getCellEditorValue() {
            String s = textField.getText();
            if (s.equals("")) {
                s = currentOrgUnit;
            }
            try
            {
                if (data.getOrg(s).getOrgUnit() == null) {
                    Double.parseDouble("throw");
                }
            }
            catch(NumberFormatException nfe)
            {
                JOptionPane.showMessageDialog(userPanel,
                        "Warning: Update failed. " +
                                "Please input an existing organisation.");
                return String.valueOf(currentOrgUnit);
            }
            return String.valueOf(s);
        }
    }

    /**
     * Handles the editor for user column to prevent invalid values
     */
    private class userEditor extends AbstractCellEditor implements TableCellEditor {

        JTextField textField = new JTextField();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textField.setText(String.valueOf(value));
            return textField;
        }

        // Checks to see if the updated user exists, if it does and it's not
        // the same name as the current user then a warning is displayed and the
        // user's name is set back to its previous state.
        @Override
        public Object getCellEditorValue() {
            String s = textField.getText();
            if(s.equals("")) {
                s = currentUser;
            }
            try
            {
                if (data.getUser(s).getUser() != null) {
                    if (!s.equalsIgnoreCase(currentUser)) {
                        Double.parseDouble("throw");
                    }
                }
            }
            catch(NumberFormatException nfe)
            {
                JOptionPane.showMessageDialog(userPanel,
                        "Warning: Update failed. " +
                                "The username you are trying to update already exists.");
                return String.valueOf(currentUser);
            }
            return String.valueOf(s);
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
