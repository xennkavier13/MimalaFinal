package state.UI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import util.GameLog; // Use GameLog to load data

public class ArcadeLeaderboard extends JPanel {
    private final ImageIcon background;
    private final JFrame frame;
    private final JPanel previousScreen;
    private final String arcadeLeaderboardFile = "arcade_leaderboard.txt";

    public ArcadeLeaderboard(JFrame frame, JPanel previousScreen) {
        this.frame = frame;
        this.previousScreen = previousScreen;
        this.setLayout(null);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setBackground(Color.BLACK); // Fallback

        background = loadIcon("/assets/Leaderboards/ArcadeLeaderboard_mainmenu.png"); // Adjust path if needed

        loadAndDisplayLeaderboard(); // Load data first
        setupButtons(); // Add buttons on top
    }

    private void setupButtons() {
        // (Button setup code remains the same as your previous ArcadeLeaderboard version)
        JLabel backButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png",
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png",
                50, 57,
                () -> {
                    frame.setContentPane(previousScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(backButton);

        JLabel leftButton = createButton(
                "/assets/Leaderboards/Buttons/LeftBtn_off.png",
                "/assets/Leaderboards/Buttons/LeftBtn_hover.png",
                65, 477, // Adjust coordinates as necessary
                () -> {
                    JPanel nextScreen = new PVELeaderboard(frame, previousScreen);
                    frame.setContentPane(nextScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(leftButton);

        // Example: Add Right button if needed
        JLabel rightButton = createButton(
                "/assets/Leaderboards/Buttons/RightBtn_off.png",
                "/assets/Leaderboards/Buttons/RightBtn_hover.png",
                1749, 476, // <<< COORDINATES FROM PVELeaderboard Right Button
                () -> {
                    JPanel nextScreen = new PVPLeaderboard(frame, previousScreen); // Assuming PVPLeaderboard exists
                    frame.setContentPane(nextScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(rightButton);
    }

    // Updated to match PVELeaderboard's layout logic
    private void loadAndDisplayLeaderboard() {
        GameLog tempLog = new GameLog();
        tempLog.ensureFileExists(arcadeLeaderboardFile);
        Map<String, Integer> leaderboardData = tempLog.loadSimpleLeaderboardData(arcadeLeaderboardFile);


        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(leaderboardData.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // --- Layout Settings Copied from PVELeaderboard ---
        int startY = 400;
        int rowHeight = 90;
        int rankX = 420;
        int nameX = 900; // Adjusted from PVE for potentially longer names? Review needed.
        int winsX = 1460;
        int bottomMargin = 24; // Margin between rows

        Font font = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 25); // Same font as PVE
        Color textColor = Color.WHITE;

        // --- Display Header (Optional - adapt from PVE if desired) ---
        // Font headerFont = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 20);
        // Color headerColor = new Color(200, 200, 200);
        // int headerY = startY - rowHeight; // Position header above first data row
        // add(createLabel("Rank", rankX, headerY, headerFont, headerColor));
        // add(createLabel("Player Name", nameX, headerY, headerFont, headerColor));
        // add(createLabel("Wins", winsX, headerY, headerFont, headerColor));


        // --- Loop and display top 5 entries ---
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            if (rank > 5) { // Limit to top 5 like PVELeaderboard
                break;
            }

            String rankStr = String.valueOf(rank);
            String playerName = entry.getKey();
            String winsStr = String.valueOf(entry.getValue());

            // Calculate Y position using PVE's logic
            int currentY = startY + (rank - 1) * (rowHeight + bottomMargin);

            // Create and add labels for the current row using PVE's createLabel
            // Note: PVE's createLabel sets width to 400, height to 60. Adjust if needed.
            add(createLabel(rankStr, rankX, currentY, font, textColor));
            add(createLabel(playerName, nameX, currentY, font, textColor));
            add(createLabel(winsStr, winsX, currentY, font, textColor));

            rank++;
        }

        // No explicit refresh needed here as components are added before panel is shown typically.
    }

    // Copied from PVELeaderboard
    private Font loadCustomFont(String fontPath, int fontSize) {
        try {
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                System.err.println("Font file not found at: " + fontPath);
                return new Font("Monospaced", Font.PLAIN, fontSize);
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, (float)fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + fontPath);
            e.printStackTrace();
            return new Font("Monospaced", Font.PLAIN, fontSize);
        }
    }

    // Copied from PVELeaderboard
    private JLabel createLabel(String text, int x, int y, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setBounds(x, y, 400, 60); // Using PVE's fixed bounds - adjust width if names are cut off
        return label;
    }

    // createButton method (ensure it's identical to the working one in PVELeaderboard)
    private JLabel createButton(String offPath, String hoverPath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadIcon(offPath);
        ImageIcon hoverIcon = loadIcon(hoverPath);
        JLabel button = new JLabel(); // Initialize empty

        if (offIcon != null && offIcon.getIconWidth() > 0) {
            button.setIcon(offIcon);
            button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());
        } else {
            System.err.println("Failed to load button icon: " + offPath);
            button.setText("?");
            button.setBounds(x, y, 50, 30);
            button.setForeground(Color.RED);
            button.setOpaque(true);
            button.setBackground(Color.DARK_GRAY);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (hoverIcon != null && hoverIcon.getIconWidth() > 0) {
                    button.setIcon(hoverIcon);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (offIcon != null && offIcon.getIconWidth() > 0) {
                    button.setIcon(offIcon);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null && background.getIconWidth() > 0) {
            g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("BG Missing", 50, 50);
        }
    }

    // loadIcon method (ensure it's identical to the working one in PVELeaderboard)
    private ImageIcon loadIcon(String path) {
        // Ensure path starts with "/" if relative to classpath root
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Missing image at " + path);
            return null; // Return null if missing
        }
    }
}