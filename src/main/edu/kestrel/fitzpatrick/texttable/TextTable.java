
package edu.kestrel.fitzpatrick.texttable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class to layout data in a plain-text table, for viewing in a text editor using a mono-space font. (Can also
 * output CSV.)
 * 
 * <p>
 * A simple table comprises a number of rows, each with a number of items:</br>
 * TextTable table = new TextTable();</br>
 * table.add("One", "Five");</br>
 * table.add("Green", "Yellow");</br>
 * System.out.println(table);
 * </p>
 * 
 * <p>
 * The table's toString() method aligns corresponding items (e.g., "One" and "Green") into columns, using spaces and
 * various Unicode line/corner characters.
 * </p>
 * 
 * <p>
 * There are methods to set column headers and alignments.
 * </p>
 * 
 * <p>
 * s A row may alternatively be a single item that spans the entire width of the table, across all columns. This can be
 * used to break the rows into blocks. e.g., addSpan("January").
 * </p>
 * 
 * <p>
 * The addLine() method draws a horizontal line across the table. The addBlank() method adds an empty row.
 * </p>
 * 
 * <p>
 * A table uses various line and corner Unicode characters to draw separators betweens rows and columns. These can be
 * turned off (so that spaces are used instead) using setUseSeparators(false).
 * </p>
 * 
 * @author Stephen Fitzpatrick
 * @version 0.5
 * 
 */
public class TextTable {

  public static enum Alignment {
    /**
     * Determines how the data in a given column is aligned: left, right or centred.
     */
    Left, Right, Centre, Default;

    /**
     * Return a string of the given length containing the given value, aligned left, centre or right.
     * 
     * @param length
     *          The length of the resulting string. Should be longer than the value's length.
     * @param value
     *          The formatted string.
     * @return
     */
    private String format(int length, String value) {
      int nSpaces = length - value.length();
      if (nSpaces <= 0) {
        return value;
      }
      switch (this) {
      case Right:
        return TableLines.spaces(nSpaces) + value;
      case Centre: {
        int left = nSpaces / 2;
        int right = nSpaces - left;
        return TableLines.spaces(left) + value + TableLines.spaces(right);
      }
      case Left:
      case Default:
      default:
        return value + TableLines.spaces(nSpaces);
      }
    }
  }

  // The table's (top) caption.
  private String topCaption = null;
  // The column headers.
  private final List<Object> headers = new ArrayList<>();
  // The column alignments.
  private final List<Alignment> alignments = new ArrayList<>();
  // The rows of the table.
  private final List<TableRow> rows = new ArrayList<>();
  private final TableLines lines = new TableLines();

  // Auto-suppresses output of a value in leading columns, up to this column number.
  private int autoSuppressColumns = 0;
  // When auto-suppressing and first column is output, prepend a line.
  private boolean outputLineOnOutermostSuppress = true;

  final List<String> previousEntries = new ArrayList<>();

  /**
   * Create an empty table.
   */
  public TextTable() {
  }

  /**
   * Create an empty table with the given caption.
   * 
   * @param caption
   *          The table's (top) caption.
   */
  public TextTable(String caption) {
    setTopCaption(caption);
  }

  /**
   * Convenience method to create a table with one row and no separators.
   * 
   * @param row
   *          The table's row.
   * @return A new table containing the given row.
   */
  public static TextTable horizontal(Object... row) {
    TextTable table = new TextTable();
    table.setUseSeparators(false);
    table.row(row);
    return table;
  }

  /**
   * Convenience method to create a table with one column and now separators. Each row contains one value from the given
   * array.
   * 
   * @param column
   *          The values contained by table's rows.
   * @return A new table containing the values, one per row.
   */
  public static TextTable vertical(Object... column) {
    TextTable table = new TextTable();
    table.setUseSeparators(false);
    for (Object obj : column) {
      table.row(obj);
    }
    return table;
  }

  /**
   * Clear the table.
   */
  public void clear() {
    headers.clear();
    alignments.clear();
    rows.clear();
    lines.clear();
    topCaption = null;
  }

