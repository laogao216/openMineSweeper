///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class processes and executes user inputs passed from Driver.
 * 
 * @author laogao216
 */
public class Minesweeper {

  private PApplet processing;
  private final PImage[] num = new PImage[9];
  private final PImage[] highlighted_num = new PImage[9];
  private final PImage flag;
  private final PImage highlighted_flag;
  private final PImage bad_flag;
  private final PImage mine;
  private final PImage triggered_mine;
  private final PImage blank;
  private final PImage highlighted_blank;
  private final PImage panel;
  private final PImage[] panel_num = new PImage[10];
  private final PImage panel_negative;
  private final PImage new_game_pressed;
  private final PImage menu_pressed;
  private final PImage toggle_dn;
  private final PImage toggle_dn_pressed;
  private final PImage toggle_up;
  private final PImage toggle_up_pressed;
  private Tile[][] tile = new Tile[Driver.ROW][Driver.COL];
  private boolean highlightIsOn;
  private final String start = "start :)";
  private final String alive = "alive ^-^";
  private final String gameOver = "gameOver *~*";
  private final String victory = "victory ^o^";
  private String gameState;
  private final String mouse = "mouse";
  private final String keyboard = "keyboard";
  private String gameMode;
  private int coveredMine;
  private int[] firstMoveLoc;
  private int[] curLoc;
  private char prevKeyPressed;
  private boolean prevMousePressed;
  private boolean newGameBtnIsPressed;
  private boolean menuBtnIsPressed;
  private boolean ctrlToggleIsPressed;
  private boolean highlightToggleIsPressed;
  private boolean endMessageShown;
  private int time;
  private Timer timer = new Timer();
  private final TimerTask task = new TimerTask() {
    @Override
    public void run() {
      if (gameState.equals(alive)) {
        time += 1;
      }
    }
  };

  /**
   * Initialize the above fields and initialize game.
   * 
   * @param processing - the PApplet to be used here
   */
  public Minesweeper(PApplet processing) {
    this.processing = processing;
    time = 0;
    gameState = start;
    gameMode = mouse;
    highlightIsOn = false;
    firstMoveLoc = new int[] {-1, -1};
    curLoc = new int[] {0, 0};
    prevKeyPressed = '\u0000';
    prevMousePressed = false;
    newGameBtnIsPressed = false;
    menuBtnIsPressed = false;
    ctrlToggleIsPressed = false;
    highlightToggleIsPressed = false;
    endMessageShown = false;
    processing.fill(0);
    processing.rect(Driver.ROW * 16 + 1, 0, 418, Driver.COL * 16);
    if (Driver.COL < 16) {
      processing.fill(0);
      processing.rect(0, Driver.COL * 16 + 1, Driver.ROW * 16, 256 - Driver.COL * 16);
    }
    for (int i = 0; i < 9; i++) {
      String path = "images" + File.separator + Integer.toString(i) + ".png";
      PImage image = processing.loadImage(path);
      num[i] = image;
    }
    for (int i = 0; i < 9; i++) {
      String path = "images" + File.separator + "highlighted_" + Integer.toString(i) + ".png";
      PImage image = processing.loadImage(path);
      highlighted_num[i] = image;
    }
    flag = processing.loadImage("images" + File.separator + "flag.png");
    highlighted_flag = processing.loadImage("images" + File.separator + "highlighted_flag.png");
    bad_flag = processing.loadImage("images" + File.separator + "bad_flag.png");
    mine = processing.loadImage("images" + File.separator + "mine.png");
    triggered_mine = processing.loadImage("images" + File.separator + "triggered_mine.png");
    blank = processing.loadImage("images" + File.separator + "blank.png");
    highlighted_blank = processing.loadImage("images" + File.separator + "highlighted_blank.png");
    panel = processing.loadImage("images" + File.separator + "panel.png");
    panel_negative = processing.loadImage("images" + File.separator + "panel_negative.png");
    new_game_pressed = processing.loadImage("images" + File.separator + "new_game_pressed.png");
    menu_pressed = processing.loadImage("images" + File.separator + "menu_pressed.png");
    toggle_dn = processing.loadImage("images" + File.separator + "toggle_dn.png");
    toggle_dn_pressed = processing.loadImage("images" + File.separator + "toggle_dn_pressed.png");
    toggle_up = processing.loadImage("images" + File.separator + "toggle_up.png");
    toggle_up_pressed = processing.loadImage("images" + File.separator + "toggle_up_pressed.png");
    for (int i = 0; i < 10; i++) {
      String path = "images" + File.separator + "panel_" + Integer.toString(i) + ".png";
      PImage image = processing.loadImage(path);
      panel_num[i] = image;
    }
    initGame();
  }

