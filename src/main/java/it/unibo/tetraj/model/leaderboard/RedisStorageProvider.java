package it.unibo.tetraj.model.leaderboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis-based implementation of leaderboard storage. Persists leaderboard entries to a Redis server
 * using a single key with JSON serialization. Maintains a maximum of {@value
 * StorageProvider#MAX_ENTRIES} entries, automatically sorted by score (descending) and timestamp.
 */
public final class RedisStorageProvider implements StorageProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisStorageProvider.class);
  private static final String LEADERBOARD_KEY = "tetraj:leaderboard";
  // Connection establishment timeout
  private static final int CONNECTION_TIMEOUT = 1500;
  // Read/write operations timeout
  private static final int SOCKET_TIMEOUT = 500;
  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
  private static final TypeReference<List<LeaderboardEntry>> ENTRY_LIST_TYPE =
      new TypeReference<>() {
        // Empty body with comment to avoid Spotless/Checkstyle conflict
      };
  private final String connectionString;
  private final String hostname;
  private final JedisPooled jedis;
  private boolean isAvailable;

  /**
   * Creates a Redis provider with connection parameters.
   *
   * @param ssl Whether to use TLS/SSL (true for rediss://, false for redis://)
   * @param hostname Redis server hostname
   * @param port Redis server port
   */
  public RedisStorageProvider(final boolean ssl, final String hostname, final int port) {
    this(ssl, hostname, port, Optional.empty(), Optional.empty());
  }

  /**
   * Creates a Redis provider with connection parameters.
   *
   * @param ssl Whether to use TLS/SSL (true for rediss://, false for redis://)
   * @param hostname Redis server hostname
   * @param port Redis server port
   * @param username Optional username for authentication (empty if not required)
   * @param password Optional password for authentication (empty if not required)
   */
  public RedisStorageProvider(
      final boolean ssl,
      final String hostname,
      final int port,
      final Optional<String> username,
      final Optional<String> password) {
    final String scheme = ssl ? "rediss" : "redis";

    isAvailable = false;
    this.hostname = hostname;
    connectionString =
        String.format("%s://%s@%s:%d", scheme, username.orElse("default"), hostname, port);

    final var configBuilder =
        DefaultJedisClientConfig.builder()
            .ssl(ssl)
            .connectionTimeoutMillis(CONNECTION_TIMEOUT)
            .socketTimeoutMillis(SOCKET_TIMEOUT);
    if (username.isPresent()) {
      configBuilder.user(username.get());
    }
    if (password.isPresent()) {
      configBuilder.password(password.get());
    }
    jedis = new JedisPooled(new HostAndPort(hostname, port), configBuilder.build());
  }

  /**
   * {@inheritDoc} Initializes the Redis connection. First validates DNS resolution, then attempts
   * to ping the server to verify connectivity. Does not throw exceptions; connection failures are
   * logged and {@link #isAvailable()} will return false.
   */
  @Override
  public void initialize() {
    try {
      // Fast DNS check - fails immediately if hostname is invalid
      InetAddress.getByName(hostname);
    } catch (final UnknownHostException e) {
      LOGGER.error("DNS resolution failed for {}: {}", getName(), e.getMessage());
      isAvailable = false;
      return;
    }

    try {
      final String response = jedis.ping();

      if ("PONG".equals(response)) {
        isAvailable = true;
        LOGGER.info("Successfully connected to {}", getName());
      } else {
        LOGGER.error("Unexpected ping response from {}: {}", getName(), response);
        isAvailable = false;
      }
    } catch (final JedisException e) {
      LOGGER.error("Failed to connect to {}: {}", getName(), e.getMessage());
      isAvailable = false;
    }
  }

  /**
   * {@inheritDoc} Returns a descriptive name including the connection string (without password).
   */
  @Override
  public String getName() {
    return String.format("Redis (%s)", connectionString.replaceAll(":[^:@]*@", ":***@"));
  }

  /**
   * {@inheritDoc} Adds the entry to the leaderboard, maintains sorting by score, and limits to
   * {@value StorageProvider#MAX_ENTRIES} entries. Reloads current entries from Redis to handle
   * concurrent updates from other game instances.
   */
  @Override
  public boolean save(final LeaderboardEntry entry) {
    if (!isAvailable) {
      LOGGER.warn("Cannot save entry: {} connection not available", getName());
      return false;
    }

    try {
      // Entries may have been updated by another game instance
      final List<LeaderboardEntry> currentEntries = loadEntriesFromRedis();
      // Add new entry, sort, and keep only top MAX_ENTRIES
      final List<LeaderboardEntry> updatedEntries =
          Stream.concat(currentEntries.stream(), Stream.of(entry))
              .sorted() // Uses LeaderboardEntry's natural ordering
              .limit(MAX_ENTRIES)
              .toList();
      // Save back to Redis
      final String json = MAPPER.writeValueAsString(updatedEntries);

      jedis.set(LEADERBOARD_KEY, json);
      LOGGER.debug("Saved entry for {} with score {}", entry.nickname(), entry.score());
      return true;
    } catch (final JsonProcessingException e) {
      LOGGER.error("Failed to serialize entry to {}: {}", getName(), e.getMessage());
      return false;
    } catch (final JedisException e) {
      LOGGER.error("Failed to save entry to {}: {}", getName(), e.getMessage());
      // Mark as unavailable if we get connection errors
      isAvailable = false;
      return false;
    }
  }

  /**
   * {@inheritDoc} Returns a defensive copy of the current top entries (max {@value
   * StorageProvider#MAX_ENTRIES}).
   */
  @Override
  public List<LeaderboardEntry> getTop() {
    if (!isAvailable) {
      LOGGER.warn("Cannot retrieve entries: {} connection not available", getName());
      return Collections.emptyList();
    }

    try {
      return new ArrayList<>(loadEntriesFromRedis());
    } catch (final JedisException e) {
      LOGGER.error("Failed to load entries from {}: {}", getName(), e.getMessage());
      isAvailable = false;
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc} Returns true if the Redis connection was successfully established during
   * initialization.
   */
  @Override
  public boolean isAvailable() {
    return isAvailable;
  }

  /**
   * Loads entries from Redis. Returns empty list if key doesn't exist or on error.
   *
   * @return List of leaderboard entries, sorted and limited to MAX_ENTRIES
   */
  private List<LeaderboardEntry> loadEntriesFromRedis() {
    try {
      // Load and validate entries
      final String json = jedis.get(LEADERBOARD_KEY);

      if (json == null || json.isEmpty()) {
        return new ArrayList<>();
      }

      final List<LeaderboardEntry> entries = MAPPER.readValue(json, ENTRY_LIST_TYPE);

      // Ensure sorted and limited
      return entries.stream().sorted().limit(MAX_ENTRIES).toList();
    } catch (final JsonProcessingException e) {
      LOGGER.error("Failed to deserialize leaderboard from {}: {}", getName(), e.getMessage());
      return new ArrayList<>();
    } catch (final JedisException e) {
      LOGGER.error("Failed to load from {}: {}", getName(), e.getMessage());
      throw e;
    }
  }
}
