```mermaid
classDiagram
    class Tetromino {
        <<interface>>
        +move(dx, dy)
        +rotateClockwise()
        +rotateCounterClockwise()
        +getShape() int[][]
        +getWidth()
        +getHeight()
        +getX()
        +getY()
        +getColor() Color
        +setPosition(x, y)
        +copy() Tetromino
    }

    class AbstractTetromino~T~ {
        <<abstract>>
        #getShapes()* int[][][]
        +move(dx, dy)
        +rotateClockwise()
        +rotateCounterClockwise()
        +getShape() int[][]
        +getWidth()
        +getHeight()
        +getX()
        +getY()
        +getColor()* Color
        +setPosition(x, y)
        +copy()* T
    }

    class ITetromino {
        #getShapes() int[][][]
        +getColor() Color
        +copy() ITetromino
    }

    class OTetromino {
        #getShapes() int[][][]
        +getColor() Color
        +copy() OTetromino
    }

    class TTetromino {
        #getShapes() int[][][]
        +getColor() Color
        +copy() TTetromino
    }

    class TetrominoRegistry {
        <<singleton>>
        +getInstance()$ TetrominoRegistry
        +getAvailableTypes() List~Class~? extends AbstractTetromino~?~~~
        +create(type, x, y) AbstractTetromino~?~
    }

    class TetrominoFactory {
        +create() AbstractTetromino~?~
    }

    Tetromino <|.. AbstractTetromino
    AbstractTetromino <|-- ITetromino
    AbstractTetromino <|-- OTetromino
    AbstractTetromino <|-- TTetromino
    TetrominoFactory --> TetrominoRegistry : delegates
    TetrominoRegistry ..> AbstractTetromino : creates
```
