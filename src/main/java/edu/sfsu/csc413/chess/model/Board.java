package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Piece[][] squares;

    public Board() {
        squares = new Piece[8][8];
    }

    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = squares[file][rank];

                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }

        return positions;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int rank = 7; rank >= 0; rank--) {
            int empty = 0;

            for (int file = 0; file < 8; file++) {
                Piece piece = squares[file][rank];

                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        result.append(empty);
                        empty = 0;
                    }

                    result.append(piece.symbol());
                }
            }

            if (empty > 0) {
                result.append(empty);
            }

            if (rank > 0) {
                result.append('/');
            }
        }

        return result.toString();
    }
}