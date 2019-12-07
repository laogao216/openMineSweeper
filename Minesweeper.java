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
  private PImage[] num = new PImage[9];
  private PImage flag;
  private PImage badFlag;
  private PImage mine;
  private PImage triggeredMine;
  private PImage blank;
  private int[][] key = new int[Driver.row][Driver.col];
  // represents game board. Value: 1 ~ 8 for normal squares, 9 for mine squares
  private String[][] state = new String[Driver.row][Driver.col];
  // represents game progress. controls what is displayed on each square
  private boolean gameOver = false;
  // TODO - for future developments: add mines left, message, and new game button
  // private final static String ALIVE = "alive ^-^";
  // private final static String gameOver = "gameOver *~*";
  // private final static String WIN = "victory ^o^";

  /**
   * initialize processing as PApplet and initialize the game setup
   * 
   * @param processing - the PApplet to be used here
   */
  public Minesweeper(PApplet processing) {
    this.processing = processing;
    // load images
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
    // populate key
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
        key[row][col] = 9;
        seatAvailable[row][col] = false;
      } else {
        i -= 1;
      }
    }
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        if (key[row][col] != 9) {
          int count = 0;
          int neighbor[][] = neighbors(row, col);
          for (int i = 0; i < 8; i++) {
            if (neighbor[i][0] != -1 && key[neighbor[i][0]][neighbor[i][1]] == 9) {
              count += 1;
            }
          }
          key[row][col] = count;
        }
      }
    }
    // initialize state to "covered" for each square
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        state[row][col] = "covered";
      }
    }
    // draw the initialized game, ready for player action
    draw();
  }

  /**
   * called by Driver.draw continuously. process user inputs by altering the state of each square
   * 
   * @param mouseX       - x coordinate of location of the mouse
   * @param mouseY       - y coordinate of location of the mouse
   * @param mousePressed - true is mouse is pressed, false if otherwise
   * @param mouseButton  - the button on the mouse that is pressed: left = 37, center = 3, right =
   *                     39
   */
  public void update(int mouseX, int mouseY, boolean mousePressed, int mouseButton) {
    // TODO - if covered = mines left, flag all
    if (gameOver == false && mousePressed == true && mouseX > 0 && mouseX < Driver.row * 16 + 1
        && mouseY > 0 && mouseY < Driver.col * 16 + 1) {
      // TODO - restart game if first move does not have key == 0
      int row = (mouseX - 1) / 16;
      int col = (mouseY - 1) / 16;
      int neighbor[][] = neighbors(row, col);
      if (key[row][col] == 9 && mouseButton == 37 && state[row][col].equals("covered")) {
        gameOver = true;
        state[row][col] = "triggeredMine";
        for (int r = 0; r < Driver.row; r++) {
          for (int c = 0; c < Driver.col; c++) {
            if (state[r][c].equals("flagged") && key[r][c] != 9) {
              state[r][c] = "badFlag";
            } else if (state[r][c].equals("flagged") == false
                && state[r][c].equals("triggeredMine") == false) {
              state[r][c] = "uncovered";
            }
          }
        }
      }
      if (key[row][col] != 9 && mouseButton == 37 && state[row][col].equals("flagged") == false) {
        state[row][col] = "uncovered";
        // FIXME - uncover continuously with a recursive
      }
      if (state[row][col].equals("covered") && mouseButton == 39) {
        state[row][col] = "flagged";
      }
      if (state[row][col].equals("flagged") && mouseButton == 3) {
        state[row][col] = "covered";
      }
      if (state[row][col].equals("uncovered") && mouseButton == 3) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1 && state[neighbor[i][0]][neighbor[i][1]].equals("flagged")) {
            count += 1;
          }
        }
        if (count == this.key[row][col]) {
          for (int i = 0; i < 8; i++) {
            if (neighbor[i][0] != -1 && state[neighbor[i][0]][neighbor[i][1]].equals("covered")) {
              state[neighbor[i][0]][neighbor[i][1]] = "uncovered";
              if (key[neighbor[i][0]][neighbor[i][1]] == 9) {
                gameOver = true;
              }
            }
          }
          if (gameOver == true) {
            for (int r = 0; r < Driver.row; r++) {
              for (int c = 0; c < Driver.col; c++) {
                if (state[r][c].equals("covered")) {
                  state[r][c] = "uncovered";
                }
                if (state[r][c].equals("flagged") && key[r][c] != 9) {
                  state[r][c] = "badFlag";
                }
              }
            }
            for (int i = 0; i < 8; i++) {
              if (state[neighbor[i][0]][neighbor[i][1]].equals("uncovered")
                  && key[neighbor[i][0]][neighbor[i][1]] == 9) {
                state[neighbor[i][0]][neighbor[i][1]] = "triggeredMine";
              }
            }
          }
        }
      }
    }
    draw();
  }

  /**
   * makes a 2d int array of every existing neighbor of the square. The upper left square is the
   * first in sequence and one under it is the last, going clockwise. If the neighbor does not
   * exist, its place would be held by -1
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

  /*
   * continuously output the current game progress, executes user inputs based on the state of each
   * individual square
   */
  private void draw() {
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        if (state[row][col].equals("covered")) {
          processing.image(blank, row * 16 + 1, col * 16 + 1);
        }
        if (state[row][col].equals("uncovered")) {
          if (key[row][col] == 9) {
            processing.image(mine, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(num[key[row][col]], row * 16 + 1, col * 16 + 1);
          }
        }
        if (state[row][col].equals("flagged")) {
          processing.image(flag, row * 16 + 1, col * 16 + 1);
        }
        if (state[row][col].equals("triggeredMine")) {
          processing.image(triggeredMine, row * 16 + 1, col * 16 + 1);
        }
        if (state[row][col].equals("badFlag")) {
          processing.image(badFlag, row * 16 + 1, col * 16 + 1);
        }
      }
    }
  }

  /**
   * print method for debugging
   */
  private static void p(Object a) {
    System.out.print(a);
  }

  /**
   * println method for debugging
   */
  private static void pln(Object a) {
    System.out.println(a);
  }

}
