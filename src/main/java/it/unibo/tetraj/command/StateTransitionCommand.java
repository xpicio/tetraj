package it.unibo.tetraj.command;

import it.unibo.tetraj.GameState;
import it.unibo.tetraj.GameStateManager;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;

/** Command to transition to a different game state. */
public class StateTransitionCommand implements Command {

  private static final Logger LOGGER = LoggerFactory.getLogger(StateTransitionCommand.class);
  private final GameStateManager stateManager;
  private final GameState targetState;

  /**
   * Creates a new state transition command.
   *
   * @param stateManager The state manager
   * @param targetState The state to transition to
   */
  public StateTransitionCommand(final GameStateManager stateManager, final GameState targetState) {
    this.stateManager = stateManager;
    this.targetState = targetState;
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    LOGGER.info("Attempting transition to: {}", targetState);
    if (!stateManager.switchTo(targetState)) {
      LOGGER.warn("Transition failed, invalid from current state");
    }
  }
}
