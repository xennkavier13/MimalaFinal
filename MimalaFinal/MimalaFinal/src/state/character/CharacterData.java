package state.character;

 // Make sure Skill class is imported
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class CharacterData {

    private final String characterName;
    private final Stats baseStats;
    private final List<Skill> skills;
    // You could add more fields like ElementType, Description, etc.

    public CharacterData(String characterName, Stats baseStats, List<Skill> skills) {
        this.characterName = characterName;
        this.baseStats = baseStats;
        // Create an unmodifiable copy to prevent accidental changes after creation
        this.skills = Collections.unmodifiableList(new ArrayList<>(skills));
    }

    // --- Getters ---

    public String getCharacterName() {
        return characterName;
    }

    public Stats getBaseStats() {
        return baseStats;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        return "CharacterData[Name=" + characterName + ", " + baseStats + ", Skills=" + skills.size() + "]";
    }
}