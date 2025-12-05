package it.unibo.tetraj.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing Loggers. Maintains a cache of created loggers to avoid creating
 * duplicate instances.
 *
 * <p>Typical usage:
 *
 * <pre>
 * public class MyClass {
 *     private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
 *
 *     public void myMethod() {
 *         logger.info("Method executed");
 *         logger.debug("Debug details: {}", someValue);
 *     }
 * }
 * </pre>
 */
public final class LoggerFactory {

  /** Cache for already created loggers. */
  private static final Map<String, Logger> LOGGER_CACHE = new ConcurrentHashMap<>();

  /** Private constructor to prevent instantiation. */
  private LoggerFactory() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Gets a logger for the specified class.
   *
   * @param clazz The class to get a logger for
   * @return The logger for the class
   */
  public static Logger getLogger(final Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * Gets a logger with the specified name.
   *
   * @param name The logger name
   * @return The logger with the specified name
   */
  public static Logger getLogger(final String name) {
    return LOGGER_CACHE.computeIfAbsent(name, ConsoleLogger::new);
  }

  /** Clears the logger cache. Mainly useful for testing. */
  public static void clearCache() {
    LOGGER_CACHE.clear();
  }

  /**
   * Flushes all cached loggers and clears the cache. Should be called during application shutdown
   * to ensure all log messages are written.
   */
  public static void flushAll() {
    LOGGER_CACHE.values().forEach(Logger::flush);
    clearCache();
  }
}
