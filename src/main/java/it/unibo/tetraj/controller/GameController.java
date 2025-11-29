package it.unibo.tetraj.controller;

import it.unibo.tetraj.model.GameModel;
import it.unibo.tetraj.view.GameView;
import it.unibo.tetraj.utils.Logger;
import it.unibo.tetraj.utils.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The game controller that manages the game loop and coordinates Model and
 * View.
 * This is the "Controller" in the MVC pattern.
 */
public final class GameController implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    // Frame rate constants
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
    private static final double NANO_TO_SEC = 1.0 / 1_000_000_000.0;

    private final GameModel model;
    private final GameView view;
    private final JFrame frame;

    private Thread gameThread;
    private volatile boolean running;

    /**
     * Creates a new game controller.
     */
    public GameController() {
        this.model = new GameModel();
        this.view = new GameView();
        this.frame = createWindow();

        setupInputHandling();
    }

    /**
     * Creates and configures the game window.
     *
     * @return The configured JFrame
     */
    private JFrame createWindow() {
        final JFrame window = new JFrame("TETRAJ - A Java Tetris Clone");
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.add(view.getCanvas());
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);

        // Add window close listener
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                stop();
            }
        });

        return window;
    }

    /**
     * Sets up keyboard input handling.
     */
    private void setupInputHandling() {
        view.getCanvas().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    /**
     * Handles keyboard input.
     *
     * @param keyCode The key code of the pressed key
     */
    private void handleKeyPress(final int keyCode) {
        switch (model.getState()) {
            case MENU:
                handleMenuInput(keyCode);
                break;
            case PLAYING:
                handleGameInput(keyCode);
                break;
            case PAUSED:
                if (keyCode == KeyEvent.VK_P) {
                    model.togglePause();
                }
                break;
            case GAME_OVER:
                handleGameOverInput(keyCode);
                break;
        }
    }

    /**
     * Handles input in the menu state.
     *
     * @param keyCode The key code of the pressed key
     */
    private void handleMenuInput(final int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ENTER:
                model.startNewGame();
                LOGGER.info("New game started");
                break;
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            default:
                break;
        }
    }

    /**
     * Handles input during gameplay.
     *
     * @param keyCode The key code of the pressed key
     */
    private void handleGameInput(final int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                model.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                model.moveRight();
                break;
            case KeyEvent.VK_DOWN:
                model.moveDown();
                break;
            case KeyEvent.VK_UP:
                model.rotateClockwise();
                break;
            case KeyEvent.VK_Z:
                model.rotateCounterClockwise();
                break;
            case KeyEvent.VK_SPACE:
                model.hardDrop();
                break;
            case KeyEvent.VK_C:
                model.holdPiece();
                break;
            case KeyEvent.VK_P:
                model.togglePause();
                break;
            case KeyEvent.VK_ESCAPE:
                model.setState(GameModel.GameState.MENU);
                break;
            default:
                break;
        }
    }

    /**
     * Handles input in the game over state.
     *
     * @param keyCode The key code of the pressed key
     */
    private void handleGameOverInput(final int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ENTER:
                model.startNewGame();
                LOGGER.info("Game restarted");
                break;
            case KeyEvent.VK_ESCAPE:
                model.setState(GameModel.GameState.MENU);
                break;
            default:
                break;
        }
    }

    /**
     * Starts the game.
     */
    public void start() {
        if (!running) {
            running = true;
            frame.setVisible(true);
            view.getCanvas().requestFocusInWindow();

            gameThread = new Thread(this, "GameThread");
            gameThread.start();

            LOGGER.info("Game controller started");
        }
    }

    /**
     * Stops the game.
     */
    public void stop() {
        if (running) {
            running = false;
            LOGGER.info("Stopping game controller...");

            try {
                if (gameThread != null) {
                    gameThread.join();
                }
            } catch (final InterruptedException ex) {
                LOGGER.error("Error while stopping game thread", ex);
                Thread.currentThread().interrupt();
            }

            frame.dispose();
            LOGGER.info("Game controller stopped");
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The main game loop.
     */
    @Override
    public void run() {
        LOGGER.info("Game loop started");

        // Initialize view
        SwingUtilities.invokeLater(view::initialize);

        // Wait a bit for initialization
        try {
            Thread.sleep(100);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        }

        long lastTime = System.nanoTime();

        while (running) {
            final long now = System.nanoTime();
            final long updateLength = now - lastTime;
            lastTime = now;

            final double deltaTime = updateLength * NANO_TO_SEC;

            // Update game logic
            model.update(deltaTime);

            // Render
            view.render(model);

            // Sleep to maintain target FPS
            final long sleepTime = (lastTime - System.nanoTime() + OPTIMAL_TIME) / 1_000_000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (final InterruptedException ex) {
                    LOGGER.error("Game loop sleep interrupted", ex);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        LOGGER.info("Game loop ended");
    }
}
