package it.unibo.tetraj.model;

import java.util.List;

/**
 * The main game model that manages game state and logic.
 * This is the "Model" in the MVC pattern.
 */
public final class GameModel {

    /** Points for clearing lines. */
    private static final int[] LINE_POINTS = {0, 100, 300, 500, 800};

    /** Base fall speed in milliseconds. */
    private static final double BASE_FALL_SPEED = 1000;

    /** Speed increase per level. */
    private static final double SPEED_MULTIPLIER = 0.9;

    private final Board board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Tetromino heldPiece;
    private boolean canHold;

    private int score;
    private int level;
    private int linesCleared;
    private GameState state;

    private double fallTimer;
    private double fallSpeed;

    /**
     * Enum for game states.
     */
    public enum GameState {
        MENU,
        PLAYING,
        PAUSED,
        GAME_OVER
    }

    /**
     * Creates a new game model.
     */
    public GameModel() {
        this.board = new Board();
        this.state = GameState.MENU;
        this.canHold = true;
    }

    /**
     * Starts a new game.
     */
    public void startNewGame() {
        board.clear();
        score = 0;
        level = 1;
        linesCleared = 0;
        fallTimer = 0;
        fallSpeed = BASE_FALL_SPEED;
        canHold = true;
        heldPiece = null;

        currentPiece = new Tetromino(TetrominoType.random(), board.getWidth());
        nextPiece = new Tetromino(TetrominoType.random(), board.getWidth());
        state = GameState.PLAYING;
    }

