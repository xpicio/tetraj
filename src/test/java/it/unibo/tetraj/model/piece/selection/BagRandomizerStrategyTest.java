package it.unibo.tetraj.model.piece.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoRegistry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for BagRandomizerStrategy. Tests follow AAA pattern: Arrange, Act, Assert. */
class BagRandomizerStrategyTest {

  private BagRandomizerStrategy strategy;
  private List<Class<? extends AbstractTetromino<?>>> availableTypes;
  private int bagSize;

  @BeforeEach
  void setUp() {
    // Arrange - common setup for all tests
    strategy = new BagRandomizerStrategy();
    availableTypes = TetrominoRegistry.getInstance().getAvailableTypes();
    bagSize = availableTypes.size();
  }

  @Test
  @DisplayName("should return a non-null tetromino class")
  void shouldReturnNonNullTetrominoClass() {
    // Arrange - done in setUp()

    // Act
    final Class<? extends AbstractTetromino<?>> result = strategy.next();

    // Assert
    assertNotNull(result, "next() should never return null");
  }

  @Test
  @DisplayName("should return a valid tetromino type from available types")
  void shouldReturnValidTetrominoType() {
    // Arrange - done in setUp()

    // Act
    final Class<? extends AbstractTetromino<?>> result = strategy.next();

    // Assert
    assertTrue(
        availableTypes.contains(result),
        "Returned type should be one of the available tetromino types");
  }

  @Test
  @DisplayName("should return each tetromino type exactly once per bag cycle")
  void shouldReturnEachTypeOncePerCycle() {
    // Arrange
    final Set<Class<? extends AbstractTetromino<?>>> receivedTypes = new HashSet<>();

    // Act
    for (int i = 0; i < bagSize; i++) {
      receivedTypes.add(strategy.next());
    }

    // Assert
    assertEquals(bagSize, receivedTypes.size(), "Should receive all unique types in one cycle");
    assertTrue(
        receivedTypes.containsAll(availableTypes), "Should receive all available tetromino types");
  }

  @Test
  @DisplayName("should refill the bag with all types after emptying")
  void shouldRefillBagAfterEmpty() {
    // Arrange
    final Set<Class<? extends AbstractTetromino<?>>> firstCycle = new HashSet<>();
    final Set<Class<? extends AbstractTetromino<?>>> secondCycle = new HashSet<>();

    // Act - First cycle
    for (int i = 0; i < bagSize; i++) {
      firstCycle.add(strategy.next());
    }

    // Act - Second cycle
    for (int i = 0; i < bagSize; i++) {
      secondCycle.add(strategy.next());
    }

    // Assert
    assertEquals(bagSize, firstCycle.size(), "First cycle should have all types");
    assertEquals(bagSize, secondCycle.size(), "Second cycle should have all types");
    assertEquals(firstCycle, secondCycle, "Both cycles should contain the same set of types");
  }

  @Test
  @DisplayName("should produce different order in consecutive bag cycles (statistically)")
  void shouldProduceDifferentOrderInConsecutiveCycles() {
    // Arrange
    final List<Class<? extends AbstractTetromino<?>>> firstSequence = new ArrayList<>();
    final List<Class<? extends AbstractTetromino<?>>> secondSequence = new ArrayList<>();

    // Act - Collect first cycle
    for (int i = 0; i < bagSize; i++) {
      firstSequence.add(strategy.next());
    }

    // Act - Collect second cycle
    for (int i = 0; i < bagSize; i++) {
      secondSequence.add(strategy.next());
    }

    // Assert
    // Note: There's a small chance (1/7!) they could be identical by random chance
    // For robust testing, we'd run multiple times or use seeded Random
    final boolean isDifferent = !Objects.equals(firstSequence, secondSequence);
    assertTrue(
        isDifferent || bagSize == 1, // If only 1 type, order can't differ
        "Sequences should be different (may fail rarely due to randomness)");
  }

  @Test
  @DisplayName("should distribute all pieces evenly over multiple complete cycles")
  void shouldDistributePiecesEvenlyOverMultipleCycles() {
    // Arrange
    final int cycles = 10;
    final Set<Class<? extends AbstractTetromino<?>>> allPiecesReceived = new HashSet<>();

    // Act
    for (int cycle = 0; cycle < cycles; cycle++) {
      final Set<Class<? extends AbstractTetromino<?>>> cycleTypes = new HashSet<>();
      for (int i = 0; i < bagSize; i++) {
        final Class<? extends AbstractTetromino<?>> piece = strategy.next();
        cycleTypes.add(piece);
        allPiecesReceived.add(piece);
      }
      // Assert - each cycle
      assertEquals(
          bagSize,
          cycleTypes.size(),
          "Cycle " + cycle + " should contain exactly " + bagSize + " different types");
    }

    // Assert - overall
    assertEquals(
        availableTypes.size(),
        allPiecesReceived.size(),
        "After " + cycles + " cycles, all tetromino types should have appeared");
  }

  @Test
  @DisplayName("should never fail when calling next() many times")
  void shouldNeverFailOnManyCalls() {
    // Arrange
    final int iterations = 1000;

    // Act & Assert - the test will fail if any exception is thrown
    for (int i = 0; i < iterations; i++) {
      final Class<? extends AbstractTetromino<?>> result = strategy.next();

      // Assert each result is valid
      assertNotNull(result, "Result should never be null at iteration " + i);
      assertTrue(
          availableTypes.contains(result), "Result should be a valid type at iteration " + i);
    }
  }

  @Test
  @DisplayName("should clear bag and start fresh cycle after reset")
  void shouldClearBagAfterReset() {
    // Arrange - partially consume the bag
    final int piecesToConsume = 3;
    for (int i = 0; i < piecesToConsume; i++) {
      strategy.next();
    }

    // Act
    strategy.reset();
    final Set<Class<? extends AbstractTetromino<?>>> afterReset = new HashSet<>();
    for (int i = 0; i < bagSize; i++) {
      afterReset.add(strategy.next());
    }

    // Assert
    assertEquals(bagSize, afterReset.size(), "After reset, should get all types in one cycle");
    assertTrue(
        afterReset.containsAll(availableTypes),
        "After reset, should receive all available tetromino types");
  }
}
