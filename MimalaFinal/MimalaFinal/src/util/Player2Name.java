package util;

// No need to import ModeSelection here, goes back to Player1Name

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // <<< Import ActionEvent
import java.awt.event.KeyEvent;  // <<< Import KeyEvent

public class Player2Name extends JPanel {

    public static String player2Name = "Player 2"; // Default value

    private JTextField nameField;
    private JButton submitButton;
    private Image backgroundImg;
    // Removed: public Player2Name(JFrame frame, Object pvp) { } // Unused constructor

    public Player2Name(JFrame frame, Runnable onSubmit) {
        setLayout(null);
        setFocusable(true); // <<< Ensure panel can get focus

        // --- Load background image --- (Use getResource)
        try {
            String bgPath = "assets/InputName/player2_entername.png";
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
        nameField.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10)); // Simulate vertical center
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

        // Inside Player2Name.java constructor

        submitButton.addActionListener(e -> {
            String enteredName = nameField.getText().trim(); // Trim leading/trailing spaces

            // --- MODIFIED CHECKS ---
            if (enteredName.isEmpty()) {
                // Check if empty first
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else if (enteredName.contains(" ")) {
                // THEN check if it contains any spaces
                JOptionPane.showMessageDialog(this, "Name cannot contain spaces. Please enter a valid name.");
            } else {
                // Only if BOTH checks pass: Assign and proceed
                player2Name = enteredName; // Set static variable
                System.out.println("Player 2 Name: " + player2Name);
                onSubmit.run(); // Proceed to character selection
            }
            // --- END OF MODIFIED CHECKS ---
        });
        add(submitButton);

        // --- Setup Key Bindings ---
        setupEscapeKeyBinding(frame, onSubmit); // <<< CALL new method, pass onSubmit for context

        // Request focus AFTER components are added
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // <<< NEW METHOD for ESC key binding >>>
    // Pass onSubmit Runnable from Player1Name screen context if needed for navigation logic
    private void setupEscapeKeyBinding(JFrame frame, Runnable p1SubmitCallback) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionKey = "goBack";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Escape pressed on Player2Name screen. Going back to Player1Name.");
                // Go back to Player1Name screen
                // We need the onSubmit Runnable that *Player1Name* would execute
                // If Player1Name always passes the same type of Runnable (e.g., one that shows Player2Name),
                // we can potentially recreate Player1Name here.
                // This assumes Player1Name's onSubmit leads here.
                frame.setContentPane(new Player1Name(frame, p1SubmitCallback));
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