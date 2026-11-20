package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

/** A bordered terminal board that faces the player whose turn it is. */
public final class TurnBoardView implements BoardView {

    private final PrintStream out;
    private final Scanner in;

    public TurnBoardView() {
        this(System.out, new Scanner(System.in));
    }

    public TurnBoardView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        boolean white = game.sideToMove() == Color.WHITE;

        out.println(
                "\n" + game.sideToMove()
                        + " perspective | " + game.status());

        String border =
                "  +---+---+---+---+---+---+---+---+";

        out.println(border);

        for (int row = 0; row < 8; row++) {
            int rank = white ? 7 - row : row;
            out.print((rank + 1) + " |");

            for (int column = 0; column < 8; column++) {
                int file = white ? column : 7 - column;
                Position position = new Position(file, rank);

                String piece = PieceGlyphs.LETTERS.glyphFor(
                        game.board().pieceAt(position));

                out.print(" " + piece + " |");
            }

            out.println(" " + (rank + 1));
            out.println(border);
        }

        out.println(
                white
                        ? "    a   b   c   d   e   f   g   h"
                        : "    h   g   f   e   d   c   b   a");

        out.println(
                game.sideToMove()
                        + " to move | Moves played: "
                        + game.history().size());

        out.println(
                "Uppercase = White; lowercase = Black. "
                        + "Use board coordinates when it turns.");
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

        return PlayerAction.parse(
                in.hasNextLine() ? in.nextLine() : "quit");
    }
}