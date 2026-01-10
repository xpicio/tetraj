package it.unibo.tetraj.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * Manages application properties loaded from app.properties file. Singleton with eager
 * initialization for thread-safety and simplicity.
 */
public final class ApplicationProperties {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProperties.class);
  private static final String PROPERTIES_FILE = "/app.properties";
  private static final int DEFAULT_WINDOW_WIDTH = 800;
  private static final int DEFAULT_WINDOW_HEIGHT = 600;
  private static final ApplicationProperties INSTANCE = new ApplicationProperties();
  private final Properties properties;

  /** Private constructor loads properties once. Called only once when class is first loaded. */
  private ApplicationProperties() {
    properties = new Properties();
    loadProperties();
  }

  /**
   * Gets the singleton instance.
   *
   * @return The ApplicationProperties instance
   */
  public static ApplicationProperties getInstance() {
    return INSTANCE;
  }

  /**
   * Gets a property value.
   *
   * @param key The property key
   * @return The property value or null if not found
   */
  public String getProperty(final String key) {
    return properties.getProperty(key);
  }

  /**
   * Gets a property value with default.
   *
   * @param key The property key
   * @param defaultValue The default value if key not found
   * @return The property value or default
   */
  public String getProperty(final String key, final String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /**
   * Gets the application name.
   *
   * @return The application name
   */
  public String getAppName() {
    return properties.getProperty("app.name", "Tetraj");
  }

  /**
   * Gets the application title for window.
   *
   * @return The application title
   */
  public String getAppTitle() {
    return properties.getProperty("app.title", "Tetraj");
  }

  /**
   * Gets the application version.
   *
   * @return The application version
   */
  public String getAppVersion() {
    return properties.getProperty("app.version", "1.0.0");
  }

  /**
   * Gets the application author.
   *
   * @return The application author
   */
  public String getAuthor() {
    return properties.getProperty("author", "Patrizio Bertozzi");
  }

  /**
   * Gets the application author email.
   *
   * @return The email
   */
  public String getAuthorEmail() {
    return properties.getProperty("author.email", "default@gmail.com");
  }

  /**
   * Gets the university detail.
   *
   * @return The university detail
   */
  public String getAuthorUniversity() {
    return properties.getProperty("author.university", "Università di Bologna");
  }

  /**
   * Gets the window width.
   *
   * @return The window width
   */
  public int getWindowWidth() {
    return Optional.ofNullable(properties.getProperty("window.width"))
        .map(Integer::parseInt)
        .orElse(DEFAULT_WINDOW_WIDTH);
  }

  /**
   * Gets the window height.
   *
   * @return The window height
   */
  public int getWindowHeight() {
    return Optional.ofNullable(properties.getProperty("window.height"))
        .map(Integer::parseInt)
        .orElse(DEFAULT_WINDOW_HEIGHT);
  }

  /** Loads properties from file or sets defaults. */
  private void loadProperties() {
    try (InputStream inputStream = getClass().getResourceAsStream(PROPERTIES_FILE)) {
      if (inputStream != null) {
        // Use UTF-8 reader for proper character encoding (è, à, ù, etc.)
        try (java.io.InputStreamReader reader =
            new java.io.InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
          properties.load(reader);
          LOGGER.info("Application properties loaded from {}", PROPERTIES_FILE);
        }
      } else {
        LOGGER.warn("Properties file {} not found, using defaults", PROPERTIES_FILE);
      }
    } catch (final IOException e) {
      LOGGER.error("Failed to load properties: {}", e.getMessage(), e);
    }
  }
}
