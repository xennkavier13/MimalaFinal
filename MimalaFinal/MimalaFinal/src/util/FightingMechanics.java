package util;

import java.util.Random;
import java.util.Scanner;

public class FightingMechanics {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        startFight();
    }

    public static void startFight() {
        Player player1 = new Player("Player 1", 100, 15);
        Player player2 = new Player("Player 2", 100, 15);

        System.out.println("⚔️ PVP BATTLE START! ⚔️\n");

        int turn = random.nextBoolean() ? 1 : 2; // Randomly choose who starts

        while (player1.getHealth() > 0 && player2.getHealth() > 0) {
            Player attacker = (turn == 1) ? player1 : player2;
            Player defender = (turn == 1) ? player2 : player1;

            System.out.println(attacker.getName() + "'s turn!");
            System.out.println("[1] Attack | [2] Defend");
            int choice = scanner.nextInt();

            if (choice == 1) {
                int damage = attacker.attack();
                defender.takeDamage(damage);
                System.out.println(attacker.getName() + " deals " + damage + " damage!");
            } else if (choice == 2) {
                System.out.println(attacker.getName() + " defends and reduces next damage.");
                attacker.setDefending(true);
            } else {
                System.out.println("Invalid choice. Skipping turn.");
            }

            System.out.println(defender.getName() + " has " + defender.getHealth() + " HP left.");
            turn = (turn == 1) ? 2 : 1; // Switch turn
        }

        // Announce Winner
        System.out.println("\n🏆 BATTLE OVER! 🏆");
        System.out.println((player1.getHealth() > 0 ? player1.getName() : player2.getName()) + " WINS!");
    }
}

class Player {
    private final String name;
    private int health;
    private final int attackPower;
    private boolean isDefending = false;

    public Player(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }

    public void takeDamage(int damage) {
        if (isDefending) {
            damage /= 2; // Reduce damage if defending
            isDefending = false; // Reset defense after one turn
        }
        health = Math.max(0, health - damage);
    }

    public int attack() {
        return attackPower + new Random().nextInt(6); // Base damage + randomness
    }

    public void setDefending(boolean defending) {
        isDefending = defending;
    }
}
