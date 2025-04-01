package state;

import javax.swing.*;
import java.awt.*;

public class ModeSelection extends JPanel {
    private final ImageIcon modeSelectionBg;
    private final JFrame frame;

    public ModeSelection(JFrame gameFrame) {
        this.frame = gameFrame;
        modeSelectionBg = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\ModeSelectionScreen\\ModeSelectionPixelated.gif");
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private void setupButtons() {
        // Redirect to Character Selection
        JLabel pvpButton = createButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\ModeSelectionScreen\\Buttons\\PVP\\PVP_off.png",
                "MimalaFinal\\MimalaFinal\\src\\assets\\ModeSelectionScreen\\Buttons\\PVP\\PVP_on.png",
                450, () -> {
                    frame.setContentPane(new CharacterSelection(frame)); // Redirect to Character Selection
                    frame.revalidate();
                    frame.repaint();
                }
        );

        // Redirect to Character Selection
        JLabel pvcButton = createButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\ModeSelectionScreen\\Buttons\\PVC\\PVC_off.png",
                "MimalaFinal\\MimalaFinal\\src\\assets\\ModeSelectionScreen\\Buttons\\PVC\\PVC_on.png",
                530, () -> {
                    frame.setContentPane(new CharacterSelection(frame)); // Redirect to Character Selection
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
        button.setBounds(970, y, offIcon.getIconWidth(), offIcon.getIconHeight());

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
