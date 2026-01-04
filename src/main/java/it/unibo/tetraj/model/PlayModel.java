package it.unibo.tetraj.model;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoFactory;
import it.unibo.tetraj.model.piece.selection.BagRandomizerStrategy;
import it.unibo.tetraj.util.ResourceManager;
import java.util.List;
import java.util.function.Consumer;

/** Model for the playing state. Manages game logic and state. */
public final class PlayModel {

  private static final int[] LINE_POINTS = {0, 100, 300, 500, 800};
  private static final double BASE_FALL_SPEED = 1000;
  private static final double SPEED_MULTIPLIER = 0.9;
  private final TetrominoFactory tetrominoFactory;
  private final Board board;
  private final ResourceManager resources;
  private AbstractTetromino<?> currentPiece;
  private AbstractTetromino<?> nextPiece;
  private AbstractTetromino<?> heldPiece;
  private boolean canHold;
  private int score;
  private int level;
  private int linesCleared;
  private double fallTimer;
  private double fallSpeed;
  private boolean gameOver;

  /** Creates a new play model. */
  public PlayModel() {
    board = new Board();
    tetrominoFactory = new TetrominoFactory(new BagRandomizerStrategy());
    resources = ResourceManager.getInstance();
    startNewGame();
  }

  /** Starts a new game. */
  public void startNewGame() {
    board.clear();
    score = 0;
    level = 1;
    linesCleared = 0;
    fallTimer = 0;
    fallSpeed = BASE_FALL_SPEED;
    canHold = true;
    heldPiece = null;
    gameOver = false;

    currentPiece = tetrominoFactory.create();
    centerPieceToTop(currentPiece);
    nextPiece = tetrominoFactory.create();
    centerPieceToTop(nextPiece);
  }

  /**
   * Updates the game logic.
   *
   * @param deltaTime Time elapsed since last update in seconds
   */
  public void update(final double deltaTime) {
    if (gameOver) {
      return;
    }

    fallTimer += deltaTime * 1000;
    if (fallTimer >= fallSpeed) {
      fallTimer = 0;
      moveDown();
    }
  }

  /** Moves the current piece left. */
  public void moveLeft() {
    if (tryMove(-1, 0)) {
      resources.playSound("move.wav");
    }
  }

  /** Moves the current piece right. */
  public void moveRight() {
    if (tryMove(1, 0)) {
      resources.playSound("move.wav");
    }
  }

  /**
   * Moves the current piece down.
   *
   * @return true if the piece was placed
   */
  public boolean moveDown() {
    if (!tryMove(0, 1)) {
      placePiece();
      return true;
    }
    return false;
  }

  /** Hard drops the current piece. */
  public void hardDrop() {
    if (gameOver || currentPiece == null) {
      return;
    }

    int dropDistance = 0;
    while (board.isValidPosition(currentPiece)) {
      currentPiece.move(0, 1);
      dropDistance++;
    }
    currentPiece.move(0, -1);
    dropDistance--;

    score += dropDistance * 2;
    placePiece();
    resources.playSound("drop.wav");
  }

  /** Rotates the current piece clockwise. */
  public void rotateClockwise() {
    if (tryRotate(AbstractTetromino::rotateClockwise, AbstractTetromino::rotatecounterClockwise)) {
      resources.playSound("rotate.wav");
    }
  }

  /** Rotates the current piece counterclockwise. */
  public void rotatecounterClockwise() {
    if (tryRotate(AbstractTetromino::rotatecounterClockwise, AbstractTetromino::rotateClockwise)) {
      resources.playSound("rotate.wav");
    }
  }

  /** Holds the current piece. */
  public void holdPiece() {
    if (gameOver || !canHold || currentPiece == null) {
      return;
    }

    canHold = false;

    if (heldPiece == null) {
      heldPiece = currentPiece;
      currentPiece = nextPiece;
      nextPiece = tetrominoFactory.create();
      centerPieceToTop(nextPiece);
    } else {
      final AbstractTetromino<?> temp = currentPiece;
      currentPiece = heldPiece;
      heldPiece = temp;
      centerPieceToTop(currentPiece);
    }
  }

