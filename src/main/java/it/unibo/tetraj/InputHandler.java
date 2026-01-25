package it.unibo.tetraj;

import it.unibo.tetraj.command.Command;
import it.unibo.tetraj.util.Logger;
import it.unibo.tetraj.util.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles input mapping and command execution. Maps key codes to commands for decoupled input
 * handling.
 */
public class InputHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(InputHandler.class);
  private final Map<Integer, Command> keyPressBindings;
  private final Map<Integer, Command> keyReleaseBindings;

  /** Creates a new input handler. */
  public InputHandler() {
    keyPressBindings = new HashMap<>();
    keyReleaseBindings = new HashMap<>();
  }

  /**
   * Binds a key press to a command.
   *
   * @param keyCode The key code
   * @param command The command to execute on key press
   */
  public void bindKey(final int keyCode, final Command command) {
    keyPressBindings.put(keyCode, command);
    LOGGER.info("Bound key press {} to command {}", keyCode, command.getClass().getSimpleName());
  }

  /**
   * Binds a key release to a command.
   *
   * @param keyCode The key code
   * @param command The command to execute on key release
   */
  public void bindKeyRelease(final int keyCode, final Command command) {
    keyReleaseBindings.put(keyCode, command);
    LOGGER.info("Bound key release {} to command {}", keyCode, command.getClass().getSimpleName());
  }

  /**
   * Unbinds a key press.
   *
   * @param keyCode The key code to unbind
   */
  public void unbindKey(final int keyCode) {
    keyPressBindings.remove(keyCode);
    LOGGER.info("Unbound key press {}", keyCode);
  }

  /**
   * Unbinds a key release.
   *
   * @param keyCode The key code to unbind
   */
  public void unbindKeyRelease(final int keyCode) {
    keyReleaseBindings.remove(keyCode);
    LOGGER.info("Unbound key release {}", keyCode);
  }

  /** Clears all key bindings (both press and release). */
  public void clearBindings() {
    keyPressBindings.clear();
    keyReleaseBindings.clear();
    LOGGER.info("Cleared all key bindings");
  }

  /**
   * Handles a key press by executing the bound command.
   *
   * @param keyCode The key code of the pressed key
   * @return true if a command was executed
   */
  public boolean handleKeyPress(final int keyCode) {
    final Command command = keyPressBindings.get(keyCode);
    if (command != null) {
      command.execute();
      LOGGER.info("Executed press command for key {}", keyCode);
      return true;
    }
    return false;
  }

  /**
   * Handles a key release by executing the bound command.
   *
   * @param keyCode The key code of the released key
   * @return true if a command was executed
   */
  public boolean handleKeyRelease(final int keyCode) {
    final Command command = keyReleaseBindings.get(keyCode);
    if (command != null) {
      command.execute();
      LOGGER.info("Executed release command for key {}", keyCode);
      return true;
    }
    return false;
  }

  /**
   * Checks if a key has a press binding.
   *
   * @param keyCode The key code
   * @return true if the key has a press binding
   */
  public boolean hasBinding(final int keyCode) {
    return keyPressBindings.containsKey(keyCode);
  }

  /**
   * Checks if a key has a release binding.
   *
   * @param keyCode The key code
   * @return true if the key has a release binding
   */
  public boolean hasReleaseBinding(final int keyCode) {
    return keyReleaseBindings.containsKey(keyCode);
  }
}