    /**
     * Updates the game logic.
     *
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(final double deltaTime) {
        if (state != GameState.PLAYING) {
            return;
        }

        // Update fall timer
        fallTimer += deltaTime * 1000; // Convert to milliseconds

        if (fallTimer >= fallSpeed) {
            fallTimer = 0;
            moveDown();
        }
    }

    /**
     * Moves the current piece left.
     */
    public void moveLeft() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return;
        }

        currentPiece.move(-1, 0);
        if (!board.isValidPosition(currentPiece)) {
            currentPiece.move(1, 0);
        }
    }

    /**
     * Moves the current piece right.
     */
    public void moveRight() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return;
        }

        currentPiece.move(1, 0);
        if (!board.isValidPosition(currentPiece)) {
            currentPiece.move(-1, 0);
        }
    }

    /**
     * Moves the current piece down.
     *
     * @return true if the piece was placed
     */
    public boolean moveDown() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return false;
        }

        currentPiece.move(0, 1);
        if (!board.isValidPosition(currentPiece)) {
            currentPiece.move(0, -1);
            placePiece();
            return true;
        }
        return false;
    }

    /**
     * Hard drops the current piece.
     */
    public void hardDrop() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return;
        }

        int dropDistance = 0;
        while (board.isValidPosition(currentPiece)) {
            currentPiece.move(0, 1);
            dropDistance++;
        }
        currentPiece.move(0, -1);
        dropDistance--;

        // Award points for hard drop
        score += dropDistance * 2;

        placePiece();
    }

    /**
     * Rotates the current piece clockwise.
     */
    public void rotateClockwise() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return;
        }

        currentPiece.rotateClockwise();
        if (!board.isValidPosition(currentPiece)) {
            // Try wall kicks
            if (!tryWallKick()) {
                currentPiece.rotateCounterClockwise();
            }
        }
    }

    /**
     * Rotates the current piece counter-clockwise.
     */
    public void rotateCounterClockwise() {
        if (state != GameState.PLAYING || currentPiece == null) {
            return;
        }

        currentPiece.rotateCounterClockwise();
        if (!board.isValidPosition(currentPiece)) {
            // Try wall kicks
            if (!tryWallKick()) {
                currentPiece.rotateClockwise();
            }
        }
    }

    /**
     * Holds the current piece.
     */
    public void holdPiece() {
        if (state != GameState.PLAYING || !canHold || currentPiece == null) {
            return;
        }

        canHold = false;

        if (heldPiece == null) {
            heldPiece = currentPiece;
            currentPiece = nextPiece;
            nextPiece = new Tetromino(TetrominoType.random(), board.getWidth());
        } else {
            final Tetromino temp = currentPiece;
            currentPiece = heldPiece;
            heldPiece = temp;
            currentPiece.setPosition((board.getWidth() - currentPiece.getWidth()) / 2, 0);
        }
    }

    /**
     * Tries wall kick adjustments.
     *
     * @return true if a valid position was found with wall kick
     */
    private boolean tryWallKick() {
        // Simple wall kick: try moving left, right, and up
        final int[] xOffsets = {-1, 1, -2, 2, 0};
        final int[] yOffsets = {0, 0, 0, 0, -1};

        for (int i = 0; i < xOffsets.length; i++) {
            currentPiece.move(xOffsets[i], yOffsets[i]);
            if (board.isValidPosition(currentPiece)) {
                return true;
            }
            currentPiece.move(-xOffsets[i], -yOffsets[i]);
        }
        return false;
    }

    /**
     * Places the current piece on the board.
     */
    private void placePiece() {
        board.placeTetromino(currentPiece);

        // Clear lines and update score
        final List<Integer> clearedLines = board.clearCompletedLines();
        if (!clearedLines.isEmpty()) {
            updateScore(clearedLines.size());
        }

        // Spawn next piece
        currentPiece = nextPiece;
        nextPiece = new Tetromino(TetrominoType.random(), board.getWidth());
        canHold = true;
        fallTimer = 0;

        // Check game over
        if (!board.isValidPosition(currentPiece)) {
            state = GameState.GAME_OVER;
        }
    }

    /**
     * Updates score and level based on lines cleared.
     *
     * @param lines The number of lines cleared
     */
    private void updateScore(final int lines) {
        linesCleared += lines;
        score += LINE_POINTS[Math.min(lines, LINE_POINTS.length - 1)] * level;

        // Level up every 10 lines
        final int newLevel = (linesCleared / 10) + 1;
        if (newLevel > level) {
            level = newLevel;
            fallSpeed = BASE_FALL_SPEED * Math.pow(SPEED_MULTIPLIER, level - 1);
        }
    }

    /**
     * Pauses or unpauses the game.
     */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    // Getters for View to access state

    /**
     * Gets the game board.
     *
     * @return The game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets a copy of the current falling piece.
     *
     * @return A copy of the current piece, or null if no piece is active
     */
    public Tetromino getCurrentPiece() {
        return currentPiece == null ? null : new Tetromino(currentPiece);
    }

    /**
     * Gets a copy of the next piece to spawn.
     *
     * @return A copy of the next piece
     */
    public Tetromino getNextPiece() {
        return nextPiece == null ? null : new Tetromino(nextPiece);
    }

    /**
     * Gets a copy of the held piece.
     *
     * @return A copy of the held piece, or null if no piece is held
     */
    public Tetromino getHeldPiece() {
        return heldPiece == null ? null : new Tetromino(heldPiece);
    }

    /**
     * Gets the current score.
     *
     * @return The score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the current level.
     *
     * @return The level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the total lines cleared.
     *
     * @return The number of lines cleared
     */
    public int getLinesCleared() {
        return linesCleared;
    }

    /**
     * Gets the game state.
     *
     * @return The current game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the game state.
     *
     * @param state The new game state
     */
    public void setState(final GameState state) {
        this.state = state;
    }

    /**
     * Gets a preview of where the current piece would land.
     *
     * @return A ghost piece showing the landing position
     */
    public Tetromino getGhostPiece() {
        if (currentPiece == null) {
            return null;
        }

        final Tetromino ghost = new Tetromino(currentPiece);

        while (board.isValidPosition(ghost)) {
            ghost.move(0, 1);
        }
        ghost.move(0, -1);

        return ghost;
    }
}
