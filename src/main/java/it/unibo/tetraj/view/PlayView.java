package it.unibo.tetraj.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

/** View for the playing state. Simple implementation showing "PLAYING STATE". */
public class PlayView {

  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;
  private static final Color BACKGROUND = new Color(20, 20, 30);
  private static final Color TEXT_COLOR = Color.GREEN;
  // Font sizes
  private static final int TITLE_FONT_SIZE = 48;
  private static final int INFO_FONT_SIZE = 20;
  private static final int INFO_Y_OFFSET = 50;
  private final Canvas canvas;
  private BufferStrategy bufferStrategy;

  /** Creates a new play view. */
  public PlayView() {
    this.canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    canvas.setBackground(Color.BLACK);
    canvas.setFocusable(true);
  }

  /** Initializes the buffer strategy. */
  public void initialize() {
    if (bufferStrategy == null) {
      canvas.createBufferStrategy(3);
      bufferStrategy = canvas.getBufferStrategy();
    }
  }

  /** Renders the play view. */
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
      g.setColor(BACKGROUND);
      g.fillRect(0, 0, WIDTH, HEIGHT);

      // Draw state text
      g.setColor(TEXT_COLOR);
      g.setFont(new Font("Arial", Font.BOLD, TITLE_FONT_SIZE));
      final String text = "PLAYING STATE";
      final int textWidth = g.getFontMetrics().stringWidth(text);
      g.drawString(text, (WIDTH - textWidth) / 2, HEIGHT / 2);

      // Draw instructions
      g.setFont(new Font("Arial", Font.PLAIN, INFO_FONT_SIZE));
      final String inst = "Press P to pause, ESC for menu";
      final int instWidth = g.getFontMetrics().stringWidth(inst);
      g.drawString(inst, (WIDTH - instWidth) / 2, HEIGHT / 2 + INFO_Y_OFFSET);

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
  public Canvas getCanvas() {
    return canvas;
  }
}
