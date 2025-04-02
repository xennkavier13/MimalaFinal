package state.CharacterScreen;

import javax.swing.*;
import java.awt.*;

public class IgnisveilScreen extends JPanel{

    public IgnisveilScreen(JFrame frame) {
        setLayout(new BorderLayout());

        // Load the looping GIF
        ImageIcon astridraGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\IgnisveilSelection.gif");
        JLabel gifLabel = new JLabel(astridraGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);

        add(gifLabel, BorderLayout.CENTER);
    }
}
