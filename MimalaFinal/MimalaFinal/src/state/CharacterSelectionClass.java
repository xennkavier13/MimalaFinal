package state;

import javax.swing.*;
import java.awt.*;

public class CharacterSelectionClass extends JPanel {
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;

    public CharacterSelectionClass() {
        setPreferredSize(new Dimension(1920, 1080));
        setSize(1920, 1080);
        setLayout(null); // Allows manual positioning of components

        JLabel label = new JLabel("Fullscreen JPanel Example");
        label.setBounds(800, 500, 300, 50);
        label.setForeground(Color.WHITE);
        add(label);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fullscreen Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        CharacterSelectionClass panel = new CharacterSelectionClass();
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
