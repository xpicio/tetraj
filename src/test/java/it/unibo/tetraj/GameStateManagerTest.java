package it.unibo.tetraj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import it.unibo.tetraj.controller.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for GameStateManager. Tests follow AAA pattern: Arrange, Act, Assert. */
@ExtendWith(MockitoExtension.class)
class GameStateManagerTest {

  private GameStateManager stateManager;
  @Mock private Controller menuController;
  @Mock private Controller playController;
  @Mock private Controller gameOverController;

  @BeforeEach
  void setUp() {
    // Arrange - common setup for all tests
    stateManager = new GameStateManager();

    // Register mock controllers
    stateManager.registerController(GameState.MENU, menuController);
    stateManager.registerController(GameState.PLAYING, playController);
    stateManager.registerController(GameState.GAME_OVER, gameOverController);
  }

  @Test
  @DisplayName("should start with null state")
  void shouldStartWithNullState() {
    // Arrange
    final GameStateManager freshStateManager = new GameStateManager();

    // Act
    final GameState currentState = freshStateManager.getCurrentState();

    // Assert
    assertNull(currentState, "Initial state should be null");
  }

  @Test
  @DisplayName("should allow transition from null to MENU")
  void shouldAllowTransitionFromNullToMenu() {
    // Arrange
    final GameStateManager freshStateManager = new GameStateManager();
    final Controller controller = mock(Controller.class);
    freshStateManager.registerController(GameState.MENU, controller);

    // Act
    final boolean result = freshStateManager.switchTo(GameState.MENU);

    // Assert
    assertTrue(result, "Should allow transition from null to MENU");
    assertEquals(GameState.MENU, freshStateManager.getCurrentState());
    verify(controller, times(1)).enter();
  }

  @Test
  @DisplayName("should not allow transition from null to PLAYING")
  void shouldNotAllowTransitionFromNullToPlaying() {
    // Arrange
    final GameStateManager freshStateManager = new GameStateManager();
    final Controller controller = mock(Controller.class);
    freshStateManager.registerController(GameState.PLAYING, controller);

    // Act
    final boolean result = freshStateManager.switchTo(GameState.PLAYING);

    // Assert
    assertFalse(result, "Should not allow transition from null to PLAYING");
    assertNull(freshStateManager.getCurrentState());
    verifyNoInteractions(controller);
  }

  @Test
  @DisplayName("should allow valid transition from MENU to PLAYING")
  void shouldAllowValidTransitionFromMenuToPlaying() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    reset(menuController, playController);

    // Act
    final boolean result = stateManager.switchTo(GameState.PLAYING);

