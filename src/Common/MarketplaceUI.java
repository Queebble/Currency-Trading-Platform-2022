package Common;

import Common.Constructors.HashPwd;
import Common.Constructors.Login;
import Common.Panels.AdminPanel;
import Common.Panels.MarketplacePanel;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Initiates user interface for the Marketplace Trading application. Listeners for
 * the login panel component are included as an inner class of this class.
 */
public class MarketplaceUI extends JFrame {

    private String loginOutcome;
    private JButton loginButton;
    private JTextField userTextField;
    private JTextField passwordField;
    private JFrame mainFrame;
    private JFrame loginFrame;
    private JTabbedPane mainPane;
    private JPanel admin;
    private JPanel marketplace;
    private MarketplacePanel marketplacePanel;
    private AdminPanel adminPanel;

    MarketplaceData data;

    /**
     * Constructor sets up user interface, adds listeners and displays.
     *
     * @param data The underlying data/model class the UI needs.
     */
    public MarketplaceUI(MarketplaceData data) {
        this.data = data;

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(30,32,38));
            UIManager.put("info", new Color(30,32,38));
            UIManager.put("nimbusBase", new Color(0, 0, 0));
            UIManager.put("nimbusBlueGrey", new Color(0, 0, 0));
            UIManager.put("nimbusDisabledText", new Color(42,45,53));
            UIManager.put("nimbusLightBackground", new Color(42,45,53));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(5,99,211));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");
        }

        LoginFrame();

        addButtonListeners(new ButtonListener());
    }

    /**
     * Constructs the main GUI
     */
    private void mainGUI(String user) {

        mainFrame = new JFrame("Electronic Asset Trading Platform");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Panels for main frame
        mainPane = new JTabbedPane();
        marketplace = new JPanel();
        admin = new JPanel();

        // Constructs each panel
        marketplacePanel = new MarketplacePanel(marketplace, data, user);
        adminPanel = new AdminPanel(admin, data);

        // Adds each panel to the main pane
        mainPane.add("Marketplace", marketplace);
        mainPane.add("Admin Settings", admin);

        mainFrame.getContentPane().add(mainPane);
        mainFrame.setPreferredSize(new Dimension(1920, 1080));
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(false);

    }

    /**
     * Setup Login Frame
     */
    private void LoginFrame() {

        JLabel userLabel = new JLabel("USERNAME");
        JLabel passwordLabel = new JLabel("PASSWORD");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("LOGIN");

        loginFrame = new JFrame();

        userLabel.setBounds(50,150,100,30);
        passwordLabel.setBounds(50,220,100,30);
        userTextField.setBounds(150,150,150,30);
        passwordField.setBounds(150,220,150,30);
        loginButton.setBounds(135,300,100,30);

        loginFrame.setLayout(null);

        loginFrame.add(userLabel);
        loginFrame.add(passwordLabel);
        loginFrame.add(userTextField);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);

        loginFrame.setTitle("Login Form");
        loginFrame.setVisible(true);
        loginFrame.setBounds(10,10,370,600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getRootPane().setDefaultButton(loginButton);
        loginFrame.setResizable(false);

    }

    /**
     * Adds a listener to the login button
     */
    private void addButtonListeners(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /**
     * Handles button listeners and action events for login frame
     */
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source == loginButton) {
                loginPressed();
            }
        }

        /**
         * Processes login details when login button pressed.
         */
        private void loginPressed() {
            String userText;
            String pwdText;
            userText = userTextField.getText();
            pwdText = passwordField.getText();

            HashPwd hashPwd = new HashPwd();
            String hashPwdText = hashPwd.HashPwd(pwdText);

            // fetches all relevant information to process login details
            String user = data.getUser(userText).getUser();
            String pwd = data.getUser(userText).getPassword();
            String accType = data.getUser(userText).getAccType();

            // constructs a new login instance and passes login + database details
            // and returns the outcome of the check, changing UI accordingly.
            Login login = new Login();
            String loginOutcome = login.Login(userText, hashPwdText, user, pwd, accType);
            if (loginOutcome.equals("Success") || loginOutcome.equals("SuccessAdmin")) {
                JOptionPane.showMessageDialog(loginFrame, "Login Successful");
                mainGUI(userText);
                loginFrame.setVisible(false);
                mainFrame.setVisible(true);
                if (!loginOutcome.equals("SuccessAdmin")) {
                    mainPane.remove(admin);
                }
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password");
            }
        }
    }


}