package it.unibo.tetraj;

import it.unibo.tetraj.controller.Controller;
import it.unibo.tetraj.utils.Logger;
import it.unibo.tetraj.utils.LoggerFactory;
import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Main game engine that orchestrates the game. Manages the window, game loop, and delegates to
 * controllers.
 */
public final class GameEngine implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);
  private static final int TARGET_FPS = 60;
  // "Optimal" time per frame in nanoseconds
  private static final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
  // Conversion from nanoseconds to seconds
  private static final double NANO_TO_SEC = 1.0 / 1_000_000_000.0;
  // Thread join timeout in milliseconds
  private static final int THREAD_JOIN_TIMEOUT_MS = 5000;
  // Canvas initialization delay in milliseconds
  private static final int CANVAS_INIT_DELAY_MS = 50;
  private final JFrame window;
  private final GameStateManager stateManager;
  private Controller currentController;
  private Thread gameThread;
  private volatile boolean running;
  private Canvas currentCanvas;

  /**
   * Creates a new game engine with dependency injection.
   *
   * @param stateManager The configured state manager
   */
  public GameEngine(final GameStateManager stateManager) {
    this.stateManager = stateManager;
    this.window = createWindow();
  }

  /**
   * Creates and configures the game window.
   *
   * @return The configured JFrame
   */
  private JFrame createWindow() {
    final JFrame frame = new JFrame("TETRAJ Draft - State Management Demo");
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);

    // Window close handler
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(final WindowEvent e) {
            stop();
          }
        });

    return frame;
  }

  /** Updates the displayed canvas when state changes. */
  private void updateCanvas() {
    // Get current controller from state manager
    currentController = stateManager.getCurrentController();
    if (currentController == null) {
      return;
    }

    final Canvas newCanvas = currentController.getCanvas();

    // Remove old canvas
    if (currentCanvas != null) {
      window.remove(currentCanvas);
      // Remove key listeners from old canvas
      for (final var listener : currentCanvas.getKeyListeners()) {
        currentCanvas.removeKeyListener(listener);
      }
    }

    // Add new canvas
    currentCanvas = newCanvas;
    window.add(currentCanvas);
    window.pack();

    // Add key listener to new canvas
    currentCanvas.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(final KeyEvent e) {
            if (currentController != null) {
              currentController.handleInput(e.getKeyCode());
            }
          }
        });

    // Request focus
    SwingUtilities.invokeLater(
        () -> {
          currentCanvas.requestFocusInWindow();
        });
  }

  /** Starts the game engine. */
  public void start() {
    if (!running) {
      running = true;

      // Start with menu state
      stateManager.switchTo(GameState.MENU);
      updateCanvas();

      // Show window
      window.setVisible(true);

      // Start game thread
      gameThread = new Thread(this, "GameThread");
      gameThread.start();

      LOGGER.info("Game engine started");
    }
  }

  /** Stops the game engine. */
  public void stop() {
    if (running) {
      running = false;
      LOGGER.info("Stopping game engine...");

      // Wait for thread to finish
      try {
        if (gameThread != null) {
          gameThread.join(THREAD_JOIN_TIMEOUT_MS);
        }
      } catch (final InterruptedException ex) {
        LOGGER.warn("Interrupted while waiting for game thread");
        Thread.currentThread().interrupt();
      }

      // Dispose window
      window.dispose();
      LOGGER.info("Game engine stopped");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void run() {
    LOGGER.info("Game loop started");

    // Track last state for canvas updates
    GameState lastState = stateManager.getCurrentState();

    // Flag to track if canvas is ready
    boolean canvasReady = true;

    long lastTime = System.nanoTime();

    while (running) {
      final long now = System.nanoTime();
      final long updateLength = now - lastTime;
      lastTime = now;

      final float deltaTime = (float) (updateLength * NANO_TO_SEC);

      // Check if state changed
      final GameState currentState = stateManager.getCurrentState();
      if (currentState != lastState) {
        LOGGER.info("State changed from " + lastState + " to " + currentState);

        // Mark canvas as not ready
        canvasReady = false;

        // Update canvas synchronously on EDT and wait
        try {
          SwingUtilities.invokeAndWait(this::updateCanvas);
          // Give a bit of time for the canvas to fully initialize
          Thread.sleep(CANVAS_INIT_DELAY_MS);
          canvasReady = true;
        } catch (final InterruptedException ex) {
          LOGGER.warn("Canvas update interrupted: {}", ex.getMessage());
          Thread.currentThread().interrupt();
        } catch (final java.lang.reflect.InvocationTargetException ex) {
          LOGGER.warn("Error in canvas update: {}", ex.getCause().getMessage());
        }

        lastState = currentState;
      }

      // Update and render current controller directly
      if (currentController != null && canvasReady) {
        currentController.update(deltaTime);
        currentController.render();
      }

      // Sleep to maintain target FPS
      final long sleepTime = (lastTime - System.nanoTime() + OPTIMAL_TIME) / 1_000_000;
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (final InterruptedException ex) {
          LOGGER.warn("Game loop sleep interrupted");
          Thread.currentThread().interrupt();
          break;
        }
      }
    }

    LOGGER.info("Game loop ended");
  }
}
