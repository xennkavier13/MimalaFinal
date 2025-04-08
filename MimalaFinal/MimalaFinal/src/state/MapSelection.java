package state;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL; // Import URL class
import java.util.Random;

public class MapSelection extends JPanel {
    private final JFrame frame;

    // Use forward slashes, relative to the classpath root
    // Assuming 'assets' is in a source/resource folder.
    // *** IMPORTANT: Ensure 'assets' folder is marked as a 'Resources Root' in your IDE
    // or placed in 'src/main/resources' if using Maven/Gradle. ***
    private final String[] mapPaths = {
            "assets/StageMap/FieldMap.png",    // Index 0 - Field
            "assets/StageMap/BuddhaMap.png",   // Index 1 - Buddha
            "assets/StageMap/ArenaMap.png",    // Index 2 - Arena
            "assets/StageMap/HauntedMap.png", // Index 3 - Haunted (Note the underscore, matching preview)
            "assets/StageMap/SnowMap.png",     // Index 4 - Snow
            "assets/StageMap/VolcanoMap.png"   // Index 5 - Volcano
    };
    // Map Names corresponding to mapPaths indices for easier lookup
    private final String[] mapNames = {
            "FieldMap", "BuddhaMap", "ArenaMap", "HauntedMap", "SnowMap", "VolcanoMap"
    };

    // Image paths for the map previews (relative to classpath root)
    private final String[] mapPreviewPaths = {
            "assets/StageMap/FieldMap.png",    // Preview for Field
            "assets/StageMap/BuddhaMap.png",   // Preview for Buddha
            "assets/StageMap/ArenaMap.png",    // Preview for Arena
            "assets/StageMap/HauntedMap.png", // Preview for Haunted
            "assets/StageMap/SnowMap.png",     // Preview for Snow
            "assets/StageMap/VolcanoMap.png"   // Preview for Volcano
    };

    private String selectedMapPath = null; // Store the *resource path* of the selected map
    private JLabel currentlySelectedMapLabel = null;
    private final Border selectionBorder = BorderFactory.createLineBorder(Color.YELLOW, 3);
    private final Border hoverBorder = BorderFactory.createLineBorder(Color.CYAN, 2);
    private final Border buttonHoverBorder = BorderFactory.createLineBorder(Color.GRAY, 2);
    private final Border buttonIdleBorder = BorderFactory.createLineBorder(Color.BLACK, 1);

    private final String firstPlayerSelection;
    private final String secondPlayerSelection;
    private final Random random = new Random();
    private final String gameMode;

