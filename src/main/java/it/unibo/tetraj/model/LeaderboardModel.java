package it.unibo.tetraj.model;

import it.unibo.tetraj.model.leaderboard.LeaderboardEntry;
import it.unibo.tetraj.model.leaderboard.PlayerProfileManager;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Model for the leaderboard view. Contains game session and leaderboard entries formatted for
 * display.
 */
public final class LeaderboardModel {

  private final List<LeaderboardEntry> leaderboardEntries;

  /**
   * Creates a new leaderboard model.
   *
   * @param leaderboardEntries The list of leaderboard entries from storage
   */
  public LeaderboardModel(final List<LeaderboardEntry> leaderboardEntries) {
    this.leaderboardEntries = List.copyOf(leaderboardEntries);
  }

  /**
   * Gets the current player profile ID for highlighting own records.
   *
   * @return The current player's ID
   */
  public String getCurrentPlayerProfileId() {
    return PlayerProfileManager.getInstance().getProfile().id();
  }

  /**
   * Gets the leaderboard entries formatted for display.
   *
   * @return List of display entries ready for rendering
   */
  public List<LeaderboardDisplayEntry> getEntries() {
    return LeaderboardDisplayEntry.fromEntries(leaderboardEntries);
  }

  /**
   * Display entry for leaderboard UI. Contains formatted data ready for rendering.
   *
   * @param rank The position in the leaderboard (1-based)
   * @param playerId The unique player identifier for highlighting own records
   * @param nickname The player's nickname
   * @param score The formatted score achieved
   * @param level The level reached
   * @param lines The number of lines cleared
   * @param date The formatted date string
   * @param duration The formatted duration string
   */
  public record LeaderboardDisplayEntry(
      String rank,
      String playerId,
      String nickname,
      String score,
      String level,
      String lines,
      String date,
      String duration) {

    /**
     * Transforms a list of domain LeaderboardEntry objects into display entries.
     *
     * @param entries The domain entries from storage
     * @return List of display entries ready for UI rendering
     */
    public static List<LeaderboardDisplayEntry> fromEntries(final List<LeaderboardEntry> entries) {
      return IntStream.range(0, entries.size())
          .mapToObj(
              i -> {
                final LeaderboardEntry entry = entries.get(i);
                return new LeaderboardDisplayEntry(
                    String.valueOf(i + 1), // rank (1-based)
                    entry.id(),
                    entry.nickname(),
                    FormatUtils.formatScore(entry.score()),
                    String.valueOf(entry.level()),
                    String.valueOf(entry.lines()),
                    FormatUtils.formatDate(entry.timestamp()),
                    FormatUtils.formatDuration(entry.duration()));
              })
          .toList();
    }
  }
}
