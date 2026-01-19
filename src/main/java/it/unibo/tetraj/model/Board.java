package it.unibo.tetraj.model;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Represents the Tetris game board. Standard Tetris board is 10 cells wide and 20 cells high. */
public final class Board {

  private static final int STANDARD_WIDTH = 10;
  private static final int STANDARD_HEIGHT = 20;
  private final int width;
  private final int height;
  private final Color[][] cells;

  /** Creates a new game board with standard dimensions. */
  public Board() {
    this(STANDARD_WIDTH, STANDARD_HEIGHT);
  }

  /**
   * Creates a new game board with specified dimensions.
   *
   * @param width The board width
   * @param height The board height
   */
  public Board(final int width, final int height) {
    this.width = width;
    this.height = height;
    cells = new Color[height][width];
  }

  /**
   * Checks if a tetromino can be placed at the specified position.
   *
   * @param tetromino The tetromino to check
   * @return true if the position is valid
   */
  public boolean isValidPosition(final AbstractTetromino<?> tetromino) {
    final int[][] shape = tetromino.getShape();
    final int pieceX = tetromino.getX();
    final int pieceY = tetromino.getY();

    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          final int boardX = pieceX + col;
          final int boardY = pieceY + row;

          // Check boundaries
          if (boardX < 0 || boardX >= width || boardY >= height) {
            return false;
          }

          // Check collision with placed pieces (not above board)
          if (boardY >= 0 && cells[boardY][boardX] != null) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Places a tetromino on the board.
   *
   * @param tetromino The tetromino to place
   */
  public void placeTetromino(final AbstractTetromino<?> tetromino) {
    final int[][] shape = tetromino.getShape();
    final int pieceX = tetromino.getX();
    final int pieceY = tetromino.getY();
    final Color color = tetromino.getColor();

    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          final int boardY = pieceY + row;
          final int boardX = pieceX + col;

          if (boardY >= 0 && boardY < height && boardX >= 0 && boardX < width) {
            cells[boardY][boardX] = color;
          }
        }
      }
    }
  }

  /**
   * Clears completed lines and returns the number of lines cleared.
   *
   * @return The list of row indices that were cleared
   */
  public List<Integer> clearCompletedLines() {
    final List<Integer> clearedLines = new ArrayList<>();
    int destRow = height - 1;

    for (int sourceRow = height - 1; sourceRow >= 0; sourceRow--) {
      if (isLineFull(sourceRow)) {
        clearedLines.add(sourceRow);
      } else {
        if (destRow != sourceRow) {
          cells[destRow] = cells[sourceRow].clone();
        }
        destRow--;
      }
    }
    // Clear remaining top rows
    while (destRow >= 0) {
      Arrays.fill(cells[destRow], null);
      destRow--;
    }
    return clearedLines;
  }

  /** Clears the entire board. */
  public void clear() {
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        cells[row][col] = null;
      }
    }
  }

  /**
   * Gets the color at a specific cell.
   *
   * @param row The row index
   * @param col The column index
   * @return The color at the cell, or null if empty
   */
  public Color getCellColor(final int row, final int col) {
    if (row >= 0 && row < height && col >= 0 && col < width) {
      return cells[row][col];
    }
    return null;
  }

  /**
   * Gets the board width.
   *
   * @return The width in cells
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the board height.
   *
   * @return The height in cells
   */
  public int getHeight() {
    return height;
  }

  /**
   * Checks if a line is full.
   *
   * @param row The row index to check
   * @return true if the line is complete, false otherwise
   */
  private boolean isLineFull(final int row) {
    for (int col = 0; col < width; col++) {
      if (cells[row][col] == null) {
        return false;
      }
    }
    return true;
  }
}
