package edu.kestrel.fitzpatrick.texttable;

import java.util.List;

public class TableLines {

  private static final char CROSS = '\u253C';
  static final char HORIZONTAL_LINE = '\u2500';
  // If true, rows/columns are separated with lines; otherwise with spaces.
  private boolean useSeparators = true;
  // How far to indent each row of the table.
  private int indent = 0;

  // Used to generate strings containing just spaces.
  private static String SPACES = "                    ";

  public void clear() {
    useSeparators = true;
    indent = 0;
  }

  public String indentation() {
    return spaces(indent);
  }

  /**
   * Create a string containing n spaces.
   * 
   * @param n
   *          The length of the string.
   * @return A string of n spaces.
   */
  public static String spaces(int n) {
    if (n < 0) {
      return "";
    }
    while (n > SPACES.length()) {
      SPACES += SPACES;
    }
    return SPACES.substring(0, n);
  }

  /**
   * @return A vertical line character.
   */
  public char getVerticalLine() {
    return useSeparators ? '\u2502' : ' ';
  }

  /**
   * @return A top-left corner character.
   */
  public char getTopLeftCorner() {
    return useSeparators ? '\u250C' : ' ';
  }

  /**
   * @return A top-middle character.
   */
  public char getTopMiddle() {
    return useSeparators ? '\u252C' : ' ';
  }

  /**
   * @return A top-right corner character.
   */
  public char getTopRightCorner() {
    return useSeparators ? '\u2510' : ' ';
  }

  /**
   * @return A horizontal line character.
   */
  public char getHorizontalLine() {
    return useSeparators ? HORIZONTAL_LINE : ' ';
  }

  /**
   * @return A left-middle character.
   */
  public char getLeftMiddle() {
    return useSeparators ? '\u251C' : ' ';
  }

  /**
   * @return A middle, horizontal line character.
   */
  public char getMiddleMiddle() {
    return useSeparators ? CROSS : ' ';
  }

  /**
   * @return A right-middle character.
   */
  public char getRightMiddle() {
    return useSeparators ? '\u2524' : ' ';
  }

  /**
   * @return A bottom-left corner character.
   */
  public char getBottomLeftCorner() {
    return useSeparators ? '\u2514' : ' ';
  }

  /**
   * @return A bottom-middle character.
   */
  public char getBottomMiddle() {
    return useSeparators ? '\u2534' : ' ';
  }

  /**
   * @return A bottom-right corner character.
   */
  public char getBottomRightCorner() {
    return useSeparators ? '\u2518' : ' ';
  }

  public String makeEmptyLine(List<Integer> widths) {
    StringBuilder s = new StringBuilder();
    if (indent > 0) {
      s.append(spaces(indent));
    }
    s.append(getVerticalLine());
    for (int i = 0; i < widths.size(); i++) {
      if (i > 0) {
        s.append(" ").append(getVerticalLine()).append(" ");
      }
      s.append(spaces(widths.get(i)));
    }
    s.append(getVerticalLine()).append("\n");
    return s.toString();
  }

  public String getHorizontalLine(int n) {
    return repeat(getHorizontalLine(), n);
  }

  public static String repeat(char c, int n) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < n; i++) {
      s.append(c);
    }
    return s.toString();
  }

  public String makeTopLine(List<Integer> widths) {
    StringBuilder s = new StringBuilder();
    if (indent > 0) {
      s.append(spaces(indent));
    }
    s.append(getTopLeftCorner());
    for (int w = 0; w < widths.size(); w++) {
      s.append(getHorizontalLine(widths.get(w)));
      if (w < widths.size() - 1) {
        s.append("").append(getHorizontalLine()).append(getTopMiddle()).append(getHorizontalLine());
      }
    }
    s.append(getTopRightCorner()).append("\n");
    return s.toString();
  }

  public String makeLine(List<Integer> widths) {
    StringBuilder s = new StringBuilder();
    if (indent > 0) {
      s.append(spaces(indent));
    }
    s.append(getLeftMiddle());
    for (int w = 0; w < widths.size(); w++) {
      s.append(getHorizontalLine(widths.get(w)));
      if (w < widths.size() - 1) {
        s.append("").append(getHorizontalLine()).append(getMiddleMiddle()).append(getHorizontalLine());
      }
    }
    s.append(getRightMiddle()).append("\n");
    return s.toString();
  }

  public String makeVisibleLine(List<Integer> widths) {
    StringBuilder s = new StringBuilder();
    if (indent > 0) {
      s.append(spaces(indent));
    }
    s.append(getLeftMiddle());
    for (int w = 0; w < widths.size(); w++) {
      s.append(getHorizontalLine(widths.get(w)));
      if (w < widths.size() - 1) {
        s.append("" + HORIZONTAL_LINE + CROSS + HORIZONTAL_LINE);
      }
    }
    s.append(getRightMiddle()).append("\n");
    return s.toString();
  }

  public String makeBottomLine(List<Integer> widths) {
    StringBuilder s = new StringBuilder();
    if (indent > 0) {
      s.append(spaces(indent));
    }
    s.append(getBottomLeftCorner());
    for (int w = 0; w < widths.size(); w++) {
      s.append(getHorizontalLine(widths.get(w)));
      if (w < widths.size() - 1) {
        s.append("").append(getHorizontalLine()).append(getBottomMiddle()).append(getHorizontalLine());
      }
    }
    s.append(getBottomRightCorner()).append("\n");
    return s.toString();
  }

  public void setIndent(int indent) {
    this.indent = indent;
  }

  public int getIndent() {
    return indent;
  }

  public boolean useSeparators() {
    return useSeparators;
  }

  public void setUseSeparators(boolean useSeparators) {
    this.useSeparators = useSeparators;
  }

}
