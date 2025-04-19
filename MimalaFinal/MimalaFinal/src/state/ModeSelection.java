package state;

import state.CharacterScreen.Select.CharacterSelection;
import state.CharacterScreen.Select.SecondPlayerSelection;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;


public class ModeSelection extends JPanel {
    private final ImageIcon modeSelectionBg;
    private final JFrame frame;
    private Clip music;

    public ModeSelection(JFrame gameFrame) {
        this.frame = gameFrame;
        modeSelectionBg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/ModeSelectBG.gif");
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
//        playMusic("/assets/MainMenuScreen/Sounds/MimalaMainMenuMusic.wav");
    }

    private void setupButtons() {
        // Back button (unchanged)
        JLabel backButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png",
                "MimalaFinal/MimalaFinal/src/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png",
                50, 57,
                () -> {
                    frame.setContentPane(new MainMenu(frame));
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(backButton);

        // New PVP Button
        JLabel pvpButton = createHoverOnlyButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVP_hover.png",
                88, 206, // SetBounds — adjust as needed
                () -> {
                    frame.setContentPane(new util.Player1Name(frame, () -> {
                        frame.setContentPane(new util.Player2Name(frame, () -> {
                            frame.setContentPane(new CharacterSelection(frame, "PVP"));
                            frame.revalidate();
                            frame.repaint();
                        }));
                        frame.revalidate();
                        frame.repaint();
                    }));
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(pvpButton);

        // New PVE Button
        JLabel pveButton = createHoverOnlyButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVE_hover.png",
                704, 206, // SetBounds — adjust as needed
                () -> {
                    frame.setContentPane(new util.PlayerName(frame, () -> {
                        frame.setContentPane(new CharacterSelection(frame, "PVC"));
                        frame.revalidate();
                        frame.repaint();
                    }));
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(pveButton);

        // New Arcade Button
        JLabel arcadeButton = createHoverOnlyButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/Arcade_hover.png",
                1320, 206, // SetBounds — adjust as needed
                () -> {
                    // Placeholder logic — replace with actual action if needed
                    JOptionPane.showMessageDialog(frame, "Arcade mode coming soon!");
                }
        );
        add(arcadeButton);
    }

    private JLabel createHoverOnlyButton(String hoverPath, int x, int y, Runnable action) {
        // Transparent default image (1x1 transparent pixel or similar)
        ImageIcon defaultIcon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(defaultIcon);
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setIcon(defaultIcon);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return button;
    }




    private JLabel createButton(String offPath, String hoverPath, int x, int y, Runnable action) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
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

    private void playMusic(String filePath) {
        try {
            // Load from resources (classpath)
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.err.println("Music file not found in resources: " + filePath);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    private void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(modeSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}