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

  /** The standard color for J piece. */
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

  /**
   * {@inheritDoc}
   *
   * @implNote Performance-critical: returns direct array reference (no clone). This is safe because
   *     AbstractTetromino is the sole consumer and only performs read operations through
   *     getShape(). The reference never leaks to public API, maintaining complete encapsulation
   *     despite returning a mutable array.
   */
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