  /**
   * Initializes the Game
   */
  private void initGame() {
    time = 0;
    coveredMine = Driver.MINE_COUNT;
    for (int row = 0; row < Driver.ROW; row++) {
      for (int col = 0; col < Driver.COL; col++) {
        tile[row][col] = new Tile();
      }
    }
    boolean[][] seatAvailable = new boolean[Driver.ROW][Driver.COL];
    for (int row = 0; row < Driver.ROW; row++) {
      for (int col = 0; col < Driver.COL; col++) {
        seatAvailable[row][col] = true;
      }
    }
    if (firstMoveLoc[0] != -1) {
      seatAvailable[firstMoveLoc[0]][firstMoveLoc[1]] = false;
      int neighbor[][] = neighbors(firstMoveLoc[0], firstMoveLoc[1]);
      for (int i = 0; i < 8; i++) {
        if (neighbor[i][0] != -1) {
          seatAvailable[neighbor[i][0]][neighbor[i][1]] = false;
        }
      }
    }
    for (int i = 0; i < Driver.MINE_COUNT; i++) {
      Random random = new Random();
      int row = random.nextInt() % Driver.ROW;
      if (row < 0) {
        row = row * -1;
      }
      int col = random.nextInt() % Driver.COL;
      if (col < 0) {
        col = col * -1;
      }
      if (seatAvailable[row][col] == true) {
        tile[row][col].setKey(9);
        seatAvailable[row][col] = false;
      } else {
        i -= 1;
      }
    }
    for (int row = 0; row < Driver.ROW; row++) {
      for (int col = 0; col < Driver.COL; col++) {
        if (tile[row][col].getKey() != 9) {
          int count = 0;
          int neighbor[][] = neighbors(row, col);
          for (int i = 0; i < 8; i++) {
            if (neighbor[i][0] != -1 && tile[neighbor[i][0]][neighbor[i][1]].getKey() == 9) {
              count += 1;
            }
          }
          tile[row][col].setKey(count);
        }
      }
    }
    draw();
  }

