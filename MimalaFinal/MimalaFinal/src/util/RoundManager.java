package util;

import state.character.CharacterStats;

import java.util.Random;

public class RoundManager {

    private int currentRound;
    private final int maxRounds;
    private final Random random = new Random();

    private boolean player1TookTurnThisRound = false;
    private boolean player2TookTurnThisRound = false;

    private int player1Wins = 0;
    private int player2Wins = 0;

    private static final double STAMINA_RECOVERY_PER_ROUND = 15.0;

    public RoundManager(int maxRounds) {
        if (maxRounds <= 0) {
            throw new IllegalArgumentException("Max rounds must be positive.");
        }
        this.maxRounds = maxRounds;
        this.currentRound = 0;
    }

    /**
     * Starts the next round if possible.
     * @return true if a new round starts, false if round limit reached.
     */
    public boolean startNextRound() {
        if (isRoundLimitReached()) {
            System.out.println("Max rounds reached. No further rounds will be started.");
            return false;
        }

        currentRound++;
        player1TookTurnThisRound = false;
        player2TookTurnThisRound = false;

        System.out.println("\n--- Starting Round " + currentRound + " ---");
        return true;
    }

    public boolean determineStartingPlayer() {
        boolean p1Starts = random.nextBoolean();
        System.out.println("Player " + (p1Starts ? "1" : "2") + " starts Round " + currentRound);
        return p1Starts;
    }

    public void recordTurnTaken(boolean isPlayer1) {
        if (isPlayer1) {
            player1TookTurnThisRound = true;
            System.out.println("Player 1 took turn for Round " + currentRound);
        } else {
            player2TookTurnThisRound = true;
            System.out.println("Player 2 took turn for Round " + currentRound);
        }
    }

    public boolean isRoundComplete() {
        boolean complete = player1TookTurnThisRound && player2TookTurnThisRound;
        if (complete) {
            System.out.println("Round " + currentRound + " complete.");
        }
        return complete;
    }

    public void recordRoundWinner(boolean isPlayer1) {
        if (isPlayer1) {
            player1Wins++;
            System.out.println("Player 1 wins Round " + currentRound + " (Total Wins: " + player1Wins + ")");
        } else {
            player2Wins++;
            System.out.println("Player 2 wins Round " + currentRound + " (Total Wins: " + player2Wins + ")");
        }
    }

    public boolean isMatchOver() {
        return player1Wins > maxRounds / 2 || player2Wins > maxRounds / 2 || currentRound >= maxRounds;
    }

    public String getMatchWinner() {
        if (player1Wins > player2Wins) {
            return "Player 1";
        } else if (player2Wins > player1Wins) {
            return "Player 2";
        } else {
            return "Draw";
        }
    }

    public double[] applyStaminaRecovery(CharacterStats p1Stats, CharacterStats p2Stats, double p1CurrentStamina, double p2CurrentStamina) {
        if (currentRound <= 1) {
            return new double[]{p1CurrentStamina, p2CurrentStamina};
        }

        double newP1Stamina = Math.min(p1Stats.getMaxStamina(), p1CurrentStamina + STAMINA_RECOVERY_PER_ROUND);
        double newP2Stamina = Math.min(p2Stats.getMaxStamina(), p2CurrentStamina + STAMINA_RECOVERY_PER_ROUND);

        System.out.printf("Stamina Recovery - Round %d: P1 %.1f -> %.1f | P2 %.1f -> %.1f%n",
                currentRound, p1CurrentStamina, newP1Stamina, p2CurrentStamina, newP2Stamina);

        return new double[]{newP1Stamina, newP2Stamina};
    }

    public boolean isRoundLimitReached() {
        return currentRound >= maxRounds;
    }

    // --- Getters ---
    public int getCurrentRound() {
        return currentRound;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }
}
