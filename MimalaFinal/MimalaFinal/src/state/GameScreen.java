package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.util.Random; // For AI

public class GameScreen extends JPanel {
    private final JFrame frame;
    private final String firstPlayerCharacter;
    private final String secondPlayerCharacter;
    private final String selectedMapPath;
    private ImageIcon mapImage;

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

    // --- Layout Constants --- (Keep previous adjustments or modify as needed)
    private static final int PADDING = 30;
    private static final int BAR_WIDTH = 600;
    private static final int HP_BAR_HEIGHT = 65;
    private static final int STAMINA_BAR_HEIGHT = 40;
    private static final int BAR_SPACING = 5;
    private static final int CHARACTER_Y_OFFSET = 30;
    private static final int CHARACTER_WIDTH = 768;
    private static final int CHARACTER_HEIGHT = 768;
    private static final int SKILL_BUTTON_WIDTH = 180;
    private static final int SKILL_BUTTON_HEIGHT = 60;
    private static final int SKILL_SPACING = 10;
    private static final int SKILL_AREA_BOTTOM_MARGIN = 30;

    // --- Asset Path Constants ---
    private static final String HP_BAR_BG_BASE_PATH = "/assets/FightingUI/HealthBar_EachCharacters/Azurox.png";
    private static final String HP_BAR_FG_PATH = "/assets/FightingUI/HealthBar_EachCharacters/HealthBar_AllCharacters1.png";
    private static final String STAMINA_BAR_BG_PATH = "/assets/FightingUI/StaminaBar1.png";
    private static final String STAMINA_BAR_FG_PATH = "/assets/FightingUI/StaminaBar2.png";
    private static final String CHAR_GIF_BASE_PATH = "/assets/FightingUI/Mimala_Characters/";

    // --- Game State ---
    private boolean isPlayer1Turn = true;
    private Timer turnTimer;
    private int remainingTime;
    private static final int TURN_DURATION_SECONDS = 15;
    private final Random random = new Random(); // For AI
    private static final String AI_PLAYER_NAME = "Computer"; // Identifier for AI player

