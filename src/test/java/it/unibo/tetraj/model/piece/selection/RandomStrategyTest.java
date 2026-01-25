package it.unibo.tetraj.model.piece.selection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for RandomStrategy. */
class RandomStrategyTest {

  private static final int MAX_ITERATIONS = 1000;
  private RandomStrategy strategy;
  private List<Class<? extends AbstractTetromino<?>>> availableTypes;

  @BeforeEach
  void setUp() {
    strategy = new RandomStrategy();
    availableTypes = TetrominoRegistry.getInstance().getAvailableTypes();
  }

  @Test
  @DisplayName("should return a non-null tetromino class")
  void shouldReturnNonNullTetrominoClass() {
    // Act
    final Class<? extends AbstractTetromino<?>> result = strategy.next();

    // Assert
    assertNotNull(result);
  }

  @Test
  @DisplayName("should return a valid tetromino type from available types")
  void shouldReturnValidTetrominoType() {
    // Act
    final Class<? extends AbstractTetromino<?>> result = strategy.next();

    // Assert
    assertTrue(availableTypes.contains(result));
  }

  @Test
  @DisplayName("should eventually return all tetromino types over many iterations")
  void shouldEventuallyReturnAllTypes() {
    // Arrange
    final Set<Class<? extends AbstractTetromino<?>>> receivedTypes = new HashSet<>();

    // Act
    for (int i = 0; i < MAX_ITERATIONS && receivedTypes.size() < availableTypes.size(); i++) {
      receivedTypes.add(strategy.next());
    }

    // Assert
    assertTrue(receivedTypes.containsAll(availableTypes));
  }

  @Test
  @DisplayName("should never fail when calling next() many times")
  void shouldNeverFailOnManyCalls() {
    // Act & Assert
    for (int i = 0; i < MAX_ITERATIONS; i++) {
      final Class<? extends AbstractTetromino<?>> result = strategy.next();
      assertNotNull(result);
      assertTrue(availableTypes.contains(result));
    }
  }

  @Test
  @DisplayName("reset should not throw and have no effect on random selection")
  void resetShouldNotThrow() {
    // Arrange
    strategy.next();
    strategy.next();

    // Act & Assert
    assertDoesNotThrow(strategy::reset);

    // Assert - can still get valid pieces after reset
    final Class<? extends AbstractTetromino<?>> result = strategy.next();
    assertNotNull(result);
    assertTrue(availableTypes.contains(result));
  }
}
