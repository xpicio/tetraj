package it.unibo.tetraj.controller;

import it.unibo.tetraj.ApplicationContext;
import it.unibo.tetraj.GameState;
import it.unibo.tetraj.InputHandler;
import it.unibo.tetraj.command.QuitCommand;
import it.unibo.tetraj.command.StateTransitionCommand;
import it.unibo.tetraj.model.MenuModel;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.util.ResourceManager;
import it.unibo.tetraj.view.MenuView;
import java.awt.Canvas;
import java.awt.event.KeyEvent;

/** Controller for the menu state. Handles menu logic and input. */
public class MenuController implements Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);
  private final ApplicationContext applicationContext;
  private final ResourceManager resources;
  private final MenuModel model;
  private final MenuView view;
  private final InputHandler inputHandler;

  /**
   * Creates a new menu controller.
   *
   * @param applicationContext The application context
   */
  public MenuController(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    resources = ResourceManager.getInstance();
    model = new MenuModel();
    view = new MenuView();
    inputHandler = new InputHandler();
  }

  /** {@inheritDoc} */
  @Override
  public void enter() {
    resources.playBackgroundMusic("menuLoop.wav", 0.1f);
    setupKeyBindings();
    LOGGER.info("Entering menu state");
  }

  /** {@inheritDoc} */
  @Override
  public void exit() {
    inputHandler.clearBindings();
    LOGGER.info("Exiting menu state");
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

  /** Sets up the key bindings for menu state. */
  private void setupKeyBindings() {
    // ENTER to start playing
    inputHandler.bindKey(
        KeyEvent.VK_ENTER,
        () -> {
          resources.playSound("menuSelect.wav");
          new StateTransitionCommand(applicationContext.getStateManager(), GameState.PLAYING)
              .execute();
        });

    // ESC to quit
    inputHandler.bindKey(KeyEvent.VK_ESCAPE, new QuitCommand(applicationContext));
  }
}
