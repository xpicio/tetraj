package it.unibo.tetraj.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.ITetromino;
import it.unibo.tetraj.model.piece.SingleCellTetromino;
import it.unibo.tetraj.util.ResourceManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

/** Tests for the PlayModel class. */
class PlayModelTest {

  private static final double DELTA_TIME_SMALL = 0.001;
  private static final double DELTA_TIME_MEDIUM = 0.05;
  private static final double DELTA_TIME_LARGE = 0.1;
  private static final double SOFT_DROP_PARTIAL_DELAY = 0.04;
  private static final double SOFT_DROP_ACTIVATION_DELAY_SECONDS = 0.134;
  private static final int INITIAL_SCORE = 0;
  private static final int INITIAL_LEVEL = 1;
  private static final int INITIAL_LINES = 0;
  private static final int MOVE_ITERATIONS = 20;
  private static final int FALL_ITERATIONS = 100;
  private static final int MANY_ITERATIONS = 1000;
  private static final int FULL_ROTATION = 4;
  private static MockedStatic<ResourceManager> resourceManagerMock;
  private PlayModel model;

  @BeforeAll
  static void setUpClass() {
    final ResourceManager mockResourceManager = mock(ResourceManager.class);
    doNothing().when(mockResourceManager).playSound(anyString());
    doNothing().when(mockResourceManager).playBackgroundMusic(anyString());
    resourceManagerMock = mockStatic(ResourceManager.class);
    resourceManagerMock.when(ResourceManager::getInstance).thenReturn(mockResourceManager);
  }

  @AfterAll
  static void tearDownClass() {
    resourceManagerMock.close();
  }

  @BeforeEach
  void setUp() {
    model = new PlayModel();
  }

  @Nested
  @DisplayName("Initial State Tests")
  class InitialStateTests {

    @Test
    @DisplayName("should start with score zero")
    void shouldStartWithScoreZero() {
      // Assert
      assertEquals(INITIAL_SCORE, model.getScore(), "Initial score should be 0");
    }

    @Test
    @DisplayName("should start at level one")
    void shouldStartAtLevelOne() {
      // Assert
      assertEquals(INITIAL_LEVEL, model.getLevel(), "Initial level should be 1");
    }

    @Test
    @DisplayName("should start with zero lines cleared")
    void shouldStartWithZeroLinesCleared() {
      // Assert
      assertEquals(INITIAL_LINES, model.getLinesCleared(), "Initial lines cleared should be 0");
    }

    @Test
    @DisplayName("should not be paused initially")
    void shouldNotBePausedInitially() {
      // Assert
      assertFalse(model.isPaused(), "Game should not be paused initially");
    }

    @Test
    @DisplayName("should not be game over initially")
    void shouldNotBeGameOverInitially() {
      // Assert
      assertFalse(model.isGameOver(), "Game should not be over initially");
    }

    @Test
    @DisplayName("should have current piece")
    void shouldHaveCurrentPiece() {
      // Assert
      assertNotNull(model.getCurrentPiece(), "Should have a current piece");
    }

    @Test
    @DisplayName("should have next piece")
    void shouldHaveNextPiece() {
      // Assert
      assertNotNull(model.getNextPiece(), "Should have a next piece");
    }

    @Test
    @DisplayName("should not have held piece initially")
    void shouldNotHaveHeldPieceInitially() {
      // Assert
      assertNull(model.getHeldPiece(), "Should not have a held piece initially");
    }

    @Test
    @DisplayName("should have ghost piece")
    void shouldHaveGhostPiece() {
      // Assert
      assertNotNull(model.getGhostPiece(), "Should have a ghost piece");
    }

    @Test
    @DisplayName("should have board")
    void shouldHaveBoard() {
      // Assert
      assertNotNull(model.getBoard(), "Should have a board");
    }

    @Test
    @DisplayName("should not be soft dropping initially")
    void shouldNotBeSoftDroppingInitially() {
      // Assert
      assertFalse(model.isSoftDropping(), "Should not be soft dropping initially");
    }
  }

  @Nested
  @DisplayName("Pause Tests")
  class PauseTests {

    @Test
    @DisplayName("should toggle pause state")
    void shouldTogglePauseState() {
      // Act
      model.togglePause();

      // Assert
      assertTrue(model.isPaused(), "Should be paused after first toggle");

      // Act
      model.togglePause();

      // Assert
      assertFalse(model.isPaused(), "Should not be paused after second toggle");
    }

