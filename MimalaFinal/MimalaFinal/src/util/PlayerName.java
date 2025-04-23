package util;

import state.ModeSelection; // Import the screen to go back to

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
// Remove unused imports: import java.io.File; import java.io.IOException;

public class PlayerName extends JPanel {

    // This static variable is used for PVC/Arcade leaderboards
    public static String playerName = ""; // Default value or load previously saved?

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;
    private String mode; // To distinguish between PVC and Arcade

    public PlayerName(JFrame frame, String mode, Runnable onSubmit) {
        this.mode = mode; // "PVC" or "Arcade"
        setLayout(null); // Manual layout
        setFocusable(true); // Ensure panel can get focus for key bindings

        // --- Load background image ---
        try {
            // Use a generic name or mode-specific if available
            String bgPath = "assets/InputName/player_entername.png";
            // Example: if (mode.equals("Arcade")) bgPath = "assets/InputName/arcade_entername.png";
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


        // --- MODIFIED ACTION LISTENER ---
        submitButton.addActionListener(e -> {
            String enteredName = nameField.getText().trim(); // Use local variable

            if (enteredName.isEmpty()) {
                // Check if empty first
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else if (enteredName.contains(" ")) {
                // THEN check if it contains any spaces
                JOptionPane.showMessageDialog(this, "Name cannot contain spaces. Please enter a valid name.");
            } else {
                // Only if BOTH checks pass: Assign static variable and proceed
                PlayerName.playerName = enteredName; // Set the static variable for this class
                System.out.println("Player Name set for " + mode + ": '" + PlayerName.playerName + "'");
                onSubmit.run(); // Move to the next screen (CharacterSelection)
            }
        });
        // --- END OF MODIFIED ACTION LISTENER ---

        add(submitButton);

        // --- Setup Key Bindings ---
        setupEscapeKeyBinding(frame);

        // Request focus AFTER components are added and panel is setup
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // Escape Key Binding Method (no changes needed here)
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
                frame.setContentPane(new ModeSelection(frame));
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    // Helper to load icons safely (no changes needed here)
    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Missing image at " + path);
        return null;
    }

    // paintComponent (no changes needed here)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Background Missing", 50, 50);
        }
    }
}