package state;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*; // <<< ADD IMPORT FOR List, Set, etc.
import java.util.List;
import java.util.stream.Collectors; // <<< ADD IMPORT
import javax.swing.SwingUtilities;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import util.Player1Name;
import util.Player2Name;

import state.UI.Pause;
import state.UI.ResultScreen;
import state.UI.StatusBar;
import state.character.CharacterDataLoader;
import state.character.CharacterStats;
import util.*;
// Removed: import javax.swing.Timer; // We are replacing this

public class GameScreen extends JPanel {
    private final JFrame frame;
    private final String firstPlayerCharacterName; // Player's chosen character
    private final String secondPlayerCharacterName; // Current AI opponent
    private final String selectedMapPath; // Current map
    private final String gameMode; // "PVP", "PVC", or "Arcade"
    private final Random random = new Random(); // Use this instance
    // --- Animation State ---
    private ImageIcon player1IdleIcon; // To store the idle state easily
    private ImageIcon player2IdleIcon;
    private javax.swing.Timer animationRevertTimer; // Timer to switch back to idle
    private boolean isAnimating = false; // Simple flag to prevent overlapping actions during animation
    private javax.swing.Timer player1AnimTimer;
    private javax.swing.Timer player2AnimTimer;
    private javax.swing.Timer deathSequenceTimer;
    // --- Character Stats ---
    private CharacterStats player1Stats;
    private CharacterStats player2Stats;
    private double player1CurrentHp;
    private double player1CurrentStamina;
    private double player2CurrentHp;
    private double player2CurrentStamina;

    // --- UI Components ---

    private StatusBar player1HpBar;
    private StatusBar player1StaminaBar;
    private JLabel player1CharacterLabel;
    private JButton player1Skill1, player1Skill2, player1Skill3;

    private StatusBar player2HpBar;
    private StatusBar player2StaminaBar;
    private JLabel player2CharacterLabel;
    private JButton player2Skill1, player2Skill2, player2Skill3;

    private JLabel timerDisplayLabel;
    private JLabel turnIndicatorLabel;
    private JLabel roundDisplayLabel; // New label for rounds

    // --- Layout Constants --- (Keep as they are)
    private static final int PADDING = 30;
    private static final int BAR_WIDTH = 600;
    private static final int HP_BAR_HEIGHT = 65;
    private static final int STAMINA_BAR_HEIGHT = 40;
    private static final int BAR_SPACING = 5;
    private static final int CHARACTER_Y_OFFSET = 30;
    private static final int CHARACTER_WIDTH = 1920;
    private static final int CHARACTER_HEIGHT = 1080;
    private static final int SKILL_BUTTON_WIDTH = 200;
    private static final int SKILL_BUTTON_HEIGHT = 60;
    private static final int SKILL_SPACING = 10;
    private static final int SKILL_AREA_BOTTOM_MARGIN = 30;


    // --- Asset Path Constants --- (Keep as they are)
    private static final String HP_BAR_BG_BASE_PATH = "/assets/FightingUI/HealthBarBorder.png";
    private static final String HP_BAR_FG_PATH = "/assets/FightingUI/HealthBar_AllCharacters.png";
    private static final String STAMINA_BAR_BG_PATH = "/assets/FightingUI/StaminaBar1.png";
    private static final String STAMINA_BAR_FG_PATH = "/assets/FightingUI/StaminaBar2.png";
    private static final String CHAR_GIF_BASE_PATH = "/assets/FightingUI/Mimala_Characters/";
    private static final String ARCADE_VICTORY_IMG_PATH = "/assets/FightingUI/AI/VictoryScreen.png";
    private JLabel victoryOverlayLabel = null; // To hold the temporary victory overlay
    private MouseAdapter victoryClickListener = null;

    // --- Game State ---
    private volatile boolean isPlayer1Turn = true; // Tracks whose turn it is NOW
    private volatile boolean gameRunning = true;
    private Thread turnTimerThread;
    private volatile int remainingTime;
    private static final int TURN_DURATION_SECONDS = 15;
    private static final String AI_PLAYER_NAME = "Computer";
    public static boolean isVsAI;
    public static int arcadeWins = 0;
    private GameLog gameLog = new GameLog();
    // --- Rounds & Stamina Recovery
    private static final int MAX_ROUNDS = 10;
    private RoundManager roundManager;
    private static final double STAMINA_RECOVERY_PER_ROUND = 10.0; // Recover 30 stamina points

    private Pause pause;

    private Clip music;

    private JLabel player1WinsLabel;
    private JLabel player2WinsLabel;

    public static int p1Wins = 0;
    public static int p1Lose = 0;
    public static int p2Wins = 0;
    public static int p2Lose = 0;
    public static int lastWinner = 0; // 1 = Player 1 wins, 2 = Player 2 wins
    private JLabel arcadeStreakLabel;

    // Round logic
    // Inside GameScreen class fields
    private int player1RoundWins = 0;
    private int player2RoundWins = 0;
    private static final int ROUNDS_TO_WIN = 2; // Best of 3 (First to 2 wins
    private int currentMatchRound = 1;

    private boolean hasShownRound1 = false;
    private boolean hasShownRound2 = false;
    private boolean hasShownRound3 = false;


    public GameScreen(JFrame frame, String firstPlayerCharacter, String secondPlayerCharacter, String selectedMapResourcePath, String gameMode) {
        this.frame = frame;
        this.firstPlayerCharacterName = firstPlayerCharacter;
        this.secondPlayerCharacterName = secondPlayerCharacter;
        this.selectedMapPath = selectedMapResourcePath;
        this.gameMode = gameMode;

        // --- Set isVsAI based on gameMode ---
        isVsAI = this.gameMode.equals("PVC") || this.gameMode.equals("Arcade");
        //ResultScreen.isVsAI = isVsAI; // <<< ALSO UPDATE ResultScreen's static flag if it relies on it

        System.out.println("GameScreen initialized. Mode: " + this.gameMode + ", Is Vs AI: " + this.isVsAI);
        if (this.gameMode.equals("Arcade")) {
            System.out.println("Current Arcade Wins: " + arcadeWins);
        }

        // --- Initialize Round Manager FIRST ---
        this.roundManager = new RoundManager(10); // Initialize here! TURNS

        // --- Load Character Stats ---
        loadCharacterStats(); // Now safe to call

        // --- Basic Panel Setup ---
        setLayout(null); // Disable layout manager so you can manually position components
        setPreferredSize(new Dimension(1920, 1080)); // Set the panel size
        setFocusable(true); // Allow the panel to receive input focus

        // --- Load Assets ---
        loadMapImage(); // Load the map for the game background

        // --- Setup UI ---
        initializeUIComponents(); // Initialize all the UI components (like buttons, labels, etc.)
        positionUIComponents(); // Set the positions of the components (e.g., player health bars, buttons)

        // --- Add Components to the Panel ---
        addAllComponents(); // Ensure all the components are added to the panel

        // --- Start Game Flow ---
        startNextRoundSequence(); // Start the first round

        // --- Request Focus ---
        SwingUtilities.invokeLater(this::requestFocusInWindow); // Ensure the panel gains focus

        setupKeyBindings();

        // --- Play Background Music ---
        playMusic("assets/FightingUI/fightmusic.wav"); // Play background music
    }

    private static final String[] mapPaths = { // Should match MapSelection
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };

    private ImageIcon mapImage;

    private void loadCharacterStats() {
        System.out.println("Loading character stats...");
        player1Stats = CharacterDataLoader.getStats(firstPlayerCharacterName);
        // In PVC/Arcade, secondPlayerCharacterName IS the specific AI opponent
        player2Stats = CharacterDataLoader.getStats(secondPlayerCharacterName);

        if (player1Stats == null || player2Stats == null) {
            System.err.println("FATAL ERROR: Could not load stats for selected characters! P1: " + firstPlayerCharacterName + ", P2: " + secondPlayerCharacterName);
            // Handle error more gracefully if possible
            if (player1Stats == null) player1Stats = CharacterDataLoader.getStats("Default");
            if (player2Stats == null) player2Stats = CharacterDataLoader.getStats("Default");
        }

        player1CurrentHp = player1Stats.getMaxHp();
        player1CurrentStamina = player1Stats.getMaxStamina();
        player2CurrentHp = player2Stats.getMaxHp();
        player2CurrentStamina = player2Stats.getMaxStamina();

        System.out.println("Stats loaded for P1 (" + firstPlayerCharacterName + ") and P2 (" + secondPlayerCharacterName + ")");
    }

