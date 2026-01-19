package it.unibo.tetraj.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.model.Board;
import it.unibo.tetraj.model.PlayModel;
import it.unibo.tetraj.model.piece.AbstractTetromino;
import it.unibo.tetraj.util.ApplicationProperties;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;

/** View for the playing state. Renders the Tetris game. */
public final class PlayView {

  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final float BACKGROUND_OVERLAY_ALPHA = 0.80f;
  private static final float TITLE_FONT_SIZE = 48f;
  private static final float DEFAULT_FONT_SIZE = 18f;
  private static final int BOARD_CELL_SIZE = 30;
  private final ApplicationProperties applicationProperties;
  private final Canvas canvas;
  private final ResourceManager resources;
  private BufferStrategy bufferStrategy;
  private Font gameFont;
  private BoardRenderer renderer;
  private final int windowWidth;
  private final int windowHeight;

  /** Creates a new play view. */
  public PlayView() {
    applicationProperties = ApplicationProperties.getInstance();
    windowWidth = applicationProperties.getWindowWidth();
    windowHeight = applicationProperties.getWindowHeight();
    canvas = new Canvas();
    resources = ResourceManager.getInstance();
    canvas.setPreferredSize(new Dimension(windowWidth, windowHeight));
    canvas.setBackground(BACKGROUND_COLOR);
    canvas.setFocusable(true);
    loadFonts();
  }

  private void loadFonts() {
    gameFont = resources.getPressStart2PFont(DEFAULT_FONT_SIZE);
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
    RenderUtils.renderWithGraphics(
        bufferStrategy,
        BACKGROUND_COLOR,
        windowWidth,
        windowHeight,
        g -> {
          renderer.render(g, model);
        });
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
   * Captures the current game state as a BufferedImage. Creates a snapshot of the entire game view
   * including board, score, level, and next piece preview. Useful for creating game-over screens or
   * transitions.
   *
   * @param model The play model to render
   * @return A BufferedImage containing the current rendered frame
   */
  public BufferedImage captureFrame(final PlayModel model) {
    if (renderer == null) {
      renderer = new BoardRenderer(model.getBoard());
    }

    final BufferedImage image =
        new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
    final Graphics2D g = image.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Clear background
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, windowWidth, windowHeight);
    renderer.render(g, model);
    g.dispose();
    return image;
  }

  /**
   * Inner class that handles all rendering with pre-calculated positions. Calculates layout once
   * based on board dimensions.
   */
  private class BoardRenderer {
    private static final Color BOARD_BACKGROUND_COLOR = new Color(10, 10, 15);
    private static final Color GRID_COLOR = new Color(40, 40, 50);
    private static final Color GHOST_PIECE_COLOR = new Color(255, 255, 255, 60);
    private static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int TEXT_OFFSET = 20;
    private static final int SCORE_VALUE_OFFSET = 25;
    private static final int BOX_OFFSET = 10;
    private static final int PADDING = 50;
    private static final int GAME_INFO_PANEL_WIDTH = 200;
    private static final int INFO_BLOCK_SPACING = 35;
    private static final int NEXT_HOLD_SPACING = 115;
    private final Board board;
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
      this.board = board;
      // Calculate pixel dimensions for board
      boardWidthCells = board.getWidth();
      boardHeightCells = board.getHeight();
      boardPixelWidth = boardWidthCells * BOARD_CELL_SIZE;
      boardPixelHeight = boardHeightCells * BOARD_CELL_SIZE;

      // Calculate centered board position
      final int totalContentWidth = boardPixelWidth + PADDING + GAME_INFO_PANEL_WIDTH;
      final int contentStartX = (windowWidth - totalContentWidth) / 2;

      boardX = contentStartX;
      boardY = (windowHeight - boardPixelHeight) / 2;
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
      drawBoard(g);
      if (!model.isGameOver()) {
        drawGhostPiece(g, model.getGhostPiece());
        drawCurrentPiece(g, model.getCurrentPiece());
      }
      drawNextPiece(g, model.getNextPiece());
      drawHeldPiece(g, model.getHeldPiece());
      drawGameInfo(g, model);
      drawPause(g, model);
    }

