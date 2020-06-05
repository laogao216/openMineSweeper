# Mines
Minesweeper, written in Java, made with Eclipse and uses processing library core.

If you haven't played minesweer yet, you can look up its rules and strategies or refer to [this wikipedia page](https://en.wikipedia.org/wiki/Minesweeper_(video_game)).

### *For users*
1. download java [here](https://www.java.com/en/download/) if you haven't
2. download the Mines_1.2.0.zip file above
3. extract all contents from Mines.zip to a directory of your choice
4. execute Mines.jar

# Game controls: 
To start a new game press 'n' or click on "NEW GAME" from the right-hand panel.

To toggle between mouse and keyboard control press 'c' or click on "MOUSE/KEYBOARD CONTROL" from the right-hand panel.

To toggle highlight press 'h' or click on "HIDE/SHOW HIGHLIGHT" from the right-hand panel.

### *keyboard control*
'w', 'a', 's', 'd' for moving the highlighted tile around.

'j' for uncovering a covered tile or uncover all covered tiles around an uncovered tile.

'k' for flagging a mine on an uncovered tile.

'l' for unflagging a flagged tile by returning it to covered.

Keyboard commands are implemented at the press of key. 

Also, if game does not respond click on the mine counter at the top left corner and try again.

### *Mouse control*
Mouse left click on a covered tile to uncover it.

Mouse left click on a uncovered tile to uncover all covered tiles around it or highlight covered tiles around it if highlight is on.

Mouse right click on a covered tile to flag it.

Mouse center (or wheel) click on a flag to unflag it by returning it to covered.

Mouse commands are implemented at the release of button.
