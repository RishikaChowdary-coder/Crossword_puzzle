# Crossword_puzzle
# Crossword Puzzle Game

A simple **Crossword Puzzle Game** developed using **Java Swing**. The game provides an interactive 10×10 crossword grid with clues, scoring, lives, hints, word reveal, and a timer.

## Features

* 🧩 10×10 crossword puzzle grid
* 📝 Across and Down clues
* ❤️ 3 lives
* ⭐ Score system
* ⏱️ Game timer
* 💡 Hint option
* 🔍 Reveal Word option
* 🔄 Reset game option
* ✅ Answer checking
* 🎉 Congratulations message when the puzzle is completed
* ⌨️ Automatic movement between cells

## Technologies Used

* **Java**
* **Java Swing**
* **AWT Event Handling**

## How to Run

1. Save the program as:
   `CrosswordPuzzle.java`

2. Compile the program:

   ```bash
   javac CrosswordPuzzle.java
   ```

3. Run the program:

   ```bash
   java CrosswordPuzzle
   ```

## Game Rules

* The player starts with **3 lives**.
* Correct answers increase the score.
* An incorrect completed answer reduces one life.
* Using a **Hint** decreases the score by 3 points.
* Using **Reveal Word** decreases the score by 10 points.
* The timer records the time taken to complete the puzzle.
* The game ends when all crossword words are correctly completed.

## Crossword Words

### Across

* 1. BOOK
* 2. MOON
* 3. TREE
* 4. WATERMELON
* 5. CHAIR
* 6. SUN

### Down

* 1. BEAR
* 7. OCEAN
* 8. MUSIC
* 9. RAIN
* 10. APPLE
* 11. FLOWER

## Project Structure

```text
CrosswordPuzzle.java
└── CrosswordPuzzle
    ├── Crossword Grid
    ├── Clues
    ├── Timer
    ├── Score System
    ├── Lives System
    ├── Hint
    ├── Reveal Word
    ├── Answer Checking
    └── Reset Function
```

## Author

Developed as a Java GUI-based crossword puzzle project.
