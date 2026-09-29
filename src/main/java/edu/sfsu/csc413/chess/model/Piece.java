package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private final Color color;
    private final PieceType type;
    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }
    public Color color() {
        return color;
    }
    public PieceType type() {
        return type;
    }

    public char symbol() {
        char symbol = type.symbol();

        if (color == Color.BLACK) {
            return Character.toLowerCase(symbol);
        }

        return symbol;
    }

    public abstract List<Move> pseudoLegalMoves(Board board, Position from);

    public boolean attacks(Board board, Position from, Position target) {
        List<Move> moves = pseudoLegalMoves(board, from);

        for (Move move : moves) {
            if (move.to().equals(target)) {
                return true;
            }
        }

        return false;
    }

    protected List<Move> steppingMoves(Board board, Position from, int[][] offsets) {
        List<Move> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            Position to = from.offsetOrNull(offset[0], offset[1]);

            if (to == null) {
                continue;
            }

            Piece piece = board.pieceAt(to);

            if (piece == null) {
                moves.add(Move.quiet(from, to, this));
            } else if (piece.color() != color) {
                moves.add(Move.capture(from, to, this, piece));
            }
        }

        return moves;
    }

    protected List<Move> slidingMoves(Board board, Position from, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        for (int[] direction : directions) {
            Position to = from.offsetOrNull(direction[0], direction[1]);

            while (to != null) {
                Piece piece = board.pieceAt(to);

                if (piece == null) {
                    moves.add(Move.quiet(from, to, this));
                } else {
                    if (piece.color() != color) {
                        moves.add(Move.capture(from, to, this, piece));
                    }

                    break;
                }

                to = to.offsetOrNull(direction[0], direction[1]);
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol());
    }
}