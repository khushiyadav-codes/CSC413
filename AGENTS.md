\# M7 Agent Instructions



This is my Java chess project ! Help me add new views without

changing the existing game logic.



&#x20;What you can change



\- Add new classes in edu.sfsu.csc413.chess.view.

\- Add view tests in src/test/java/edu/sfsu/csc413/chess/view.

\- Update Main.java so I can choose the new views.

\- Update AGENTS.md, CLAUDE.md, and m7-submission.md.



What you should leave alone



\- Do not change model/, engine/, or factory/.

\- Do not change GameController.

\- Do not change BoardView, PlayerAction, ConsoleView, or EmojiView.

\- Leave the other existing view files alone too.

\- Do not change pom.xml or add dependencies.

\- Do not edit or delete existing tests.

\- Only use the JDK.



How the views should work



\- Send player actions to GameController.handle().

\- The Undo button should send new PlayerAction.Undo().

\- Do not call game.play(), undoLastMove(), or change the board directly.

\- Use game.legalMoves() to get legal moves.

\- Use game.status() to find out about check, checkmate, and stalemate.

\- Keep the selected square in the view.

\- Swing start() should create the window and return.

\- Do not call controller.runLoop() from Swing.

\- Start the Swing game on the event dispatch thread.

\- Remember that render() and showMessage() can be called before start().

\- Use labels for messages. Do not use modal dialogs in this version.

\- Use BoardView's default queen promotion.

\- Keep the final board and result visible after the game ends.

\- Use clear names and short methods so I can follow the code.



&#x20;Checking the work



\- Run the tests and tell me the results.

\- Check that only the allowed files changed.

\- Check Fool's Mate, an illegal move, and Undo.

\- Tell me if something could not be tested.

\- If you think a protected file needs changing, stop and explain why.

\- If a protected file seems to need changes, stop and explain why.

