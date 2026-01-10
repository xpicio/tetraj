package it.unibo.tetraj.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.model.Board;
import it.unibo.tetraj.model.PlayModel;
import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.List;

/** View for the playing state. Renders the Tetris game. */
public final class PlayView {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final int CELL_SIZE = 30;
  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final int FONT_SIZE = 16;
  private static final int PAUSED_TITLE_FONT_SIZE = 48;
  private static final float PAUSED_OVERLAY_ALPHA = 0.80f;
  private final Canvas canvas;
  private final ResourceManager resources;
  private BufferStrategy bufferStrategy;
  private Font gameFont;
  private BoardRenderer renderer;

  /** Creates a new play view. */
  public PlayView() {
    canvas = new Canvas();
    resources = ResourceManager.getInstance();
    canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    canvas.setBackground(BACKGROUND_COLOR);
    canvas.setFocusable(true);
    loadFonts();
  }

  private void loadFonts() {
    gameFont = resources.getPressStart2PFont(FONT_SIZE);
  }

  private void initialize() {
    if (bufferStrategy == null) {
      canvas.createBufferStrategy(3);
      bufferStrategy = canvas.getBufferStrategy();
    }
  }

  /**
   * Renders the play view with the game model.
   *
   * @param model The play model to render
   */
  public void render(final PlayModel model) {
    if (bufferStrategy == null) {
      initialize();
      if (bufferStrategy == null) {
        return;
      }
    }

    // Lazy initialization of renderer with board dimensions
    if (renderer == null) {
      renderer = new BoardRenderer(model.getBoard());
    }

    Graphics2D g = null;
    try {
      g = (Graphics2D) bufferStrategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g.setColor(BACKGROUND_COLOR);
      g.fillRect(0, 0, WIDTH, HEIGHT);

      renderer.render(g, model);

      bufferStrategy.show();
    } finally {
      if (g != null) {
        g.dispose();
      }
    }
  }

  /**
   * Gets the canvas.
   *
   * @return The canvas component
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Canvas must be exposed for GameEngine to mount current view")
  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Inner class that handles all rendering with pre-calculated positions. Calculates layout once
   * based on board dimensions.
   */
  private class BoardRenderer {
    private static final int TEXT_OFFSET = 20;
    private static final int SCORE_VALUE_OFFSET = 25;
    private static final int BOX_OFFSET = 10;
    private static final int PADDING = 50;
    private static final int GAME_INFO_PANEL_WIDTH = 200;
    private static final int INFO_BLOCK_SPACING = 35;
    private static final int NEXT_HOLD_SPACING = 115;
    private static final Color BOARD_BACKGROUND_COLOR = new Color(10, 10, 15);
    private static final Color GRID_COLOR = new Color(40, 40, 50);
    private static final Color GHOST_ALPHA = new Color(255, 255, 255, 60);
    private static final Color TEXT_COLOR = Color.WHITE;
    private final int boardWidthCells;
    private final int boardHeightCells;
    private final int boardPixelWidth;
    private final int boardPixelHeight;
    private final int boardX;
    private final int boardY;
    private final int gameInfoPanelX;
    private final int nextY;
    private final int nextBoxY;
    private final int holdY;
    private final int holdBoxY;
    private final int scoreY;
    private final int levelY;
    private final int linesY;

    /**
     * Creates a renderer with pre-calculated positions.
     *
     * @param board The game board to base calculations on
     */
    BoardRenderer(final Board board) {
      // Calculate pixel dimensions for board
      boardWidthCells = board.getWidth();
      boardHeightCells = board.getHeight();
      boardPixelWidth = boardWidthCells * CELL_SIZE;
      boardPixelHeight = boardHeightCells * CELL_SIZE;

      // Calculate centered board position
      final int totalContentWidth = boardPixelWidth + PADDING + GAME_INFO_PANEL_WIDTH;
      final int contentStartX = (WIDTH - totalContentWidth) / 2;
      boardX = contentStartX;
      boardY = (HEIGHT - boardPixelHeight) / 2;

      // Game info panel position
      gameInfoPanelX = boardX + boardPixelWidth + PADDING;

      // Next and hold positions aligned with board top
      nextY = boardY + TEXT_OFFSET;
      nextBoxY = nextY + BOX_OFFSET;
      holdY = nextY + NEXT_HOLD_SPACING;
      holdBoxY = holdY + BOX_OFFSET;

      // Score/Level/Lines aligned with board bottom
      linesY = boardY + boardPixelHeight - TEXT_OFFSET;
      levelY = linesY - INFO_BLOCK_SPACING * 2;
      scoreY = levelY - INFO_BLOCK_SPACING * 2;
    }

    /**
     * Renders all game elements.
     *
     * @param g The graphics context
     * @param model The game model to render
     */
    void render(final Graphics2D g, final PlayModel model) {
      drawBoard(g, model.getBoard());
      drawGhostPiece(g, model.getGhostPiece());
      drawCurrentPiece(g, model.getCurrentPiece());
      drawNextPiece(g, model.getNextPiece());
      drawHeldPiece(g, model.getHeldPiece());
      drawGameInfo(g, model);
      drawPause(g, model);
    }

