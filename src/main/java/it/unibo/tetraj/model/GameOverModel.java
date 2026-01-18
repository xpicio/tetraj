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
  private final boolean isScoreSaved;

  /**
   * Creates a new game over model from the game session.
   *
   * @param gameSession The game session containing final game data
   * @param isScoreSaved Whether the score was successfully saved to the leaderboard
   */
  public GameOverModel(final GameSession gameSession, final boolean isScoreSaved) {
    this.gameSession = gameSession;
    this.isScoreSaved = isScoreSaved;
    lastFrame = this.gameSession.getLastFrame();
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
    final Instant startTime =
        gameSession.getGameStartTime() != null ? gameSession.getGameStartTime() : Instant.now();
    final Instant endTime =
        gameSession.getGameEndTime() != null ? gameSession.getGameEndTime() : Instant.now();
    final Duration duration = Duration.between(startTime, endTime);
    final String formattedDuration = FormatUtils.formatDuration(duration);
    final String formattedScore = FormatUtils.formatScore(gameSession.getScore());
    final String greatRun =
        String.format("Great run, %s!", gameSession.getPlayerProfile().nickname());
    final String defaultRun =
        String.format("Game over, %s.", gameSession.getPlayerProfile().nickname());

    return List.of(
        String.format(
            "%s You survived for %s,", isScoreSaved ? greatRun : defaultRun, formattedDuration),
        String.format(
            "clearing %d lines to reach level %d",
            gameSession.getLinesCleared(), gameSession.getLevel()),
        String.format("and earning a total of %s points.", formattedScore));
  }
}
