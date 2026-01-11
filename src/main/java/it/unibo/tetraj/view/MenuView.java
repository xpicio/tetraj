package it.unibo.tetraj.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.model.MenuModel;
import it.unibo.tetraj.model.MenuModel.Controls;
import it.unibo.tetraj.model.MenuModel.Credits;
import it.unibo.tetraj.util.ApplicationProperties;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;

/** View for the main menu state. Handles rendering with custom graphics and fonts. */
public final class MenuView {

  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final float BACKGROUND_OVERLAY_ALPHA = 0.80f;
  private static final Color TITLE_TEXT_COLOR = new Color(255, 220, 100);
  private static final Color HEADER_TEXT_COLOR = new Color(255, 220, 100);
  private static final Color CREDITS_TEXT_COLOR = new Color(200, 200, 200);
  private static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  private static final float TITLE_FONT_SIZE = 72f;
  private static final float HEADER_FONT_SIZE = 24f;
  private static final float CREDITS_FONT_SIZE = 12f;
  private static final float DEFAULT_FONT_SIZE = 18f;
  private static final int CONTROLS_SPACING = 25;
  private static final int SECTION_SPACING = 40;
  private static final int CREDITS_LINE_SPACING = 15;
  private static final int TITLE_Y = 120;
  private static final int CONTROLS_Y = 220;
  private static final int CREDITS_BOTTOM_OFFSET = 40;
  private final ApplicationProperties applicationProperties;
  private final Canvas canvas;
  private BufferStrategy bufferStrategy;
  private Image backgroundImage;
  private Font titleFont;
  private Font headerFont;
  private Font textFont;
  private Font creditsFont;
  private final int windowWidth;
  private final int windowHeight;

  /** Creates a new menu view. */
  public MenuView() {
    applicationProperties = ApplicationProperties.getInstance();
    windowWidth = applicationProperties.getWindowWidth();
    windowHeight = applicationProperties.getWindowHeight();
    canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(windowWidth, windowHeight));
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

    RenderUtils.renderWithGraphics(
        bufferStrategy,
        BACKGROUND_COLOR,
        windowWidth,
        windowHeight,
        g -> {
          // Draw background image if loaded
          if (backgroundImage != null) {
            // Calculate scale factor to cover the entire screen (like CSS: background-size: cover)
            // Use Math.max to ensure the image fills the largest dimension
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

            g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);
            // Add semi-transparent overlay for text readability
            g.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BACKGROUND_OVERLAY_ALPHA));
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, windowWidth, windowHeight);
            g.setComposite(originalComposite);
          }
          // Draw menu content
          drawTitle(g, model);
          drawControls(g, model);
          drawCredits(g, model);
        });
  }

  /**
   * Gets the canvas component.
   *
   * @return The canvas
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "Canvas must be exposed for GameEngine to mount current view")
  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Gets the window width.
   *
   * @return The width
   */
  public int getWidth() {
    return windowWidth;
  }

  /**
   * Gets the window height.
   *
   * @return The height
   */
  public int getHeight() {
    return windowHeight;
  }

  /** Preloads all resources needed for the menu. */
  private void preloadResources() {
    final ResourceManager resources = ResourceManager.getInstance();

    // Load background image
    backgroundImage = resources.loadImage("splashScreenBackground.png");
    // Load fonts
    titleFont = resources.getPressStart2PFont(TITLE_FONT_SIZE);
    headerFont = resources.getPressStart2PFont(HEADER_FONT_SIZE);
    textFont = resources.getPressStart2PFont(DEFAULT_FONT_SIZE);
    creditsFont = resources.getPressStart2PFont(CREDITS_FONT_SIZE);
  }

  /**
   * Draws the game title.
   *
   * @param g The graphics context
   * @param model The menu model containing the title
   */
  private void drawTitle(final Graphics2D g, final MenuModel model) {
    final String appTitle = model.getAppTitle();

    g.setColor(TITLE_TEXT_COLOR);
    g.setFont(titleFont);
    RenderUtils.drawCenteredString(
        g, windowWidth, TITLE_Y, appTitle.toUpperCase(java.util.Locale.ROOT));
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
    g.setColor(HEADER_TEXT_COLOR);
    g.setFont(headerFont);
    RenderUtils.drawCenteredString(g, windowWidth, y, controls.header());
    y += SECTION_SPACING;
    // Controls section
    g.setFont(textFont);
    RenderUtils.drawCenteredString(g, windowWidth, y, controls.sectionTitle());
    y += CONTROLS_SPACING + 10;
    g.setColor(DEFAULT_TEXT_COLOR);
    g.setFont(textFont);
    // Movement controls
    for (final Controls.ControlBinding binding : controls.movements()) {
      RenderUtils.drawCenteredString(g, windowWidth, y, binding.toString());
      y += CONTROLS_SPACING;
    }
    // Action controls
    y += CONTROLS_SPACING + 10;
    for (final Controls.ControlBinding binding : controls.actions()) {
      RenderUtils.drawCenteredString(g, windowWidth, y, binding.toString());
      y += CONTROLS_SPACING;
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
    int y = windowHeight - CREDITS_BOTTOM_OFFSET;

    g.setColor(CREDITS_TEXT_COLOR);
    g.setFont(creditsFont);
    // First line of credits
    RenderUtils.drawCenteredString(g, windowWidth, y, credits.getFirstLine());
    // Second line of credits
    y += CREDITS_LINE_SPACING;
    RenderUtils.drawCenteredString(g, windowWidth, y, credits.getSecondLine());
  }
}
