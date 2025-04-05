package state.character; // Or your preferred package structure

/**
 * Holds the base statistics for a character type.
 * These are typically immutable values defining the character's potential.
 */
public class Stats {

    private final int maxHealth;
    private final int maxStamina;
    private final int baseAttack; // Base damage potential
    private final int defense;    // Damage reduction capability
    private final int speed;      // Determines turn order or action frequency

    /**
     * Creates a new Stats object.
     *
     * @param maxHealth    Maximum health points.
     * @param maxStamina   Maximum stamina points for using skills.
     * @param baseAttack   Base attack power (influences skill/basic attack damage).
     * @param defense      Base defense value (reduces incoming damage).
     * @param speed        Base speed value.
     */
    public Stats(int maxHealth, int maxStamina, int baseAttack, int defense, int speed) {
        // Consider adding validation (e.g., stats > 0) if needed
        this.maxHealth = maxHealth;
        this.maxStamina = maxStamina;
        this.baseAttack = baseAttack;
        this.defense = defense;
        this.speed = speed;
    }

    // --- Getters ---

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Stats[HP=" + maxHealth + ", STM=" + maxStamina + ", ATK=" + baseAttack +
                ", DEF=" + defense + ", SPD=" + speed + "]";
    }
}