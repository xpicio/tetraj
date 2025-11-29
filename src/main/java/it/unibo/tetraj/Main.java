package it.unibo.tetraj;

import it.unibo.tetraj.controller.GameController;
import it.unibo.tetraj.utils.Logger;
import it.unibo.tetraj.utils.LoggerFactory;

/**
 * Main class that starts the Tetraj game.
 */
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String SEPARATOR = "========================================";

    /**
     * Application entry point.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Set system look and feel for native appearance
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            LOGGER.debug("System look and feel set successfully");
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            // If it fails, use default look and feel
            LOGGER.warn("Unable to set system look and feel", ex);
        }

        // Print startup information
        LOGGER.info(SEPARATOR);
        LOGGER.info("      TETRAJ - A Java Tetris Clone");
        LOGGER.info(SEPARATOR);
        LOGGER.info("Starting game...");
        LOGGER.info("Java Version: {}", System.getProperty("java.version"));
        LOGGER.info("OS: {}", System.getProperty("os.name"));
        LOGGER.info(SEPARATOR);

        // Create and start the game controller (MVC pattern)
        javax.swing.SwingUtilities.invokeLater(() -> {
            final GameController game = new GameController();
            game.start();
            LOGGER.info("Game started successfully");
        });
    }
}
