package it.unibo.tetraj.model.piece.selection;

import it.unibo.tetraj.model.piece.AbstractTetromino;

/**
 * Strategy interface for selecting the next tetromino piece to spawn. Allows different piece
 * selection algorithms (random, bag, sequence, etc.).
 */
public interface PieceSelectionStrategy {

  /**
   * Selects the next type of tetromino to create.
   *
   * @return The class of the tetromino to instantiate
   */
  Class<? extends AbstractTetromino<?>> next();

  /**
   * Resets the strategy to its initial state. Useful for strategies that maintain internal state
   * (e.g., bag randomizer, sequences).
   */
  void reset();
}
