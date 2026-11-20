package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.RotatingView;
import edu.sfsu.csc413.chess.view.SwingView;

import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * Entry point.
 *
 * <p>At M0 this does nothing but prove the toolchain works. It grows into the
 * real launcher as the engine appears underneath it.
 */
public final class Main {

    public static void main(String[] args) {
        Game game = new Game();

        String choice = args.length == 0 ? "console" : args[0].toLowerCase(Locale.ROOT);
        BoardView view = switch (choice) {
            case "swing" -> new SwingView();
            case "rotating" -> new RotatingView();
            case "emoji" -> new EmojiView();
            default -> new ConsoleView();
        };

        GameController controller = new GameController(game, view);
        if (view instanceof SwingView) {
            SwingUtilities.invokeLater(controller::start);
        } else {
            controller.start();
        }
    }

    private Main() {
    }
}
