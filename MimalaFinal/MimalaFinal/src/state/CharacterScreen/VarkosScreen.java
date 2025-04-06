package state.CharacterScreen;

import javax.swing.*;

public class VarkosScreen extends CharacterSelectionScreen {

    public VarkosScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/VarkosSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Varkos";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/VarkosInfo.png";
    }
}
