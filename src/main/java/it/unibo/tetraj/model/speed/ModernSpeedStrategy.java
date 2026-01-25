package it.unibo.tetraj.model.speed;

/**
 * Modern Tetris speed progression strategy. Based on the official Tetris Guideline formula used in
 * contemporary versions like Tetris Effect, Tetris 99, and Puyo Puyo Tetris. Features smooth
 * exponential acceleration.
 */
public final class ModernSpeedStrategy implements SpeedStrategy {

  private static final double BASE_SPEED = 1000.0;
  private static final double BASE_GRAVITY = 0.8;
  private static final double GRAVITY_INCREMENT = 0.007;
  private static final double SOFT_DROP_DIVISOR = 20.0;
  private static final double MIN_SOFT_DROP_MS = 50.0;
  private static final double MIN_FALL_SPEED_MS = 1.0; // Cap at 1ms for level 30+

  /** Creates a modern speed strategy based on Tetris Guideline. */
  public ModernSpeedStrategy() {
    // All configuration is in static constants
  }

  /** {@inheritDoc} */
  @Override
  public double getFallSpeed(final int level) {
    if (level <= 0) {
      return BASE_SPEED;
    }

    // Official Tetris Guideline formula: (0.8 - ((level - 1) * 0.007))^(level - 1)
    final double gravity = Math.pow(BASE_GRAVITY - ((level - 1) * GRAVITY_INCREMENT), level - 1);
    final double milliseconds = gravity * BASE_SPEED;
    return Math.max(milliseconds, MIN_FALL_SPEED_MS);
  }

  /** {@inheritDoc} */
  @Override
  public double getSoftDropSpeed(final int level) {
    final double normalSpeed = getFallSpeed(level);

    // Soft drop is 20x faster than normal fall, but never slower than normal speed
    return Math.min(Math.max(MIN_SOFT_DROP_MS, normalSpeed / SOFT_DROP_DIVISOR), normalSpeed);
  }
}