    public MapSelection(JFrame frame, String firstPlayerSelection, String secondPlayerSelection, String mode) {
        this.frame = frame;
        this.firstPlayerSelection = firstPlayerSelection;
        this.secondPlayerSelection = secondPlayerSelection;
        this.gameMode = mode;
        // Add this line to help debug classpath issues if images still don't load:
        System.out.println("Attempting to load resources relative to classpath. Check project structure if paths starting with /assets/... fail.");
        // You can also print the working directory, but it's less relevant for resource loading:
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));


        setLayout(null);
        setPreferredSize(frame.getSize());
        setBackground(Color.WHITE); // Use a solid background color

        setupComponents();
    }

    private void setupComponents() {
        // Get panel dimensions (important for calculations)
        Dimension panelSize = getPreferredSize(); // Or getSize() if preferredSize isn't reliable yet
        int panelWidth = panelSize.width;
        int panelHeight = panelSize.height; // Useful for vertical centering if needed

        // --- Title ---

        int titleWidthEstimate = 400;
        // Center title horizontally, place it near the top vertically
        int titleY = 100; // Adjust vertical position as needed


        // --- Map Preview Buttons ---
        // Define map grid parameters
        int mapColumns = 3;
        int mapWidth = 320;  // Width of each map preview image/label
        int mapHeight = 200; // Height of each map preview image/label
        int spacingX = 40;   // Horizontal space between maps
        int spacingY = 40;   // Vertical space between map rows

        // Calculate the total width occupied by the map grid
        int totalGridWidth = (mapColumns * mapWidth) + ((mapColumns - 1) * spacingX);

        // Calculate the starting X coordinate to center the grid horizontally
        int startX = (panelWidth - totalGridWidth) / 2;

        // Define the starting Y coordinate (adjust for vertical centering)
        // Place it below the title, leaving some space
        int startY = titleY + 200; // Adjust 60 for spacing below title

        // Ensure startX and startY are not negative if panel is too small
        if (startX < 0) startX = 10; // Add some padding if calculated X is negative
        if (startY < 0) startY = 10;

        // Row 1
        createMapPreviewButton(mapNames[0], mapPreviewPaths[0], startX, startY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[1], mapPreviewPaths[1], startX + mapWidth + spacingX, startY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[2], mapPreviewPaths[2], startX + 2 * (mapWidth + spacingX), startY, mapWidth, mapHeight);

        // Row 2
        int secondRowY = startY + mapHeight + spacingY;
        createMapPreviewButton(mapNames[3], mapPreviewPaths[3], startX, secondRowY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[4], mapPreviewPaths[4], startX + mapWidth + spacingX, secondRowY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[5], mapPreviewPaths[5], startX + 2 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);


        // --- Select and Random Buttons ---
        int buttonWidth = 150;
        int buttonHeight = 50;
        // Position below the second row of maps
        int buttonY = secondRowY + mapHeight + 150; // Adjust 50 for spacing below maps
        int buttonSpacing = 20;

        // Calculate total width and starting X for the button group (already centers the group)
        int totalButtonsWidth = 2 * buttonWidth + buttonSpacing;
        int buttonsStartX = (panelWidth - totalButtonsWidth) / 2;

        // Ensure buttonsStartX is not negative
        if (buttonsStartX < 0) buttonsStartX = (panelWidth - buttonWidth)/2; // Center at least one button if panel too small


        JLabel selectButton = createActionButton("Select", buttonsStartX, buttonY, buttonWidth, buttonHeight);
        JLabel randomButton = createActionButton("rdm", buttonsStartX + buttonWidth + buttonSpacing, buttonY, buttonWidth, buttonHeight);

        // Select Button Action (Mouse Listeners remain the same)
        selectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { selectButton.setBorder(buttonHoverBorder); }
            @Override
            public void mouseExited(MouseEvent e) { selectButton.setBorder(buttonIdleBorder); }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedMapPath != null) {
                    System.out.println("Selected Map Resource Path: " + selectedMapPath);
                    transitionToGameScreen(selectedMapPath);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a map first.", "No Map Selected", JOptionPane.WARNING_MESSAGE);
                    System.out.println("Select clicked, but no map chosen.");
                }
            }
        });

        // Random Button Action (Mouse Listeners remain the same)
        randomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { randomButton.setBorder(buttonHoverBorder); }
            @Override
            public void mouseExited(MouseEvent e) { randomButton.setBorder(buttonIdleBorder); }
            @Override
            public void mouseClicked(MouseEvent e) {
                int randomIndex = random.nextInt(mapPaths.length);
                String randomMapPath = mapPaths[randomIndex];
                System.out.println("Random Map Resource Path: " + randomMapPath);
                transitionToGameScreen(randomMapPath);
            }
        });

        add(selectButton);
        add(randomButton);
    }


    // Creates the clickable map preview labels using Classpath Resources
    private void createMapPreviewButton(String mapName, String previewResourcePath, int x, int y, int width, int height) {
        ImageIcon previewIcon = null;
        JLabel button = null; // Declare button here to add it later

        try {
            // Construct the resource path starting with '/' (absolute from classpath root)
            String fullResourcePath = "/" + previewResourcePath;
            URL imgURL = getClass().getResource(fullResourcePath);

            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                // Scale image *after* loading successfully
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                previewIcon = new ImageIcon(scaledImage);
                button = new JLabel(previewIcon); // Create label with the loaded icon
            } else {
                System.err.println("Resource not found: " + fullResourcePath);
                // Create a placeholder label if image fails to load
                button = new JLabel("<html><center>Missing:<br>" + mapName + "</center></html>"); // Show map name on error
                button.setOpaque(true);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.RED);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
        } catch (Exception e) {
            System.err.println("Error loading image resource: " + previewResourcePath);
            e.printStackTrace();
            // Create a placeholder label on exception
            button = new JLabel("<html><center>Error:<br>" + mapName + "</center></html>");
            button.setOpaque(true);
            button.setBackground(Color.DARK_GRAY);
            button.setForeground(Color.ORANGE);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        }

        // Configure the button (either with image or error message)
        button.setBounds(x, y, width, height);

        // Only add mouse listener if the image loaded successfully
        if (previewIcon != null) {
            final JLabel finalButton = button; // Need final variable for lambda/inner class
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (currentlySelectedMapLabel != finalButton) {
                        finalButton.setBorder(hoverBorder);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (currentlySelectedMapLabel != finalButton) {
                        finalButton.setBorder(null);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentlySelectedMapLabel != null) {
                        currentlySelectedMapLabel.setBorder(null);
                    }
                    finalButton.setBorder(selectionBorder);
                    currentlySelectedMapLabel = finalButton;
                    // Store the *resource path* from mapPaths corresponding to this button's mapName
                    selectedMapPath = mapPaths[mapNameToIndex(mapName)];

                    System.out.println("Clicked Map: " + mapName + ", Resource Path: " + selectedMapPath);
                }
            });
        }

        add(button);
    }


    // Helper to create styled text buttons (Select/Random)
    private JLabel createActionButton(String text, int x, int y, int width, int height) {
        JLabel buttonLabel = new JLabel(text);
        buttonLabel.setBounds(x, y, width, height);
        buttonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonLabel.setVerticalAlignment(SwingConstants.CENTER);
        buttonLabel.setFont(new Font("Arial", Font.BOLD, 24));
        buttonLabel.setBorder(buttonIdleBorder);
        buttonLabel.setOpaque(false); // Keep background transparent
        buttonLabel.setForeground(Color.BLACK);
        return buttonLabel;
    }


    // Finds the index in mapPaths/mapNames based on the map name
    private int mapNameToIndex(String mapName) {
        for (int i = 0; i < mapNames.length; i++) {
            if (mapNames[i].equals(mapName)) {
                return i;
            }
        }
        System.err.println("Warning: Map name not found in mapNames array: " + mapName);
        return -1;
    }

    // Centralized method to transition to the game screen
    // Pass the selected RESOURCE PATH to the next screen
    private void transitionToGameScreen(String mapResourcePath) {
        if (mapResourcePath == null) {
            System.err.println("Error: Attempted to transition with null mapResourcePath.");
            return;
        }
        // Ensure GameScreen knows how to handle a resource path (it might also need to use getResource)
        // Or, GameScreen might just need the name/index, and it looks up its own background resource path. Adjust as needed.
        frame.setContentPane(new GameScreen(
                frame,
                firstPlayerSelection,
                secondPlayerSelection,
                mapResourcePath,
                gameMode// Pass the resource path
        ));
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Handles painting the background color set by setBackground()

        // Optional: Draw background image using resource loading.
        // Uncomment and adjust path if needed. Ensure SampleBg.png is also in assets/StageMap/

        try {
            String bgResourcePath = "/assets/StageMap/MapSelect.png"; // Relative to classpath root
            URL bgURL = getClass().getResource(bgResourcePath);
            if(bgURL != null) {
                Image bgImage = new ImageIcon(bgURL).getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.err.println("Background resource not found: " + bgResourcePath);
                // Background color will be shown as fallback
            }
        } catch (Exception e) {
            System.err.println("Error loading background image resource");
            e.printStackTrace();
            // Background color will be shown as fallback
        }

    }
}