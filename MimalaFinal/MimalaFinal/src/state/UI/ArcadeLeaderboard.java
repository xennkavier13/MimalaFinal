package state.UI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import util.GameLog;

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
        this.setBackground(Color.BLACK);

        background = loadIcon("/assets/Leaderboards/ArcadeLeaderboard_mainmenu.png");

        loadAndDisplayLeaderboard();
        setupButtons();
    }

    private void setupButtons() {
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
                65, 477,
                () -> {
                    JPanel nextScreen = new PVELeaderboard(frame, previousScreen);
                    frame.setContentPane(nextScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(leftButton);

        JLabel rightButton = createButton(
                "/assets/Leaderboards/Buttons/RightBtn_off.png",
                "/assets/Leaderboards/Buttons/RightBtn_hover.png",
                1749, 476,
                () -> {
                    JPanel nextScreen = new PVPLeaderboard(frame, previousScreen);
                    frame.setContentPane(nextScreen);
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(rightButton);
    }

    private void loadAndDisplayLeaderboard() {
        GameLog tempLog = new GameLog();
        tempLog.ensureFileExists(arcadeLeaderboardFile);
        Map<String, Integer> leaderboardData = tempLog.loadSimpleLeaderboardData(arcadeLeaderboardFile);

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(leaderboardData.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int startY = 400;
        int rowHeight = 90;
        int bottomMargin = 24;

        // Define centers
        int rankCenterX = 420;
        int nameCenterX = 960;
        int winsCenterX = 1470;

        Font font = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 25);
        Color textColor = Color.WHITE;

        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            if (rank > 5) break;

            String rankStr = String.valueOf(rank);
            String playerName = entry.getKey();
            String winsStr = String.valueOf(entry.getValue());

            int yPosition = startY + (rank - 1) * (rowHeight + bottomMargin);

            JLabel rankLabel = createCenteredLabel(rankStr, rankCenterX, yPosition, font, textColor);
            JLabel nameLabel = createCenteredLabel(playerName, nameCenterX, yPosition, font, textColor);
            JLabel winsLabel = createCenteredLabel(winsStr, winsCenterX, yPosition, font, textColor);

            add(rankLabel);
            add(nameLabel);
            add(winsLabel);

            rank++;
        }
    }

    private JLabel createCenteredLabel(String text, int centerX, int y, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);

        FontMetrics metrics = label.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int x = centerX - (textWidth / 2);

        label.setBounds(x, y, textWidth, 60);
        return label;
    }

    private Font loadCustomFont(String fontPath, int fontSize) {
        try {
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                System.err.println("Font file not found at: " + fontPath);
                return new Font("Monospaced", Font.PLAIN, fontSize);
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, (float) fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + fontPath);
            e.printStackTrace();
            return new Font("Monospaced", Font.PLAIN, fontSize);
        }
    }

    private JLabel createButton(String offPath, String hoverPath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadIcon(offPath);
        ImageIcon hoverIcon = loadIcon(hoverPath);
        JLabel button = new JLabel();

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

    private ImageIcon loadIcon(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Missing image at " + path);
            return null;
        }
    }
}
