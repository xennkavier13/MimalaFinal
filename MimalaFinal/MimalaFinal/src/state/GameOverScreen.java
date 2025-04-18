package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import static util.AudioManager.stopMusic;

public class GameOverScreen extends JPanel {

    private final JFrame frame;
    private JLabel backgroundLabel = null;
    private final boolean player1Won;

    private final String rematchOffPath = "assets/GameOver/Rematch/Rematch_Off.png";
    private final String rematchHoverPath = "assets/GameOver/Rematch/Rematch_Hover.png";
    private final String menuOffPath = "assets/GameOver/ReturnToMainMenu/MainMenu_off.png";
    private final String menuHoverPath = "assets/GameOver/ReturnToMainMenu/MainMenu_hover.png";
    private final String leaderboardOffPath = "";
    private final String p1Name, p2Name, mapPath, gameMode;
    private final String bgPath = "assets/GameOver/GameOver.png";

    public GameOverScreen(JFrame frame, boolean player1Won, String p1, String p2, String map, String mode) {
        this.frame = frame;
        this.player1Won = player1Won;
        this.p1Name = p1;
        this.p2Name = p2;
        this.mapPath = map;
        this.gameMode = mode;

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        setupBackgroundGif(this);
        setupButtons();
    }

    private void setupBackgroundGif(Component componentForTracker) {
        if (bgPath != null) {
            ImageIcon gifIcon = loadImageIconResource("/" + bgPath, -1, -1, componentForTracker);
            if (gifIcon != null) {
                backgroundLabel = new JLabel(gifIcon);
                backgroundLabel.setBounds(0, 0, 1920, 1080);
                add(backgroundLabel);
            } else {
                System.err.println("Failed to load background GIF: " + bgPath);
            }
        }
    }

    private void setupButtons() {
        Component componentForTracker = this;

        // Rematch button positions
        int rematchOffX = 820, rematchOffY = 568;
        int rematchHoverX = 418, rematchHoverY = 548;

        JLabel rematchButton = createButton(
                rematchOffPath,
                rematchHoverPath,
                rematchOffX, rematchOffY,
                rematchHoverX, rematchHoverY,
                () -> {
                    System.out.println("Rematch button clicked");
                    if (p1Name != null && p2Name != null && mapPath != null && gameMode != null) {
                        frame.setContentPane(new GameScreen(frame, p1Name, p2Name, mapPath, gameMode));
                    } else {
                        frame.setContentPane(new MainMenu(frame));
                    }
                    frame.revalidate();
                    frame.repaint();
                },
                componentForTracker
        );

        // Menu button positions
        int menuOffX = 585, menuOffY = 718;
        int menuHoverX = 418, menuHoverY = 700;

        JLabel menuButton = createButton(
                menuOffPath,
                menuHoverPath,
                menuOffX, menuOffY,
                menuHoverX, menuHoverY,
                () -> {
                    System.out.println("Main Menu button clicked");
                    frame.setContentPane(new MainMenu(frame));
                    frame.revalidate();
                    frame.repaint();
                },
                componentForTracker
        );

        if (rematchButton != null) {
            add(rematchButton);
            stopMusic();
        }
        if (menuButton != null) {
            add(menuButton);
        }

        if (backgroundLabel != null && backgroundLabel.getParent() == this) {
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }
    }

    private JLabel createButton(String offPath, String hoverPath,
                                int offX, int offY, int hoverX, int hoverY,
                                Runnable action, Component componentForTracker) {

        ImageIcon offIcon = loadImageIconResource("/" + offPath, -1, -1, componentForTracker);
        ImageIcon hoverIcon = loadImageIconResource("/" + hoverPath, -1, -1, componentForTracker);

        if (offIcon == null) {
            System.err.println("Error loading button icon: " + offPath);
            JLabel errorLabel = new JLabel("Btn Load Err");
            errorLabel.setBounds(offX, offY, 200, 50);
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            return errorLabel;
        }

        if (hoverIcon == null) {
            System.err.println("Warning: Failed to load hover icon: " + hoverPath + ". Using normal icon instead.");
            hoverIcon = offIcon;
        }

        int offWidth = offIcon.getIconWidth();
        int offHeight = offIcon.getIconHeight();
        int hoverWidth = hoverIcon.getIconWidth();
        int hoverHeight = hoverIcon.getIconHeight();

        JLabel button = new JLabel(offIcon);
        button.setBounds(offX, offY, offWidth, offHeight);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon finalHoverIcon = hoverIcon;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(finalHoverIcon);
                button.setBounds(hoverX, hoverY, hoverWidth, hoverHeight);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
                button.setBounds(offX, offY, offWidth, offHeight);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }
        });

        return button;
    }

    private ImageIcon loadImageIconResource(String resourcePath, int width, int height, Component component) {
        URL imgURL = getClass().getResource(resourcePath);
        if (imgURL == null) {
            System.err.println("Image not found: " + resourcePath);
            return null;
        }
        ImageIcon icon = new ImageIcon(imgURL);
        if (width > 0 && height > 0) {
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return icon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundLabel == null || backgroundLabel.getParent() != this) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String message = "Background GIF failed to load.";
            if (backgroundLabel == null) {
                message += " (Label is null)";
            } else {
                message += " (Label not added)";
            }
            g.drawString(message, 50, 50);
        }
    }
}
