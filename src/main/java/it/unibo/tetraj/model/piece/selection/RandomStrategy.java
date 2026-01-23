package it.unibo.tetraj.model.piece.selection;

import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.model.piece.TetrominoRegistry;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.util.List;
import java.util.Random;

/**
 * Pure random piece selection strategy, as used in classic Tetris versions (NES, Game Boy). Each
 * piece has an equal 1/7 probability of being selected on every spawn, completely independent of
 * previous selections.
 *
 * <p>This was the original Tetris randomizer before the Guideline standardization in 2001. While
 * simpler to implement, it can lead to frustrating gameplay scenarios:
 *
 * <ul>
 *   <li>Long droughts without specific pieces (e.g., 30+ pieces without an I-piece)
 *   <li>Floods of the same piece (e.g., 5+ S-pieces in a row)
 *   <li>No guaranteed piece distribution
 *   <li>Theoretically infinite gaps between identical pieces
 * </ul>
 */
public final class RandomStrategy implements PieceSelectionStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(RandomStrategy.class);
  private final List<Class<? extends AbstractTetromino<?>>> availableTypes;
  private final Random random = new Random();

  /** Creates a new random selection strategy. */
  public RandomStrategy() {
    this.availableTypes = TetrominoRegistry.getInstance().getAvailableTypes();
    LOGGER.debug("Initialized with {} piece types", availableTypes.size());
  }

  /** {@inheritDoc} */
  @Override
  public Class<? extends AbstractTetromino<?>> next() {
    final int index = random.nextInt(availableTypes.size());
    final Class<? extends AbstractTetromino<?>> currentPiece = availableTypes.get(index);

    LOGGER.debug("Spawning piece {}", currentPiece.getSimpleName());
    return currentPiece;
  }

  /** {@inheritDoc} */
  @Override
  public void reset() {
    LOGGER.debug("Reset called, no action needed for random strategy");
  }
}