    // Assert
    assertTrue(result, "Should allow transition from MENU to PLAYING");
    assertEquals(GameState.PLAYING, stateManager.getCurrentState());
    verify(menuController, times(1)).exit();
    verify(playController, times(1)).enter();
  }

  @Test
  @DisplayName("should not allow invalid transition from MENU to GAME_OVER")
  void shouldNotAllowInvalidTransitionFromMenuToPaused() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    reset(menuController, gameOverController);

    // Act
    final boolean result = stateManager.switchTo(GameState.GAME_OVER);

    // Assert
    assertFalse(result, "Should not allow transition from MENU to GAME_OVER");
    assertEquals(GameState.MENU, stateManager.getCurrentState());
    verify(menuController, never()).exit();
    verifyNoInteractions(gameOverController);
  }

  @Test
  @DisplayName("should allow valid transition from PLAYING to GAME_OVER")
  void shouldAllowValidTransitionFromPlayingToGameOver() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    stateManager.switchTo(GameState.PLAYING);
    reset(playController, gameOverController);

    // Act
    final boolean result = stateManager.switchTo(GameState.GAME_OVER);

    // Assert
    assertTrue(result, "Should allow transition from PLAYING to GAME_OVER");
    assertEquals(GameState.GAME_OVER, stateManager.getCurrentState());
    verify(playController, times(1)).exit();
    verify(gameOverController, times(1)).enter();
  }

  @Test
  @DisplayName("should allow valid transition from GAME_OVER to MENU")
  void shouldAllowValidTransitionFromGameOverToMenu() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    stateManager.switchTo(GameState.PLAYING);
    stateManager.switchTo(GameState.GAME_OVER);
    reset(gameOverController, menuController);

    // Act
    final boolean result = stateManager.switchTo(GameState.MENU);

    // Assert
    assertTrue(result, "Should allow transition from GAME_OVER to MENU");
    assertEquals(GameState.MENU, stateManager.getCurrentState());
    verify(gameOverController, times(1)).exit();
    verify(menuController, times(1)).enter();
  }

  @Test
  @DisplayName("should allow valid transition from GAME_OVER to PLAYING for restart")
  void shouldAllowValidTransitionFromGameOverToPlayingForRestart() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    stateManager.switchTo(GameState.PLAYING);
    stateManager.switchTo(GameState.GAME_OVER);
    reset(gameOverController, playController);

    // Act
    final boolean result = stateManager.switchTo(GameState.PLAYING);

    // Assert
    assertTrue(result, "Should allow transition from GAME_OVER to PLAYING for restart");
    assertEquals(GameState.PLAYING, stateManager.getCurrentState());
    verify(gameOverController, times(1)).exit();
    verify(playController, times(1)).enter();
  }

  @Test
  @DisplayName("should return false when switching to state without registered controller")
  void shouldReturnFalseWhenSwitchingToStateWithoutRegisteredController() {
    // Arrange
    final GameStateManager freshStateManager = new GameStateManager();
    // No controllers registered

    // Act
    final boolean result = freshStateManager.switchTo(GameState.MENU);

    // Assert
    assertFalse(result, "Should return false when no controller is registered");
    assertNull(freshStateManager.getCurrentState());
  }

  @Test
  @DisplayName("should maintain state when invalid transition attempted")
  void shouldMaintainStateWhenInvalidTransitionAttempted() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    stateManager.switchTo(GameState.PLAYING);

    // Act
    stateManager.switchTo(GameState.MENU); // Valid
    stateManager.switchTo(GameState.GAME_OVER); // Invalid from MENU

    // Assert
    assertEquals(GameState.MENU, stateManager.getCurrentState());
  }

  @Test
  @DisplayName("should return correct current controller after state switch")
  void shouldReturnCorrectCurrentControllerAfterStateSwitch() {
    // Arrange - done in setUp()

    // Act
    stateManager.switchTo(GameState.MENU);
    final Controller controller1 = stateManager.getCurrentController();

    stateManager.switchTo(GameState.PLAYING);
    final Controller controller2 = stateManager.getCurrentController();

    // Assert
    assertEquals(menuController, controller1);
    assertEquals(playController, controller2);
  }

  @Test
  @DisplayName("should validate all transitions from PLAYING state")
  void shouldValidateAllTransitionsFromPlayingState() {
    // Arrange - done in setUp()

    // Act & Assert
    assertTrue(stateManager.isValidTransition(GameState.PLAYING, GameState.GAME_OVER));
    assertTrue(stateManager.isValidTransition(GameState.PLAYING, GameState.MENU));
    assertFalse(stateManager.isValidTransition(GameState.PLAYING, GameState.PLAYING));
  }

  @Test
  @DisplayName("should validate all transitions from MENU state")
  void shouldValidateAllTransitionsFromMenuState() {
    // Arrange - done in setUp()

    // Act & Assert
    assertTrue(stateManager.isValidTransition(GameState.MENU, GameState.PLAYING));
    assertFalse(stateManager.isValidTransition(GameState.MENU, GameState.GAME_OVER));
    assertFalse(stateManager.isValidTransition(GameState.MENU, GameState.MENU));
  }

  @Test
  @DisplayName("should validate all transitions from GAME_OVER state")
  void shouldValidateAllTransitionsFromGameOverState() {
    // Arrange - done in setUp()

    // Act & Assert
    assertTrue(stateManager.isValidTransition(GameState.GAME_OVER, GameState.MENU));
    assertTrue(stateManager.isValidTransition(GameState.GAME_OVER, GameState.PLAYING));
  }

  @Test
  @DisplayName("should call exit and enter methods in correct order during transition")
  void shouldCallExitAndEnterMethodsInCorrectOrderDuringTransition() {
    // Arrange
    stateManager.switchTo(GameState.MENU);
    reset(menuController, playController);

    // Act
    stateManager.switchTo(GameState.PLAYING);

    // Assert - verify order of calls using InOrder
    final var inOrder = org.mockito.Mockito.inOrder(menuController, playController);
    inOrder.verify(menuController).exit();
    inOrder.verify(playController).enter();
  }
}
