package Common.Panels;

import Common.MarketplaceData;

import javax.swing.*;
import java.awt.*;

/**
 * Initialises Admin Panel for the Marketplace Trading application's admin view.
 */
public class AdminPanel {

    private ListModel userList;

    /**
     * Constructs the admin panel display for UI
     *
     * @param adminPanel reference to admin panel in main UI.
     * @param data the data source to pass into each panel component
     */
    public AdminPanel(JPanel adminPanel, MarketplaceData data) {

        adminPanel.setLayout(new BorderLayout());
        JTabbedPane settingsPane = new JTabbedPane(JTabbedPane.LEFT);

        JPanel userPanel = new JPanel();
        JPanel orgPanel = new JPanel();
        JPanel assetPanel = new JPanel();

        CreateUserPanel createUserPanel = new CreateUserPanel(userPanel, data);
        CreateOrgPanel createOrgPanel = new CreateOrgPanel(orgPanel, data);
        CreateAssetPanel createAssetPanel = new CreateAssetPanel(assetPanel, data);

        settingsPane.add("Users", userPanel);
        settingsPane.add("Organisation Units", orgPanel);
        settingsPane.add("Assets", assetPanel);

        adminPanel.add(settingsPane);
    }
}
