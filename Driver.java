///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import processing.core.PApplet;

/*
 * The driver application for Minesweeper
 * 
 * @author laogao216
 */
public class Driver extends PApplet {

  // TODO - for future developments: prompt user for the following three fields
  public static int row = 50;
  public static int col = 50;
  public static int mineCount = 500;

  private Minesweeper pen;

  /*
   * runs PApplet
   */
  public static void main(String[] args) {
    PApplet.main("Driver");
  }

  /*
   * set up window dimension
   */
  public void settings() {
    size(row * 16 + 1, col * 16 + 1);
  }

  /*
   * calling Minesweeper constructor with PApplet processing
   */
  public void setup() {
    pen = new Minesweeper(this);
  }

  /*
   * send user actions to Minesweeper class
   */
  public void draw() {
    pen.update(mouseX, mouseY, mousePressed, mouseButton);
  }

}
