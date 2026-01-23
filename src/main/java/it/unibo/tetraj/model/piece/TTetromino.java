package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The T-shaped tetromino. Appears as purple/magenta color in standard Tetris. */
public final class TTetromino extends AbstractTetromino<TTetromino> {

  /** The 4 rotation states of the T piece. */
  private static final int[][][] SHAPES = {
    {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
    {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
    {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
    {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}},
  };

  private static final Color COLOR = Color.MAGENTA;

  /**
   * Creates a new T-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public TTetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The T-tetromino to copy
   */
  private TTetromino(final TTetromino other) {
    super(other);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("PMD.MethodReturnsInternalArray")
  protected int[][][] getShapes() {
    return SHAPES;
  }

  /** {@inheritDoc} */
  @Override
  public Color getColor() {
    return COLOR;
  }

  /** {@inheritDoc} */
  @Override
  public TTetromino copy() {
    return new TTetromino(this);
  }
}
