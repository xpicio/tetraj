package it.unibo.tetraj.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.tetraj.util.ApplicationProperties;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

/** View for the game over state. Simple implementation showing "GAME OVER". */
public class GameOverView {

  private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
  private static final Color TEXT_COLOR = Color.RED;
  private static final int TITLE_FONT_SIZE = 48;
  private static final int INFO_FONT_SIZE = 20;
  private static final int INFO_Y_OFFSET = 50;
  private final ApplicationProperties applicationProperties;
  private final Canvas canvas;
  private BufferStrategy bufferStrategy;
  private final int windowWidth;
  private final int windowHeight;

  /** Creates a new game over view. */
  public GameOverView() {
    applicationProperties = ApplicationProperties.getInstance();
    windowWidth = applicationProperties.getWindowWidth();
    windowHeight = applicationProperties.getWindowHeight();
    canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(windowWidth, windowHeight));
    canvas.setBackground(BACKGROUND_COLOR);
    canvas.setFocusable(true);
  }

  /** Initializes the buffer strategy. */
  public void initialize() {
    if (bufferStrategy == null) {
      canvas.createBufferStrategy(3);
      bufferStrategy = canvas.getBufferStrategy();
    }
  }

  /** Renders the game over view. */
  public void render() {
    if (bufferStrategy == null) {
      initialize();
      if (bufferStrategy == null) {
        return;
      }
    }

    Graphics2D g = null;
    try {
      g = (Graphics2D) bufferStrategy.getDrawGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // Clear screen
      g.setColor(BACKGROUND_COLOR);
      g.fillRect(0, 0, windowWidth, windowHeight);

      // Draw state text
      g.setColor(TEXT_COLOR);
      g.setFont(new Font("Arial", Font.BOLD, TITLE_FONT_SIZE));
      final String text = "GAME OVER";
      final int textWidth = g.getFontMetrics().stringWidth(text);
      g.drawString(text, (windowWidth - textWidth) / 2, windowHeight / 2);

      // Draw instructions
      g.setFont(new Font("Arial", Font.PLAIN, INFO_FONT_SIZE));
      final String inst = "Press ENTER to restart, ESC for menu";
      final int instWidth = g.getFontMetrics().stringWidth(inst);
      g.drawString(inst, (windowWidth - instWidth) / 2, windowHeight / 2 + INFO_Y_OFFSET);

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
}
