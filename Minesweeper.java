///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.io.File;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

/*
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
  private String[][] state = new String[Driver.row][Driver.col];
  // TODO - for future developments: add mines left, message, and new game button
  // private final static String ALIVE = "alive ^-^";
  // private final static String DEAD = "dead *~*";
  // private final static String WIN = "victory ^o^";

  /*
   * initialize processing as PApplet and initialize the game setup
   * 
   * @param processing - the PApplet to be used here
   */
  public Minesweeper(PApplet processing) {
    this.processing = processing;
    initGame();
  }

  /*
   * initializes the game setup
   */
  private void initGame() {
    loadImages();
    genKey();
    initState();
  }

  /*
   * load 16*16 images that represent a single square to private PImage fields
   */
  private void loadImages() {
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
  }

  /*
   * randomize mine locations and generate a number map around mines, stored in int array key
   */
  private void genKey() {
    boolean[][] seatAvailable = new boolean[Driver.row][Driver.col];
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        seatAvailable[row][col] = true;
      }
    }
    for (int i = 0; i < Driver.mineCount; i++) {
      int row = randInt(Driver.row);
      int col = randInt(Driver.row);
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
          if (row > 0 && col > 0 && key[row - 1][col - 1] == 9)
            count += 1;
          if (row > 0 && key[row - 1][col] == 9)
            count += 1;
          if (row > 0 && col < Driver.col - 1 && key[row - 1][col + 1] == 9)
            count += 1;
          if (col < Driver.col - 1 && key[row][col + 1] == 9)
            count += 1;
          if (row < Driver.row - 1 && col < Driver.col - 1 && key[row + 1][col + 1] == 9)
            count += 1;
          if (row < Driver.row - 1 && key[row + 1][col] == 9)
            count += 1;
          if (row < Driver.row - 1 && col > 0 && key[row + 1][col - 1] == 9)
            count += 1;
          if (col > 0 && key[row][col - 1] == 9)
            count += 1;
          key[row][col] = count;
        }
      }
    }
  }

  /*
   * generate a random int between 0 (inclusive) and bound (exclusive)
   * 
   * @param bound - the int that the max return value is one fewer
   * 
   * @return random int between 0 (inclusive) and bound (exclusive)
   */
  private int randInt(int bound) {
    Random random = new Random();
    int output = random.nextInt() % bound;
    if (output < 0)
      output = output * -1;
    return output;
  }

  /*
   * initialize String array state[row][col] over every individual square to "covered"
   */
  private void initState() {
    for (int row = 0; row < Driver.row; row++) {
      for (int col = 0; col < Driver.col; col++) {
        state[row][col] = "covered";
      }
    }
  }

  /*
   * called by Driver.draw continuously. process user inputs by altering the state of each square
   * 
   * @param mouseX - x coordinate of location of the mouse
   * 
   * @param mouseY - y coordinate of location of the mouse
   * 
   * @param mousePressed - true is mouse is pressed, false if otherwise
   * 
   * @param mouseButton - the button on the mouse that is pressed: left = 37, center = 3, right = 39
   */
  public void update(int mouseX, int mouseY, boolean mousePressed, int mouseButton) {
    // FIXME - restart game if first move kills
    // TODO - for future developments: if covered = mines left, flag all
    if (mousePressed == true && mouseX > 0 && mouseX < Driver.row * 16 + 1 && mouseY > 0
        && mouseY < Driver.col * 16 + 1) {
      if (key[(mouseX - 1) / 16][(mouseY - 1) / 16] == 9 && mouseButton == 37
          && state[(mouseX - 1) / 16][(mouseY - 1) / 16].equals("covered")) {
        state[(mouseX - 1) / 16][(mouseY - 1) / 16] = "triggeredMine";
        for (int row = 0; row < Driver.row; row++) {
          for (int col = 0; col < Driver.col; col++) {
            if (state[row][col].equals("flagged") && key[row][col] != 9) {
              state[row][col] = "badFlag";
            } else if (state[row][col].equals("flagged") == false
                && state[row][col].equals("triggeredMine") == false) {
              state[row][col] = "uncovered";
            }
          }
        }
      }
      if (key[(mouseX - 1) / 16][(mouseY - 1) / 16] != 9 && mouseButton == 37
          && state[(mouseX - 1) / 16][(mouseY - 1) / 16].equals("flagged") == false) {
        state[(mouseX - 1) / 16][(mouseY - 1) / 16] = "uncovered";
        // FIXME - uncover continuously with a recursive
      }
      if (state[(mouseX - 1) / 16][(mouseY - 1) / 16].equals("covered") && mouseButton == 39) {
        state[(mouseX - 1) / 16][(mouseY - 1) / 16] = "flagged";
      }
      if (state[(mouseX - 1) / 16][(mouseY - 1) / 16].equals("flagged") && mouseButton == 3) {
        state[(mouseX - 1) / 16][(mouseY - 1) / 16] = "covered";
      }
      if (state[(mouseX - 1) / 16][(mouseY - 1) / 16].equals("uncovered") && mouseButton == 3) {
        int row = (mouseX - 1) / 16;
        int col = (mouseY - 1) / 16;
        int key = this.key[row][col];
        int count = 0;
        if (row > 0 && col > 0 && state[row - 1][col - 1].equals("flagged"))
          count += 1;
        if (row > 0 && state[row - 1][col].equals("flagged"))
          count += 1;
        if (row > 0 && col < Driver.col - 1 && state[row - 1][col + 1].equals("flagged"))
          count += 1;
        if (col < Driver.col - 1 && state[row][col + 1].equals("flagged"))
          count += 1;
        if (row < Driver.row - 1 && col < Driver.col - 1
            && state[row + 1][col + 1].equals("flagged"))
          count += 1;
        if (row < Driver.row - 1 && state[row + 1][col].equals("flagged"))
          count += 1;
        if (row < Driver.row - 1 && col > 0 && state[row + 1][col - 1].equals("flagged"))
          count += 1;
        if (col > 0 && state[row][col - 1].equals("flagged"))
          count += 1;
        if (count == key) {
          boolean dead = false;
          if (row > 0 && col > 0 && state[row - 1][col - 1].equals("covered"))
            if (death(row - 1, col - 1) == true)
              dead = true;
          if (row > 0 && state[row - 1][col].equals("covered"))
            if (death(row - 1, col) == true)
              dead = true;
          if (row > 0 && col < Driver.col - 1 && state[row - 1][col + 1].equals("covered"))
            if (death(row - 1, col + 1) == true)
              dead = true;
          if (col < Driver.col - 1 && state[row][col + 1].equals("covered"))
            if (death(row, col + 1) == true)
              dead = true;
          if (row < Driver.row - 1 && col < Driver.col - 1
              && state[row + 1][col + 1].equals("covered"))
            if (death(row + 1, col + 1) == true)
              dead = true;
          if (row < Driver.row - 1 && state[row + 1][col].equals("covered"))
            if (death(row + 1, col) == true)
              dead = true;
          if (row < Driver.row - 1 && col > 0 && state[row + 1][col - 1].equals("covered"))
            if (death(row + 1, col - 1) == true)
              dead = true;
          if (col > 0 && state[row][col - 1].equals("covered"))
            if (death(row, col - 1) == true)
              dead = true;
          if (dead == true) {
            for (int r = 0; r < Driver.row; r++) {
              for (int c = 0; c < Driver.col; c++) {
                if ((r >= row - 1 && r <= row + 1 && c >= col - 1 && c <= col + 1) == false)
                  state[r][c] = "uncovered";
              }
            }
            if (row > 0 && col > 0)
              deathSeq(row - 1, col - 1);
            if (row > 0)
              deathSeq(row - 1, col);
            if (row > 0 && col < Driver.col - 1)
              deathSeq(row - 1, col + 1);
            if (col < Driver.col - 1)
              deathSeq(row, col + 1);
            if (row < Driver.row - 1 && col < Driver.col - 1)
              deathSeq(row + 1, col + 1);
            if (row < Driver.row - 1)
              deathSeq(row + 1, col);
            if (row < Driver.row - 1 && col > 0)
              deathSeq(row + 1, col - 1);
            if (col > 0)
              deathSeq(row, col - 1);
          }
        }
      }
    }
    draw();
  }

  /*
   * checks if unflagged mine is uncovered, which causes death
   * 
   * @param row - the row index of the square in question
   * 
   * @param col - the column index of the square in question
   * 
   * @return true if the square in question is an unflagged mine, causing death, false if the square
   * in question is not a mine or is a flagged mine, not fatal
   */
  private boolean death(int row, int col) {
    if (key[row][col] != 9) {
      state[row][col] = "uncovered";
      return false;
    } else if (state[row][col].equals("flagged")) {
      return false;
    }
    return true;
  }

  /*
   * post mortem operations: change states of badFlags and triggeredMines to show cause of death
   * 
   * @param row - the row index of the square in question
   * 
   * @param col - the column index of the square in question
   */
  private void deathSeq(int row, int col) {
    if (key[row][col] != 9 && state[row][col].equals("flagged"))
      state[row][col] = "badFlag";
    else if (key[row][col] == 9 && state[row][col].equals("flagged") == false)
      state[row][col] = "triggeredMine";
    else
      state[row][col] = "uncovered";
  }

  /*
   * continuously output the current game progress, executes user inputs based on the state of each
   * individual square
   */
  private void draw() {
    // FIXME - lock down after victory/death
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

  /*
   * print method for debugging
   */
  private static void p(Object a) {
    System.out.print(a);
  }

  /*
   * println method for debugging
   */
  private static void pln(Object a) {
    System.out.println(a);
  }

}
