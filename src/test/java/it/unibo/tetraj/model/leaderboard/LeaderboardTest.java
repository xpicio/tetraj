package it.unibo.tetraj.model.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/**
 * Unit tests for Leaderboard.
 *
 * <p>These tests use mock providers to verify provider selection logic and a separate JSON file for
 * integration tests to avoid conflicts with production data.
 */
class LeaderboardTest {

  private static final String LEADERBOARD_ENTRY_P1_ID = "player1";
  private static final String LEADERBOARD_ENTRY_P1_NICKNAME = "Alice";
  private static final int LEADERBOARD_ENTRY_P1_SCORE = 1000;
  private static final int LEADERBOARD_ENTRY_P1_LEVEL = 5;
  private static final int LEADERBOARD_ENTRY_P1_LINES = 20;
  private static final int LEADERBOARD_ENTRY_P1_DURATION = 10;
  private static final String LEADERBOARD_ENTRY_P2_ID = "player2";
  private static final String LEADERBOARD_ENTRY_P2_NICKNAME = "Bob";
  private static final int LEADERBOARD_ENTRY_P2_SCORE = 2000;
  private static final int LEADERBOARD_ENTRY_P2_LEVEL = 8;
  private static final int LEADERBOARD_ENTRY_P2_LINES = 40;
  private static final int LEADERBOARD_ENTRY_P2_DURATION = 15;
  private static final int SCORE_THRESHOLD = 100;
  @TempDir private Path tempDir;
  private Path testFilePath;
  private JsonFileStorageProvider jsonProvider;

  @BeforeEach
  void setUp() {
    // Arrange - create isolated JSON provider for integration tests
    testFilePath = tempDir.resolve("testLeaderboard.json");
    jsonProvider = new JsonFileStorageProvider(testFilePath);
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up test file if it exists
    if (Files.exists(testFilePath)) {
      Files.delete(testFilePath);
    }
  }

  @Test
  @DisplayName("should create independent instances")
  void shouldCreateIndependentInstances() {
    // Act
    final Leaderboard instance1 = new Leaderboard(List.of(jsonProvider));
    final Leaderboard instance2 = new Leaderboard(List.of(jsonProvider));

    // Assert
    assertNotEquals(instance1, instance2, "Should create independent instances");
  }

  @Test
  @DisplayName("should select first available provider")
  void shouldSelectFirstAvailableProvider() {
    // Arrange - create mock providers
    final StorageProvider unavailableProvider1 = mock(StorageProvider.class);
    when(unavailableProvider1.isAvailable()).thenReturn(false);

    final StorageProvider availableProvider = mock(StorageProvider.class);
    when(availableProvider.isAvailable()).thenReturn(true);
    when(availableProvider.getName()).thenReturn("AvailableProvider");

    final StorageProvider unavailableProvider2 = mock(StorageProvider.class);
    when(unavailableProvider2.isAvailable()).thenReturn(false);

    // Act
    final Leaderboard leaderboard =
        new Leaderboard(List.of(unavailableProvider1, availableProvider, unavailableProvider2));

    // Assert
    assertEquals(
        "AvailableProvider",
        leaderboard.getActiveProviderName(),
        "Should select first available provider");
    verify(unavailableProvider1).initialize();
    verify(availableProvider).initialize();
  }

  @Test
  @DisplayName("should return None when no provider available")
  void shouldReturnNoneWhenNoProviderAvailable() {
    // Arrange - all providers unavailable
    final StorageProvider unavailableProvider1 = mock(StorageProvider.class);
    when(unavailableProvider1.isAvailable()).thenReturn(false);

    final StorageProvider unavailableProvider2 = mock(StorageProvider.class);
    when(unavailableProvider2.isAvailable()).thenReturn(false);

    // Act
    final Leaderboard leaderboard =
        new Leaderboard(List.of(unavailableProvider1, unavailableProvider2));

    // Assert
    assertEquals("None", leaderboard.getActiveProviderName(), "Should return None");
  }

