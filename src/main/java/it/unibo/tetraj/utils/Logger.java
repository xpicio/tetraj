package it.unibo.tetraj.utils;

/**
 * Interface for the game's logging system.
 * Provides an abstraction layer over the concrete logging library used.
 *
 * <p>Log levels follow the common standard:
 * - TRACE: Very detailed information, typically only for debugging
 * - DEBUG: Detailed debug information
 * - INFO: Informational messages that highlight the progress of the application
 * - WARN: Potentially harmful situations
 * - ERROR: Error events that might still allow the application to continue running
 * - FATAL: Very severe error events that will presumably lead the application to abort
 */
public interface Logger {

    /**
     * Log at TRACE level.
     *
     * @param message The message to log
     */
    void trace(String message);

    /**
     * Log at TRACE level with formatted parameters.
     *
     * @param format Format string (SLF4J style with {})
     * @param args Arguments for formatting
     */
    void trace(String format, Object... args);

    /**
     * Log at DEBUG level.
     *
     * @param message The message to log
     */
    void debug(String message);

    /**
     * Log at DEBUG level with formatted parameters.
     *
     * @param format Format string
     * @param args Arguments for formatting
     */
    void debug(String format, Object... args);

    /**
     * Log at INFO level.
     *
     * @param message The message to log
     */
    void info(String message);

    /**
     * Log at INFO level with formatted parameters.
     *
     * @param format Format string
     * @param args Arguments for formatting
     */
    void info(String format, Object... args);

    /**
     * Log at WARN level.
     *
     * @param message The message to log
     */
    void warn(String message);

    /**
     * Log at WARN level with formatted parameters.
     *
     * @param format Format string
     * @param args Arguments for formatting
     */
    void warn(String format, Object... args);

    /**
     * Log at WARN level with exception.
     *
     * @param message The message to log
     * @param throwable The exception to log
     */
    void warn(String message, Throwable throwable);

    /**
     * Log at ERROR level.
     *
     * @param message The message to log
     */
    void error(String message);

    /**
     * Log at ERROR level with formatted parameters.
     *
     * @param format Format string
     * @param args Arguments for formatting
     */
    void error(String format, Object... args);

    /**
     * Log at ERROR level with exception.
     *
     * @param message The message to log
     * @param throwable The exception to log
     */
    void error(String message, Throwable throwable);

    /**
     * Log at FATAL level.
     *
     * @param message The message to log
     */
    void fatal(String message);

    /**
     * Log at FATAL level with exception.
     *
     * @param message The message to log
     * @param throwable The exception to log
     */
    void fatal(String message, Throwable throwable);

    /**
     * Check if TRACE level is enabled.
     *
     * @return true if TRACE level is enabled
     */
    boolean isTraceEnabled();

    /**
     * Check if DEBUG level is enabled.
     *
     * @return true if DEBUG level is enabled
     */
    boolean isDebugEnabled();

    /**
     * Check if INFO level is enabled.
     *
     * @return true if INFO level is enabled
     */
    boolean isInfoEnabled();

    /**
     * Check if WARN level is enabled.
     *
     * @return true if WARN level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Check if ERROR level is enabled.
     *
     * @return true if ERROR level is enabled
     */
    boolean isErrorEnabled();

    /**
     * Get the logger name.
     *
     * @return The logger name (typically the class name)
     */
    String getName();
}
