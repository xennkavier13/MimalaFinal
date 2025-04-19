package Main;

import javax.swing.*;
import state.UI.IntroScreen;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mimala Intro");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setResizable(false);
            frame.setContentPane(new IntroScreen(frame));
            frame.setVisible(true);
        });
    }
}