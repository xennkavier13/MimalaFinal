package util;

import state.GameScreen;
// Removed: util.Player1Name; - Access statically if needed
// Removed: util.Player2Name; - Access statically if needed

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GameLog {
    private final String statsFile = "scores.txt"; // General match stats (optional)
    private final String gameLogFile = "gamelog.txt"; // Detailed log of matches
    private final String pvpLeaderboardFile = "pvp_leaderboard.txt";
    private final String pveLeaderboardFile = "pve_leaderboard.txt";
    // <<< NEW LEADERBOARD FILE >>>
    private final String arcadeLeaderboardFile = "arcade_leaderboard.txt";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Constructor and Load/Save for general stats (optional, unchanged) ---
    public GameLog() {
        // loadStatsFromFile(); // Can remove if not using p1Wins etc. directly
    }

    // Save general stats (can be removed if GameScreen.p1Wins etc. are removed)
    public void saveStatsToFile() {
       /* // This uses static variables from GameScreen which might be confusing.
          // Consider removing this if not strictly needed or redesign state storage.
        try (FileWriter writer = new FileWriter(statsFile)) {
            writer.write("Player 1 - Wins: " + GameScreen.p1Wins + "\n");
            writer.write("Player 1 - Losses: " + GameScreen.p1Lose + "\n");
            writer.write("Player 2 - Wins: " + GameScreen.p2Wins + "\n");
            writer.write("Player 2 - Losses: " + GameScreen.p2Lose + "\n");
        } catch (IOException e) {
            System.out.println("Failed to save stats: " + e.getMessage());
        }
        */
    }

    // Records a standard PVP game outcome
    public void recordGame(String winnerString) {
        LocalDate today = LocalDate.now();
        logToFile(today, winnerString);
        // saveStatsToFile(); // Optional
        updatePvPLeaderboard();
    }

    // Records a standard PVE game outcome
    public void recordPVEGame(String playerName, boolean won) {
        LocalDate today = LocalDate.now();
        logToFile(today, won ? playerName + " (PvE Win)" : "AI (PvE Win)");
        // saveStatsToFile(); // Optional
        updatePvELeaderboard(playerName, won); // Pass player name directly
    }

    public void recordArcadeWin(String playerName) {
        String p1 = (playerName != null && !playerName.trim().isEmpty()) ? playerName : "Player 1";
        // Log details to the main game log file
        // We use GameScreen.arcadeWins here, assuming it was incremented *before* calling this
        logToFile(LocalDate.now(), p1 + " won Arcade match (Streak: " + GameScreen.arcadeWins + ")");
        System.out.println("Logged Arcade match win for " + p1);
    }

    // --- logToFile (unchanged) ---
    private void logToFile(LocalDate date, String winner) {
        // ... (existing code) ...
        try (FileWriter writer = new FileWriter(gameLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            writer.write("DateTime: " + now.format(dateFormatter) + " --- Winner/Info: " + winner + "\n");
            // Logging static GameScreen wins here might be inaccurate if runs overlap
            // writer.write("Player 1 - W   ins: " + GameScreen.p1Wins + ", Losses: " + GameScreen.p1Lose + "\n");
            // writer.write("Player 2 - Wins: " + GameScreen.p2Wins + ", Losses: " + GameScreen.p2Lose + "\n");
            writer.write("--------------------------------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to game log: " + e.getMessage());
        }
    }


    // <<< NEW METHOD for Arcade Mode >>>
    /**
     * Records the result of an Arcade run. Updates the leaderboard only if the
     * new score is higher than the player's previous best.
     * @param playerName The name of the player.
     * @param wins The number of wins achieved in this run.
     */
    public void recordArcadeRun(String playerName, int wins) {
        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.println("Cannot record Arcade run for null or empty player name.");
            return;
        }
        System.out.println("Recording Arcade Run for " + playerName + " with " + wins + " wins.");
        ensureFileExists(arcadeLeaderboardFile);
        Map<String, Integer> leaderboard = loadSimpleLeaderboardData(arcadeLeaderboardFile);

        // Get current best score for this player, default to 0 if not present
        int currentBest = leaderboard.getOrDefault(playerName, 0);

        // Update only if the new score is better
        if (wins > currentBest) {
            System.out.println("New Arcade high score for " + playerName + "! Previous: " + currentBest + ", New: " + wins);
            leaderboard.put(playerName, wins);
            saveSimpleLeaderboardData(arcadeLeaderboardFile, leaderboard, "Arcade Leaderboard");
            // Also log the achievement to the general game log
            logToFile(LocalDate.now(), playerName + " achieved Arcade score: " + wins);
        } else {
            System.out.println("Arcade score for " + playerName + " (" + wins + ") did not beat previous best (" + currentBest + "). Leaderboard not updated.");
        }
    }


    // --- PVP Leaderboard Methods (unchanged, but check winner name logic) ---
    private void updatePvPLeaderboard() {
        ensureFileExists(pvpLeaderboardFile);
        Map<String, Integer> leaderboard = loadSimpleLeaderboardData(pvpLeaderboardFile); // Use simple load/save

        String winnerName = getWinnerName(); // Relies on GameScreen.lastWinner and PlayerXName static vars
        if (winnerName == null) {
            System.out.println("No valid winner name set. Skipping PvP leaderboard update.");
            return;
        }

        leaderboard.put(winnerName, leaderboard.getOrDefault(winnerName, 0) + 1);
        saveSimpleLeaderboardData(pvpLeaderboardFile, leaderboard, "PvP Leaderboard"); // Use simple save
    }

    // --- PVE Leaderboard Methods (unchanged) ---
    private void updatePvELeaderboard(String playerName, boolean won) {
        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.println("Cannot update PvE leaderboard for null or empty player name.");
            return;
        }
        ensureFileExists(pveLeaderboardFile);
        Map<String, Integer> leaderboard = loadSimpleLeaderboardData(pveLeaderboardFile);
        // Only increment wins, don't record losses as score
        if (won) {
            leaderboard.put(playerName, leaderboard.getOrDefault(playerName, 0) + 1);
        } else {
            // Ensure player exists on leaderboard even if they lost first game
            leaderboard.putIfAbsent(playerName, 0);
        }
        saveSimpleLeaderboardData(pveLeaderboardFile, leaderboard, "PvE Leaderboard");
    }


    // --- loadSimpleLeaderboardData (can be reused for Arcade) ---
    // Reads a file formatted like: Rank Name Score
    // Inside GameLog.java

    public Map<String, Integer> loadSimpleLeaderboardData(String filename) {
        Map<String, Integer> leaderboard = new HashMap<>();
        ensureFileExists(filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read and potentially discard the header line
            String header = reader.readLine();
            if (header == null || !header.trim().startsWith("Rank")) {
                // If no header or unexpected header, maybe log a warning or reset pointer
                System.out.println("Leaderboard file " + filename + " might be missing a header.");
                // If header was null, file is empty, return empty map
                if(header == null) return leaderboard;
                // If header wasn't null but unexpected, we still try to process from the first line read
                line = header; // Process the first line if it wasn't the expected header
            } else {
                line = reader.readLine(); // Read the first actual data line if header was present
            }


            while (line != null) {
                if (line.trim().isEmpty()) {
                    line = reader.readLine(); // Read next line
                    continue; // Skip empty lines
                }

                // Split by one or more whitespace characters
                String[] parts = line.trim().split("\\s+");

                // Expect at least 3 parts: Rank, Name (at least one part), Score
                if (parts.length >= 3) {
                    // Score is the LAST part
                    String scoreStr = parts[parts.length - 1];
                    // Name is everything between Rank (parts[0]) and Score (last part)
                    StringBuilder nameBuilder = new StringBuilder();
                    for (int i = 1; i < parts.length - 1; i++) {
                        nameBuilder.append(parts[i]).append(" ");
                    }
                    String name = nameBuilder.toString().trim(); // Reconstruct name

                    if (name.isEmpty()) {
                        System.err.println("Skipping line with empty name in " + filename + ": " + line);
                    } else {
                        try {
                            int score = Integer.parseInt(scoreStr);
                            leaderboard.put(name, score);
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping corrupted line in " + filename + ": [" + line + "] - Invalid score part: '" + scoreStr + "'");
                        }
                    }
                } else {
                    System.err.println("Skipping malformed line in " + filename + " (less than 3 parts): [" + line + "]");
                }
                line = reader.readLine(); // Read next line
            }
        } catch (IOException e) {
            System.out.println("Error reading leaderboard file " + filename + ". Starting fresh or returning empty.");
        }
        return leaderboard;
    }


    // --- saveSimpleLeaderboardData (can be reused for Arcade) ---
    // Saves data sorted by score (descending)
    private void saveSimpleLeaderboardData(String filename, Map<String, Integer> leaderboard, String title) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leaderboard.entrySet());
        // Sort by value (score) descending
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write(String.format("%-5s %-20s %-10s\n", "Rank", "Player Name", "Score"));
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(String.format("%-5d %-20s %-10d\n", rank++, entry.getKey(), entry.getValue()));
            }
            System.out.println(title + " updated successfully.");
        } catch (IOException e) {
            System.out.println("Failed to write " + title + " to " + filename + ": " + e.getMessage());
        }
    }


    // --- getWinnerName (unchanged, relies on static state) ---
    private String getWinnerName() {
        // This logic might be fragile if GameScreen instances change rapidly
        // or if PlayerXName isn't set correctly.
        if (GameScreen.lastWinner == 1 && Player1Name.player1Name != null) {
            return Player1Name.player1Name;
        } else if (GameScreen.lastWinner == 2 && Player2Name.player2Name != null) {
            return Player2Name.player2Name;
        }
        System.err.println("Could not determine winner name (lastWinner=" + GameScreen.lastWinner + ")");
        return null;
    }

    // --- ensureFileExists (unchanged) ---
    public void ensureFileExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                // Create parent directories if they don't exist
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
                System.out.println("Created new leaderboard file: " + filename);
            } catch (IOException e) {
                System.out.println("Failed to create leaderboard file: " + e.getMessage());
            }
        }
    }
}