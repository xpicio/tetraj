package it.unibo.tetraj.view;

import it.unibo.tetraj.model.MenuModel;
import it.unibo.tetraj.model.MenuModel.Controls;
import it.unibo.tetraj.model.MenuModel.Credits;
import it.unibo.tetraj.util.ResourceManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Locale;

/** View for the main menu state. Handles rendering with custom graphics and fonts. */
public final class MenuView extends AbstractView<MenuModel> {

  private static final float BACKGROUND_OVERLAY_ALPHA = 0.80f;
  private static final Color DISPLAY_TEXT_COLOR = new Color(255, 220, 100);
  private static final Color H2_TEXT_COLOR = new Color(255, 220, 100);
  private static final Color BODY_TEXT_COLOR = Color.WHITE;
  private static final Color CAPTION_TEXT_COLOR = new Color(200, 200, 200);
  private static final int CONTROLS_SPACING = 25;
  private static final int SECTION_SPACING = 40;
  private static final int CREDITS_LINE_SPACING = 15;
  private static final int TITLE_Y = 120;
  private static final int CONTROLS_Y = 220;
  private static final int CREDITS_BOTTOM_OFFSET = 40;
  private Image backgroundImage;

  /** Creates a new menu view. */
  public MenuView() {
    super();
    preloadResources();
  }

  /** {@inheritDoc} */
  @Override
  protected void renderContent(final MenuModel model) {
    RenderUtils.renderWithGraphics(
        getBufferStrategy(),
        getBackgroundColor(),
        getWindowWidth(),
        getWindowHeight(),
        g -> {
          // Draw background image with overlay
          RenderUtils.drawBackgroundWithOverlay(
              g, backgroundImage, getWindowWidth(), getWindowHeight(), BACKGROUND_OVERLAY_ALPHA);
          // Draw menu content
          drawTitle(g, model);
          drawControls(g, model);
          drawCredits(g, model);
        });
  }

  /** Preloads all resources needed for the menu. */
  private void preloadResources() {
    final ResourceManager resources = ResourceManager.getInstance();

    // Load background image
    backgroundImage = resources.loadImage("splashScreenBackground.png");
  }

  /**
   * Draws the game title.
   *
   * @param g The graphics context
   * @param model The menu model containing the title
   */
  private void drawTitle(final Graphics2D g, final MenuModel model) {
    final String appTitle = model.getAppTitle();

    g.setColor(DISPLAY_TEXT_COLOR);
    g.setFont(getDisplayFont());
    RenderUtils.drawCenteredString(g, getWindowWidth(), TITLE_Y, appTitle.toUpperCase(Locale.ROOT));
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
    g.setColor(H2_TEXT_COLOR);
    g.setFont(getH2Font());
    RenderUtils.drawCenteredString(g, getWindowWidth(), y, controls.header());
    y += SECTION_SPACING;
    // Controls section
    g.setFont(getBodyFont());
    RenderUtils.drawCenteredString(g, getWindowWidth(), y, controls.sectionTitle());
    y += CONTROLS_SPACING + 10;
    g.setColor(BODY_TEXT_COLOR);
    g.setFont(getBodyFont());
    // Movement controls
    for (final Controls.ControlBinding binding : controls.movements()) {
      RenderUtils.drawCenteredString(g, getWindowWidth(), y, binding.toString());
      y += CONTROLS_SPACING;
    }
    // Action controls
    y += CONTROLS_SPACING + 10;
    for (final Controls.ControlBinding binding : controls.actions()) {
      RenderUtils.drawCenteredString(g, getWindowWidth(), y, binding.toString());
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
    int y = getWindowHeight() - CREDITS_BOTTOM_OFFSET;

    g.setColor(CAPTION_TEXT_COLOR);
    g.setFont(getCaptionFont());
    // First line of credits
    RenderUtils.drawCenteredString(g, getWindowWidth(), y, credits.getFirstLine());
    // Second line of credits
    y += CREDITS_LINE_SPACING;
    RenderUtils.drawCenteredString(g, getWindowWidth(), y, credits.getSecondLine());
  }
}
