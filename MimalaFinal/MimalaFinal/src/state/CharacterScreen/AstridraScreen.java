package state.CharacterScreen;

import state.CharacterSelection;
import state.MapSelection;
import state.SecondPlayerSelection; // Import needed for PVP mode

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL; // Import URL
import java.util.Random; // Import Random

public class AstridraScreen extends JPanel {

    private final JFrame frame;
    private final String mode; // Store the game mode ("PVC" or "PVP")
    private final String characterName = "Astridra"; // Character for this screen

    // List of all characters for random AI selection in PVC mode
    // Ensure this list matches the one in CharacterSelection/CharacterInfoScreen
    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };

    // Resource path for the background GIF (use forward slashes!)
    private final String gifResourcePath = "/assets/CharacterSelectionScreen/CharacterScreen/AstridraSelection.gif";
    private ImageIcon astridraGif; // Load lazily or in constructor

    public AstridraScreen(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode; // Store the passed mode

        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null); // Use null layout for manual positioning

        // Load background GIF using resource path
        this.astridraGif = loadImage(gifResourcePath);
        if (this.astridraGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF: " + gifResourcePath);
            // Handle error, maybe show solid color + error text in paintComponent
        }

        setupButtons();
    }

    // Helper method to load ImageIcons using classpath resources
    private ImageIcon loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            System.err.println(this.getClass().getSimpleName() + ": Provided resource path is null or empty.");
            return null;
        }
        try {
            // Ensure path starts with '/' for absolute path from classpath root
            String absolutePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
            URL imgURL = getClass().getResource(absolutePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println(this.getClass().getSimpleName() + ": Resource not found: " + absolutePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName() + ": Error loading image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }


    private void setupButtons() {
        int panelWidth = getPreferredSize().width;
        int panelHeight = getPreferredSize().height;
        int bottomMargin = 70; // Increased margin from bottom
        int sideMargin = 70;   // Increased margin from sides

        // Define Resource Paths for buttons (MUST start with '/' assuming 'assets' is at classpath root)
        String backOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png";
        String backHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png";
        String contOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Continue/Continue_off.png";
        String contHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Continue/Continue_hover.png";

        // --- Back Button ---
        // Attempt to load the icon first to get dimensions for positioning
        ImageIcon backIcon = loadImage(backOffPath);
        int backW = (backIcon != null) ? backIcon.getIconWidth() : 100; // Default width if load fails
        int backH = (backIcon != null) ? backIcon.getIconHeight() : 40; // Default height
        int backButtonX = sideMargin;
        int backButtonY = panelHeight - bottomMargin - backH; // Position based on height

        JLabel backButton = createButton(backOffPath, backHoverPath, backButtonX, backButtonY, () -> {
            System.out.println("Back button clicked on AstridraScreen");
            // Go back to Character Selection, passing the original mode
            frame.setContentPane(new CharacterSelection(frame, this.mode));
            frame.revalidate();
            frame.repaint();
        });
        add(backButton); // Add to panel

        // --- Continue Button ---
        ImageIcon contIcon = loadImage(contOffPath);
        int contW = (contIcon != null) ? contIcon.getIconWidth() : 100; // Default width
        int contH = (contIcon != null) ? contIcon.getIconHeight() : 40; // Default height
        int continueButtonX = panelWidth - sideMargin - contW; // Position from right edge
        int continueButtonY = panelHeight - bottomMargin - contH; // Align vertically with back button

        JLabel continueButton = createButton(contOffPath, contHoverPath, continueButtonX, continueButtonY, () -> {
            System.out.println("Continue button clicked on AstridraScreen. Mode: " + this.mode);
            String player1Selection = this.characterName; // "Astridra"

            // Check mode and navigate accordingly
            if ("PVC".equalsIgnoreCase(this.mode)) {
                System.out.println("Mode PVC detected. Selecting opponent and going to Map Selection.");
                String player2Selection = selectRandomCharacterForPVE(player1Selection);
                System.out.println("Player 2 (AI) selected: " + player2Selection);

                // Navigate to MapSelection, passing P1 and P2 choices
                // Ensure MapSelection constructor is: MapSelection(JFrame, String, String)
                frame.setContentPane(new MapSelection(frame, player1Selection, player2Selection));
                frame.revalidate();
                frame.repaint();

            } else if ("PVP".equalsIgnoreCase(this.mode)) {
                System.out.println("Mode PVP detected. Going to Second Player Selection.");
                // Navigate to SecondPlayerSelection, passing P1 choice
                // Ensure SecondPlayerSelection constructor is: SecondPlayerSelection(JFrame, String)
                frame.setContentPane(new SecondPlayerSelection(frame, player1Selection));
                frame.revalidate();
                frame.repaint();
            } else {
                // Handle unknown mode if necessary
                System.err.println("Unknown mode encountered on AstridraScreen: " + this.mode);
                JOptionPane.showMessageDialog(frame, "Cannot continue: Invalid game mode '" + this.mode + "'", "Mode Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(continueButton); // Add to panel
    }

    // Updated button creation method using resource loading and proper bounds setting
    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath);

        if (offIcon == null) {
            System.err.println("Failed to load mandatory button icon: " + offResourcePath);
            JLabel errorLabel = new JLabel("BTN ERR"); // Simple text fallback
            errorLabel.setBounds(x, y, 100, 30); // Placeholder size
            errorLabel.setForeground(Color.RED);
            errorLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
            // Still attach the action to the error label so it's clickable
            errorLabel.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { action.run(); }
            });
            return errorLabel;
        }

        // Use hoverIcon if available, otherwise fallback to offIcon for hover state
        ImageIcon finalHoverIcon = (hoverIcon != null) ? hoverIcon : offIcon;

        JLabel button = new JLabel(offIcon);
        // Set bounds based on the loaded 'off' icon's dimensions
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setIcon(finalHoverIcon); }
            @Override
            public void mouseExited(MouseEvent e) { button.setIcon(offIcon); }
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return button;
    }

    // Method to select a random character for PVE, different from Player 1
    private String selectRandomCharacterForPVE(String player1Choice) {
        Random random = new Random();
        String aiChoice;
        if (ALL_CHARACTER_NAMES.length <= 1) {
            return player1Choice; // Or handle error appropriately
        }
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice)); // Ensure AI is different from Player 1
        return aiChoice;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background GIF if loaded successfully
        if (astridraGif != null) {
            g.drawImage(astridraGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback if GIF failed to load
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Background GIF not loaded!", 50, 50);
            g.drawString("Path: " + gifResourcePath, 50, 80);
        }
    }
}