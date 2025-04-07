// CharacterStats.java (can be in the same package or a sub-package like 'data')
package state.character; // Or your data package

public class CharacterStats {
    private final String name;
    private final double maxHp;
    private final double maxStamina;
    private final double skill1Damage;
    private final double skill1Cost;
    private final double skill2Damage;
    private final double skill2Cost;
    private final double skill3Damage;
    private final double skill3Cost;

    public CharacterStats(String name, double maxHp, double maxStamina,
                          double skill1Damage, double skill1Cost,
                          double skill2Damage, double skill2Cost,
                          double skill3Damage, double skill3Cost) {
        this.name = name;
        this.maxHp = maxHp;
        this.maxStamina = maxStamina;
        this.skill1Damage = skill1Damage;
        this.skill1Cost = skill1Cost;
        this.skill2Damage = skill2Damage;
        this.skill2Cost = skill2Cost;
        this.skill3Damage = skill3Damage;
        this.skill3Cost = skill3Cost;
    }

    // --- Getters ---
    public String getName() { return name; }
    public double getMaxHp() { return maxHp; }
    public double getMaxStamina() { return maxStamina; }
    public double getSkillDamage(int skillNumber) {
        switch (skillNumber) {
            case 1: return skill1Damage;
            case 2: return skill2Damage;
            case 3: return skill3Damage;
            default: return 0.0;
        }
    }
    public double getSkillCost(int skillNumber) {
        switch (skillNumber) {
            case 1: return skill1Cost;
            case 2: return skill2Cost;
            case 3: return skill3Cost;
            default: return Double.MAX_VALUE; // Prevent invalid skills
        }
    }
}