package it.unibo.tetraj.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Manages game resources (fonts, images, sounds). Implements lazy loading and caching for optimal
 * performance.
 */
public final class ResourceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);
  // Resources paths
  private static final String FONTS_PATH = "/fonts/";
  private static final String IMAGES_PATH = "/images/";
  private static final String SOUNDS_PATH = "/sounds/";
  // Default font name for fallback
  private static final String DEFAULT_FONT = "Arial";
  // Font files with subfolder paths
  private static final String RUBIK_FONT_FILE = "Rubik_Mono_One/RubikMonoOne-Regular.ttf";
  private static final String PIXEL_FONT_FILE = "Press_Start_2P/PressStart2P-Regular.ttf";
  // Font size
  private static final float FONT_SIZE_TITLE = 48f;
  private static final float FONT_SIZE_LARGE = 36f;
  private static final float FONT_SIZE_NORMAL = 24f;
  private static final float FONT_SIZE_MEDIUM = 20f;
  private static final float FONT_SIZE_SMALL = 16f;
  // Resources cache
  private static final Map<FontKey, Font> FONT_CACHE = new HashMap<>();
  private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();
  private static final Map<String, Clip> SOUND_CACHE = new HashMap<>();
  private static final ResourceManager INSTANCE = new ResourceManager();
  // Background music management
  private static final float DEFAULT_MUSIC_VOLUME = 0.4f;
  private Clip backgroundMusic;
  private volatile int pausedPosition;
  private volatile boolean isPaused;

  /** Private constructor for singleton pattern. */
  private ResourceManager() {
    // Singleton
  }

  /**
   * Gets the singleton instance. Thread-safe implementation using double-checked locking.
   *
   * @return The resource manager instance
   */
  public static ResourceManager getInstance() {
    return INSTANCE;
  }

  /**
   * Loads a font from resources.
   *
   * @param fontName The font file name (e.g., "RubikMonoOne.ttf")
   * @param size The desired font size
   * @return The loaded font, or default font if loading fails
   */
  public Font loadFont(final String fontName, final float size) {
    final FontKey key = new FontKey(fontName, size);

    return FONT_CACHE.computeIfAbsent(key, k -> loadFontInternal(k.name(), k.size()));
  }

  /**
   * Loads an image from resources.
   *
   * @param imageName The image file name (e.g., "background.png")
   * @return The loaded image, or null if loading fails
   */
  public Image loadImage(final String imageName) {
    return IMAGE_CACHE.computeIfAbsent(imageName, this::loadImageInternal);
  }

  /**
   * Loads a sound clip from resources.
   *
   * @param soundName The sound file name (e.g., "drop.wav")
   * @return The loaded clip, or null if loading fails
   */
  public Clip loadSound(final String soundName) {
    return SOUND_CACHE.computeIfAbsent(soundName, this::loadSoundInternal);
  }

  /**
   * Gets the Rubik Mono One font in specified size.
   *
   * @param size The desired font size
   * @return The Rubik Mono One font
   */
  public Font getRubikMonoOneFont(final float size) {
    return loadFont(RUBIK_FONT_FILE, size);
  }

  /**
   * Gets the Press Start 2P pixel font in specified size.
   *
   * @param size The desired font size
   * @return The Press Start 2P font
   */
  public Font getPressStart2PFont(final float size) {
    return loadFont(PIXEL_FONT_FILE, size);
  }

  /**
   * Plays a sound effect. The sound is rewound to the beginning before playing.
   *
   * @param soundName The sound to play
   */
  public void playSound(final String soundName) {
    final Clip clip = loadSound(soundName);
    if (clip != null) {
      clip.setFramePosition(0); // Rewind to beginning
      clip.start();
    }
  }

  /**
   * Plays background music in continuous loop with default volume. Stops any currently playing
   * background music before starting new one.
   *
   * @param musicName The music file to play in loop
   */
  public void playBackgroundMusic(final String musicName) {
    playBackgroundMusic(musicName, DEFAULT_MUSIC_VOLUME);
  }

  /**
   * Plays background music in continuous loop with specified volume. Stops any currently playing
   * background music before starting new one.
   *
   * @param musicName The music file to play in loop
   * @param volume Volume level from 0.0 (mute) to 1.0 (max)
   */
  public void playBackgroundMusic(final String musicName, final float volume) {
    stopBackgroundMusic();

    backgroundMusic = loadSound(musicName);
    if (backgroundMusic != null) {
      try {
        // Set volume
        final FloatControl volumeControl =
            (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
        final float dB = (float) (Math.log10(volume) * 20.0);
        volumeControl.setValue(dB);
        // Start looping
        backgroundMusic.setFramePosition(0);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        LOGGER.info("Started background music: {}", musicName);
      } catch (final IllegalArgumentException e) {
        LOGGER.warn("Volume control not available for: {}", musicName);
        // Play anyway without volume control
        backgroundMusic.setFramePosition(0);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
      }
    }
  }

  /** Stops the currently playing background music. */
  public void stopBackgroundMusic() {
    if (backgroundMusic != null) {
      backgroundMusic.stop();
      backgroundMusic.setFramePosition(0);
      pausedPosition = 0;
      isPaused = false;
      LOGGER.info("Stopped background music");
    }
  }

  /** Pauses the currently playing background music. */
  public void pauseBackgroundMusic() {
    if (backgroundMusic != null && backgroundMusic.isRunning()) {
      pausedPosition = backgroundMusic.getFramePosition();
      backgroundMusic.stop();
      isPaused = true;
      LOGGER.info("Paused background music at frame: {}", pausedPosition);
    }
  }

  /** Resumes the paused background music. */
  public void resumeBackgroundMusic() {
    if (backgroundMusic != null && isPaused) {
      backgroundMusic.setFramePosition(pausedPosition);
      backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
      isPaused = false;
      LOGGER.info("Resumed background music from frame: {}", pausedPosition);
    }
  }

  /**
   * Checks if background music is currently playing.
   *
   * @return true if music is playing, false otherwise
   */
  public boolean isBackgroundMusicPlaying() {
    return backgroundMusic != null && backgroundMusic.isRunning();
  }

  /**
   * Preloads all essential resources. Call this during game initialization for smoother gameplay.
   */
  public void preloadResources() {
    LOGGER.info("Preloading resources...");

    // Preload fonts in common sizes
    preloadFonts();

    // Preload sound effects
    preloadSounds();

    // Preload images if any
    preloadImages();

    LOGGER.info("Resources preloaded successfully");
  }

  /** Clears all caches to free memory. */
  public void clearCaches() {
    LOGGER.info("Clearing resource caches...");

    // Stop background music
    stopBackgroundMusic();
    if (backgroundMusic != null) {
      backgroundMusic.close();
      backgroundMusic = null;
    }

    FONT_CACHE.clear();
    IMAGE_CACHE.clear();

    // Close all sound clips before clearing
    SOUND_CACHE.values().forEach(Clip::close);
    SOUND_CACHE.clear();

    LOGGER.info("Resource caches cleared");
  }

  /** Preloads common fonts. */
  private void preloadFonts() {
    // Modern font for UI - Rubik Mono One
    loadFont(RUBIK_FONT_FILE, FONT_SIZE_TITLE);
    loadFont(RUBIK_FONT_FILE, FONT_SIZE_LARGE);
    loadFont(RUBIK_FONT_FILE, FONT_SIZE_NORMAL);
    loadFont(RUBIK_FONT_FILE, FONT_SIZE_MEDIUM);
    loadFont(RUBIK_FONT_FILE, FONT_SIZE_SMALL);

    // Pixel font for retro feel - Press Start 2P
    loadFont(PIXEL_FONT_FILE, FONT_SIZE_NORMAL);
    loadFont(PIXEL_FONT_FILE, FONT_SIZE_SMALL);
  }

  /** Preloads sound effects. */
  private void preloadSounds() {
    // Background music
    loadSound("playLoop.wav");
    loadSound("menuLoop.wav");
    // Gameplay sounds
    loadSound("drop.wav");
    loadSound("rotate.wav");
    loadSound("move.wav");
    // Scoring sounds
    loadSound("clear.wav");
    loadSound("levelUp.wav");
    // UI sounds
    loadSound("gameOver.wav");
    loadSound("pause.wav");
    loadSound("menuSelect.wav");
  }

  /** Preloads images. */
  private void preloadImages() {
    // Background images
    loadImage("splashScreenBackground.png");
  }

  /**
   * Internal method to load a font.
   *
   * @param fontName The font file name
   * @param size The desired size
   * @return The loaded font or a default fallback
   */
  private Font loadFontInternal(final String fontName, final float size) {
    try (InputStream inputStream = getClass().getResourceAsStream(FONTS_PATH + fontName)) {
      if (inputStream == null) {
        LOGGER.warn("Font not found: {}", fontName);
        return new Font(DEFAULT_FONT, Font.PLAIN, (int) size);
      }

      final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);

      // Register font with system for better rendering
      final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(baseFont);

      // Derive font with desired size
      final Font sizedFont = baseFont.deriveFont(size);

      LOGGER.info("Font loaded: {} size {}", fontName, size);
      return sizedFont;

    } catch (final FontFormatException | IOException e) {
      LOGGER.error("Failed to load font: {}", fontName, e);
      return new Font(DEFAULT_FONT, Font.PLAIN, (int) size);
    }
  }

  /**
   * Internal method to load an image.
   *
   * @param imageName The image file name
   * @return The loaded image or null
   */
  private Image loadImageInternal(final String imageName) {
    try (InputStream inputStream = getClass().getResourceAsStream(IMAGES_PATH + imageName)) {
      if (inputStream == null) {
        LOGGER.warn("Image not found: {}", imageName);
        return null;
      }

      final Image image = ImageIO.read(inputStream);
      LOGGER.info("Image loaded: {}", imageName);
      return image;

    } catch (final IOException e) {
      LOGGER.error("Failed to load image: {}", imageName, e);
      return null;
    }
  }

  /**
   * Internal method to load a sound clip.
   *
   * @param soundName The sound file name
   * @return The loaded clip or null
   */
  private Clip loadSoundInternal(final String soundName) {
    final String resourcePath = SOUNDS_PATH + soundName;

    // Use URL directly, no need for InputStream
    final var url = getClass().getResource(resourcePath);
    if (url == null) {
      LOGGER.warn("Sound not found: {}", soundName);
      return null;
    }

    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url)) {
      final Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);

      LOGGER.info("Sound loaded: {}", soundName);
      return clip;

    } catch (final UnsupportedAudioFileException e) {
      LOGGER.error("Unsupported audio format: {}", soundName, e);
      return null;
    } catch (final IOException e) {
      LOGGER.error("IO error loading sound: {}", soundName, e);
      return null;
    } catch (final LineUnavailableException e) {
      LOGGER.error("Audio line unavailable: {}", soundName, e);
      return null;
    }
  }

  /**
   * Record for font cache keys. Combines font name and size for unique identification.
   *
   * @param name The font file name
   * @param size The font size
   */
  private record FontKey(String name, float size) {
    // empty
  }
}