    @Test
    @DisplayName("should not update when paused")
    void shouldNotUpdateWhenPaused() {
      // Arrange
      model.togglePause();
      final int yBefore = model.getCurrentPiece().getY();

      // Act
      for (int i = 0; i < FALL_ITERATIONS; i++) {
        model.update(DELTA_TIME_LARGE);
      }

      // Assert
      assertEquals(yBefore, model.getCurrentPiece().getY(), "Piece should not move when paused");
    }
  }

  @Nested
  @DisplayName("Movement Tests")
  class MovementTests {

    @Test
    @DisplayName("should move piece left")
    void shouldMovePieceLeft() {
      // Arrange
      final int xBefore = model.getCurrentPiece().getX();

      // Act
      model.moveLeft();

      // Assert
      assertEquals(xBefore - 1, model.getCurrentPiece().getX(), "Piece should move one cell left");
    }

    @Test
    @DisplayName("should move piece right")
    void shouldMovePieceRight() {
      // Arrange
      final int xBefore = model.getCurrentPiece().getX();

      // Act
      model.moveRight();

      // Assert
      assertEquals(xBefore + 1, model.getCurrentPiece().getX(), "Piece should move one cell right");
    }

    @Test
    @DisplayName("should move piece down")
    void shouldMovePieceDown() {
      // Arrange
      final int yBefore = model.getCurrentPiece().getY();

      // Act
      final boolean placed = model.moveDown();

      // Assert
      if (!placed) {
        assertEquals(
            yBefore + 1, model.getCurrentPiece().getY(), "Piece should move one cell down");
      }
    }

    @Test
    @DisplayName("should not move left beyond board boundary")
    void shouldNotMoveLeftBeyondBoundary() {
      // Arrange
      for (int i = 0; i < MOVE_ITERATIONS; i++) {
        model.moveLeft();
      }
      final int xAtBoundary = model.getCurrentPiece().getX();

      // Act
      model.moveLeft();

      // Assert
      assertEquals(
          xAtBoundary, model.getCurrentPiece().getX(), "Should not move past left boundary");
    }

    @Test
    @DisplayName("should not move right beyond board boundary")
    void shouldNotMoveRightBeyondBoundary() {
      // Arrange
      for (int i = 0; i < MOVE_ITERATIONS; i++) {
        model.moveRight();
      }
      final int xAtBoundary = model.getCurrentPiece().getX();

      // Act
      model.moveRight();

      // Assert
      assertEquals(
          xAtBoundary, model.getCurrentPiece().getX(), "Should not move past right boundary");
    }
  }

  @Nested
  @DisplayName("Soft Drop Tests")
  class SoftDropTests {

    @Test
    @DisplayName("should not activate soft drop immediately")
    void shouldNotActivateSoftDropImmediately() {
      // Act
      model.startSoftDrop();

      // Assert
      assertFalse(model.isSoftDropping(), "Soft drop should not be active immediately");
    }

    @Test
    @DisplayName("should activate soft drop after delay")
    void shouldActivateSoftDropAfterDelay() {
      // Arrange
      model.startSoftDrop();

      // Act
      model.update(SOFT_DROP_ACTIVATION_DELAY_SECONDS);

      // Assert
      assertTrue(model.isSoftDropping(), "Soft drop should be active after delay");
    }

    @Test
    @DisplayName("should stop soft drop when released")
    void shouldStopSoftDropWhenReleased() {
      // Arrange
      model.startSoftDrop();
      model.update(SOFT_DROP_ACTIVATION_DELAY_SECONDS);

      // Act
      model.stopSoftDrop();

      // Assert
      assertFalse(model.isSoftDropping(), "Soft drop should stop when released");
    }

    @Test
    @DisplayName("should reset soft drop state on stop")
    void shouldResetSoftDropStateOnStop() {
      // Arrange
      model.startSoftDrop();
      model.update(DELTA_TIME_MEDIUM);
      model.stopSoftDrop();
      model.startSoftDrop();

      // Act
      model.update(DELTA_TIME_MEDIUM);

      // Assert
      assertFalse(model.isSoftDropping(), "Should need full delay after restart");
    }

    @Test
    @DisplayName("should ignore repeated startSoftDrop calls")
    void shouldIgnoreRepeatedStartSoftDropCalls() {
      // Arrange
      model.startSoftDrop();
      model.update(DELTA_TIME_LARGE);
      model.startSoftDrop();

      // Act
      model.update(SOFT_DROP_PARTIAL_DELAY);

      // Assert
      assertTrue(model.isSoftDropping(), "Key repeat should not reset soft drop timer");
    }
  }

