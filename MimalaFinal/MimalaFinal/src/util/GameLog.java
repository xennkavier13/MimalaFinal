package util;

import state.GameScreen;
import util.Player1Name;
import util.Player2Name;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GameLog {
    private final String statsFile = "scores.txt";
    private final String gameLogFile = "gamelog.txt";
    private final String pvpLeaderboardFile = "pvp_leaderboard.txt";
    private final String pveLeaderboardFile = "pve_leaderboard.txt";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GameLog() {
        loadStatsFromFile();
    }

    private void loadStatsFromFile() {
        // Optional: Load stats if needed
    }

    public void saveStatsToFile() {
        try (FileWriter writer = new FileWriter(statsFile)) {
            writer.write("Player 1 - Wins: " + GameScreen.p1Wins + "\n");
            writer.write("Player 1 - Losses: " + GameScreen.p1Lose + "\n");
            writer.write("Player 2 - Wins: " + GameScreen.p2Wins + "\n");
            writer.write("Player 2 - Losses: " + GameScreen.p2Lose + "\n");
        } catch (IOException e) {
            System.out.println("Failed to save stats: " + e.getMessage());
        }
    }

    public void recordGame(String winnerString) {
        LocalDate today = LocalDate.now();
        logToFile(today, winnerString);
        saveStatsToFile();
        updatePvPLeaderboard();
    }

    public void recordPVEGame(String playerName, boolean won) {
        LocalDate today = LocalDate.now();
        logToFile(today, won ? playerName + " (PvE Win)" : "AI (PvE Win)");
        saveStatsToFile();
        updatePvELeaderboard(PlayerName.playerName, won);
    }

    private void logToFile(LocalDate date, String winner) {
        try (FileWriter writer = new FileWriter(gameLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            writer.write("DateTime: " + now.format(dateFormatter) + " --- Winner: " + winner + "\n");
            writer.write("Player 1 - Wins: " + GameScreen.p1Wins + ", Losses: " + GameScreen.p1Lose + "\n");
            writer.write("Player 2 - Wins: " + GameScreen.p2Wins + ", Losses: " + GameScreen.p2Lose + "\n");
            writer.write("--------------------------------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to game log: " + e.getMessage());
        }
    }

    private void updatePvPLeaderboard() {
        Map<String, Integer> leaderboard = loadLeaderboardData(pvpLeaderboardFile);

        String winnerName = getWinnerName();
        if (winnerName == null) {
            System.out.println("No valid winner name set. Skipping PvP leaderboard update.");
            return;
        }

        leaderboard.put(winnerName, leaderboard.getOrDefault(winnerName, 0) + 1);
        saveLeaderboardData(pvpLeaderboardFile, leaderboard, "PvP Leaderboard");
    }

    private void updatePvELeaderboard(String playerName, boolean won) {
        ensureFileExists(pveLeaderboardFile);  // Ensure file exists first

        Map<String, Integer> leaderboard = loadSimpleLeaderboardData(pveLeaderboardFile);
        leaderboard.put(playerName, leaderboard.getOrDefault(playerName, 0) + (won ? 1 : 0));
        saveSimpleLeaderboardData(pveLeaderboardFile, leaderboard, "PvE Leaderboard");
    }

    private Map<String, Integer> loadLeaderboardData(String filename) {
        Map<String, Integer> leaderboard = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("Rank")) continue;
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    String name = parts[1];
                    int wins = Integer.parseInt(parts[2]);
                    leaderboard.put(name, wins);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing leaderboard file. A new one will be created.");
        }
        return leaderboard;
    }

    private Map<String, Integer> loadSimpleLeaderboardData(String filename) {
        Map<String, Integer> leaderboard = new HashMap<>();
        try {
            ensureFileExists(filename);  // Ensure file exists before reading
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("Rank")) continue;
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    String name = parts[1];
                    int wins = Integer.parseInt(parts[2]);
                    leaderboard.put(name, wins);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading leaderboard file. Starting fresh.");
        } catch (NumberFormatException e) {
            System.out.println("Corrupted leaderboard data. Starting fresh.");
        }
        return leaderboard;
    }

    private void saveLeaderboardData(String filename, Map<String, Integer> leaderboard, String title) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leaderboard.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(String.format("%-5s %-20s %-10s\n", "Rank", "Player Name", "Wins"));
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(String.format("%-5d %-20s %-10d\n", rank++, entry.getKey(), entry.getValue()));
            }
            System.out.println(title + " updated successfully.");
        } catch (IOException e) {
            System.out.println("Failed to write leaderboard: " + e.getMessage());
        }
    }

    private void saveSimpleLeaderboardData(String filename, Map<String, Integer> leaderboard, String title) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leaderboard.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(String.format("%-5s %-20s %-10s\n", "Rank", "Player Name", "Wins"));
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(String.format("%-5d %-20s %-10d\n", rank++, entry.getKey(), entry.getValue()));
            }
            System.out.println(title + " updated successfully.");
        } catch (IOException e) {
            System.out.println("Failed to write " + title + ": " + e.getMessage());
        }
    }

    private String getWinnerName() {
        if (GameScreen.lastWinner == 1 && Player1Name.player1Name != null) {
            return Player1Name.player1Name;
        } else if (GameScreen.lastWinner == 2 && Player2Name.player2Name != null) {
            return Player2Name.player2Name;
        }
        return null;
    }

    private void ensureFileExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created new leaderboard file: " + filename);
            } catch (IOException e) {
                System.out.println("Failed to create leaderboard file: " + e.getMessage());
            }
        }
    }
}