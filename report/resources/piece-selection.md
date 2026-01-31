```mermaid
classDiagram
    class PieceSelectionStrategy {
        <<interface>>
        +next() Class~AbstractTetromino~
        +reset() void
    }

    class RandomStrategy {
        +next() Class~AbstractTetromino~
        +reset() void
    }

    class BagRandomizerStrategy {
        +next() Class~AbstractTetromino~
        +reset() void
    }

    class PieceSelectionFactory {
        +create(config)$ PieceSelectionStrategy
    }

    class TetrominoFactory {
        +create() AbstractTetromino
    }

    PieceSelectionStrategy <|.. RandomStrategy
    PieceSelectionStrategy <|.. BagRandomizerStrategy
    PieceSelectionFactory ..> PieceSelectionStrategy : creates
    TetrominoFactory --> PieceSelectionStrategy : uses
```
