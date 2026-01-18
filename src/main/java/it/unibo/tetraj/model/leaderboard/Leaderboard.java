package it.unibo.tetraj.model.leaderboard;

import it.unibo.tetraj.util.ApplicationProperties;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Facade for leaderboard operations. Manages provider selection, fallback chain, and provides
 * unified API. Singleton pattern ensures single provider selection for entire app lifecycle.
 */
public final class Leaderboard {

  private static final Logger LOGGER = LoggerFactory.getLogger(Leaderboard.class);
  private static final Leaderboard INSTANCE = new Leaderboard();
  private static final int REDIS_DEFAULT_PORT = 6379;

  private final List<StorageProvider> providers;
  private StorageProvider activeProvider;

  Leaderboard(final List<StorageProvider> providers) {
    this.providers = providers;
    selectActiveProvider();
  }

  private Leaderboard() {
    this(createDefaultProviders());
  }

  /**
   * Gets the singleton instance.
   *
   * @return The Leaderboard instance
   */
  public static Leaderboard getInstance() {
    return INSTANCE;
  }

  /**
   * Checks if a score qualifies for the leaderboard. A score qualifies if there are less than
   * MAX_ENTRIES or it beats the lowest score.
   *
   * @param score The score to check
   * @return true if the score would enter the leaderboard
   */
  public boolean isQualifyingScore(final int score) {
    if (activeProvider == null) {
      return false;
    }

    final List<LeaderboardEntry> entries = activeProvider.getTop();

    // Qualifies if: not full OR beats worst score
    return entries.size() < StorageProvider.MAX_ENTRIES
        || score > entries.get(entries.size() - 1).score();
  }

  /**
   * Saves a score to the leaderboard.
   *
   * @param playerId The unique identifier of the player
   * @param playerNickname The display name of the player
   * @param score The score achieved
   * @param level The level reached
   * @param lines The number of lines cleared
   * @param duration The game duration
   * @return true if successfully saved
   */
  public boolean save(
      final String playerId,
      final String playerNickname,
      final int score,
      final int level,
      final int lines,
      final Duration duration) {
    if (activeProvider == null) {
      LOGGER.error("No provider available to save score");
      return false;
    }

    final LeaderboardEntry entry =
        new LeaderboardEntry(
            playerId, playerNickname, score, Instant.now(), level, lines, duration);

    final boolean saved = activeProvider.save(entry);

    if (saved) {
      LOGGER.info("Score {} for {} saved by {}", score, playerNickname, getActiveProviderName());
    } else {
      LOGGER.error("Failed to save score with {}", getActiveProviderName());
    }
    return saved;
  }

  /**
   * Gets the current top entries.
   *
   * @return List of top entries, or empty if no provider available
   */
  public List<LeaderboardEntry> getTopEntries() {
    if (activeProvider == null) {
      return Collections.emptyList();
    }

    return activeProvider.getTop();
  }

  /**
   * Gets the name of the active provider.
   *
   * @return Provider name or "None" if no provider available
   */
  public String getActiveProviderName() {
    return activeProvider != null ? activeProvider.getName() : "None";
  }

  private static List<StorageProvider> createDefaultProviders() {
    // Select first available provider (Upstash Redis, local Redis, or JSON fallback)
    final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    return List.of(
        new RedisStorageProvider(
            true,
            applicationProperties.getProperty("storageProvider.redis.upstash.hostname"),
            Integer.parseInt(
                applicationProperties.getProperty("storageProvider.redis.upstash.port")),
            Optional.of(
                applicationProperties.getProperty("storageProvider.redis.upstash.username")),
            Optional.of(
                applicationProperties.getProperty("storageProvider.redis.upstash.password"))),
        new RedisStorageProvider(false, "localhost", REDIS_DEFAULT_PORT),
        new JsonFileStorageProvider());
  }

  /** Selects the first available provider from the chain. Called once at initialization. */
  private void selectActiveProvider() {
    activeProvider =
        providers.stream()
            .peek(StorageProvider::initialize)
            .filter(
                provider -> {
                  final boolean available = provider.isAvailable();
                  if (available) {
                    LOGGER.info("Leaderboard initialized with: {}", provider.getName());
                  } else {
                    LOGGER.warn("{} not available", provider.getName());
                  }
                  return available;
                })
            .findFirst()
            .orElse(null);
    if (activeProvider == null) {
      LOGGER.error("No leaderboard provider available!");
    }
  }
}