    public GameScreen(JFrame frame, String firstPlayerCharacter, String secondPlayerCharacter, String selectedMapResourcePath) {
        this.frame = frame;
        this.firstPlayerCharacter = firstPlayerCharacter;
        this.secondPlayerCharacter = secondPlayerCharacter;
        this.selectedMapPath = selectedMapResourcePath;

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        // Make the panel focusable to receive key events for bindings
        setFocusable(true);

        loadMapImage();
        initializeUIComponents();
        positionUIComponents();
        setupGameTimer();
        setupKeyBindings(); // Setup keyboard inputs

        updateUIBasedOnTurn();
        addAllComponents(); // Add components AFTER positioning
        startTurn(); // Start the first turn timer

        // Request focus right after the panel is set up and visible
        // Best done after the frame is packed/visible, but can try here too.
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    // --- Path Generation Helpers (Corrected) ---

    private String getHpBackgroundPath(String characterName, boolean isPlayer1) {
        return HP_BAR_BG_BASE_PATH;
    }

    private String getCharacterGifPath(String characterName, boolean isPlayer1) {
        // Reverted to flipped logic for P2 - Make sure "[Char]Idle_flipped.gif" files exist!
        String gifFileName = characterName  + "Idle" + (isPlayer1 ? "": "flipped") + ".gif";
        String path = CHAR_GIF_BASE_PATH + characterName + "/" + gifFileName;
        System.out.println("Attempting to load GIF from: " + path);
        return path;
    }

    // --- Loading and Initialization (Mostly unchanged, ensure paths are correct) ---

    private void loadMapImage() {
        try {
            URL imageURL = getClass().getResource("/" + selectedMapPath);
            if (imageURL != null) {
                mapImage = new ImageIcon(imageURL);
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
        // Check if AI is selected but has no specific assets, maybe use a default?
        if (characterName.equals(AI_PLAYER_NAME)) {
            // Option 1: Use a default character's assets
            // characterName = "DefaultCharacterName"; // e.g., "Zenfang"
            // Option 2: Return a specific "AI" placeholder
            System.out.println("Loading placeholder for AI player.");
            return createPlaceholderIcon(CHARACTER_WIDTH, CHARACTER_HEIGHT, "AI Player");
        }

        String path = getCharacterGifPath(characterName, isPlayer1);
        URL gifUrl = getClass().getResource(path);
        if (gifUrl != null) {
            System.out.println("GIF loaded successfully: " + path);
            return new ImageIcon(gifUrl);
        } else {
            System.err.println("ERROR: Character GIF not found at path: " + path);
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
        String p1HpBgPath = getHpBackgroundPath(firstPlayerCharacter, true);
        System.out.println("P1 HP BG Path: " + p1HpBgPath);
        player1HpBar = new StatusBar(p1HpBgPath, HP_BAR_FG_PATH);
        player1StaminaBar = new StatusBar(STAMINA_BAR_BG_PATH, STAMINA_BAR_FG_PATH);
        ImageIcon p1Gif = loadCharacterGif(firstPlayerCharacter, true);
        player1CharacterLabel = new JLabel(p1Gif);

        player1Skill1 = new JButton("Skill 1 (1)"); // Add key hint
        player1Skill2 = new JButton("Skill 2 (2)");
        player1Skill3 = new JButton("Skill 3 (3)");

        // --- Player 2 ---
        // Handle case where P2 is AI for HP bar background
        String p2ActualCharacter = secondPlayerCharacter.equals(AI_PLAYER_NAME) ? firstPlayerCharacter : secondPlayerCharacter; // Example: AI uses P1's bar mirror? Or needs own default?
        String p2HpBgPath = getHpBackgroundPath(p2ActualCharacter, false); // Use AI identifier or default if needed
        System.out.println("P2 HP BG Path: " + p2HpBgPath);
        player2HpBar = new StatusBar(p2HpBgPath, HP_BAR_FG_PATH);
        player2StaminaBar = new StatusBar(STAMINA_BAR_BG_PATH, STAMINA_BAR_FG_PATH);
        ImageIcon p2Gif = loadCharacterGif(secondPlayerCharacter, false); // Load AI placeholder or char GIF
        player2CharacterLabel = new JLabel(p2Gif);

        player2Skill1 = new JButton("Skill 1 (1)"); // Also uses 1,2,3 but only active on P2 turn
        player2Skill2 = new JButton("Skill 2 (2)");
        player2Skill3 = new JButton("Skill 3 (3)");


        // --- Center HUD ---
        timerDisplayLabel = new JLabel(String.valueOf(TURN_DURATION_SECONDS));
        timerDisplayLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timerDisplayLabel.setForeground(Color.WHITE);
        timerDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);

        turnIndicatorLabel = new JLabel();
        turnIndicatorLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnIndicatorLabel.setForeground(Color.YELLOW);
        turnIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // --- Action Listeners ---
        player1Skill1.addActionListener(e -> handleAction("P1_SKILL_1"));
        player1Skill2.addActionListener(e -> handleAction("P1_SKILL_2"));
        player1Skill3.addActionListener(e -> handleAction("P1_SKILL_3"));

        // Add listeners for P2 only if not AI? Or let handleAction check turn.
        player2Skill1.addActionListener(e -> handleAction("P2_SKILL_1"));
        player2Skill2.addActionListener(e -> handleAction("P2_SKILL_2"));
        player2Skill3.addActionListener(e -> handleAction("P2_SKILL_3"));

        // Set initial values (make sure bars are initialized first)
        player1HpBar.setValue(1.0);
        player1StaminaBar.setValue(1.0);
        player2HpBar.setValue(1.0);
        player2StaminaBar.setValue(1.0);

        System.out.println("UI Components Initialized.");
    }

    private void positionUIComponents() {
        System.out.println("Positioning UI Components...");
        int panelWidth = getPreferredSize().width > 0 ? getPreferredSize().width : 1920; // Use default if panel size not known yet
        int panelHeight = getPreferredSize().height > 0 ? getPreferredSize().height : 1080;

        // --- Use Constants for Bar Sizes ---
        // NOTE: StatusBar's preferredSize might interfere with null layout.
        // setBounds should override, but ensure StatusBar doesn't resize itself.
        // You might need to remove setPreferredSize in StatusBar if issues persist.
        int p1HpW = BAR_WIDTH; int p1HpH = HP_BAR_HEIGHT;
        int p1StW = BAR_WIDTH; int p1StH = STAMINA_BAR_HEIGHT; // Assuming stamina bar should also be wide
        int p2HpW = BAR_WIDTH; int p2HpH = HP_BAR_HEIGHT;
        int p2StW = BAR_WIDTH; int p2StH = STAMINA_BAR_HEIGHT;

        // --- Player 1 Positions ---
        int p1_X = PADDING + 50; // Bar starts near left edge
        int p1_Y_HP = PADDING;
        player1HpBar.setBounds(p1_X, p1_Y_HP, p1HpW, p1HpH);

        int p1_Y_Stamina = p1_Y_HP + p1HpH + BAR_SPACING;
        player1StaminaBar.setBounds(p1_X, p1_Y_Stamina, p1StW, p1StH);

        // --- Player 1 Character Position (Moved Right and Lower) ---
        // Position from bottom edge, slightly offset from left
        int p1_Char_X = PADDING + 150; // Increased X offset from left edge
        int p1_Char_Y = panelHeight - CHARACTER_HEIGHT - SKILL_AREA_BOTTOM_MARGIN - 80; // Positioned near the bottom, above potential skill area space
        player1CharacterLabel.setBounds(p1_Char_X, p1_Char_Y, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        // --- Player 1 Skills Position (Near Bottom Left) ---
        int p1_Skill_Y_Start = panelHeight - SKILL_AREA_BOTTOM_MARGIN - SKILL_BUTTON_HEIGHT;
        // Align skills closer to the corner if desired
        int p1_Skill_X = PADDING;
        player1Skill3.setBounds(p1_Skill_X, p1_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player1Skill2.setBounds(p1_Skill_X, p1_Skill_Y_Start - SKILL_BUTTON_HEIGHT - SKILL_SPACING, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player1Skill1.setBounds(p1_Skill_X, p1_Skill_Y_Start - 2 * (SKILL_BUTTON_HEIGHT + SKILL_SPACING), SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);


        // --- Player 2 Positions ---
        int p2_X_HP = panelWidth - PADDING - p2HpW - 50; // Bar ends near right edge
        int p2_Y_HP = PADDING;
        player2HpBar.setBounds(p2_X_HP, p2_Y_HP, p2HpW, p2HpH);

        int p2_Y_Stamina = p2_Y_HP + p2HpH + BAR_SPACING;
        int p2_X_Stamina = panelWidth - PADDING - p2StW - 50; // Align stamina bar end with HP bar end
        player2StaminaBar.setBounds(p2_X_Stamina, p2_Y_Stamina, p2StW, p2StH);

        // --- Player 2 Character Position (Moved Left and Lower) ---
        // Position from bottom edge, slightly offset from right
        int p2_Char_X = panelWidth - PADDING  - 150 - CHARACTER_WIDTH; // Increased X offset from right edge (moved left)
        int p2_Char_Y = panelHeight - CHARACTER_HEIGHT - SKILL_AREA_BOTTOM_MARGIN - 80; // Same Y as P1
        player2CharacterLabel.setBounds(p2_Char_X, p2_Char_Y, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        // --- Player 2 Skills Position (Near Bottom Right) ---
        int p2_Skill_X = panelWidth - PADDING - SKILL_BUTTON_WIDTH;
        int p2_Skill_Y_Start = panelHeight - SKILL_AREA_BOTTOM_MARGIN - SKILL_BUTTON_HEIGHT;
        player2Skill3.setBounds(p2_Skill_X, p2_Skill_Y_Start, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player2Skill2.setBounds(p2_Skill_X, p2_Skill_Y_Start - SKILL_BUTTON_HEIGHT - SKILL_SPACING, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
        player2Skill1.setBounds(p2_Skill_X, p2_Skill_Y_Start - 2 * (SKILL_BUTTON_HEIGHT + SKILL_SPACING), SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);


        // --- Center HUD Positions (Remain the same) ---
        int timerWidth = 100; int timerHeight = 60;
        timerDisplayLabel.setBounds(panelWidth / 2 - timerWidth / 2, PADDING, timerWidth, timerHeight);
        int indicatorWidth = 300; int indicatorHeight = 40;
        turnIndicatorLabel.setBounds(panelWidth / 2 - indicatorWidth / 2, PADDING + timerHeight + 5, indicatorWidth, indicatorHeight);

        System.out.println("UI Components Positioned.");
    }

    // --- Keyboard Input Setup ---
    private void setupKeyBindings() {
        System.out.println("Setting up key bindings...");
        // Use WHEN_IN_FOCUSED_WINDOW to capture keys even if buttons have focus
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // --- Skill 1 (Key '1') ---
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "skill1Action");
        actionMap.put("skill1Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlayer1Turn) {
                    handleAction("P1_SKILL_1");
                } else if (!secondPlayerCharacter.equals(AI_PLAYER_NAME)) { // P2 is human
                    handleAction("P2_SKILL_1");
                }
            }
        });

        // --- Skill 2 (Key '2') ---
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "skill2Action");
        actionMap.put("skill2Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlayer1Turn) {
                    handleAction("P1_SKILL_2");
                } else if (!secondPlayerCharacter.equals(AI_PLAYER_NAME)) {
                    handleAction("P2_SKILL_2");
                }
            }
        });

        // --- Skill 3 (Key '3') ---
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "skill3Action");
        actionMap.put("skill3Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlayer1Turn) {
                    handleAction("P1_SKILL_3");
                } else if (!secondPlayerCharacter.equals(AI_PLAYER_NAME)) {
                    handleAction("P2_SKILL_3");
                }
            }
        });
        System.out.println("Key bindings set up.");
    }


    // --- Game Logic Methods ---

    private void setupGameTimer() {
        turnTimer = new Timer(1000, e -> { // Use Lambda if preferred
            remainingTime--;
            timerDisplayLabel.setText(String.valueOf(remainingTime));
            if (remainingTime <= 0) {
                System.out.println("Time's up!");
                switchTurn(); // Switch turn automatically
            }
        });
        turnTimer.setRepeats(true);
    }

    private void startTurn() {
        remainingTime = TURN_DURATION_SECONDS;
        timerDisplayLabel.setText(String.valueOf(remainingTime));
        turnTimer.start();
        updateUIBasedOnTurn();
        System.out.println((isPlayer1Turn ? firstPlayerCharacter : secondPlayerCharacter) + "'s turn started.");
    }

    private void switchTurn() {
        turnTimer.stop();
        isPlayer1Turn = !isPlayer1Turn;

        // Check if it's AI's turn
        if (!isPlayer1Turn && secondPlayerCharacter.equals(AI_PLAYER_NAME)) {
            updateUIBasedOnTurn(); // Update UI to show AI's turn briefly
            performAiAction();   // AI takes its turn
        } else {
            startTurn(); // Start timer for the next human player
        }
    }

    // Central method to handle player actions
    private void handleAction(String actionCode) {
        boolean isP1Action = actionCode.startsWith("P1");

        // Check turn validity (redundant check for keyboard actions, but good for button clicks)
        if ((isP1Action && !isPlayer1Turn) || (!isP1Action && isPlayer1Turn)) {
            System.out.println("Not your turn! (Action: " + actionCode + ", isP1Turn: " + isPlayer1Turn + ")");
            return;
        }
        // Check if AI action is attempted by human input
        if (!isP1Action && secondPlayerCharacter.equals(AI_PLAYER_NAME)) {
            System.out.println("Cannot manually trigger AI action.");
            return; // Prevent human input during AI's turn or for AI player
        }


        turnTimer.stop(); // Stop timer as action is chosen
        System.out.println("Action handled: " + actionCode + " by " + (isP1Action ? "Player 1" : "Player 2"));

        // --- Apply skill effect (Placeholder) ---
        // IMPORTANT: Replace with your actual skill logic based on 'actionCode'
        // Use data structures (Maps, Classes) to store skill costs/effects per character later.
        double staminaCost = 0.1; // Default placeholder cost
        double damageDealt = 0.0; // Default placeholder damage

        if (isP1Action) { // Player 1's action
            if (actionCode.equals("P1_SKILL_1")) { staminaCost = 0.15; damageDealt = 0.1; }
            else if (actionCode.equals("P1_SKILL_2")) { staminaCost = 0.20; damageDealt = 0.12; }
            else if (actionCode.equals("P1_SKILL_3")) { staminaCost = 0.25; damageDealt = 0.15; }

            if (player1StaminaBar.getValue() >= staminaCost) {
                player1StaminaBar.setValue(player1StaminaBar.getValue() - staminaCost);
                player2HpBar.setValue(player2HpBar.getValue() - damageDealt);
                System.out.printf("P1 used %s. P1 Stamina: %.2f, P2 HP: %.2f%n", actionCode, player1StaminaBar.getValue(), player2HpBar.getValue());
            } else {
                System.out.println("P1: Not enough stamina for " + actionCode);
                startTurn(); // Restart turn timer if action failed
                return; // Don't switch turn
            }
        } else { // Player 2's action (Must be human player at this point)
            if (actionCode.equals("P2_SKILL_1")) { staminaCost = 0.15; damageDealt = 0.1; }
            else if (actionCode.equals("P2_SKILL_2")) { staminaCost = 0.20; damageDealt = 0.12; }
            else if (actionCode.equals("P2_SKILL_3")) { staminaCost = 0.25; damageDealt = 0.15; }

            if (player2StaminaBar.getValue() >= staminaCost) {
                player2StaminaBar.setValue(player2StaminaBar.getValue() - staminaCost);
                player1HpBar.setValue(player1HpBar.getValue() - damageDealt);
                System.out.printf("P2 used %s. P2 Stamina: %.2f, P1 HP: %.2f%n", actionCode, player2StaminaBar.getValue(), player1HpBar.getValue());
            } else {
                System.out.println("P2: Not enough stamina for " + actionCode);
                startTurn(); // Restart turn timer if action failed
                return; // Don't switch turn
            }
        }


        // Check for win/loss conditions AFTER applying effects
        if (player1HpBar.getValue() <= 0 || player2HpBar.getValue() <= 0) {
            endGame();
            return; // Don't switch turn if game ended
        }

        // --- Switch to the next player's turn (if action was successful) ---
        switchTurn();
    }

    private void performAiAction() {
        System.out.println("AI ("+ secondPlayerCharacter +") is thinking...");
        turnTimer.stop(); // Ensure timer is stopped during AI "choice"

        // Simple AI: Choose a random skill (0, 1, or 2 corresponds to Skill 1, 2, 3)
        int skillChoice = random.nextInt(3); // Generates 0, 1, or 2
        String actionCode = "P2_SKILL_" + (skillChoice + 1);

        // Check if AI has enough stamina (Basic check, can be improved)
        double staminaCost = 0.1; // Default cost, adjust based on skillChoice later
        if (actionCode.equals("P2_SKILL_1")) { staminaCost = 0.15; }
        else if (actionCode.equals("P2_SKILL_2")) { staminaCost = 0.20; }
        else if (actionCode.equals("P2_SKILL_3")) { staminaCost = 0.25; }

        final String finalActionCode = actionCode;
        final double finalStaminaCost = staminaCost;

        // Add a small delay before the AI performs the action
        Timer aiDelayTimer = new Timer(1200, e -> { // 1.2 second delay
            if (player2StaminaBar.getValue() >= finalStaminaCost) {
                System.out.println("AI performs action: " + finalActionCode);
                handleAiSkillExecution(finalActionCode, finalStaminaCost); // Separate method for actual effect
            } else {
                System.out.println("AI has insufficient stamina for " + finalActionCode + ". Skipping turn.");
                switchTurn(); // AI skips turn if it can't afford any move (simple logic)
            }
        });
        aiDelayTimer.setRepeats(false);
        aiDelayTimer.start();
    }

    // Separated AI skill execution to be called after delay
    private void handleAiSkillExecution(String actionCode, double staminaCost) {
        double damageDealt = 0.0;
        if (actionCode.equals("P2_SKILL_1")) { damageDealt = 0.1; }
        else if (actionCode.equals("P2_SKILL_2")) { damageDealt = 0.12; }
        else if (actionCode.equals("P2_SKILL_3")) { damageDealt = 0.15; }

        player2StaminaBar.setValue(player2StaminaBar.getValue() - staminaCost);
        player1HpBar.setValue(player1HpBar.getValue() - damageDealt);
        System.out.printf("AI used %s. P2 Stamina: %.2f, P1 HP: %.2f%n", actionCode, player2StaminaBar.getValue(), player1HpBar.getValue());

        // Check for win/loss conditions AFTER AI effects
        if (player1HpBar.getValue() <= 0 || player2HpBar.getValue() <= 0) {
            endGame();
            return;
        }
        // Switch back to Player 1's turn AFTER AI action completes
        switchTurn();
    }


    private void updateUIBasedOnTurn() {
        boolean isHumanP2 = !secondPlayerCharacter.equals(AI_PLAYER_NAME);

        // Enable/Disable P1 buttons
        player1Skill1.setEnabled(isPlayer1Turn);
        player1Skill2.setEnabled(isPlayer1Turn);
        player1Skill3.setEnabled(isPlayer1Turn);

        // Enable/Disable P2 buttons only if P2 is human AND it's their turn
        player2Skill1.setEnabled(!isPlayer1Turn && isHumanP2);
        player2Skill2.setEnabled(!isPlayer1Turn && isHumanP2);
        player2Skill3.setEnabled(!isPlayer1Turn && isHumanP2);

        // Update turn indicator text
        turnIndicatorLabel.setText(isPlayer1Turn ? firstPlayerCharacter + "'s Turn" : secondPlayerCharacter + "'s Turn");

        // Update highlight border
        player1CharacterLabel.setBorder(isPlayer1Turn ? BorderFactory.createLineBorder(Color.YELLOW, 3) : null);
        player2CharacterLabel.setBorder(!isPlayer1Turn ? BorderFactory.createLineBorder(Color.YELLOW, 3) : null);

        // Ensure panel has focus for key bindings when a turn starts
        if ((isPlayer1Turn || isHumanP2)) { // If it's a human player's turn
            SwingUtilities.invokeLater(this::requestFocusInWindow); // Request focus politely
        }
    }


    private void endGame() {
        System.out.println("Ending Game...");
        turnTimer.stop();
        // Disable all buttons regardless of AI status
        player1Skill1.setEnabled(false); player1Skill2.setEnabled(false); player1Skill3.setEnabled(false);
        player2Skill1.setEnabled(false); player2Skill2.setEnabled(false); player2Skill3.setEnabled(false);

        // Display winner message
        String winner;
        // Check HP after potential final blow
        boolean p1Alive = player1HpBar.getValue() > 0;
        boolean p2Alive = player2HpBar.getValue() > 0;

        if (!p1Alive && !p2Alive) { winner = "It's a Draw!"; }
        else if (!p2Alive) { winner = firstPlayerCharacter + " Wins!"; }
        else if (!p1Alive) { winner = secondPlayerCharacter + " Wins!"; }
        else { winner = "Error determining winner"; } // Should not happen if called correctly

        turnIndicatorLabel.setText("GAME OVER: " + winner);
        turnIndicatorLabel.setForeground(Color.RED);
        System.out.println("Game Over! Winner: " + winner);
    }


    private void addAllComponents() {
        // Ensure all components are added. Add order doesn't usually matter for null layout.
        System.out.println("Adding components to panel...");
        add(player1HpBar);
        add(player1StaminaBar); // Make sure this is added
        add(player1CharacterLabel); // Make sure this is added
        add(player1Skill1);
        add(player1Skill2);
        add(player1Skill3);

        add(player2HpBar);
        add(player2StaminaBar); // Make sure this is added
        add(player2CharacterLabel); // Make sure this is added
        add(player2Skill1);
        add(player2Skill2);
        add(player2Skill3);

        add(timerDisplayLabel);
        add(turnIndicatorLabel);
        System.out.println("Components added.");
        // Print component count for verification
        System.out.println("Component count: " + getComponentCount());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears the panel

            g.drawImage(mapImage.getImage(), 0, 0, getWidth(), getHeight(), this);

        // Swing paints children AFTER this method completes
    }
}