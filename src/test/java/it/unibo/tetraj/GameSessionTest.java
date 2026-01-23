package it.unibo.tetraj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for GameSession. */
class GameSessionTest {

  private static final long TEST_SCORE = 1000;
  private static final int TEST_LEVEL = 5;
  private static final int TEST_LINES = 42;
  private static final int TEST_IMAGE_WIDTH = 10;
  private static final int TEST_IMAGE_HEIGHT = 20;
  private static final long TEST_DURATION_SECONDS = 300;
  private static final long PARTIAL_SCORE = 500;
  private static final int PARTIAL_LEVEL = 2;
  private static final int NEGATIVE_VALUE = -1;

  @Test
  @DisplayName("should create empty session with default values")
  void shouldCreateEmptySession() {
    // Arrange & Act
    final GameSession session = GameSession.empty();

    // Assert
    assertNotNull(session);
    assertTrue(session.isEmpty());
    assertFalse(session.hasData());
    assertEquals(Duration.ZERO, session.getDuration());
    assertEquals(0, session.getScore());
    assertEquals(0, session.getLevel());
  }

  @Test
  @DisplayName("should build session with all fields populated")
  void shouldBuildSessionWithAllFields() {
    // Arrange
    final Instant start = Instant.now();
    final Instant end = start.plusSeconds(TEST_DURATION_SECONDS);
    final BufferedImage image =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

    // Act
    final GameSession session =
        GameSession.builder()
            .withScore(TEST_SCORE)
            .withLevel(TEST_LEVEL)
            .withLinesCleared(TEST_LINES)
            .withLastFrame(image)
            .withGameStart(start)
            .withGameEnd(end)
            .build();

    // Assert
    assertFalse(session.isEmpty());
    assertTrue(session.hasData());
    assertEquals(TEST_SCORE, session.getScore());
    assertEquals(TEST_LEVEL, session.getLevel());
    assertEquals(TEST_LINES, session.getLinesCleared());
    final BufferedImage frameFromSession = session.getLastFrame();
    assertNotNull(frameFromSession);
    assertEquals(image.getWidth(), frameFromSession.getWidth());
    assertEquals(image.getHeight(), frameFromSession.getHeight());
    assertEquals(image.getType(), frameFromSession.getType());
    assertEquals(Duration.ofSeconds(TEST_DURATION_SECONDS), session.getDuration());
  }

  @Test
  @DisplayName("should build partial session with only some fields set")
  void shouldBuildPartialSession() {
    // Arrange & Act
    final GameSession session =
        GameSession.builder().withScore(PARTIAL_SCORE).withLevel(PARTIAL_LEVEL).build();

    // Assert
    assertFalse(session.isEmpty());
    assertEquals(PARTIAL_SCORE, session.getScore());
    assertEquals(PARTIAL_LEVEL, session.getLevel());
    assertEquals(0, session.getLinesCleared());
    assertNull(session.getLastFrame());
    assertEquals(Duration.ZERO, session.getDuration());
  }

  @Test
  @DisplayName("should create defensive copy of image on store and retrieval")
  void shouldCreateDefensiveCopyOfImage() {
    // Arrange
    final BufferedImage original =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

    // Act
    final GameSession session =
        GameSession.builder().withScore(TEST_SCORE).withLastFrame(original).build();
    final BufferedImage retrieved = session.getLastFrame();
    final BufferedImage anotherRetrieve = session.getLastFrame();

    // Assert
    assertNotNull(retrieved);
    assertTrue(!Objects.equals(retrieved, original), "Builder should create defensive copy");
    assertEquals(original.getWidth(), retrieved.getWidth());
    assertEquals(original.getHeight(), retrieved.getHeight());
    assertNotNull(anotherRetrieve);
    assertTrue(!Objects.equals(retrieved, anotherRetrieve), "Each access should return a new copy");
    assertEquals(retrieved.getWidth(), anotherRetrieve.getWidth());
    assertEquals(retrieved.getHeight(), anotherRetrieve.getHeight());
  }

  @Test
  @DisplayName("should mark game start and end times correctly")
  void shouldMarkGameStartAndEnd() {
    // Arrange & Act
    final GameSession session =
        GameSession.builder().withScore(TEST_SCORE).markGameStart().markGameEnd().build();

    // Assert
    assertNotNull(session.getGameStartTime());
    assertNotNull(session.getGameEndTime());
    assertTrue(session.getDuration().toMillis() >= 0);
  }

