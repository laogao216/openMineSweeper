///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JOptionPane;
import processing.core.PApplet;

/**
 * This class is the driver application for Minesweeper.
 * 
 * @author laogao216
 */
public class Driver extends PApplet {

  private static int ROW;
  private static int COL;
  private static int MINE_COUNT;
  private Minesweeper pen;

  /**
   * Set game dimension and starts the game
   */
  public static void main(String[] args) {
    try {
      Object[] saved = Minesweeper.txtParseHelp();
      if ((boolean) saved[0]) {
        ROW = (int) saved[1];
        COL = (int) saved[2];
        MINE_COUNT = (int) saved[3];
      } else {
        String[] options = {"Load Saved", "Easy", "Normal", "Hard", "Custimize"};
        String message = "Easy: 10*10, 10 mines" + System.lineSeparator()
            + "Normal: 20*20, 60 mines" + System.lineSeparator() + "Hard: 50*50, 500 mines";
        int input = JOptionPane.showOptionDialog(null, message, "Choose Difficulty Level",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        if (input == 0) {
          ROW = (int) saved[1];
          COL = (int) saved[2];
          MINE_COUNT = (int) saved[3];
          Minesweeper.txtCompileHelp(true, ROW, COL, MINE_COUNT, (int) saved[4], (int) saved[5],
              (Tile[][]) saved[6]);
        } else if (input == 1) {
          ROW = 10;
          COL = 10;
          MINE_COUNT = 10;
        } else if (input == 2) {
          ROW = 20;
          COL = 20;
          MINE_COUNT = 60;
        } else if (input == 3) {
          ROW = 50;
          COL = 50;
          MINE_COUNT = 500;
        } else if (input == 4) {
          try {
            int MAX_ROW = (Toolkit.getDefaultToolkit().getScreenSize().width - 416) / 16;
            int MAX_COL = (Toolkit.getDefaultToolkit().getScreenSize().height - 88) / 16;
            System.out.print(MAX_ROW);
            String rowInput = JOptionPane.showInputDialog(null, "Length of each row",
                "Customize (1/3)", JOptionPane.QUESTION_MESSAGE);
            ROW = Integer.parseInt(rowInput);
            if (ROW < 1) {
              throw new NumberFormatException();
            }
            if (ROW > MAX_ROW) {
              String errorMessage = "ERROR 000" + System.lineSeparator()
                  + "Rows are too long for your device" + System.lineSeparator()
                  + "Your screen can hold at most " + MAX_ROW + " tiles in each row";
              JOptionPane.showMessageDialog(null, errorMessage, "Customize Unsuccessful",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }
            String colInput = JOptionPane.showInputDialog(null, "Length of each column",
                "Customize (2/3)", JOptionPane.QUESTION_MESSAGE);
            COL = Integer.parseInt(colInput);
            if (COL < 1) {
              throw new NumberFormatException();
            }
            if (COL > MAX_COL) {
              String errorMessage = "ERROR 001" + System.lineSeparator()
                  + "Columns are too long for your device" + System.lineSeparator()
                  + "Your screen can hold at most " + MAX_COL + " tiles in each column";
              JOptionPane.showMessageDialog(null, errorMessage, "Customize Unsuccessful",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }
            String mineCountInput = JOptionPane.showInputDialog(null, "Number of mine",
                "Customize (3/3)", JOptionPane.QUESTION_MESSAGE);
            MINE_COUNT = Integer.parseInt(mineCountInput);
            if (MINE_COUNT < 1) {
              throw new NumberFormatException();
            }
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                "ERROR 020" + System.lineSeparator() + "Enter a positive integer",
                "ERROR 020. Customize Unsuccessful", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (MINE_COUNT > ROW * COL - 9) {
            String errorMessage = "ERROR 030" + System.lineSeparator()
                + "There are more mines than the customized dimension can hold"
                + System.lineSeparator() + "Either increase dimension or decrease number of mines";
            JOptionPane.showMessageDialog(null, errorMessage, "Customize Unsuccessful",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
        } else {
          return;
        }
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "ERROR 100. Cannot parse save.txt", "File Corruption",
          JOptionPane.ERROR_MESSAGE);
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
    if (COL > 23) {
      size(ROW * 16 + 418, COL * 16 + 1);
    } else {
      size(ROW * 16 + 418, 385);
    }
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
    pen.update(mouseX, mouseY, mousePressed, mouseButton, keyPressed ? key : '\0');
  }

  /**
   * This accessor is used to access the length of each row.
   * 
   * @return the length of each row
   */
  public static int getRow() {
    return ROW;
  }

  /**
   * This accessor is used to access the length of each column.
   * 
   * @return the length of each column
   */
  public static int getCol() {
    return COL;
  }

  /**
   * This accessor is used to access the total number of mines.
   * 
   * @return the total number of mines
   */
  public static int getMineCount() {
    return MINE_COUNT;
  }

}
