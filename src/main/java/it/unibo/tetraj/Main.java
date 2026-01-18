package it.unibo.tetraj;

import it.unibo.tetraj.util.ApplicationProperties;
import java.util.Locale;

/**
 * Application entry point for Tetraj game.
 *
 * <p>This class serves as the main entry point for the Tetraj application, handling initial system
 * configuration before delegating to the ApplicationContext for the actual application bootstrap.
 */
public final class Main {

  /**
   * Private constructor to prevent instantiation. This is a utility class with only static methods.
   */
  private Main() {
    // Prevent instantiation
  }

  /**
   * Main entry point of the application.
   *
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    configureApplicationProperties();

    final ApplicationContext applicationContext = ApplicationContext.getInstance();
    applicationContext.bootstrap();
  }

  /** Configures application name for all platforms. */
  private static void configureApplicationProperties() {
    final String appName = ApplicationProperties.getInstance().getAppName();

    // Get OS name for platform-specific configurations
    final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

    // macOS - set app name in menu bar
    if (osName.contains("mac")) {
      System.setProperty("apple.awt.application.name", appName);
    }

    // Cross-platform anti-aliasing for better text rendering
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
  }
}
