package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/** A click-to-move board. All UI events and controller actions use Swing's thread. */
public final class SwingView implements BoardView {
    private static final Color LIGHT = new Color(238, 226, 203);
    private static final Color DARK = new Color(170, 190, 167);
    private static final Color SELECTED = new Color(248, 207, 92);
    private static final Color LEGAL = new Color(155, 205, 223);

    private final JButton[][] squares = new JButton[8][8];
    private GameController controller;
    private Game game;
    private JFrame window;
    private JLabel status;
    private JTextArea messages;
    private JTextArea history;
    private JButton undo;
    private Position selected;
    private String message = "Click a piece, then its destination.";
    private boolean quitting;

    @Override
    public void start(GameController controller) {
        onEventThread(() -> {
            this.controller = controller;
            this.game = controller.game();
            if (window == null) {
                buildWindow();
            }
            refresh();
            window.setVisible(true);
        });
    }

    private void buildWindow() {
        window = new JFrame("Chess — Swing");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                send(new PlayerAction.Quit());
            }
        });
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        status = new JLabel();
        status.setFont(status.getFont().deriveFont(Font.BOLD, 18f));
        content.add(status, BorderLayout.NORTH);
        content.add(buildBoard(), BorderLayout.CENTER);
        content.add(buildSidebar(), BorderLayout.EAST);
        content.add(buildFooter(), BorderLayout.SOUTH);
        window.setContentPane(content);
        window.pack();
        window.setMinimumSize(window.getSize());
        window.setLocationByPlatform(true);
    }

    private JPanel buildBoard() {
        JPanel board = new JPanel(new GridLayout(9, 9, 2, 2));
        board.setPreferredSize(new Dimension(540, 540));
        for (int rank = 7; rank >= 0; rank--) {
            board.add(new JLabel(String.valueOf(rank + 1), SwingConstants.CENTER));
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                JButton square = new JButton();
                square.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                square.setForeground(Color.BLACK);
                square.setOpaque(true);
                square.setBorderPainted(true);
                square.setName(position.toString());
                square.addActionListener(event -> clickSquare(position));
                squares[file][rank] = square;
                board.add(square);
            }
        }
        board.add(new JLabel());
        for (char file = 'a'; file <= 'h'; file++) {
            board.add(new JLabel(String.valueOf(file), SwingConstants.CENTER));
        }
        return board;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 8));
        sidebar.add(new JLabel("Move history"), BorderLayout.NORTH);
        history = textArea(18, 17);
        history.setName("history");
        sidebar.add(new JScrollPane(history), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        JPanel actions = new JPanel();
        undo = new JButton("Undo");
        undo.addActionListener(event -> send(new PlayerAction.Undo()));
        actions.add(undo);
        JButton moves = new JButton("Legal moves");
        moves.addActionListener(event -> send(new PlayerAction.ShowMoves()));
        actions.add(moves);
        JButton quit = new JButton("Quit");
        quit.addActionListener(event -> send(new PlayerAction.Quit()));
        actions.add(quit);
        actions.add(new JLabel("w = White, b = Black; N = knight"));
        messages = textArea(3, 50);
        messages.setName("messages");
        messages.setText(message);
        footer.add(actions, BorderLayout.NORTH);
        footer.add(new JScrollPane(messages), BorderLayout.CENTER);
        return footer;
    }

    private JTextArea textArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void clickSquare(Position position) {
        if (game.status().isGameOver()) {
            return;
        }
        Piece piece = game.board().pieceAt(position);
        if (position.equals(selected)) {
            selected = null;
        } else if (piece != null && piece.color() == game.sideToMove()) {
            selected = position;
        } else if (selected != null) {
            String notation = selected.toString() + position;
            send(new PlayerAction.Move(notation));
            return;
        } else {
            showMessage("Select a piece belonging to " + game.sideToMove() + ".");
        }
        refresh();
    }

    private void send(PlayerAction action) {
        selected = null;
        quitting = action instanceof PlayerAction.Quit;
        showMessage("");
        controller.handle(action);
    }

    @Override
    public void render(Game game) {
        onEventThread(() -> {
            this.game = game;
            selected = null;
            if (window != null) {
                refresh();
            }
        });
    }

    private void refresh() {
        status.setText(game.sideToMove() + " to move  |  " + game.status());
        Set<Position> destinations = new HashSet<>();
        if (selected != null) {
            game.legalMoves().stream().filter(move -> move.from().equals(selected))
                    .forEach(move -> destinations.add(move.to()));
        }
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                drawSquare(new Position(file, rank), destinations);
            }
        }
        undo.setEnabled(!game.history().isEmpty());
        StringBuilder text = new StringBuilder();
        for (int index = 0; index < game.history().size(); index++) {
            text.append(index + 1).append(". ").append(game.history().get(index)).append('\n');
        }
        history.setText(text.toString());
    }

    private void drawSquare(Position position, Set<Position> destinations) {
        JButton square = squares[position.file()][position.rank()];
        Piece piece = game.board().pieceAt(position);
        String label = piece == null ? "" :
                (piece.color() == edu.sfsu.csc413.chess.model.Color.WHITE ? "w" : "b")
                        + piece.type().symbol();
        square.setText(label);
        square.setToolTipText(position + (piece == null ? " — empty" : " — "
                + piece.color() + " " + piece.type()));
        square.getAccessibleContext().setAccessibleName(square.getToolTipText());
        Color background = (position.file() + position.rank()) % 2 == 0 ? DARK : LIGHT;
        square.setBackground(position.equals(selected) ? SELECTED
                : destinations.contains(position) ? LEGAL : background);
        square.setBorder(BorderFactory.createLineBorder(
                position.equals(selected) ? Color.BLACK : background, 2));
    }

    @Override
    public void showMessage(String message) {
        onEventThread(() -> {
            this.message = message;
            if (messages != null) {
                messages.setText(message);
                messages.setCaretPosition(0);
            }
        });
    }

    @Override
    public void close() {
        onEventThread(() -> {
            // Keep the final position visible for review or Undo; Quit releases the window.
            if (window != null && (quitting || !game.status().isGameOver())) {
                window.dispose();
            }
        });
    }

    private void onEventThread(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