    // --- Path Generation Helpers (Keep as they are) ---
    private String getHpBackgroundPath(String characterName, boolean isPlayer1) {
        // Assuming Azurox bar is the standard for now
        return HP_BAR_BG_BASE_PATH;
    }

    private String getCharacterGifPath(String characterName, boolean isPlayer1) {
        // Check if AI needs specific handling for GIF path (it shouldn't, use selected character)
        // String actualCharacterName = (isVsAI && !isPlayer1) ? firstPlayerCharacterName : characterName; // Example if AI mirrors P1 visually? No, use selected char.

        String gifFileName = characterName + "Idle" + (isPlayer1 ? "" : "flipped") + ".gif"; // Ensure flipped GIFs exist!
        String path = CHAR_GIF_BASE_PATH + characterName + "/" + gifFileName;
        System.out.println("Attempting to load GIF from: " + path + " for " + characterName);
        URL testUrl = getClass().getResource(path);
        if (testUrl == null) {
            System.err.println("!!! GIF NOT FOUND AT: " + path + " !!!");
            // Consider returning a path to a default/placeholder GIF here
        }
        return path;
    }


    // --- Loading and Initialization ---

    private void loadMapImage() {
        try {
            URL imageURL = getClass().getResource("/" + selectedMapPath);
            if (imageURL != null) {
                mapImage = new ImageIcon(imageURL); // This works for GIF as well
                System.out.println("Map loaded: " + imageURL);
            } else {
                System.err.println("Error: GameScreen Map image resource not found: /" + selectedMapPath);
                mapImage = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading map image in GameScreen: " + selectedMapPath);
            e.printStackTrace();
            mapImage = null;
        }
    }

    private ImageIcon loadCharacterGif(String characterName, boolean isPlayer1) {
        // Now uses the selected character name directly
        String path = getCharacterGifPath(characterName, isPlayer1);
        URL gifUrl = getClass().getResource(path);
        if (gifUrl != null) {
            System.out.println("GIF loaded successfully: " + path);
            return new ImageIcon(gifUrl);
        } else {
            System.err.println("ERROR: Character GIF not found at path: " + path + ". Creating placeholder.");
            return createPlaceholderIcon(CHARACTER_WIDTH, CHARACTER_HEIGHT, characterName + " GIF Missing");
        }
    }

    private ImageIcon createPlaceholderIcon(int width, int height, String text) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(100, 100, 100, 150)); // Slightly transparent gray
        g2d.fillRect(0, 0, width, height);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16)); // Use a common font
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent() - fm.getDescent();
        g2d.drawString(text, (width - textWidth) / 2, height / 2 + textHeight / 2);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private Font loadCustomFont(String fontPath, int fontSize) {
        try {
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                System.err.println("Font file not found at: " + fontPath);
                return new Font("Monospaced", Font.PLAIN, fontSize);
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, (float) fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + fontPath);
            e.printStackTrace();
            return new Font("Monospaced", Font.PLAIN, fontSize);
        }
    }

    private void initializeUIComponents() {
        // ... (Load HP/Stamina bars, GIFs, Skill Buttons - unchanged) ...
        // --- Player 1 ---
        String p1HpBgPath = getHpBackgroundPath(firstPlayerCharacterName, true);
        player1HpBar = new StatusBar(p1HpBgPath, HP_BAR_FG_PATH);
        player1StaminaBar = new StatusBar(STAMINA_BAR_BG_PATH, STAMINA_BAR_FG_PATH);
        ImageIcon p1Gif = loadCharacterGif(firstPlayerCharacterName, true);
        player1CharacterLabel = new JLabel(p1Gif);


        ImageIcon skillIcon = new ImageIcon(getClass().getResource("/assets/FightingUI/skillbutton.png"));


        player1Skill1 = new JButton(String.format("Skill 1 (%.0f dmg / %.0f sta)", player1Stats.getSkillDamage(1), player1Stats.getSkillCost(1)));
        player1Skill2 = new JButton(String.format("Skill 2 (%.0f dmg / %.0f sta)", player1Stats.getSkillDamage(2), player1Stats.getSkillCost(2)));
        player1Skill3 = new JButton(String.format("Skill 3 (%.0f dmg / %.0f sta)", player1Stats.getSkillDamage(3), player1Stats.getSkillCost(3)));

        // Style for Player 1 Skills
        styleButton(player1Skill1, skillIcon);
        styleButton(player1Skill2, skillIcon);
        styleButton(player1Skill3, skillIcon);


        // --- Player 2 ---
        String p2HpBgPath = getHpBackgroundPath(secondPlayerCharacterName, false);
        player2HpBar = new StatusBar(p2HpBgPath, HP_BAR_FG_PATH);
        player2StaminaBar = new StatusBar(STAMINA_BAR_BG_PATH, STAMINA_BAR_FG_PATH);
        ImageIcon p2Gif = loadCharacterGif(secondPlayerCharacterName, false); // Use P2's character name even if AI
        player2CharacterLabel = new JLabel(p2Gif);
        player2Skill1 = new JButton(String.format("Skill 1 (%.0f dmg / %.0f sta)", player2Stats.getSkillDamage(1), player2Stats.getSkillCost(1)));
        player2Skill2 = new JButton(String.format("Skill 2 (%.0f dmg / %.0f sta)", player2Stats.getSkillDamage(2), player2Stats.getSkillCost(2)));
        player2Skill3 = new JButton(String.format("Skill 3 (%.0f dmg / %.0f sta)", player2Stats.getSkillDamage(3), player2Stats.getSkillCost(3)));

        // Style for Player 2 Skills
        styleButton(player2Skill1, skillIcon);
        styleButton(player2Skill2, skillIcon);
        styleButton(player2Skill3, skillIcon);

        // --- Center HUD ---
        timerDisplayLabel = new JLabel(String.valueOf(TURN_DURATION_SECONDS));
        timerDisplayLabel.setFont(new Font("Arial", Font.BOLD, 90));
        timerDisplayLabel.setForeground(Color.WHITE);
        timerDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);


        roundDisplayLabel = new JLabel("Round " + currentMatchRound);
        // Use a similar font or a distinct one. Let's use Cinzel like the wins, but smaller.
        Font roundFont = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 45); // Adjust size as needed
        roundDisplayLabel.setFont(roundFont);
        roundDisplayLabel.setForeground(new Color(255, 255, 0)); // Off-white or goldish?
        roundDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Font playerWinsFont = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 60); // CINZEL FONT

        // --- Win Counters ---
        // Modify labels for Arcade mode to show current streak
        player1WinsLabel = new JLabel("Wins: x0"); // Initialize to 0
        player1WinsLabel.setFont(playerWinsFont);

        player1WinsLabel.setForeground(new Color(79, 57, 51));
        player1WinsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        player2WinsLabel = new JLabel("Wins: x0"); // Initialize to 0
        player2WinsLabel.setFont(playerWinsFont);
        player2WinsLabel.setForeground(new Color(79, 57, 51));
        player2WinsLabel.setHorizontalAlignment(SwingConstants.RIGHT);


        Font wins = loadCustomFont("MimalaFinal/MimalaFinal/src/assets/Cinzel-Medium.ttf", 40);

        arcadeStreakLabel = new JLabel("Streak: " + arcadeWins); // Use static arcadeWins
        arcadeStreakLabel.setFont(wins); // Smaller font than round wins
        arcadeStreakLabel.setForeground(Color.YELLOW); // Different color?
        arcadeStreakLabel.setHorizontalAlignment(SwingConstants.CENTER);
        arcadeStreakLabel.setVisible(gameMode.equals("Arcade")); // Only visible in Arcade mode

        // --- Add to Panel ---
        this.add(player1WinsLabel);
        this.add(player2WinsLabel); // Add it even if hidden later
        this.add(arcadeStreakLabel);
        this.add(roundDisplayLabel);
        player1IdleIcon = (ImageIcon) player1CharacterLabel.getIcon();
        player2IdleIcon = (ImageIcon) player2CharacterLabel.getIcon();

        // Action Listeners (unchanged)
        player1Skill1.addActionListener(e -> handleAction(1));
        player1Skill2.addActionListener(e -> handleAction(2));
        player1Skill3.addActionListener(e -> handleAction(3));
        player2Skill1.addActionListener(e -> handleAction(1));
        player2Skill2.addActionListener(e -> handleAction(2));
        player2Skill3.addActionListener(e -> handleAction(3));

        // Set Initial Bar Values (unchanged)
        updateHpBars();
        updateStaminaBars();

        System.out.println("UI Components Initialized.");
    }


    // --- handleAction (Small modification for Arcade outcome) ---
    private void handleAction(int skillNumber) {
        if (!gameRunning) return;
        // ... (logic to determine actor/opponent, check stamina - unchanged) ...
        CharacterStats currentActorStats;
        double currentActorStamina;
        // Remove opponentCurrentHp as it's not used directly here
        String actorName;

        if (isPlayer1Turn) {
            actorName = firstPlayerCharacterName;
            currentActorStats = player1Stats;
            currentActorStamina = player1CurrentStamina;
        } else {
            if (isVsAI) { // Check includes Arcade now
                System.out.println("Cannot manually trigger AI action.");
                return; // Correctly prevents manual P2 action in PVC/Arcade
            }
            // This block only runs for human P2 in PVP
            actorName = secondPlayerCharacterName;
            currentActorStats = player2Stats;
            currentActorStamina = player2CurrentStamina;
        }

        double staminaCost = currentActorStats.getSkillCost(skillNumber);
        double damageDealt = currentActorStats.getSkillDamage(skillNumber);

        System.out.printf("%s attempts Skill %d (Cost: %.1f, Damage: %.1f)%n", actorName, skillNumber, staminaCost, damageDealt);


        if (currentActorStamina >= staminaCost) {
            stopTurnTimer();
            String skillAnimType = "Skill" + skillNumber;
            int skillAnimDuration = getAnimationDuration(skillAnimType);

            if (isPlayer1Turn) {
                playAnimation(player1CharacterLabel, firstPlayerCharacterName, skillAnimType, true, player1IdleIcon);
            } else { // Should only be human P2 here
                playAnimation(player2CharacterLabel, secondPlayerCharacterName, skillAnimType, false, player2IdleIcon);
            }

            // Timer to apply effects *after* animation starts
            javax.swing.Timer effectTimer = new javax.swing.Timer(skillAnimDuration, evt -> {
                if (!gameRunning) return; // Check if game ended during animation

                boolean targetIsP2 = isPlayer1Turn; // If P1's turn, target is P2

                // Apply Stamina Cost & Damage
                if (targetIsP2) { // P1's action affecting P2
                    player1CurrentStamina -= staminaCost;
                    player2CurrentHp -= damageDealt;
                    System.out.printf("P1 used Skill %d...%n", skillNumber);
                    if (damageDealt > 0) {
                        playAnimation(player2CharacterLabel, secondPlayerCharacterName, "GetHit", false, player2IdleIcon);
                    }
                } else { // P2's action affecting P1 (only in PVP)
                    player2CurrentStamina -= staminaCost;
                    player1CurrentHp -= damageDealt;
                    System.out.printf("P2 used Skill %d...%n", skillNumber);
                    if (damageDealt > 0) {
                        playAnimation(player1CharacterLabel, firstPlayerCharacterName, "GetHit", true, player1IdleIcon);
                    }
                }

                // Update UI
                updateHpBars();
                updateStaminaBars();

                // Check for match end condition (HP <= 0)
                boolean p1Died = player1CurrentHp <= 0;
                boolean p2Died = player2CurrentHp <= 0;

                if (p1Died || p2Died) {
                    // <<< CHANGE HERE: Call handleRoundEnd instead of handleMatchEnd >>>
                    handleRoundEnd(p1Died, p2Died); // Decide round winner and check match status
                    return; // Stop further processing like switchTurn
                }

                // If no one died, switch turn
                if (roundManager.isRoundComplete()) {
                    startNextRoundSequence();
                } else {
                    switchTurn();
                }
            });

            effectTimer.setRepeats(false);
            effectTimer.start();
        } else {
            System.out.println(actorName + ": Not enough stamina for Skill " + skillNumber);
            // Maybe add a small visual/audio cue for insufficient stamina?
        }
    }

    private void styleButton(JButton button, ImageIcon icon) {
        button.setIcon(icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void playAnimation(JLabel characterLabel, String characterName, String animationType, boolean isPlayer1, ImageIcon idleIcon) {

        // Construct the path to the animation GIF
        String flippedSuffix = isPlayer1 ? "" : "flipped";
        String gifFileName = characterName + animationType + flippedSuffix + ".gif";
        String path = CHAR_GIF_BASE_PATH + characterName + "/" + gifFileName;
        System.out.println("Attempting animation: " + path);

        URL gifUrl = getClass().getResource(path);
        ImageIcon animationIcon;

        if (gifUrl != null) {
            animationIcon = new ImageIcon(gifUrl); // Reload ImageIcon
            animationIcon.setImageObserver(characterLabel);
        } else {
            System.err.println("ERROR: Animation GIF not found: " + path);
            return; // Don't animate if file missing
        }

        // --- Set Icon ---
        characterLabel.setIcon(animationIcon);

        // --- Timer Logic ---

        // Select the correct timer and stop it if it's running for THIS player
        javax.swing.Timer relevantTimer = isPlayer1 ? player1AnimTimer : player2AnimTimer;
        if (relevantTimer != null && relevantTimer.isRunning()) {
            relevantTimer.stop();
        }

        // If it's a death animation, don't set a timer to revert
        if ("Death".equals(animationType)) {
            System.out.println(characterName + " death animation playing.");
            return;
        }

        // Get estimated duration for other animations
        int durationMs = getAnimationDuration(animationType);

        // Define the action to perform when the timer finishes (revert to idle)
        ActionListener revertAction = e -> {
            // Check if the label and idle icon are still valid
            if (idleIcon != null && characterLabel != null) {

                characterLabel.setIcon(idleIcon);
                System.out.println("Reverted " + characterName + " to Idle after " + animationType);

            }

            if (isPlayer1) player1AnimTimer = null; else player2AnimTimer = null;
        };

        // Create and start the NEW timer instance for the specific player
        relevantTimer = new javax.swing.Timer(durationMs, revertAction);
        relevantTimer.setRepeats(false);
        relevantTimer.start();

        // Store the reference to the running timer for this player
        if (isPlayer1) {
            player1AnimTimer = relevantTimer;
        } else {
            player2AnimTimer = relevantTimer;
        }
    }

    private int getAnimationDuration(String animationType) {
        switch (animationType) {
            case "Skill1": return 2000; // 2 seconds
            case "Skill2": return 2000; // 2 seconds
            case "Skill3": return 2000; // 2 seconds
            case "GetHit": return 1000;  // 1 seconds
            case "Death":  return 1000;// Death animation duration not needed here as we don't auto-revert it
            default:       return 1000; // Default duration
        }
    }

    private void positionUIComponents() {
        System.out.println("Positioning UI Components...");
        int panelWidth = getPreferredSize().width > 0 ? getPreferredSize().width : 1920;
        int panelHeight = getPreferredSize().height > 0 ? getPreferredSize().height : 1080;


        // --- Player 1 Positions --- (Keep existing positioning logic)
        int p1_X = PADDING + 50 + 5;
        int p1_Y_HP = PADDING + 24;
        player1HpBar.setBounds(p1_X, p1_Y_HP, BAR_WIDTH, HP_BAR_HEIGHT);
        int p1_Y_Stamina = p1_Y_HP + HP_BAR_HEIGHT + BAR_SPACING;
        player1StaminaBar.setBounds(p1_X, p1_Y_Stamina, BAR_WIDTH, STAMINA_BAR_HEIGHT);
        int p1_Char_X = -230;
        int p1_Char_Y = panelHeight - CHARACTER_HEIGHT - SKILL_AREA_BOTTOM_MARGIN - 55; // position character
        player1CharacterLabel.setBounds(p1_Char_X, p1_Char_Y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        int p1_Skill_Y_Start = panelHeight - SKILL_AREA_BOTTOM_MARGIN - SKILL_BUTTON_HEIGHT - 130;
        int p1_Skill_X = PADDING + 50;
        player1Skill1.setBounds(p1_Skill_X, p1_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player1Skill2.setBounds(p1_Skill_X + SKILL_BUTTON_WIDTH + SKILL_SPACING, p1_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player1Skill3.setBounds(p1_Skill_X + 2 * (SKILL_BUTTON_WIDTH + SKILL_SPACING), p1_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);

        // --- Player 2 Positions --- (Keep existing positioning logic)
        int p2_X = panelWidth - PADDING - BAR_WIDTH - 50 - 5;
        int p2_Y_HP = PADDING + 24;
        player2HpBar.setBounds(p2_X, p2_Y_HP, BAR_WIDTH, HP_BAR_HEIGHT);
        int p2_Y_Stamina = p2_Y_HP + HP_BAR_HEIGHT + BAR_SPACING;
        player2StaminaBar.setBounds(p2_X, p2_Y_Stamina, BAR_WIDTH, STAMINA_BAR_HEIGHT);
        int p2_Char_X = panelWidth + 260 - CHARACTER_WIDTH;
        int p2_Char_Y = panelHeight - CHARACTER_HEIGHT - SKILL_AREA_BOTTOM_MARGIN - 50; // position character
        player2CharacterLabel.setBounds(p2_Char_X, p2_Char_Y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        int p2_Skill_X = panelWidth - PADDING - SKILL_BUTTON_WIDTH - 50;
        int p2_Skill_Y_Start = panelHeight - SKILL_AREA_BOTTOM_MARGIN - SKILL_BUTTON_HEIGHT - 130;
        player2Skill3.setBounds(p2_Skill_X, p2_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player2Skill2.setBounds(p2_Skill_X - SKILL_BUTTON_WIDTH - SKILL_SPACING, p2_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player2Skill1.setBounds(p2_Skill_X - 2 *  (SKILL_BUTTON_WIDTH + SKILL_SPACING ), p2_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);

        // --- Win Counter Positions ---
        int winLabelWidth = 400;
        int winLabelHeight = 100;
        int winLabelY = panelHeight - 105; // 40px from the bottom


        player1WinsLabel.setBounds(PADDING + 57, winLabelY, winLabelWidth, winLabelHeight);
        player2WinsLabel.setBounds(panelWidth - winLabelWidth - PADDING - 65, winLabelY, winLabelWidth, winLabelHeight);

        // --- Center HUD Positions ---
        int timerWidth = 100; int timerHeight = 100;
        int timerX = panelWidth / 2 - timerWidth / 2;
        int timerY = PADDING + 50; // Keep timer position
        timerDisplayLabel.setBounds(timerX, timerY, timerWidth, timerHeight);

        int roundLabelWidth = 300; // Adjust width as needed
        int roundLabelHeight = 50; // Adjust height as needed
        int roundLabelX = panelWidth / 2 - roundLabelWidth / 2;
        // Position it slightly above the timer
        int roundLabelY = timerY - roundLabelHeight - 5; // 5px spacing above timer
        roundDisplayLabel.setBounds(roundLabelX, roundLabelY, roundLabelWidth, roundLabelHeight);

        int indicatorWidth = 300; int indicatorHeight = 40;
        int streakLabelWidth = 300;
        int streakLabelHeight = 60;
        int streakLabelX = panelWidth / 2 - streakLabelWidth / 2;
        int streakLabelY = timerWidth + timerHeight + 10; // Position below the timer
        arcadeStreakLabel.setBounds(streakLabelX, streakLabelY, streakLabelWidth, streakLabelHeight);
        // Move turn indicator down slightly to make room for round display
        //turnIndicatorLabel.setBounds(panelWidth / 2 - indicatorWidth / 2, PADDING + timerHeight + BAR_SPACING + 25, indicatorWidth, indicatorHeight);

        // Position round display above timer
        //roundDisplayLabel.setBounds(panelWidth / 2 - roundLabelWidth / 2, PADDING - roundLabelHeight + 15 , roundLabelWidth, roundLabelHeight);

        System.out.println("UI Components Positioned.");
    }

    public void updateWinCounters() {
        SwingUtilities.invokeLater(() -> {
            // Update round win labels based on current match score
            player1WinsLabel.setText("Wins: x" + player1RoundWins);
            player2WinsLabel.setText("Wins: x" + player2RoundWins);

            // Revalidate and repaint *only* the round win labels
            player1WinsLabel.revalidate();
            player1WinsLabel.repaint();
            player2WinsLabel.revalidate();
            player2WinsLabel.repaint();
        });
    }

    // --- Keyboard Input Setup ---
    private void setupKeyBindings() {
        System.out.println("Setting up key bindings...");

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Skill bindings
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "skill1Action");
        actionMap.put("skill1Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(1);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "skill2Action");
        actionMap.put("skill2Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(2);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "skill3Action");
        actionMap.put("skill3Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(3);
            }
        });

        // Skip turn binding (Spacebar)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "skipTurnAction");
        actionMap.put("skipTurnAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameRunning) return;

                boolean isHumanTurn = "PVP".equals(gameMode) || isPlayer1Turn;

                if (isHumanTurn) {
                    System.out.println("Player " + (isPlayer1Turn ? "1" : "2") + " skipped turn.");
                    stopTurnTimer();
                    switchTurn();
                } else {
                    System.out.println("Cannot skip turn (Not a human player's turn).");
                }
            }
        });

        // Pause game binding (ESC key)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pauseGame");
        actionMap.put("pauseGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionToPause(selectedMapPath);
            }
        });

        System.out.println("Key bindings set up.");
    }


    // MODIFIED: Uses Thread instead of Timer
    private void startTurnTimer() {
        stopTurnTimer(); // Ensure any previous timer thread is stopped

        remainingTime = TURN_DURATION_SECONDS;
        updateTimerLabel(); // Update display immediately

        turnTimerThread = new Thread(() -> {
            try {
                while (gameRunning && remainingTime > 0) {
                    Thread.sleep(1000); // Wait for 1 second
                    if (!gameRunning) break; // Exit if game ended during sleep

                    remainingTime--;
                    // Update UI on the Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(this::updateTimerLabel);

                    if (remainingTime <= 0) {
                        // Time's up! Switch turn on the EDT
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("Time's up for " + (isPlayer1Turn ? firstPlayerCharacterName : secondPlayerCharacterName));
                            if (gameRunning) { // Check again in case game ended while scheduling
                                switchTurn();
                            }
                        });
                        break; // Exit the timer loop
                    }
                }
            } catch (InterruptedException e) {
                // This is expected when we stop the timer
                System.out.println("Timer thread interrupted for " + (isPlayer1Turn ? firstPlayerCharacterName : secondPlayerCharacterName));
                Thread.currentThread().interrupt(); // Restore interrupt status
            } catch (Exception e) {
                System.err.println("Error in timer thread: " + e.getMessage());
                e.printStackTrace();
            } finally {
                System.out.println("Timer thread finished for " + (isPlayer1Turn ? firstPlayerCharacterName : secondPlayerCharacterName));
            }
        }, "TurnTimerThread-" + (isPlayer1Turn ? "P1" : "P2"));

        turnTimerThread.setDaemon(true); // Allows JVM to exit even if thread is running
        turnTimerThread.start();
        System.out.println((isPlayer1Turn ? firstPlayerCharacterName : secondPlayerCharacterName) + "'s turn started. Timer running.");
    }

    // Helper to update label (must be called via invokeLater from other threads)
    private void updateTimerLabel() {
        timerDisplayLabel.setText(String.valueOf(remainingTime));
    }

    private void startTurnTimerWithDelay() {
        // Wait for 3 seconds after the round image is shown
        Timer delayTimer = new Timer(3000, e -> {
            startTurnTimer(); // After 3 seconds, start the timer
        });
        delayTimer.setRepeats(false); // Don't repeat, only run once after 3 seconds
        delayTimer.start();
    }

    private void startNextRoundSequence() {
        if (!gameRunning) {
            System.out.println("startNextRoundSequence called but game not running.");
            return;
        }

        // 🧠 Check if we should show any round image based on state and flags
        if (currentMatchRound == 1 && player1RoundWins == 0 && player2RoundWins == 0 && !hasShownRound1) {
            showRoundImage(currentMatchRound, player1RoundWins, player2RoundWins);
            hasShownRound1 = true;
        } else if (currentMatchRound == 2 && (player1RoundWins == 1 || player2RoundWins == 1) &&
                (player1RoundWins + player2RoundWins == 1) && !hasShownRound2) {
            showRoundImage(currentMatchRound, player1RoundWins, player2RoundWins);
            hasShownRound2 = true;
        } else if (currentMatchRound == 3 && (player1RoundWins == 1 && player2RoundWins == 1 && !hasShownRound3)) {
            showRoundImage(currentMatchRound,player1RoundWins, player2RoundWins);
            hasShownRound3 = true;
        }

        startTurnTimerWithDelay();

        System.out.println("Starting next round sequence...");

        if (!roundManager.startNextRound()) {
            System.out.println("Max rounds reached for the match. Handling round timeout.");
            handleRoundTimeout();
            return;
        }

        double[] newStaminas = roundManager.applyStaminaRecovery(
                player1Stats, player2Stats,
                player1CurrentStamina, player2CurrentStamina
        );
        player1CurrentStamina = newStaminas[0];
        player2CurrentStamina = newStaminas[1];
        updateStaminaBars();

        isPlayer1Turn = roundManager.determineStartingPlayer();
        updateUIBasedOnTurn();

        if (!isPlayer1Turn && isVsAI) {
            System.out.println("[Log] AI starts round " + roundManager.getCurrentRound() + ". Triggering AI action...");
            SwingUtilities.invokeLater(this::performAiAction);
        }
    }

    private void showRoundImage(int currentMatchRound, int player1Wins, int player2Wins) {
        String imagePath = "";

        // Logic to select the image based on the number of wins
        if (currentMatchRound == 1 && (player1Wins == 0 && player2Wins == 0)) {
            // Round 1: Show before the first fight (both have 0 wins)
            imagePath = "MimalaFinal/MimalaFinal/src/assets/FightingUI/round1.png";
        } else if (currentMatchRound == 2 && (player1Wins == 1 || player2Wins == 1)) {
            // Round 2: Show after either player wins 1 match
            imagePath = "MimalaFinal/MimalaFinal/src/assets/FightingUI/round2.png";
        } else if (currentMatchRound == 3 && (player1Wins == 1 && player2Wins == 1)) {
            // Round 3: Show when both players have 1 win (tied)
            imagePath = "MimalaFinal/MimalaFinal/src/assets/FightingUI/round3.png";
        }

        // Only show image if it's a valid image path
        if (!imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            JLabel roundLabel = new JLabel(icon);

            // Adjust the position manually (adjust xOffset and yOffset as needed)
            int centerX = (getWidth() - icon.getIconWidth()) / 2;
            int centerY = (getHeight() - icon.getIconHeight()) / 2;

            roundLabel.setBounds(
                    centerX,  // Center horizontally
                    centerY,  // Center vertically
                    icon.getIconWidth(),
                    icon.getIconHeight()
            );


            roundLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add to layered pane so it floats above everything
            frame.getLayeredPane().add(roundLabel, JLayeredPane.POPUP_LAYER);
            frame.getLayeredPane().repaint();

            // Remove after 3 seconds (adjust as needed)
            Timer fadeTimer = new Timer(3000, e -> {
                frame.getLayeredPane().remove(roundLabel);
                frame.getLayeredPane().repaint();
            });
            fadeTimer.setRepeats(false);
            fadeTimer.start();
        }
    }

    private void handleRoundTimeout() {
        System.out.println("Handling Round Timeout (Max Rounds Reached)...");
        gameRunning = false; // Pause game logic
        stopTurnTimer();

        double finalP1Hp = Math.max(0, player1CurrentHp);
        double finalP2Hp = Math.max(0, player2CurrentHp);
        boolean p1WinsRoundOnTimeout = finalP1Hp > finalP2Hp;
        boolean draw = finalP1Hp == finalP2Hp;

        System.out.printf("Final Round Timeout HP - P1: %.1f, P2: %.1f%n", finalP1Hp, finalP2Hp);

        if (draw) {
            System.out.println("Final Round Timeout: Draw! Awarding round win to Player 2.");
            // Award round win to P2 on draw timeout? Or handle draws differently?
            player2RoundWins++;
        } else if (p1WinsRoundOnTimeout) {
            System.out.println("Final Round Timeout: Player 1 wins the round on HP.");
            player1RoundWins++;
        } else {
            System.out.println("Final Round Timeout: Player 2 wins the round on HP.");
            player2RoundWins++;
        }

        updateWinCounters(); // Update display with final round result

        // Now check if this final round win decided the match
        checkMatchCompletionAndProceed();
    }

    private void handleMatchCompletion(boolean player1WonMatch) {
        System.out.println("Handling Match Completion. Player 1 Won Match: " + player1WonMatch);
        stopMusic();

        if (gameMode.equals("Arcade")) {
            if (player1WonMatch) {
                arcadeWins++; // Increment streak

                // <<< ADDED: Directly update streak label text >>>
                final int currentStreak = arcadeWins; // Capture current value for EDT
                SwingUtilities.invokeLater(() -> {
                    if (arcadeStreakLabel != null) { // Check if label exists
                        arcadeStreakLabel.setText("Streak: " + currentStreak);
                    }
                });
                // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

                // gameLog.recordArcadeWin(Player1Name.playerName); // Optional logging
                System.out.println("Arcade match win! Streak: " + arcadeWins + ". Showing victory screen...");
                showTemporaryVictoryOverlayWithClickListener(); // Show overlay, wait for click

            } else {
                // ... (Loss logic: recordArcadeRun, transitionToResultScreen) ...
                System.out.println("Arcade run ended. Final Wins: " + arcadeWins);
                gameLog.recordArcadeRun(Player1Name.player1Name, arcadeWins);
                transitionToResultScreen(false);
            }
        } else {
            // ... (PvP/PvC logic: Call correct GameLog method, transitionToResultScreen) ...
            System.out.println("Standard match completed.");
            gameLog.recordGame(Player1Name.player1Name);// Use your specific log methods here
            transitionToResultScreen(player1WonMatch);
        }
    }


    public void recordArcadeWin(String playerName) {
        // Optional: Log each match win within an Arcade run if desired
        System.out.println("Arcade match win recorded for " + playerName);
        // You might update a temporary counter or just log to the detailed file
        // logMatchDetails("ArcadeWin", playerName + " won match", playerName, "AI", GameScreen.arcadeWins);
    }


    private void showTemporaryVictoryOverlayWithClickListener() {
        SwingUtilities.invokeLater(() -> {
            if (victoryOverlayLabel != null) {
                hideTemporaryVictoryOverlay(); // Clean up previous if any
            }

            // Load Icon
            ImageIcon victoryIcon = null;
            try {
                java.net.URL imgUrl = getClass().getResource(ARCADE_VICTORY_IMG_PATH);
                if (imgUrl != null) {
                    victoryIcon = new ImageIcon(imgUrl);
                } else {
                    System.err.println("Victory overlay image not found: " + ARCADE_VICTORY_IMG_PATH);
                    // Maybe proceed without overlay? Or show error? For now, just return.
                    proceedToNextArcadeFight(); // Or handle error differently
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error loading victory overlay image: " + e);
                proceedToNextArcadeFight(); // Proceed without overlay on error
                return;
            }

            // Create Label
            victoryOverlayLabel = new JLabel(victoryIcon);
            victoryOverlayLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            victoryOverlayLabel.setOpaque(false);

            // --- Create and Add Mouse Listener ---
            victoryClickListener = new MouseAdapter() {
                private boolean clicked = false; // Prevent multiple triggers

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (clicked) return; // Only proceed once
                    clicked = true;

                    System.out.println("Victory overlay clicked. Proceeding to next fight...");
                    hideTemporaryVictoryOverlay(); // Remove the overlay
                    proceedToNextArcadeFight(); // Execute the next fight logic
                }
            };
            victoryOverlayLabel.addMouseListener(victoryClickListener);
            // --- End Listener ---

            // Add to Layered Pane
            frame.getLayeredPane().add(victoryOverlayLabel, JLayeredPane.POPUP_LAYER);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
            victoryOverlayLabel.requestFocusInWindow(); // Try to ensure it can receive clicks
        });
    }

    // <<< NEW Helper Method containing the logic to start the next fight >>>
    // This was previously inside the timer's action listener
    private void proceedToNextArcadeFight() {
        System.out.println("Setting up next opponent...");
        String nextOpponent = selectRandomOpponent(firstPlayerCharacterName);
        String nextMap = selectRandomMap();

        if (nextOpponent == null || nextMap == null) {
            System.err.println("Error selecting next opponent/map. Ending Arcade run.");
            // Record score achieved *before* ending due to error
            gameLog.recordArcadeRun(PlayerName.playerName, arcadeWins);
            // Transition to the standard ResultScreen showing defeat
            transitionToResultScreen(false);
            return;
        }

        System.out.println("Next Opponent: " + nextOpponent);
        System.out.println("Next Map: " + nextMap);

        // Transition to a new GameScreen instance
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(new GameScreen(frame, firstPlayerCharacterName, nextOpponent, nextMap, "Arcade"));
            frame.revalidate();
            frame.repaint();
        });
    }

    private void showTemporaryVictoryOverlay() {
        // Ensure running on EDT for UI modification
        SwingUtilities.invokeLater(() -> {
            if (victoryOverlayLabel != null) { // Remove previous if any somehow exists
                hideTemporaryVictoryOverlay();
            }
            ImageIcon victoryIcon = null;
            try {
                java.net.URL imgUrl = getClass().getResource(ARCADE_VICTORY_IMG_PATH);
                if (imgUrl != null) {
                    victoryIcon = new ImageIcon(imgUrl);
                } else {
                    System.err.println("Victory overlay image not found: " + ARCADE_VICTORY_IMG_PATH);
                    return; // Don't show overlay if image missing
                }
            } catch (Exception e) {
                System.err.println("Error loading victory overlay image: " + e);
                return;
            }


            victoryOverlayLabel = new JLabel(victoryIcon);
            // Set bounds to cover the entire frame
            victoryOverlayLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            victoryOverlayLabel.setOpaque(false); // Make label transparent if image has transparency

            // Add to the popup layer of the frame's layered pane
            frame.getLayeredPane().add(victoryOverlayLabel, JLayeredPane.POPUP_LAYER);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
        });
    }

    // <<< NEW Helper Method to hide victory overlay >>>
    private void hideTemporaryVictoryOverlay() {
        SwingUtilities.invokeLater(() -> {
            if (victoryOverlayLabel != null) {
                // Remove listener to prevent further clicks and memory leaks
                if (victoryClickListener != null) {
                    victoryOverlayLabel.removeMouseListener(victoryClickListener);
                    victoryClickListener = null; // Clear reference
                }
                // Remove label from pane
                frame.getLayeredPane().remove(victoryOverlayLabel);
                frame.getLayeredPane().revalidate();
                frame.getLayeredPane().repaint();
                victoryOverlayLabel = null; // Release reference
            }
        });
    }

    private String selectRandomOpponent(String playerCharacter) {
        Set<String> allNames = CharacterDataLoader.getAllCharacterNames();
        if (allNames.isEmpty()) return null;
        List<String> possibleOpponents = allNames.stream()
                .filter(name -> !name.equals(playerCharacter))
                .collect(Collectors.toList());
        if (possibleOpponents.isEmpty()) {
            possibleOpponents = new ArrayList<>(allNames); // Fallback
            if (possibleOpponents.isEmpty()) return null;
        }
        return possibleOpponents.get(random.nextInt(possibleOpponents.size()));
    }

    private String selectRandomMap() {
        if (mapPaths == null || mapPaths.length == 0) return null;
        return mapPaths[random.nextInt(mapPaths.length)];
    }



    // MODIFIED: Stops the timer Thread
    private void stopTurnTimer() {
        if (turnTimerThread != null && turnTimerThread.isAlive()) {
            turnTimerThread.interrupt(); // Signal the thread to stop
            try {
                turnTimerThread.join(100); // Wait briefly for it to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            turnTimerThread = null; // Release the reference
            System.out.println("Timer thread stopped.");
        }
    }

    // MODIFIED: Handles turn switching and round progression
    private void switchTurn() {
        if (!gameRunning) return;

        System.out.println("Switching turn sequence...");
        stopTurnTimer(); // Stop the timer for the player who just finished

        // 1. Record that the player took their turn this round
        roundManager.recordTurnTaken(isPlayer1Turn);

        // 2. Check if the round is now complete (both players had a turn)
        if (roundManager.isRoundComplete()) {
            // Start the next round sequence
            System.out.println("Round complete, starting next round sequence...");
            // Add a small delay maybe? Or go straight to next round.
            startNextRoundSequence();
        } else {
            // Round is not complete, switch to the other player
            System.out.println("Round not complete, switching player...");
            isPlayer1Turn = !isPlayer1Turn; // Flip the turn indicator

            // Update UI for the player whose turn it is now
            updateUIBasedOnTurn();

            // Start the turn timer/AI action for the next player
            if (!isPlayer1Turn && isVsAI) {
                System.out.println("[Log] Switching to AI's turn. Triggering AI action..."); // ADD LOG
                SwingUtilities.invokeLater(this::performAiAction);
            } else {
                System.out.println("[Log] Switching to Human's turn. Starting timer..."); // ADD LOG
                startTurnTimer(); // Start timer for human player
            }
        }
    }



    // MODIFIED: Central method to handle player actions using skill number


    private void resetForNewRound() {
        // <<< REMOVE Arcade Check >>>
        // if(gameMode.equals("Arcade")) { ... return; } // REMOVE THIS

        System.out.println("Resetting for new round...");

        // Reset HP/Stamina
        if (player1Stats != null) {
            player1CurrentHp = player1Stats.getMaxHp();
            player1CurrentStamina = player1Stats.getMaxStamina();
        } else { System.err.println("Cannot reset P1 stats - player1Stats is null");}
        if (player2Stats != null) {
            player2CurrentHp = player2Stats.getMaxHp();
            player2CurrentStamina = player2Stats.getMaxStamina();
        } else { System.err.println("Cannot reset P2 stats - player2Stats is null");}

        // Update UI
        updateHpBars();
        updateStaminaBars();
        updateWinCounters(); // Show current round wins

        currentMatchRound++; // Increment for the next round
        final int roundToShow = currentMatchRound; // Capture for lambda
        SwingUtilities.invokeLater(() -> {
            roundDisplayLabel.setText("Round " + roundToShow);
            roundDisplayLabel.revalidate();
            roundDisplayLabel.repaint();
        });
        System.out.println("Match round incremented to: " + roundToShow);

        // Reset animations to idle
        if (player1IdleIcon != null) player1CharacterLabel.setIcon(player1IdleIcon);
        if (player2IdleIcon != null) player2CharacterLabel.setIcon(player2IdleIcon);
        else {
            player2IdleIcon = loadCharacterGif(secondPlayerCharacterName, false);
            player2CharacterLabel.setIcon(player2IdleIcon);
        }

        // Re-enable game logic
        gameRunning = true;

        // Start the next round sequence
        SwingUtilities.invokeLater(this::startNextRoundSequence);

        System.out.println("Reset for new round complete.");
    }

    // MODIFIED: AI Logic uses stats and skill numbers
    private void performAiAction() {
        if (!gameRunning || isPlayer1Turn) return; // Should only run on AI's turn

        System.out.println("AI (" + secondPlayerCharacterName + ") is thinking...");

        // Simple AI: Choose a random valid skill it can afford
        int skillChoice = -1;

        java.util.List<Integer> possibleSkills = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (player2CurrentStamina >= player2Stats.getSkillCost(i)) {
                possibleSkills.add(i);
            }
        }

        if (!possibleSkills.isEmpty()) {
            skillChoice = possibleSkills.get(random.nextInt(possibleSkills.size()));
        } else {
            System.out.println("AI has insufficient stamina for any skill. Skipping turn.");
            stopTurnTimer(); // Stop timer as turn is skipped
            SwingUtilities.invokeLater(this::switchTurn); // Call switchTurn to record AI's (skipped) turn
            return;
        }

        final int finalSkillChoice = skillChoice;

        // Add delay
        javax.swing.Timer aiDelayTimer = new javax.swing.Timer(3000, e -> {
            if (gameRunning && !isPlayer1Turn) {
                stopTurnTimer(); // Stop timer just before executing the action
                handleAiSkillExecution(finalSkillChoice);
            }
        });

        aiDelayTimer.setRepeats(false);
        aiDelayTimer.start();
    }

    // MODIFIED: Separated AI skill execution, uses skill number and stats
    private void handleAiSkillExecution(int skillNumber) {
        if (!gameRunning || isPlayer1Turn) return; // Final check

        System.out.printf("AI (%s) executing Skill %d...%n", secondPlayerCharacterName, skillNumber);

        String skillAnimType = "Skill" + skillNumber;
        int skillAnimDuration = getAnimationDuration(skillAnimType);

        playAnimation(player2CharacterLabel, secondPlayerCharacterName, skillAnimType, false, player2IdleIcon);

        // Timer to apply effects *after* animation starts
        javax.swing.Timer aiEffectTimer = new javax.swing.Timer(skillAnimDuration, evt -> {
            if (!gameRunning) return; // Check if game ended during animation

            // Apply Effects
            double staminaCost = player2Stats.getSkillCost(skillNumber);
            double damageDealt = player2Stats.getSkillDamage(skillNumber);

            player2CurrentStamina -= staminaCost;
            player1CurrentHp -= damageDealt;
            System.out.printf("AI used Skill %d, dealt %.1f damage.%n", skillNumber, damageDealt);

            // Play player's GetHit animation
            if (damageDealt > 0) {
                playAnimation(player1CharacterLabel, firstPlayerCharacterName, "GetHit", true, player1IdleIcon);
            }

            // Update UI Bars
            updateHpBars();
            updateStaminaBars();

            // Check for match end condition (HP <= 0)
            boolean p1Died = player1CurrentHp <= 0;
            // boolean p2Died = player2CurrentHp <= 0; // AI won't die from its own attack

            if (p1Died) {
                // <<< CHANGE HERE: Call handleRoundEnd instead of handleMatchEnd >>>
                handleRoundEnd(true, false); // P1 died, P2 won round
                return; // Stop further processing
            }

            // If no one died, switch turn back to Player 1
            if (roundManager.isRoundComplete()) {
                startNextRoundSequence();
            } else {
                switchTurn(); // Switch back to P1
            }
        });
        aiEffectTimer.setRepeats(false);
        aiEffectTimer.start();
    }

    private void handleRoundEnd(boolean p1HasDied, boolean p2HasDied) {
        if (p1HasDied && p2HasDied) { // Handle Double KO
            System.out.println("DOUBLE KO!");
            // Decide outcome: e.g., P2 wins round, or no one gets a point?
            // Let's award round to P2 for simplicity here. Modify if needed.
            p1HasDied = true;
            p2HasDied = false;
        }

        System.out.println("Handling Round End. P1 Died: " + p1HasDied + ", P2 Died: " + p2HasDied);
        gameRunning = false; // Pause game logic during sequence
        stopTurnTimer();
        if (player1AnimTimer != null && player1AnimTimer.isRunning()) player1AnimTimer.stop();
        if (player2AnimTimer != null && player2AnimTimer.isRunning()) player2AnimTimer.stop();

        boolean didP1WinThisRound;

        if (p1HasDied) {
            player2RoundWins++; // P2 wins the round
            didP1WinThisRound = false;
            System.out.println("Player 2 (" + secondPlayerCharacterName + ") wins Round " + roundManager.getCurrentRound() + ". Score: P1 " + player1RoundWins + " - P2 " + player2RoundWins);
            playAnimation(player1CharacterLabel, firstPlayerCharacterName, "Death", true, player1IdleIcon);
        } else { // p2HasDied must be true
            player1RoundWins++; // P1 wins the round
            didP1WinThisRound = true;
            System.out.println("Player 1 (" + firstPlayerCharacterName + ") wins Round " + roundManager.getCurrentRound() + ". Score: P1 " + player1RoundWins + " - P2 " + player2RoundWins);
            playAnimation(player2CharacterLabel, secondPlayerCharacterName, "Death", false, player2IdleIcon);
        }
        updateWinCounters(); // Show updated round score

        // Timer to wait for death animation before checking match status
        if (deathSequenceTimer != null && deathSequenceTimer.isRunning()) {
            deathSequenceTimer.stop();
        }
        int deathAnimTime = getAnimationDuration("Death");
        System.out.println("Starting death sequence timer (" + deathAnimTime + "ms)");

        deathSequenceTimer = new Timer(deathAnimTime + 300, e -> { // Wait for animation + buffer
            System.out.println("Death sequence timer finished. Checking match completion...");
            checkMatchCompletionAndProceed(); // <<< NEW METHOD CALL >>>
        });
        deathSequenceTimer.setRepeats(false);
        deathSequenceTimer.start();
    }

    private void checkMatchCompletionAndProceed() {
        boolean matchIsOver = player1RoundWins >= ROUNDS_TO_WIN || player2RoundWins >= ROUNDS_TO_WIN;

        if (matchIsOver) {
            System.out.println("Match score limit reached.");
            boolean player1WonMatch = player1RoundWins >= ROUNDS_TO_WIN;
            handleMatchCompletion(player1WonMatch); // <<< CALL Renamed method >>>
        } else {
            System.out.println("Match continues to next round.");
            // Match not over, reset characters and start next round
            resetForNewRound();
        }
    }
    /**
     * Cleans up game state and switches the view to the GameOverScreen.
     */

    private void transitionToResultScreen(boolean player1WonMatch) {
        System.out.println("Executing transitionToResultScreen... Mode: " + gameMode + ", P1 Won: " + player1WonMatch);

        // (Stop timers logic might be redundant if already stopped in handleMatchEnd, but safe to keep)
        stopTurnTimer();
        if (player1AnimTimer != null) player1AnimTimer.stop();
        if (player2AnimTimer != null) player2AnimTimer.stop();
        if (deathSequenceTimer != null) deathSequenceTimer.stop();

        boolean didP1WinThisRound = player2CurrentHp <= 0 && player1CurrentHp > 0;
        boolean didP2WinThisRound  = player1CurrentHp <= 0 && player2CurrentHp > 0;
        // Determine result
        boolean p1WinsBool = player2CurrentHp <= 0 && player1CurrentHp > 0;
        boolean p2WinsBool = player1CurrentHp <= 0 && player2CurrentHp > 0;

        boolean player1Won = p1WinsBool;
        boolean player2Won = p2WinsBool;

        // --- Log Game Results (if needed, e.g., for PVP/PVC) ---
        // ... (logging logic if applicable to non-arcade or arcade loss) ...
        if (!gameMode.equals("Arcade")) { // Log non-arcade results
            // Example: Log PVP/PVC results here using GameLog
            if(gameMode.equals("PVP")){
                if (didP1WinThisRound) {
                    GameScreen.p1Wins++;
                    GameScreen.p2Lose++;
                    GameScreen.lastWinner = 1;
                    new GameLog().recordGame(Player1Name.player1Name);
                } else if (didP2WinThisRound) {
                    GameScreen.p2Wins++;
                    GameScreen.p1Lose++;
                    GameScreen.lastWinner = 2;
                    new GameLog().recordGame(Player2Name.player2Name);
                }
            } else if(gameMode.equals("PVC")){
                if (player1RoundWins >= ROUNDS_TO_WIN || player2RoundWins >= ROUNDS_TO_WIN) {
                    // Only record the win when match is complete
                    if (isVsAI) {
                        boolean playerWon = player1RoundWins >= ROUNDS_TO_WIN;
                        new GameLog().recordPVEGame(Player1Name.player1Name, playerWon);
                    } else {
                        String winner = player1RoundWins >= ROUNDS_TO_WIN ? Player1Name.player1Name : Player2Name.player2Name;
                        new GameLog().recordGame(winner);
                    }
                    new GameLog().saveStatsToFile();
                } else {
                    // Start next round
                    // Start next round
                    resetForNewRound();
                }
            }

        } else { // This is an Arcade Loss
            System.out.println("Showing final result screen for Arcade loss.");
            // Arcade score was already logged in handleMatchEnd
        }

        // --- Transition to ResultScreen UI (Overlay before GameOverScreen) ---
        ResultScreen resultOverlay = new ResultScreen(frame, player1WonMatch,
                firstPlayerCharacterName, secondPlayerCharacterName,
                selectedMapPath, gameMode);

        // Ensure UI updates are on EDT
        SwingUtilities.invokeLater(() -> {
            frame.getLayeredPane().add(resultOverlay, JLayeredPane.POPUP_LAYER);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
            // ResultScreen handles showing the correct image internally
        });
    }


    // Make sure endGame is removed or no longer called on death by HP
    // public void endGame(boolean maxRoundsReached) { ... } // Keep if needed for MAX_ROUNDS end condition

    // --- UI Update Helpers ---

    private void updateHpBars() {
        if (player1HpBar == null || player2HpBar == null || player1Stats == null || player2Stats == null) return; // Safety check
        double p1HpDisplay = Math.max(0, player1CurrentHp);
        double p2HpDisplay = Math.max(0, player2CurrentHp);
        player1HpBar.setValue(p1HpDisplay / player1Stats.getMaxHp());
        player2HpBar.setValue(p2HpDisplay / player2Stats.getMaxHp());
        // Force repaint sometimes helps with visual updates
        player1HpBar.repaint();
        player2HpBar.repaint();
    }

    private void updateStaminaBars() {
        if (player1StaminaBar == null || player2StaminaBar == null || player1Stats == null || player2Stats == null) return; // Safety check
        player1StaminaBar.setValue(player1CurrentStamina / player1Stats.getMaxStamina());
        player2StaminaBar.setValue(player2CurrentStamina / player2Stats.getMaxStamina());
        player1StaminaBar.repaint();
        player2StaminaBar.repaint();
    }

    // Update win counters needs adjustment for Arcade

    private void updateUIBasedOnTurn() {
        if (!gameRunning) return; // Don't update if game over

        boolean isHumanP2 = !isVsAI;

        // Enable/Disable P1 buttons
        player1Skill1.setEnabled(isPlayer1Turn);
        player1Skill2.setEnabled(isPlayer1Turn);
        player1Skill3.setEnabled(isPlayer1Turn);

        // Enable/Disable P2 buttons only if P2 is human AND it's their turn
        player2Skill1.setEnabled(!isPlayer1Turn && isHumanP2);
        player2Skill2.setEnabled(!isPlayer1Turn && isHumanP2);
        player2Skill3.setEnabled(!isPlayer1Turn && isHumanP2);

        // Update turn indicator text
        //turnIndicatorLabel.setText(isPlayer1Turn ? firstPlayerCharacterName + "'s Turn" : secondPlayerCharacterName + "'s Turn");

        // Update highlight border
        //player1CharacterLabel.setBorder(isPlayer1Turn ? BorderFactory.createLineBorder(Color.YELLOW, 3) : null);
        // Only highlight P2 if it's their turn (human or AI)
        //player2CharacterLabel.setBorder(!isPlayer1Turn ? BorderFactory.createLineBorder(Color.YELLOW, 3) : null);

        // Ensure panel has focus for key bindings when a human player's turn starts
        if ((isPlayer1Turn || (!isPlayer1Turn && isHumanP2))) {
            SwingUtilities.invokeLater(this::requestFocusInWindow);
        }
    }


    // MODIFIED: Handles game end conditions
    private void endGame(boolean maxRoundsReached) {
        if (!gameRunning) return; // Prevent multiple calls

        System.out.println("Ending Game...");
        gameRunning = false; // Signal threads to stop
        stopTurnTimer(); // Stop the timer thread immediately

        // Disable all buttons
        player1Skill1.setEnabled(false); player1Skill2.setEnabled(false); player1Skill3.setEnabled(false);
        player2Skill1.setEnabled(false); player2Skill2.setEnabled(false); player2Skill3.setEnabled(false);

        // Determine winner based on HP or rounds
        String winnerMessage;
        double finalP1Hp = Math.max(0, player1CurrentHp);
        double finalP2Hp = Math.max(0, player2CurrentHp);

        if (maxRoundsReached) {
            // ... (Determine winner by HP as before) ...
            winnerMessage = determineWinnerByHp(finalP1Hp, finalP2Hp);
        } else { // Game ended due to HP depletion
            // ... (Determine winner by HP as before) ...
            winnerMessage = determineWinnerByHp(finalP1Hp, finalP2Hp);
        }


        turnIndicatorLabel.setText("GAME OVER: " + winnerMessage);
        turnIndicatorLabel.setForeground(Color.RED);
        // Display the round the game ended on
        //roundDisplayLabel.setText("Final Round: " + roundManager.getCurrentRound());
        System.out.println("Game Over! " + winnerMessage + " (Ended on Round " + roundManager.getCurrentRound() + ")");
    }

    private String determineWinnerByHp(double p1Hp, double p2Hp) {
        if (p1Hp > p2Hp) {
            return firstPlayerCharacterName + " Wins!";
        } else if (p2Hp > p1Hp) {
            return secondPlayerCharacterName + " Wins!";
        } else {
            return "It's a Draw!";
        }
    }

    private void addAllComponents() {
        // Add Player 1 and Player 2 components to the GameScreen (this panel)
        this.add(player1CharacterLabel);
        this.add(player2CharacterLabel);
        this.add(player1HpBar);
        this.add(player2HpBar);
        this.add(player1StaminaBar);
        this.add(player2StaminaBar);

        // Add skill buttons
        this.add(player1Skill1);
        this.add(player1Skill2);
        this.add(player1Skill3);
        this.add(player2Skill1);
        this.add(player2Skill2);
        this.add(player2Skill3);

        // Add the win counters
        this.add(player1WinsLabel);
        this.add(player2WinsLabel);
        this.add(roundDisplayLabel);
        // Add the timer display
        this.add(timerDisplayLabel);
        this.add(arcadeStreakLabel);
        // (Add any other components like background images, special UI elements, etc.)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            // Scale map to fit panel size
            g.drawImage(mapImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // Draw a fallback background if map is missing
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Map image missing: /" + selectedMapPath, 50, 50);
        }
        // Swing components (labels, buttons, bars) are painted *after* this method
    }


    public void cleanup() {
        System.out.println("Cleaning up GameScreen...");
        gameRunning = false; // Ensure flag is set
        stopTurnTimer(); // Explicitly stop the timer thread
        // Remove listeners? Not strictly necessary if the panel/frame is disposed.
    }


    private void playMusic(String filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/" + filePath); // Leading slash is important
            if (audioSrc == null) {
                System.err.println("Music file not found in resources: " + filePath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
            System.out.println("Music started successfully");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }



    private void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }
    }

