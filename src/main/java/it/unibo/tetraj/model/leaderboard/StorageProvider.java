package it.unibo.tetraj.model.leaderboard;

import java.util.List;

/**
 * Provider interface for leaderboard persistence. Implementations handle storage and retrieval of
 * leaderboard entries.
 */
public interface StorageProvider {

  /** Maximum number of entries to store. */
  int MAX_ENTRIES = 10;

  /**
   * Initializes the storage provider. Verifies access permissions and prepares the storage for
   * read/write operations. Implementations should not throw exceptions but instead mark themselves
   * as unavailable via {@link #isAvailable()} if initialization fails.
   */
  void initialize();

  /**
   * Gets the provider name for logging and debugging. Should include relevant connection details.
   *
   * @return Descriptive name like "Redis (redis://localhost:6379)" or "JSON
   *     (/home/userName/tetrajLeaderboard.json)"
   */
  String getName();

  /**
   * Saves a leaderboard entry.
   *
   * @param entry The entry to save
   * @return true if saved successfully
   */
  boolean save(LeaderboardEntry entry);

  /**
   * Gets top N entries.
   *
   * @return Top entries sorted by score (max {@value MAX_ENTRIES} entries)
   */
  List<LeaderboardEntry> getTop();

  /**
   * Checks if provider is available/connected.
   *
   * @return true if provider is ready
   */
  boolean isAvailable();
}
