package it.unibo.tetraj.model.piece;

import java.awt.Color;

/**
 * Abstract base class for all tetromino pieces. Uses generics to ensure type-safe copy operations.
 *
 * @param <T> The concrete tetromino type
 */
public abstract class AbstractTetromino<T extends AbstractTetromino<T>> implements Tetromino {

  private int x;
  private int y;

  /** Current rotation state (0-3). */
  private int rotation;

  /**
   * Creates a new tetromino at the specified position.
   *
   * @param x Initial X position
   * @param y Initial Y position
   */
  protected AbstractTetromino(final int x, final int y) {
    this.x = x;
    this.y = y;
    this.rotation = 0;
  }

  /**
   * Copy constructor for creating a clone of an existing tetromino.
   *
   * @param other The tetromino to copy
   */
  protected AbstractTetromino(final T other) {
    this.x = other.getX();
    this.y = other.getY();
    this.rotation = other.getRotation();
  }

  /** {@inheritDoc} */
  @Override
  public void move(final int dx, final int dy) {
    this.x += dx;
    this.y += dy;
  }

  /** {@inheritDoc} */
  @Override
  public void rotateClockwise() {
    rotation = (rotation + 1) % 4;
  }

  /** {@inheritDoc} */
  @Override
  public void rotateCounterClockwise() {
    rotation = (rotation + 3) % 4;
  }

  /** {@inheritDoc} */
  @Override
  public int[][] getShape() {
    return getShapes()[rotation];
  }

  /** {@inheritDoc} */
  @Override
  public int getWidth() {
    final int[][] shape = getShape();
    return shape[0].length;
  }

  /** {@inheritDoc} */
  @Override
  public int getHeight() {
    return getShape().length;
  }

  /** {@inheritDoc} */
  @Override
  public int getX() {
    return x;
  }

  /** {@inheritDoc} */
  @Override
  public int getY() {
    return y;
  }

  /** {@inheritDoc} */
  @Override
  public void setPosition(final int newX, final int newY) {
    this.x = newX;
    this.y = newY;
  }

  /**
   * Gets the current rotation.
   *
   * @return The rotation index (0-3)
   */
  protected int getRotation() {
    return rotation;
  }

  /**
   * Gets all rotation shapes for this tetromino.
   *
   * @return A 3D array containing all 4 rotation states
   */
  protected abstract int[][][] getShapes();

  /**
   * Gets the color of this tetromino.
   *
   * @return The color
   */
  @Override
  public abstract Color getColor();

  /**
   * Creates a copy of this tetromino.
   *
   * @return A new instance with the same state
   */
  @Override
  public abstract T copy();
}
