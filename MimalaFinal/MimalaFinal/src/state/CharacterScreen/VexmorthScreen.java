package state.CharacterScreen;

import javax.swing.*;

public class VexmorthScreen extends CharacterSelectionScreen {

    public VexmorthScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/VexmorthSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Vexmorth";
    }
}
