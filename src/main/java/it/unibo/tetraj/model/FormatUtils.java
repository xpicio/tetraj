package it.unibo.tetraj.model;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utility class for formatting game data (scores, durations, dates). Provides consistent formatting
 * across all views and models.
 */
public final class FormatUtils {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  /** Private constructor to prevent instantiation. */
  private FormatUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Formats a score with thousands separator using neutral locale.
   *
   * @param score The score to format
   * @return Formatted score string (e.g., "1,234,567")
   */
  public static String formatScore(final long score) {
    return String.format(Locale.ROOT, "%,d", score);
  }

  /**
   * Formats a duration as MM:SS.
   *
   * @param duration The duration to format
   * @return Formatted duration string (e.g., "12:34")
   */
  public static String formatDuration(final Duration duration) {
    final long minutes = duration.toMinutes();
    final long seconds = duration.minusMinutes(minutes).getSeconds();

    return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
  }

  /**
   * Formats an instant as a date string (yyyy/MM/dd) in system default timezone.
   *
   * @param instant The instant to format
   * @return Formatted date string (e.g., "2024/01/15")
   */
  public static String formatDate(final Instant instant) {
    return instant.atZone(ZoneId.systemDefault()).format(DATE_FORMATTER);
  }
}