  /**
   * Set the table's indentation.
   * 
   * Each normal row will be indented by this number of spaces. (Span rows are not indented.)
   *
   * @param indent
   */
  public void setIndent(int indent) {
    lines.setIndent(indent);
  }

  /**
   * Get the table's indentation.
   *
   * @return The number of spaces by which the table will be indented.
   */
  public int getIndent() {
    return lines.getIndent();
  }

  /**
   * Get the table's (top) caption.
   * 
   * @return The (top) caption.
   */
  public String getTopCaption() {
    return topCaption;
  }

  /**
   * Set the caption that is displayed, centred, at the top of the table.
   *
   * @param caption
   *          The (top) caption.
   */
  public void setTopCaption(String caption) {
    topCaption = caption;
  }

  /**
   * Set the table's column headers. Each header is centred.
   *
   * The header row is displayed each time a block of columnar rows begins. (e.g., a spanning row followed by a columnar
   * row.)
   *
   * @param objs
   *          The column headers.
   */
  public void setHeaders(Object... objs) {
    setHeaders(Arrays.asList(objs));
  }

  /**
   * Set the table's column headers from the elements of the list. Each header is centred.
   *
   * The header row is displayed each time a block of columnar rows begins. (e.g., a spanning row followed by a columnar
   * row.)
   *
   * @param objs
   *          The column headers.
   */
  public void setHeaders(List<Object> objs) {
    headers.clear();
    int column = 0;
    Alignment lastAlignment = null;
    for (int i = 0; i < objs.size(); i++) {
      Object obj = objs.get(i);
      if (obj instanceof Alignment) {
        lastAlignment = (Alignment) obj;
      } else {
        setHeader(column, obj);
        if (lastAlignment != null) {
          setAlignment(column, lastAlignment);
        }
        column += 1;
      }
    }
  }

  /**
   * Append the given values to the table's column headers.
   * 
   * @param objs
   *          The additional column headers.
   */
  public void appendHeaders(Object... objs) {
    for (Object obj : objs) {
      headers.add(obj.toString());
    }
  }

  /**
   * Get the column headers.
   * 
   * @return The column headers.
   */
  public List<Object> getHeaders() {
    return headers;
  }

  /**
   * Set the alignment of the columns. Any number of alignments can be given - if there are more columns, they will use
   * the default (left) alignment.
   *
   * @param alignments
   *          The column alignments.
   */
  public void setAlignments(Alignment... alignments) {
    this.alignments.clear();
    appendAlignments(alignments);
  }

  /**
   * Set the column alignments from the elements of the list.
   * 
   * @param alignments
   *          The column alignments.
   */
  public void setAlignments(List<Alignment> alignments) {
    this.alignments.clear();
    this.alignments.addAll(alignments);
  }

  /**
   * Append the given alignments to the column alignments.
   * 
   * @param alignments
   *          The additional alignments.
   */
  public void appendAlignments(Alignment... alignments) {
    Collections.addAll(this.alignments, alignments);
  }

  /**
   * Get the column alignments.
   * 
   * @return The column alignments.
   */
  public List<Alignment> getAlignments() {
    return alignments;
  }

  /**
   * If true, use lines to separate rows/columns; otherwise, spaced.
   * 
   * @return The useSpearators property.
   */
  public boolean useSeparators() {
    return lines.useSeparators();
  }

  /**
   * Set whether to use lines to separate rows/columns (true); or spaces (false).
   * 
   * @param useSeparators
   *          The new value of the useSeparators property.
   */
  public void setUseSeparators(boolean useSeparators) {
    lines.setUseSeparators(useSeparators);
    ;
  }

  /**
   * Add a row of values.
   * 
   * The rows need not contain the same number of columns - shorter rows will be extended with empty cells to match the
   * longest row.
   *
   * @param objs
   */
  public void row(Object... objs) {
    rows.add(new TableRow.Entries(objs));
  }

  /**
   * Add the elements of the list as a row of values (as distinct from adding a row containing the list as its single
   * value).
   * 
   * @param objs
   *          Values to be added as a row to the table.
   */
  public void rowFrom(Collection<Object> objs) {
    row(objs.toArray());
  }

