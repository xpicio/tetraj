package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The Z-shaped tetromino. Appears as red color in standard Tetris. */
public final class ZTetromino extends AbstractTetromino<ZTetromino> {

  /** The 4 rotation states of the Z piece. */
  private static final int[][][] SHAPES = {
    {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}},
    {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
    {{0, 0, 0}, {1, 1, 0}, {0, 1, 1}},
    {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}},
  };

  /** The standard color for Z piece. */
  private static final Color COLOR = Color.RED;

  /**
   * Creates a new Z-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public ZTetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The Z-tetromino to copy
   */
  private ZTetromino(final ZTetromino other) {
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
  public ZTetromino copy() {
    return new ZTetromino(this);
  }
}
