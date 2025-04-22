package state.CharacterScreen;

// Ensure this import points to the correct superclass file
import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class AstridraScreen extends CharacterSelectionScreen {


    public AstridraScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        // Pass all received arguments to the superclass constructor
        super(frame, characterName, mode, firstPlayerSelection);
    }

    // --- Overridden methods to provide character-specific details ---
    // These methods are called by the superclass (CharacterSelectionScreen)

    @Override
    protected String getGifPath() {
        // Path to the specific background/animation for Astridra's screen
        return "/assets/CharacterSelectionScreen/CharacterScreen/AstridraSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        // Returns the name associated with this specific screen
        return "Astridra";
    }

    @Override
    protected String getInfoImagePath() {
        // Path to the specific info panel image for Astridra
        return "/assets/CharacterSelectionScreen/CharacterInfos/AstridraInfo.png";
    }
}