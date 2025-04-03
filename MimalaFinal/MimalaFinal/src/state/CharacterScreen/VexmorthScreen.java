package state.CharacterScreen;

import javax.swing.JFrame;

public class VexmorthScreen extends CharacterSelectionScreen {

    // Constructor used by CharacterSelection (for P1)
    public VexmorthScreen(JFrame frame, String mode) {
        super(frame, mode); // Calls the constructor that sets firstPlayerSelection to null
    }

    // Constructor used by SecondPlayerSelection (for P2)
    public VexmorthScreen(JFrame frame, String mode, String firstPlayerSelection) {
        super(frame, mode, firstPlayerSelection); // Calls the constructor that stores P1's choice
    }

    @Override
    protected String getGifPath() {
        // Return the correct path for Vexmorth's GIF
        return "/assets/CharacterSelectionScreen/CharacterScreen/VexmorthSelection.gif"; // Example path - ADJUST AS NEEDED
    }

    @Override
    protected String getCharacterName() {
        return "Vexmorth";
    }

    // You might override setupButtons() if Vexmorth has unique button images/positions
}