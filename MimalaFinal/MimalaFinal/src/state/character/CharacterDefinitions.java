package state.character;
// Example package for definitions


import java.util.List;

public class CharacterDefinitions {

    // Define Pyrothar's Stats
    private static final Stats PYROTHAR_STATS = new Stats(
            100, // maxHealth
            80,  // maxStamina
            15,  // baseAttack
            10,  // defense
            12   // speed
    );

    // Define Pyrothar's Skills
    private static final List<Skill> PYROTHAR_SKILLS = List.of( // Use List.of for immutable lists directly
            new Skill(
                    "Fireball",
                    "Hurls a ball of intense fire at a single enemy.",
                    15, // staminaCost
                    25, // basePower (damage)
                    SkillEffectType.DAMAGE_MAGICAL, // Or DAMAGE_FIRE if you add elements
                    SkillTargetType.SINGLE_ENEMY,
                    0   // cooldown
            ),
            new Skill(
                    "Flame Cloak",
                    "Engulfs self in fire, boosting Attack for 3 turns.",
                    20, // staminaCost
                    5,  // basePower (Attack boost amount)
                    SkillEffectType.BUFF_ATTACK,
                    SkillTargetType.SELF,
                    4   // cooldown (lasts 3 turns, usable again on 4th)
            ),
            new Skill(
                    "Incinerate",
                    "Unleashes a wave of fire, damaging all enemies and potentially burning them.",
                    35, // staminaCost
                    30, // basePower (damage)
                    SkillEffectType.DAMAGE_MAGICAL, // Could have a secondary STATUS_EFFECT_BURN
                    SkillTargetType.ALL_ENEMIES,
                    3   // cooldown
            )
            // Add more skills as needed
    );

    // Create the CharacterData object for Pyrothar
    public static final CharacterData PYROTHAR = new CharacterData(
            "Pyrothar",
            PYROTHAR_STATS,
            PYROTHAR_SKILLS
    );

    // --- Define other characters similarly ---

    // Example: Azurox Stats (adjust values)
    private static final Stats AZUROX_STATS = new Stats(120, 70, 12, 15, 10);

    // Example: Azurox Skills (define specific skills)
    private static final List<Skill> AZUROX_SKILLS = List.of(
            new Skill("Aqua Jet", "A quick dash surrounded by water.", 10, 18, SkillEffectType.DAMAGE_PHYSICAL, SkillTargetType.SINGLE_ENEMY, 0),
            new Skill("Hydro Pump", "Blasts a powerful stream of water.", 30, 40, SkillEffectType.DAMAGE_MAGICAL, SkillTargetType.SINGLE_ENEMY, 2)
            // ... more skills
    );

    public static final CharacterData AZUROX = new CharacterData(
            "Azurox",
            AZUROX_STATS,
            AZUROX_SKILLS
    );


    private static final Stats Zenfang_STATS = new Stats(120, 70, 12, 15, 10);

    // Example: Azurox Skills (define specific skills)
    private static final List<Skill> Zenfang_SKILLS = List.of(
            new Skill("Aqua Jet", "A quick dash surrounded by water.", 10, 18, SkillEffectType.DAMAGE_PHYSICAL, SkillTargetType.SINGLE_ENEMY, 0),
            new Skill("Hydro Pump", "Blasts a powerful stream of water.", 30, 40, SkillEffectType.DAMAGE_MAGICAL, SkillTargetType.SINGLE_ENEMY, 2)
            // ... more skills
    );

    public static final CharacterData Zenfang = new CharacterData(
            "Azurox",
            AZUROX_STATS,
            AZUROX_SKILLS
    );


    // ... Define Zenfang, Auricannon, Vexmorth, Astridra, Varkos, Ignisveil ...

    // Optional: A way to get data by name
    public static CharacterData getCharacterDataByName(String name) {
        switch (name) {
            case "Pyrothar": return PYROTHAR;
            case "Azurox": return AZUROX;
            // Add cases for all other characters
            // case "Zenfang": return ZENFANG;
            // ...
            default: return null; // Or throw an exception
        }
    }
}