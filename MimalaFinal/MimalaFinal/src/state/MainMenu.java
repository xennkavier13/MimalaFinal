package state;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private final ImageIcon mainMenuGif;
    private final JFrame frame;

    public MainMenu() {
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // Make the window without decoration
        frame.setSize(1920, 1080);
        frame.setContentPane(this); // Set this panel as the content pane
        frame.setVisible(true); // Make the frame visible

        mainMenuGif = new ImageIcon("C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\MainMenuScreen\\MainMenu.gif");
        setLayout(null);
        setFocusable(true); // Make the panel focusable
        requestFocusInWindow(); // Request focus for key events
        setPreferredSize(new Dimension(1920, 1080));  // Explicitly setting the panel size
        setupButtons();
    }

    private void setupButtons() {
        // Transition to ModeSelection
        JLabel startButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Start\\Start_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Start\\Start_On.png",
                450, () -> {
                    // Transition to ModeSelection
                    frame.setContentPane(new ModeSelection(frame));
                    frame.revalidate();
                    frame.repaint();
                }
        );

        JLabel endButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\End\\End_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\End\\End_On.png",
                530, () -> System.exit(0)
        );

        JLabel creditsButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Credits\\Credits_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Credits\\Credits_On.png",
                600, () -> System.out.println("Credits Clicked")
        );

        add(startButton);
        add(endButton);
        add(creditsButton);
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
        g.drawImage(mainMenuGif.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}
