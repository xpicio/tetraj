package it.unibo.tetraj.model.piece;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for Tetromino classes. */
class TetrominoTest {

  private static final int ROTATION_COUNT = 4;
  private static final int MANY_ROTATIONS = 100;
  private static final String NAME_I = "I";
  private static final String NAME_O = "O";
  private static final String NAME_T = "T";
  private static final String NAME_S = "S";
  private static final String NAME_Z = "Z";
  private static final String NAME_J = "J";
  private static final String NAME_L = "L";
  private TetrominoRegistry registry;

  @BeforeEach
  void setUp() {
    registry = TetrominoRegistry.getInstance();
  }

  private static void assertRotationValid(
      final int[][] expected, final int[][] actual, final int rotationIndex) {
    assertArrayEquals(expected, actual, String.format("Rotation %d mismatch", rotationIndex));
  }

  /**
   * Provides tetromino types with their expected standard Tetris colors.
   *
   * @return stream of arguments containing tetromino class, expected color, and name
   */
  static Stream<Arguments> tetrominoColorProvider() {
    return Stream.of(
        Arguments.of(ITetromino.class, Color.CYAN, NAME_I),
        Arguments.of(OTetromino.class, Color.YELLOW, NAME_O),
        Arguments.of(TTetromino.class, Color.MAGENTA, NAME_T),
        Arguments.of(STetromino.class, Color.GREEN, NAME_S),
        Arguments.of(ZTetromino.class, Color.RED, NAME_Z),
        Arguments.of(JTetromino.class, Color.BLUE, NAME_J),
        Arguments.of(LTetromino.class, Color.ORANGE, NAME_L));
  }

