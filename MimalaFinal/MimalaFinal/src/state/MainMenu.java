package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JPanel {
    private ImageIcon mainMenuGif;
    private JLabel startButton, endButton, creditsButton;

    public MainMenu() {
        mainMenuGif = new ImageIcon("C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\MainMenu.gif");
        setLayout(null); // Allows manual positioning of buttons

        setupButtons();
        setupFrame();
    }

    private void setupButtons() {
        startButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Start\\Start_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Start\\Start_On.png",
                970, 450 // Adjust these coordinates manually
        );

        endButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\End\\End_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\End\\End_On.png",
                970, 530 // Adjust these coordinates manually
        );

        creditsButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Credits\\Credits_Off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\Buttons\\Credits\\Credits_On.png",
                970, 600 // Adjust these coordinates manually
        );

        endButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0); // Exit when End Game is clicked
            }
        });

        add(startButton);
        add(endButton);
        add(creditsButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int x, int y) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
            }
        });
        return button;
    }

    private void setupFrame() {
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(frame);

        frame.setContentPane(this);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mainMenuGif.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}