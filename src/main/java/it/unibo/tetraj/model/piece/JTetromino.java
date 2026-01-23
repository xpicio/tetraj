package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The J-shaped tetromino. Appears as blue color in standard Tetris. */
public final class JTetromino extends AbstractTetromino<JTetromino> {

  /** The 4 rotation states of the J piece. */
  private static final int[][][] SHAPES = {
    {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}},
    {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}},
    {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}},
    {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}},
  };

  private static final Color COLOR = Color.BLUE;

  /**
   * Creates a new J-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public JTetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The J-tetromino to copy
   */
  private JTetromino(final JTetromino other) {
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
  public JTetromino copy() {
    return new JTetromino(this);
  }
}
