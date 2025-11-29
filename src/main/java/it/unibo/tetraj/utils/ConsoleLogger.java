package it.unibo.tetraj.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.ParameterizedMessage;

/**
 * Logger implementation based on Log4j2 that logs to the console.
 * This class wraps a Log4j2 logger providing a unified interface.
 */
public final class ConsoleLogger implements Logger {

    private final org.apache.logging.log4j.Logger log4jLogger;
    private final String name;

    /**
     * Creates a new ConsoleLogger for the specified class.
     *
     * @param clazz The class to create a logger for
     */
    public ConsoleLogger(final Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * Creates a new ConsoleLogger with the specified name.
     *
     * @param name The logger name
     */
    public ConsoleLogger(final String name) {
        this.name = name;
        this.log4jLogger = LogManager.getLogger(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final String message) {
        log4jLogger.trace(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final String format, final Object... args) {
        if (isTraceEnabled()) {
            log4jLogger.trace(formatMessage(format, args));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final String message) {
        log4jLogger.debug(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final String format, final Object... args) {
        if (isDebugEnabled()) {
            log4jLogger.debug(formatMessage(format, args));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final String message) {
        log4jLogger.info(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final String format, final Object... args) {
        if (isInfoEnabled()) {
            log4jLogger.info(formatMessage(format, args));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final String message) {
        log4jLogger.warn(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final String format, final Object... args) {
        if (isWarnEnabled()) {
            log4jLogger.warn(formatMessage(format, args));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final String message, final Throwable throwable) {
        log4jLogger.warn(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final String message) {
        log4jLogger.error(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final String format, final Object... args) {
        if (isErrorEnabled()) {
            log4jLogger.error(formatMessage(format, args));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final String message, final Throwable throwable) {
        log4jLogger.error(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final String message) {
        log4jLogger.fatal(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final String message, final Throwable throwable) {
        log4jLogger.fatal(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled() {
        return log4jLogger.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return log4jLogger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return log4jLogger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return log4jLogger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return log4jLogger.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Formats a message with parameters using SLF4J style ({} placeholders).
     *
     * @param format The message format
     * @param args   The arguments
     * @return The formatted message
     */
    private String formatMessage(final String format, final Object... args) {
        // Log4j2 natively supports {} format through ParameterizedMessage
        return ParameterizedMessage.format(format, args);
    }

    /**
     * Utility method to get a logger for a class.
     *
     * @param clazz The class
     * @return A new ConsoleLogger
     */
    public static Logger getLogger(final Class<?> clazz) {
        return new ConsoleLogger(clazz);
    }

    /**
     * Utility method to get a logger with a specific name.
     *
     * @param name The logger name
     * @return A new ConsoleLogger
     */
    public static Logger getLogger(final String name) {
        return new ConsoleLogger(name);
    }
}
