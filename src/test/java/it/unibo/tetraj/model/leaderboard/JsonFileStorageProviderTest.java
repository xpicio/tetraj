package it.unibo.tetraj.model.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for JsonFileStorageProvider. */
class JsonFileStorageProviderTest {

  private static final String LEADERBOARD_ENTRY_P1_ID = "player1";
  private static final String LEADERBOARD_ENTRY_P1_NICKNAME = "Alice";
  private static final long LEADERBOARD_ENTRY_P1_SCORE = 1000;
  private static final int LEADERBOARD_ENTRY_P1_LEVEL = 5;
  private static final int LEADERBOARD_ENTRY_P1_LINES = 20;
  private static final int LEADERBOARD_ENTRY_P1_DURATION = 10;
  private static final String LEADERBOARD_ENTRY_P2_ID = "player2";
  private static final String LEADERBOARD_ENTRY_P2_NICKNAME = "Bob";
  private static final long LEADERBOARD_ENTRY_P2_SCORE = 2000;
  private static final int LEADERBOARD_ENTRY_P2_LEVEL = 8;
  private static final int LEADERBOARD_ENTRY_P2_LINES = 40;
  private static final int LEADERBOARD_ENTRY_P2_DURATION = 15;
  private static final String LEADERBOARD_ENTRY_P3_ID = "player3";
  private static final String LEADERBOARD_ENTRY_P3_NICKNAME = "Charlie";
  private static final long LEADERBOARD_ENTRY_P3_SCORE = 1500;
  private static final int LEADERBOARD_ENTRY_P3_LEVEL = 6;
  private static final int LEADERBOARD_ENTRY_P3_LINES = 30;
  private static final int LEADERBOARD_ENTRY_P3_DURATION = 12;
  private static final int TIMESTAMP_TEST_SECONDS_OFFSET = 10;
  private static final int ADDITIONAL_ENTRIES = 5;
  @TempDir private Path tempDir;
  private Path testFilePath;
  private JsonFileStorageProvider provider;

  @BeforeEach
  void setUp() {
    // Arrange - common setup for all tests
    testFilePath = tempDir.resolve("testLeaderboard.json");
    provider = new JsonFileStorageProvider(testFilePath);
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up test file if it exists
    if (Files.exists(testFilePath)) {
      Files.delete(testFilePath);
    }
  }

  @Test
  @DisplayName("should be available after initialization")
  void shouldBeAvailableAfterInitialization() {
    // Act
    provider.initialize();

    // Assert
    assertTrue(provider.isAvailable(), "Provider should be available");
  }

  @Test
  @DisplayName("should create empty file when initializing for first time")
  void shouldCreateEmptyFileWhenInitializingForFirstTime() {
    // Act
    provider.initialize();
    final List<LeaderboardEntry> entries = provider.getTop();

    // Assert
    assertTrue(Files.exists(testFilePath), "File should be created");
    assertTrue(entries.isEmpty(), "Initial leaderboard should be empty");
  }

