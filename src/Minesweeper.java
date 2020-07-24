///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
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
  private final PImage new_game_btn_highlight;
  private final PImage menu_btn_highlight;
  private final PImage pause_btn_highlight;
  private final PImage save_btn_highlight;
  private final PImage load_btn_highlight;
  private final PImage toggle_dn;
  private final PImage toggle_dn_pressed;
  private final PImage toggle_up;
  private final PImage toggle_up_pressed;
  private Tile[][] tile = new Tile[Driver.getRow()][Driver.getCol()];
  private boolean highlightIsOn;
  private final int start = 0;
  private final int alive = 1;
  private final int paused = 2;
  private final int gameOver = 3;
  private final int victory = 4;
  private int gameState;
  private final String mouse = "mouse";
  private final String keyboard = "keyboard";
  private String gameMode;
  private int coveredMine;
  private int[] firstMoveLoc;
  private int[] curLoc;
  private char prevKeyPressed;
  private boolean prevMousePressed;
  private boolean newGameBtnHighlight;
  private boolean menuBtnHighlight;
  private boolean pauseBtnHighlight;
  private boolean saveBtnHighlight;
  private boolean loadBtnHighlight;
  private boolean ctrlToggleHighlight;
  private boolean highlightToggleHighlight;
  private boolean endMessageShown;
  private int time;
  private Timer timer = new Timer();
  private final TimerTask task = new TimerTask() {
    @Override
    public void run() {
      if (gameState == alive) {
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
    newGameBtnHighlight = false;
    menuBtnHighlight = false;
    pauseBtnHighlight = false;
    saveBtnHighlight = false;
    loadBtnHighlight = false;
    ctrlToggleHighlight = false;
    highlightToggleHighlight = false;
    endMessageShown = false;
    processing.fill(0);
    processing.rect(Driver.getRow() * 16 + 1, 1, 418, Driver.getCol() * 16);
    if (Driver.getCol() < 24) {
      processing.fill(0);
      processing.rect(0, Driver.getCol() * 16 + 1, Driver.getRow() * 16,
          384 - Driver.getCol() * 16);
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
    new_game_btn_highlight =
        processing.loadImage("images" + File.separator + "new_game_btn_highlight.png");
    menu_btn_highlight = processing.loadImage("images" + File.separator + "menu_btn_highlight.png");
    pause_btn_highlight =
        processing.loadImage("images" + File.separator + "pause_btn_highlight.png");
    save_btn_highlight = processing.loadImage("images" + File.separator + "save_btn_highlight.png");
    load_btn_highlight = processing.loadImage("images" + File.separator + "load_btn_highlight.png");
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
    for (int row = 0; row < Driver.getRow(); row++) {
      for (int col = 0; col < Driver.getCol(); col++) {
        tile[row][col] = new Tile();
      }
    }
    try {
      Object[] saved = txtParseHelp();
      if ((Boolean) saved[0] != null && (Boolean) saved[0] == true) {
        gameState = paused;
        coveredMine = (int) saved[4];
        time = (int) saved[5];
        tile = (Tile[][]) saved[6];
        try {
          timer.scheduleAtFixedRate(task, 0, 1);
        } catch (IllegalStateException e) {
          e.printStackTrace();
        }
        txtCompileHelp(false, Driver.getRow(), Driver.getCol(), Driver.getMineCount(), coveredMine,
            time, tile);
      } else {
        coveredMine = Driver.getMineCount();
        time = 0;
        boolean[][] seatAvailable = new boolean[Driver.getRow()][Driver.getCol()];
        for (int row = 0; row < Driver.getRow(); row++) {
          for (int col = 0; col < Driver.getCol(); col++) {
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
        for (int i = 0; i < Driver.getMineCount(); i++) {
          Random random = new Random();
          int row = random.nextInt() % Driver.getRow();
          if (row < 0) {
            row = row * -1;
          }
          int col = random.nextInt() % Driver.getCol();
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
        for (int row = 0; row < Driver.getRow(); row++) {
          for (int col = 0; col < Driver.getCol(); col++) {
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
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null,
          "ERROR 100" + System.lineSeparator() + "Cannot parse save.txt", "File Corruption",
          JOptionPane.ERROR_MESSAGE);
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
    if (gameState == start) {
      time = 0;
    }
    // handle first move when game already restarted at least once:
    if (gameState == start && firstMoveLoc[0] != -1) {
      int row = firstMoveLoc[0];
      int col = firstMoveLoc[1];
      if (tile[row][col].getKey() == 0) {
        gameState = alive;
        uncoverHelp(row, col);
        try {
          timer.scheduleAtFixedRate(task, 0, 1);
        } catch (IllegalStateException e) {
          time = 0;
        }
      } else {
        initGame();
      }
    }
    // show game result:
    if (gameState == victory && endMessageShown == false) {
      JOptionPane.showMessageDialog(null,
          "Congratulations, you have finished in " + time / 1000 + " seconds"
              + System.lineSeparator() + "Thank you for playing!",
          "Game Result", JOptionPane.INFORMATION_MESSAGE);
      endMessageShown = true;
    }
    if (gameState == gameOver && endMessageShown == false) {
      JOptionPane.showMessageDialog(null,
          "Better luck next time, you have lasted " + time / 1000 + " seconds"
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
    // handle restart game:
    if (mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 208 && mouseY > 64
        && mouseY < 127 && mousePressed == true && gameState != start) {
      newGameBtnHighlight = true;
    } else {
      newGameBtnHighlight = false;
    }
    if ((key == 'n' || key == 'N'
        || mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 208 && mouseY > 64
            && mouseY < 127 && mouseReleased == true)
        && gameState != start) {
      if (gameState == alive) {
        gameState = paused;
      }
      int input;
      String message = "Progress of this game will be lost if not saved" + System.lineSeparator()
          + "Do you want to proceed?";
      input = JOptionPane.showConfirmDialog(null, message, "Confirmation",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (input == 0) {
        gameState = start;
        firstMoveLoc = new int[] {-1, -1};
        endMessageShown = false;
        initGame();
      } else if (gameState == paused) {
        gameState = alive;
      }
    }
    // handle back to menu:
    if (mouseX > Driver.getRow() * 16 + 210 && mouseX < Driver.getRow() * 16 + 415 && mouseY > 64
        && mouseY < 127 && mousePressed == true) {
      menuBtnHighlight = true;
    } else {
      menuBtnHighlight = false;
    }
    if (key == 'm' || key == 'M'
        || mouseX > Driver.getRow() * 16 + 210 && mouseX < Driver.getRow() * 16 + 415 && mouseY > 64
            && mouseY < 127 && mouseReleased == true) {
      int tempState = gameState;
      gameState = paused;
      int input;
      String message = "Progress of this game will be lost if not saved" + System.lineSeparator()
          + "Do you want to proceed?";
      input = JOptionPane.showConfirmDialog(null, message, "Confirmation",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (input == 0) {
        processing.exit();
        File jar = new File("mines.jar");
        try {
          Desktop.getDesktop().open(jar);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null,
              "Error 200" + System.lineSeparator() + "Cannot launch mines.jar", "File Missing",
              JOptionPane.ERROR_MESSAGE);
        }
      } else {
        gameState = tempState;
      }
    }
    // handle pause:
    if (mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 415 && mouseY > 129
        && mouseY < 192 && mousePressed == true && gameState == alive || gameState == paused) {
      pauseBtnHighlight = true;
    } else {
      pauseBtnHighlight = false;
    }
    if ((key == 'p' || key == 'P'
        || mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 415 && mouseY > 129
            && mouseY < 192 && mouseReleased == true)
        && (gameState == alive || gameState == paused)) {
      if (gameState == paused) {
        gameState = alive;
      } else if (gameState == alive) {
        gameState = paused;
      }
    }
    // handle save game:
    if (mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 208 && mouseY > 194
        && mouseY < 257 && mousePressed == true && (gameState == alive || gameState == paused)) {
      saveBtnHighlight = true;
    } else {
      saveBtnHighlight = false;
    }
    if ((key == 's' || key == 'S'
        || mouseX > Driver.getRow() * 16 + 3 && mouseX < Driver.getRow() * 16 + 208 && mouseY > 194
            && mouseY < 257 && mouseReleased == true)
        && (gameState == alive || gameState == paused)) {
      int tempState = gameState;
      gameState = paused;
      int input;
      String message = "Previously saved game would be overwritten" + System.lineSeparator()
          + "Do you want to proceed?";
      input = JOptionPane.showConfirmDialog(null, message, "Confirmation",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (input == 0) {
        txtCompileHelp(false, Driver.getRow(), Driver.getCol(), Driver.getMineCount(), coveredMine,
            time, tile);
      }
      gameState = tempState;
    }
    // handle load game:
    try {
      Object[] saved = txtParseHelp();
      if (mouseX > Driver.getRow() * 16 + 210 && mouseX < Driver.getRow() * 16 + 415 && mouseY > 194
          && mouseY < 257 && mousePressed == true && (Boolean) saved[0] != null
          && (Boolean) saved[0] == false) {
        loadBtnHighlight = true;
      } else {
        loadBtnHighlight = false;
      }
      if ((key == 'l' || key == 'L'
          || mouseX > Driver.getRow() * 16 + 210 && mouseX < Driver.getRow() * 16 + 415
              && mouseY > 194 && mouseY < 257 && mouseReleased == true)
          && (Boolean) saved[0] != null && (Boolean) saved[0] == false) {
        int tempState = gameState;
        gameState = paused;
        int input;
        String message = "Progress of this game will be lost if not saved" + System.lineSeparator()
            + "Do you want to proceed?";
        input = JOptionPane.showConfirmDialog(null, message, "Confirmation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (input == 0) {
          txtCompileHelp(true, (int) saved[1], (int) saved[2], (int) saved[3], (int) saved[4],
              (int) saved[5], (Tile[][]) saved[6]);
          processing.exit();
          File jar = new File("mines.jar");
          try {
            Desktop.getDesktop().open(jar);
          } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error 200" + System.lineSeparator() + "Cannot launch mines.jar", "File Missing",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          gameState = tempState;
        }
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null,
          "ERROR 100" + System.lineSeparator() + "Cannot parse save.txt", "File Corruption",
          JOptionPane.ERROR_MESSAGE);
    }
    // toggle game control mode:
    if (mouseX > Driver.getRow() * 16 && mouseX < Driver.getRow() * 16 + 418 && mouseY > 255
        && mouseY < 321 && mousePressed == true) {
      ctrlToggleHighlight = true;
    } else {
      ctrlToggleHighlight = false;
    }
    if (key == 'c' || key == 'C'
        || mouseX > Driver.getRow() * 16 && mouseX < Driver.getRow() * 16 + 418 && mouseY > 255
            && mouseY < 321 && mouseReleased == true) {
      if (gameMode.equals(keyboard)) {
        gameMode = mouse;
      } else {
        gameMode = keyboard;
      }
      for (int r = 0; r < Driver.getRow(); r++) {
        for (int c = 0; c < Driver.getCol(); c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
    }
    // toggle highlight:
    if (mouseX > Driver.getRow() * 16 && mouseX < Driver.getRow() * 16 + 418 && mouseY > 320
        && mouseY < 385 && mousePressed == true) {
      highlightToggleHighlight = true;
    } else {
      highlightToggleHighlight = false;
    }
    if (key == 'h' || key == 'H'
        || mouseX > Driver.getRow() * 16 && mouseX < Driver.getRow() * 16 + 418 && mouseY > 320
            && mouseY < 385 && mouseReleased == true) {
      if (highlightIsOn == true) {
        highlightIsOn = false;
        for (int r = 0; r < Driver.getRow(); r++) {
          for (int c = 0; c < Driver.getCol(); c++) {
            tile[r][c].setIsHighlighted(false);
          }
        }
        tile[curLoc[0]][curLoc[1]].setIsHighlighted(true);
      } else {
        highlightIsOn = true;
      }
    }
    // handle highlight:
    if (gameMode.equals(keyboard)) {
      if ((key == 'w' || key == 'W') && curLoc[1] > 0) {
        curLoc[1] -= 1;
      }
      if ((key == 'a' || key == 'A') && curLoc[0] > 0) {
        curLoc[0] -= 1;
      }
      if ((key == 's' || key == 'S') && curLoc[1] < Driver.getCol() - 1) {
        curLoc[1] += 1;
      }
      if ((key == 'd' || key == 'D') && curLoc[0] < Driver.getRow() - 1) {
        curLoc[0] += 1;
      }
      for (int r = 0; r < Driver.getRow(); r++) {
        for (int c = 0; c < Driver.getCol(); c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
      tile[curLoc[0]][curLoc[1]].setIsHighlighted(true);
      if (highlightIsOn) {
        if (tile[curLoc[0]][curLoc[1]].getDisplay() == Display.UNCOVERED) {
          int neighbor[][] = neighbors(curLoc[0], curLoc[1]);
          for (int i = 0; i < 8; i++) {
            if (neighbor[i][0] != -1
                && (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.COVERED
                    || tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.FLAG)) {
              tile[neighbor[i][0]][neighbor[i][1]].setIsHighlighted(true);
            }
          }
        }
      }
    }
    if (highlightIsOn && gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.getRow() * 16 + 1
        && mouseY > 0 && mouseY < Driver.getCol() * 16 + 1 && mousePressed == true
        && mouseButton == 37) {
      for (int r = 0; r < Driver.getRow(); r++) {
        for (int c = 0; c < Driver.getCol(); c++) {
          tile[r][c].setIsHighlighted(false);
        }
      }
      int row = (mouseX - 1) / 16;
      int col = (mouseY - 1) / 16;
      tile[row][col].setIsHighlighted(true);
      if (tile[row][col].getDisplay() == Display.UNCOVERED) {
        int neighbor[][] = neighbors(row, col);
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1
              && (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.COVERED
                  || tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.FLAG)) {
            tile[neighbor[i][0]][neighbor[i][1]].setIsHighlighted(true);
          }
        }
      }
    }
    // handle first move for the first time:
    if (gameState == start && firstMoveLoc[0] == -1) {
      if (gameMode.equals(keyboard)) {
        if (key == 'j' || key == 'J') {
          if (tile[curLoc[0]][curLoc[1]].getKey() == 0) {
            gameState = alive;
            uncoverHelp(curLoc[0], curLoc[1]);
            try {
              timer.scheduleAtFixedRate(task, 0, 1);
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
      if (gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.getRow() * 16 + 1 && mouseY > 0
          && mouseY < Driver.getCol() * 16 + 1) {
        int row = (mouseX - 1) / 16;
        int col = (mouseY - 1) / 16;
        if (mouseReleased == true && mouseButton == 37) {
          if (tile[row][col].getKey() == 0) {
            gameState = alive;
            uncoverHelp(row, col);
            try {
              timer.scheduleAtFixedRate(task, 0, 1);
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
    if (gameState == alive) {
      int foundMine = 0;
      int wrongMine = 0;
      int coveredTile = 0;
      for (int r = 0; r < Driver.getRow(); r++) {
        for (int c = 0; c < Driver.getCol(); c++) {
          if (tile[r][c].getDisplay() == Display.FLAG && tile[r][c].getKey() == 9) {
            foundMine += 1;
          }
          if (tile[r][c].getDisplay() == Display.FLAG && tile[r][c].getKey() != 9) {
            wrongMine += 1;
          }
          if (tile[r][c].getDisplay() == Display.COVERED) {
            coveredTile += 1;
          }
        }
      }
      // process flagging and uncovering:
      if (gameMode.equals(keyboard)) {
        if ((key == 'k' || key == 'K')
            && tile[curLoc[0]][curLoc[1]].getDisplay() == Display.COVERED) {
          tile[curLoc[0]][curLoc[1]].setDisplay(Display.FLAG);
          coveredMine -= 1;
        }
        if ((key == 'l' || key == 'L') && tile[curLoc[0]][curLoc[1]].getDisplay() == Display.FLAG) {
          tile[curLoc[0]][curLoc[1]].setDisplay(Display.COVERED);
          coveredMine += 1;
        }
        if (key == 'j' || key == 'J') {
          uncoverHelp(curLoc[0], curLoc[1]);
        }
      }
      if (gameMode.equals(mouse) && mouseX > 0 && mouseX < Driver.getRow() * 16 + 1 && mouseY > 0
          && mouseY < Driver.getCol() * 16 + 1) {
        int row = (mouseX - 1) / 16;
        int col = (mouseY - 1) / 16;
        if (mouseReleased == true && mouseButton == 39
            && tile[row][col].getDisplay() == Display.COVERED) {
          tile[row][col].setDisplay(Display.FLAG);
          coveredMine -= 1;
          for (int r = 0; r < Driver.getRow(); r++) {
            for (int c = 0; c < Driver.getCol(); c++) {
              tile[r][c].setIsHighlighted(false);
            }
          }
        }
        if (mouseReleased == true && mouseButton == 3
            && tile[row][col].getDisplay() == Display.FLAG) {
          tile[row][col].setDisplay(Display.COVERED);
          coveredMine += 1;
          for (int r = 0; r < Driver.getRow(); r++) {
            for (int c = 0; c < Driver.getCol(); c++) {
              tile[r][c].setIsHighlighted(false);
            }
          }
        }
        if (mouseReleased == true && mouseButton == 37) {
          uncoverHelp(row, col);
        }
      }
      // test for victory:
      if ((foundMine == Driver.getMineCount() || coveredTile == Driver.getMineCount() - foundMine)
          && wrongMine == 0) {
        gameState = victory;
      }
      // prepare game result:
      if (gameState == victory) {
        coveredMine = 0;
        for (int r = 0; r < Driver.getRow(); r++) {
          for (int c = 0; c < Driver.getCol(); c++) {
            if (tile[r][c].getKey() != 9) {
              tile[r][c].setDisplay(Display.UNCOVERED);
            }
            if (tile[r][c].getKey() == 9) {
              tile[r][c].setDisplay(Display.FLAG);
            }
          }
        }
      }
      if (gameState == gameOver) {
        coveredMine = Driver.getMineCount() - foundMine;
        for (int r = 0; r < Driver.getRow(); r++) {
          for (int c = 0; c < Driver.getCol(); c++) {
            if (tile[r][c].getDisplay() == Display.FLAG && tile[r][c].getKey() != 9) {
              tile[r][c].setDisplay(Display.BAD_FLAG);
            } else if (tile[r][c].getDisplay() == Display.COVERED && tile[r][c].getKey() == 9) {
              tile[r][c].setDisplay(Display.MINE);
            } else if (tile[r][c].getDisplay() != Display.FLAG
                && tile[r][c].getDisplay() != Display.TRIGGERED_MINE) {
              tile[r][c].setDisplay(Display.UNCOVERED);
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
    if (row > 0 && col < Driver.getCol() - 1) {
      output[2][0] = row - 1;
      output[2][1] = col + 1;
    }
    if (col < Driver.getCol() - 1) {
      output[3][0] = row;
      output[3][1] = col + 1;
    }
    if (row < Driver.getRow() - 1 && col < Driver.getCol() - 1) {
      output[4][0] = row + 1;
      output[4][1] = col + 1;
    }
    if (row < Driver.getRow() - 1) {
      output[5][0] = row + 1;
      output[5][1] = col;
    }
    if (row < Driver.getRow() - 1 && col > 0) {
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
    if (tile[row][col].getKey() != 9 && tile[row][col].getDisplay() != Display.FLAG) {
      tile[row][col].setDisplay(Display.UNCOVERED);
    }
    if (tile[row][col].getKey() == 9 && tile[row][col].getDisplay() == Display.COVERED) {
      gameState = gameOver;
      tile[row][col].setDisplay(Display.TRIGGERED_MINE);
    }
    // base case, all neighbors uncovered:
    int neighbor[][] = neighbors(row, col);
    int coveredNeighbor = 0;
    for (int i = 0; i < 8; i++) {
      if (neighbor[i][0] != -1) {
        if (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.COVERED) {
          coveredNeighbor += 1;
        }
      }
    }
    if (coveredNeighbor == 0) {
      return;
    }
    // recursive call and uncover all neighbors around the tile:
    if (tile[row][col].getDisplay() == Display.UNCOVERED) {
      int count = 0;
      for (int i = 0; i < 8; i++) {
        if (neighbor[i][0] != -1
            && tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.FLAG) {
          count += 1;
        }
      }
      if (count == this.tile[row][col].getKey()) {
        for (int i = 0; i < 8; i++) {
          if (neighbor[i][0] != -1) {
            if (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.FLAG
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() != 9) {
              gameState = gameOver;
            }
            if (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.COVERED
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() != 9) {
              tile[neighbor[i][0]][neighbor[i][1]].setDisplay(Display.UNCOVERED);
              if (tile[neighbor[i][0]][neighbor[i][1]].getKey() == 0) {
                uncoverHelp(neighbor[i][0], neighbor[i][1]);
              }
            }
            if (tile[neighbor[i][0]][neighbor[i][1]].getDisplay() == Display.COVERED
                && tile[neighbor[i][0]][neighbor[i][1]].getKey() == 9) {
              gameState = gameOver;
              tile[neighbor[i][0]][neighbor[i][1]].setDisplay(Display.TRIGGERED_MINE);
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
   * Compiles data of game progress into a single string before writing it onto save.txt,
   * overwriting anything on the file.
   * 
   * @param loadGame     - true if game is to be loaded now, false if otherwise
   * @param rowLength    - length of each row
   * @param columnLength - length of each column
   * @param totalMines   - total number of mines
   * @param minesLeft    - number of covered mines
   * @param timeElapsed  - value on the timer when game is saved
   * @param tile         - the tile array when game is saved
   */
  public static void txtCompileHelp(boolean loadGame, int rowLength, int columnLength,
      int totalMines, int minesLeft, int timeElapsed, Tile[][] tile) {
    String output = "";
    if (loadGame) {
      output += "LOAD GAME = " + "true" + ";" + System.lineSeparator();
    } else {
      output += "LOAD GAME = " + "false" + ";" + System.lineSeparator();
    }
    output += "ROW LENGTH = " + new Integer(rowLength).toString() + ";" + System.lineSeparator();
    output +=
        "COLUMN LENGTH = " + new Integer(columnLength).toString() + ";" + System.lineSeparator();
    output += "TOTAL MINES = " + new Integer(totalMines).toString() + ";" + System.lineSeparator();
    output += "MINES LEFT = " + new Integer(minesLeft).toString() + ";" + System.lineSeparator();
    output +=
        "TIME ELAPSED = " + new Integer(timeElapsed).toString() + ";" + System.lineSeparator();
    for (int row = 0; row < rowLength; row++) {
      for (int col = 0; col < columnLength; col++) {
        output += "(" + new Integer(row).toString() + ":" + new Integer(col).toString() + ":"
            + new Integer(tile[row][col].getKey()).toString() + ":"
            + tile[row][col].getDisplay().toString() + ")";
        if (col != columnLength - 1 || row != rowLength - 1) {
          output += ", ";
        } else {
          output += ";";
        }
      }
    }
    FileWriter writer;
    try {
      writer = new FileWriter("save.txt");
      writer.write(output);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null,
          "Error 110" + System.lineSeparator() + "Cannot find or access save.txt", "File Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Reads saves.txt and parse it into lines before returning data embedded within each line in the
   * form of an Object array. Items in the array needs to be casted into their respective type
   * before using.
   * 
   * @return a String array of embedded data, by line
   * @throws IOException when save.txt is corrupted
   */
  public static Object[] txtParseHelp() throws IOException {
    Scanner scanner;
    Object[] output = new Object[7];
    try {
      scanner = new Scanner(new File("save.txt"));
      for (int line = 0; line < 7; line++) {
        output[line] = scanner.nextLine();
      }
      scanner.close();
    } catch (NoSuchElementException e) {
      if (((String) output[0]).equals("0")) {
        Object[] outputNull = new Object[1];
        outputNull[0] = null;
        return outputNull;
      } else {
        throw new IOException();
      }
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(null,
          "Error 120" + System.lineSeparator() + "Cannot find save.txt", "File Not Found",
          JOptionPane.ERROR_MESSAGE);
    }
    String line0 = (String) output[0];
    if (line0.substring(12, 16).equals("true")) {
      output[0] = true;
    } else if (line0.substring(12, 17).equals("false")) {
      output[0] = false;
    } else {
      throw new IOException();
    }
    try {
      String line1 = (String) output[1];
      output[1] =
          (int) Integer.parseInt(line1.substring(line1.indexOf("=") + 2, line1.indexOf(";")));
      String line2 = (String) output[2];
      output[2] =
          (int) Integer.parseInt(line2.substring(line2.indexOf("=") + 2, line2.indexOf(";")));
      String line3 = (String) output[3];
      output[3] =
          (int) Integer.parseInt(line3.substring(line3.indexOf("=") + 2, line3.indexOf(";")));
      String line4 = (String) output[4];
      output[4] =
          (int) Integer.parseInt(line4.substring(line4.indexOf("=") + 2, line4.indexOf(";")));
      String line5 = (String) output[5];
      output[5] =
          (int) Integer.parseInt(line5.substring(line5.indexOf("=") + 2, line5.indexOf(";")));
    } catch (NumberFormatException e) {
      throw new IOException();
    }
    for (int line = 1; line < 6; line++) {
      if ((int) output[line] < 1) {
        throw new IOException();
      }
    }
    try {
      String line6 = (String) output[6];
      String[] savedGame = line6.substring(0, line6.indexOf(";")).split(", ");
      Tile[][] saved = new Tile[(int) output[1]][(int) output[2]];
      for (int row = 0; row < (int) output[1]; row++) {
        for (int col = 0; col < (int) output[2]; col++) {
          saved[row][col] = new Tile();
        }
      }
      for (int i = 0; i < savedGame.length; i++) {
        savedGame[i] = savedGame[i].substring(1, savedGame[i].length() - 1);
        String[] savedTile = savedGame[i].split(":");
        int row = Integer.parseInt(savedTile[0]);
        int col = Integer.parseInt(savedTile[1]);
        int key = Integer.parseInt(savedTile[2]);
        saved[row][col].setKey(key);
        saved[row][col].setDisplay(Display.valueOf(savedTile[3]));
        output[6] = saved;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IOException();
    }
    for (int row = 0; row < (int) output[1]; row++) {
      for (int col = 0; col < (int) output[2]; col++) {
        if (((Tile[][]) output[6])[row][col].getKey() == -1) {
          throw new IOException();
        }
      }
    }
    return output;
  }

  /**
   * continuously output the current game progress, displays each individual tile game board
   * according to its displayed state.
   */
  private void draw() {
    // display panel:
    processing.image(panel, Driver.getRow() * 16 + 1, 1);
    // display counter:
    if (coveredMine > -1 && coveredMine < 10000) {
      processing.image(panel_num[coveredMine / 1000], Driver.getRow() * 16 + 65, 10);
      processing.image(panel_num[coveredMine % 1000 / 100], Driver.getRow() * 16 + 92, 10);
      processing.image(panel_num[coveredMine % 100 / 10], Driver.getRow() * 16 + 119, 10);
      processing.image(panel_num[coveredMine % 10], Driver.getRow() * 16 + 146, 10);
    } else if (-1 * coveredMine / 1000 == 0) {
      processing.image(panel_negative, Driver.getRow() * 16 + 65, 10);
      processing.image(panel_num[-1 * coveredMine % 1000 / 100], Driver.getRow() * 16 + 92, 10);
      processing.image(panel_num[-1 * coveredMine % 100 / 10], Driver.getRow() * 16 + 119, 10);
      processing.image(panel_num[-1 * coveredMine % 10], Driver.getRow() * 16 + 146, 10);
    } else {
      processing.image(panel_negative, Driver.getRow() * 16 + 65, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 92, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 119, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 146, 10);
    }
    // display timer:
    int t = time / 1000;
    if (t < 10000) {
      processing.image(panel_num[t / 1000], Driver.getRow() * 16 + 302, 10);
      processing.image(panel_num[t % 1000 / 100], Driver.getRow() * 16 + 329, 10);
      processing.image(panel_num[t % 100 / 10], Driver.getRow() * 16 + 356, 10);
      processing.image(panel_num[t % 10], Driver.getRow() * 16 + 383, 10);
    } else {
      processing.image(panel_negative, Driver.getRow() * 16 + 302, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 329, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 356, 10);
      processing.image(panel_negative, Driver.getRow() * 16 + 383, 10);
    }
    // display panel components:
    if (newGameBtnHighlight) {
      processing.image(new_game_btn_highlight, Driver.getRow() * 16 + 4, 65);
    }
    if (menuBtnHighlight) {
      processing.image(menu_btn_highlight, Driver.getRow() * 16 + 211, 65);
    }
    if (pauseBtnHighlight) {
      processing.image(pause_btn_highlight, Driver.getRow() * 16 + 4, 130);
    }
    if (saveBtnHighlight) {
      processing.image(save_btn_highlight, Driver.getRow() * 16 + 4, 195);
    }
    if (loadBtnHighlight) {
      processing.image(load_btn_highlight, Driver.getRow() * 16 + 211, 195);
    }
    if (gameMode.equals(mouse)) {
      if (ctrlToggleHighlight) {
        processing.image(toggle_up_pressed, Driver.getRow() * 16 + 10, 269);
      } else {
        processing.image(toggle_up, Driver.getRow() * 16 + 10, 269);
      }
    }
    if (gameMode.equals(keyboard)) {
      if (ctrlToggleHighlight) {
        processing.image(toggle_dn_pressed, Driver.getRow() * 16 + 10, 269);
      } else {
        processing.image(toggle_dn, Driver.getRow() * 16 + 10, 269);
      }
    }
    if (highlightIsOn) {
      if (highlightToggleHighlight) {
        processing.image(toggle_dn_pressed, Driver.getRow() * 16 + 10, 333);
      } else {
        processing.image(toggle_dn, Driver.getRow() * 16 + 10, 333);
      }
    } else {
      if (highlightToggleHighlight) {
        processing.image(toggle_up_pressed, Driver.getRow() * 16 + 10, 333);
      } else {
        processing.image(toggle_up, Driver.getRow() * 16 + 10, 333);
      }
    }
    // display game board:
    for (int row = 0; row < Driver.getRow(); row++) {
      for (int col = 0; col < Driver.getCol(); col++) {
        if (tile[row][col].getDisplay() == Display.COVERED) {
          if (tile[row][col].getIsHighlighted() == false) {
            processing.image(blank, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_blank, row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getDisplay() == Display.UNCOVERED) {
          if (tile[row][col].getIsHighlighted() == false) {
            processing.image(num[tile[row][col].getKey()], row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_num[tile[row][col].getKey()], row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getDisplay() == Display.FLAG) {
          if (tile[row][col].getIsHighlighted() == false) {
            processing.image(flag, row * 16 + 1, col * 16 + 1);
          } else {
            processing.image(highlighted_flag, row * 16 + 1, col * 16 + 1);
          }
        }
        if (tile[row][col].getDisplay() == Display.MINE) {
          processing.image(mine, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getDisplay() == Display.TRIGGERED_MINE) {
          processing.image(triggered_mine, row * 16 + 1, col * 16 + 1);
        }
        if (tile[row][col].getDisplay() == Display.BAD_FLAG) {
          processing.image(bad_flag, row * 16 + 1, col * 16 + 1);
        }
      }
    }
  }

}