  /**
   * Called by Driver.draw() continuously. process user inputs by altering the displayed states of
   * each tile.
   * 
   * @param mouseX       - x coordinate of location of the mouse
   * @param mouseY       - y coordinate of location of the mouse
   * @param mousePressed - true if a mouse button is being pressed, false if otherwise
   * @param mouseButton  - the button on the mouse (left: 37, center: 3, right: 39)
   * @param keyPressed   - the key that is being pressed
   */
  public void update(int mouseX, int mouseY, boolean mousePressed, int mouseButton,
      char keyPressed) {
    // keep timer at 0 before first move:
    if (gameState.equals(start)) {
      time = 0;
    }
    // handle first move when game already restarted at least once:
    if (gameState.equals(start) && firstMoveLoc[0] != -1) {
      int row = firstMoveLoc[0];
      int col = firstMoveLoc[1];
      if (tile[row][col].getKey() == 0) {
        gameState = alive;
        uncoverHelp(row, col);
        try {
          timer.scheduleAtFixedRate(task, 0, 1000);
        } catch (IllegalStateException e) {
          time = 0;
        }
      } else {
        initGame();
      }
    }
    // show game result:
    if (gameState.equals(victory) && endMessageShown == false) {
      JOptionPane.showMessageDialog(
          null, "Congratulations, you have finished in " + time + " seconds"
              + System.lineSeparator() + "Thank you for playing!",
          "Game Result", JOptionPane.INFORMATION_MESSAGE);
      endMessageShown = true;
    }
    if (gameState.equals(gameOver) && endMessageShown == false) {
      JOptionPane.showMessageDialog(
          null, "Better luck next time, you have lasted " + time + " seconds"
              + System.lineSeparator() + "Thank you for playing!",
          "Game Result", JOptionPane.INFORMATION_MESSAGE);
      endMessageShown = true;
    }
    // clean key input by removing duplicated input:
    char key;
    if (keyPressed == prevKeyPressed) {
      key = '\u0000';
    } else {
      key = keyPressed;
      prevKeyPressed = keyPressed;
    }
    // detects mouse release action:
    boolean mouseReleased = false;
    if (prevMousePressed == true && mousePressed == false) {
      mouseReleased = true;
    } else {
      mouseReleased = false;
    }
    prevMousePressed = mousePressed;
    // restart game:
    if (mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 209 && mouseY > 65
        && mouseY < 129 && mousePressed == true) {
      newGameBtnIsPressed = true;
    } else {
      newGameBtnIsPressed = false;
    }
    if (key == 'n' || key == 'N' || mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 209
        && mouseY > 65 && mouseY < 129 && mouseReleased == true) {
      gameState = start;
      firstMoveLoc = new int[] {-1, -1};
      initGame();
    }
    // Back to menu:
    if (mouseX > Driver.ROW * 16 + 210 && mouseX < Driver.ROW * 16 + 418 && mouseY > 65
        && mouseY < 129 && mousePressed == true) {
      menuBtnIsPressed = true;
    } else {
      menuBtnIsPressed = false;
    }
    if (key == 'm' || key == 'M' || mouseX > Driver.ROW * 16 + 210 && mouseX < Driver.ROW * 16 + 418
        && mouseY > 65 && mouseY < 129 && mouseReleased == true) {
      processing.exit();
      File jar = new File("mines.jar");
      try {
        Desktop.getDesktop().open(jar);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    // toggle game control mode:
    if (mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 418 && mouseY > 129
        && mouseY < 193 && mousePressed == true) {
      ctrlToggleIsPressed = true;
    } else {
      ctrlToggleIsPressed = false;
    }
    if (key == 'c' || key == 'C' || mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 418
        && mouseY > 129 && mouseY < 194 && mouseReleased == true) {
      if (gameMode.equals(keyboard)) {
        gameMode = mouse;
      } else {
        gameMode = keyboard;
      }
      for (int r = 0; r < Driver.ROW; r++) {
        for (int c = 0; c < Driver.COL; c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
    }
    // toggle highlight:
    if (mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 418 && mouseY > 193
        && mouseY < 258 && mousePressed == true) {
      highlightToggleIsPressed = true;
    } else {
      highlightToggleIsPressed = false;
    }
    if (key == 'h' || key == 'H' || mouseX > Driver.ROW * 16 + 1 && mouseX < Driver.ROW * 16 + 418
        && mouseY > 193 && mouseY < 258 && mouseReleased == true) {
      if (highlightIsOn == true) {
        highlightIsOn = false;
        for (int r = 0; r < Driver.ROW; r++) {
          for (int c = 0; c < Driver.COL; c++) {
            tile[r][c].setIsHighlighted(false);
          }
        }
      } else {
        highlightIsOn = true;
      }
    }
    // handle highlight:
    if (highlightIsOn && gameMode.equals(keyboard)) {
      if (key == 'w' || key == 'W' && curLoc[1] > 0) {
        curLoc[1] -= 1;
      }
      if (key == 'a' || key == 'A' && curLoc[0] > 0) {
        curLoc[0] -= 1;
      }
      if (key == 's' || key == 'S' && curLoc[1] < Driver.COL - 1) {
        curLoc[1] += 1;
      }
      if (key == 'd' || key == 'D' && curLoc[0] < Driver.ROW - 1) {
        curLoc[0] += 1;
      }
      for (int r = 0; r < Driver.ROW; r++) {
        for (int c = 0; c < Driver.COL; c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
      tile[curLoc[0]][curLoc[1]].setIsHighlighted(true);
      if (tile[curLoc[0]][curLoc[1]].getState() == Display.UNCOVERED) {
        int neighbor[][] = neighbors(curLoc[0], curLoc[1]);
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1
              && (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.COVERED
                  || tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.FLAG)) {
            tile[neighbor[i][0]][neighbor[i][1]].setIsHighlighted(true);
          }
        }
      }
    }
    if (highlightIsOn && gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.ROW * 16 + 1
        && mouseY > 0 && mouseY < Driver.COL * 16 + 1 && mousePressed == true
        && mouseButton == 37) {
      for (int r = 0; r < Driver.ROW; r++) {
        for (int c = 0; c < Driver.COL; c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
      int row = (mouseX - 1) / 16;
      int col = (mouseY - 1) / 16;
      tile[row][col].setIsHighlighted(true);
      if (tile[row][col].getState() == Display.UNCOVERED) {
        int neighbor[][] = neighbors(row, col);
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1
              && (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.COVERED
                  || tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.FLAG)) {
            tile[neighbor[i][0]][neighbor[i][1]].setIsHighlighted(true);
          }
        }
      }
    }
    // handle first move for the first time:
    if (gameState.equals(start) && firstMoveLoc[0] == -1) {
      if (gameMode.equals(keyboard)) {
        if (key == 'j' || key == 'J') {
          if (tile[curLoc[0]][curLoc[1]].getKey() == 0) {
            gameState = alive;
            uncoverHelp(curLoc[0], curLoc[1]);
            try {
              timer.scheduleAtFixedRate(task, 0, 1000);
            } catch (IllegalStateException e) {
              time = 0;
            }
          } else {
            firstMoveLoc[0] = curLoc[0];
            firstMoveLoc[1] = curLoc[1];
            initGame();
          }
        }
      }
      if (gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.ROW * 16 + 1 && mouseY > 0
          && mouseY < Driver.COL * 16 + 1) {
        int row = (mouseX - 1) / 16;
        int col = (mouseY - 1) / 16;
        if (mouseReleased == true && mouseButton == 37) {
          if (tile[row][col].getKey() == 0) {
            gameState = alive;
            uncoverHelp(row, col);
            try {
              timer.scheduleAtFixedRate(task, 0, 1000);
            } catch (IllegalStateException e) {
              time = 0;
            }
          } else {
            firstMoveLoc[0] = row;
            firstMoveLoc[1] = col;
            initGame();
          }
        }
      }
    }
    // handle main game play. Calls draw() until this testing fails, on victory or death:
    if (gameState.equals(alive)) {
      int foundMine = 0;
      int wrongMine = 0;
      int coveredTile = 0;
      for (int r = 0; r < Driver.ROW; r++) {
        for (int c = 0; c < Driver.COL; c++) {
          if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() == 9) {
            foundMine += 1;
          }
          if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() != 9) {
            wrongMine += 1;
          }
          if (tile[r][c].getState() == Display.COVERED) {
            coveredTile += 1;
          }
        }
      }
      // process flagging and uncovering:
      if (gameMode.equals(keyboard)) {
        if ((key == 'k' || key == 'K')
            && tile[curLoc[0]][curLoc[1]].getState() == Display.COVERED) {
          tile[curLoc[0]][curLoc[1]].setState(Display.FLAG);
          coveredMine -= 1;
        }
        if ((key == 'l' || key == 'L') && tile[curLoc[0]][curLoc[1]].getState() == Display.FLAG) {
          tile[curLoc[0]][curLoc[1]].setState(Display.COVERED);
          coveredMine += 1;
        }
        if (key == 'j' || key == 'J') {
          uncoverHelp(curLoc[0], curLoc[1]);
        }
      }
      if (gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.ROW * 16 + 1 && mouseY > 0
          && mouseY < Driver.COL * 16 + 1) {
        int row = (mouseX - 1) / 16;
        int col = (mouseY - 1) / 16;
        if (mouseReleased == true && mouseButton == 39
            && tile[row][col].getState() == Display.COVERED) {
          tile[row][col].setState(Display.FLAG);
          coveredMine -= 1;
          for (int r = 0; r < Driver.ROW; r++) {
            for (int c = 0; c < Driver.COL; c++) {
              tile[r][c].setIsHighlighted(false);
            }
          }
        }
        if (mouseReleased == true && mouseButton == 3
            && tile[row][col].getState() == Display.FLAG) {
          tile[row][col].setState(Display.COVERED);
          coveredMine += 1;
          for (int r = 0; r < Driver.ROW; r++) {
            for (int c = 0; c < Driver.COL; c++) {
              tile[r][c].setIsHighlighted(false);
            }
          }
        }
        if (mouseReleased == true && mouseButton == 37) {
          uncoverHelp(row, col);
        }
      }
      // test for victory:
      if ((foundMine == Driver.MINE_COUNT || coveredTile == Driver.MINE_COUNT - foundMine)
          && wrongMine == 0) {
        gameState = victory;
      }
      // prepare game result:
      if (gameState.equals(victory)) {
        coveredMine = 0;
        for (int r = 0; r < Driver.ROW; r++) {
          for (int c = 0; c < Driver.COL; c++) {
            if (tile[r][c].getKey() != 9) {
              tile[r][c].setState(Display.UNCOVERED);
            }
            if (tile[r][c].getKey() == 9) {
              tile[r][c].setState(Display.FLAG);
            }
          }
        }
      }
      if (gameState.equals(gameOver)) {
        coveredMine = Driver.MINE_COUNT - foundMine;
        for (int r = 0; r < Driver.ROW; r++) {
          for (int c = 0; c < Driver.COL; c++) {
            if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() != 9) {
              tile[r][c].setState(Display.BAD_FLAG);
            } else if (tile[r][c].getState() != Display.FLAG
                && tile[r][c].getState() != Display.TRIGGERED_MINE) {
              tile[r][c].setState(Display.UNCOVERED);
            }
          }
        }
      }
    }
    // output the current game progress
    draw();
  }

  /**
   * This method makes a 2d int array of every existing neighbor of the square, in the format of
   * [neighbor index][row col identifier: 0 for row, 1 for col index of neighbor]. The upper left
   * tile is the first in sequence and one under it is the last, going clockwise. If the neighbor
   * does not exist, its place would be held by -1
   * 
   * @param row - the row index of the central tile
   * @param col - the column index of the central tile
   * @return 2d int array of neighboring squares coordinates
   */
  private int[][] neighbors(int row, int col) {
    int[][] output = new int[8][2];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 2; j++) {
        output[i][j] = -1;
      }
    }
    if (row > 0 && col > 0) {
      output[0][0] = row - 1;
      output[0][1] = col - 1;
    }
    if (row > 0) {
      output[1][0] = row - 1;
      output[1][1] = col;
    }
    if (row > 0 && col < Driver.COL - 1) {
      output[2][0] = row - 1;
      output[2][1] = col + 1;
    }
    if (col < Driver.COL - 1) {
      output[3][0] = row;
      output[3][1] = col + 1;
    }
    if (row < Driver.ROW - 1 && col < Driver.COL - 1) {
      output[4][0] = row + 1;
      output[4][1] = col + 1;
    }
    if (row < Driver.ROW - 1) {
      output[5][0] = row + 1;
      output[5][1] = col;
    }
    if (row < Driver.ROW - 1 && col > 0) {
      output[6][0] = row + 1;
      output[6][1] = col - 1;
    }
    if (col > 0) {
      output[7][0] = row;
      output[7][1] = col - 1;
    }
    return output;
  }

  /**
   * Uncover a single covered tile, or uncover neighboring tiles around an uncovered tile. If
   * uncover a tile whose key is 0, uncover its neighbors continuously with a recursive call.
   * 
   * @param row - to row index of the tile to be uncovered
   * @param col - to column index of the tile to be uncovered
   */
  private void uncoverHelp(int row, int col) {
    // ignore tiles that does exist:
    if (row == -1 || col == -1) {
      return;
    }
    // uncover a single tile:
    if (tile[row][col].getKey() != 9 && tile[row][col].getState() != Display.FLAG) {
      tile[row][col].setState(Display.UNCOVERED);
    }
    if (tile[row][col].getKey() == 9 && tile[row][col].getState() == Display.COVERED) {
      gameState = gameOver;
      tile[row][col].setState(Display.TRIGGERED_MINE);
    }
    // base case, all neighbors uncovered:
    int neighbor[][] = neighbors(row, col);
    int coveredNeighbor = 0;
    for (int i = 0; i < 8; i++) {
      if (neighbor[i][0] != -1) {
        if (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.COVERED) {
          coveredNeighbor += 1;
        }
      }
    }
    if (coveredNeighbor == 0) {
      return;
    }
    // recursive call and uncover all neighbors around the tile:
    if (tile[row][col].getState() == Display.UNCOVERED) {
      int count = 0;
      for (int i = 0; i < 8; i++) {
        if (neighbor[i][0] != -1
            && tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.FLAG) {
          count += 1;
        }
      }
      if (count == this.tile[row][col].getKey()) {
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1) {
            if (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.FLAG
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() != 9) {
              gameState = gameOver;
            }
            if (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.COVERED
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() != 9) {
              tile[neighbor[i][0]][neighbor[i][1]].setState(Display.UNCOVERED);
              if (tile[neighbor[i][0]][neighbor[i][1]].getKey() == 0) {
                uncoverHelp(neighbor[i][0], neighbor[i][1]);
              }
            }
            if (tile[neighbor[i][0]][neighbor[i][1]].getState() == Display.COVERED
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() == 9) {
              gameState = gameOver;
              tile[neighbor[i][0]][neighbor[i][1]].setState(Display.TRIGGERED_MINE);
            }
          }
        }
      } else {
        return;
      }
    }
    // recursive call for uncovering a single tile:
    for (int i = 0; i < 8; i++) {
      if (tile[row][col].getKey() == 0) {
        uncoverHelp(neighbor[i][0], neighbor[i][1]);
      }
    }
  }

  /**
   * continuously output the current game progress, displays each individual tile game board
   * according to its displayed state.
   */
  private void draw() {
    // display panel:
    processing.image(panel, Driver.ROW * 16 + 1, 1);
    // display counter:
    if (coveredMine > -1 && coveredMine < 10000) {
      processing.image(panel_num[coveredMine / 1000], Driver.ROW * 16 + 65, 10);
      processing.image(panel_num[coveredMine % 1000 / 100], Driver.ROW * 16 + 92, 10);
      processing.image(panel_num[coveredMine % 100 / 10], Driver.ROW * 16 + 119, 10);
      processing.image(panel_num[coveredMine % 10], Driver.ROW * 16 + 146, 10);
    } else if (-1 * coveredMine / 1000 == 0) {
      processing.image(panel_negative, Driver.ROW * 16 + 65, 10);
      processing.image(panel_num[-1 * coveredMine % 1000 / 100], Driver.ROW * 16 + 92, 10);
      processing.image(panel_num[-1 * coveredMine % 100 / 10], Driver.ROW * 16 + 119, 10);
      processing.image(panel_num[-1 * coveredMine % 10], Driver.ROW * 16 + 146, 10);
    } else {
      processing.image(panel_negative, Driver.ROW * 16 + 65, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 92, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 119, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 146, 10);
    }
    // display timer:
    if (time < 10000) {
      processing.image(panel_num[time / 1000], Driver.ROW * 16 + 302, 10);
      processing.image(panel_num[time % 1000 / 100], Driver.ROW * 16 + 329, 10);
      processing.image(panel_num[time % 100 / 10], Driver.ROW * 16 + 356, 10);
      processing.image(panel_num[time % 10], Driver.ROW * 16 + 383, 10);
    } else {
      processing.image(panel_negative, Driver.ROW * 16 + 302, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 329, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 356, 10);
      processing.image(panel_negative, Driver.ROW * 16 + 383, 10);
    }
    // display new game button:
    if (newGameBtnIsPressed) {
      processing.image(new_game_pressed, Driver.ROW * 16 + 1, 65);
    }
    // display new game button:
    if (menuBtnIsPressed) {
      processing.image(menu_pressed, Driver.ROW * 16 + 210, 65);
    }
    // display control toggle:
    if (gameMode.equals(mouse)) {
      if (ctrlToggleIsPressed) {
        processing.image(toggle_up_pressed, Driver.ROW * 16 + 10, 141);
      } else {
        processing.image(toggle_up, Driver.ROW * 16 + 10, 141);
      }
    }
    if (gameMode.equals(keyboard)) {
      if (ctrlToggleIsPressed) {
        processing.image(toggle_dn_pressed, Driver.ROW * 16 + 10, 141);
      } else {
        processing.image(toggle_dn, Driver.ROW * 16 + 10, 141);
      }
    }
    // display highlight toggle:
    if (highlightIsOn) {
      if (highlightToggleIsPressed) {
        processing.image(toggle_dn_pressed, Driver.ROW * 16 + 10, 205);
      } else {
        processing.image(toggle_dn, Driver.ROW * 16 + 10, 205);
      }
    } else {
      if (highlightToggleIsPressed) {
        processing.image(toggle_up_pressed, Driver.ROW * 16 + 10, 205);
      } else {
        processing.image(toggle_up, Driver.ROW * 16 + 10, 205);
      }
    }
    // display game board:
    for (int row = 0; row < Driver.ROW; row++) {
      for (int col = 0; col < Driver.COL; col++) {
        if (tile[row][col].getState() == Display.COVERED) {
          if (tile[row][col].getIsHighlighted() == false) {
            processing.image(blank, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_blank, row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getState() == Display.UNCOVERED) {
          if (tile[row][col].getKey() == 9) {
            processing.image(mine, row * 16 + 1, col * 16 + 1);
          } else if (tile[row][col].getIsHighlighted() == false) {
            processing.image(num[tile[row][col].getKey()], row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_num[tile[row][col].getKey()], row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getState() == Display.FLAG) {
          if (tile[row][col].getIsHighlighted() == false) {
            processing.image(flag, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_flag, row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getState() == Display.TRIGGERED_MINE) {
          processing.image(triggered_mine, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getState() == Display.BAD_FLAG) {
          processing.image(bad_flag, row * 16 + 1, col * 16 + 1);
        }
      }
    }
  }

}
