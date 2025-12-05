package it.unibo.tetraj;

import it.unibo.tetraj.controller.Controller;
import it.unibo.tetraj.controller.GameOverController;
import it.unibo.tetraj.controller.MenuController;
import it.unibo.tetraj.controller.PauseController;
import it.unibo.tetraj.controller.PlayController;
import it.unibo.tetraj.utils.Logger;
import it.unibo.tetraj.utils.LoggerFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Application context that manages bootstrap and shutdown. This is the main entry point of the
 * application.
 */
public final class ApplicationContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
  private static final String SEPARATOR = "========================================";
  private static final String TITLE = "      TETRAJ - A Java Tetris Clone      ";
  private GameEngine gameEngine;
  private GameStateManager stateManager;
  private volatile boolean shutdownRequested;
  private volatile boolean fromShutdownHook;

  /**
   * Gets the state manager.
   *
   * @return The state manager
   */
  public GameStateManager getStateManager() {
    return stateManager;
  }

  /** Requests application shutdown. */
  public void shutdown() {
    shutdown(false);
  }

  /**
   * Requests application shutdown with hook flag.
   *
   * @param fromHook true if called from shutdown hook, false otherwise
   */
  public void shutdown(final boolean fromHook) {
    if (!shutdownRequested) {
      shutdownRequested = true;
      fromShutdownHook = fromHook;
      LOGGER.info("Shutdown requested (from hook: {})", fromHook);
      gracefulShutdown();
    }
  }

  /** Bootstraps the entire application. */
  private void bootstrap() {
    LOGGER.info(SEPARATOR);
    LOGGER.info(TITLE);
    LOGGER.info(SEPARATOR);
    LOGGER.info("Starting game...");
    LOGGER.info("Java Version: " + System.getProperty("java.version"));
    LOGGER.info("OS: " + System.getProperty("os.name"));
    LOGGER.info(SEPARATOR);

    // Set system look and feel
    setupLookAndFeel();

    // Create and wire all components
    setupGameComponents();

    // Start the game engine
    SwingUtilities.invokeLater(
        () -> {
          gameEngine.start();
          LOGGER.info("Game started successfully");
        });
  }

  /** Sets up the system look and feel. */
  private void setupLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      LOGGER.info("System look and feel set successfully");
    } catch (final ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException ex) {
      LOGGER.warn("Unable to set system look and feel: {}", ex.getMessage());
    }
  }

  /** Sets up all game components and wires them together. */
  private void setupGameComponents() {
    LOGGER.info("Initializing game components...");

    // Create state manager
    stateManager = new GameStateManager();

    // Create all controllers
    final Controller menuController = new MenuController(this);
    final Controller playController = new PlayController(this);
    final Controller pauseController = new PauseController(this);
    final Controller gameOverController = new GameOverController(this);

    // Register controllers with state manager
    stateManager.registerController(GameState.MENU, menuController);
    stateManager.registerController(GameState.PLAYING, playController);
    stateManager.registerController(GameState.PAUSED, pauseController);
    stateManager.registerController(GameState.GAME_OVER, gameOverController);

    // Create game engine with configured state manager
    gameEngine = new GameEngine(stateManager);

    LOGGER.info("Game components initialized successfully");
  }

  /** Performs graceful shutdown sequence. */
  private void gracefulShutdown() {
    LOGGER.info("Performing graceful shutdown...");

    // Stop game engine
    if (gameEngine != null) {
      gameEngine.stop();
    }

    // Any additional cleanup here
    LOGGER.info("Releasing resources...");

    LOGGER.info("Shutdown complete. Bye!");

    LoggerFactory.flushAll();

    if (!fromShutdownHook) {
      System.exit(0);
    }
  }

  /**
   * Main entry point of the application.
   *
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    final int watchdogTimeout = 10_000;
    final ApplicationContext context = new ApplicationContext();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  LOGGER.info("Termination signal received (SIGINT or SIGTERM)");

                  final Thread watchdog =
                      new Thread(
                          () -> {
                            try {
                              Thread.sleep(watchdogTimeout);
                              LOGGER.warn("Shutdown timed out {}ms! Forcing halt", watchdogTimeout);
                              Runtime.getRuntime().halt(1);
                            } catch (final InterruptedException e) {
                              // Ignore interruption - we're shutting down anyway
                              Thread.currentThread().interrupt();
                            }
                          });
                  watchdog.setDaemon(true);
                  watchdog.start();

                  context.shutdown(true);
                }));

    context.bootstrap();
  }
}
