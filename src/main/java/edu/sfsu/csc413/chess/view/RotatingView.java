package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

/** A bordered terminal board that turns to face the player whose turn it is. */
public final class RotatingView implements BoardView {
    private static final String BORDER = "  +---+---+---+---+---+---+---+---+";
    private final PrintStream out;
    private final Scanner in;

    public RotatingView() {
        this(System.out, new Scanner(System.in));
    }

    public RotatingView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        boolean flipped = game.sideToMove() == Color.BLACK;
        out.println();
        out.println("Viewing from " + game.sideToMove() + "'s side (uppercase = White).");
        out.println(BORDER);
        for (int row = 0; row < 8; row++) {
            int rank = flipped ? row : 7 - row;
            out.print((rank + 1) + " |");
            for (int column = 0; column < 8; column++) {
                int file = flipped ? 7 - column : column;
                out.print(" " + PieceGlyphs.LETTERS.glyphFor(
                        game.board().pieceAt(new Position(file, rank))) + " |");
            }
            out.println();
            out.println(BORDER);
        }
        out.println(flipped ? "    h   g   f   e   d   c   b   a" : "    a   b   c   d   e   f   g   h");
        out.println(game.sideToMove() + " to move  |  " + game.status());
        out.println("History: " + game.history());
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Move (e2e4, e7e8q), undo, moves, or quit: ");
        out.flush();
        return PlayerAction.parse(in.hasNextLine() ? in.nextLine() : "quit");
    }
}