  @Test
  @DisplayName("should save and retrieve entry with JSON provider")
  void shouldSaveAndRetrieveEntryWithJsonProvider() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));
    final String playerId = "test-player-1";
    final String nickname = "TestPlayer";
    final int score = 5000;
    final int level = 10;
    final int lines = 50;
    final int duration = 20;

    // Act
    final boolean saved =
        leaderboard.save(playerId, nickname, score, level, lines, Duration.ofMinutes(duration));
    final List<LeaderboardEntry> entries = leaderboard.getTopEntries();

    // Assert
    assertTrue(saved, "Entry should be saved successfully");
    assertFalse(entries.isEmpty(), "Leaderboard should not be empty");
    assertEquals(1, entries.size(), "Should have one entry");
  }

  @Test
  @DisplayName("should return empty list when no provider available")
  void shouldReturnEmptyListWhenNoProviderAvailable() {
    // Arrange - no providers
    final Leaderboard leaderboard = new Leaderboard(List.of());

    // Act
    final List<LeaderboardEntry> entries = leaderboard.getTopEntries();

    // Assert
    assertNotNull(entries, "Should return non-null list");
    assertTrue(entries.isEmpty(), "Should return empty list when no provider available");
  }

  @Test
  @DisplayName("should delegate save to active provider")
  void shouldDelegateSaveToActiveProvider() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));

    // Act
    final boolean saved =
        leaderboard.save(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));

    // Assert
    assertTrue(saved, "Should delegate to provider and return true");
    assertEquals(1, leaderboard.getTopEntries().size(), "Should save entry");
  }

  @Test
  @DisplayName("should delegate getTopEntries to active provider")
  void shouldDelegateGetTopEntriesToActiveProvider() {
    // Arrange
    final StorageProvider mockProvider = mock(StorageProvider.class);
    when(mockProvider.isAvailable()).thenReturn(true);
    final List<LeaderboardEntry> mockEntries =
        List.of(
            new LeaderboardEntry(
                LEADERBOARD_ENTRY_P1_ID,
                LEADERBOARD_ENTRY_P1_NICKNAME,
                LEADERBOARD_ENTRY_P1_SCORE,
                Instant.now(),
                LEADERBOARD_ENTRY_P1_LEVEL,
                LEADERBOARD_ENTRY_P1_LINES,
                Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION)));
    when(mockProvider.getTop()).thenReturn(mockEntries);

    final Leaderboard leaderboard = new Leaderboard(List.of(mockProvider));

    // Act
    final List<LeaderboardEntry> entries = leaderboard.getTopEntries();

    // Assert
    assertEquals(1, entries.size(), "Should return entries from provider");
    assertEquals(
        LEADERBOARD_ENTRY_P1_NICKNAME, entries.get(0).nickname(), "Should return correct entry");
  }

  @Test
  @DisplayName("should handle saving multiple entries")
  void shouldHandleSavingMultipleEntries() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));

    // Act
    final boolean saved1 =
        leaderboard.save(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));
    final boolean saved2 =
        leaderboard.save(
            LEADERBOARD_ENTRY_P2_ID,
            LEADERBOARD_ENTRY_P2_NICKNAME,
            LEADERBOARD_ENTRY_P2_SCORE,
            LEADERBOARD_ENTRY_P2_LEVEL,
            LEADERBOARD_ENTRY_P2_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P2_DURATION));

    // Assert
    assertTrue(saved1, "First entry should be saved");
    assertTrue(saved2, "Second entry should be saved");
    assertEquals(2, leaderboard.getTopEntries().size(), "Should have two entries");
  }

  @Test
  @DisplayName("should qualify high score when leaderboard has entries")
  void shouldQualifyHighScoreWhenLeaderboardHasEntries() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));
    leaderboard.save(
        LEADERBOARD_ENTRY_P1_ID,
        LEADERBOARD_ENTRY_P1_NICKNAME,
        LEADERBOARD_ENTRY_P1_SCORE,
        LEADERBOARD_ENTRY_P1_LEVEL,
        LEADERBOARD_ENTRY_P1_LINES,
        Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));

    // Act
    final boolean isQualifyingScore = leaderboard.isQualifyingScore(LEADERBOARD_ENTRY_P2_SCORE);

    // Assert
    assertTrue(isQualifyingScore, "Higher score should qualify");
  }

  @Test
  @DisplayName("should check if score qualifies for empty leaderboard")
  void shouldCheckIfScoreQualifiesForEmptyLeaderboard() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));

    // Act
    final boolean isQualifyingScore = leaderboard.isQualifyingScore(SCORE_THRESHOLD);

    // Assert
    assertTrue(isQualifyingScore, "Any score qualifies for empty leaderboard");
  }

  @Test
  @DisplayName("should handle saving with zero score")
  void shouldHandleSavingWithZeroScore() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));
    final String playerId = "zero-player";
    final String nickname = "ZeroScore";
    final int zeroScore = 0;
    final int minLevel = 1;
    final int zeroLines = 0;
    final int minDuration = 1;

    // Act
    final boolean saved =
        leaderboard.save(
            playerId, nickname, zeroScore, minLevel, zeroLines, Duration.ofMinutes(minDuration));

    // Assert
    assertTrue(saved, "Should handle zero score without crashing");
  }

  @Test
  @DisplayName("should handle saving with very large score")
  void shouldHandleSavingWithVeryLargeScore() {
    // Arrange
    jsonProvider.initialize();
    final Leaderboard leaderboard = new Leaderboard(List.of(jsonProvider));
    final String playerId = "very-high-player";
    final String nickname = "VeryHighScore";
    final int score = Integer.MAX_VALUE - 1;
    final int level = 100;
    final int lines = 1000;
    final int duration = 66;

    // Act
    final boolean saved =
        leaderboard.save(playerId, nickname, score, level, lines, Duration.ofHours(duration));

    // Assert
    assertTrue(saved, "Should handle large score without crashing");
  }

  @Test
  @DisplayName("should return false when saving with no provider")
  void shouldReturnFalseWhenSavingWithNoProvider() {
    // Arrange - no providers
    final Leaderboard leaderboard = new Leaderboard(List.of());

    // Act
    final boolean saved =
        leaderboard.save(
            LEADERBOARD_ENTRY_P1_ID,
            LEADERBOARD_ENTRY_P1_NICKNAME,
            LEADERBOARD_ENTRY_P1_SCORE,
            LEADERBOARD_ENTRY_P1_LEVEL,
            LEADERBOARD_ENTRY_P1_LINES,
            Duration.ofMinutes(LEADERBOARD_ENTRY_P1_DURATION));

    // Assert
    assertFalse(saved, "Should return false when no provider available");
  }

  @Test
  @DisplayName("should return false when checking if score qualifies with no provider")
  void shouldReturnFalseWhenQualifiesWithNoProvider() {
    // Arrange - no providers
    final Leaderboard leaderboard = new Leaderboard(List.of());

    // Act
    final boolean isQualifyingScore = leaderboard.isQualifyingScore(SCORE_THRESHOLD);

    // Assert
    assertFalse(isQualifyingScore, "Should return false when no provider available");
  }
}
