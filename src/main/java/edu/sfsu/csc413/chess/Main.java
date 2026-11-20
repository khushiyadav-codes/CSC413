package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.SwingView;
import edu.sfsu.csc413.chess.view.TurnBoardView;

import java.util.Locale;

public final class Main {

    public static void main(String[] args) {
        Game game = new Game();

        String choice = args.length == 0
                ? "console"
                : args[0].toLowerCase(Locale.ROOT);

        BoardView view = switch (choice) {
            case "swing" -> new SwingView();
            case "turn" -> new TurnBoardView();
            case "emoji" -> new EmojiView();
            default -> new ConsoleView();
        };

        GameController controller = new GameController(game, view);
        controller.start();
    }

    private Main() {
    }
}