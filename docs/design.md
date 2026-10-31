# M5 Chess Engine Design

## Class Diagram

This diagram shows the classes in the `engine` and `model` packages.
`BoardFactory` is included because `Game` uses it to create the starting
board. The six piece subclasses are shown without their methods to keep
the diagram readable.

```mermaid
classDiagram
    direction TB

    class Game {
        -Board board
        -List~Move~ history
        -Color sideToMove
        +Game()
        +Game(Board board, Color sideToMove)
        +board() Board
        +sideToMove() Color
        +history() List~Move~
        +legalMoves() List~Move~
        +findLegalMove(String notation) Optional~Move~
        +play(Move move) void
        +undoLastMove() Optional~Move~
    }

    class MoveGenerator {
        <<utility>>
        -MoveGenerator()
        +legalMoves(Board board, Color color) List~Move~$
        +isAttacked(Board, Position, Color) boolean$
        +isInCheck(Board board, Color color) boolean$
        ~pseudoLegalMoves(Board board, Color color) List~Move~$
    }

    class Board {
        -Piece[][] squares
        +Board()
        +pieceAt(Position position) Piece
        +isEmpty(Position position) boolean
        +place(Position position, Piece piece) void
        +positionsOf(Color color) List~Position~
        +kingPosition(Color color) Position
        +apply(Move move) void
        +undo(Move move) void
        +toString() String
    }

    class Piece {
        <<abstract>>
        -Color color
        -PieceType type
        +color() Color
        +type() PieceType
        +symbol() char
        +pseudoLegalMoves(Board board, Position from) List~Move~*
        +attacks(Board, Position, Position) boolean
        +toString() String
    }

    class Pawn
    class Knight
    class Bishop
    class Rook
    class Queen
    class King

    class Move {
        <<record>>
        -Position from
        -Position to
        -Piece moved
        -Piece captured
        -PieceType promotesTo
        +Move(Position, Position, Piece, Piece, PieceType)
        +from() Position
        +to() Position
        +moved() Piece
        +captured() Piece
        +promotesTo() PieceType
        +quiet(Position, Position, Piece) Move$
        +capture(Position, Position, Piece, Piece) Move$
        +promotion(Position, Position, Piece, Piece, PieceType) Move$
        +isCapture() boolean
        +isPromotion() boolean
        +toString() String
        +equals(Object other) boolean
        +hashCode() int
    }

    class Position {
        <<record>>
        +Position(int file, int rank)
        +file() int
        +rank() int
        +isOnBoard(int file, int rank) boolean$
        +parse(String algebraic) Position$
        +offsetOrNull(int fileDelta, int rankDelta) Position
        +toString() String
        +equals(Object other) boolean
        +hashCode() int
    }

    class Color {
        <<enumeration>>
        WHITE
        BLACK
        +opposite() Color
        +pawnDirection() int
        +pawnStartRank() int
        +promotionRank() int
        +values() Color[]$
        +valueOf(String name) Color$
    }

    class PieceType {
        <<enumeration>>
        PAWN
        KNIGHT
        BISHOP
        ROOK
        QUEEN
        KING
        +symbol() char
        +fromSymbol(char letter) PieceType$
        +values() PieceType[]$
        +valueOf(String name) PieceType$
    }

    class BoardFactory {
        <<external>>
        +standard() Board$
    }

    Pawn --|> Piece
    Piece <|-- Knight
    Piece <|-- Bishop
    Piece <|-- Rook
    Piece <|-- Queen
    Piece <|-- King

    Game "1" *-- "1" Board : board
    Move "0..*" --o "1" Game : history
    Game --> Color : sideToMove
    Game ..> MoveGenerator : generates moves with
    Game ..> BoardFactory : creates starting board with

    Board "1" -- "0..64" Piece : squares
    Board ..> Move : applies and undoes

    MoveGenerator ..> Board : queries and restores
    MoveGenerator ..> Piece : asks moves and attacks
    MoveGenerator ..> Move : candidate and result lists
    MoveGenerator ..> Position : piece and king locations
    MoveGenerator ..> Color : moving and attacking sides

    Move --> "1" Position : from
    Move --> "1" Position : to
    Move --> "1" Piece : moved
    Move --> "0..1" Piece : captured
    Move --> "0..1" PieceType : promotesTo

    Piece --> Color : color
    Piece --> PieceType : type
```

`Game` stores the board, move history, and whose turn it is.
`MoveGenerator` has no fields. It uses the board passed to its static
methods to find legal moves.

`Move` records where a piece moved from and to, what was captured, and
whether it promoted. This gives `Board.undo` the information it needs
to restore the position.

Only the `BoardFactory` method used by `Game` is shown.

## Sequence Diagram

This diagram shows what happens when `game.legalMoves()` is called.
The `Piece` lifeline represents whichever piece is being checked.
The smaller board queries inside piece methods are left out.

```mermaid
sequenceDiagram
    actor Caller
    participant Game
    participant MoveGenerator
    participant Board
    participant Piece

    Caller->>Game: legalMoves()
    Game->>MoveGenerator: legalMoves(board, sideToMove)
    MoveGenerator->>MoveGenerator: pseudoLegalMoves(board, color)
    MoveGenerator->>Board: positionsOf(color)
    Board-->>MoveGenerator: occupied positions

    loop Each position belonging to color
        MoveGenerator->>Board: pieceAt(from)
        Board-->>MoveGenerator: piece
        MoveGenerator->>Piece: pseudoLegalMoves(board, from)
        Piece-->>MoveGenerator: moves for this piece
        MoveGenerator->>MoveGenerator: add moves to candidate list
    end

    MoveGenerator-->>MoveGenerator: all candidates

    loop Each candidate move
        MoveGenerator->>Board: apply(move)
        MoveGenerator->>MoveGenerator: isInCheck(board, color)
        MoveGenerator->>Board: kingPosition(color)
        Board-->>MoveGenerator: king position or null

        alt No king for this color
            MoveGenerator-->>MoveGenerator: inCheck = false
        else King exists
            MoveGenerator->>MoveGenerator: isAttacked(board, king, color.opposite())
            MoveGenerator->>Board: positionsOf(attacker)
            Board-->>MoveGenerator: opponent positions

            loop Opponent pieces until an attack is found
                MoveGenerator->>Board: pieceAt(from)
                Board-->>MoveGenerator: attacking piece
                MoveGenerator->>Piece: attacks(board, from, king)
                Piece-->>MoveGenerator: true or false
            end

            MoveGenerator-->>MoveGenerator: true if any attack, otherwise false
            MoveGenerator-->>MoveGenerator: inCheck = attack result
        end

        MoveGenerator->>Board: undo(move) in finally

        alt inCheck is false
            MoveGenerator->>MoveGenerator: keep move in legal list
        else inCheck is true
            MoveGenerator->>MoveGenerator: drop candidate
        end
    end

    MoveGenerator-->>Game: legal moves
    Game-->>Caller: legal moves
```

Each candidate move is played, checked, and undone. It is kept only if
the moving side's king is not in check. The `finally` block restores the
board even if checking for attacks throws an exception.

Pawns use their own attack method because they attack diagonally.
A side without a king is treated as not in check, which allows partial
boards to work in the tests.