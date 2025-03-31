package state;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game");
            MainMenu mainMenu = new MainMenu();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Adjust as needed
            frame.setContentPane(mainMenu);
            frame.setVisible(true);
        });
    }
}