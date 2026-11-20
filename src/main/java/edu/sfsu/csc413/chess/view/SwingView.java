package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

public class SwingView implements BoardView {
    private static final java.awt.Color LIGHT =
            new java.awt.Color(240, 217, 181);
    private static final java.awt.Color DARK =
            new java.awt.Color(181, 136, 99);
    private static final java.awt.Color LEGAL =
            new java.awt.Color(144, 238, 144);

    private final JButton[][] squares = new JButton[8][8];
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final JPanel boardPanel = createBoard();

    private GameController controller;
    private Game game;
    private Position selected;
    private JFrame frame;

    @Override
    public void start(GameController controller) {
        this.controller = controller;

        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(createControls(), BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                controller.handle(new PlayerAction.Quit());
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createBoard() {
        JPanel panel = new JPanel(new GridLayout(8, 8));

        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                JButton square = createSquare(position);
                squares[file][rank] = square;
                panel.add(square);
            }
        }

        return panel;
    }

    private JButton createSquare(Position position) {
        JButton square = new JButton();
        square.setMargin(new Insets(0, 0, 0, 0));
        square.setPreferredSize(new Dimension(64, 64));
        square.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        square.setForeground(java.awt.Color.BLACK);
        square.setContentAreaFilled(false);
        square.setOpaque(true);
        square.setFocusPainted(false);
        square.setBorderPainted(false);
        square.setToolTipText(position.toString());
        square.addActionListener(event -> clickSquare(position));
        return square;
    }

    private JPanel createControls() {
        JButton undo = new JButton("Undo");
        undo.addActionListener(event ->
                sendAction(new PlayerAction.Undo()));

        JPanel controls = new JPanel(new GridLayout(3, 1));
        controls.add(statusLabel);
        controls.add(messageLabel);
        controls.add(undo);
        return controls;
    }

    @Override
    public void render(Game game) {
        this.game = game;
        selected = null;
        drawBoard();
        statusLabel.setText(statusText());
    }

    private String statusText() {
        String turn = game.sideToMove() + " to move";

        return switch (game.status()) {
            case IN_PROGRESS -> turn;
            case CHECK -> turn + " - Check!";
            case CHECKMATE -> "Checkmate. "
                    + game.sideToMove().opposite() + " wins.";
            case STALEMATE -> "Stalemate. The game is a draw.";
        };
    }

    private void drawBoard() {
        Set<Position> destinations = legalDestinations();

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Position position = new Position(file, rank);
                JButton square = squares[file][rank];

                square.setText(
                        pieceLabel(game.board().pieceAt(position)));
                square.setBackground(
                        squareColor(position, destinations));
            }
        }
    }

    private Set<Position> legalDestinations() {
        Set<Position> destinations = new HashSet<>();

        if (selected != null) {
            for (Move move : game.legalMoves()) {
                if (move.from().equals(selected)) {
                    destinations.add(move.to());
                }
            }
        }

        return destinations;
    }

    private java.awt.Color squareColor(
            Position position, Set<Position> destinations) {
        if (position.equals(selected)) {
            return java.awt.Color.YELLOW;
        }

        if (destinations.contains(position)) {
            return LEGAL;
        }

        return (position.file() + position.rank()) % 2 == 0
                ? DARK : LIGHT;
    }

    private String pieceLabel(Piece piece) {
        if (piece == null) {
            return "";
        }

        return (piece.color() == Color.WHITE ? "W" : "B")
                + piece.type().symbol();
    }

    private void clickSquare(Position position) {
        if (game.status().isGameOver()) {
            return;
        }

        if (position.equals(selected)) {
            selected = null;
            showMessage("Selection cleared.");
            drawBoard();
            return;
        }

        Piece piece = game.board().pieceAt(position);

        if (piece != null && piece.color() == game.sideToMove()) {
            selected = position;
            showMessage("Selected " + position + ". Click a destination.");
            drawBoard();
        } else if (selected == null) {
            showMessage("Select a " + game.sideToMove() + " piece first.");
        } else {
            sendAction(new PlayerAction.Move(
                    selected.toString() + position));
        }
    }

    private void sendAction(PlayerAction action) {
        selected = null;
        showMessage(" ");
        controller.handle(action);
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void close() {
        // Keep the final board and result visible when the game ends.
        // The player closes the window using its close button.
    }
}
