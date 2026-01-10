package it.unibo.tetraj.controller;

import it.unibo.tetraj.ApplicationContext;
import it.unibo.tetraj.GameState;
import it.unibo.tetraj.InputHandler;
import it.unibo.tetraj.command.PlayCommand;
import it.unibo.tetraj.command.StateTransitionCommand;
import it.unibo.tetraj.model.PlayModel;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.util.ResourceManager;
import it.unibo.tetraj.view.PlayView;
import java.awt.Canvas;
import java.awt.event.KeyEvent;

/** Controller for the playing state. */
public final class PlayController implements Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlayController.class);
  private final ApplicationContext applicationContext;
  private final ResourceManager resources;
  private final PlayModel model;
  private final PlayView view;
  private final InputHandler inputHandler;

  /**
   * Creates a new play controller.
   *
   * @param applicationContext The application context
   */
  public PlayController(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    resources = ResourceManager.getInstance();
    model = new PlayModel();
    view = new PlayView();
    inputHandler = new InputHandler();
  }

  @Override
  public void enter() {
    resources.playBackgroundMusic("playLoop.wav");
    model.startNewGame();
    setupKeyBindings();
    LOGGER.info("Entering play state");
  }

  @Override
  public void exit() {
    inputHandler.clearBindings();
    LOGGER.info("Exiting play state");
  }

  @Override
  public void update(final float deltaTime) {
    model.update(deltaTime);

    if (model.isGameOver()) {
      applicationContext.getStateManager().switchTo(GameState.GAME_OVER);
    }
  }

  @Override
  public void render() {
    view.render(model);
  }

  @Override
  public void handleInput(final int keyCode) {
    inputHandler.handleKeyPress(keyCode);
  }

  @Override
  public Canvas getCanvas() {
    return view.getCanvas();
  }

  /** Sets up the key bindings for playing state. */
  private void setupKeyBindings() {
    // LEFT or A to move piece left
    inputHandler.bindKey(KeyEvent.VK_LEFT, new PlayCommand(model, PlayModel::moveLeft, "moveLeft"));
    inputHandler.bindKey(KeyEvent.VK_A, new PlayCommand(model, PlayModel::moveLeft, "moveLeft"));

    // RIGHT or D to move piece right
    inputHandler.bindKey(
        KeyEvent.VK_RIGHT, new PlayCommand(model, PlayModel::moveRight, "moveRight"));
    inputHandler.bindKey(KeyEvent.VK_D, new PlayCommand(model, PlayModel::moveRight, "moveRight"));

    // DOWN or S to soft drop
    inputHandler.bindKey(KeyEvent.VK_DOWN, new PlayCommand(model, PlayModel::moveDown, "moveDown"));
    inputHandler.bindKey(KeyEvent.VK_S, new PlayCommand(model, PlayModel::moveDown, "moveDown"));

    // SPACE to hard drop
    inputHandler.bindKey(
        KeyEvent.VK_SPACE, new PlayCommand(model, PlayModel::hardDrop, "hardDrop"));

    // UP or W to rotate clockwise
    inputHandler.bindKey(
        KeyEvent.VK_UP, new PlayCommand(model, PlayModel::rotateClockwise, "rotateClockwise"));
    inputHandler.bindKey(
        KeyEvent.VK_W, new PlayCommand(model, PlayModel::rotateClockwise, "rotateClockwise"));

    // CONTROL or Z to rotate counterclockwise
    inputHandler.bindKey(
        KeyEvent.VK_CONTROL,
        new PlayCommand(model, PlayModel::rotatecounterClockwise, "rotatecounterClockwise"));
    inputHandler.bindKey(
        KeyEvent.VK_Z,
        new PlayCommand(model, PlayModel::rotatecounterClockwise, "rotatecounterClockwise"));

    // SHIFT or C to hold piece
    inputHandler.bindKey(
        KeyEvent.VK_SHIFT, new PlayCommand(model, PlayModel::holdPiece, "holdPiece"));
    inputHandler.bindKey(KeyEvent.VK_C, new PlayCommand(model, PlayModel::holdPiece, "holdPiece"));

    // P to pause playing
    inputHandler.bindKey(
        KeyEvent.VK_P, new PlayCommand(model, PlayModel::togglePause, "togglePause"));

    // ESC to quit playing or resume pause
    inputHandler.bindKey(
        KeyEvent.VK_ESCAPE,
        () -> {
          if (model.isPaused()) {
            model.togglePause();
          } else {
            new StateTransitionCommand(applicationContext.getStateManager(), GameState.MENU)
                .execute();
          }
        });
  }
}
