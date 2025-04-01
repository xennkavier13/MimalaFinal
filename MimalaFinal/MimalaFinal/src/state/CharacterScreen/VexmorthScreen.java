package state.CharacterScreen;

import javax.swing.*;
import java.awt.*;

public class VexmorthScreen extends JPanel{

    public VexmorthScreen(JFrame frame) {
        setLayout(new BorderLayout());

        // Load the looping GIF
        ImageIcon pyrotharGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\VexmorthSelection.gif");
        JLabel gifLabel = new JLabel(pyrotharGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);

        add(gifLabel, BorderLayout.CENTER);
    }
}
