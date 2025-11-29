package it.unibo.tetraj.view;

import it.unibo.tetraj.model.GameModel;
import it.unibo.tetraj.model.Board;
import it.unibo.tetraj.model.Tetromino;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

/**
 * The game view responsible for rendering the game state.
 * This is the "View" in the MVC pattern.
 */
public final class GameView {

    // Window dimensions
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Board rendering constants
    private static final int CELL_SIZE = 25;
    private static final int BOARD_X = 250;
    private static final int BOARD_Y = 50;

    // UI constants
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 40);
    private static final Color BOARD_BG_COLOR = new Color(20, 20, 30);
    private static final Color GRID_COLOR = new Color(50, 50, 60);
    private static final Color GHOST_ALPHA = new Color(255, 255, 255, 60);
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 150);

    // Font constants
    private static final String FONT_NAME = "Arial";
    private static final Font TITLE_FONT = new Font(FONT_NAME, Font.BOLD, 36);
    private static final Font SCORE_FONT = new Font(FONT_NAME, Font.BOLD, 20);
    private static final Font INFO_FONT = new Font(FONT_NAME, Font.PLAIN, 16);

    // UI positioning constants
    private static final int TITLE_Y = 200;
    private static final int SUBTITLE_OFFSET = 50;
    private static final int INSTRUCTIONS_OFFSET = 80;
    private static final int LINE_HEIGHT = 25;
    private static final int PANEL_X = 550;
    private static final int PANEL_START_Y = 50;
    private static final int SECTION_SPACING = 40;
    private static final int SUBSECTION_SPACING = 25;
    private static final int LARGE_SECTION_SPACING = 60;
    private static final int NEXT_PIECE_SPACING = 120;
    private static final int PREVIEW_OFFSET = 5;
    private static final int PREVIEW_CELL_SIZE = 20;
    private static final int PAUSE_TEXT_OFFSET = 40;
    private static final int GAME_OVER_Y_OFFSET = 50;

    private final Canvas canvas;
    private BufferStrategy bufferStrategy;

    /**
     * Creates a new game view.
     */
    public GameView() {
        this.canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        canvas.setBackground(Color.BLACK);
        canvas.setFocusable(true);
    }

    /**
     * Initializes the buffer strategy.
     * Must be called after the canvas is added to a visible window.
     */
    public void initialize() {
        canvas.createBufferStrategy(3);
        bufferStrategy = canvas.getBufferStrategy();
    }

    /**
     * Renders the game state.
     *
     * @param model The game model to render
     */
    public void render(final GameModel model) {
        if (bufferStrategy == null) {
            initialize();
            if (bufferStrategy == null) {
                return;
            }
        }

        Graphics2D g = null;
        try {
            g = (Graphics2D) bufferStrategy.getDrawGraphics();

            // Enable anti-aliasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Clear screen
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Render based on game state
            switch (model.getState()) {
                case MENU:
                    renderMenu(g);
                    break;
                case PLAYING:
                case PAUSED:
                    renderGame(g, model);
                    if (model.getState() == GameModel.GameState.PAUSED) {
                        renderPauseOverlay(g);
                    }
                    break;
                case GAME_OVER:
                    renderGame(g, model);
                    renderGameOverOverlay(g, model);
                    break;
            }

            bufferStrategy.show();
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    /**
     * Renders the main menu.
     *
     * @param g The graphics context
     */
    private void renderMenu(final Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(TITLE_FONT);

        final String title = "TETRAJ";
        int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        int y = TITLE_Y;
        g.drawString(title, x, y);

        g.setFont(SCORE_FONT);
        final String subtitle = "A Java Tetris Clone";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(subtitle)) / 2;
        y += SUBTITLE_OFFSET;
        g.drawString(subtitle, x, y);

        g.setFont(INFO_FONT);
        final String[] instructions = {
            "Press ENTER to Start",
            "",
            "Controls:",
            "← → - Move",
            "↑ - Rotate",
            "↓ - Soft Drop",
            "SPACE - Hard Drop",
            "C - Hold",
            "P - Pause",
        };

        y += INSTRUCTIONS_OFFSET;
        for (final String line : instructions) {
            x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(line)) / 2;
            g.drawString(line, x, y);
            y += LINE_HEIGHT;
        }
    }

    /**
     * Renders the game board and UI.
     *
     * @param g The graphics context
     * @param model The game model to render
     */
    private void renderGame(final Graphics2D g, final GameModel model) {
        final Board board = model.getBoard();

        // Draw board background
        g.setColor(BOARD_BG_COLOR);
        g.fillRect(BOARD_X, BOARD_Y,
                board.getWidth() * CELL_SIZE,
                board.getHeight() * CELL_SIZE);

        // Draw grid
        g.setColor(GRID_COLOR);
        for (int row = 0; row <= board.getHeight(); row++) {
            g.drawLine(BOARD_X, BOARD_Y + row * CELL_SIZE,
                    BOARD_X + board.getWidth() * CELL_SIZE,
                    BOARD_Y + row * CELL_SIZE);
        }
        for (int col = 0; col <= board.getWidth(); col++) {
            g.drawLine(BOARD_X + col * CELL_SIZE, BOARD_Y,
                    BOARD_X + col * CELL_SIZE,
                    BOARD_Y + board.getHeight() * CELL_SIZE);
        }

        // Draw placed pieces
        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                final Color color = board.getCellColor(row, col);
                if (color != null) {
                    drawCell(g, BOARD_X + col * CELL_SIZE,
                            BOARD_Y + row * CELL_SIZE, color);
                }
            }
        }

        // Draw ghost piece
        final Tetromino ghost = model.getGhostPiece();
        if (ghost != null) {
            drawTetromino(g, ghost, BOARD_X, BOARD_Y, true);
        }

        // Draw current piece
        final Tetromino current = model.getCurrentPiece();
        if (current != null) {
            drawTetromino(g, current, BOARD_X, BOARD_Y, false);
        }

        // Draw UI panels
        renderSidePanel(g, model);
    }

    /**
     * Draws a single tetromino.
     *
     * @param g The graphics context
     * @param tetromino The tetromino to draw
     * @param boardX The board X position
     * @param boardY The board Y position
     * @param isGhost Whether this is a ghost piece
     */
    private void drawTetromino(final Graphics2D g, final Tetromino tetromino,
                               final int boardX, final int boardY,
                               final boolean isGhost) {
        final int[][] shape = tetromino.getShape();
        final Color color = isGhost ? GHOST_ALPHA : tetromino.getColor();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    final int x = boardX + (tetromino.getX() + col) * CELL_SIZE;
                    final int y = boardY + (tetromino.getY() + row) * CELL_SIZE;

                    if (isGhost) {
                        g.setColor(color);
                        g.drawRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                    } else {
                        drawCell(g, x, y, color);
                    }
                }
            }
        }
    }

    /**
     * Draws a single cell.
     *
     * @param g The graphics context
     * @param x The X position
     * @param y The Y position
     * @param color The cell color
     */
    private void drawCell(final Graphics2D g, final int x, final int y,
                          final Color color) {
        // Main color
        g.setColor(color);
        g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        // Highlight
        g.setColor(color.brighter());
        g.fillRect(x + 2, y + 2, CELL_SIZE - 4, 2);
        g.fillRect(x + 2, y + 2, 2, CELL_SIZE - 4);

        // Shadow
        g.setColor(color.darker());
        g.fillRect(x + CELL_SIZE - 3, y + 2, 2, CELL_SIZE - 3);
        g.fillRect(x + 2, y + CELL_SIZE - 3, CELL_SIZE - 3, 2);
    }

    /**
     * Renders the side panel with score and next piece.
     *
     * @param g The graphics context
     * @param model The game model
     */
    private void renderSidePanel(final Graphics2D g, final GameModel model) {
        int y = PANEL_START_Y;

        // Title
        g.setColor(Color.WHITE);
        g.setFont(TITLE_FONT);
        g.drawString("TETRAJ", PANEL_X, y);

        // Score
        y += LARGE_SECTION_SPACING;
        g.setFont(SCORE_FONT);
        g.drawString("Score", PANEL_X, y);
        y += SUBSECTION_SPACING;
        g.setFont(INFO_FONT);
        g.drawString(String.format("%,d", model.getScore()), PANEL_X, y);

        // Level
        y += SECTION_SPACING;
        g.setFont(SCORE_FONT);
        g.drawString("Level", PANEL_X, y);
        y += SUBSECTION_SPACING;
        g.setFont(INFO_FONT);
        g.drawString(String.valueOf(model.getLevel()), PANEL_X, y);

        // Lines
        y += SECTION_SPACING;
        g.setFont(SCORE_FONT);
        g.drawString("Lines", PANEL_X, y);
        y += SUBSECTION_SPACING;
        g.setFont(INFO_FONT);
        g.drawString(String.valueOf(model.getLinesCleared()), PANEL_X, y);

        // Next piece
        y += LARGE_SECTION_SPACING;
        g.setFont(SCORE_FONT);
        g.drawString("Next", PANEL_X, y);
        y += SUBSECTION_SPACING + PREVIEW_OFFSET;

        final Tetromino next = model.getNextPiece();
        if (next != null) {
            drawPreviewPiece(g, next, PANEL_X, y);
        }

        // Held piece
        y += NEXT_PIECE_SPACING;
        g.setFont(SCORE_FONT);
        g.drawString("Hold", PANEL_X, y);
        y += SUBSECTION_SPACING + PREVIEW_OFFSET;

        final Tetromino held = model.getHeldPiece();
        if (held != null) {
            drawPreviewPiece(g, held, PANEL_X, y);
        }
    }

    /**
     * Draws a preview of a tetromino.
     *
     * @param g The graphics context
     * @param piece The piece to preview
     * @param x The X position
     * @param y The Y position
     */
    private void drawPreviewPiece(final Graphics2D g, final Tetromino piece,
                                  final int x, final int y) {
        final int[][] shape = piece.getShape();
        final Color color = piece.getColor();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    final int cellX = x + col * PREVIEW_CELL_SIZE;
                    final int cellY = y + row * PREVIEW_CELL_SIZE;

                    g.setColor(color);
                    g.fillRect(cellX, cellY, PREVIEW_CELL_SIZE - 1, PREVIEW_CELL_SIZE - 1);

                    g.setColor(color.brighter());
                    g.drawLine(cellX, cellY, cellX + PREVIEW_CELL_SIZE - 2, cellY);
                    g.drawLine(cellX, cellY, cellX, cellY + PREVIEW_CELL_SIZE - 2);

                    g.setColor(color.darker());
                    g.drawLine(cellX + PREVIEW_CELL_SIZE - 2, cellY + 1,
                            cellX + PREVIEW_CELL_SIZE - 2, cellY + PREVIEW_CELL_SIZE - 2);
                    g.drawLine(cellX + 1, cellY + PREVIEW_CELL_SIZE - 2,
                            cellX + PREVIEW_CELL_SIZE - 2, cellY + PREVIEW_CELL_SIZE - 2);
                }
            }
        }
    }

    /**
     * Renders the pause overlay.
     *
     * @param g The graphics context
     */
    private void renderPauseOverlay(final Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Pause text
        g.setColor(Color.WHITE);
        g.setFont(TITLE_FONT);
        final String text = "PAUSED";
        final int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        final int y = WINDOW_HEIGHT / 2;
        g.drawString(text, x, y);

        g.setFont(INFO_FONT);
        final String hint = "Press P to resume";
        final int hintX = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(hint)) / 2;
        g.drawString(hint, hintX, y + PAUSE_TEXT_OFFSET);
    }

    /**
     * Renders the game over overlay.
     *
     * @param g The graphics context
     * @param model The game model
     */
    private void renderGameOverOverlay(final Graphics2D g, final GameModel model) {
        // Semi-transparent overlay
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Game over text
        g.setColor(Color.RED);
        g.setFont(TITLE_FONT);
        String text = "GAME OVER";
        int x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        int y = WINDOW_HEIGHT / 2 - GAME_OVER_Y_OFFSET;
        g.drawString(text, x, y);

        // Final score
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        text = String.format("Final Score: %,d", model.getScore());
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        y += GAME_OVER_Y_OFFSET;
        g.drawString(text, x, y);

        // Instructions
        g.setFont(INFO_FONT);
        text = "Press ENTER to play again";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        y += SECTION_SPACING;
        g.drawString(text, x, y);

        text = "Press ESC to return to menu";
        x = (WINDOW_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        y += LINE_HEIGHT;
        g.drawString(text, x, y);
    }

    /**
     * Gets the canvas component.
     *
     * @return The canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Gets the window width.
     *
     * @return The width
     */
    public int getWidth() {
        return WINDOW_WIDTH;
    }

    /**
     * Gets the window height.
     *
     * @return The height
     */
    public int getHeight() {
        return WINDOW_HEIGHT;
    }
}
