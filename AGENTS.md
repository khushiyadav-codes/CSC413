\# Instructions for coding agents



This is a chess engine built with MVC. The model and controller are

finished. Your job is to add views.



\## Do



\- Add views as new classes in src/main/java/edu/sfsu/csc413/chess/view/

&#x20; or a new package under it.

\- Implement BoardView. Read BoardView, ConsoleView, and PlayerAction

&#x20; before writing anything.

\- Read the game through Game: board(), sideToMove(), legalMoves(),

&#x20; status(), and history().

\- Send player actions to the controller as PlayerAction objects.

&#x20; For example: controller.handle(new PlayerAction.Move("e2e4")).

\- Update the view selection in Main.main so the new views can be

&#x20; chosen from the command line. This project has no chooseView method.

\- Use clear names and short methods so I can understand the code.

\- In the Swing view, show the selected square and its number of legal destination squares in a status label.



\## Don't



\- Don't change anything in model/, engine/, or factory/.

\- Don't change BoardView, PlayerAction, GameController,

&#x20; ConsoleView, or EmojiView.

\- Don't change other existing view files.

\- Don't call game.play(), game.undoLastMove(), or anything that

&#x20; changes the game. Only the controller changes the game.

\- Don't work out chess rules in the view. Legal moves come from

&#x20; game.legalMoves(). Check, checkmate, and stalemate come from

&#x20; game.status().

\- Don't wait for the next move inside the window. A window's start()

&#x20; builds the window and returns. It never calls controller.runLoop().

&#x20; A modal dialog that asks one question, such as which promotion

&#x20; piece to use, is allowed.

\- Don't change pom.xml or add dependencies. Use only the JDK.

\- Don't edit or delete existing tests.

\- Don't write the word runLoop anywhere in the Swing view, even in a

&#x20; comment. The M7 check searches for it and expects no matches.



\## Check your work



\- Run ./mvnw test, or .\\mvnw.cmd test on Windows.

\- The existing tests must still pass.

\- Check git status and the diff. Changes should only include new

&#x20; view files, new view tests, Main.java, and the M7 documentation

&#x20; allowed by the handout.

\- Test Fool's Mate, an illegal move, and Undo.

\- Report what you actually checked and anything you could not test.



\## Stop and ask



If you think a protected file needs changing, stop and explain why

instead of changing it.

