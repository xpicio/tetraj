package it.unibo.tetraj.model.piece;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.unibo.tetraj.model.piece.selection.PieceSelectionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for TetrominoFactory. */
class TetrominoFactoryTest {

  @Test
  @DisplayName("should create tetromino using selection strategy")
  void shouldCreateTetrominoUsingStrategy() {
    // Arrange
    final PieceSelectionStrategy strategy = new FixedPieceStrategy(ITetromino.class);
    final TetrominoFactory factory = new TetrominoFactory(strategy);

    // Act
    final AbstractTetromino<?> piece = factory.create();

    // Assert
    assertNotNull(piece);
    assertInstanceOf(ITetromino.class, piece);
  }

  @Test
  @DisplayName("should create different pieces based on strategy")
  void shouldCreateDifferentPiecesBasedOnStrategy() {
    // Arrange
    final PieceSelectionStrategy strategy =
        new SequencePieceStrategy(TTetromino.class, STetromino.class, ZTetromino.class);
    final TetrominoFactory factory = new TetrominoFactory(strategy);

    // Act & Assert
    assertInstanceOf(TTetromino.class, factory.create());
    assertInstanceOf(STetromino.class, factory.create());
    assertInstanceOf(ZTetromino.class, factory.create());
  }

  @Test
  @DisplayName("should cycle through sequence when exceeding length")
  void shouldCycleThroughSequence() {
    // Arrange
    final PieceSelectionStrategy strategy =
        new SequencePieceStrategy(ITetromino.class, OTetromino.class);
    final TetrominoFactory factory = new TetrominoFactory(strategy);

    // Act & Assert
    assertInstanceOf(ITetromino.class, factory.create());
    assertInstanceOf(OTetromino.class, factory.create());
    assertInstanceOf(ITetromino.class, factory.create());
    assertInstanceOf(OTetromino.class, factory.create());
  }

  /** Test helper: strategy that always returns the same piece type. */
  private static class FixedPieceStrategy implements PieceSelectionStrategy {
    private final Class<? extends AbstractTetromino<?>> type;

    FixedPieceStrategy(final Class<? extends AbstractTetromino<?>> type) {
      this.type = type;
    }

    @Override
    public Class<? extends AbstractTetromino<?>> next() {
      return type;
    }

    @Override
    public void reset() {
      // No state to reset
    }
  }

  /** Test helper: strategy that cycles through a sequence of piece types. */
  private static class SequencePieceStrategy implements PieceSelectionStrategy {
    private final Class<? extends AbstractTetromino<?>>[] sequence;
    private int index;

    @SafeVarargs
    SequencePieceStrategy(final Class<? extends AbstractTetromino<?>>... types) {
      this.sequence = types.clone();
      this.index = 0;
    }

    @Override
    public Class<? extends AbstractTetromino<?>> next() {
      final Class<? extends AbstractTetromino<?>> result = sequence[index % sequence.length];
      index++;
      return result;
    }

    @Override
    public void reset() {
      index = 0;
    }
  }
}
