package it.unibo.tetraj.view;

import it.unibo.tetraj.model.MenuModel;
import it.unibo.tetraj.model.MenuModel.Controls;
import it.unibo.tetraj.model.MenuModel.Credits;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

/** View for the main menu state. Handles rendering with custom graphics and fonts. */
public final class MenuView {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final float BG_OVERLAY_ALPHA = 0.80f;
  private static final float TITLE_SIZE = 72f;
  private static final float HEADER_SIZE = 24f;
  private static final float TEXT_SIZE = 18f;
  private static final float CREDITS_SIZE = 12f;
  private static final int LINE_HEIGHT = 25;
  private static final int SECTION_SPACING = 40;
  private static final int TITLE_Y = 120;
  private static final int CONTROLS_Y = 220;
  private static final int CREDITS_BOTTOM_OFFSET = 40;
  private static final int CREDITS_LINE_HEIGHT = 15;
  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final Color HEADER_COLOR = new Color(255, 220, 100);
  private static final Color CREDITS_COLOR = new Color(200, 200, 200);
  private final Canvas canvas;
  private BufferStrategy bufferStrategy;
  private Image backgroundImage;
  private Font titleFont;
  private Font headerFont;
  private Font textFont;
  private Font creditsFont;

  /** Creates a new menu view. */
  public MenuView() {
    this.canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    canvas.setBackground(BACKGROUND_COLOR);
    canvas.setFocusable(true);
    preloadResources();
  }

  /** Initializes the buffer strategy. Must be called after canvas is added to window. */
  public void initialize() {
    if (bufferStrategy == null) {
      canvas.createBufferStrategy(3);
      bufferStrategy = canvas.getBufferStrategy();
    }
  }

  /**
   * Renders the menu view.
   *
   * @param model The menu model containing data to render
   */
  public void render(final MenuModel model) {
    if (bufferStrategy == null) {
      initialize();
      if (bufferStrategy == null) {
        return;
      }
    }

    Graphics2D g = null;
    try {
      g = (Graphics2D) bufferStrategy.getDrawGraphics();

      // Enable anti-aliasing for smooth text
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // Clear screen
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, WIDTH, HEIGHT);

      // Draw background image if loaded
      if (backgroundImage != null) {
        // Calculate scale factor to cover the entire screen (like CSS: background-size: cover)
        // Use Math.max to ensure the image fills the largest dimension
        final int imgWidth = backgroundImage.getWidth(null);
        final int imgHeight = backgroundImage.getHeight(null);
        final double scale = Math.max((double) WIDTH / imgWidth, (double) HEIGHT / imgHeight);
        // Calculate new dimensions keeping aspect ratio
        final int newWidth = (int) (imgWidth * scale);
        final int newHeight = (int) (imgHeight * scale);
        final int x = (WIDTH - newWidth) / 2;
        final int y = HEIGHT - newHeight;
        g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);

        // Add semi-transparent overlay for text readability
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BG_OVERLAY_ALPHA));
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      }

      // Draw menu content
      drawTitle(g, model);
      drawControls(g, model);
      drawCredits(g, model);

      bufferStrategy.show();
    } finally {
      if (g != null) {
        g.dispose();
      }
    }
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
    return WIDTH;
  }

  /**
   * Gets the window height.
   *
   * @return The height
   */
  public int getHeight() {
    return HEIGHT;
  }

  /** Preloads all resources needed for the menu. */
  private void preloadResources() {
    final ResourceManager resources = ResourceManager.getInstance();

    // Load background image
    backgroundImage = resources.loadImage("splashScreenBackground.png");

    // Load fonts
    titleFont = resources.getPressStart2PFont(TITLE_SIZE);
    headerFont = resources.getPressStart2PFont(HEADER_SIZE);
    textFont = resources.getPressStart2PFont(TEXT_SIZE);
    creditsFont = resources.getPressStart2PFont(CREDITS_SIZE);
  }

  /**
   * Draws the game title.
   *
   * @param g The graphics context
   * @param model The menu model containing the title
   */
  private void drawTitle(final Graphics2D g, final MenuModel model) {
    final String appTitle = model.getAppTitle();
    g.setColor(HEADER_COLOR);
    g.setFont(titleFont);
    drawCenteredString(g, appTitle.toUpperCase(java.util.Locale.ROOT), TITLE_Y);
  }

  /**
   * Draws the controls section.
   *
   * @param g The graphics context
   * @param model The menu model containing controls data
   */
  private void drawControls(final Graphics2D g, final MenuModel model) {
    final Controls controls = model.getControls();
    int y = CONTROLS_Y;

    // Controls header
    g.setColor(HEADER_COLOR);
    g.setFont(headerFont);
    drawCenteredString(g, controls.header(), y);

    y += SECTION_SPACING;

    // Controls section
    g.setFont(textFont);
    drawCenteredString(g, controls.sectionTitle(), y);

    y += LINE_HEIGHT + 10;

    g.setColor(TEXT_COLOR);
    g.setFont(textFont);

    // Movement controls
    for (final Controls.ControlBinding binding : controls.movements()) {
      drawCenteredString(g, binding.toString(), y);
      y += LINE_HEIGHT;
    }

    // Action controls
    y += LINE_HEIGHT + 10;
    for (final Controls.ControlBinding binding : controls.actions()) {
      drawCenteredString(g, binding.toString(), y);
      y += LINE_HEIGHT;
    }
  }

  /**
   * Draws the credits section.
   *
   * @param g The graphics context
   * @param model The menu model containing credits data
   */
  private void drawCredits(final Graphics2D g, final MenuModel model) {
    final Credits credits = model.getCredits();
    int y = HEIGHT - CREDITS_BOTTOM_OFFSET;

    g.setColor(CREDITS_COLOR);
    g.setFont(creditsFont);
    // First line of credits
    drawCenteredString(g, credits.getFirstLine(), y);
    // Second line of credits
    y += CREDITS_LINE_HEIGHT;
    drawCenteredString(g, credits.getSecondLine(), y);
  }

  private void drawCenteredString(final Graphics2D g, final String text, final int y) {
    final int x = (WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
    g.drawString(text, x, y);
  }
}
