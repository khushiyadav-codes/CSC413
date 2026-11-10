\# M6 Submission



\## 1. Who decides, who shows



MoveGenerator.status() detects checkmate because this is a chess rule,

which answers "Is it a rule?" GameController.handle() announces the

result through BoardView.showMessage(), so it takes the request, runs

the rule, and tells the view. ConsoleView or EmojiView displays the

message because that belongs to what the user sees.



\## 2. The dependency rule



Main imports classes from edu.sfsu.csc413.chess.view to connect the game

to a view and controller. Classes in engine, model, and factory must not

import view because the chess rules should not depend on a specific

display. This lets the same game work with a console or a graphical view.



\## 3. Test 2 on my engine



For M7, I expect to add SwingView.java in the view package and update

Main.java to select it. GameController and the engine should not need

changes because SwingView would use the same BoardView interface.

Its button events would send PlayerAction objects to GameController.handle().

