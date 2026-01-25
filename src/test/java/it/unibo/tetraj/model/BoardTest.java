package it.unibo.tetraj.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.tetraj.model.piece.SingleCellTetromino;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the Board class. */
class BoardTest {

  private static final int BOTTOM_ROW = 19;
  private static final int SECOND_BOTTOM_ROW = 18;
  private static final int THIRD_BOTTOM_ROW = 17;
  private static final int FOURTH_BOTTOM_ROW = 16;
  private static final int FIFTH_BOTTOM_ROW = 15;
  private static final int TEST_COLUMN = 5;
  private static final int TEST_COLUMN_2 = 3;
  private static final int LAST_COLUMN = 9;
  private static final int MIDDLE_ROW = 10;
  private static final int TOP_ROW = 0;
  private static final int OUT_OF_BOUNDS_ROW = 20;
  private static final int OUT_OF_BOUNDS_COLUMN = 10;
  private static final int NEGATIVE_POSITION = -1;
  private static final int SINGLE_GAP = 1;
  private static final int THREE_GAPS = 3;
  private static final int FOUR_LINES = 4;
  private static final int THREE_LINES = 3;
  private static final int ONE_LINE = 1;
  private Board board;
  private BoardTestHelper helper;

  @BeforeEach
  void setUp() {
    board = new Board();
    helper = new BoardTestHelper();
  }

  @Test
  @DisplayName("should clear single complete line")
  void shouldClearSingleCompleteLine() {
    // Arrange
    helper.fillRow(board, BOTTOM_ROW, Color.BLUE);

    // Act
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertEquals(ONE_LINE, clearedLines.size(), "Should clear exactly one line");
    assertEquals(BOTTOM_ROW, clearedLines.get(0), "Should clear row 19");

    // Verify the row is now empty
    for (int col = 0; col < board.getWidth(); col++) {
      assertNull(
          board.getCellColor(BOTTOM_ROW, col),
          String.format("Cell [%d][%d] should be empty", BOTTOM_ROW, col));
    }
  }

  @Test
  @DisplayName("should clear multiple consecutive lines")
  void shouldClearMultipleConsecutiveLines() {
    // Arrange - fill rows 19, 18, 17
    helper.fillRow(board, BOTTOM_ROW, Color.BLUE);
    helper.fillRow(board, SECOND_BOTTOM_ROW, Color.RED);
    helper.fillRow(board, THIRD_BOTTOM_ROW, Color.GREEN);

    // Place a marker piece above to verify shifting
    board.placeTetromino(new SingleCellTetromino(TEST_COLUMN, FOURTH_BOTTOM_ROW, Color.YELLOW));

    // Act
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertEquals(THREE_LINES, clearedLines.size(), "Should clear three lines");

    // All three bottom rows should be cleared
    for (int row = THIRD_BOTTOM_ROW; row < BOTTOM_ROW; row++) {
      for (int col = 0; col < board.getWidth(); col++) {
        assertNull(
            board.getCellColor(row, col), String.format("Row %d should be empty after clear", row));
      }
    }

    // The marker should have moved down 3 rows (from row 16 to row 19)
    assertNotNull(board.getCellColor(BOTTOM_ROW, TEST_COLUMN), "Marker should have shifted down");
    assertEquals(
        Color.YELLOW,
        board.getCellColor(BOTTOM_ROW, TEST_COLUMN),
        "Marker color should be preserved");
  }

  @Test
  @DisplayName("should clear multiple non-consecutive lines")
  void shouldClearMultipleNonConsecutiveLines() {
    // Arrange - fill rows 19, 17, 15 (with gaps)
    helper.fillRow(board, BOTTOM_ROW, Color.BLUE);
    helper.fillRow(board, THIRD_BOTTOM_ROW, Color.RED);
    helper.fillRow(board, FIFTH_BOTTOM_ROW, Color.GREEN);

    // Place markers in the gap rows to verify correct shifting
    board.placeTetromino(new SingleCellTetromino(TEST_COLUMN_2, SECOND_BOTTOM_ROW, Color.YELLOW));
    board.placeTetromino(new SingleCellTetromino(TEST_COLUMN_2, FOURTH_BOTTOM_ROW, Color.CYAN));

    // Act
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertEquals(THREE_LINES, clearedLines.size(), "Should clear three lines");

    // Check that the markers shifted correctly
    // After clearing 19, 17, 15, the marker from 18 should be at 19, from 16 should be at 18
    assertNotNull(
        board.getCellColor(BOTTOM_ROW, TEST_COLUMN_2), "First marker should be at row 19");
    assertEquals(
        Color.YELLOW,
        board.getCellColor(BOTTOM_ROW, TEST_COLUMN_2),
        "First marker color preserved");

    assertNotNull(
        board.getCellColor(SECOND_BOTTOM_ROW, TEST_COLUMN_2), "Second marker should be at row 18");
    assertEquals(
        Color.CYAN,
        board.getCellColor(SECOND_BOTTOM_ROW, TEST_COLUMN_2),
        "Second marker color preserved");
  }

