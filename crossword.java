import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CrosswordPuzzle extends JFrame {
    private static final int GRID_SIZE = 10;
    private JTextField[][] grid;
    private int lives = 3;
    private int score = 0;
    private JLabel livesLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JTextArea cluesArea;

    private javax.swing.Timer timer;
    private int secondsPassed = 0;

    private int[][] structure = {
        {0,0,0,0,1,0,0,0,0,0},
        {1,1,0,1,1,1,0,1,1,1},
        {0,0,0,0,1,0,0,0,0,0},
        {1,1,0,1,1,1,0,1,1,0},
        {0,0,0,0,0,0,0,0,0,0},
        {1,0,1,1,0,1,1,1,0,1},
        {0,0,0,0,0,1,0,0,0,0},
        {1,0,1,1,0,1,1,1,0,1},
        {0,0,0,1,0,0,0,0,0,0},
        {1,1,0,1,1,1,0,1,1,0}
    };

    private String[][] clueNumbers = new String[GRID_SIZE][GRID_SIZE];
    private Map<String, Word> words = new HashMap<>();

    class Word {
        int row, col;
        String text;
        boolean isAcross;

        Word(int row, int col, String text, boolean isAcross) {
            this.row = row;
            this.col = col;
            this.text = text.toUpperCase();
            this.isAcross = isAcross;
        }
    }

    public CrosswordPuzzle() {

        setTitle("Crossword Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // FULLSCREEN MODE
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initializeWords();
        setClueNumbers();

        grid = new JTextField[GRID_SIZE][GRID_SIZE];
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBackground(Color.BLACK);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {

                JTextField cell = new JTextField();
                grid[i][j] = cell;

                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                cell.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        cell.setBackground(new Color(210, 240, 255));
                    }
                    public void focusLost(FocusEvent e) {
                        cell.setBackground(Color.WHITE);
                    }
                });

                JLayeredPane layeredCell = new JLayeredPane();
                layeredCell.setPreferredSize(new Dimension(40, 40));
                layeredCell.setLayout(null);

                if (structure[i][j] == 1) {
                    cell.setEnabled(false);
                    cell.setBackground(Color.DARK_GRAY);
                } else {
                    cell.setBackground(Color.WHITE);

                    if (clueNumbers[i][j] != null) {
                        JLabel numLabel = new JLabel(clueNumbers[i][j]);
                        numLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                        numLabel.setForeground(Color.BLUE);
                        numLabel.setBounds(2, 0, 20, 12);
                        layeredCell.add(numLabel, JLayeredPane.PALETTE_LAYER);
                    }

                    final int row = i;
                    final int col = j;

                    cell.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char c = e.getKeyChar();
                            if (!Character.isLetter(c)) {
                                e.consume();
                            } else {
                                SwingUtilities.invokeLater(() -> {
                                    grid[row][col].setText(String.valueOf(c).toUpperCase());
                                    moveToNextCell(row, col);
                                });
                            }
                        }

                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                                SwingUtilities.invokeLater(() -> {
                                    if (grid[row][col].getText().isEmpty()) {
                                        moveToPreviousCell(row, col);
                                    }
                                });
                            }
                        }
                    });
                }

                cell.setBounds(0, 0, 40, 40);
                layeredCell.add(cell, JLayeredPane.DEFAULT_LAYER);
                gridPanel.add(layeredCell);
            }
        }

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(350, 600));
        rightPanel.setBackground(new Color(255, 255, 230));

        cluesArea = new JTextArea();
        cluesArea.setEditable(false);
        cluesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        cluesArea.setText(getCluesText());
        cluesArea.setLineWrap(true);
        cluesArea.setWrapStyleWord(true);
        cluesArea.setBackground(new Color(255, 255, 230));
        cluesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rightPanel.add(new JLabel("CLUES", JLabel.CENTER), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(cluesArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());

        livesLabel = new JLabel("Lives: ***");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setForeground(new Color(210, 160, 0));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(Color.BLUE);

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.MAGENTA);

        JButton checkButton = new JButton("Check Answer");
        checkButton.addActionListener(e -> checkAnswers());

        JButton hintButton = new JButton("Hint");
        hintButton.addActionListener(e -> giveHint());

        JButton revealButton = new JButton("Reveal Word");
        revealButton.addActionListener(e -> revealWord());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        startTimer();

        bottomPanel.add(livesLabel);
        bottomPanel.add(scoreLabel);
        bottomPanel.add(timerLabel);
        bottomPanel.add(checkButton);
        bottomPanel.add(hintButton);
        bottomPanel.add(revealButton);
        bottomPanel.add(resetButton);

        add(gridPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startTimer() {
        timer = new javax.swing.Timer(1000, e -> {

            secondsPassed++;
            timerLabel.setText("Time: " + secondsPassed + "s");
        });
        timer.start();
    }

    private void initializeWords() {
        words.put("1A", new Word(0, 0, "BOOK", true));
        words.put("2A", new Word(2, 0, "MOON", true));
        words.put("3A", new Word(3, 2, "TREE", true));
        words.put("4A", new Word(4, 0, "WATERMELON", true));
        words.put("5A", new Word(6, 0, "CHAIR", true));
        words.put("6A", new Word(8, 0, "SUN", true));

        words.put("1D", new Word(0, 0, "BEAR", false));
        words.put("7D", new Word(0, 2, "OCEAN", false));
        words.put("8D", new Word(2, 5, "MUSIC", false));
        words.put("9D", new Word(0, 6, "RAIN", false));
        words.put("10D", new Word(0, 8, "APPLE", false));
        words.put("11D", new Word(3, 9, "FLOWER", false));
    }

    private void setClueNumbers() {
        clueNumbers[0][0] = "1";
        clueNumbers[2][0] = "2";
        clueNumbers[3][2] = "3";
        clueNumbers[4][0] = "4";
        clueNumbers[6][0] = "5";
        clueNumbers[8][0] = "6";
        clueNumbers[0][2] = "7";
        clueNumbers[2][5] = "8";
        clueNumbers[0][6] = "9";
        clueNumbers[0][8] = "10";
        clueNumbers[3][9] = "11";
    }

    private String getCluesText() {
        return
            "ACROSS:\n\n" +
            "1. You read this (4 letters)\n" +
            "2. Shines at night (4 letters)\n" +
            "3. Woody plant with branches (4 letters)\n" +
            "4. Large juicy summer fruit (10 letters)\n" +
            "5. You sit on this (5 letters)\n" +
            "6. Star in our solar system (3 letters)\n\n" +
            "DOWN:\n\n" +
            "1. Large furry animal (4 letters)\n" +
            "7. Large body of salt water (5 letters)\n" +
            "8. Melody and rhythm (5 letters)\n" +
            "9. Water from clouds (4 letters)\n" +
            "10. Red fruit, keeps doctor away (5 letters)\n" +
            "11. Beautiful plant with petals (6 letters)\n";
    }

    private void giveHint() {
        Toolkit.getDefaultToolkit().beep();
        for (Word word : words.values()) {
            for (int i = 0; i < word.text.length(); i++) {
                int r = word.isAcross ? word.row : word.row + i;
                int c = word.isAcross ? word.col + i : word.col;

                if (grid[r][c].getText().isEmpty()) {
                    grid[r][c].setText(String.valueOf(word.text.charAt(i)));
                    grid[r][c].setForeground(Color.ORANGE);
                    score -= 3;
                    updateScore();
                    return;
                }
            }
        }
    }

    private void revealWord() {
        Toolkit.getDefaultToolkit().beep();
        for (Word word : words.values()) {
            for (int i = 0; i < word.text.length(); i++) {
                int r = word.isAcross ? word.row : word.row + i;
                int c = word.isAcross ? word.col + i : word.col;

                grid[r][c].setText(String.valueOf(word.text.charAt(i)));
                grid[r][c].setForeground(Color.BLUE);
            }
        }
        score -= 10;
        updateScore();
    }

    private void checkAnswers() {
        boolean hasError = false;

        for (Word word : words.values()) {
            StringBuilder user = new StringBuilder();

            for (int i = 0; i < word.text.length(); i++) {
                int r = word.isAcross ? word.row : word.row + i;
                int c = word.isAcross ? word.col + i : word.col;
                String t = grid[r][c].getText().toUpperCase();
                user.append(t.isEmpty() ? "_" : t);
            }

            boolean correct = user.toString().equals(word.text);

            for (int i = 0; i < word.text.length(); i++) {
                int r = word.isAcross ? word.row : word.row + i;
                int c = word.isAcross ? word.col + i : word.col;
                grid[r][c].setForeground(correct ? Color.GREEN : Color.RED);
            }

            if (correct) score += 5;
            if (!correct && !user.toString().contains("_")) hasError = true;
        }

        updateScore();

        if (hasError) {
            Toolkit.getDefaultToolkit().beep();
            lives--;
            updateLivesDisplay();
            if (lives <= 0) {
                JOptionPane.showMessageDialog(this, "Game Over!");
                resetGame();
            }
        }

        if (isPuzzleComplete()) {
            timer.stop();
            celebrateWin();
            JOptionPane.showMessageDialog(this, "🎉 Congratulations! Puzzle completed!");
        }
    }

    private void celebrateWin() {
        getContentPane().setBackground(Color.GREEN);
        new javax.swing.Timer(300, e -> getContentPane().setBackground(null)).start();
    }

    private boolean isPuzzleComplete() {
        for (Word word : words.values()) {
            for (int i = 0; i < word.text.length(); i++) {
                int r = word.isAcross ? word.row : word.row + i;
                int c = word.isAcross ? word.col + i : word.col;

                if (!grid[r][c].getText().equalsIgnoreCase(
                        String.valueOf(word.text.charAt(i))))
                    return false;
            }
        }
        return true;
    }

    private void updateLivesDisplay() {
        livesLabel.setText("Lives: " + "*".repeat(lives));
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }

    private void resetGame() {
        lives = 3;
        score = 0;
        secondsPassed = 0;
        timer.restart();
        updateLivesDisplay();
        updateScore();

        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++)
                if (structure[i][j] == 0) {
                    grid[i][j].setText("");
                    grid[i][j].setForeground(Color.BLACK);
                }
    }

    private void moveToNextCell(int row, int col) {
        for (int j = col + 1; j < GRID_SIZE; j++)
            if (structure[row][j] == 0) { grid[row][j].requestFocus(); return; }

        for (int i = row + 1; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++)
                if (structure[i][j] == 0) { grid[i][j].requestFocus(); return; }
    }

    private void moveToPreviousCell(int row, int col) {
        for (int j = col - 1; j >= 0; j--)
            if (structure[row][j] == 0) { grid[row][j].requestFocus(); return; }

        for (int i = row - 1; i >= 0; i--)
            for (int j = GRID_SIZE - 1; j >= 0; j--)
                if (structure[i][j] == 0) { grid[i][j].requestFocus(); return; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CrosswordPuzzle::new);
    }
}
