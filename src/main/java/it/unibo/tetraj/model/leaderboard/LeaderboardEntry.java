package it.unibo.tetraj.model.leaderboard;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a single leaderboard entry with player information and game statistics.
 *
 * @param id Player unique identifier
 * @param nickname Player display name
 * @param score Final score achieved
 * @param timestamp When the game was played
 * @param level Final level reached
 * @param lines Total lines cleared
 * @param duration Total game duration
 */
public record LeaderboardEntry(
    String id,
    String nickname,
    long score,
    Instant timestamp,
    int level,
    int lines,
    Duration duration)
    implements Comparable<LeaderboardEntry> {

  /**
   * Compares entries by score (descending) then by timestamp (ascending). Higher scores come first.
   * For equal scores, older entries come first.
   *
   * @param other The other entry to compare to
   * @return Negative if this should come before other, positive if after, 0 if equal
   */
  @Override
  public int compareTo(final LeaderboardEntry other) {
    // First compare by score (descending, higher is better)
    final int scoreCompare = Long.compare(other.score, this.score);

    if (scoreCompare != 0) {
      return scoreCompare;
    }
    // For equal scores, compare by timestamp (ascending, older first)
    return this.timestamp.compareTo(other.timestamp);
  }
}
