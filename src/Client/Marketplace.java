package Client;

import Common.MarketplaceData;
import Common.MarketplaceUI;

import javax.swing.*;

/**
 * Invokes the Marketplace Trading Application
 */
public class Marketplace {

    /**
     * Create the GUI. For thread safety
     * invoke from the event-dispatching thread
     */
    private static void createAndShowGUI() {
        new MarketplaceUI(new MarketplaceData(new NetworkDataSource()));
    }

    /**
     * The application's entry point
     *
     * @param args an array of command-line arguments for the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Marketplace::createAndShowGUI);
    }
}