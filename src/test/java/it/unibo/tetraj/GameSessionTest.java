package it.unibo.tetraj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
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
  void shouldCreateEmptySession() {
    // Act
    final GameSession session = GameSession.empty();

    // Assert
    assertNotNull(session);
    assertTrue(session.isEmpty());
    assertFalse(session.hasData());
    assertEquals(Duration.ZERO, session.getDuration());
    assertEquals(session.getScore(), 0);
    assertEquals(session.getLevel(), 0);
  }

  @Test
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
  void shouldBuildPartialSession() {
    // Act
    final GameSession session =
        GameSession.builder().withScore(PARTIAL_SCORE).withLevel(PARTIAL_LEVEL).build();

    // Assert
    assertFalse(session.isEmpty());
    assertEquals(PARTIAL_SCORE, session.getScore());
    assertEquals(PARTIAL_LEVEL, session.getLevel());
    assertEquals(session.getLinesCleared(), 0);
    assertNull(session.getLastFrame());
    assertEquals(Duration.ZERO, session.getDuration());
  }

  @Test
  void shouldCreateDefensiveCopyOfImage() {
    // Arrange
    final BufferedImage original =
        new BufferedImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

    // Act - Builder should create a defensive copy
    final GameSession session =
        GameSession.builder().withScore(TEST_SCORE).withLastFrame(original).build();

    // Assert - the stored image should be a different instance (defensive copy)
    final BufferedImage retrieved = session.getLastFrame();
    assertNotNull(retrieved);
    assertTrue(!Objects.equals(retrieved, original), "Builder should create defensive copy");
    assertEquals(original.getWidth(), retrieved.getWidth());
    assertEquals(original.getHeight(), retrieved.getHeight());

    // Test that each call returns a new defensive copy (true immutability)
    final BufferedImage anotherRetrieve = session.getLastFrame();
    assertNotNull(anotherRetrieve);
    assertTrue(!Objects.equals(retrieved, anotherRetrieve), "Each access should return a new copy");
    assertEquals(retrieved.getWidth(), anotherRetrieve.getWidth());
    assertEquals(retrieved.getHeight(), anotherRetrieve.getHeight());
  }

  @Test
  void shouldMarkGameStartAndEnd() {
    // Act
    final GameSession session =
        GameSession.builder().withScore(TEST_SCORE).markGameStart().markGameEnd().build();

    // Assert
    assertNotNull(session.getGameStartTime());
    assertNotNull(session.getGameEndTime());
    assertTrue(session.getDuration().toMillis() >= 0);
  }

  @Test
  void shouldRejectNegativeScore() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> GameSession.builder().withScore(NEGATIVE_VALUE).build(),
        "Score cannot be negative");
  }

  @Test
  void shouldRejectNegativeLevel() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> GameSession.builder().withLevel(NEGATIVE_VALUE).build(),
        "Level cannot be negative");
  }

  @Test
  void shouldRejectNegativeLines() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> GameSession.builder().withLinesCleared(NEGATIVE_VALUE).build(),
        "Lines cleared cannot be negative");
  }
}
