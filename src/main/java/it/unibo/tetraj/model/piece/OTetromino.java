package it.unibo.tetraj.model.piece;

import java.awt.Color;

/** The O-shaped tetromino (square). Appears as yellow color in standard Tetris. */
public final class OTetromino extends AbstractTetromino<OTetromino> {

  /** The 4 rotation states of the O piece (all identical). */
  private static final int[][][] SHAPES = {
    {{1, 1}, {1, 1}},
    {{1, 1}, {1, 1}},
    {{1, 1}, {1, 1}},
    {{1, 1}, {1, 1}},
  };

  /** The standard color for O piece. */
  private static final Color COLOR = Color.YELLOW;

  /**
   * Creates a new O-tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  public OTetromino(final int x, final int y) {
    super(x, y);
  }

  /**
   * Copy constructor.
   *
   * @param other The O-tetromino to copy
   */
  private OTetromino(final OTetromino other) {
    super(other);
  }

  /**
   * {@inheritDoc}
   *
   * @implNote Returns internal array without defensive copy for performance reason. These arrays
   *     are immutable game data shared across all instances.
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
  public OTetromino copy() {
    return new OTetromino(this);
  }
}
