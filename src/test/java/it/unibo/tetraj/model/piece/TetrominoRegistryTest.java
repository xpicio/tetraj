package it.unibo.tetraj.model.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for TetrominoRegistry. */
class TetrominoRegistryTest {

  private static final int TETROMINO_TYPE_COUNT = 7;
  private TetrominoRegistry registry;

  @BeforeEach
  void setUp() {
    registry = TetrominoRegistry.getInstance();
  }

  @Test
  @DisplayName("should contain exactly 7 tetromino types")
  void registryShouldContainSevenTypes() {
    // Arrange - done in setUp()

    // Act
    final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

    // Assert
    assertEquals(TETROMINO_TYPE_COUNT, types.size());
  }

  @Test
  @DisplayName("should create all tetromino types successfully")
  void registryShouldCreateAllTypes() {
    // Arrange - done in setUp()

    // Arrange
    final List<Class<? extends AbstractTetromino<?>>> types = registry.getAvailableTypes();

    // Act & Assert
    for (final Class<? extends AbstractTetromino<?>> type : types) {
      final AbstractTetromino<?> piece = registry.create(type, 0, 0);
      assertNotNull(piece);
      assertInstanceOf(type, piece);
    }
  }

  @Test
  @DisplayName("should create piece at specified position")
  void shouldCreatePieceAtSpecifiedPosition() {
    // Arrange
    final int expectedX = 5;
    final int expectedY = 10;

    // Act
    final AbstractTetromino<?> piece = registry.create(ITetromino.class, expectedX, expectedY);

    // Assert
    assertEquals(expectedX, piece.getX());
    assertEquals(expectedY, piece.getY());
  }

  @Test
  @DisplayName("should return singleton instance")
  void shouldReturnSingletonInstance() {
    // Arrange & Act
    final TetrominoRegistry instance1 = TetrominoRegistry.getInstance();
    final TetrominoRegistry instance2 = TetrominoRegistry.getInstance();

    // Assert
    assertEquals(instance1, instance2);
  }
}
