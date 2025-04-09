package state.character;


import java.util.HashMap;
import java.util.Map;

public class CharacterDataLoader {

    private static final Map<String, CharacterStats> characterStatsMap = new HashMap<>();

    // Static block to load data when the class is loaded
    static {
        // --- Define Stats Here ---
        // Example Stats (Replace with your actual balanced values)
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

        // Add stats for all your characters...

        // Add stats for the AI placeholder if needed, perhaps mirroring P1 or having default stats
        // For simplicity, let's assume AI uses the stats of the character it represents (secondPlayerCharacter)
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

    // Optional: Add a method to handle AI stat retrieval if it needs special logic
    public static CharacterStats getStatsForPlayer(String characterName, boolean isAI) {
        if (isAI && characterName.equals(AI_PLAYER_NAME)) {
            // Decide AI stats logic. Here, we return null, expecting GameScreen to handle it.
            // Or you could return stats for a default AI character, etc.
            System.out.println("AI player detected - Stats will be based on chosen character for P2.");
            return null; // Signal that GameScreen needs to use P2's chosen character stats
        }
        return getStats(characterName);
    }

    // Need AI_PLAYER_NAME constant if using getStatsForPlayer
    private static final String AI_PLAYER_NAME = "Computer";
}