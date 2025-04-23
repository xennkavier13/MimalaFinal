package state.character;

import java.util.HashMap;
import java.util.Map;
import java.util.Set; // <<< ADD THIS IMPORT

public class CharacterDataLoader {

    private static final Map<String, CharacterStats> characterStatsMap = new HashMap<>();
    private static final String AI_PLAYER_NAME = "Computer"; // Keep consistent

    static {
        // --- NO CHANGES HERE ---
        characterStatsMap.put("Azurox", new CharacterStats("Azurox",
                100.0, 100.0, // Max HP, Max Stamina
                20.0, 25.0,   // Skill 1 Damage, Cost
                25.0, 30.0,   // Skill 2 Damage, Cost
                30.0, 40.0    // Skill 3 Damage, Cost
        ));
        characterStatsMap.put("Astridra", new CharacterStats("Astridra",
                100.0, 100.0, // Max HP, Max Stamina
                25.0, 25.0,   // Skill 1 Damage, Cost
                30.0, 30.0,   // Skill 2 Damage, Cost
                35.0, 40.0    // Skill 3 Damage, Cost
        ));
        characterStatsMap.put("Auricannon", new CharacterStats("Auricannon",
                100.0, 100.0, // Max HP, Max Stamina
                20.0, 25.0,   // Skill 1 Damage, Cost
                25.0, 30.0,   // Skill 2 Damage, Cost
                30.0, 40.0    // Skill 3 Damage, Cost
        ));

        characterStatsMap.put("Zenfang", new CharacterStats("Zenfang",
                100.0, 100.0, // Max HP, Max Stamina
                20.0, 20.0,   // Skill 1 Damage, Cost
                25.0, 25.0,   // Skill 2 Damage, Cost
                30.0, 30.0    // Skill 3 Damage, Cost
        ));

        characterStatsMap.put("Ignisveil", new CharacterStats("Ignisveil",
                100.0, 100.0, // Max HP, Max Stamina
                23.0, 25.0,   // Skill 1 Damage, Cost
                28.0, 30.0,   // Skill 2 Damage, Cost
                33.0, 40.0    // Skill 3 Damage, Cost
        ));
        characterStatsMap.put("Pyrothar", new CharacterStats("Pyrothar",
                100.0, 100.0, // Max HP, Max Stamina
                30.0, 25.0,   // Skill 1 Damage, Cost
                35.0, 30.0,   // Skill 2 Damage, Cost
                40.0, 45.0    // Skill 3 Damage, Cost
        ));
        characterStatsMap.put("Varkos", new CharacterStats("Varkos",
                100.0, 100.0, // Max HP, Max Stamina
                20.0, 25.0,   // Skill 1 Damage, Cost
                25.0, 30.0,   // Skill 2 Damage, Cost
                30.0, 40.0    // Skill 3 Damage, Cost
        ));
        characterStatsMap.put("Vexmorth", new CharacterStats("Vexmorth",
                100.00, 100.0, // Max HP, Max Stamina
                25.0, 25.0,   // Skill 1 Damage, Cost
                30.0, 30.0,   // Skill 2 Damage, Cost
                35.0, 40.0    // Skill 3 Damage, Cost
        ));
    }

    public static CharacterStats getStats(String characterName) {
        // --- NO CHANGES HERE ---
        if (!characterStatsMap.containsKey(characterName)) {
            System.err.println("WARNING: Stats not found for character: " + characterName + ". Returning default.");
            return new CharacterStats("Default", 100, 100, 10, 10, 10, 10, 10, 10);
        }
        return characterStatsMap.get(characterName);
    }

    // Method getStatsForPlayer seems unused based on GameScreen, can be ignored or removed
    // public static CharacterStats getStatsForPlayer(String characterName, boolean isAI) { ... }

    // <<< NEW METHOD >>>
    /**
     * Returns a set of all defined character names.
     * @return A Set containing the names of all characters with loaded stats.
     */
    public static Set<String> getAllCharacterNames() {
        return characterStatsMap.keySet();
    }
}