package state.CharacterScreen;

import state.CharacterSelection;

import javax.swing.*;
import java.awt.*;

public class VexmorthScreen extends JPanel{
    private final ImageIcon vexmorthGif;
    private final JFrame frame;

    public VexmorthScreen(JFrame frame) {
        this.frame = frame;
        vexmorthGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\VexmorthSelection.gif");
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private void setupButtons() {
        int backButtonX = 100;
        int backButtonY = 200;
        int continueButtonX = 70;
        int continueButtonY = 500;

        JLabel backButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreenButtons\\Back\\Back_off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreenButtons\\Back\\Back_hover.png",
                backButtonX, backButtonY, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVP")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        JLabel continueButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreenButtons\\Continue\\Continue_off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreenButtons\\Continue\\Continue_hover.png ",
                continueButtonX, continueButtonY, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVC")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        add(backButton);
        add(continueButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, int backButtonY, Runnable action) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        //button.setBounds(970, y, offIcon.getIconWidth(), offIcon.getIconHeight());

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
        g.drawImage(vexmorthGif.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