  @Test
  @DisplayName("should save and retrieve single entry")
  void shouldSaveAndRetrieveSingleEntry() {
    // Arrange
    provider.initialize();
    final LeaderboardEntry entry =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));

    // Act
    final boolean saved = provider.save(entry);
    final List<LeaderboardEntry> entries = provider.getTop();

    // Assert
    assertTrue(saved, "Entry should be saved successfully");
    assertEquals(1, entries.size(), "Should have one entry");
    assertEquals(LEADERBOARD_ENTRY_P1_NICKNAME, entries.get(0).nickname(), "Nickname should match");
    assertEquals(LEADERBOARD_ENTRY_P1_SCORE, entries.get(0).score(), "Score should match");
  }

  @Test
  @DisplayName("should save and retrieve multiple entries sorted by score")
  void shouldSaveAndRetrieveMultipleEntriesSortedByScore() {
    // Arrange
    provider.initialize();
    final LeaderboardEntry entry1 =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));
    final LeaderboardEntry entry2 =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P2_ID,
            LEADERBOARD_ENTRY_P2_NICKNAME,
            LEADERBOARD_ENTRY_P2_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P2_LEVEL,
            LEADERBOARD_ENTRY_P2_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P2_DURATION));
    final LeaderboardEntry entry3 =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P3_ID,
            LEADERBOARD_ENTRY_P3_NICKNAME,
            LEADERBOARD_ENTRY_P3_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P3_LEVEL,
            LEADERBOARD_ENTRY_P3_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P3_DURATION));

    // Act
    provider.save(entry1);
    provider.save(entry2);
    provider.save(entry3);
    final List<LeaderboardEntry> entries = provider.getTop();

    // Assert
    assertEquals(3, entries.size(), "Should have three entries");
    assertEquals(
        LEADERBOARD_ENTRY_P2_NICKNAME,
        entries.stream().findFirst().get().nickname(),
        "Bob should be first (highest score)");
    assertEquals(
        LEADERBOARD_ENTRY_P3_NICKNAME,
        entries.stream().skip(1).findFirst().get().nickname(),
        "Charlie should be second");
    assertEquals(
        LEADERBOARD_ENTRY_P1_NICKNAME,
        entries.stream().skip(2).findFirst().get().nickname(),
        "Alice should be third (lowest score)");
  }

  @Test
  @DisplayName("should limit entries to MAX_ENTRIES")
  void shouldLimitEntriesToMaxEntries() {
    // Arrange
    provider.initialize();

    // Act - add more than MAX_ENTRIES entries
    for (int i = 0; i < StorageProvider.MAX_ENTRIES + ADDITIONAL_ENTRIES; i++) {
      final LeaderboardEntry entry =
          new LeaderboardEntry(
              "player" + i,
              "Player" + i,
              (long) i * 100,
              Instant.now(),
              i,
              i * 2,
              Duration.ofMinutes(i));
      provider.save(entry);
    }

    final List<LeaderboardEntry> entries = provider.getTop();

    // Assert
    assertEquals(
        StorageProvider.MAX_ENTRIES, entries.size(), "Should only keep MAX_ENTRIES top scores");
  }

  @Test
  @DisplayName("should persist entries across provider instances")
  void shouldPersistEntriesAcrossProviderInstances() {
    // Arrange
    provider.initialize();
    final LeaderboardEntry entry =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));
    provider.save(entry);

    // Act - create new provider with same file
    final JsonFileStorageProvider newProvider = new JsonFileStorageProvider(testFilePath);
    newProvider.initialize();
    final List<LeaderboardEntry> entries = newProvider.getTop();

    // Assert
    assertEquals(1, entries.size(), "Entry should be loaded from file");
    assertEquals(LEADERBOARD_ENTRY_P1_NICKNAME, entries.get(0).nickname(), "Nickname should match");
  }

  @Test
  @DisplayName("should return empty list when file is corrupted")
  void shouldReturnEmptyListWhenFileIsCorrupted() throws IOException {
    // Arrange
    provider.initialize();
    // Write invalid JSON to file
    Files.writeString(testFilePath, "invalid json content");

    // Act - create new provider that will try to load corrupted file
    final JsonFileStorageProvider newProvider = new JsonFileStorageProvider(testFilePath);
    newProvider.initialize();
    final List<LeaderboardEntry> entries = newProvider.getTop();

    // Assert
    assertTrue(entries.isEmpty(), "Should return empty list for corrupted file");
  }

  @Test
  @DisplayName("should sort entries with same score by timestamp")
  void shouldSortEntriesWithSameScoreByTimestamp() {
    // Arrange
    provider.initialize();
    final Instant now = Instant.now();
    final LeaderboardEntry older =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            now.minusSeconds(TIMESTAMP_TEST_SECONDS_OFFSET),
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));
    final LeaderboardEntry newer =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P2_ID,
            LEADERBOARD_ENTRY_P2_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            now,
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));

    // Act
    provider.save(newer);
    provider.save(older);
    final List<LeaderboardEntry> entries = provider.getTop();

    // Assert
    assertEquals(2, entries.size(), "Should have two entries");
    assertEquals(
        LEADERBOARD_ENTRY_P1_NICKNAME,
        entries.stream().findFirst().get().nickname(),
        "Older entry should come first when scores are equal");
    assertEquals(
        LEADERBOARD_ENTRY_P2_NICKNAME,
        entries.stream().skip(1).findFirst().get().nickname(),
        "Newer entry should come second when scores are equal");
  }

  @Test
  @DisplayName("should return defensive copy of entries")
  void shouldReturnDefensiveCopyOfEntries() {
    // Arrange
    provider.initialize();
    final LeaderboardEntry entry =
        new LeaderboardEntry(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            Instant.now(),
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));
    provider.save(entry);

    // Act
    final List<LeaderboardEntry> entries1 = provider.getTop();
    final List<LeaderboardEntry> entries2 = provider.getTop();

    // Assert
    assertNotSame(entries1, entries2, "Should return different list instances");
    assertEquals(entries1.size(), entries2.size(), "Both lists should have same content");
  }

  @Test
  @DisplayName("should include file path in provider name")
  void shouldIncludeFilePathInProviderName() {
    // Act
    final String name = provider.getName();

    // Assert
    assertTrue(name.contains("JSON"), "Name should contain 'JSON'");
    assertTrue(name.contains(testFilePath.toString()), "Name should contain file path");
  }
}
