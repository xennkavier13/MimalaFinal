package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class AstridraScreen extends CharacterSelectionScreen {

    public AstridraScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/AstridraSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Astridra";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/AstridraInfo.png";
    }
}