  /**
   * Provides tetromino types with their expected initial shapes.
   *
   * @return stream of arguments containing tetromino class, expected shape, and name
   */
  static Stream<Arguments> tetrominoInitialShapeProvider() {
    return Stream.of(
        Arguments.of(
            ITetromino.class,
            new int[][] {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
            NAME_I),
        Arguments.of(OTetromino.class, new int[][] {{1, 1}, {1, 1}}, NAME_O),
        Arguments.of(TTetromino.class, new int[][] {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}}, NAME_T),
        Arguments.of(STetromino.class, new int[][] {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}}, NAME_S),
        Arguments.of(ZTetromino.class, new int[][] {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}}, NAME_Z),
        Arguments.of(JTetromino.class, new int[][] {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}}, NAME_J),
        Arguments.of(LTetromino.class, new int[][] {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}}, NAME_L));
  }

  /**
   * Provides tetromino types with their expected dimensions.
   *
   * @return stream of arguments containing tetromino class, width, height, and name
   */
  static Stream<Arguments> tetrominoDimensionProvider() {
    return Stream.of(
        Arguments.of(ITetromino.class, 4, 4, NAME_I),
        Arguments.of(OTetromino.class, 2, 2, NAME_O),
        Arguments.of(TTetromino.class, 3, 3, NAME_T),
        Arguments.of(STetromino.class, 3, 3, NAME_S),
        Arguments.of(ZTetromino.class, 3, 3, NAME_Z),
        Arguments.of(JTetromino.class, 3, 3, NAME_J),
        Arguments.of(LTetromino.class, 3, 3, NAME_L));
  }

  /**
   * Provides tetromino types for copy tests.
   *
   * @return stream of arguments containing tetromino class and name
   */
  static Stream<Arguments> tetrominoTypeProvider() {
    return Stream.of(
        Arguments.of(ITetromino.class, NAME_I),
        Arguments.of(OTetromino.class, NAME_O),
        Arguments.of(TTetromino.class, NAME_T),
        Arguments.of(STetromino.class, NAME_S),
        Arguments.of(ZTetromino.class, NAME_Z),
        Arguments.of(JTetromino.class, NAME_J),
        Arguments.of(LTetromino.class, NAME_L));
  }

  /**
   * Provides tetromino types with all 4 rotation states for clockwise rotation.
   *
   * @return stream of arguments containing tetromino class, expected shapes for all rotations, and
   *     name
   */
  static Stream<Arguments> tetrominoRotationProvider() {
    return Stream.of(
        Arguments.of(
            ITetromino.class,
            new int[][][] {
              {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
              {{0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}},
              {{0, 0, 0, 0}, {0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}},
              {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}},
            },
            NAME_I),
        Arguments.of(
            OTetromino.class,
            new int[][][] {{{1, 1}, {1, 1}}, {{1, 1}, {1, 1}}, {{1, 1}, {1, 1}}, {{1, 1}, {1, 1}}},
            NAME_O),
        Arguments.of(
            TTetromino.class,
            new int[][][] {
              {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
              {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
              {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
              {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}},
            },
            NAME_T),
        Arguments.of(
            STetromino.class,
            new int[][][] {
              {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}},
              {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}},
              {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}},
              {{1, 0, 0}, {1, 1, 0}, {0, 1, 0}},
            },
            NAME_S),
        Arguments.of(
            ZTetromino.class,
            new int[][][] {
              {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}},
              {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
              {{0, 0, 0}, {1, 1, 0}, {0, 1, 1}},
              {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}},
            },
            NAME_Z),
        Arguments.of(
            JTetromino.class,
            new int[][][] {
              {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}},
              {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}},
              {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}},
              {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}},
            },
            NAME_J),
        Arguments.of(
            LTetromino.class,
            new int[][][] {
              {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}},
              {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}},
              {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}},
              {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}},
            },
            NAME_L));
  }

  @Nested
  class ParameterizedTetrominoTests {

    @ParameterizedTest(name = "{2}-Tetromino should have correct color")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoColorProvider")
    @DisplayName("should have correct standard Tetris color")
    void shouldHaveCorrectColor(
        final Class<? extends AbstractTetromino<?>> type,
        final Color expectedColor,
        final String name) {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);

      // Act
      final Color actualColor = piece.getColor();

      // Assert
      assertEquals(expectedColor, actualColor);
    }

    @ParameterizedTest(name = "{2}-Tetromino should have correct initial shape")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoInitialShapeProvider")
    @DisplayName("should have correct initial shape")
    void shouldHaveCorrectInitialShape(
        final Class<? extends AbstractTetromino<?>> type,
        final int[][] expectedShape,
        final String name) {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);

      // Act
      final int[][] actualShape = piece.getShape();

      // Assert
      assertArrayEquals(expectedShape, actualShape);
    }

    @ParameterizedTest(name = "{3}-Tetromino should have {1}x{2} dimensions")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoDimensionProvider")
    @DisplayName("should have correct dimensions")
    void shouldHaveCorrectDimensions(
        final Class<? extends AbstractTetromino<?>> type,
        final int expectedWidth,
        final int expectedHeight,
        final String name) {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);

      // Act & Assert
      assertEquals(expectedWidth, piece.getWidth());
      assertEquals(expectedHeight, piece.getHeight());
    }

    @ParameterizedTest(name = "{2}-Tetromino should rotate clockwise correctly")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoRotationProvider")
    @DisplayName("should rotate clockwise correctly through all states")
    void shouldRotateClockwiseCorrectly(
        final Class<? extends AbstractTetromino<?>> type,
        final int[][][] expectedShapes,
        final String name) {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);

      // Act & Assert
      for (int i = 0; i < ROTATION_COUNT; i++) {
        assertRotationValid(expectedShapes[i], piece.getShape(), i);
        piece.rotateClockwise();
      }
      // After 4 rotations, should return to initial state
      assertArrayEquals(expectedShapes[0], piece.getShape());
    }

    @ParameterizedTest(name = "{2}-Tetromino should rotate counter-clockwise correctly")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoRotationProvider")
    @DisplayName("should rotate counter-clockwise correctly through all states")
    void shouldRotateCounterClockwiseCorrectly(
        final Class<? extends AbstractTetromino<?>> type,
        final int[][][] expectedShapes,
        final String name) {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);
      // Reverse order for counter-clockwise: 0, 3, 2, 1
      final int[] counterClockwiseOrder = {0, 3, 2, 1};

      // Act & Assert
      for (int i = 0; i < ROTATION_COUNT; i++) {
        assertRotationValid(expectedShapes[counterClockwiseOrder[i]], piece.getShape(), i);
        piece.rotateCounterClockwise();
      }
      // After 4 rotations, should return to initial state
      assertArrayEquals(expectedShapes[0], piece.getShape());
    }

    @ParameterizedTest(name = "{1}-Tetromino copy should preserve state")
    @MethodSource("it.unibo.tetraj.model.piece.TetrominoTest#tetrominoTypeProvider")
    @DisplayName("should copy piece preserving all state")
    void shouldCopyPiecePreservingState(
        final Class<? extends AbstractTetromino<?>> type, final String name) {
      // Arrange
      final int posX = 5;
      final int posY = 10;
      final AbstractTetromino<?> original = registry.create(type, posX, posY);
      original.rotateClockwise();

      // Act
      final Tetromino copied = original.copy();

      // Assert
      assertNotSame(original, copied);
      assertInstanceOf(type, copied);
      assertEquals(original.getX(), copied.getX());
      assertEquals(original.getY(), copied.getY());
      assertArrayEquals(original.getShape(), copied.getShape());
      assertEquals(original.getColor(), copied.getColor());
    }
  }

  @Nested
  class CommonTetrominoBehaviorTests {

    @Test
    @DisplayName("should move piece correctly")
    void shouldMovePieceCorrectly() {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(TTetromino.class, 0, 0);
      final int dx = 3;
      final int dy = 2;

      // Act
      piece.move(dx, dy);

      // Assert
      assertEquals(dx, piece.getX());
      assertEquals(dy, piece.getY());
    }

    @Test
    @DisplayName("should set position correctly")
    void shouldSetPositionCorrectly() {
      // Arrange
      final AbstractTetromino<?> piece = registry.create(JTetromino.class, 0, 0);
      final int newX = 7;
      final int newY = 15;

      // Act
      piece.setPosition(newX, newY);

      // Assert
      assertEquals(newX, piece.getX());
      assertEquals(newY, piece.getY());
    }

    @Test
    @DisplayName("should handle multiple rotations without index errors")
    void shouldHandleMultipleRotationsWithoutErrors() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        for (int i = 0; i < MANY_ROTATIONS; i++) {
          piece.rotateClockwise();
          assertNotNull(piece.getShape());
          assertTrue(piece.getWidth() > 0);
          assertTrue(piece.getHeight() > 0);
        }
      }
    }

    @Test
    @DisplayName("should handle multiple counter-clockwise rotations without index errors")
    void shouldHandleMultipleCounterClockwiseRotationsWithoutErrors() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        for (int i = 0; i < MANY_ROTATIONS; i++) {
          piece.rotateCounterClockwise();
          assertNotNull(piece.getShape());
          assertTrue(piece.getWidth() > 0);
          assertTrue(piece.getHeight() > 0);
        }
      }
    }

    @Test
    @DisplayName("should have valid shape arrays with only 0s and 1s")
    void shouldHaveValidShapeArrays() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        for (int rotation = 0; rotation < ROTATION_COUNT; rotation++) {
          final int[][] shape = piece.getShape();
          for (final int[] row : shape) {
            for (final int cell : row) {
              assertTrue(
                  cell == 0 || cell == 1,
                  String.format("Cell value should be 0 or 1, found: %d", cell));
            }
          }
          piece.rotateClockwise();
        }
      }
    }

    @Test
    @DisplayName("should return to initial state after 4 clockwise rotations")
    void shouldReturnToInitialAfterFourClockwiseRotations() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        final int[][] initialShape = piece.getShape();

        piece.rotateClockwise();
        piece.rotateClockwise();
        piece.rotateClockwise();
        piece.rotateClockwise();

        assertArrayEquals(initialShape, piece.getShape());
      }
    }

    @Test
    @DisplayName("should return to initial state after 4 counter-clockwise rotations")
    void shouldReturnToInitialAfterFourCounterClockwiseRotations() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        final int[][] initialShape = piece.getShape();

        piece.rotateCounterClockwise();
        piece.rotateCounterClockwise();
        piece.rotateCounterClockwise();
        piece.rotateCounterClockwise();

        assertArrayEquals(initialShape, piece.getShape());
      }
    }

    @Test
    @DisplayName("clockwise and counter-clockwise rotations should be inverse operations")
    void rotationsShouldBeInverseOperations() {
      // Arrange
      final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

      // Act & Assert
      for (final Class<? extends AbstractTetromino<?>> type : types) {
        final AbstractTetromino<?> piece = registry.create(type, 0, 0);
        final int[][] initialShape = piece.getShape();

        piece.rotateClockwise();
        piece.rotateCounterClockwise();

        assertArrayEquals(initialShape, piece.getShape());
      }
    }
  }
}
