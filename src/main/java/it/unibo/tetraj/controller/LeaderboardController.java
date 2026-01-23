package it.unibo.tetraj.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.ApplicationContext;
import it.unibo.tetraj.GameSession;
import it.unibo.tetraj.GameState;
import it.unibo.tetraj.InputHandler;
import it.unibo.tetraj.command.StateTransitionCommand;
import it.unibo.tetraj.model.LeaderboardModel;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.util.ResourceManager;
import it.unibo.tetraj.view.LeaderboardView;
import java.awt.Canvas;
import java.awt.event.KeyEvent;

/** Controller for the leaderboard state. Displays top scores and player information. */
public final class LeaderboardController implements Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardController.class);
  private static final float MUSIC_VOLUME = 0.1f;
  private final ApplicationContext applicationContext;
  private final ResourceManager resources;
  private final LeaderboardView view;
  private final InputHandler inputHandler;
  private LeaderboardModel model;

  /**
   * Creates a new leaderboard controller.
   *
   * @param applicationContext The application context
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "ApplicationContext is a shared singleton service")
  public LeaderboardController(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    resources = ResourceManager.getInstance();
    view = new LeaderboardView();
    inputHandler = new InputHandler();
  }

  /** {@inheritDoc} */
  @Override
  public void enter(final GameSession gameSession) {
    model = new LeaderboardModel(applicationContext.getLeaderboard().getTopEntries());
    resources.playBackgroundMusic("menuLoop.wav", MUSIC_VOLUME);
    setupKeyBindings();
    LOGGER.info("Entering leaderboard state");
  }

  /** {@inheritDoc} */
  @Override
  public GameSession exit() {
    final GameSession gameSession = GameSession.empty();
    inputHandler.clearBindings();
    LOGGER.info(String.format("Exiting leaderboard state with %s", gameSession));
    return gameSession;
  }

  /** {@inheritDoc} */
  @Override
  public void update(final float deltaTime) {
    // Menu doesn't need updates in this simple version
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
    // ESC to return to menu
    inputHandler.bindKey(
        KeyEvent.VK_ESCAPE,
        new StateTransitionCommand(applicationContext.getStateManager(), GameState.MENU));
  }
}
