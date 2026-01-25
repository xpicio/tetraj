package it.unibo.tetraj.model.speed;

/**
 * Classic Nintendo NES speed progression strategy. Based on the original 1989 Tetris frames-per-row
 * timing at 60 FPS (NTSC). Features discrete speed steps rather than smooth progression.
 */
public final class ClassicSpeedStrategy implements SpeedStrategy {

  // Frames per row for levels 0 through 29 (and beyond)
  private static final int[] FRAMES_PER_ROW = {
    48, 43, 38, 33, 28, 23, 18, 13, 8, 6, // Levels 0-9
    5, 5, 5, 4, 4, 4, 3, 3, 3, 2, // Levels 10-19
    2, 2, 2, 2, 2, 2, 2, 2, 2, 1, // Levels 20-29 (Kill screen!)
  };
  private static final double MS_PER_FRAME = 1000.0 / 60.0;
  private static final double SOFT_DROP_MS = 33.33; // fixed at 2 frames per row

  /** Creates a classic speed strategy based on NES Tetris timing. */
  public ClassicSpeedStrategy() {
    // All configuration is in static constants
  }

  /** {@inheritDoc} */
  @Override
  public double getFallSpeed(final int level) {
    final int index = Math.min(Math.max(0, level), FRAMES_PER_ROW.length - 1);
    return FRAMES_PER_ROW[index] * MS_PER_FRAME;
  }

  /** {@inheritDoc} */
  @Override
  public double getSoftDropSpeed(final int level) {
    // Soft drop should never be slower than normal fall speed
    return Math.min(SOFT_DROP_MS, getFallSpeed(level));
  }
}
