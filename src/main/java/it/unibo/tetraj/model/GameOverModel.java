package it.unibo.tetraj.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.GameSession;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/** Model for the game over state. Contains final game statistics and background image. */
public final class GameOverModel {

  private final GameSession gameSession;
  private final BufferedImage lastFrame;
  private final GameOverStats gameOverStats;

  /**
   * Creates a new game over model from the game session.
   *
   * @param gameSession The game session containing final game data
   */
  public GameOverModel(final GameSession gameSession) {
    this.gameSession = gameSession;
    lastFrame = this.gameSession.getLastFrame();
    gameOverStats = GameOverStats.fromSession(this.gameSession);
  }

  /**
   * Gets the game session containing the final game data.
   *
   * @return The game session with score, level, and other game statistics
   */
  public GameSession getGameSession() {
    return gameSession;
  }

  /**
   * Gets the background image (last frame from gameplay).
   *
   * @return The background image to display behind game over screen
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Performance optimization. GameOverView only reads the image for rendering")
  public BufferedImage getBackgroundImage() {
    return lastFrame;
  }

  /**
   * Gets formatted game over statistics as a list of strings.
   *
   * @return List of formatted text lines with game statistics
   */
  public List<String> getGameOverStats() {
    return gameOverStats.getFormattedLines();
  }

  /**
   * Record containing game over statistics with formatting capabilities.
   *
   * @param score The final score achieved
   * @param level The final level reached
   * @param lines Total number of lines cleared
   * @param duration Time elapsed during the game
   */
  public record GameOverStats(long score, int level, int lines, Duration duration) {

    /**
     * Creates GameOverStats from a game session.
     *
     * @param gameSession The session to extract stats from
     * @return GameOverStats with extracted values
     */
    public static GameOverStats fromSession(final GameSession gameSession) {
      final Instant startTime =
          gameSession.getGameStartTime() != null ? gameSession.getGameStartTime() : Instant.now();
      final Instant endTime =
          gameSession.getGameEndTime() != null ? gameSession.getGameEndTime() : Instant.now();
      final Duration duration = Duration.between(startTime, endTime);

      return new GameOverStats(
          gameSession.getScore(), gameSession.getLevel(), gameSession.getLinesCleared(), duration);
    }

    /**
     * Formats the game statistics into human-readable text lines.
     *
     * @return List of formatted strings describing the game performance
     */
    public List<String> getFormattedLines() {
      final String formattedDuration = FormatUtils.formatDuration(duration);
      final String formattedScore = FormatUtils.formatScore(score);

      return List.of(
          String.format("You survived for %s,", formattedDuration),
          String.format("clearing %d lines to reach level %d", lines, level),
          String.format("and earning a total of %s points.", formattedScore));
    }
  }
}
