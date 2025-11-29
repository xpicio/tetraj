classDiagram
%% Package structure
namespace model {
class TetrominoType {
<<enumeration>>
+I
+O
+T
+S
+Z
+J
+L
-shapes: int[][][]
-color: Color
+getShape(rotation: int): int[][]
+getColor(): Color
+random(): TetrominoType$
}

        class Tetromino {
            -type: TetrominoType
            -x: int
            -y: int
            -rotation: int
            +Tetromino(type: TetrominoType, boardWidth: int)
            +Tetromino(other: Tetromino)
            +move(dx: int, dy: int): void
            +rotateClockwise(): void
            +rotateCounterClockwise(): void
            +getShape(): int[][]
            +getWidth(): int
            +getHeight(): int
            +getX(): int
            +getY(): int
            +setPosition(x: int, y: int): void
            +getColor(): Color
            +getType(): TetrominoType
        }

        class Board {
            +STANDARD_WIDTH: int = 10$
            +STANDARD_HEIGHT: int = 20$
            -width: int
            -height: int
            -cells: Color[][]
            +Board()
            +Board(width: int, height: int)
            +isValidPosition(tetromino: Tetromino): boolean
            +placeTetromino(tetromino: Tetromino): void
            +clearCompletedLines(): List~Integer~
            +isGameOver(): boolean
            +clear(): void
            +getCellColor(row: int, col: int): Color
            +getWidth(): int
            +getHeight(): int
        }

        class GameModel {
            <<final>>
            -board: Board
            -currentPiece: Tetromino
            -nextPiece: Tetromino
            -heldPiece: Tetromino
            -canHold: boolean
            -score: int
            -level: int
            -linesCleared: int
            -state: GameState
            -fallTimer: double
            -fallSpeed: double
            +GameModel()
            +startNewGame(): void
            +update(deltaTime: double): void
            +moveLeft(): void
            +moveRight(): void
            +moveDown(): boolean
            +hardDrop(): void
            +rotateClockwise(): void
            +rotateCounterClockwise(): void
            +holdPiece(): void
            +togglePause(): void
            +getBoard(): Board
            +getCurrentPiece(): Tetromino
            +getNextPiece(): Tetromino
            +getHeldPiece(): Tetromino
            +getScore(): int
            +getLevel(): int
            +getLinesCleared(): int
            +getState(): GameState
            +setState(state: GameState): void
            +getGhostPiece(): Tetromino
        }

        class GameState {
            <<enumeration>>
            +MENU
            +PLAYING
            +PAUSED
            +GAME_OVER
        }
    }

    namespace view {
        class GameView {
            <<final>>
            -canvas: Canvas
            -bufferStrategy: BufferStrategy
            -WINDOW_WIDTH: int = 800$
            -WINDOW_HEIGHT: int = 600$
            -CELL_SIZE: int = 25$
            +GameView()
            +initialize(): void
            +render(model: GameModel): void
            -renderMenu(g: Graphics2D): void
            -renderGame(g: Graphics2D, model: GameModel): void
            -renderPauseOverlay(g: Graphics2D): void
            -renderGameOverOverlay(g: Graphics2D, model: GameModel): void
            -drawTetromino(g: Graphics2D, tetromino: Tetromino, boardX: int, boardY: int, isGhost: boolean): void
            -drawCell(g: Graphics2D, x: int, y: int, color: Color): void
            -renderSidePanel(g: Graphics2D, model: GameModel): void
            -drawPreviewPiece(g: Graphics2D, piece: Tetromino, x: int, y: int): void
            +getCanvas(): Canvas
            +getWidth(): int
            +getHeight(): int
        }
    }

    namespace controller {
        class GameController {
            <<final>>
            -model: GameModel
            -view: GameView
            -frame: JFrame
            -gameThread: Thread
            -running: boolean
            -TARGET_FPS: int = 60$
            +GameController()
            +start(): void
            +stop(): void
            +run(): void
            -createWindow(): JFrame
            -setupInputHandling(): void
            -handleKeyPress(keyCode: int): void
            -handleMenuInput(keyCode: int): void
            -handleGameInput(keyCode: int): void
            -handleGameOverInput(keyCode: int): void
        }
    }

    namespace utils {
        class Logger {
            <<interface>>
            +trace(message: String): void
            +trace(format: String, args: Object[]): void
            +debug(message: String): void
            +debug(format: String, args: Object[]): void
            +info(message: String): void
            +info(format: String, args: Object[]): void
            +warn(message: String): void
            +warn(format: String, args: Object[]): void
            +warn(message: String, throwable: Throwable): void
            +error(message: String): void
            +error(format: String, args: Object[]): void
            +error(message: String, throwable: Throwable): void
            +fatal(message: String): void
            +fatal(message: String, throwable: Throwable): void
            +isTraceEnabled(): boolean
            +isDebugEnabled(): boolean
            +isInfoEnabled(): boolean
            +isWarnEnabled(): boolean
            +isErrorEnabled(): boolean
            +getName(): String
        }

        class ConsoleLogger {
            <<final>>
            -log4jLogger: org.apache.logging.log4j.Logger
            -name: String
            +ConsoleLogger(clazz: Class)
            +ConsoleLogger(name: String)
            +getLogger(clazz: Class): Logger$
            +getLogger(name: String): Logger$
            -formatMessage(format: String, args: Object[]): String
        }

        class LoggerFactory {
            <<final>>
            -LOGGER_CACHE: Map~String, Logger~$
            +getLogger(clazz: Class): Logger$
            +getLogger(name: String): Logger$
            +clearCache(): void$
        }
    }

    class Main {
        <<final>>
        -LOGGER: Logger$
        +main(args: String[]): void$
    }

    class Runnable {
        <<interface>>
        +run(): void
    }

    %% Relationships
    GameController --|> Runnable : implements
    GameController *-- GameModel : has
    GameController *-- GameView : has
    GameController --> JFrame : creates

    GameModel *-- Board : has
    GameModel *-- Tetromino : has currentPiece
    GameModel *-- Tetromino : has nextPiece
    GameModel *-- Tetromino : has heldPiece
    GameModel --> GameState : uses

    Tetromino --> TetrominoType : has
    Board --> Color : stores

    GameView --> Canvas : has
    GameView --> BufferStrategy : uses
    GameView --> GameModel : renders

    ConsoleLogger --|> Logger : implements
    LoggerFactory --> Logger : creates
    LoggerFactory --> ConsoleLogger : instantiates

    Main --> GameController : creates
    Main --> Logger : uses
    Main --> LoggerFactory : uses

    %% Notes on package structure
    note for GameModel "Core game logic:\n- Manages game state\n- Handles piece movement\n- Score calculation"
    note for GameView "Rendering only:\n- No game logic\n- Draws based on Model"
    note for GameController "MVC Controller:\n- Handles input\n- Updates Model\n- Triggers View render"
