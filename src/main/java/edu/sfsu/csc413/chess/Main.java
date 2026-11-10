package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;

/**
 * Entry point.
 *
 * <p>At M0 this does nothing but prove the toolchain works. It grows into the
 * real launcher as the engine appears underneath it.
 */
public final class Main {

    public static void main(String[] args) {
        Game game = new Game();

        BoardView view = args.length > 0 && args[0].equalsIgnoreCase("emoji")
                ? new EmojiView()
                : new ConsoleView();

        GameController controller = new GameController(game, view);
        controller.start();
    }

    private Main() {
    }
}