    private void drawBoard(final Graphics2D g) {
      // Board background
      g.setColor(BOARD_BACKGROUND_COLOR);
      g.fillRect(boardX, boardY, boardPixelWidth, boardPixelHeight);
      // Grid lines
      g.setColor(GRID_COLOR);
      // Horizontal lines
      for (int row = 0; row <= boardHeightCells; row++) {
        final int y = boardY + row * BOARD_CELL_SIZE;
        g.drawLine(boardX, y, boardX + boardPixelWidth, y);
      }
      // Vertical lines
      for (int col = 0; col <= boardWidthCells; col++) {
        final int x = boardX + col * BOARD_CELL_SIZE;
        g.drawLine(x, boardY, x, boardY + boardPixelHeight);
      }
      // Draw placed pieces
      for (int row = 0; row < boardHeightCells; row++) {
        for (int col = 0; col < boardWidthCells; col++) {
          final Color cellColor = board.getCellColor(row, col);
          if (cellColor != null) {
            drawCell(g, boardX + col * BOARD_CELL_SIZE, boardY + row * BOARD_CELL_SIZE, cellColor);
          }
        }
      }
    }

    private void drawCurrentPiece(final Graphics2D g, final AbstractTetromino<?> piece) {
      if (piece == null) {
        return;
      }

      final int x = boardX + piece.getX() * BOARD_CELL_SIZE;
      final int y = boardY + piece.getY() * BOARD_CELL_SIZE;

      drawTetromino(g, piece, x, y, piece.getColor());
    }

    private void drawGhostPiece(final Graphics2D g, final AbstractTetromino<?> ghost) {
      if (ghost == null) {
        return;
      }

      final int x = boardX + ghost.getX() * BOARD_CELL_SIZE;
      final int y = boardY + ghost.getY() * BOARD_CELL_SIZE;

      drawTetromino(g, ghost, x, y, GHOST_PIECE_COLOR);
    }

    private void drawNextPiece(final Graphics2D g, final AbstractTetromino<?> next) {
      g.setColor(DEFAULT_TEXT_COLOR);
      g.setFont(gameFont);
      g.drawString("NEXT", gameInfoPanelX, nextY);
      if (next != null) {
        drawTetrominoPreview(g, next, gameInfoPanelX, nextBoxY);
      }
    }

    private void drawHeldPiece(final Graphics2D g, final AbstractTetromino<?> held) {
      g.setColor(DEFAULT_TEXT_COLOR);
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
            drawCell(g, x + col * BOARD_CELL_SIZE, y + row * BOARD_CELL_SIZE, color);
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
      g.fillRect(x + 1, y + 1, BOARD_CELL_SIZE - 2, BOARD_CELL_SIZE - 2);
      // Light edges (top and left) for 3D effect
      g.setColor(color.brighter());
      g.drawLine(x + 1, y + 1, x + BOARD_CELL_SIZE - 2, y + 1);
      g.drawLine(x + 1, y + 1, x + 1, y + BOARD_CELL_SIZE - 2);
      // Dark edges (bottom and right) for 3D effect
      g.setColor(color.darker());
      g.drawLine(x + BOARD_CELL_SIZE - 1, y + 1, x + BOARD_CELL_SIZE - 1, y + BOARD_CELL_SIZE - 1);
      g.drawLine(x + 1, y + BOARD_CELL_SIZE - 1, x + BOARD_CELL_SIZE - 1, y + BOARD_CELL_SIZE - 1);
    }

    private void drawGameInfo(final Graphics2D g, final PlayModel model) {
      g.setColor(DEFAULT_TEXT_COLOR);
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
        RenderUtils.drawOverlay(g, windowWidth, windowHeight, BACKGROUND_OVERLAY_ALPHA);
        g.setColor(DEFAULT_TEXT_COLOR);
        g.setFont(gameFont);
        RenderUtils.drawCenteredTextBlock(
            g,
            List.of("PAUSED", "Press P or ESC to resume"),
            resources.getPressStart2PFont(TITLE_FONT_SIZE),
            windowWidth,
            windowHeight);
      }
    }
  }
}