  @Test
  @DisplayName("should reject negative score")
  void shouldRejectNegativeScore() {
    // Arrange
    final GameSession.Builder builder = GameSession.builder().withScore(NEGATIVE_VALUE);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  @DisplayName("should reject negative level")
  void shouldRejectNegativeLevel() {
    // Arrange
    final GameSession.Builder builder = GameSession.builder().withLevel(NEGATIVE_VALUE);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  @DisplayName("should reject negative lines cleared")
  void shouldRejectNegativeLines() {
    // Arrange
    final GameSession.Builder builder = GameSession.builder().withLinesCleared(NEGATIVE_VALUE);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  @DisplayName("isEmpty should return false when session has data")
  void isEmptyShouldReturnFalseWhenSessionHasData() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act
    final boolean result = session.isEmpty();

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("getDuration should return zero when both dates are null")
  void durationShouldBeZeroWhenBothDatesNull() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act
    final Duration duration = session.getDuration();

    // Assert
    assertEquals(Duration.ZERO, duration);
  }

  @Test
  @DisplayName("getDuration should return zero when only start date is set")
  void durationShouldBeZeroWhenOnlyStartDateSet() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).markGameStart().build();

    // Act
    final Duration duration = session.getDuration();

    // Assert
    assertEquals(Duration.ZERO, duration);
  }

  @Test
  @DisplayName("getPlayerProfile should return non-null profile")
  void playerProfileShouldNotBeNull() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act
    final var profile = session.getPlayerProfile();

