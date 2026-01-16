package it.unibo.tetraj.model;

import it.unibo.tetraj.model.leaderboard.PlayerProfile;
import it.unibo.tetraj.model.leaderboard.PlayerProfileManager;
import it.unibo.tetraj.util.ApplicationProperties;
import java.util.List;

/**
 * Model for the main menu state. Contains all data needed for menu display including title, credits
 * and controls.
 */
public final class MenuModel {

  private final String title;
  private final Credits credits;
  private final Controls controls;

  /** Creates a new menu model. */
  public MenuModel() {
    final ApplicationProperties props = ApplicationProperties.getInstance();
    final PlayerProfile playerProfile = PlayerProfileManager.getInstance().getProfile();

    title = props.getAppTitle();
    credits = new Credits(props.getAuthor(), props.getAuthorEmail(), props.getAuthorUniversity());
    controls =
        new Controls(
            String.format("%s PRESS ENTER TO START", playerProfile.nickname()),
            "CONTROLS",
            List.of(
                new Controls.ControlBinding("A/D or ← →", "Move"),
                new Controls.ControlBinding("W or ↑", "Rotate clockwise"),
                new Controls.ControlBinding("Z or CTRL", "Rotate counterclockwise"),
                new Controls.ControlBinding("C or SHIFT", "Hold piece"),
                new Controls.ControlBinding("S or ↓", "Soft Drop"),
                new Controls.ControlBinding("SPACE", "Hard Drop")),
            List.of(
                new Controls.ControlBinding("P", "Pause"),
                new Controls.ControlBinding("ESC", "Back to menu or quit")));
  }

  /**
   * Gets the application title.
   *
   * @return The application title
   */
  public String getAppTitle() {
    return title;
  }

  /**
   * Gets the credits information.
   *
   * @return The credits
   */
  public Credits getCredits() {
    return credits;
  }

  /**
   * Gets the controls information.
   *
   * @return The controls
   */
  public Controls getControls() {
    return controls;
  }

  /**
   * Credits information record. Encapsulates author details for the menu display.
   *
   * @param author The author name
   * @param email The author email
   * @param university The university name
   */
  public record Credits(String author, String email, String university) {

    /**
     * Gets the first line of credits (author - email).
     *
     * @return The formatted first line
     */
    public String getFirstLine() {
      return String.format("%s - %s", author, email);
    }

    /**
     * Gets the second line of credits (university).
     *
     * @return The university line
     */
    public String getSecondLine() {
      return university;
    }

    /**
     * Gets all credit lines as a list.
     *
     * @return List of formatted credit lines
     */
    public List<String> getLines() {
      return List.of(getFirstLine(), getSecondLine());
    }
  }

  /**
   * Controls information for the game. Encapsulates all control key bindings and descriptions.
   *
   * @param header The header text
   * @param sectionTitle The section title
   * @param movements The movement controls
   * @param actions The action controls
   */
  public record Controls(
      String header,
      String sectionTitle,
      List<ControlBinding> movements,
      List<ControlBinding> actions) {

    /**
     * Compact constructor with defensive copies. Uses {@code List.copyOf()} to satisfy SpotBugs
     * warnings, though lists created with {@code List.of()} are already immutable. No performance
     * impact as {@code List.copyOf()} returns the same instance for immutable lists.
     *
     * @param header The header text
     * @param sectionTitle The section title
     * @param movements The movement controls
     * @param actions The action controls
     */
    public Controls {
      movements = List.copyOf(movements);
      actions = List.copyOf(actions);
    }

    /**
     * Single control binding.
     *
     * @param keys The key combination
     * @param description The action description
     */
    public record ControlBinding(String keys, String description) {

      /** {@inheritDoc} */
      @Override
      public String toString() {
        return String.format("%s - %s", keys, description);
      }
    }
  }
}
