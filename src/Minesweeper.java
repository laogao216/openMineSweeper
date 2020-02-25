///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.io.File;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * This class processes and executes user inputs passed from Driver.
 * 
 * @author laogao216
 */
public class Minesweeper {

  private PApplet processing;
  private final PImage[] num = new PImage[9];
  private final PImage flag;
  private final PImage badFlag;
  private final PImage mine;
  private final PImage triggeredMine;
  private final PImage blank;
  private final PImage[] counter = new PImage[10];
  private final PImage counterNegative;
  private Tile[][] tile = new Tile[Driver.row][Driver.col];
  private final String start = "start :)";
  private final String alive = "alive ^-^";
  private final String gameOver = "gameOver *~*";
  private final String victory = "victory ^o^";
  private String gameState;
  // record the state of game progress
  private int coveredMine;
  // number of mines left, displayed on top of game board
  private int[] firstMoveRowCol;
  // record the location of the first move, in case its key is not 0 and game has to restart
  private int rowDisp;
  private int colDisp;
  // move the game board to the right or down. Unit is pixel.

  /**
   * Initialize the above fields and initialize game.
   * 
   * @param processing - the PApplet to be used here
   * @param rowDisp - move the game board to the right. Unit is pixel.
   * @param colDisp - move the game board down. Unit is pixel.
   */
  public Minesweeper(PApplet processing, int rowDisp, int colDisp) {
    this.rowDisp = rowDisp;
    this.colDisp = colDisp;
    gameState = start;
    this.processing = processing;
    firstMoveRowCol = new int[] {-1, -1};
    for (int i = 0; i < 9; i++) {
      String name = "images" + File.separator + Integer.toString(i) + ".png";
      PImage image = processing.loadImage(name);
      num[i] = image;
    }
    flag = processing.loadImage("images" + File.separator + "flag.png");
    badFlag = processing.loadImage("images" + File.separator + "bad_flag.png");
    mine = processing.loadImage("images" + File.separator + "mine.png");
    triggeredMine = processing.loadImage("images" + File.separator + "triggered_mine.png");
    blank = processing.loadImage("images" + File.separator + "blank.png");
    for (int i = 0; i < 10; i++) {
      String name = "images" + File.separator + "counter" + Integer.toString(i) + ".png";
      PImage image = processing.loadImage(name);
      counter[i] = image;
    }
    counterNegative = processing.loadImage("images" + File.separator + "counterNegative.png");
    initGame();
  }

