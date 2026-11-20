package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.stream.Collectors;

/** A click-driven board. Only the controller carries out player actions. */
public final class SwingView implements BoardView {
    private static final Color LIGHT = new Color(240, 226, 199);
    private static final Color DARK = new Color(151, 175, 152);
    private static final Border NORMAL =
            BorderFactory.createEmptyBorder(4, 4, 4, 4);

    private final JButton[][] squares = new JButton[8][8];

    private GameController controller;
    private Game game;
    private Position selected;

    private JFrame window;
    private JPanel content;
    private JLabel gameStatus;
    private JLabel selectionStatus;
    private JLabel messageLabel;
    private JTextArea moveHistory;

    private String message =
            "Click a piece, then its destination. Promotion chooses a queen.";

    @Override
    public void start(GameController controller) {
        onEventThread(() -> {
            window = new JFrame("Chess — Run 3");
            window.setDefaultCloseOperation(
                    WindowConstants.DO_NOTHING_ON_CLOSE);

            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    controller.handle(new PlayerAction.Quit());
                    window.dispose();
                }
            });

            window.setContentPane(createContent(controller));
            window.pack();
            window.setMinimumSize(window.getSize());
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    /** Builds controls on the Swing event thread. */
    JPanel createContent(GameController controller) {
        this.controller = controller;
        game = controller.game();

        content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel labels = new JPanel(new GridLayout(0, 1, 0, 5));

        gameStatus = new JLabel();
        selectionStatus = new JLabel();
        messageLabel = new JLabel(message);

        gameStatus.setName("gameStatus");
        selectionStatus.setName("selectionStatus");
        messageLabel.setName("message");

        labels.add(gameStatus);
        labels.add(selectionStatus);
        labels.add(messageLabel);

        content.add(labels, BorderLayout.NORTH);
        content.add(createBoard(), BorderLayout.CENTER);
        content.add(createHistoryPanel(), BorderLayout.EAST);

        JButton undo = new JButton("Undo");
        undo.setName("undo");
        undo.addActionListener(
                event -> send(new PlayerAction.Undo()));

        content.add(undo, BorderLayout.SOUTH);

        refresh();
        return content;
    }

    private JPanel createBoard() {
        JPanel board = new JPanel(new GridLayout(8, 8));
        board.setPreferredSize(new Dimension(560, 560));

        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                JButton square = new JButton();

                square.setName(position.toString());
                square.setFont(
                        new Font(Font.SANS_SERIF, Font.BOLD, 20));
                square.setOpaque(true);
                square.setForeground(Color.BLACK);

                square.addActionListener(
                        event -> click(position));

                squares[file][rank] = square;
                board.add(square);
            }
        }

        return board;
    }

    private JScrollPane createHistoryPanel() {
        moveHistory = new JTextArea(20, 19);
        moveHistory.setName("moveHistory");
        moveHistory.setEditable(false);
        moveHistory.setFont(
                new Font(Font.MONOSPACED, Font.PLAIN, 14));
        moveHistory.setMargin(
                new java.awt.Insets(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(moveHistory);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Move history"));

        return scroll;
    }

    private void updateHistory() {
        StringBuilder text = new StringBuilder();
        int number = 1;

        for (Move move : game.history()) {
            text.append(number++)
                    .append(". ")
                    .append(move.moved().color())
                    .append("  ")
                    .append(move)
                    .append('\n');
        }

        String historyText = text.isEmpty()
                ? "No moves yet."
                : text.toString();

        if (!historyText.equals(moveHistory.getText())) {
            moveHistory.setText(historyText);
            moveHistory.setCaretPosition(
                    moveHistory.getDocument().getLength());
        }
    }

    private void click(Position position) {
        if (game.status().isGameOver()) {
            showMessage(
                    "Game over. Use Undo to return to the previous position.");
            return;
        }

        Piece piece = game.board().pieceAt(position);

        if (position.equals(selected)) {
            selected = null;
        } else if (piece != null
                && piece.color() == game.sideToMove()) {
            selected = position;
        } else if (selected != null) {
            send(new PlayerAction.Move(
                    selected.toString() + position));
            return;
        } else {
            showMessage(
                    "Select a piece belonging to "
                            + game.sideToMove() + ".");
        }

        refresh();
    }

    private void send(PlayerAction action) {
        selected = null;
        showMessage(" ");
        controller.handle(action);
    }

    @Override
    public void render(Game game) {
        onEventThread(() -> {
            this.game = game;
            selected = null;

            if (content != null) {
                refresh();
            }
        });
    }

    private void refresh() {
        Set<Position> destinations = game.legalMoves().stream()
                .filter(move -> move.from().equals(selected))
                .map(Move::to)
                .collect(Collectors.toSet());

        gameStatus.setText(
                game.sideToMove() + " to move | "
                        + game.status()
                        + " | Moves played: "
                        + game.history().size());

        selectionStatus.setText(
                selected == null
                        ? "Selected: none | Legal destinations: 0"
                        : "Selected: " + selected
                        + " | Legal destinations: "
                        + destinations.size());

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                drawSquare(
                        position,
                        destinations.contains(position));
            }
        }

        updateHistory();
    }

    private void drawSquare(
            Position position, boolean destination) {

        Piece piece = game.board().pieceAt(position);
        JButton square =
                squares[position.file()][position.rank()];

        // W/B labels remain readable without a chess-symbol font.
        String label = piece == null
                ? ""
                : (piece.color()
                == edu.sfsu.csc413.chess.model.Color.WHITE
                ? "W" : "B")
                + piece.type().symbol();

        square.setText(label);

        String description = position
                + (piece == null
                ? " — empty"
                : " — " + piece.color()
                + " " + piece.type());

        if (position.equals(selected)) {
            Set<Position> legalSquares = new java.util.HashSet<>();
            for (Move move : game.legalMoves()) {
                if (move.from().equals(position)) {
                    legalSquares.add(move.to());
                }
            }
            description += " — " + legalSquares.size()
                    + " legal destinations";
        }
        square.setToolTipText(description);
        square.getAccessibleContext()
                .setAccessibleName(description);

        square.setBackground(
                (position.file() + position.rank()) % 2 == 0
                        ? DARK : LIGHT);

        if (position.equals(selected)) {
            square.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(170, 80, 0), 4));
        } else if (destination) {
            square.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(15, 85, 150), 4));
        } else {
            square.setBorder(NORMAL);
        }
    }

    @Override
    public void showMessage(String message) {
        onEventThread(() -> {
            this.message = message;

            if (messageLabel != null) {
                messageLabel.setText(message);
            }
        });
    }

    @Override
    public void close() {
        onEventThread(() -> {
            // Keep the final result visible for reading or Undo.
            if (window != null && !game.status().isGameOver()) {
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