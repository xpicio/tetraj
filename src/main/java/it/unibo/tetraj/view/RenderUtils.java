package it.unibo.tetraj.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for common rendering operations in views. Provides methods for text rendering,
 * centering, and overlay effects.
 */
public final class RenderUtils {

  private static final int DEFAULT_ROW_SPACING = 20;
  private static final int TITLE_ROW_SPACING = 30;
  private static final float MIN_ALPHA = 0.0f;
  private static final float MAX_ALPHA = 1.0f;

  /** Private constructor to prevent instantiation. */
  private RenderUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Draws a vertically and horizontally centered text block with custom title color. Supports an
   * optional title font and color for the first line.
   *
   * @param g The graphics context
   * @param lines The lines of text to draw
   * @param titleFont Font for the first line, null for uniform font
   * @param titleFontColor Color for the title text
   * @param canvasWidth The width of the canvas for centering
   * @param canvasHeight The height of the canvas for centering
   */
  public static void drawCenteredTextBlock(
      final Graphics2D g,
      final List<String> lines,
      final Font titleFont,
      final Color titleFontColor,
      final int canvasWidth,
      final int canvasHeight) {
    if (lines.isEmpty()) {
      return;
    }

    final Font originalFont = g.getFont();
    final boolean hasTitle = titleFont != null;
    // Calculate metrics for both fonts
    final FontMetrics titleMetrics = hasTitle ? g.getFontMetrics(titleFont) : null;
    final FontMetrics textMetrics = g.getFontMetrics(originalFont);
    // Calculate total height
    final int rowSpacing = hasTitle ? TITLE_ROW_SPACING : DEFAULT_ROW_SPACING;
    final int totalHeight =
        calculateTotalHeight(lines, hasTitle, titleMetrics, textMetrics, rowSpacing);
    // Calculate starting Y position for vertical centering
    final int centerY = canvasHeight / 2;
    final int startY = centerY - totalHeight / 2;

    // Draw each line
    drawLines(
        g,
        titleFont,
        titleFontColor,
        originalFont,
        hasTitle,
        startY,
        rowSpacing,
        canvasWidth,
        lines);
    // Restore original font
    g.setFont(originalFont);
  }

  /**
   * Draws a vertically and horizontally centered text block. Supports an optional title font for
   * the first line.
   *
   * @param g The graphics context
   * @param lines The lines of text to draw
   * @param titleFont Font for the first line, null for uniform font
   * @param canvasWidth The width of the canvas for centering
   * @param canvasHeight The height of the canvas for centering
   */
  public static void drawCenteredTextBlock(
      final Graphics2D g,
      final List<String> lines,
      final Font titleFont,
      final int canvasWidth,
      final int canvasHeight) {
    drawCenteredTextBlock(g, lines, titleFont, g.getColor(), canvasWidth, canvasHeight);
  }

  /**
   * Draws a single horizontally centered string.
   *
   * @param g The graphics context
   * @param canvasWidth The width of the canvas for centering
   * @param y The Y coordinate for the baseline
   * @param text The text to draw
   */
  public static void drawCenteredString(
      final Graphics2D g, final int canvasWidth, final int y, final String text) {
    final FontMetrics fm = g.getFontMetrics();
    final int x = (canvasWidth - fm.stringWidth(text)) / 2;
    g.drawString(text, x, y);
  }

