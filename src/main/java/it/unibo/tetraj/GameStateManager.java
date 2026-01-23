package it.unibo.tetraj;

import it.unibo.tetraj.controller.Controller;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages game state transitions using a finite state machine. Validates transitions between
 * states.
 */
public class GameStateManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameStateManager.class);
  private GameState currentState;
  private final Map<GameState, Set<GameState>> validTransitions;
  private final Map<GameState, Controller> controllers;
  private Controller currentController;

  /** Creates a new state manager with defined valid transitions. */
  public GameStateManager() {
    controllers = new EnumMap<>(GameState.class);
    validTransitions = new EnumMap<>(GameState.class);
    initializeValidTransitions();
  }

  /**
   * Registers a controller for a specific state.
   *
   * @param state The game state
   * @param controller The controller for that state
   */
  public void registerController(final GameState state, final Controller controller) {
    controllers.put(state, controller);
    LOGGER.info("Registered controller for state: {}", state);
  }

  /**
   * Checks if a transition is valid.
   *
   * @param from Source state
   * @param to Target state
   * @return true if transition is valid
   */
  public boolean isValidTransition(final GameState from, final GameState to) {
    if (from == null) {
      // Initial state, allow transition to MENU only
      return to == GameState.MENU;
    }

    return validTransitions.get(from).contains(to);
  }

  /**
   * Switches to a new state if the transition is valid.
   *
   * @param newState The state to switch to
   * @return true if transition was successful
   */
  public boolean switchTo(final GameState newState) {
    if (!isValidTransition(currentState, newState)) {
      LOGGER.warn("Invalid state transition: {} -> {}", currentState, newState);
      return false;
    }

    LOGGER.info("State transition: {} -> {}", currentState, newState);

    // Check if controller exists for new state
    final Controller newController = controllers.get(newState);
    GameSession gameSession = GameSession.empty();

    if (newController == null) {
      LOGGER.error("No controller registered for state: {}", newState);
      return false;
    }

    // Exit current state
    if (currentController != null) {
      gameSession = currentController.exit();
    }
    // Switch to new state
    currentState = newState;
    currentController = newController;
    // Enter new state
    currentController.enter(gameSession);
    return true;
  }

  /**
   * Gets the current state.
   *
   * @return The current game state
   */
  public GameState getCurrentState() {
    return currentState;
  }

  /**
   * Gets the current controller.
   *
   * @return The active controller
   */
  public Controller getCurrentController() {
    return currentController;
  }

  /** Initializes the valid state transitions. */
  private void initializeValidTransitions() {
    // From MENU
    validTransitions.put(GameState.MENU, EnumSet.of(GameState.PLAYING, GameState.LEADERBOARD));
    // From PLAYING
    validTransitions.put(GameState.PLAYING, EnumSet.of(GameState.GAME_OVER, GameState.MENU));
    // From GAME_OVER
    validTransitions.put(
        GameState.GAME_OVER, EnumSet.of(GameState.MENU, GameState.PLAYING, GameState.LEADERBOARD));
    // From LEADERBOARD
    validTransitions.put(GameState.LEADERBOARD, EnumSet.of(GameState.MENU));
  }
}
