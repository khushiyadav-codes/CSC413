package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

public class TurnView implements BoardView {
    private static final String BORDER = "  +" + "----+".repeat(8);

    private final PrintStream out;
    private final Scanner in;

    public TurnView() {
        this(System.out, new Scanner(System.in));
    }

    public TurnView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    @Override
    public void render(Game game) {
        boolean flipped = game.sideToMove() == Color.BLACK;

        out.println();
        out.println(BORDER);
        for (int row = 0; row < 8; row++) {
            printRank(game, row, flipped);
            out.println(BORDER);
        }
        printFiles(flipped);
        out.println(game.sideToMove() + " to move | " + game.status());
    }

    private void printRank(Game game, int row, boolean flipped) {
        int rank = flipped ? row : 7 - row;
        out.print((rank + 1) + " |");

        for (int column = 0; column < 8; column++) {
            int file = flipped ? 7 - column : column;
            Piece piece = game.board().pieceAt(new Position(file, rank));
            out.print(" " + pieceLabel(piece) + " |");
        }
        out.println(" " + (rank + 1));
    }

    private void printFiles(boolean flipped) {
        out.print("   ");
        for (int column = 0; column < 8; column++) {
            int file = flipped ? 7 - column : column;
            out.print(" " + (char) ('a' + file) + "   ");
        }
        out.println();
    }

    private String pieceLabel(Piece piece) {
        if (piece == null) {
            return "  ";
        }
        return (piece.color() == Color.WHITE ? "W" : "B")
                + piece.type().symbol();
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Your move (e2e4, undo, moves, quit): ");
        out.flush();
        return PlayerAction.parse(in.hasNextLine() ? in.nextLine() : "quit");
    }
}