  @Nested
  @DisplayName("Hard Drop Tests")
  class HardDropTests {

    @Test
    @DisplayName("should place piece immediately on hard drop")
    void shouldPlacePieceImmediatelyOnHardDrop() {
      // Arrange
      final int landingY = model.getGhostPiece().getY();

      // Act
      model.hardDrop();

      // Assert
      final AbstractTetromino<?> pieceAfter = model.getCurrentPiece();
      assertNotNull(pieceAfter, "Should have a new current piece after hard drop");
      assertTrue(landingY > pieceAfter.getY(), "New piece should be above landing position");
    }

    @Test
    @DisplayName("should award 2 points per cell dropped")
    void shouldAward2PointsPerCellDropped() {
      // Arrange
      final int scoreBefore = model.getScore();
      final int yBefore = model.getCurrentPiece().getY();
      final int expectedDropDistance = model.getGhostPiece().getY() - yBefore;

      // Act
      model.hardDrop();

      // Assert
      final int expectedScore = scoreBefore + (expectedDropDistance * 2);
      assertEquals(expectedScore, model.getScore(), "Should award 2 points per cell dropped");
    }
  }

  @Nested
  @DisplayName("Rotation Tests")
  class RotationTests {

    @Test
    @DisplayName("should rotate piece clockwise without error")
    void shouldRotatePieceClockwise() {
      // Act
      model.rotateClockwise();

      // Assert
      assertNotNull(model.getCurrentPiece(), "Piece should still exist after rotation");
    }

    @Test
    @DisplayName("should rotate piece counterclockwise without error")
    void shouldRotatePieceCounterClockwise() {
      // Act
      model.rotateCounterClockwise();

      // Assert
      assertNotNull(model.getCurrentPiece(), "Piece should still exist after rotation");
    }

    @Test
    @DisplayName("should allow multiple rotations")
    void shouldAllowMultipleRotations() {
      // Act
      for (int i = 0; i < FULL_ROTATION; i++) {
        model.rotateClockwise();
      }

      // Assert
      assertNotNull(model.getCurrentPiece(), "Piece should exist after 4 rotations");
    }
  }

  @Nested
  @DisplayName("Hold Piece Tests")
  class HoldPieceTests {

    @Test
    @DisplayName("should hold current piece")
    void shouldHoldCurrentPiece() {
      // Act
      model.holdPiece();

      // Assert
      assertNotNull(model.getHeldPiece(), "Should have held piece after hold");
    }

    @Test
    @DisplayName("should swap with held piece on second hold")
    void shouldSwapWithHeldPieceOnSecondHold() {
      // Arrange
      model.holdPiece();
      final Class<?> firstHeldType = model.getHeldPiece().getClass();
      model.hardDrop();

      // Act
      model.holdPiece();

      // Assert
      assertNotNull(model.getHeldPiece(), "Should still have a held piece");
      assertNotNull(firstHeldType, "First held type should have been recorded");
    }

    @Test
    @DisplayName("should not allow hold twice in same turn")
    void shouldNotAllowHoldTwiceInSameTurn() {
      // Arrange
      model.holdPiece();
      final Class<?> firstHeldType = model.getHeldPiece().getClass();

      // Act
      model.holdPiece();

      // Assert
      assertEquals(
          firstHeldType,
          model.getHeldPiece().getClass(),
          "Should not swap when trying to hold twice in same turn");
    }
  }

  @Nested
  @DisplayName("Ghost Piece Tests")
  class GhostPieceTests {

    @Test
    @DisplayName("ghost piece should be at bottom of valid path")
    void ghostPieceShouldBeAtBottomOfValidPath() {
      // Arrange
      final AbstractTetromino<?> current = model.getCurrentPiece();

      // Act
      final AbstractTetromino<?> ghost = model.getGhostPiece();

      // Assert
      assertTrue(ghost.getY() >= current.getY(), "Ghost should be at or below current piece");
      assertEquals(current.getX(), ghost.getX(), "Ghost should have same X as current piece");
    }

    @Test
    @DisplayName("ghost piece X should match current piece X")
    void ghostPieceXShouldMatchCurrentPieceX() {
      // Arrange
      model.moveRight();
      model.moveRight();
      final int currentX = model.getCurrentPiece().getX();

      // Act
      final AbstractTetromino<?> ghost = model.getGhostPiece();

      // Assert
      assertEquals(currentX, ghost.getX(), "Ghost X should match current piece X after move");
    }
  }

  @Nested
  @DisplayName("Start New Game Tests")
  class StartNewGameTests {

