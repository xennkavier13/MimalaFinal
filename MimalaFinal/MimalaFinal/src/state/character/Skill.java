package state.character; // Or your preferred package structure

// Optional: Define enums for clarity and type safety
enum SkillEffectType {
    DAMAGE_PHYSICAL,
    DAMAGE_MAGICAL, // Or element types like FIRE, WATER etc.
    HEAL,
    BUFF_ATTACK,
    BUFF_DEFENSE,
    BUFF_SPEED,
    DEBUFF_ATTACK,
    DEBUFF_DEFENSE,
    DEBUFF_SPEED,
    STATUS_EFFECT_BURN, // Example status
    STATUS_EFFECT_FREEZE // Example status
    // Add more as needed
}

enum SkillTargetType {
    SELF,
    SINGLE_ENEMY,
    ALL_ENEMIES,
    SINGLE_ALLY, // If you have team modes
    ALL_ALLIES    // If you have team modes
}


/**
 * Represents a skill that a character can use.
 */
public class Skill {

    private final String name;
    private final String description;
    private final int staminaCost;
    private final int basePower; // Can represent damage, heal amount, buff strength etc.
    private final SkillEffectType effectType;
    private final SkillTargetType targetType;
    private final int cooldown; // Number of turns until usable again after use (0 = no cooldown)

    /**
     * Creates a new Skill object.
     *
     * @param name          The display name of the skill.
     * @param description   A short description of what the skill does.
     * @param staminaCost   How much stamina is required to use the skill.
     * @param basePower     The base potency of the skill (damage, healing amount, etc.).
     * @param effectType    The primary type of effect (e.g., DAMAGE, HEAL, BUFF).
     * @param targetType    Who the skill affects (e.g., SELF, SINGLE_ENEMY).
     * @param cooldown      The cooldown period in turns after using the skill.
     */
    public Skill(String name, String description, int staminaCost, int basePower,
                 SkillEffectType effectType, SkillTargetType targetType, int cooldown) {
        this.name = name;
        this.description = description;
        this.staminaCost = staminaCost;
        this.basePower = basePower;
        this.effectType = effectType;
        this.targetType = targetType;
        this.cooldown = cooldown;
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public int getBasePower() {
        return basePower;
    }

    public SkillEffectType getEffectType() {
        return effectType;
    }

    public SkillTargetType getTargetType() {
        return targetType;
    }

    public int getCooldown() {
        return cooldown;
    }

    @Override
    public String toString() {
        return name + " (Cost:" + staminaCost + " STM, Pow:" + basePower + ", CD:" + cooldown + ")";
    }
}