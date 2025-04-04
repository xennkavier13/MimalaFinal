package state;

import javax.swing.*;
import java.awt.*;

public class ModeSelection extends JPanel {
    private final ImageIcon modeSelectionBg;
    private final JFrame frame;

    public ModeSelection(JFrame gameFrame) {
        this.frame = gameFrame;
        modeSelectionBg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/MainMenuScreen/MainMenuBG.gif");
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private void setupButtons() {
        JLabel pvpButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVP/PVP_off.png",
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVP/PVP_hover.png",
                450, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVP")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        JLabel pvcButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PvAI/PvAI_off.png",
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PvAI/PvAI_hover.png",
                530, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVC")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        add(pvpButton);
        add(pvcButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(680, y, offIcon.getIconWidth(), offIcon.getIconHeight());

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
        g.drawImage(modeSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}