  @Test
  @DisplayName("should clear four lines for Tetris")
  void shouldClearFourLinesForTetris() {
    // Arrange - fill bottom 4 rows
    helper.fillRow(board, BOTTOM_ROW, Color.BLUE);
    helper.fillRow(board, SECOND_BOTTOM_ROW, Color.RED);
    helper.fillRow(board, THIRD_BOTTOM_ROW, Color.GREEN);
    helper.fillRow(board, FOURTH_BOTTOM_ROW, Color.YELLOW);

    // Place a marker above to verify shifting
    board.placeTetromino(new SingleCellTetromino(TEST_COLUMN, FIFTH_BOTTOM_ROW, Color.MAGENTA));

    // Act
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertEquals(FOUR_LINES, clearedLines.size(), "Should clear exactly four lines (Tetris!)");

    // Bottom 4 rows should be empty
    for (int row = FOURTH_BOTTOM_ROW; row < BOTTOM_ROW; row++) {
      for (int col = 0; col < board.getWidth(); col++) {
        assertNull(
            board.getCellColor(row, col),
            String.format("Row %d should be empty after Tetris", row));
      }
    }

    // Marker should be at the bottom now
    assertNotNull(board.getCellColor(BOTTOM_ROW, TEST_COLUMN), "Marker should be at bottom");
    assertEquals(
        Color.MAGENTA, board.getCellColor(BOTTOM_ROW, TEST_COLUMN), "Marker color preserved");
  }

  @Test
  @DisplayName("should not clear partial lines")
  void shouldNotClearPartialLines() {
    // Arrange - fill row 19 but leave one gap
    helper.partialFillRow(board, BOTTOM_ROW, SINGLE_GAP); // Leave 1 cell empty

    // Act
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertTrue(clearedLines.isEmpty(), "Should not clear partial lines");

    // Verify cells are still there
    assertNotNull(board.getCellColor(BOTTOM_ROW, 0), "Cells should remain");
    assertNull(board.getCellColor(BOTTOM_ROW, LAST_COLUMN), "Gap should remain");
  }

  @Test
  @DisplayName("should return empty list when clearing empty board")
  void shouldReturnEmptyListWhenClearingEmptyBoard() {
    // Act - clear on empty board
    final List<Integer> clearedLines = board.clearCompletedLines();

    // Assert
    assertTrue(clearedLines.isEmpty(), "Empty board should have no lines to clear");
  }

  @Test
  @DisplayName("should detect game over when top row has pieces")
  void shouldDetectGameOverWhenTopRowHasPieces() {
    // Arrange
    final SingleCellTetromino testTetromino =
        new SingleCellTetromino(TEST_COLUMN, TOP_ROW, Color.RED);

    // Act - place piece at top
    board.placeTetromino(testTetromino);

    // Assert
    assertFalse(
        board.isValidPosition(testTetromino), "Should be game over when top row has pieces");
  }

  @Test
  @DisplayName("should clear entire board")
  void shouldClearEntireBoard() {
    // Arrange - add some pieces
    helper.fillRow(board, BOTTOM_ROW, Color.BLUE);
    helper.partialFillRow(board, SECOND_BOTTOM_ROW, THREE_GAPS);

    // Act
    board.clear();

    // Assert - all cells should be null
    for (int row = 0; row < board.getHeight(); row++) {
      for (int col = 0; col < board.getWidth(); col++) {
        assertNull(
            board.getCellColor(row, col),
            String.format("Cell [%d][%d] should be null after clear", row, col));
      }
    }
  }

  @Test
  @DisplayName("should return true for valid position")
  void shouldReturnTrueForValidPosition() {
    // Arrange
    final SingleCellTetromino piece = new SingleCellTetromino(TEST_COLUMN, MIDDLE_ROW, Color.BLUE);

    // Act & Assert
    assertTrue(board.isValidPosition(piece), "Valid position should return true");
  }

  @Test
  @DisplayName("should return false for position with collision")
  void shouldReturnFalseForPositionWithCollision() {
    // Arrange - place a piece first
    board.placeTetromino(new SingleCellTetromino(TEST_COLUMN, MIDDLE_ROW, Color.RED));

    // Try to place another piece at same position
    final SingleCellTetromino piece = new SingleCellTetromino(TEST_COLUMN, MIDDLE_ROW, Color.BLUE);

    // Act & Assert
    assertFalse(board.isValidPosition(piece), "Collision should return false");
  }

  @Test
  @DisplayName("should return false for out of bounds position")
  void shouldReturnFalseForOutOfBoundsPosition() {
    // Test left boundary
    SingleCellTetromino piece = new SingleCellTetromino(NEGATIVE_POSITION, MIDDLE_ROW, Color.BLUE);
    assertFalse(board.isValidPosition(piece), "Left out of bounds should return false");

    // Test right boundary
    piece = new SingleCellTetromino(OUT_OF_BOUNDS_COLUMN, MIDDLE_ROW, Color.BLUE);
    assertFalse(board.isValidPosition(piece), "Right out of bounds should return false");

    // Test bottom boundary
    piece = new SingleCellTetromino(TEST_COLUMN, OUT_OF_BOUNDS_ROW, Color.BLUE);
    assertFalse(board.isValidPosition(piece), "Bottom out of bounds should return false");
  }

  /** Test helper class for creating board scenarios. */
  private static final class BoardTestHelper {

    /**
     * Fills an entire row with the specified color.
     *
     * @param board The board to modify
     * @param row The row to fill
     * @param color The color to use
     */
    public void fillRow(final Board board, final int row, final Color color) {
      for (int col = 0; col < board.getWidth(); col++) {
        board.placeTetromino(new SingleCellTetromino(col, row, color));
      }
    }

    /**
     * Partially fills a row, leaving gaps at the end.
     *
     * @param board The board to modify
     * @param row The row to fill
     * @param gaps Number of cells to leave empty at the end
     */
    public void partialFillRow(final Board board, final int row, final int gaps) {
      for (int col = 0; col < board.getWidth() - gaps; col++) {
        board.placeTetromino(new SingleCellTetromino(col, row, Color.CYAN));
      }
    }
  }
}
