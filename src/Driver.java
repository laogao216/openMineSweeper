///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import processing.core.PApplet;
import javax.swing.JOptionPane;

/**
 * This class is the driver application for Minesweeper.
 * 
 * @author laogao216
 */
public class Driver extends PApplet {

  // TODO - add custom dimension, use jFrame or jPanel, throw error when mineCount > row * col - 9
  private static int row;
  private static int col;
  private static int mineCount;
  private Minesweeper pen;

  /**
   * runs PApplet
   */
  public static void main(String[] args) {
    String[] options = {"Easy", "Normal", "Hard"};
    String message = "Easy: 10*10, 10 mines" + System.lineSeparator() + "Normal: 20*20, 60 mines"
        + System.lineSeparator() + "Hard: 50*50, 500 mines";
    int input = JOptionPane.showOptionDialog(null, message, "choose difficulty level",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
    if (input == 0) {
      row = 10;
      col = 10;
      mineCount = 10;
    } else if (input == 1) {
      row = 20;
      col = 20;
      mineCount = 60;
    } else if (input == 2) {
      row = 50;
      col = 50;
      mineCount = 500;
    } else {
      return;
    }
    PApplet.main("Driver");
  }

  /**
   * set up window dimension
   * 
   * @see https://processing.org/reference/settings_.html
   */
  @Override
  public void settings() {
    size(row * 16 + 1, col * 16 + 1 + 51);
  }

  /**
   * calling Minesweeper constructor with PApplet processing
   * 
   * @see https://processing.org/reference/setup_.html
   */
  @Override
  public void setup() {
    pen = new Minesweeper(this);
  }

  /**
   * send user actions to Minesweeper class
   * 
   * @see https://processing.org/reference/draw_.html
   */
  @Override
  public void draw() {
    pen.update(mouseX, mouseY, mousePressed, mouseButton);
  }

  /**
   * accessor for row
   * 
   * @return length of rows of tiles
   */
  public static int getRow() {
    return row;
  }

  /**
   * accessor for col
   * 
   * @return length of columns of tiles
   */
  public static int getCol() {
    return col;
  }

  /**
   * accessor for mineCount
   * 
   * @return number of mines
   */
  public static int getMineCount() {
    return mineCount;
  }

}
