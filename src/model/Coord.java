package model;

/**
 * Represents a two-dimensional coordinate.
 * Rows extend downward; columns extend rightward.
 */
public final class Coord {
  public final int row;
  public final int col;

  /**
   * Initializes the row and col value.
   * @param row - row of the coordinate.
   * @param col - column of the coordinate.
   */
  public Coord(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public String toString() {
    return String.format("(r%d,c%d)", row, col);
  }

}