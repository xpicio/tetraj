package it.unibo.tetraj.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.ApplicationContext;
import it.unibo.tetraj.GameSession;
import it.unibo.tetraj.GameState;
import it.unibo.tetraj.InputHandler;
import it.unibo.tetraj.command.StateTransitionCommand;
import it.unibo.tetraj.model.GameOverModel;
import it.unibo.tetraj.model.leaderboard.Leaderboard;
import it.unibo.tetraj.model.leaderboard.PlayerProfile;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.util.ResourceManager;
import it.unibo.tetraj.view.GameOverView;
import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.util.Optional;

/** Controller for the game over state. Handles game over logic and input. */
public class GameOverController implements Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameOverController.class);
  private final ApplicationContext applicationContext;
  private final ResourceManager resources;
  private final GameOverView view;
  private final InputHandler inputHandler;
  private Optional<GameOverModel> model = Optional.empty();

  /**
   * Creates a new game over controller.
   *
   * @param applicationContext The application context
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "ApplicationContext is a shared singleton service")
  public GameOverController(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    resources = ResourceManager.getInstance();
    view = new GameOverView();
    inputHandler = new InputHandler();
  }

  /** {@inheritDoc} */
  @Override
  public void enter(final GameSession gameSession) {
    final boolean isScoreSaved = saveScoreIfQualifying(gameSession);
    resources.playSound("gameOver.wav");
    model = Optional.of(new GameOverModel(gameSession, isScoreSaved));
    setupKeyBindings();
    LOGGER.info("Entering game over state");
  }

  /** {@inheritDoc} */
  @Override
  public GameSession exit() {
    final GameSession gameSession =
        model.map(GameOverModel::getGameSession).orElse(GameSession.empty());
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
    view.render(model.get());
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

  /**
   * Saves the score to the leaderboard if it qualifies.
   *
   * @param gameSession The game session containing the score
   * @return true if the score was successfully saved to the leaderboard, false otherwise
   */
  private boolean saveScoreIfQualifying(final GameSession gameSession) {
    final Leaderboard leaderboard = applicationContext.getLeaderboard();
    final PlayerProfile playerProfile = gameSession.getPlayerProfile();
    boolean isSaved = false;

    if (leaderboard.isQualifyingScore(gameSession.getScore())) {
      isSaved =
          leaderboard.save(
              playerProfile.id(),
              playerProfile.nickname(),
              gameSession.getScore(),
              gameSession.getLevel(),
              gameSession.getLinesCleared(),
              gameSession.getDuration());

      if (isSaved) {
        LOGGER.info(
            "Score {} for player {} saved to leaderboard",
            gameSession.getScore(),
            playerProfile.nickname());
      } else {
        LOGGER.error("Failed to save qualifying score to leaderboard");
      }
    } else {
      LOGGER.info("Score {} does not qualify for leaderboard", gameSession.getScore());
    }
    return isSaved;
  }
}
