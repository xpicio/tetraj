package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The L-shaped tetromino. Appears as orange color in standard Tetris. */
public final class LTetromino extends AbstractTetromino<LTetromino> {

  /** The 4 rotation states of the L piece. */
  private static final int[][][] SHAPES = {
    {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}},
    {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}},
    {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}},
    {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}},
  };

  private static final Color COLOR = Color.ORANGE;

  /**
   * Creates a new L-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public LTetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The L-tetromino to copy
   */
  private LTetromino(final LTetromino other) {
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
  public LTetromino copy() {
    return new LTetromino(this);
  }
}
