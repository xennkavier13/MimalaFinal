package state.character;


import java.util.HashMap;
import java.util.Map;

public class CharacterDataLoader {

    private static final Map<String, CharacterStats> characterStatsMap = new HashMap<>();

    static {
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
        // Handle potential missing stats, maybe return a default or throw error
        if (!characterStatsMap.containsKey(characterName)) {
            System.err.println("WARNING: Stats not found for character: " + characterName + ". Returning default.");
            // Return a default placeholder or throw an exception
            return new CharacterStats("Default", 100, 100, 10, 10, 10, 10, 10, 10);
        }
        return characterStatsMap.get(characterName);
    }

    public static CharacterStats getStatsForPlayer(String characterName, boolean isAI) {
        if (isAI && characterName.equals(AI_PLAYER_NAME)) {
            System.out.println("AI player detected - Stats will be based on chosen character for P2.");
            return null;
        }
        return getStats(characterName);
    }
    private static final String AI_PLAYER_NAME = "Computer";
}