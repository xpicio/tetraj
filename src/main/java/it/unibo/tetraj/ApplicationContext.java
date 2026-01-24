package it.unibo.tetraj;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.controller.Controller;
import it.unibo.tetraj.controller.GameOverController;
import it.unibo.tetraj.controller.LeaderboardController;
import it.unibo.tetraj.controller.MenuController;
import it.unibo.tetraj.controller.PlayController;
import it.unibo.tetraj.model.leaderboard.Leaderboard;
import it.unibo.tetraj.util.ApplicationProperties;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import it.unibo.tetraj.util.ResourceManager;
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
  private static final ApplicationContext INSTANCE = new ApplicationContext();
  private final ApplicationProperties applicationProperties;
  private GameEngine gameEngine;
  private GameStateManager stateManager;
  private Leaderboard leaderboard;
  private volatile boolean shutdownRequested;
  private volatile boolean fromShutdownHook;

  /** Private constructor for singleton pattern. */
  private ApplicationContext() {
    applicationProperties = ApplicationProperties.getInstance();
  }

  /**
   * Gets the singleton instance.
   *
   * @return The ApplicationContext instance
   */
  @SuppressFBWarnings(
      value = "MS_EXPOSE_REP",
      justification =
          "ApplicationContext is the main application singleton. "
              + "Mutable state is required for game lifecycle management.")
  public static ApplicationContext getInstance() {
    return INSTANCE;
  }

  /**
   * Gets the state manager.
   *
   * @return The state manager
   */
  public GameStateManager getStateManager() {
    return stateManager;
  }

  /**
   * Gets the leaderboard.
   *
   * @return The leaderboard instance
   */
  public Leaderboard getLeaderboard() {
    return leaderboard;
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
  public void bootstrap() {
    LOGGER.info(SEPARATOR);
    LOGGER.info(TITLE);
    LOGGER.info(SEPARATOR);
    LOGGER.info(String.format("Game version: %s", applicationProperties.getAppVersion()));
    LOGGER.info(String.format("Java Version: %s", System.getProperty("java.version")));
    LOGGER.info(String.format("OS: %s", System.getProperty("os.name")));
    LOGGER.info(SEPARATOR);
    // Register shutdown hook to handle SIGINT and SIGTERM signals
    addShutdownHook();
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
    // Create leaderboard
    leaderboard = new Leaderboard();
    // Create state manager
    stateManager = new GameStateManager();
    // Create all controllers
    final Controller leaderboardController = new LeaderboardController(this);
    final Controller menuController = new MenuController(this);
    final Controller playController = new PlayController(this);
    final Controller gameOverController = new GameOverController(this);
    // Register controllers with state manager
    stateManager.registerController(GameState.MENU, menuController);
    stateManager.registerController(GameState.PLAYING, playController);
    stateManager.registerController(GameState.GAME_OVER, gameOverController);
    stateManager.registerController(GameState.LEADERBOARD, leaderboardController);
    // Create game engine with configured state manager
    gameEngine = new GameEngine(stateManager);
    LOGGER.info("Game components initialized successfully");
  }

  /** Performs graceful shutdown sequence. Closes resources and exits the JVM. */
  @SuppressFBWarnings(
      value = "DM_EXIT",
      justification = "This is a shutdown method. Shutting down is literally its job.")
  private void gracefulShutdown() {
    final ResourceManager resourceManager = ResourceManager.getInstance();

    LOGGER.info("Performing graceful shutdown...");
    // Stop game engine
    if (gameEngine != null) {
      gameEngine.stop();
    }
    // Any additional cleanup here
    LOGGER.info("Releasing resources...");
    // Cleanup resources
    resourceManager.clearCaches();
    LOGGER.info("Shutdown complete. Bye!");
    LoggerFactory.flushAll();
    if (!fromShutdownHook) {
      System.exit(0);
    }
  }

  private void addShutdownHook() {
    final int watchdogTimeout = 10_000;
    final ApplicationContext applicationContext = this;

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
                  applicationContext.shutdown(true);
                }));
  }
}