// Inside GameScreen.java

    // Add a flag to track pause state
    private volatile boolean isPaused = false;
    // Store remaining time when paused
    private int timeWhenPaused = 0;

// --- Other GameScreen code ---

    /**
     * Stops active timers and threads, pausing the game logic.
     */
    public void pauseGame() {
        if (!gameRunning || isPaused) return; // Don't pause if not running or already paused

        System.out.println("Pausing game...");
        isPaused = true;

        // Stop the main turn timer thread and store remaining time
        timeWhenPaused = remainingTime; // Store remaining time
        stopTurnTimer(); // Stops the thread

        // Stop active animation/effect timers (check if they exist and are running)
        if (player1AnimTimer != null && player1AnimTimer.isRunning()) {
            player1AnimTimer.stop();
            System.out.println("Stopped P1 Anim Timer for pause.");
        }
        if (player2AnimTimer != null && player2AnimTimer.isRunning()) {
            player2AnimTimer.stop();
            System.out.println("Stopped P2 Anim Timer for pause.");
        }
        // Stop other timers like effectTimer or aiDelayTimer if they might be active
        // Note: Finding references to temporary timers like 'effectTimer' or 'aiDelayTimer'
        // created inside methods is harder. Consider making them member fields if pausing them is critical.
        // For now, pausing the main turn timer is the most important.

        if (deathSequenceTimer != null && deathSequenceTimer.isRunning()) {
            deathSequenceTimer.stop(); // Stop death sequence if paused during it
            System.out.println("Stopped Death Timer for pause.");
        }


        // Stop game music
        stopMusic();

        System.out.println("Game Paused. Remaining time stored: " + timeWhenPaused);
    }

    /**
     * Resumes active timers and threads from their paused state.
     */
    public void resumeGame() {
        if (!gameRunning || !isPaused) return; // Don't resume if not running or not paused

        System.out.println("Resuming game...");
        isPaused = false;

        // Resume the main turn timer using the stored remaining time
        remainingTime = timeWhenPaused; // Restore remaining time
        if (remainingTime > 0) {
            System.out.println("Restarting turn timer with " + remainingTime + " seconds.");
            startTurnTimer(); // Restarts the thread
        } else {
            // If time ran out exactly when pausing, or was already 0, switch turn?
            // Or just let it be 0. Switching turn might be complex here.
            System.out.println("Timer was at 0 when paused, not restarting timer.");
            // Ensure timer label reflects 0 if needed
            SwingUtilities.invokeLater(this::updateTimerLabel);
        }


        // Do NOT automatically restart animation timers. They likely finished or
        // restarting them mid-animation would look strange. Let the game flow continue.

        // Resume game music
        playMusic("assets/FightingUI/fightmusic.wav"); // Or use a variable for the current track

        // Ensure the panel regains focus for keybindings
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        System.out.println("Game Resumed.");
    }


    // --- Modify transitionToPause ---
    private void transitionToPause(String mapResourcePath) { // mapResourcePath seems unused here?
        // if (mapResourcePath == null) return; // Keep check if needed

        if (!gameRunning || isPaused) return; // Don't allow pausing if game over or already paused

        // <<< CALL pauseGame() FIRST >>>
        pauseGame();

        // Now switch the view
        SwingUtilities.invokeLater(() -> {
            Pause pauseScreen = new Pause(frame, this); // 'this' is the GameScreen instance
            frame.setContentPane(pauseScreen);
            frame.revalidate();
            frame.repaint();
            // Request focus for the pause screen if it needs keyboard input
            pauseScreen.requestFocusInWindow();
        });
    }



}
