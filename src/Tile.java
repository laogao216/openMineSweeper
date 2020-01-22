///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

/**
 * Each instance of this class represents a single tile on the game board.
 * 
 * @author laogao216
 */
public class Tile {

  private int key;
  // 9 if the tile is a mine, otherwise, the tile has 1 ~ 8 neighboring mines
  private Display state;
  // represents the image displayed on this tile

  /**
   * Each tile is constructed to initially display a covered state.
   */
  public Tile() {
    state = Display.COVERED;
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
   * This accessor is used to access the key of this tile.
   * 
   * @return key of this tile
   */
  public int getKey() {
    return key;
  }

  /**
   * This mutator changes the displayed state of this tile as the game progresses.
   * 
   * @param state - that this tile displays
   */
  public void setState(Display state) {
    this.state = state;
  }

  /**
   * This accessor is used to access the displayed state of this tile.
   * 
   * @return displayed state of this tile
   */
  public Display getState() {
    return state;
  }

}
