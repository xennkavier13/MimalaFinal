package Main;

import javax.swing.*;
import state.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure UI updates run on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Mimala");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true); // Remove title bar and borders
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
            frame.setResizable(false); // Prevent resizing

            // Initialize MainMenu and add it to the frame
            MainMenu mainMenu = new MainMenu(frame);
            frame.setContentPane(mainMenu);
            frame.setVisible(true);
        });
    }
}
