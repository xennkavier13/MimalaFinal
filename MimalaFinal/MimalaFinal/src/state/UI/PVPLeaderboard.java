package state.UI;

import javax.swing.*;
import java.awt.*;

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
        this.setBackground(Color.BLACK);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Missing image at " + path);
        return new ImageIcon();
    }
}
