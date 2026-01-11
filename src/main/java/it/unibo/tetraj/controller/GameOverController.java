package it.unibo.tetraj.controller;

import it.unibo.tetraj.ApplicationContext;
import it.unibo.tetraj.GameSession;
import it.unibo.tetraj.GameState;
import it.unibo.tetraj.InputHandler;
import it.unibo.tetraj.command.StateTransitionCommand;
import it.unibo.tetraj.model.GameOverModel;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.view.GameOverView;
import java.awt.Canvas;
import java.awt.event.KeyEvent;

/** Controller for the game over state. Handles game over logic and input. */
public class GameOverController implements Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameOverController.class);
  private final GameOverView view;
  private final InputHandler inputHandler;
  private final ApplicationContext applicationContext;
  private GameOverModel model;

  /**
   * Creates a new game over controller.
   *
   * @param applicationContext The application context
   */
  public GameOverController(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    view = new GameOverView();
    inputHandler = new InputHandler();
  }

  /** {@inheritDoc} */
  @Override
  public void enter(final GameSession gameSession) {
    model = new GameOverModel(gameSession);
    setupKeyBindings();
    LOGGER.info("Entering game over state");
  }

  /** {@inheritDoc} */
  @Override
  public GameSession exit() {
    final GameSession gameSession = model.getGameSession();
    inputHandler.clearBindings();
    LOGGER.info(String.format("Exiting game over state with %s", gameSession));
    return gameSession;
  }

  /** {@inheritDoc} */
  @Override
  public void update(final float deltaTime) {
    // Game over doesn't need updates
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    view.render(model);
  }

  /** {@inheritDoc} */
  @Override
  public void handleInput(final int keyCode) {
    inputHandler.handleKeyPress(keyCode);
  }

  /** {@inheritDoc} */
  @Override
  public Canvas getCanvas() {
    return view.getCanvas();
  }

  /** Sets up the key bindings for game over state. */
  private void setupKeyBindings() {
    // ENTER to restart (play again)
    inputHandler.bindKey(
        KeyEvent.VK_ENTER,
        new StateTransitionCommand(applicationContext.getStateManager(), GameState.PLAYING));
    // ESC to return to menu
    inputHandler.bindKey(
        KeyEvent.VK_ESCAPE,
        new StateTransitionCommand(applicationContext.getStateManager(), GameState.MENU));
  }
}
