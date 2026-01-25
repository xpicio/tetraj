package it.unibo.tetraj.controller;

import it.unibo.tetraj.GameSession;
import java.awt.Canvas;

/** Interface for controllers. Each game state has its own implementation handling its logic. */
public interface Controller {

  /**
   * Called when entering this state. Receives session data from the previous state and initializes
   * state-specific resources.
   *
   * @param gameSession The session data passed from the previous state, may be empty
   */
  void enter(GameSession gameSession);

  /**
   * Called when exiting this state. Creates and returns session data for the next state and
   * performs cleanup.
   *
   * @return A GameSession containing relevant data for the next state
   */
  GameSession exit();

  /**
   * Updates the state logic.
   *
   * @param deltaTime Time elapsed since last update in seconds
   */
  void update(float deltaTime);

  /** Renders the state visuals. */
  void render();

  /**
   * Handles key press input for this state.
   *
   * @param keyCode The key code of the pressed key
   */
  void handleInput(int keyCode);

  /**
   * Handles key release input for this state. Default implementation does nothing. Override in
   * controllers that need key release handling (e.g., for soft drop).
   *
   * @param keyCode The key code of the released key
   */
  default void handleInputRelease(final int keyCode) {
    // Default: do nothing
  }

  /**
   * Gets the canvas for this state's view.
   *
   * @return The canvas component
   */
  Canvas getCanvas();
}
