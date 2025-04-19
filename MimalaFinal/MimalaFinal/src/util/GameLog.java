package util;

import state.GameScreen;
import util.Player1Name;
import util.Player2Name;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameLog {
    private final String statsFile = "scores.txt";
    private final String gameLogFile = "gamelog.txt";
    private final String leaderboardFile = "pvp_leaderboard.txt";

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
        updateLeaderboard();
    }

    private void logToFile(LocalDate date, String winner) {
        try (FileWriter writer = new FileWriter(gameLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            writer.write("DateTime: " + now.format(formatter) + " --- Winner: " + winner + "\n");
            writer.write("Player 1 - Wins: " + GameScreen.p1Wins + ", Losses: " + GameScreen.p1Lose + "\n");
            writer.write("Player 2 - Wins: " + GameScreen.p2Wins + ", Losses: " + GameScreen.p2Lose + "\n");
            writer.write("--------------------------------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to game log: " + e.getMessage());
        }
    }

    private void updateLeaderboard() {
        Map<String, Integer> leaderboard = new HashMap<>();

        // Load current leaderboard
        try (BufferedReader reader = new BufferedReader(new FileReader(leaderboardFile))) {
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

        // Get winner name from GameScreen
        String winnerName = null;
        if (GameScreen.lastWinner == 1 && Player1Name.player1Name != null) {
            winnerName = Player1Name.player1Name;
        } else if (GameScreen.lastWinner == 2 && Player2Name.player2Name != null) {
            winnerName = Player2Name.player2Name;
        }

        if (winnerName == null) {
            System.out.println("No valid winner name set. Skipping leaderboard update.");
            return;
        }

        // Debug log
        System.out.println("Updating leaderboard for winner: " + winnerName);

        leaderboard.put(winnerName, leaderboard.getOrDefault(winnerName, 0) + 1);

        // Sort leaderboard
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leaderboard.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        // Write to leaderboard file
        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            writer.write(String.format("%-5s %-20s %-10s\n", "Rank", "Player Name", "Wins"));
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(String.format("%-5d %-20s %-10d\n", rank++, entry.getKey(), entry.getValue()));
            }
            System.out.println("Leaderboard updated successfully.");
        } catch (IOException e) {
            System.out.println("Failed to write leaderboard: " + e.getMessage());
        }
    }
}
