package state.UI;

import state.GameScreen;
import state.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class Pause extends JPanel {

    private final JFrame frame;
    private final Image pauseBackground;
    private final GameScreen gameScreen;

    private final String resumeOffPath = "assets/PauseScreen/PAUSE/Resume/Resume_Off.png";
    private final String resumeHoverPath = "assets/PauseScreen/PAUSE/Resume/Resume_Hover.png";
    private final String exitOffPath = "assets/PauseScreen/PAUSE/Exit/Exit_off.png";
    private final String exitHoverPath = "assets/PauseScreen/PAUSE/Exit/Exit_hover.png";

    public Pause(JFrame frame, GameScreen gameScreen) {
        this.frame = frame;
        this.gameScreen = gameScreen;
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        pauseBackground = new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/PauseScreen_nobutton.png")).getImage();

        // Resume Button
        JLabel resumeButton = createButton(
                resumeOffPath, resumeHoverPath,
                850, 567, // off position
                418, 548, // hover position
                () -> {
                    frame.setContentPane(gameScreen);
                    frame.revalidate();
                    frame.repaint();
                },
                this
        );

        // Exit Button
        JLabel exitButton = createButton(
                exitOffPath, exitHoverPath,
                585, 718, // off position
                418, 700, // hover position
                () -> {
                    JPanel newScreen = new MainMenu(frame);
                    newScreen.setOpaque(true);
                    newScreen.setBackground(Color.BLACK);
                    SwingUtilities.invokeLater(() -> {
                        frame.getContentPane().removeAll();
                        frame.setBackground(Color.BLACK);
                        frame.setContentPane(newScreen);
                        frame.revalidate();
                        frame.repaint();
                    });
                },
                this
        );

        add(resumeButton);
        add(exitButton);
    }

    private JLabel createButton(String offPath, String hoverPath,
                                int offX, int offY, int hoverX, int hoverY,
                                Runnable action, Component componentForTracker) {

        final ImageIcon offIcon = loadImageIconResource("/" + offPath, -1, -1, componentForTracker);
        final ImageIcon hoverIcon = loadImageIconResource("/" + hoverPath, -1, -1, componentForTracker);

        if (offIcon == null) {
            System.err.println("Failed to load off icon: " + offPath);
            return new JLabel("Load Error");
        }

        final int offWidth = offIcon.getIconWidth();
        final int offHeight = offIcon.getIconHeight();
        final int hoverWidth = hoverIcon.getIconWidth();
        final int hoverHeight = hoverIcon.getIconHeight();

        final JLabel button = new JLabel(offIcon);
        button.setBounds(offX, offY, offWidth, offHeight);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
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
        g.drawImage(pauseBackground, 0, 0, getWidth(), getHeight(), this);
    }
}
