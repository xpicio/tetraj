package it.unibo.tetraj.model.piece;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registry for all available tetromino types. Provides factory methods to create tetromino
 * instances. Implemented as a singleton to ensure a single source of truth.
 */
public final class TetrominoRegistry {

  private static final TetrominoRegistry INSTANCE = new TetrominoRegistry();
  private final Map<
          Class<? extends AbstractTetromino<?>>, BiFunction<Integer, Integer, AbstractTetromino<?>>>
      factories;
  private final List<Class<? extends AbstractTetromino<?>>> availableTypes;

  /** Private constructor for singleton pattern. */
  private TetrominoRegistry() {
    factories =
        Map.of(
            ITetromino.class, (x, y) -> new ITetromino(x, y),
            OTetromino.class, (x, y) -> new OTetromino(x, y),
            TTetromino.class, (x, y) -> new TTetromino(x, y),
            STetromino.class, (x, y) -> new STetromino(x, y),
            ZTetromino.class, (x, y) -> new ZTetromino(x, y),
            JTetromino.class, (x, y) -> new JTetromino(x, y),
            LTetromino.class, (x, y) -> new LTetromino(x, y));
    availableTypes = List.copyOf(factories.keySet());
  }

  /**
   * Returns the singleton instance of the registry.
   *
   * @return the registry instance
   */
  public static TetrominoRegistry getInstance() {
    return INSTANCE;
  }

  /**
   * Returns the list of all available tetromino types.
   *
   * @return an immutable list of tetromino classes
   */
  public List<Class<? extends AbstractTetromino<?>>> getAvailableTypes() {
    return availableTypes;
  }

  /**
   * Creates a new tetromino instance of the specified type.
   *
   * @param type the class of the tetromino to create
   * @param x the initial X position
   * @param y the initial Y position
   * @return a new tetromino instance
   */
  public AbstractTetromino<?> create(
      final Class<? extends AbstractTetromino<?>> type, final int x, final int y) {
    return factories.get(type).apply(x, y);
  }
}