  /**
   * Append the given values to the current bottom row of the table.
   * 
   * @param objs
   *          Values to be appended to the current bottom row of the table.
   */
  public void append(Object... objs) {
    TableRow.Entries row;
    if (rows.isEmpty() || !(rows.get(rows.size() - 1) instanceof TableRow.Entries)) {
      row = new TableRow.Entries();
      rows.add(row);
    } else {
      row = (TableRow.Entries) rows.get(rows.size() - 1);
    }
    for (Object s : objs) {
      row.append(s);
    }
  }

  /**
   * Add one row to the table for each element of the collection.
   * 
   * @param collection
   *          A collection of values.
   * @param rowMaker
   *          A function applied to each value to get the items for its row.
   */
  public <A> void populate(Collection<A> collection, Function<A, Object[]> rowMaker) {
    for (A a : collection) {
      row(rowMaker.apply(a));
    }
  }

  /**
   * Add one row to the table for each element of the array.
   * 
   * @param collection
   *          An array of values.
   * @param rowMaker
   *          A function applied to each value to get the items for its row.
   */
  public <A> void populate(A[] array, Function<A, Object[]> rowMaker) {
    for (A a : array) {
      row(rowMaker.apply(a));
    }
  }

  /**
   * Add one row to the table for each element supplied by the iterator.
   * 
   * @param collection
   *          An iterator of values.
   * @param rowMaker
   *          A function applied to each value to get the items for its row.
   */
  public <A> void populate(Iterator<A> iterator, Function<A, Object[]> rowMaker) {
    iterator.forEachRemaining(a -> row(rowMaker.apply(a)));
  }

  /**
   * Add a row that spans across the table in a contiguous entry, rather than being broken into columns.
   * 
   * A spanning row does not affect the alignment of the columns, nor is it affected by the columns. The given items are
   * simply displayed (concatenated) across the row.
   *
   * The spanning rows break a table into blocks of contiguous rows of columns. The header row (if there is one) is
   * displayed at the start of each these blocks.
   *
   * @param objs
   */
  public void addSpan(Object... objs) {
    rows.add(new TableRow.Span(objs));
  }

  /**
   * Add a blank row of spaces.
   * 
   */
  public void addBlank() {
    rows.add(new TableRow.Skip());
  }

  /**
   * Add a horizontal line.
   */
  public void addLine() {
    rows.add(new TableRow.Line());
  }

  /**
   * Does the table have any rows (of any kind)?
   * 
   * @return Whether or not the table has any rows.
   */
  public boolean isEmpty() {
    return rows.isEmpty();
  }

  /**
   * Get the number of columns in the table.
   * 
   * @return The number of columns in the table.
   */
  public int getNumberOfColumns() {
    return getWidths(getBlocks()).size();
  }

  /**
   * The values in leading columns can be automatically suppressed if they are the same as in the previous row. This
   * parameter says how many leading columns to auto-suppress (default 0).
   * 
   * @return
   */
  public int getAutoSuppress() {
    return autoSuppressColumns;
  }

  /**
   * The values in leading columns can be automatically suppressed if they are the same as in the previous row.
   * 
   * @param nColumns
   *          How many columns to auto-suppress.
   */
  public void setAutoSuppress(int nColumns) {
    autoSuppressColumns = nColumns;
  }

  /**
   * 
   * @return Automatically add lines when auto-suppressing?
   */
  public boolean getAddLineWhenAutoSuppressing() {
    return outputLineOnOutermostSuppress;
  }

  /**
   * If previous rows had auto-suppressed values, and in the current row none are suppressed, should a line be prefixed.
   * 
   * @param addLine
   *          Automatically add lines when auto-suppressing.
   * 
   */
  public void setAddLineWhenAutoSuppressing(boolean addLine) {
    outputLineOnOutermostSuppress = addLine;
  }

