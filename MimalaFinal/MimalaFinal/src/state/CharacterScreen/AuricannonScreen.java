package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class AuricannonScreen extends CharacterSelectionScreen {

    public AuricannonScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        super(frame, characterName, mode, firstPlayerSelection);
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
