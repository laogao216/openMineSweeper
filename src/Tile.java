///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

/**
 * Each instance of this class represents a single tile on the game board.
 * 
 * @author laogao216
 */
public class Tile {

  private int key;
  // 9 if the tile is a mine, otherwise, the tile has 1 ~ 8 neighboring mines
  private Display display;
  // represents the image displayed on this tile
  private boolean isHighlighted;
  // true if tile is highlighted, false if otherwise

  /**
   * Each tile is constructed to initially display a covered state.
   */
  public Tile() {
    key = -1;
    display = Display.COVERED;
    isHighlighted = false;
  }

  /**
   * This accessor is used to access the key of this tile.
   * 
   * @return key of this tile
   */
  public int getKey() {
    return key;
  }

  /**
   * This mutator is used during game initialization to assign a key to this tile.
   * 
   * @param key - assigned to this tile
   */
  public void setKey(int key) {
    this.key = key;
  }

  /**
   * This accessor is used to access the displayed state of this tile.
   * 
   * @return displayed state of this tile
   */
  public Display getDisplay() {
    return display;
  }

  /**
   * This mutator controls the displayed state of this tile as the game progresses.
   * 
   * @param state - that this tile displays
   */
  public void setDisplay(Display display) {
    this.display = display;
  }
  
  /**
   * This accessor is used to access the isHighlighted state of this tile.
   * 
   * @return true if the tile is highlighted, false if otherwise
   */
  public boolean getIsHighlighted() {
    return isHighlighted;
  }

  /**
   * This mutator controls whether this tile is highlighted. 
   * 
   * @param isHightlighted - true if the tile is highlighted, false if otherwise
   */
  public void setIsHighlighted(boolean isHighlighted) {
    this.isHighlighted = isHighlighted;
  }
}
