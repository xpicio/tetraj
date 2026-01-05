package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The I-shaped tetromino (straight line). Appears as cyan color in standard Tetris. */
public final class ITetromino extends AbstractTetromino<ITetromino> {

  /** The 4 rotation states of the I piece. */
  private static final int[][][] SHAPES = {
    {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
    {{0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}},
    {{0, 0, 0, 0}, {0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}},
    {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}},
  };

  /** The standard color for I piece. */
  private static final Color COLOR = Color.CYAN;

  /**
   * Creates a new I-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public ITetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The I-tetromino to copy
   */
  private ITetromino(final ITetromino other) {
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
  public ITetromino copy() {
    return new ITetromino(this);
  }
}
