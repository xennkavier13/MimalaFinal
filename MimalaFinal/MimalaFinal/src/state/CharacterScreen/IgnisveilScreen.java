package state.CharacterScreen;

import javax.swing.*;

public class IgnisveilScreen extends CharacterSelectionScreen {

    public IgnisveilScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/IgnisveilSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Ignisveil";
    }
}
