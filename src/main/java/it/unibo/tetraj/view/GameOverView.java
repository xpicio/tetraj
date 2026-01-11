package it.unibo.tetraj.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.model.GameOverModel;
import it.unibo.tetraj.util.ApplicationProperties;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

/** View for the game over state. Simple implementation showing "GAME OVER" and game statistics. */
public class GameOverView {

  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final float BACKGROUND_OVERLAY_ALPHA = 0.80f;
  private static final Color TITLE_TEXT_COLOR = new Color(220, 40, 40);
  private static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  private static final float TITLE_FONT_SIZE = 48f;
  private static final float DEFAULT_FONT_SIZE = 18f;
  private final ApplicationProperties applicationProperties;
  private final Canvas canvas;
  private final int windowWidth;
  private final int windowHeight;
  private BufferStrategy bufferStrategy;
  private Font titleFont;
  private Font defaulFont;

  /** Creates a new game over view. */
  public GameOverView() {
    applicationProperties = ApplicationProperties.getInstance();
    windowWidth = applicationProperties.getWindowWidth();
    windowHeight = applicationProperties.getWindowHeight();
    canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(windowWidth, windowHeight));
    canvas.setBackground(BACKGROUND_COLOR);
    canvas.setFocusable(true);
    preloadResources();
  }

  /** Initializes the buffer strategy. */
  public void initialize() {
    if (bufferStrategy == null) {
      canvas.createBufferStrategy(3);
      bufferStrategy = canvas.getBufferStrategy();
    }
  }

  /**
   * Renders the game over view.
   *
   * @param model The game over model containing game statistics and background
   */
  public void render(final GameOverModel model) {
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
          // Draw background image if exist
          final BufferedImage backgroundImage = model.getBackgroundImage();
          if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
            RenderUtils.drawOverlay(g, windowWidth, windowHeight, BACKGROUND_OVERLAY_ALPHA);
          }
          // Set defaults
          g.setColor(DEFAULT_TEXT_COLOR);
          g.setFont(defaulFont);
          // Render text
          RenderUtils.drawCenteredTextBlock(
              g,
              Stream.concat(Stream.of("GAME OVER"), model.getGameOverStats().stream()).toList(),
              titleFont,
              TITLE_TEXT_COLOR,
              windowWidth,
              windowHeight);
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

  /** Preloads all resources needed for the view. */
  private void preloadResources() {
    final ResourceManager resources = ResourceManager.getInstance();
    // Load fonts
    titleFont = resources.getPressStart2PFont(TITLE_FONT_SIZE);
    defaulFont = resources.getPressStart2PFont(DEFAULT_FONT_SIZE);
  }
}