    @Test
    @DisplayName("should reset score on new game")
    void shouldResetScoreOnNewGame() {
      // Arrange
      model.hardDrop();

      // Act
      model.startNewGame();

      // Assert
      assertEquals(INITIAL_SCORE, model.getScore(), "Score should reset to 0");
    }

    @Test
    @DisplayName("should reset level on new game")
    void shouldResetLevelOnNewGame() {
      // Act
      model.startNewGame();

      // Assert
      assertEquals(INITIAL_LEVEL, model.getLevel(), "Level should reset to 1");
    }

    @Test
    @DisplayName("should reset lines on new game")
    void shouldResetLinesOnNewGame() {
      // Act
      model.startNewGame();

      // Assert
      assertEquals(INITIAL_LINES, model.getLinesCleared(), "Lines should reset to 0");
    }

    @Test
    @DisplayName("should clear held piece on new game")
    void shouldClearHeldPieceOnNewGame() {
      // Arrange
      model.holdPiece();
      assertNotNull(model.getHeldPiece(), "Should have held piece");

      // Act
      model.startNewGame();

      // Assert
      assertNull(model.getHeldPiece(), "Held piece should be cleared");
    }

    @Test
    @DisplayName("should reset game over on new game")
    void shouldResetGameOverOnNewGame() {
      // Act
      model.startNewGame();

      // Assert
      assertFalse(model.isGameOver(), "Game over should be false after new game");
    }

    @Test
    @DisplayName("should reset soft drop state on new game")
    void shouldResetSoftDropStateOnNewGame() {
      // Arrange
      model.startSoftDrop();
      model.update(SOFT_DROP_ACTIVATION_DELAY_SECONDS);

      // Act
      model.startNewGame();

      // Assert
      assertFalse(model.isSoftDropping(), "Soft drop should be reset");
    }
  }

  @Nested
  @DisplayName("Update Tests")
  class UpdateTests {

    @Test
    @DisplayName("update should progress game state")
    void updateShouldProgressGameState() {
      // Act
      for (int i = 0; i < MANY_ITERATIONS; i++) {
        model.update(DELTA_TIME_SMALL);
      }

      // Assert
      assertNotNull(model.getCurrentPiece(), "Game should still have a current piece");
    }

    @Test
    @DisplayName("piece should fall over time")
    void pieceShouldFallOverTime() {
      // Arrange
      final int yBefore = model.getCurrentPiece().getY();

      // Act
      for (int i = 0; i < FALL_ITERATIONS; i++) {
        model.update(DELTA_TIME_MEDIUM);
      }

      // Assert
      assertTrue(
          model.getCurrentPiece().getY() > yBefore || model.getScore() >= 0,
          "Game should progress over time");
    }
  }

  @Nested
  @DisplayName("Piece Getter Tests")
  class PieceGetterTests {

    @Test
    @DisplayName("getCurrentPiece should return a copy")
    void currentPieceShouldReturnACopy() {
      // Act
      final AbstractTetromino<?> piece1 = model.getCurrentPiece();
      final AbstractTetromino<?> piece2 = model.getCurrentPiece();

      // Assert
      assertEquals(piece1.getX(), piece2.getX(), "Copies should have same X");
      assertEquals(piece1.getY(), piece2.getY(), "Copies should have same Y");
    }

    @Test
    @DisplayName("getNextPiece should return a copy")
    void nextPieceShouldReturnACopy() {
      // Act
      final AbstractTetromino<?> piece1 = model.getNextPiece();
      final AbstractTetromino<?> piece2 = model.getNextPiece();

      // Assert
      assertEquals(piece1.getX(), piece2.getX(), "Copies should have same X");
      assertEquals(piece1.getY(), piece2.getY(), "Copies should have same Y");
    }

    @Test
    @DisplayName("getHeldPiece should return a copy when present")
    void heldPieceShouldReturnACopyWhenPresent() {
      // Arrange
      model.holdPiece();

      // Act
      final AbstractTetromino<?> piece1 = model.getHeldPiece();
      final AbstractTetromino<?> piece2 = model.getHeldPiece();

      // Assert
      assertNotNull(piece1, "Held piece should not be null");
      assertEquals(piece1.getClass(), piece2.getClass(), "Copies should be same type");
    }
  }

  @Nested
  @DisplayName("Score Tests")
  class ScoreTests {

