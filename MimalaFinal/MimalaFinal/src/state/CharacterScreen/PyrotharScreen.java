package state.CharacterScreen;

import javax.swing.*;

public class PyrotharScreen extends CharacterSelectionScreen {

    public PyrotharScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/PyrotharSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Pyrothar";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/PyrotharInfo.png";
    }
}