  /**
   * Draws a background image scaled to cover the entire canvas with a semi-transparent overlay. The
   * image is scaled proportionally to fill the canvas (like CSS background-size: cover).
   *
   * @param g The graphics context
   * @param backgroundImage The background image to draw
   * @param windowWidth The width of the canvas
   * @param windowHeight The height of the canvas
   * @param overlayAlpha The transparency of the overlay (0-1, where 0 is transparent and 1 is
   *     opaque)
   */
  public static void drawBackgroundWithOverlay(
      final Graphics2D g,
      final Image backgroundImage,
      final int windowWidth,
      final int windowHeight,
      final float overlayAlpha) {
    if (backgroundImage == null) {
      return;
    }

    // Calculate scale factor to cover the entire screen (like CSS: background-size: cover)
    final int imgWidth = backgroundImage.getWidth(null);
    final int imgHeight = backgroundImage.getHeight(null);
    final double scale =
        Math.max((double) windowWidth / imgWidth, (double) windowHeight / imgHeight);
    // Calculate new dimensions keeping aspect ratio
    final int newWidth = (int) (imgWidth * scale);
    final int newHeight = (int) (imgHeight * scale);
    final int x = (windowWidth - newWidth) / 2;
    final int y = windowHeight - newHeight;
    final Composite originalComposite = g.getComposite();

    // Draw the background image
    g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);
    // Add semi-transparent overlay for text readability
    g.setComposite(
        AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, Math.min(MAX_ALPHA, Math.max(MIN_ALPHA, overlayAlpha))));
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, windowWidth, windowHeight);
    g.setComposite(originalComposite);
  }

  /**
   * Creates a semi-transparent overlay effect using AlphaComposite. Useful for pause screens,
   * modals, etc.
   *
   * @param g The graphics context
   * @param width The width of the overlay
   * @param height The height of the overlay
   * @param alpha The transparency (0-1, where 0 is transparent and 1 is opaque)
   */
  public static void drawOverlay(
      final Graphics2D g, final int width, final int height, final float alpha) {
    final Composite originalComposite = g.getComposite();
    g.setComposite(
        AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, Math.min(MAX_ALPHA, Math.max(MIN_ALPHA, alpha))));
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    g.setComposite(originalComposite);
  }

  /**
   * Executes a rendering operation with proper setup and cleanup. Handles graphics preparation,
   * antialiasing, background clearing, and disposal.
   *
   * @param bufferStrategy The buffer strategy to use
   * @param backgroundColor The background color
   * @param width Canvas width
   * @param height Canvas height
   * @param renderAction The rendering code to execute
   */
  public static void renderWithGraphics(
      final BufferStrategy bufferStrategy,
      final Color backgroundColor,
      final int width,
      final int height,
      final Consumer<Graphics2D> renderAction) {
    if (bufferStrategy == null) {
      return;
    }

    Graphics2D g = null;
    try {
      g = (Graphics2D) bufferStrategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setColor(backgroundColor);
      g.fillRect(0, 0, width, height);

      renderAction.accept(g);

      bufferStrategy.show();
    } finally {
      if (g != null) {
        g.dispose();
      }
    }
  }

  /**
   * Draws centered text block positioned from the bottom of the canvas. Useful for footers and
   * bottom-aligned content. The last line's baseline will be positioned at exactly canvasHeight -
   * bottomOffset.
   *
   * @param g The graphics context
   * @param lines The lines of text to draw
   * @param font The font to use for all lines
   * @param color The text color
   * @param canvasWidth The width of the canvas
   * @param canvasHeight The height of the canvas
   * @param bottomOffset The offset in pixels from the bottom of the canvas to the last line's
   *     baseline
   */
  public static void drawCenteredTextBlockFromBottom(
      final Graphics2D g,
      final List<String> lines,
      final Font font,
      final Color color,
      final int canvasWidth,
      final int canvasHeight,
      final int bottomOffset) {
    if (lines.isEmpty()) {
      return;
    }

    // Calculate positioning: last line's baseline should be at canvasHeight - bottomOffset
    final Font originalFont = g.getFont();
    final FontMetrics metrics = g.getFontMetrics();
    final int lineHeight = metrics.getHeight();
    // Start position for first line's baseline, working backwards from where last line should be
    final int startY =
        canvasHeight - bottomOffset - ((lines.size() - 1) * (lineHeight + DEFAULT_ROW_SPACING));

    g.setColor(color);
    // Draw each line
    drawLines(g, null, null, font, false, startY, DEFAULT_ROW_SPACING, canvasWidth, lines);
    // Restore original font
    g.setFont(originalFont);
  }

  /**
   * Calculates the total height needed for a text block. Takes into account different font sizes
   * for title and text lines, as well as spacing between lines.
   *
   * @param lines The lines of text to measure
   * @param hasTitle Whether the first line should be treated as a title
   * @param titleMetrics Font metrics for the title font, can be null if no title
   * @param textMetrics Font metrics for regular text lines
   * @param rowSpacing Spacing in pixels between rows
   * @return The total height in pixels needed to render all lines
   */
  private static int calculateTotalHeight(
      final List<String> lines,
      final boolean hasTitle,
      final FontMetrics titleMetrics,
      final FontMetrics textMetrics,
      final int rowSpacing) {
    if (lines.isEmpty()) {
      return 0;
    }

    int height = 0;

    if (hasTitle && titleMetrics != null) {
      // Title line height
      height += titleMetrics.getHeight();
      // Remaining lines
      if (lines.size() > 1) {
        height += rowSpacing; // Space after title
        height += (lines.size() - 1) * textMetrics.getHeight();
        height += (lines.size() - 2) * DEFAULT_ROW_SPACING; // Spaces between text lines
      }
    } else {
      // All lines with same font
      height = lines.size() * textMetrics.getHeight();
      if (lines.size() > 1) {
        height += (lines.size() - 1) * rowSpacing;
      }
    }
    return height;
  }

  /**
   * Draws the lines with appropriate fonts and spacing.
   *
   * @param g The graphics context
   * @param titleFont Font for the title (first line)
   * @param titleFontColor Color for the title text
   * @param textFont Font for regular text
   * @param hasTitle Whether to use title font for first line
   * @param startY Starting Y coordinate
   * @param rowSpacing Spacing between rows
   * @param canvasWidth Width of the canvas for centering
   * @param lines The lines of text to draw
   */
  private static void drawLines(
      final Graphics2D g,
      final Font titleFont,
      final Color titleFontColor,
      final Font textFont,
      final boolean hasTitle,
      final int startY,
      final int rowSpacing,
      final int canvasWidth,
      final List<String> lines) {
    final Color originalFontColor = g.getColor();
    int currentY = startY;

    for (int i = 0; i < lines.size(); i++) {
      final boolean isTitle = hasTitle && i == 0;

      // Set appropriate font
      if (isTitle) {
        g.setColor(titleFontColor);
        g.setFont(titleFont);
      } else {
        g.setColor(originalFontColor);
        g.setFont(textFont);
      }

      // Get metrics for current font
      final FontMetrics metrics = g.getFontMetrics();

      // Position and draw the line
      currentY += metrics.getAscent();
      drawCenteredString(g, canvasWidth, currentY, lines.get(i));
      currentY += metrics.getDescent();
      // Add spacing for next line (if not last)
      if (i < lines.size() - 1) {
        currentY += isTitle ? rowSpacing : DEFAULT_ROW_SPACING;
      }
    }
  }
}