    private void drawBoard(final Graphics2D g, final Board board) {
      // Board background
      g.setColor(BOARD_BACKGROUND_COLOR);
      g.fillRect(boardX, boardY, boardPixelWidth, boardPixelHeight);

      // Grid lines
      g.setColor(GRID_COLOR);

      // Horizontal lines
      for (int row = 0; row <= boardHeightCells; row++) {
        final int y = boardY + row * CELL_SIZE;
        g.drawLine(boardX, y, boardX + boardPixelWidth, y);
      }

      // Vertical lines
      for (int col = 0; col <= boardWidthCells; col++) {
        final int x = boardX + col * CELL_SIZE;
        g.drawLine(x, boardY, x, boardY + boardPixelHeight);
      }

      // Draw placed pieces
      for (int row = 0; row < boardHeightCells; row++) {
        for (int col = 0; col < boardWidthCells; col++) {
          final Color cellColor = board.getCellColor(row, col);
          if (cellColor != null) {
            drawCell(g, boardX + col * CELL_SIZE, boardY + row * CELL_SIZE, cellColor);
          }
        }
      }
    }

    private void drawCurrentPiece(final Graphics2D g, final AbstractTetromino<?> piece) {
      if (piece == null) {
        return;
      }

      final int x = boardX + piece.getX() * CELL_SIZE;
      final int y = boardY + piece.getY() * CELL_SIZE;
      drawTetromino(g, piece, x, y, piece.getColor());
    }

    private void drawGhostPiece(final Graphics2D g, final AbstractTetromino<?> ghost) {
      if (ghost == null) {
        return;
      }

      final int x = boardX + ghost.getX() * CELL_SIZE;
      final int y = boardY + ghost.getY() * CELL_SIZE;
      drawTetromino(g, ghost, x, y, GHOST_ALPHA);
    }

    private void drawNextPiece(final Graphics2D g, final AbstractTetromino<?> next) {
      g.setColor(TEXT_COLOR);
      g.setFont(gameFont);
      g.drawString("NEXT", gameInfoPanelX, nextY);

      if (next != null) {
        drawTetrominoPreview(g, next, gameInfoPanelX, nextBoxY);
      }
    }

    private void drawHeldPiece(final Graphics2D g, final AbstractTetromino<?> held) {
      g.setColor(TEXT_COLOR);
      g.setFont(gameFont);
      g.drawString("HOLD", gameInfoPanelX, holdY);

      if (held != null) {
        drawTetrominoPreview(g, held, gameInfoPanelX, holdBoxY);
      }
    }

    private void drawTetromino(
        final Graphics2D g,
        final AbstractTetromino<?> piece,
        final int x,
        final int y,
        final Color color) {
      final int[][] shape = piece.getShape();
      for (int row = 0; row < shape.length; row++) {
        for (int col = 0; col < shape[row].length; col++) {
          if (shape[row][col] != 0) {
            drawCell(g, x + col * CELL_SIZE, y + row * CELL_SIZE, color);
          }
        }
      }
    }

    private void drawTetrominoPreview(
        final Graphics2D g, final AbstractTetromino<?> piece, final int x, final int y) {
      drawTetromino(g, piece, x, y, piece.getColor());
    }

    private void drawCell(final Graphics2D g, final int x, final int y, final Color color) {
      // Main cell body
      g.setColor(color);
      g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

      // Light edges (top and left) for 3D effect
      g.setColor(color.brighter());
      g.drawLine(x + 1, y + 1, x + CELL_SIZE - 2, y + 1);
      g.drawLine(x + 1, y + 1, x + 1, y + CELL_SIZE - 2);

      // Dark edges (bottom and right) for 3D effect
      g.setColor(color.darker());
      g.drawLine(x + CELL_SIZE - 1, y + 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1);
      g.drawLine(x + 1, y + CELL_SIZE - 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1);
    }

    private void drawGameInfo(final Graphics2D g, final PlayModel model) {
      g.setColor(TEXT_COLOR);
      g.setFont(gameFont);

      // Score
      g.drawString("SCORE", gameInfoPanelX, scoreY);
      g.drawString(String.valueOf(model.getScore()), gameInfoPanelX, scoreY + SCORE_VALUE_OFFSET);

      // Level
      g.drawString("LEVEL", gameInfoPanelX, levelY);
      g.drawString(String.valueOf(model.getLevel()), gameInfoPanelX, levelY + SCORE_VALUE_OFFSET);

      // Lines
      g.drawString("LINES", gameInfoPanelX, linesY);
      g.drawString(
          String.valueOf(model.getLinesCleared()), gameInfoPanelX, linesY + SCORE_VALUE_OFFSET);
    }

    private void drawPause(final Graphics2D g, final PlayModel model) {
      if (model.isPaused()) {
        RenderUtils.drawOverlay(g, WIDTH, HEIGHT, PAUSED_OVERLAY_ALPHA);
        g.setColor(TEXT_COLOR);
        g.setFont(gameFont);
        RenderUtils.drawCenteredTextBlock(
            g,
            List.of("PAUSED", "Press P or ESC to resume"),
            resources.getPressStart2PFont(PAUSED_TITLE_FONT_SIZE),
            WIDTH,
            HEIGHT);
      }
    }
  }
}