  /**
   * Write the table to the specified file. The file is overwritten if it already exists. Any needed directories are
   * created. IO exceptions are caught.
   * 
   * @param file
   *          The file to write.
   */
  public void writeTo(File file) {
    file.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Append the table to the specified file, creating the file if it does not already exist. Any needed directories are
   * created. If the file exists, the given prefix is first appended to the file (e.g., the prefix might be one or more
   * new lines to separate the table from existing content.) IO exceptions are caught.
   * 
   * @param file
   *          The file to which the table is appended.
   * @param prefix
   *          Literal text to add to the file before the table.
   */
  public void appendTo(File file, String prefix) {
    file.getParentFile().mkdirs();
    boolean fileExists = file.exists();
    try (FileWriter writer = new FileWriter(file, true)) {
      if (prefix != null && fileExists) {
        writer.write(prefix);
      }
      writer.write(toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write the table in comma-separated-value format to the given file. If the file already exists, it is overwritten.
   * Any needed directories are created.
   * 
   * @param file
   *          The file into which the table is written.
   */
  public void writeCSVTo(File file) {
    file.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(file)) {
      if (!headers.isEmpty()) {
        for (int i = 0; i < headers.size(); i++) {
          if (i > 0) {
            writer.write(",");
          }
          writer.write("\"" + headers.get(i) + "\"");
        }
        writer.write("\n");
      }
      for (TableRow row : rows) {
        if (row instanceof TableRow.Entries) {
          for (List<String> line : getMatrix(row.getEntries())) {
            for (int i = 0; i < line.size(); i++) {
              String s = line.get(i);
              if (i == 0) {
                writer.write(s);
              } else {
                writer.write("," + s);
              }
            }
          }
          writer.write("\n");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  ///////////////////////////////////////////////////////////////////////
  // Implementation details.
  ///////////////////////////////////////////////////////////////////////

  /**
   * A list of rows, each of which is a list of strings.
   */
  static final class CellMatrix implements Iterable<List<String>> {
    List<List<String>> matrix = new ArrayList<>();

    void setElement(int i, int j, String element) {
      while (i >= matrix.size()) {
        matrix.add(new ArrayList<>());
      }
      List<String> row = matrix.get(i);
      while (j >= row.size()) {
        row.add("");
      }
      row.set(j, element);
    }

    @Override
    public Iterator<List<String>> iterator() {
      return matrix.iterator();
    }

    public boolean isEmpty() {
      return matrix.isEmpty();
    }

  }

  /**
   * Returns the specified element of the list, or the given default value if the given index is out of range.
   * 
   * @param list
   *          A list.
   * @param index
   *          The index of the element to get.
   * @param defaultValue
   *          The value to return if the index is out of range.
   * @return
   */
  static <A> A get(List<A> list, int index, A defaultValue) {
    if (index >= 0 && index < list.size()) {
      return list.get(index);
    } else {
      return defaultValue;
    }
  }

  /**
   * Sets the specified element of the list. If the given index is beyond the end of the list, null values are appended
   * to make the list long enough.
   * 
   * @param list
   *          A list.
   * @param index
   *          The index of the element to be set.
   * @param value
   *          The value to be set.
   */
  static <A> void set(List<A> list, int index, A value) {
    while (list.size() < index + 1) {
      list.add(null);
    }
    list.set(index, value);
  }

  /**
   * Set the alignment for the given column.
   * 
   * @param column
   *          The column index.
   * @param alignment
   *          The alignment.
   */
  void setAlignment(int column, Alignment alignment) {
    set(alignments, column, alignment);
  }

  /**
   * Set the header for the given column.
   * 
   * @param column
   *          The column index.
   * @param header
   *          The header.
   */
  void setHeader(int column, Object header) {
    set(headers, column, header == null ? "" : header.toString());
  }

  /**
   * Convert the given values, which may contains nulls, into strings.
   * 
   * @param objs
   *          An array of values, which may contains nulls.
   * @return An array of string representations of the values.
   */
  static List<String> toStrings(Object[] objs) {
    List<String> strings = new ArrayList<>();
    for (Object obj : objs) {
      if (obj == null) {
        strings.add("null");
      } else {
        strings.add(obj.toString());
      }
    }
    return strings;
  }

  /**
   * Convert the given object, which may be null, into a string.
   * 
   * @param obj
   *          The object.
   * @return The empty string if the object is null; otherwise the value returns by obj.toString().
   */
  static String toString(Object obj) {
    if (obj == null) {
      return "";
    } else {
      return obj.toString();
    }
  }

  /**
   * Convert a row of strings into a cell matrix.
   * 
   * @param row
   *          The row of strings to be converted.
   * @return The matrix of strings resulting from expanding new lines into new rows.
   */
  static CellMatrix getMatrix(List<Object> row) {
    CellMatrix matrix = new CellMatrix();
    for (int i = 0; i < row.size(); i++) {
      String[] parts = toString(row.get(i)).split("\n", -1);
      for (int j = 0; j < parts.length; j++) {
        matrix.setElement(j, i, parts[j]);
      }
    }
    return matrix;
  }

  /**
   * Update the column widths.
   * 
   * @param widths
   *          Current column widths.
   * @param newWidths
   *          New column widths.
   */
  void updateWidths(List<Integer> widths, List<Integer> newWidths) {
    for (int i = 0; i < newWidths.size(); i++) {
      while (i >= widths.size()) {
        widths.add(0);
      }
      widths.set(i, Math.max(widths.get(i), newWidths.get(i)));
    }
  }

  /**
   * Compute the widths of the columns.
   * 
   * @return The width of each column.
   */
  List<Integer> getWidths(List<TableBlock> blocks) {
    List<Integer> widths = new ArrayList<>();
    CellMatrix matrix = getMatrix(headers);
    for (List<String> aMatrix : matrix) {
      updateWidths(widths, aMatrix.stream().map(String::length).collect(Collectors.toList()));
    }
    for (TableBlock block : blocks) {
      updateWidths(widths, block.getWidths());
    }
    for (TableRow row : rows) {
      if (!row.isEntries()) {
        continue;
      }
      matrix = getMatrix(row.getEntries());
      for (List<String> aMatrix : matrix) {
        updateWidths(widths, aMatrix.stream().map(String::length).collect(Collectors.toList()));
      }
    }
    return widths;
  }

  /**
   * Get the alignment of the given column, using the default if none is explicitly set.
   * 
   * @param column
   *          The column index.
   * @return The column's alignment.
   */
  Alignment getAlignment(int column) {
    if (column < alignments.size() && alignments.get(column) != null) {
      return alignments.get(column);
    }
    return Alignment.Default;
  }

  /**
   * Splits the rows into blocks separated by spans.
   * 
   * @return The list of blocks.
   */
  List<TableBlock> getBlocks() {
    List<List<TableRow>> rowBlocks = new ArrayList<>();
    List<TableRow> rowBlock = null;
    for (TableRow row : rows) {
      if (rowBlock == null || row.isSpan()) {
        rowBlock = new ArrayList<>();
        rowBlocks.add(rowBlock);
      }
      rowBlock.add(row);
    }
    return rowBlocks.stream().map(b -> new TableBlock(b)).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    List<TableBlock> blocks = getBlocks();
    List<Integer> widths = getWidths(blocks);
    StringBuilder s = new StringBuilder();
    outputTopCaption(s, widths);
    int nBlocks = blocks.size();
    for (int b = 0; b < nBlocks; b++) {
      outputBlock(s, widths, blocks.get(b), b == 0, b == nBlocks - 1);
    }
    return s.toString();
  }

  /**
   * Adds the top caption to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   */
  private void outputTopCaption(StringBuilder s, List<Integer> widths) {
    if (topCaption != null) {
      s.append(lines.indentation());
      int totalWidth = widths.stream().mapToInt(i -> i + 3).sum();
      totalWidth -= 2;
      String[] rows = topCaption.split("\n");
      for (String row : rows) {
        s.append(Alignment.Centre.format(totalWidth, row)).append("\n");
      }
    }
  }

  /**
   * Adds a block to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   * @param block
   *          The block to add.
   * @param isFirstBlock
   *          Is the block the first?
   * @param isLastBlock
   *          Is the block the last?
   */
  void outputBlock(StringBuilder s, List<Integer> widths, TableBlock block, boolean isFirstBlock, boolean isLastBlock) {
    previousEntries.clear();
    boolean startsWithSpan = block.getRow(0) instanceof TableRow.Span;
    if (startsWithSpan) {
      ouputSpan(s, widths, (TableRow.Span) block.getRow(0));
    }
    outputHeader(s, widths);
    int nRows = block.numberofRows();
    int firstR = startsWithSpan ? 1 : 0;
    for (int r = firstR; r < nRows; r++) {
      boolean isFirstRow = r == firstR;
      boolean isLastRow = r == nRows - 1;
      outputRow(s, widths, block.getRow(r), isFirstRow, isLastRow);
    }
    s.append(lines.makeBottomLine(widths));
  }

  /**
   * Add a span row to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   * @param row
   *          The row to add.
   */
  private void ouputSpan(StringBuilder s, List<Integer> widths, TableRow.Span row) {
    for (Object entry : row.getEntries()) {
      s.append(toString(entry));
    }
    s.append("\n");
  }

  /**
   * Add the header to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   */
  void outputHeader(StringBuilder s, List<Integer> widths) {
    if (headers.isEmpty()) {
      s.append(lines.makeTopLine(widths));
      return;
    }
    s.append(lines.makeTopLine(widths));
    CellMatrix hm = getMatrix(headers);
    if (!hm.isEmpty()) {
      for (List<String> h : hm) {
        outputEntries(s, widths, h, true, false, false);
      }
      s.append(lines.makeLine(widths));
    }
  }

  /**
   * Add a row to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   * @param row
   *          The row to add.
   * @param isFirstRow
   *          Is the row the first in its block?
   * @param isLastRow
   *          Is the row the last in its block?
   */
  private void outputRow(StringBuilder s, List<Integer> widths, TableRow row, boolean isFirstRow, boolean isLastRow) {
    if (row.isSkip()) {
      for (int skip = 0; skip < ((TableRow.Skip) row).getNumberOfRows(); skip++) {
        s.append(lines.makeEmptyLine(widths));
      }
    } else if (row.isLine()) {
      if (!isFirstRow && !isLastRow) {
        s.append(lines.makeVisibleLine(widths));
      }
    } else if (row.isSpan()) {
      for (Object entry : row.getEntries()) {
        s.append(toString(entry));
      }
      s.append("\n");
    } else {
      for (List<String> line : getMatrix(row.getEntries())) {
        outputEntries(s, widths, line, false, isFirstRow, isLastRow);
      }
    }
  }

  /**
   * Add a row of entries to the output.
   * 
   * @param s
   *          Output onto which the caption is added.
   * @param widths
   *          The columns widths.
   * @param row
   *          The row to add.
   * @param isHeader
   *          Is the row the header?
   * @param isFirstRow
   *          Is the row the first in its block?
   * @param isLastRow
   *          Is the row the last in its block?
   */
  void outputEntries(StringBuilder s, List<Integer> widths, List<String> row, boolean isHeader, boolean isFirstRow,
          boolean isLastRow) {
    int suppress = 0;
    if (!isHeader) {
      for (int c = 0; c < row.size() && c < autoSuppressColumns; c++) {
        if (get(previousEntries, c, "").equals(row.get(c))) {
          suppress += 1;
        } else {
          break;
        }
      }
    }
    if (outputLineOnOutermostSuppress && suppress == 0 && autoSuppressColumns > 0 && !isHeader && !isFirstRow) {
      s.append(lines.makeVisibleLine(widths));
    }
    s.append(lines.indentation());
    for (int i = 0; i < widths.size(); i++) {
      if (i == 0) {
        s.append(lines.getVerticalLine());
      } else {
        s.append(" ").append(lines.getVerticalLine()).append(" ");
      }
      String value;
      if (i < suppress) {
        value = "";
      } else if (i < row.size()) {
        value = row.get(i);
        set(previousEntries, i, value);
      } else {
        value = "";
        set(previousEntries, i, value);
      }
      Alignment alignment;
      if (isHeader) {
        alignment = Alignment.Centre;
      } else {
        alignment = getAlignment(i);
      }
      s.append(alignment.format(widths.get(i), value));
    }
    s.append(lines.getVerticalLine()).append("\n");
  }

}
