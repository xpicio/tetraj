package it.unibo.tetraj.model.piece.selection;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoRegistry;
import it.unibo.tetraj.utils.Logger;
import it.unibo.tetraj.utils.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Bag randomizer strategy for piece selection. Ensures each tetromino type appears exactly once
 * before the bag is refilled. This provides more balanced gameplay compared to pure random
 * selection.
 */
public final class BagRandomizerStrategy implements PieceSelectionStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(BagRandomizerStrategy.class);
  private final List<Class<? extends AbstractTetromino<?>>> bag = new ArrayList<>();
  private final List<Class<? extends AbstractTetromino<?>>> availableTypes =
      TetrominoRegistry.getInstance().getAvailableTypes();
  private final Random random = new Random();

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
  }
}
