package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Player1Name extends JPanel {

    public static String player1Name = ""; // To be used later for leaderboard

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;

    public Player1Name(JFrame frame, Runnable onSubmit) {
        setLayout(null); // Allows manual positioning
        setFocusable(true);
        requestFocusInWindow();

        // Load background image
        backgroundImg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/player1_entername.png").getImage();

        // --- Name input field ---
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 40));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBounds(645, 513, 630, 81); // Adjust as needed
        add(nameField);

        // --- Submit button ---
        submitButton = new JButton();
        submitButton.setBounds(830, 620, 260, 80); // Adjust as needed
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(false);

        ImageIcon offIcon = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/submit_off.png");
        ImageIcon hoverIcon = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/submit_hover.png");

        submitButton.setIcon(offIcon);
        submitButton.setRolloverIcon(hoverIcon);

        submitButton.addActionListener(e -> {
            player1Name = nameField.getText().trim();
            if (!player1Name.isEmpty()) {
                System.out.println("Player 1 Name: " + player1Name);
                onSubmit.run(); // Call next screen
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            }
        });

        add(submitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
