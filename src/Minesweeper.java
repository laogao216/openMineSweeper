///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.io.File;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * This class processes and executes user inputs passed from Driver
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
  private Tile[][] tile = new Tile[Driver.row][Driver.col];
  private final String alive = "alive ^-^";
  private final String gameOver = "gameOver *~*";
  private final String victory = "victory ^o^";
  private String gameState = alive;
  private int coveredMine = Driver.mineCount;

  /**
   * Initialize processing as PApplet and initialize the game setup.
   * 
   * @param processing - the PApplet to be used here
   */
  public Minesweeper(PApplet processing) {
    this.processing = processing;
    // load images:
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
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        tile[row][col] = new Tile();
      }
    }
    // populate key:
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
    // draw the initialized game:
    draw();
  }

  /**
   * Called by Driver.draw continuously. process user inputs by altering the state of each square
   * 
   * @param mouseX     - x coordinate of location of the mouse
   * @param mouseY     - y coordinate of location of the mouse
   * @param keyPressed - char from keyboard input
   */
  public void update(int mouseX, int mouseY, char keyPressed) {
    if (gameState.equals(alive) && mouseX > 0 && mouseX < Driver.row * 16 + 1 && mouseY > 0
        && mouseY < Driver.col * 16 + 1) {
      // TODO - restart game if first uncovered square does not have key == 0
      int row = (mouseX - 1) / 16;
      int col = (mouseY - 1) / 16;
      int neighbor[][] = neighbors(row, col);
      int foundMine = 0;
      int covered = 0;
      for (int r = 0; r < Driver.row; r++) {
        for (int c = 0; c < Driver.col; c++) {
          if (tile[r][c].getState() == Display.FLAG && tile[r][c].getKey() == 9) {
            foundMine += 1;
          }
          if (tile[r][c].getState() == Display.COVERED) {
            covered += 1;
          }
        }
      }
      // process flagging:
      if (keyPressed == 'x' && tile[row][col].getState() == Display.COVERED) {
        tile[row][col].setState(Display.FLAG);
        coveredMine -= 1;
      }
      if (keyPressed == 'c' && tile[row][col].getState() == Display.FLAG) {
        tile[row][col].setState(Display.COVERED);
        coveredMine += 1;
      }
      // process uncovering:
      if (keyPressed == 'z') {
        if (tile[row][col].getKey() != 9 && tile[row][col].getState() != Display.FLAG) {
          tile[row][col].setState(Display.UNCOVERED);
          // TODO - uncover continuously with a recursive
        }
        if (tile[row][col].getKey() == 9 && tile[row][col].getState() == Display.COVERED) {
          gameState = gameOver;
          tile[row][col].setState(Display.TRIGGERED_MINE);
        }
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
      }
      // test for victory:
      if (foundMine == Driver.mineCount || foundMine + covered == Driver.mineCount) {
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

  /**
   * makes a 2d int array of every existing neighbor of the square, in the format of [neighbor
   * index][0 for row, 1 for col index of neighbor]. The upper left square is the first in sequence
   * and one under it is the last, going clockwise. If the neighbor does not exist, its place would
   * be held by -1
   * 
   * @param row - the row index of the central square
   * @param col - the column index of the central square
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
   * continuously output the current game progress, displays each individual tile game board
   * according to its displayed state.
   */
  private void draw() {
    System.out.println(coveredMine);
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        if (tile[row][col].getState() == Display.COVERED) {
          processing.image(blank, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getState() == Display.UNCOVERED) {
          if (tile[row][col].getKey() == 9) {
            processing.image(mine, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(num[tile[row][col].getKey()], row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getState() == Display.FLAG) {
          processing.image(flag, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getState() == Display.TRIGGERED_MINE) {
          processing.image(triggeredMine, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getState() == Display.BAD_FLAG) {
          processing.image(badFlag, row * 16 + 1, col * 16 + 1);
        }
      }
    }
  }

}
