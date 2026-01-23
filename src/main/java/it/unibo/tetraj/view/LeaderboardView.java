package it.unibo.tetraj.view;

import it.unibo.tetraj.model.LeaderboardModel;
import it.unibo.tetraj.model.LeaderboardModel.LeaderboardDisplayEntry;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.Locale;

/** View for the leaderboard state. Displays top scores with player information. */
public class LeaderboardView extends AbstractView<LeaderboardModel> {

  private static final float BACKGROUND_OVERLAY_ALPHA = 0.90f;
  private static final Color H1_TEXT_COLOR = new Color(255, 220, 100);
  private static final Color BODY_TEXT_COLOR = new Color(180, 180, 180);
  private static final Color CAPTION_TEXT_COLOR = Color.WHITE;
  private static final Color HIGHLIGHT_TEXT_COLOR = new Color(100, 255, 100);
  private static final int TITLE_Y_OFFSET = 120;
  private static final int HEADER_Y_OFFSET = 180;
  private static final int ENTRY_START_Y = 240;
  private static final int ENTRY_LINE_HEIGHT = 30;
  private static final int FOOTER_BOTTOM_OFFSET = 40;
  private static final int NICKNAME_MAX_LENGTH = 16;
  private static final float TABLE_WIDTH_PERCENT = 0.80f;
  private static final float[] COLUMN_WIDTHS = {0.08f, 0.30f, 0.22f, 0.12f, 0.12f, 0.16f};
  private Image backgroundImage;

  /** Creates a new leaderboard view. */
  public LeaderboardView() {
    super();
    preloadResources();
  }

  /** {@inheritDoc} */
  @Override
  protected void renderContent(final LeaderboardModel model) {
    RenderUtils.renderWithGraphics(
        getBufferStrategy(),
        getBackgroundColor(),
        getWindowWidth(),
        getWindowHeight(),
        g -> {
          // Draw background image with overlay
          RenderUtils.drawBackgroundWithOverlay(
              g, backgroundImage, getWindowWidth(), getWindowHeight(), BACKGROUND_OVERLAY_ALPHA);
          // Draw title
          drawTitle(g);
          // Draw header
          drawHeader(g);
          // Draw entries
          drawEntries(g, model.getEntries(), model.getCurrentPlayerProfileId());
          // Draw footer
          drawFooter(g);
        });
  }

  /** Preloads all resources needed for the view. */
  private void preloadResources() {
    final ResourceManager resources = ResourceManager.getInstance();

    // Load background image
    backgroundImage = resources.loadImage("splashScreenBackground.png");
  }

  /**
   * Draws the title "Block Legends" at the top.
   *
   * @param g The graphics context
   */
  private void drawTitle(final Graphics2D g) {
    final String title = "Block Legends";

    g.setColor(H1_TEXT_COLOR);
    g.setFont(getH1Font());
    RenderUtils.drawCenteredString(
        g, getWindowWidth(), TITLE_Y_OFFSET, title.toUpperCase(Locale.ROOT));
  }

  /**
   * Draws the header row with column names.
   *
   * @param g The graphics context
   */
  private void drawHeader(final Graphics2D g) {
    final String[] headers = {"RANK", "PLAYER", "SCORE", "LEVEL", "LINES", "DATE"};

    g.setColor(BODY_TEXT_COLOR);
    g.setFont(getBodyFont());
    drawTableRow(g, HEADER_Y_OFFSET, headers);
  }

  /**
   * Draws all leaderboard entries.
   *
   * @param g The graphics context
   * @param entries The list of display entries
   * @param currentPlayerId The current player's ID for highlighting
   */
  private void drawEntries(
      final Graphics2D g,
      final List<LeaderboardDisplayEntry> entries,
      final String currentPlayerId) {
    int yPosition = ENTRY_START_Y;

    g.setFont(getCaptionFont());
    for (final LeaderboardDisplayEntry entry : entries) {
      final String[] row = {
        String.valueOf(entry.rank()),
        truncate(entry.nickname(), NICKNAME_MAX_LENGTH),
        String.valueOf(entry.score()),
        String.valueOf(entry.level()),
        String.valueOf(entry.lines()),
        entry.date(),
      };

      // Highlight current player's entries
      if (entry.playerId().equals(currentPlayerId)) {
        g.setColor(HIGHLIGHT_TEXT_COLOR);
      } else {
        g.setColor(CAPTION_TEXT_COLOR);
      }
      drawTableRow(g, yPosition, row);
      yPosition += ENTRY_LINE_HEIGHT;
    }
  }

  /**
   * Draws a table row with centered text in each column.
   *
   * @param g The graphics context
   * @param y The Y position for the row baseline
   * @param values Array of strings to draw in each column
   */
  private void drawTableRow(final Graphics2D g, final int y, final String[] values) {
    final int tableWidth = (int) (getWindowWidth() * TABLE_WIDTH_PERCENT);
    final int tableStartX = (getWindowWidth() - tableWidth) / 2;
    final java.awt.FontMetrics fontMetrics = g.getFontMetrics();
    int currentX = tableStartX;

    for (int i = 0; i < values.length && i < COLUMN_WIDTHS.length; i++) {
      final int colWidth = (int) (tableWidth * COLUMN_WIDTHS[i]);
      final int textWidth = fontMetrics.stringWidth(values[i]);
      final int textX = currentX + (colWidth - textWidth) / 2;

      g.drawString(values[i], textX, y);
      currentX += colWidth;
    }
  }

  /**
   * Draws the footer with instructions.
   *
   * @param g The graphics context
   */
  private void drawFooter(final Graphics2D g) {
    final String instruction = "Press ESC to return to menu";

    g.setFont(getBodyFont());
    g.setColor(BODY_TEXT_COLOR);
    RenderUtils.drawCenteredString(
        g,
        getWindowWidth(),
        getWindowHeight() - FOOTER_BOTTOM_OFFSET,
        instruction.toUpperCase(Locale.ROOT));
  }

  /**
   * Truncates a string to a maximum length.
   *
   * @param text The string to truncate
   * @param maxLength The maximum length
   * @return The truncated string
   */
  private String truncate(final String text, final int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }

    return text.substring(0, maxLength - 3) + "...";
  }
}
