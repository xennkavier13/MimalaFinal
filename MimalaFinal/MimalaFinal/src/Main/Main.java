package Main;

import javax.swing.*;
import state.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Fighting Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MainMenu(frame)); // Set the content pane to MainMenu
            frame.pack(); // Pack the frame to fit the components
            frame.setLocationRelativeTo(null); // Center the window on the screen
            frame.setVisible(true); // Make the frame visible
        });
    }
}