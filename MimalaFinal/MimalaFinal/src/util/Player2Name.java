package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Player2Name extends JPanel {

    public static String player2Name = ""; // To be used in leaderboard logic later

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;

    public Player2Name(JFrame frame, Runnable onSubmit) {
        setLayout(null); // Manual positioning
        setFocusable(true);
        requestFocusInWindow();

        // Load background image
        backgroundImg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/player2_entername.png").getImage();

        // --- Name input field ---
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 40));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBounds(645, 513, 630, 81); // Same positioning as Player 1
        add(nameField);

        // --- Submit button ---
        submitButton = new JButton();
        submitButton.setBounds(830, 620, 260, 80); // Same positioning
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(false);

        ImageIcon offIcon = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/submit_off.png");
        ImageIcon hoverIcon = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/submit_hover.png");

        submitButton.setIcon(offIcon);
        submitButton.setRolloverIcon(hoverIcon);

        submitButton.addActionListener(e -> {
            player2Name = nameField.getText().trim();
            if (!player2Name.isEmpty()) {
                System.out.println("Player 2 Name: " + player2Name);
                onSubmit.run(); // Proceed to character selection
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            }
        });

        add(submitButton);
    }

    public Player2Name(JFrame frame, Object pvp) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
