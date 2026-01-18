package it.unibo.tetraj.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.GameSession;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

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
    lastFrame = this.gameSession.lastFrame();
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
      final Duration duration =
          Duration.between(
              gameSession.getGameStartTime().orElse(Instant.now()),
              gameSession.getGameEndTime().orElse(Instant.now()));
      return new GameOverStats(
          gameSession.getScore().orElse(0),
          gameSession.getLevel().orElse(0),
          gameSession.getLinesCleared().orElse(0),
          duration);
    }

    /**
     * Formats the game statistics into human-readable text lines.
     *
     * @return List of formatted strings describing the game performance
     */
    public List<String> getFormattedLines() {
      // Format duration (MM:ss)
      final long minutes = duration.toMinutes();
      final int seconds = duration.toSecondsPart();
      final String formattedDuration = String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
      // Format score with thousands separator
      final String formattedScore = String.format(Locale.ROOT, "%,d", score);
      // Construct the list of lines
      return List.of(
          String.format(Locale.ROOT, "You survived for %s,", formattedDuration),
          String.format(Locale.ROOT, "clearing %d lines to reach level %d", lines, level),
          String.format(Locale.ROOT, "and earning a total of %s points.", formattedScore));
    }
  }
}
