///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import processing.core.PApplet;
import javax.swing.JOptionPane;

/**
 * This class is the driver application for Minesweeper.
 * 
 * @author laogao216
 */
public class Driver extends PApplet {

  public static int ROW;
  public static int COL;
  public static int MINE_COUNT;
  private Minesweeper pen;

  /**
   * Initialize ROW, COl, and MINE_COUNT with user input before running PApplet.main()
   */
  public static void main(String[] args) {
    String[] options = {"Easy", "Normal", "Hard", "Custimize"};
    String message = "Easy: 10*10, 10 mines" + System.lineSeparator() + "Normal: 20*20, 60 mines"
        + System.lineSeparator() + "Hard: 50*50, 500 mines";
    int input = JOptionPane.showOptionDialog(null, message, "Choose Difficulty Level",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
    if (input == 0) {
      ROW = 10;
      COL = 10;
      MINE_COUNT = 10;
    } else if (input == 1) {
      ROW = 20;
      COL = 20;
      MINE_COUNT = 60;
    } else if (input == 2) {
      ROW = 50;
      COL = 50;
      MINE_COUNT = 500;
    } else if (input == 3) {
      try {
        String rowInput = JOptionPane.showInputDialog(null, "Length of each row", "Customize (1/3)",
            JOptionPane.QUESTION_MESSAGE);
        ROW = Integer.parseInt(rowInput);
        if (ROW < 1) {
          throw new NumberFormatException();
        }
        String colInput = JOptionPane.showInputDialog(null, "Length of each column",
            "Customize (2/3)", JOptionPane.QUESTION_MESSAGE);
        COL = Integer.parseInt(colInput);
        if (COL < 1) {
          throw new NumberFormatException();
        }
        String mineCountInput = JOptionPane.showInputDialog(null, "Number of mine",
            "Customize (3/3)", JOptionPane.QUESTION_MESSAGE);
        MINE_COUNT = Integer.parseInt(mineCountInput);
        if (MINE_COUNT < 1) {
          throw new NumberFormatException();
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Enter a positive integer", "Customize Unsuccessful",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (MINE_COUNT > ROW * COL - 9) {
        String errorMessage = "There are more mines than the customized dimension can hold"
            + System.lineSeparator() + "Either increase dimension or decrease number of mines";
        JOptionPane.showMessageDialog(null, errorMessage, "Customize Unsuccessful",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    } else {
      return;
    }
    PApplet.main("Driver");
  }

  /**
   * Set up window dimension.
   * 
   * @see https://processing.org/reference/settings_.html
   */
  @Override
  public void settings() {
    size(ROW * 16 + 1, COL * 16 + 1 + 51);
  }

  /**
   * Call Minesweeper constructor with PApplet processing as argument.
   * 
   * @see https://processing.org/reference/setup_.html
   */
  @Override
  public void setup() {
    pen = new Minesweeper(this);
  }

  /**
   * Send user actions to Minesweeper class.
   * 
   * @see https://processing.org/reference/draw_.html
   */
  @Override
  public void draw() {
    pen.update(mouseX, mouseY, mousePressed, mouseButton);
  }

}

// This is a great moment for both this pet project of mine and myself:
// Firstly, I have finished all the todo tags and I like what this is now so I am calling it done.
// I will export the project in one executable file from eclipse for the first time.
// This does not mean I will stop working on it. There are always places to improve. 
// And g*d forbid i catch any bugs. What I mean is it passes as a functional game.
// I will mostly make cosmetic changes from now on, to make the game play more smoothly
// or jsut for the look.
// Secondly, I met someone, and I am going to dedication this program to her. 
// My darling, if you see this, that means you care enough about me to follow my progress here.
// Thank you for that. Also, I want to let you know that I love you. Always.
