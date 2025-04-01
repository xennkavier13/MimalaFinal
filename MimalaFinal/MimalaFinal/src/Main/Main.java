package Main;

import javax.swing.*;
import state.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Mimala");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the content pane to MainMenu
            frame.setContentPane(new MainMenu(frame));

            // Set undecorated to remove title bar and borders
            frame.setUndecorated(true);

            // Set the window to fullscreen
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Set the frame to occupy the entire screen
            frame.setVisible(true);

            // Optionally, make the frame not resizable
            frame.setResizable(false);
        });
    }
}
