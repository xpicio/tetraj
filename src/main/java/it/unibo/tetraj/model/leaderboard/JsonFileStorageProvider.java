package it.unibo.tetraj.model.leaderboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * JSON file-based implementation of leaderboard storage. Persists leaderboard entries to a JSON
 * file in the user's home directory. Maintains a maximum of {@value StorageProvider#MAX_ENTRIES}
 * entries, automatically sorted by score (descending) and timestamp.
 */
public final class JsonFileStorageProvider implements StorageProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileStorageProvider.class);
  private static final String LEADERBOARD_FILENAME = "tetrajLeaderboard.json";
  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
  private static final TypeReference<List<LeaderboardEntry>> ENTRY_LIST_TYPE =
      new TypeReference<>() {
        // Empty body with comment to avoid Spotless/Checkstyle conflict
      };
  private final Path filePath;
  private List<LeaderboardEntry> entries;

  /**
   * Creates a provider using the default file location in user's home directory. The file will be
   * named "tetrajLeaderboard.json".
   */
  public JsonFileStorageProvider() {
    this(Paths.get(System.getProperty("user.home"), LEADERBOARD_FILENAME));
  }

  /**
   * Creates a provider using a specific file path.
   *
   * @param filePath The path where the leaderboard JSON file should be stored
   */
  public JsonFileStorageProvider(final Path filePath) {
    this.filePath = filePath;
    this.entries = new ArrayList<>();
  }

  /**
   * {@inheritDoc} Loads or creates the leaderboard file. Verifies read/write permissions on the
   * file and parent directory.
   */
  @Override
  public void initialize() {
    loadOrCreateLeaderboard();
  }

  /** {@inheritDoc} Returns a descriptive name including the full file path. */
  @Override
  public String getName() {
    return String.format("JSON (%s)", filePath);
  }

  /**
   * {@inheritDoc} Adds the entry to the leaderboard, maintains sorting by score, and limits to
   * {@value StorageProvider#MAX_ENTRIES} entries.
   */
  @Override
  public boolean save(final LeaderboardEntry entry) {
    try {
      // Add new entry, sort, and keep only top MAX_ENTRIES
      entries =
          Stream.concat(entries.stream(), Stream.of(entry))
              .sorted() // Uses LeaderboardEntry's natural ordering
              .limit(MAX_ENTRIES)
              .toList();
      // Persist to file
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), entries);
      LOGGER.debug("Saved entry for {} with score {}", entry.nickname(), entry.score());
      return true;
    } catch (final JsonProcessingException e) {
      LOGGER.error("Failed to serialize entry to {}: {}", getName(), e.getMessage());
      return false;
    } catch (final IOException e) {
      LOGGER.error("Failed to save entry to {}: {}", getName(), e.getMessage());
      return false;
    }
  }

  /**
   * {@inheritDoc} Returns a defensive copy of the current top entries (max {@value
   * StorageProvider#MAX_ENTRIES}).
   */
  @Override
  public List<LeaderboardEntry> getTop() {
    return new ArrayList<>(entries);
  }

  /** {@inheritDoc} File system storage is always considered available. */
  @Override
  public boolean isAvailable() {
    return true; // File system is always available
  }

  /**
   * Loads existing leaderboard from file or creates a new empty one. If the file exists but cannot
   * be read, starts with an empty leaderboard. Ensures loaded entries are sorted and limited to
   * MAX_ENTRIES.
   */
  private void loadOrCreateLeaderboard() {
    if (Files.exists(filePath)) {
      try {
        // Load and validate entries
        final List<LeaderboardEntry> loadedEntries =
            MAPPER.readValue(filePath.toFile(), ENTRY_LIST_TYPE);
        // Ensure sorted and limited
        entries = loadedEntries.stream().sorted().limit(MAX_ENTRIES).toList();
        LOGGER.info("Loaded {} entries from {}", entries.size(), getName());
        return;
      } catch (final JsonProcessingException e) {
        LOGGER.error("Failed to deserialize leaderboard from {}: {}", getName(), e.getMessage());
      } catch (final IOException e) {
        LOGGER.error("Failed to load leaderboard from {}: {}", getName(), e.getMessage());
        LOGGER.info("Starting with empty leaderboard");
      }
    }
    // Initialize empty leaderboard
    entries = new ArrayList<>();
    try {
      MAPPER
          .writerWithDefaultPrettyPrinter()
          .writeValue(filePath.toFile(), Collections.emptyList());
      LOGGER.info("Created new leaderboard file: {}", getName());
    } catch (final IOException e) {
      LOGGER.warn("Could not create initial file {}: {}", getName(), e.getMessage());
    }
  }
}
