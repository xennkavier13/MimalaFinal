package state;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.util.Random;

import state.UI.Pause;
import state.UI.ResultScreen;
import state.UI.StatusBar;
import state.character.CharacterDataLoader;
import state.character.CharacterStats;
import util.GameLog;
import util.RoundManager;
// Removed: import javax.swing.Timer; // We are replacing this

public class GameScreen extends JPanel {
    private final JFrame frame;
    private final String firstPlayerCharacterName; // Renamed for clarity
    private final String secondPlayerCharacterName; // Renamed for clarity
    private final String selectedMapPath;
    private ImageIcon mapImage;
    // --- Animation State ---
    private ImageIcon player1IdleIcon; // To store the idle state easily
    private ImageIcon player2IdleIcon;
    private javax.swing.Timer animationRevertTimer; // Timer to switch back to idle
    private boolean isAnimating = false; // Simple flag to prevent overlapping actions during animation
    private javax.swing.Timer player1AnimTimer;
    private javax.swing.Timer player2AnimTimer;
    private javax.swing.Timer deathSequenceTimer;
    public static int p1Wins = 0;
    public static int p1Lose = 0;
    public static int p2Wins = 0;
    public static int p2Lose = 0;
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
    //private JLabel roundDisplayLabel; // New label for rounds

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
    // ... (rest of the layout constants)

    // --- Asset Path Constants --- (Keep as they are)
    private static final String HP_BAR_BG_BASE_PATH = "/assets/FightingUI/HealthBarBorder.png";
    private static final String HP_BAR_FG_PATH = "/assets/FightingUI/HealthBar_AllCharacters.png";
    private static final String STAMINA_BAR_BG_PATH = "/assets/FightingUI/StaminaBar1.png";
    private static final String STAMINA_BAR_FG_PATH = "/assets/FightingUI/StaminaBar2.png";
    private static final String CHAR_GIF_BASE_PATH = "/assets/FightingUI/Mimala_Characters/";


    // --- Game State ---
    private volatile boolean isPlayer1Turn = true; // Tracks whose turn it is NOW
    private volatile boolean gameRunning = true;
    private Thread turnTimerThread;
    private volatile int remainingTime;
    private static final int TURN_DURATION_SECONDS = 15;
    private final Random random = new Random(); // Still needed for AI, maybe other things
    private static final String AI_PLAYER_NAME = "Computer";
    public static boolean isVsAI;

    // --- Rounds & Stamina Recovery
    private static final int MAX_ROUNDS = 10;
    private RoundManager roundManager;
    private static final double STAMINA_RECOVERY_PER_ROUND = 10.0; // Recover 30 stamina points

    private final String gameMode; // <<< ADD field for game mode

    private Pause pause;

    private Clip music;

    public GameScreen(JFrame frame, String firstPlayerCharacter, String secondPlayerCharacter, String selectedMapResourcePath, String gameMode) {
        this.frame = frame;
        this.firstPlayerCharacterName = firstPlayerCharacter;
        this.secondPlayerCharacterName = secondPlayerCharacter;
        this.selectedMapPath = selectedMapResourcePath;
        this.gameMode = gameMode;

        // --- Set isVsAI based on gameMode ---
        this.isVsAI = this.gameMode.equals("PVC"); // <<< Use gameMode now
        System.out.println("GameScreen initialized. Mode: " + this.gameMode + ", Is Vs AI: " + this.isVsAI);

        // --- Initialize Round Manager FIRST ---
        this.roundManager = new RoundManager(10); // Initialize here! TURNS

        // --- Load Character Stats ---
        loadCharacterStats(); // Now safe to call

        // --- Basic Panel Setup ---
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);

        // --- Load Assets ---
        loadMapImage();

        // --- Setup UI (Now safe to use roundManager here) ---
        initializeUIComponents();
        positionUIComponents();
        setupKeyBindings();

        // --- Add Components ---
        addAllComponents(); // Add components AFTER positioning

        // --- Start Game Flow ---
        startNextRoundSequence(); // Start the first round

