package util;

import javax.swing.*;
import java.awt.*;

public class PlayerName extends JPanel {

    public static String playerName = ""; // Used in leaderboard

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;

    public PlayerName(JFrame frame, Runnable onSubmit) {
        setLayout(null); // Manual layout
        setFocusable(true);
        requestFocusInWindow();

        // Load background image
        backgroundImg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/InputName/player_entername.png").getImage();

        // --- Name input field ---
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 40));
        nameField.setHorizontalAlignment(JTextField.CENTER); // Center text horizontally
        nameField.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10)); // Vertical alignment simulation
        nameField.setBounds(645, 513, 630, 81); // Adjust if needed
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
            playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                System.out.println("Player Name (PVC): " + playerName);
                onSubmit.run(); // Move to next screen
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