    // Assert
    assertNotNull(profile);
    assertNotNull(profile.id());
    assertNotNull(profile.nickname());
  }

  @Test
  @DisplayName("equals should return true for same instance")
  void equalsShouldReturnTrueForSameInstance() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act & Assert
    assertEquals(session, session);
  }

  @Test
  @DisplayName("equals should return false when compared with null")
  void equalsShouldReturnFalseWhenComparedWithNull() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act & Assert
    assertNotEquals(null, session);
  }

  @Test
  @DisplayName("equals should return false when compared with different class")
  void equalsShouldReturnFalseWhenComparedWithDifferentClass() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act & Assert
    assertNotEquals("not a session", session);
  }

  @Test
  @DisplayName("equals should return false for sessions with different scores")
  void equalsShouldReturnFalseForDifferentScores() {
    // Arrange
    final GameSession session1 = GameSession.builder().withScore(TEST_SCORE).build();
    final GameSession session2 = GameSession.builder().withScore(PARTIAL_SCORE).build();

    // Act & Assert
    assertNotEquals(session1, session2);
  }

  @Test
  @DisplayName("hashCode should return consistent value for same data")
  void hashCodeShouldReturnConsistentValue() {
    // Arrange
    final GameSession session =
        GameSession.builder().withScore(TEST_SCORE).withLevel(TEST_LEVEL).build();

    // Act
    final int hash1 = session.hashCode();
    final int hash2 = session.hashCode();

    // Assert
    assertEquals(hash1, hash2);
  }

  @Test
  @DisplayName("hashCode should be different for sessions with different data")
  void hashCodeShouldBeDifferentForDifferentData() {
    // Arrange
    final GameSession session1 = GameSession.builder().withScore(TEST_SCORE).build();
    final GameSession session2 = GameSession.builder().withScore(PARTIAL_SCORE).build();

    // Act
    final int hash1 = session1.hashCode();
    final int hash2 = session2.hashCode();

    // Assert
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("toString should contain all relevant fields")
  void stringRepresentationShouldContainAllRelevantFields() {
    // Arrange
    final GameSession session =
        GameSession.builder()
            .withScore(TEST_SCORE)
            .withLevel(TEST_LEVEL)
            .withLinesCleared(TEST_LINES)
            .build();

    // Act
    final String result = session.toString();

    // Assert
    assertTrue(result.contains("GameSession"));
    assertTrue(result.contains(String.valueOf(TEST_SCORE)));
    assertTrue(result.contains(String.valueOf(TEST_LEVEL)));
    assertTrue(result.contains(String.valueOf(TEST_LINES)));
  }

  @Test
  @DisplayName("toString should indicate when lastFrame is present")
  void stringRepresentationShouldIndicateLastFramePresent() {
    // Arrange
    final BufferedImage image =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final GameSession session = GameSession.builder().withLastFrame(image).build();

    // Act
    final String result = session.toString();

    // Assert
    assertTrue(result.contains("present"));
  }

  @Test
  @DisplayName("toString should indicate when lastFrame is null")
  void stringRepresentationShouldIndicateLastFrameNull() {
    // Arrange
    final GameSession session = GameSession.builder().withScore(TEST_SCORE).build();

    // Act
    final String result = session.toString();

    // Assert
    assertTrue(result.contains("null"));
  }

  @Test
  @DisplayName("equals should return false for sessions with different levels")
  void equalsShouldReturnFalseForDifferentLevels() {
    // Arrange
    final GameSession session1 = GameSession.builder().withLevel(TEST_LEVEL).build();
    final GameSession session2 = GameSession.builder().withLevel(PARTIAL_LEVEL).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return false for sessions with different lines cleared")
  void equalsShouldReturnFalseForDifferentLinesCleared() {
    // Arrange
    final GameSession session1 = GameSession.builder().withLinesCleared(TEST_LINES).build();
    final GameSession session2 = GameSession.builder().withLinesCleared(0).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return false for sessions with different gameStartTime")
  void equalsShouldReturnFalseForDifferentGameStartTime() {
    // Arrange
    final Instant start1 = Instant.now();
    final Instant start2 = start1.plusSeconds(TEST_DURATION_SECONDS);
    final GameSession session1 = GameSession.builder().withGameStart(start1).build();
    final GameSession session2 = GameSession.builder().withGameStart(start2).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return false for sessions with different gameEndTime")
  void equalsShouldReturnFalseForDifferentGameEndTime() {
    // Arrange
    final Instant end1 = Instant.now();
    final Instant end2 = end1.plusSeconds(TEST_DURATION_SECONDS);
    final GameSession session1 = GameSession.builder().withGameEnd(end1).build();
    final GameSession session2 = GameSession.builder().withGameEnd(end2).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return true for sessions with same image dimensions and type")
  void equalsShouldReturnTrueForSameImageDimensionsAndType() {
    // Arrange
    final BufferedImage image1 =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final BufferedImage image2 =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final GameSession session1 = GameSession.builder().withLastFrame(image1).build();
    final GameSession session2 = GameSession.builder().withLastFrame(image2).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertTrue(result);
  }

  @Test
  @DisplayName("equals should return false for sessions with different image dimensions")
  void equalsShouldReturnFalseForDifferentImageDimensions() {
    // Arrange
    final BufferedImage image1 =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final BufferedImage image2 =
        new BufferedImage(TEST_IMAGE_WIDTH + 1, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final GameSession session1 = GameSession.builder().withLastFrame(image1).build();
    final GameSession session2 = GameSession.builder().withLastFrame(image2).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return false when one session has null lastFrame and other has image")
  void equalsShouldReturnFalseWhenOneHasNullLastFrame() {
    // Arrange
    final BufferedImage image =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final GameSession session1 = GameSession.builder().withLastFrame(image).build();
    final GameSession session2 = GameSession.builder().build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("equals should return true when both sessions have null lastFrame")
  void equalsShouldReturnTrueWhenBothHaveNullLastFrame() {
    // Arrange
    final GameSession session1 = GameSession.builder().withScore(TEST_SCORE).build();
    final GameSession session2 = GameSession.builder().withScore(TEST_SCORE).build();

    // Act
    final boolean result = session1.equals(session2);

    // Assert
    assertTrue(result);
  }

  @Test
  @DisplayName("hashCode should be consistent for sessions with same image dimensions")
  void hashCodeShouldBeConsistentForSameImageDimensions() {
    // Arrange
    final BufferedImage image1 =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final BufferedImage image2 =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    final GameSession session1 = GameSession.builder().withLastFrame(image1).build();
    final GameSession session2 = GameSession.builder().withLastFrame(image2).build();

    // Act
    final int hash1 = session1.hashCode();
    final int hash2 = session2.hashCode();

    // Assert
    assertEquals(hash1, hash2);
  }
}