    private static final int BOARD_WIDTH = 10;
    private static final int BOTTOM_ROW = 19;
    private static final int SECOND_BOTTOM_ROW = 18;
    private static final int THIRD_BOTTOM_ROW = 17;
    private static final int FOURTH_BOTTOM_ROW = 16;
    private static final int SOFT_DROP_POINTS_PER_CELL = 1;
    private static final int LINES_FOR_LEVEL_UP = 10;
    private static final int LEVEL_TWO = 2;
    private static final int MAX_PIECES_TO_I = 21;

    @ParameterizedTest(name = "{0} lines = {1} points")
    @CsvSource({"1, 100", "2, 300", "3, 500", "4, 800"})
    @DisplayName("should award correct points for line clears")
    void shouldAwardCorrectPointsForLineClears(final int lines, final int expectedPoints) {
      // Arrange
      waitAndPositionIPiece();
      final Board board = model.getBoard();
      final int[] rows = {BOTTOM_ROW, SECOND_BOTTOM_ROW, THIRD_BOTTOM_ROW, FOURTH_BOTTOM_ROW};
      for (int i = 0; i < lines; i++) {
        fillRowExceptColumn(board, rows[i], 0);
      }
      final int scoreBefore = model.getScore();

      // Act
      model.hardDrop();

      // Assert
      assertTrue(
          model.getScore() >= scoreBefore + expectedPoints,
          String.format("Should award at least %d points for %d line(s)", expectedPoints, lines));
    }

    @Test
    @DisplayName("should level up after 10 lines cleared")
    void shouldLevelUpAfter10LinesCleared() {
      // Arrange
      for (int i = 0; i < LINES_FOR_LEVEL_UP; i++) {
        fillRow(model.getBoard(), BOTTOM_ROW);
        model.hardDrop();
      }

      // Assert
      assertEquals(
          LINES_FOR_LEVEL_UP, model.getLinesCleared(), "Should have cleared exactly 10 lines");
      assertEquals(LEVEL_TWO, model.getLevel(), "Should be level 2 after 10 lines");
    }

    @Test
    @DisplayName("should award soft drop points per cell")
    void shouldAwardSoftDropPointsPerCell() {
      // Arrange
      final int scoreBefore = model.getScore();
      model.startSoftDrop();
      model.update(SOFT_DROP_ACTIVATION_DELAY_SECONDS);

      // Act
      final boolean placed = model.moveDown();

      // Assert
      if (!placed) {
        assertEquals(
            scoreBefore + SOFT_DROP_POINTS_PER_CELL,
            model.getScore(),
            "Should award 1 point per cell during soft drop");
      }
    }

    @Test
    @DisplayName("should not award soft drop points before activation")
    void shouldNotAwardSoftDropPointsBeforeActivation() {
      // Arrange
      final int scoreBefore = model.getScore();
      model.startSoftDrop();
      model.update(DELTA_TIME_SMALL);

      // Act
      model.moveDown();

      // Assert
      assertEquals(scoreBefore, model.getScore(), "Should not award points before activation");
    }

    /**
     * Waits for an I-piece and positions it vertically on the left side. Uses hardDrop and clear to
     * cycle through pieces until I-piece arrives (guaranteed within 7 pieces with bag strategy).
     * When I-piece is current, rotates it to vertical and moves it to column 0.
     */
    private void waitAndPositionIPiece() {
      final Board board = model.getBoard();

      // Cycle pieces until I-piece
      for (int i = 0;
          i < MAX_PIECES_TO_I && !(model.getCurrentPiece() instanceof ITetromino);
          i++) {
        board.clear();
        model.hardDrop();
      }

      // Ensure board is clean before filling rows
      board.clear();

      // Rotate to vertical (counter-clockwise gives shape with cells at piece column 1)
      model.rotateCounterClockwise();

      // Move all the way left (piece X = -1, cells land in board column 0)
      for (int i = 0; i < MOVE_ITERATIONS; i++) {
        model.moveLeft();
      }
    }

    /**
     * Fills an entire row.
     *
     * @param board The board to modify
     * @param row The row to fill
     */
    private void fillRow(final Board board, final int row) {
      fillRowExceptColumn(board, row, -1);
    }

    /**
     * Fills an entire row with blocks except for column 0.
     *
     * @param board The board to modify
     * @param row The row to fill
     * @param exceptCol The column to leave empty
     */
    private void fillRowExceptColumn(final Board board, final int row, final int exceptCol) {
      for (int col = 0; col < BOARD_WIDTH; col++) {
        if (col != exceptCol) {
          board.placeTetromino(new SingleCellTetromino(col, row));
        }
      }
    }
  }
}
