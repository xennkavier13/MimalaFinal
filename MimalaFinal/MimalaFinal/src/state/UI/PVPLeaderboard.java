package state.UI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class PVPLeaderboard extends JPanel {
    private final ImageIcon background;
    private final JFrame frame;
    private final JPanel previousScreen;

    public PVPLeaderboard(JFrame frame, JPanel previousScreen) {
        this.frame = frame;
        this.previousScreen = previousScreen;
        this.setLayout(null);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setBackground(Color.CYAN); // Temporary background color to ensure the panel shows

        loadAndDisplayLeaderboard();

        background = loadIcon("assets/Leaderboards/PVPLeaderboard_mainmenu.png");

        setupButtons();
    }

    private void setupButtons() {
        JLabel backButton = createButton(
                "assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png",
                "assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png",
                50, 57,
                () -> {
                    frame.setContentPane(previousScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(backButton);

        JLabel rightButton = createButton(
                "assets/Leaderboards/Buttons/RightBtn_off.png",
                "assets/Leaderboards/Buttons/RightBtn_hover.png",
                1749, 476,
                () -> {
                    JPanel nextScreen = new PVELeaderboard(frame, previousScreen);
                    frame.setContentPane(nextScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(rightButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadIcon(offPath);
        ImageIcon hoverIcon = loadIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setIcon(offIcon);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return button;
    }

    private void loadAndDisplayLeaderboard() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("pvp_leaderboard.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (Exception e) {
            System.out.println("Error reading leaderboard: " + e.getMessage());
        }

        System.out.println("Leaderboard Data: " + lines); // Debugging: Check if data is loaded correctly

        int startY = 400; // starting Y position for first row
        int rowHeight = 90; // vertical spacing between rows (adjust as needed)

        // Manually set the positions of rank, name, and wins
        int rankX = 420; // X position for rank (adjust as necessary)
        int nameX = 900; // X position for name (adjust as necessary)
        int winsX = 1460; // X position for wins (adjust as necessary)

        int bottomMargin = 24; // Adjust the bottom margin between each row

        Font font = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 25); // font size for better visibility (non-bold)
        Color textColor = Color.WHITE;

        // Loop through and display the top 5 rows
        int rank = 1;
        for (String line : lines) {
            // Skip lines that don't contain valid leaderboard data
            if (line.isEmpty() || line.startsWith("Rank") || line.startsWith("-")) {
                continue;
            }

            // Split by spaces (use regular expression to match any whitespace)
            String[] parts = line.split("\\s+");
            if (parts.length >= 3) {
                String playerName = parts[1];
                String wins = parts[2];

                // Create and position the rank label
                JLabel rankLabel = createLabel(String.valueOf(rank), rankX, startY + (rank - 1) * (rowHeight + bottomMargin), font, textColor);
                // Create and position the player name label
                JLabel nameLabel = createLabel(playerName, nameX, startY + (rank - 1) * (rowHeight + bottomMargin), font, textColor);
                // Create and position the wins label
                JLabel winsLabel = createLabel(wins, winsX, startY + (rank - 1) * (rowHeight + bottomMargin), font, textColor);

                // Add the labels to the panel
                add(rankLabel);
                add(nameLabel);
                add(winsLabel);

                if (rank > 5) break;
                else rank++; // Increment the rank for the next row
            }
        }

        // Refresh the panel
        revalidate();
        repaint();
    }

    private Font loadCustomFont(String fontPath, int fontSize) {
        try {
            // Load the custom font from the file
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(Font.PLAIN, fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont); // Register the font to be used in the app
            return customFont;
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, fontSize); // Fallback to default font if custom font is not found
        }
    }


    private JLabel createLabel(String text, int x, int y, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setBounds(x, y, 400, 60); // Adjusted label height
        return label;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Missing image at " + path);
        return new ImageIcon();
    }
}
