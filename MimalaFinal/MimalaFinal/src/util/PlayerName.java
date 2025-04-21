package util;

import state.ModeSelection; // <<< Import the screen to go back to

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // <<< Import ActionEvent
import java.awt.event.KeyEvent;  // <<< Import KeyEvent

public class PlayerName extends JPanel {

    public static String playerName = ""; // Used in leaderboard

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;
    private String mode; // To distinguish between PVC and Arcade

    public PlayerName(JFrame frame, String mode, Runnable onSubmit) {
        this.mode = mode; // "PVC" or "Arcade"
        setLayout(null); // Manual layout
        setFocusable(true); // <<< Ensure panel can get focus for key bindings

        // --- Load background image --- (Ensure paths are correct / use getResource)
        try {
            String bgPath = "assets/InputName/player_entername.png"; // Default path
            // Consider mode-specific backgrounds if they exist
            if (mode.equals("Arcade")) {
                // bgPath = "assets/InputName/arcade_entername.png"; // Example
            }
            java.net.URL imgUrl = getClass().getClassLoader().getResource(bgPath);
            if (imgUrl != null) {
                backgroundImg = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Background image not found: " + bgPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e);
        }
        // --- End background loading ---


        // --- Name input field ---
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 40));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        nameField.setBounds(645, 513, 630, 81);
        add(nameField);

        // --- Submit button ---
        submitButton = new JButton();
        submitButton.setBounds(830, 620, 260, 80);
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(false);

        // Use getResource for icons
        ImageIcon offIcon = loadIcon("assets/InputName/submit_off.png");
        ImageIcon hoverIcon = loadIcon("assets/InputName/submit_hover.png");

        if (offIcon != null) submitButton.setIcon(offIcon);
        if (hoverIcon != null) submitButton.setRolloverIcon(hoverIcon);


        submitButton.addActionListener(e -> {
            String enteredName = nameField.getText().trim(); // Use local variable
            if (!enteredName.isEmpty()) {
                playerName = enteredName; // Set static variable only on success
                System.out.println("Player Name (" + mode + "): " + playerName);
                onSubmit.run(); // Move to the next screen
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            }
        });
        add(submitButton);

        // --- Setup Key Bindings ---
        setupEscapeKeyBinding(frame); // <<< CALL a new method

        // Request focus AFTER components are added and panel is setup
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // <<< NEW METHOD for ESC key binding >>>
    private void setupEscapeKeyBinding(JFrame frame) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionKey = "goBack";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Escape pressed on PlayerName screen. Going back to ModeSelection.");
                // Go back to ModeSelection screen
                frame.setContentPane(new ModeSelection(frame));
                frame.revalidate();
                frame.repaint();
            }
        });
    }
    // Helper to load icons safely
    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Missing image at " + path);
        return null; // Return null if not found
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY); // Fallback background
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Background Missing", 50, 50);
        }
    }
}