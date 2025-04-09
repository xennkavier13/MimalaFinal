package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class AuricannonScreen extends CharacterSelectionScreen {

    public AuricannonScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/AuricannonSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Auricannon";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/AuricannonInfo.png";
    }
}
