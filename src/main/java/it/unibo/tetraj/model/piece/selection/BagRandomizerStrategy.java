package it.unibo.tetraj.model.piece.selection;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoRegistry;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Bag randomizer strategy implementing the official Tetris Guideline "Random Generator" system.
 * Also known as the "7-bag" randomizer, standard in all licensed Tetris games since 2001.
 *
 * <p>Places all 7 tetromino types into a bag, shuffles them, then deals them out. When empty,
 * refills and reshuffles. This ensures:
 *
 * <ul>
 *   <li>Each piece appears exactly once every 7 pieces
 *   <li>Maximum gap between identical pieces: 12 (first in bag N, last in bag N+1)
 *   <li>Minimum gap between identical pieces: 0 (consecutive pieces possible!)
 *   <li>Average gap between identical pieces: 6
 * </ul>
 */
public final class BagRandomizerStrategy implements PieceSelectionStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(BagRandomizerStrategy.class);
  private final List<Class<? extends AbstractTetromino<?>>> bag = new ArrayList<>();
  private final List<Class<? extends AbstractTetromino<?>>> availableTypes;
  private final Random random = new Random();

  /** Creates a new bag randomizer selection strategy. */
  public BagRandomizerStrategy() {
    this.availableTypes = TetrominoRegistry.getInstance().getAvailableTypes();
    LOGGER.debug("Initialized with {} piece types", availableTypes.size());
  }

  @Override
  public Class<? extends AbstractTetromino<?>> next() {
    final Class<? extends AbstractTetromino<?>> currentPiece;

    if (bag.isEmpty()) {
      LOGGER.debug("Bag empty, shuffling {} pieces", availableTypes.size());
      bag.addAll(availableTypes);
      Collections.shuffle(bag, random);
    }
    currentPiece = bag.remove(0);
    LOGGER.debug("Spawning piece {}", currentPiece.getSimpleName());
    return currentPiece;
  }

  @Override
  public void reset() {
    bag.clear();
    LOGGER.debug("Reset called");
  }
}