  private boolean tryRotate(
      final Consumer<AbstractTetromino<?>> action,
      final Consumer<AbstractTetromino<?>> undoAction) {
    if (gameOver || currentPiece == null) {
      return false;
    }

    action.accept(currentPiece);
    if (!board.isValidPosition(currentPiece) && !tryWallKick()) {
      undoAction.accept(currentPiece);
      return false;
    }
    return true;
  }

  private boolean tryMove(final int dx, final int dy) {
    if (gameOver || currentPiece == null) {
      return false;
    }

    currentPiece.move(dx, dy);
    if (!board.isValidPosition(currentPiece)) {
      currentPiece.move(-dx, -dy);
      return false;
    }
    return true;
  }

  /*
   * Attempts to find a valid position for the rotated piece using wall kick.
   * Wall kick allows pieces to "slide" into valid positions when rotation
   * would cause collision with walls or other pieces.
   *
   * Tests positions in order:
   * 1. One cell left
   * 2. One cell right
   * 3. Two cells left (for I-piece near walls)
   * 4. Two cells right
   * 5. One cell up (floor kick)
   *
   * Returns true if a valid position is found, false if rotation is blocked.
   */
  private boolean tryWallKick() {
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

  private void placePiece() {
    board.placeTetromino(currentPiece);

    final List<Integer> clearedLines = board.clearCompletedLines();
    if (!clearedLines.isEmpty()) {
      updateScore(clearedLines.size());
      resources.playSound("clear.wav");
    }

    currentPiece = nextPiece;
    nextPiece = tetrominoFactory.create();
    centerPieceToTop(nextPiece);
    canHold = true;
    fallTimer = 0;

    if (!board.isValidPosition(currentPiece)) {
      gameOver = true;
    }
  }

  private void updateScore(final int lines) {
    linesCleared += lines;
    score += LINE_POINTS[Math.min(lines, LINE_POINTS.length - 1)] * level;

    final int newLevel = (linesCleared / 10) + 1;
    if (newLevel > level) {
      level = newLevel;
      fallSpeed = BASE_FALL_SPEED * Math.pow(SPEED_MULTIPLIER, level - 1);
      resources.playSound("levelUp.wav");
    }
  }

  private void centerPieceToTop(final AbstractTetromino<?> tetromino) {
    tetromino.setPosition((board.getWidth() - tetromino.getWidth()) / 2, 0);
  }

  /**
   * Gets the game board.
   *
   * @return The game board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets the current piece.
   *
   * @return The current piece or null
   */
  public AbstractTetromino<?> getCurrentPiece() {
    return currentPiece.copy();
  }

  /**
   * Gets the next piece.
   *
   * @return The next piece or null
   */
  public AbstractTetromino<?> getNextPiece() {
    return nextPiece.copy();
  }

  /**
   * Gets the held piece.
   *
   * @return The held piece or null
   */
  public AbstractTetromino<?> getHeldPiece() {
    return heldPiece.copy();
  }

  /**
   * Gets a ghost piece showing where the current piece will land.
   *
   * @return The ghost piece or null
   */
  public AbstractTetromino<?> getGhostPiece() {
    if (currentPiece == null) {
      return null;
    }

    final AbstractTetromino<?> ghost = currentPiece.copy();
    while (board.isValidPosition(ghost)) {
      ghost.move(0, 1);
    }
    ghost.move(0, -1);
    return ghost;
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
   * Gets total lines cleared.
   *
   * @return The lines cleared
   */
  public int getLinesCleared() {
    return linesCleared;
  }

  /**
   * Checks if the game is over.
   *
   * @return true if game is over
   */
  public boolean isGameOver() {
    return gameOver;
  }
}
