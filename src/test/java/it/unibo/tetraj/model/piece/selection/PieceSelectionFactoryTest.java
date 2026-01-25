package it.unibo.tetraj.model.piece.selection;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for PieceSelectionFactory. */
class PieceSelectionFactoryTest {

  @Test
  @DisplayName("should create BagRandomizerStrategy when strategy name is 7-bag")
  void shouldCreateBagRandomizerStrategyFor7Bag() {
    // Arrange
    final String strategyName = "7-bag";

    // Act
    final PieceSelectionStrategy result = PieceSelectionFactory.create(strategyName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(BagRandomizerStrategy.class, result);
  }

  @Test
  @DisplayName("should create RandomStrategy when strategy name is random")
  void shouldCreateRandomStrategyForRandom() {
    // Arrange
    final String strategyName = "random";

    // Act
    final PieceSelectionStrategy result = PieceSelectionFactory.create(strategyName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(RandomStrategy.class, result);
  }

  @Test
  @DisplayName("should create RandomStrategy when strategy name is unknown")
  void shouldCreateRandomStrategyForUnknown() {
    // Arrange
    final String strategyName = "unknown-strategy";

    // Act
    final PieceSelectionStrategy result = PieceSelectionFactory.create(strategyName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(RandomStrategy.class, result);
  }

  @Test
  @DisplayName("should create RandomStrategy when strategy name is null")
  void shouldCreateRandomStrategyForNull() {
    // Arrange & Act
    final PieceSelectionStrategy result = PieceSelectionFactory.create((String) null);

    // Assert
    assertNotNull(result);
    assertInstanceOf(RandomStrategy.class, result);
  }

  @Test
  @DisplayName("should handle case insensitive strategy names")
  void shouldHandleCaseInsensitiveStrategyNames() {
    // Arrange & Act & Assert
    assertInstanceOf(BagRandomizerStrategy.class, PieceSelectionFactory.create("7-BAG"));
    assertInstanceOf(BagRandomizerStrategy.class, PieceSelectionFactory.create("7-Bag"));
    assertInstanceOf(RandomStrategy.class, PieceSelectionFactory.create("RANDOM"));
    assertInstanceOf(RandomStrategy.class, PieceSelectionFactory.create("Random"));
  }

  @Test
  @DisplayName("should handle strategy names with whitespace")
  void shouldHandleStrategyNamesWithWhitespace() {
    // Arrange & Act & Assert
    assertInstanceOf(BagRandomizerStrategy.class, PieceSelectionFactory.create("  7-bag  "));
    assertInstanceOf(RandomStrategy.class, PieceSelectionFactory.create("  random  "));
  }

  @Test
  @DisplayName("should create strategy from default configuration")
  void shouldCreateStrategyFromDefaultConfiguration() {
    // Act
    final PieceSelectionStrategy result = PieceSelectionFactory.create();

    // Assert
    assertNotNull(result);
  }
}