        // --- Request Focus ---
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        playMusic("assets/FightingUI/fightmusic.wav");

    }

    private void loadCharacterStats() {
        System.out.println("Loading character stats...");
        player1Stats = CharacterDataLoader.getStats(firstPlayerCharacterName);
        // If P2 is AI, load stats for P1's character initially for placeholder? No, load the character AI *will* be.
        // The AI *is* the second character selected, just controlled by computer.
        player2Stats = CharacterDataLoader.getStats(secondPlayerCharacterName);

        if (player1Stats == null || player2Stats == null) {
            // Handle error - perhaps show a message and go back to selection?
            System.err.println("FATAL ERROR: Could not load stats for selected characters!");
            // For now, use defaults to avoid NullPointerException, but this is bad practice
            if (player1Stats == null) player1Stats = CharacterDataLoader.getStats("Default"); // Assuming Default exists
            if (player2Stats == null) player2Stats = CharacterDataLoader.getStats("Default");
            // A real game should prevent starting with invalid characters.
        }

        // Set initial current HP/Stamina based on loaded max values
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


    private void initializeUIComponents() {
        System.out.println("Initializing UI Components...");
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

//        turnIndicatorLabel = new JLabel(); // Text set in updateUIBasedOnTurn
//        turnIndicatorLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        turnIndicatorLabel.setForeground(Color.YELLOW);
//        turnIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

//        roundDisplayLabel = new JLabel("Round: - /" + roundManager.getMaxRounds());
//        roundDisplayLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        roundDisplayLabel.setForeground(Color.CYAN);
//        roundDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);


        player1IdleIcon = (ImageIcon) player1CharacterLabel.getIcon();
        player2IdleIcon = (ImageIcon) player2CharacterLabel.getIcon();
        // --- Action Listeners ---
        player1Skill1.addActionListener(e -> handleAction(1)); // Pass skill number
        player1Skill2.addActionListener(e -> handleAction(2));
        player1Skill3.addActionListener(e -> handleAction(3));

        player2Skill1.addActionListener(e -> handleAction(1));
        player2Skill2.addActionListener(e -> handleAction(2));
        player2Skill3.addActionListener(e -> handleAction(3));

        // --- Set Initial Bar Values (based on current HP/Stamina which are initially max) ---
        updateHpBars();
        updateStaminaBars();

        System.out.println("UI Components Initialized.");
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
            case "Skill1": return 2000; // 1 second
            case "Skill2": return 2000; // 1.2 seconds
            case "Skill3": return 2000; // 2.0 seconds
            case "GetHit": return 1000;  // 0.8 seconds
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

        // --- Center HUD Positions ---
        int timerWidth = 100; int timerHeight = 100;
        timerDisplayLabel.setBounds(panelWidth / 2 - timerWidth / 2, PADDING + 50, timerWidth, timerHeight);

        int indicatorWidth = 300; int indicatorHeight = 40;
        // Move turn indicator down slightly to make room for round display
        //turnIndicatorLabel.setBounds(panelWidth / 2 - indicatorWidth / 2, PADDING + timerHeight + BAR_SPACING + 25, indicatorWidth, indicatorHeight);

        int roundLabelWidth = 250; int roundLabelHeight = 40;
        // Position round display above timer
        //roundDisplayLabel.setBounds(panelWidth / 2 - roundLabelWidth / 2, PADDING - roundLabelHeight + 15 , roundLabelWidth, roundLabelHeight);


        System.out.println("UI Components Positioned.");
    }

    // --- Keyboard Input Setup ---
    private void setupKeyBindings() {
        System.out.println("Setting up key bindings...");
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Bind keys 1, 2, 3 to call handleAction with the skill number
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "skill1Action");
        actionMap.put("skill1Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(1); // Use skill 1

            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "skill2Action");
        actionMap.put("skill2Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(2); // Use skill 2
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "skill3Action");
        actionMap.put("skill3Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(3); // Use skill 3
            }
        });

        // --- Skip Turn (Spacebar) --- <<< NEW BINDING >>>
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "skipTurnAction");
        actionMap.put("skipTurnAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameRunning) return; // Don't skip if game over

                boolean isHumanTurn = false;
                if ("PVP".equals(gameMode)) {
                    // In PVP, either player can skip
                    isHumanTurn = true;
                } else { // PVC mode
                    // In PVC, only Player 1 can skip
                    isHumanTurn = isPlayer1Turn;
                }

                if (isHumanTurn) {
                    System.out.println("Player " + (isPlayer1Turn ? "1" : "2") + " skipped turn.");
                    // Directly switch turn without applying actions/costs
                    stopTurnTimer(); // Stop timer immediately
                    switchTurn();    // Go to next turn/round sequence
                } else {
                    System.out.println("Cannot skip turn (Not a human player's turn).");
                }
            }
        });


        inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = getActionMap();

        // Bind ESCAPE key
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pauseGame");

        actionMap.put("pauseGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionToPause(selectedMapPath);  // Call your existing method
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

    private void startNextRoundSequence() {
        if (!gameRunning) return;

        // 1. Attempt to start the next round (increments round counter)
        if (!roundManager.startNextRound()) {
            // Max rounds reached, end the game
            endGame(true); // Pass true for max rounds reached
            return;
        }

        // 2. Apply Stamina Recovery (if applicable, handled within RoundManager)
        double[] newStaminas = roundManager.applyStaminaRecovery(
                player1Stats, player2Stats,
                player1CurrentStamina, player2CurrentStamina
        );
        player1CurrentStamina = newStaminas[0];
        player2CurrentStamina = newStaminas[1];
        updateStaminaBars(); // Update UI with potentially recovered stamina

        // 3. Determine who starts *this* round randomly
        isPlayer1Turn = roundManager.determineStartingPlayer();

        // 4. Update UI for the new round and starting player
        //roundDisplayLabel.setText("Round: " + roundManager.getCurrentRound() + "/" + roundManager.getMaxRounds());
        updateUIBasedOnTurn(); // Set enabled buttons, highlights, turn indicator

        // 5. Start the first turn of the round
        startTurnTimer(); // Start the timer for the randomly chosen starting player

        // 6. If AI starts, trigger its action
        if (!isPlayer1Turn && isVsAI) {
            System.out.println("[Log] AI starts round " + roundManager.getCurrentRound() + ". Triggering AI action..."); // ADD LOG
            SwingUtilities.invokeLater(this::performAiAction);
        }
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
    private void handleAction(int skillNumber) {
        if (!gameRunning) return;

        CharacterStats currentActorStats;
        double currentActorStamina;
        double opponentCurrentHp;
        String actorName;

        if (isPlayer1Turn) {
            actorName = firstPlayerCharacterName;
            currentActorStats = player1Stats;
            currentActorStamina = player1CurrentStamina;
            opponentCurrentHp = player2CurrentHp;
        } else {
            if (isVsAI) {
                System.out.println("Cannot manually trigger AI action.");
                return;
            }
            actorName = secondPlayerCharacterName;
            currentActorStats = player2Stats;
            currentActorStamina = player2CurrentStamina;
            opponentCurrentHp = player1CurrentHp;
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
            } else {
                playAnimation(player2CharacterLabel, secondPlayerCharacterName, skillAnimType, false, player2IdleIcon);
            }

            javax.swing.Timer effectTimer = new javax.swing.Timer(skillAnimDuration, evt -> {
                if (isPlayer1Turn) {
                    player1CurrentStamina -= staminaCost;
                    player2CurrentHp -= damageDealt;
                    System.out.printf("P1 used Skill %d...%n", skillNumber);

                    if (damageDealt > 0) {
                        playAnimation(player2CharacterLabel, secondPlayerCharacterName, "GetHit", false, player2IdleIcon);
                    }
                } else {
                    player2CurrentStamina -= staminaCost;
                    player1CurrentHp -= damageDealt;
                    System.out.printf("P2 used Skill %d...%n", skillNumber);

                    if (damageDealt > 0) {
                        playAnimation(player1CharacterLabel, firstPlayerCharacterName, "GetHit", true, player1IdleIcon);
                    }
                }

                updateHpBars();
                updateStaminaBars();

                boolean p1Died = player1CurrentHp <= 0;
                boolean p2Died = player2CurrentHp <= 0;
                String winner = "";

                if (p1Died) {
                    playAnimation(player1CharacterLabel, firstPlayerCharacterName, "Death", true, player1IdleIcon);
                    p1Lose++;
                    p2Wins++;
                    winner = "Player 2";
                    new GameLog().recordGame(winner);
                    new GameLog().saveStatsToFile();

                    // Stop game and timers
                    gameRunning = false;
                    stopTurnTimer();
                    if (player1AnimTimer != null) player1AnimTimer.stop();
                    if (player2AnimTimer != null) player2AnimTimer.stop();

                    // Start death sequence timer
                    if (deathSequenceTimer != null && deathSequenceTimer.isRunning()) {
                        deathSequenceTimer.stop();
                    }
                    deathSequenceTimer = new javax.swing.Timer(getAnimationDuration("Death"), e -> {
                        transitionToResultScreen();
                    });
                    deathSequenceTimer.setRepeats(false);
                    deathSequenceTimer.start();
                    return; // Exit early since game is over
                }
                if (p2Died) {
                    playAnimation(player2CharacterLabel, secondPlayerCharacterName, "Death", false, player2IdleIcon);
                    p2Lose++;
                    p1Wins++;
                    winner = "Player 1";
                    new GameLog().recordGame(winner);
                    new GameLog().saveStatsToFile();

                    // Stop game and timers
                    gameRunning = false;
                    stopTurnTimer();
                    if (player1AnimTimer != null) player1AnimTimer.stop();
                    if (player2AnimTimer != null) player2AnimTimer.stop();

                    // Start death sequence timer
                    if (deathSequenceTimer != null && deathSequenceTimer.isRunning()) {
                        deathSequenceTimer.stop();
                    }
                    deathSequenceTimer = new javax.swing.Timer(getAnimationDuration("Death"), e -> {
                        transitionToResultScreen();
                    });
                    deathSequenceTimer.setRepeats(false);
                    deathSequenceTimer.start();
                    return; // Exit early since game is over
                }

                switchTurn();
            });

            effectTimer.setRepeats(false);
            effectTimer.start();

        } else {
            System.out.println(actorName + ": Not enough stamina for Skill " + skillNumber);
        }
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

        playAnimation(player2CharacterLabel, secondPlayerCharacterName, "Skill" + skillNumber, false, player2IdleIcon); // AI Skill
        playAnimation(player1CharacterLabel, firstPlayerCharacterName, "GetHit", true, player1IdleIcon);           // P1 Hit

        // --- Apply Effects ---
        double staminaCost = player2Stats.getSkillCost(skillNumber);
        double damageDealt = player2Stats.getSkillDamage(skillNumber);

        player2CurrentStamina -= staminaCost;
        player1CurrentHp -= damageDealt;
        System.out.printf("AI used Skill %d...%n", skillNumber);

        // Update UI Bars
        updateHpBars();
        updateStaminaBars();
        boolean p1Died = player1CurrentHp <= 0;

        if (p1Died) {
            System.out.println("Death detected (P1). Starting game over sequence...");
            gameRunning = false; // Stop game logic
            // AI's turn timer was already stopped before execution, but ensure main one is stopped
            stopTurnTimer();

            playAnimation(player1CharacterLabel, firstPlayerCharacterName, "Death", true, player1IdleIcon);

            int deathAnimDuration = getAnimationDuration("Death");

            // Stop any ongoing revert timers immediately
            if (player1AnimTimer != null) player1AnimTimer.stop();
            if (player2AnimTimer != null) player2AnimTimer.stop();

            if (deathSequenceTimer != null && deathSequenceTimer.isRunning()) {
                deathSequenceTimer.stop();
            }
            deathSequenceTimer = new javax.swing.Timer(deathAnimDuration, e -> { // Use 'e' or 'evt'
                System.out.println("Death sequence timer finished. Transitioning...");
                // --- CALL THE RENAMED METHOD ---
                transitionToResultScreen();
            });
            deathSequenceTimer.setRepeats(false);
            deathSequenceTimer.start();
            return;

        }
        // Switch back to Player 1's turn AFTER AI action completes
        // Use invokeLater to ensure UI updates and checks happen correctly on EDT
        SwingUtilities.invokeLater(this::switchTurn);
    }
    /**
     * Cleans up game state and switches the view to the GameOverScreen.
     */
    private void transitionToResultScreen() {
        System.out.println("Executing transitionToResultScreen...");
        // Ensure all timers are stopped
        stopTurnTimer();
        if (player1AnimTimer != null) player1AnimTimer.stop();
        if (player2AnimTimer != null) player2AnimTimer.stop();
        if (deathSequenceTimer != null) deathSequenceTimer.stop();

        // Determine the result (which player won?)
        // Note: If max rounds reached, might need different logic here passed from endGame()
        boolean p1Wins = player2CurrentHp <= 0 && player1CurrentHp > 0;
        boolean p2Wins = player1CurrentHp <= 0 && player2CurrentHp > 0;
        // Default to player 1 winning if it's a draw or P2 HP is 0 (adjust logic as needed)
        boolean player1Won = p1Wins || (!p2Wins && player2CurrentHp == 0);

        System.out.println("Player 1 Won: " + player1Won);

        // Create and set the new ResultScreen panel
        // Pass details needed for rematch LATER if you implement it
        ResultScreen resultScreen = new ResultScreen(frame, player1Won, firstPlayerCharacterName, secondPlayerCharacterName, selectedMapPath, gameMode );
        frame.setContentPane(resultScreen);
        frame.revalidate();
        frame.repaint();
        stopMusic();
        System.out.println("Switched to ResultScreen.");
    }

    // Make sure endGame is removed or no longer called on death by HP
    // public void endGame(boolean maxRoundsReached) { ... } // Keep if needed for MAX_ROUNDS end condition

    // --- UI Update Helpers ---

    private void updateHpBars() {
        // Ensure HP doesn't go below 0 for display purposes
        double p1HpDisplay = Math.max(0, player1CurrentHp);
        double p2HpDisplay = Math.max(0, player2CurrentHp);

        player1HpBar.setValue(p1HpDisplay / player1Stats.getMaxHp());
        player2HpBar.setValue(p2HpDisplay / player2Stats.getMaxHp());
    }

    private void updateStaminaBars() {
        player1StaminaBar.setValue(player1CurrentStamina / player1Stats.getMaxStamina());
        player2StaminaBar.setValue(player2CurrentStamina / player2Stats.getMaxStamina());
    }


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
        // Ensure all components are added. Add order doesn't usually matter for null layout.
        System.out.println("Adding components to panel...");
        add(player1HpBar);
        add(player1StaminaBar);
        add(player1CharacterLabel);
        add(player1Skill1);
        add(player1Skill2);
        add(player1Skill3);

        add(player2HpBar);
        add(player2StaminaBar);
        add(player2CharacterLabel);
        add(player2Skill1);
        add(player2Skill2);
        add(player2Skill3);

        add(timerDisplayLabel);
        //add(turnIndicatorLabel);
        //add(roundDisplayLabel); // Add the new round label
        System.out.println("Components added.");
        System.out.println("Component count: " + getComponentCount());
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


    private void transitionToPause(String mapResourcePath) {
        if (mapResourcePath == null) return;

        SwingUtilities.invokeLater(() -> {
            stopMusic();
            Pause pauseScreen = new Pause(frame, this); // Pass reference to GameScreen so it can resume
            frame.setContentPane(pauseScreen);
            frame.revalidate();
            frame.repaint();
        });
    }


}