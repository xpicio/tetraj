package it.unibo.tetraj.model.piece;

import java.awt.Color;

/**
 * Test tetromino that represents a single 1x1 cell. Used for precise placement in tests. This class
 * provides a minimal tetromino implementation for testing board operations without the complexity
 * of real tetromino shapes.
 */
public final class SingleCellTetromino extends AbstractTetromino<SingleCellTetromino> {

  private static final int[][][] SHAPES = {
    {{1}}, {{1}}, {{1}}, {{1}},
  };
  private static final Color DEFAULT_COLOR = Color.GRAY;
  private final Color color;

  /**
   * Creates a 1x1 test tetromino with the default color (gray).
   *
   * @param x The x position
   * @param y The y position
   */
  public SingleCellTetromino(final int x, final int y) {
    this(x, y, DEFAULT_COLOR);
  }

  /**
   * Creates a 1x1 test tetromino with a specific color.
   *
   * @param x The x position
   * @param y The y position
   * @param color The color
   */
  public SingleCellTetromino(final int x, final int y, final Color color) {
    super(x, y);
    this.color = color;
  }

  /**
   * {@inheritDoc}
   *
   * @implNote Returns the internal array directly without defensive copying. While performance is
   *     not critical in tests, this mirrors the production implementation for consistency. The
   *     returned array is only accessed by the protected methods in AbstractTetromino and never
   *     exposed publicly, maintaining proper encapsulation.
   */
  @Override
  @SuppressWarnings("PMD.MethodReturnsInternalArray")
  protected int[][][] getShapes() {
    return SHAPES;
  }

  /** {@inheritDoc} */
  @Override
  public Color getColor() {
    return color;
  }

  /** {@inheritDoc} */
  @Override
  public SingleCellTetromino copy() {
    return new SingleCellTetromino(getX(), getY(), color);
  }
}
