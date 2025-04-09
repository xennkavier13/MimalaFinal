// RoundManager.java
package util; // Or a sub-package like state.gameplay

import state.character.CharacterStats;

import java.util.Random;

public class RoundManager {

    private int currentRound;
    private final int maxRounds;
    private final Random random = new Random(); // For randomizing start player

    private boolean player1TookTurnThisRound = false;
    private boolean player2TookTurnThisRound = false;

    private static final double STAMINA_RECOVERY_PER_ROUND = 15.0; // Keep recovery logic here

    public RoundManager(int maxRounds) {
        if (maxRounds <= 0) {
            throw new IllegalArgumentException("Max rounds must be positive.");
        }
        this.maxRounds = maxRounds;
        this.currentRound = 0; // Will be incremented to 1 on the first call to startNextRound
    }

    /**
     * Prepares for the start of the next round (or the first round).
     * Increments the round counter, resets turn flags, and determines the starting player randomly.
     *
     * @return True if a new round can start, false if max rounds reached.
     */
    public boolean startNextRound() {
        currentRound++;
        if (isRoundLimitReached()) {
            System.out.println("Max rounds (" + maxRounds + ") reached or exceeded.");
            currentRound--; // Decrement back as the game ended before this round truly started
            return false; // Cannot start next round, game should end
        }

        System.out.println("\n--- Preparing Round " + currentRound + " ---");
        // Reset turn tracking for the new round
        player1TookTurnThisRound = false;
        player2TookTurnThisRound = false;
        return true; // Ready for the new round
    }

    /**
     * Determines randomly which player should start the current round.
     * Should be called after startNextRound().
     * @return true if Player 1 starts, false if Player 2 starts.
     */
    public boolean determineStartingPlayer() {
        boolean p1Starts = random.nextBoolean();
        System.out.println("Randomizing round start: Player " + (p1Starts ? "1" : "2") + " goes first in round " + currentRound);
        return p1Starts;
    }

    /**
     * Marks the specified player as having taken their turn this round.
     * @param isPlayer1 True if player 1 took the turn, false for player 2.
     */
    public void recordTurnTaken(boolean isPlayer1) {
        if (isPlayer1) {
            player1TookTurnThisRound = true;
            System.out.println("Player 1 marked turn taken for round " + currentRound);
        } else {
            player2TookTurnThisRound = true;
            System.out.println("Player 2 marked turn taken for round " + currentRound);
        }
    }

    /**
     * Checks if both players have taken their turn in the current round.
     * @return true if the round is complete, false otherwise.
     */
    public boolean isRoundComplete() {
        boolean complete = player1TookTurnThisRound && player2TookTurnThisRound;
        if(complete){
            System.out.println("Round " + currentRound + " is complete.");
        }
        return complete;
    }

    /**
     * Checks if the next round would exceed the maximum allowed rounds.
     * @return true if the round limit has been reached (game should end).
     */
    public boolean isRoundLimitReached() {
        // Check if the *current* round number already exceeds max rounds.
        // This is checked *before* starting the round actions.
        return currentRound > maxRounds;
    }


    /**
     * Applies stamina recovery. Intended to be called at the start of a new round (after round 1).
     *
     * @param p1Stats Player 1's stats (for max stamina).
     * @param p2Stats Player 2's stats (for max stamina).
     * @param p1CurrentStamina Player 1's current stamina value.
     * @param p2CurrentStamina Player 2's current stamina value.
     * @return A double array containing the new [player1Stamina, player2Stamina].
     */
    public double[] applyStaminaRecovery(CharacterStats p1Stats, CharacterStats p2Stats, double p1CurrentStamina, double p2CurrentStamina) {
        if (currentRound <= 1) {
            return new double[]{p1CurrentStamina, p2CurrentStamina}; // No recovery before round 2
        }

        double newP1Stamina = Math.min(p1Stats.getMaxStamina(), p1CurrentStamina + STAMINA_RECOVERY_PER_ROUND);
        double newP2Stamina = Math.min(p2Stats.getMaxStamina(), p2CurrentStamina + STAMINA_RECOVERY_PER_ROUND);

        System.out.printf("Stamina Recovered for Round %d! P1: %.1f -> %.1f, P2: %.1f -> %.1f%n",
                currentRound,
                p1CurrentStamina, newP1Stamina,
                p2CurrentStamina, newP2Stamina);

        return new double[]{newP1Stamina, newP2Stamina};
    }

    // --- Getters ---
    public int getCurrentRound() {
        return currentRound;
    }

    public int getMaxRounds() {
        return maxRounds;
    }
}