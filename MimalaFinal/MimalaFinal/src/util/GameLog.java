package util;

import state.GameScreen;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class GameLog {
    private int player1Wins;
    private int player2Wins;
    private int player1Losses;
    private int player2Losses;


    private final String statsFile = "scores.txt";
    private final String gameLogFile = "gamelog.txt";

    public GameLog() {
        loadStatsFromFile();
    }

    private void loadStatsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(statsFile))) {
            Map<String, Integer> stats = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    stats.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }

            player1Wins = stats.getOrDefault("player1Wins", 0);
            player1Losses = stats.getOrDefault("player1Losses", 0);
            player2Wins = stats.getOrDefault("player2Wins", 0);
            player2Losses = stats.getOrDefault("player2Losses", 0);

        } catch (IOException e) {
            System.out.println("Stats file not found, starting fresh.");
        }
    }

    public void saveStatsToFile() {
        try (FileWriter writer = new FileWriter(statsFile)) {
            writer.write("player1Wins = " + player1Wins + "\n");
            writer.write("player1Losses = " + player1Losses + "\n");
            writer.write("player2Wins = " + player2Wins + "\n");
            writer.write("player2Losses = " + player2Losses + "\n");
        } catch (IOException e) {
            System.out.println("Failed to save stats: " + e.getMessage());
        }
    }

    public void recordGame(String winner) {
        LocalDate today = LocalDate.now();


        player1Wins = GameScreen.p1Wins;
        player1Losses = GameScreen.p1Lose;
        player2Wins = GameScreen.p2Wins;
        player2Losses = GameScreen.p2Lose;


        logToFile(today,winner);
        saveStatsToFile(); // Save updated stats
    }

    private void logToFile(LocalDate date, String winner) {
        try (FileWriter writer = new FileWriter(gameLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            writer.write("DateTime: " + now.format(formatter) + " --- Winner: "+ winner +"\n");
            writer.write("Player 1 - Wins: " + player1Wins + ", Losses: " + player1Losses + "\n");
            writer.write("Player 2 - Wins: " + player2Wins + ", Losses: " + player2Losses + "\n");
            writer.write("--------------------------------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to game log: " + e.getMessage());
        }
    }


}