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
        System.out.println("[LOAD DEBUG] Attempting to load: " + filename); // <<< ADDED
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String header = reader.readLine();
            System.out.println("[LOAD DEBUG] Header Line: [" + header + "]"); // <<< ADDED
            if (header == null) {
                System.out.println("[LOAD DEBUG] File is empty."); // <<< ADDED
                return leaderboard;
            }

            int lineNum = 1; // <<< ADDED line counter
            while ((line = reader.readLine()) != null) {
                lineNum++; // <<< Increment line counter
                System.out.println("[LOAD DEBUG] Reading Line " + lineNum + ": [" + line + "]"); // <<< ADDED
                if (line.trim().isEmpty()) {
                    System.out.println("[LOAD DEBUG] Skipping empty line."); // <<< ADDED
                    continue;
                }

                String[] parts = line.split("\t"); // Split by Tab
                System.out.println("[LOAD DEBUG] Parts found: " + parts.length); // <<< ADDED

                if (parts.length == 3) {
                    String rankStr = parts[0].trim();
                    String name = parts[1].trim();    // Name is second part
                    String scoreStr = parts[2].trim(); // Score is third part
                    // <<< ADDED Print extracted parts >>>
                    System.out.println("[LOAD DEBUG] Extracted -> Rank:'" + rankStr + "' Name:'" + name + "' Score:'" + scoreStr + "'");

                    if (name.isEmpty()) {
                        System.err.println("Skipping line " + lineNum + " with empty name: " + line);
                    } else {
                        try {
                            int rank = Integer.parseInt(rankStr);
                            int score = Integer.parseInt(scoreStr);
                            leaderboard.put(name, score);
                            System.out.println("[LOAD DEBUG] Successfully loaded entry: Name='" + name + "', Score=" + score); // <<< ADDED
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping corrupted line " + lineNum + " in " + filename + ": [" + line + "] - Invalid rank or score part.");
                        }
                    }
                } else {
                    System.err.println("Skipping malformed line " + lineNum + " in " + filename + " (expected 3 tab-separated parts): [" + line + "]");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading leaderboard file " + filename + ". Error: " + e.getMessage());
        }
        System.out.println("[LOAD DEBUG] Finished loading " + filename + ". Entries loaded: " + leaderboard.size()); // <<< ADDED
        return leaderboard;
    }


    // --- saveSimpleLeaderboardData (can be reused for Arcade) ---
    // Saves data sorted by score (descending)
    // Inside GameLog.java
    private void saveSimpleLeaderboardData(String filename, Map<String, Integer> leaderboard, String title) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leaderboard.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("[SAVE DEBUG] Attempting to save: " + filename + " (" + title + ")"); // <<< ADDED
        ensureFileExists(filename);
        try (FileWriter writer = new FileWriter(filename, false)) { // Overwrite mode
            String scoreHeader = title.contains("Stage") || title.contains("Arcade") ? "Score" : "Wins"; // Use Score for Arcade
            String headerLine = "Rank\tPlayer Name\t" + scoreHeader + "\n";
            System.out.print("[SAVE DEBUG] Writing Header: " + headerLine.replace("\t", "\\t")); // <<< ADDED (print \t for clarity)
            writer.write(headerLine);

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                String lineToWrite = String.format("%d\t%s\t%d\n", rank++, entry.getKey(), entry.getValue());
                System.out.print("[SAVE DEBUG] Writing Line " + (rank-1) + ": " + lineToWrite.replace("\t", "\\t")); // <<< ADDED (print \t for clarity)
                writer.write(lineToWrite);
            }
            System.out.println("\n" + title + " updated successfully using Tab format."); // Add newline before this
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