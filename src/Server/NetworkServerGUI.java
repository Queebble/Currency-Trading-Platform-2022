package Server;

import Common.MarketplaceDataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Initialises Network server
 */
public class NetworkServerGUI {

    /**
     * Initialises the network server and generates a GUI
     * to indicate server is running on a given port
     * @param args an array of command-line arguments for the application
     */
     public static void main(String[] args) {
        NetworkServer server = new NetworkServer();
        SwingUtilities.invokeLater(() -> createAndShowGUI(server));
        try {
            server.start();
        } catch (IOException e) {
            // In the case of an exception, show an error message and terminate
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null, e.getMessage(),
                        "Error starting server", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    private static void createAndShowGUI(NetworkServer server) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Network server for Electronic Asset Trading Platform");
        JButton shutdownButton = new JButton("Shut down server");
        // This button will simply close the dialog. CLosing the dialog
        // will shut down the server
        shutdownButton.addActionListener(e -> dialog.dispose());

        // When the dialog is closed, shut down the server
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                server.shutdown();
            }
        });

        // Create labels/panels to show server info
        JLabel serverLabel = new JLabel("   Server running on port " + NetworkServer.getPort());

        // Add the button and label frames to the dialog
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(shutdownButton, BorderLayout.SOUTH);
        dialog.getContentPane().add(serverLabel, BorderLayout.CENTER);
        dialog.pack();

        // Display dialog on bottom right of screen
        dialog.setSize(200, 100);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - dialog.getWidth();
        int y = (int) rect.getMaxY() - dialog.getHeight();
        dialog.setLocation(x, y-40);

        // Show the dialog
        dialog.setVisible(true);
    }

}