package it.unibo.tetraj.model.piece;

import it.unibo.tetraj.model.piece.selection.PieceSelectionStrategy;
import java.util.Objects;

/**
 * Factory class for creating tetromino pieces. Handles random piece generation and spawn position
 * calculation.
 */
public final class TetrominoFactory {

  /** The strategy used to select which piece to create next. */
  private final PieceSelectionStrategy pieceSelectionStrategy;

  /**
   * Creates a new factory with the specified selection strategy.
   *
   * @param pieceSelectionStrategy the piece selection strategy
   */
  public TetrominoFactory(final PieceSelectionStrategy pieceSelectionStrategy) {
    this.pieceSelectionStrategy = Objects.requireNonNull(pieceSelectionStrategy);
  }

  /**
   * Creates a new tetromino using the configured selection strategy.
   *
   * @return a new tetromino instance at position (0, 0)
   */
  public AbstractTetromino<?> create() {
    final Class<? extends AbstractTetromino<?>> clazz = pieceSelectionStrategy.next();
    return TetrominoRegistry.getInstance().create(clazz, 0, 0);
  }
}
