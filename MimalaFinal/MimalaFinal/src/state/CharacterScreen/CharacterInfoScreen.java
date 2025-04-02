package state; // Or your specific UI sub-package

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random; // Import Random for AI selection

// Import the screens we need to navigate to
import state.CharacterSelection;
import state.SecondPlayerSelection; // Assuming this exists for PVP
import state.MapSelection;        // Assuming this exists

public class CharacterInfoScreen extends JPanel {

    private final JFrame frame;
    private final String characterName; // The character being displayed/potentially selected
    private final String mode;          // "PVP" or "PVE"

    // Character names needed for PVE random selection
    // Make static final if the list is constant and shared logic
    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };

    // --- Asset paths ---
    private final String INFO_ASSET_BASE = "/assets/CharacterInfo/";
    private final String PORTRAIT_FILE = "/portrait.png";
    private final String DESC_FILE = "/description.txt";
    private final String COMMON_BG = "/assets/CharacterInfo/common_background.png";

    public CharacterInfoScreen(JFrame frame, String characterName, String mode) {
        this.frame = frame;
        this.characterName = characterName;
        this.mode = mode;

        System.out.println("Showing info for: " + characterName);

        setLayout(null);
        setPreferredSize(frame.getSize());

        loadAndDisplayCharacterInfo();
        addNavigationButtons();
    }

    private void loadAndDisplayCharacterInfo() {
        // ... (loadAndDisplayCharacterInfo method remains the same as before) ...
        // --- Load Character Specific Assets using getResource() ---
        ImageIcon portraitIcon = loadImage(INFO_ASSET_BASE + characterName + PORTRAIT_FILE);
        String description = loadDescription(INFO_ASSET_BASE + characterName + DESC_FILE);

        // --- Create and Position Components ---
        int panelWidth = getPreferredSize().width;
        int panelHeight = getPreferredSize().height;

        if (portraitIcon != null) {
            JLabel portraitLabel = new JLabel(portraitIcon);
            int portraitX = (panelWidth - portraitIcon.getIconWidth()) / 2;
            portraitLabel.setBounds(portraitX, 100, portraitIcon.getIconWidth(), portraitIcon.getIconHeight());
            add(portraitLabel);
        } else {
            JLabel missingPortrait = new JLabel("Portrait Missing");
            missingPortrait.setBounds(panelWidth / 2 - 100, 100, 200, 50);
            missingPortrait.setForeground(Color.RED);
            missingPortrait.setHorizontalAlignment(SwingConstants.CENTER);
            add(missingPortrait);
        }

        JTextArea descriptionArea = new JTextArea(description != null ? description : "Description not found.");
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setForeground(Color.WHITE);

        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBounds(panelWidth / 4, 500, panelWidth / 2, 200); // Adjust as needed
        descriptionScrollPane.getViewport().setOpaque(false);
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.setBorder(null);
        add(descriptionScrollPane);

        JLabel nameLabel = new JLabel(characterName.toUpperCase());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 36));
        nameLabel.setForeground(Color.YELLOW);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBounds(0, 50, panelWidth, 50);
        add(nameLabel);
    }

    private void addNavigationButtons() {
        int panelWidth = getPreferredSize().width;
        int panelHeight = getPreferredSize().height;
        int buttonWidth = 120;
        int buttonHeight = 40;
        int bottomMargin = 50;
        int sideMargin = 50;

        // --- Back Button ---
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setBounds(sideMargin, panelHeight - bottomMargin - buttonHeight, buttonWidth, buttonHeight);
        backButton.addActionListener(e -> {
            // Go back to Character Selection screen WITHOUT selecting
            frame.setContentPane(new CharacterSelection(frame, mode));
            frame.revalidate();
            frame.repaint();
        });
        add(backButton);

        // --- Continue Button ---
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 20));
        continueButton.setBounds(panelWidth - sideMargin - buttonWidth, panelHeight - bottomMargin - buttonHeight, buttonWidth, buttonHeight);
        continueButton.addActionListener(e -> {
            // --- Action: Confirm selection and proceed ---
            String player1Selection = this.characterName; // The character shown on this screen IS P1's choice
            System.out.println("Player 1 selected: " + player1Selection);

            if (mode.equals("PVP")) {
                System.out.println("Mode: PVP. Proceeding to Second Player Selection...");
                // Assuming SecondPlayerSelection screen exists and takes frame and P1 choice
                frame.setContentPane(new SecondPlayerSelection(frame, player1Selection));

            } else { // PVE mode
                System.out.println("Mode: PVE. Selecting random opponent and proceeding to Map Selection...");
                String player2Selection = selectRandomCharacterForPVE(player1Selection);
                System.out.println("Player 2 (AI) selected: " + player2Selection);
                // Assuming MapSelection screen exists and takes frame, P1 choice, P2 choice
                frame.setContentPane(new MapSelection(frame, player1Selection, player2Selection));
            }
            // Update the frame
            frame.revalidate();
            frame.repaint();
        });
        add(continueButton);
    }

    // Method to select a random character for PVE, different from Player 1
    private String selectRandomCharacterForPVE(String player1Choice) {
        Random random = new Random();
        String aiChoice;
        if (ALL_CHARACTER_NAMES.length <= 1) {
            // Handle edge case where there's only one character total
            return player1Choice; // Or throw an error, or return null
        }
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice)); // Ensure AI is different from Player 1
        return aiChoice;
    }


    // Helper method to load images using resources (same as before)
    private ImageIcon loadImage(String resourcePath) {
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Info Screen: Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Info Screen: Error loading image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to load text description (same as before)
    private String loadDescription(String resourcePath) {
        try (java.io.InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Info Screen: Resource not found: " + resourcePath);
                return "Description file not found.";
            }
            java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            System.err.println("Info Screen: Error loading description resource: " + resourcePath);
            e.printStackTrace();
            return "Error loading description.";
        }
    }

    // paintComponent method remains the same as before
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon bgIcon = loadImage(COMMON_BG);
        if (bgIcon != null) {
            g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Background image missing: " + COMMON_BG, 20, 20);
        }
    }
}