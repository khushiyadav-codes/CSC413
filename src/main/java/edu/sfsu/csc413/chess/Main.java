package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.SwingView;
import edu.sfsu.csc413.chess.view.TurnView;

import javax.swing.SwingUtilities;
import java.util.Locale;

public final class Main {
    public static void main(String[] args) {
        String choice = args.length == 0
                ? "console"
                : args[0].toLowerCase(Locale.ROOT);

        if (choice.equals("swing")) {
            SwingUtilities.invokeLater(() ->
                    new GameController(new Game(), new SwingView()).start());
            return;
        }

        BoardView view = switch (choice) {
            case "turn" -> new TurnView();
            case "emoji" -> new EmojiView();
            default -> new ConsoleView();
        };

        new GameController(new Game(), view).start();
    }

    private Main() {
    }
}