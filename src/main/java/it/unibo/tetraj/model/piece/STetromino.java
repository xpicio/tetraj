package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The S-shaped tetromino. Appears as green color in standard Tetris. */
public final class STetromino extends AbstractTetromino<STetromino> {

  /** The 4 rotation states of the S piece. */
  private static final int[][][] SHAPES = {
    {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}},
    {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}},
    {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}},
    {{1, 0, 0}, {1, 1, 0}, {0, 1, 0}},
  };

  /** The standard color for S piece. */
  private static final Color COLOR = Color.GREEN;

  /**
   * Creates a new S-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public STetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The S-tetromino to copy
   */
  private STetromino(final STetromino other) {
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
  public STetromino copy() {
    return new STetromino(this);
  }
}