  /**
   * Initializes the Game
   */
  public void initGame() {
    coveredMine = Driver.mineCount;
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        tile[row][col] = new Tile();
      }
    }
    boolean[][] seatAvailable = new boolean[Driver.row][Driver.col];
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        seatAvailable[row][col] = true;
      }
    }
    for (int i = 0; i < Driver.mineCount; i++) {
      Random random = new Random();
      int row = random.nextInt() % Driver.row;
      if (row < 0) {
        row = row * -1;
      }
      int col = random.nextInt() % Driver.col;
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
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
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
   */
  public void update(int mouseX, int mouseY, boolean mousePressed, int mouseButton) {
    // handle restarted game due to unlucky first move:
    if (gameState.equals(start) && firstMoveRowCol[0] != -1) {
      int row = firstMoveRowCol[0];
      int col = firstMoveRowCol[1];
      if (tile[row][col].getKey() == 0) {
        gameState = alive;
        uncoverHelp(row, col);
      } else {
        initGame();
      }
    }
    // handle first move:
    if (mouseX > rowDisp && mouseX < Driver.row * 16 + 1 + rowDisp && mouseY > colDisp
        && mouseY < Driver.col * 16 + 1 + colDisp) {
      if (gameState.equals(start)) {
        int row = (mouseX - rowDisp - 1) / 16;
        int col = (mouseY - colDisp - 1) / 16;
        if (mousePressed == true && mouseButton == 37) {
          if (tile[row][col].getKey() == 0) {
            gameState = alive;
            uncoverHelp(row, col);
          } else {
            firstMoveRowCol[0] = row;
            firstMoveRowCol[1] = col;
            initGame();
          }
        }
        draw();
      }
      // handle main game play. calls draw() until this testing fails, on victory or death:
      if (gameState.equals(alive)) {
        int row = (mouseX - rowDisp - 1) / 16;
        int col = (mouseY - colDisp - 1) / 16;
        int foundMine = 0;
        int wrongMine = 0;
        for (int r = 0; r < Driver.row; r++) {
          for (int c = 0; c < Driver.col; c++) {
            if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() == 9) {
              foundMine += 1;
            }
            if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() != 9) {
              wrongMine += 1;
            }
          }
        }
        // process flagging:
        if (mousePressed == true && mouseButton == 39
            && tile[row][col].getState() == Display.COVERED) {
          tile[row][col].setState(Display.FLAG);
          coveredMine -= 1;
        }
        if (mousePressed == true && mouseButton == 3 && tile[row][col].getState() == Display.FLAG) {
          tile[row][col].setState(Display.COVERED);
          coveredMine += 1;
        }
        // process uncovering:
        if (mousePressed == true && mouseButton == 37) {
          uncoverHelp(row, col);
        }
        // test for victory:
        if (foundMine == Driver.mineCount && wrongMine == 0) {
          gameState = victory;
        }
        // prepare game result:
        if (gameState.equals(victory)) {
          coveredMine = 0;
          for (int r = 0; r < Driver.row; r++) {
            for (int c = 0; c < Driver.col; c++) {
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
          coveredMine = Driver.mineCount - foundMine;
          for (int r = 0; r < Driver.row; r++) {
            for (int c = 0; c < Driver.col; c++) {
              if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() != 9) {
                tile[r][c].setState(Display.BAD_FLAG);
              } else if (tile[r][c].getState() != Display.FLAG
                  && tile[r][c].getState() != Display.TRIGGERED_MINE) {
                tile[r][c].setState(Display.UNCOVERED);
              }
            }
          }
        }
        // output the current game progress
        draw();
      }
    }
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
    if (row > 0 && col < Driver.col - 1) {
      output[2][0] = row - 1;
      output[2][1] = col + 1;
    }
    if (col < Driver.col - 1) {
      output[3][0] = row;
      output[3][1] = col + 1;
    }
    if (row < Driver.row - 1 && col < Driver.col - 1) {
      output[4][0] = row + 1;
      output[4][1] = col + 1;
    }
    if (row < Driver.row - 1) {
      output[5][0] = row + 1;
      output[5][1] = col;
    }
    if (row < Driver.row - 1 && col > 0) {
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
      if (neighbor[i][0] != -1 || neighbor[i][1] != -1) {
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
    // display counter:
    if (coveredMine > -1) {
      processing.image(counter[coveredMine / 1000], 1, 1);
      processing.image(counter[coveredMine % 1000 / 100], 28, 1);
      processing.image(counter[coveredMine % 100 / 10], 55, 1);
      processing.image(counter[coveredMine % 10], 82, 1);
    } else {
      if (-1 * coveredMine / 1000 == 0) {
        processing.image(counterNegative, 1, 1);
        processing.image(counter[-1 * coveredMine % 1000 / 100], 28, 1);
        processing.image(counter[-1 * coveredMine % 100 / 10], 55, 1);
        processing.image(counter[-1 * coveredMine % 10], 82, 1);
      } else {
        processing.image(counterNegative, 1, 1);
        processing.image(counterNegative, 28, 1);
        processing.image(counterNegative, 55, 1);
        processing.image(counterNegative, 82, 1);
      }
    }
    // display game board:
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        if (tile[row][col].getState() == Display.COVERED) {
          processing.image(blank, row * 16 + 1 + rowDisp, col * 16 + 1 + colDisp);
        }
        if (tile[row][col].getState() == Display.UNCOVERED) {
          if (tile[row][col].getKey() == 9) {
            processing.image(mine, row * 16 + 1 + rowDisp, col * 16 + 1 + colDisp);
          } else {
            processing.image(num[tile[row][col].getKey()], row * 16 + 1 + rowDisp,
                col * 16 + 1 + colDisp);
          }
        }
        if (tile[row][col].getState() == Display.FLAG) {
          processing.image(flag, row * 16 + 1 + rowDisp, col * 16 + 1 + colDisp);
        }
        if (tile[row][col].getState() == Display.TRIGGERED_MINE) {
          processing.image(triggeredMine, row * 16 + 1 + rowDisp, col * 16 + 1 + colDisp);
        }
        if (tile[row][col].getState() == Display.BAD_FLAG) {
          processing.image(badFlag, row * 16 + 1 + rowDisp, col * 16 + 1 + colDisp);
        }
      }
    }
  }

}
