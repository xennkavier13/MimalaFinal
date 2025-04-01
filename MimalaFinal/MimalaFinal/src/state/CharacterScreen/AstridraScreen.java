package state.CharacterScreen;

import javax.swing.*;
import java.awt.*;

public class AstridraScreen extends JPanel{

    public AstridraScreen(JFrame frame) {
        setLayout(new BorderLayout());

        // Load the looping GIF
        ImageIcon astridraGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\AstridraSelection.gif");
        JLabel gifLabel = new JLabel(astridraGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);

        add(gifLabel, BorderLayout.CENTER);
    }
}
