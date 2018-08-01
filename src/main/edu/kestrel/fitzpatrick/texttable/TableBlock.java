package edu.kestrel.fitzpatrick.texttable;

import java.util.ArrayList;
import java.util.List;

import edu.kestrel.fitzpatrick.texttable.TextTable.CellMatrix;

/**
 * Represents a list of rows. Caches the column widths.
 *
 */
final class TableBlock {
  // The rows.
  final List<TableRow> rows;
  // The cell matrix for each row.
  final List<CellMatrix> matrices = new ArrayList<>();
  final List<Integer> widths = new ArrayList<>();

  public TableBlock(List<TableRow> rows) {
    this.rows = new ArrayList<>(rows);
    for (TableRow row : rows) {
      if (row instanceof TableRow.Entries) {
        matrices.add(TextTable.getMatrix(((TableRow.Entries) row).getEntries()));
      } else {
        matrices.add(null);
      }
    }
    for (CellMatrix matrix : matrices) {
      if (matrix != null) {
        for (List<String> row : matrix) {
          for (int c = 0; c < row.size(); c++) {
            TextTable.set(widths, c, Math.max(TextTable.get(widths, c, 0), row.get(c).length()));
          }
        }
      }
    }
    for (int i = 0; i < widths.size(); i++) {
      if (widths.get(i) == null) {
        widths.set(i, 0);
      }
    }
  }

  /**
   * 
   * @return The number of rows.
   */
  public int numberofRows() {
    return rows.size();
  }

  /**
   * 
   * @param r
   *          The row index.
   * @return The row at the given index.
   */
  public TableRow getRow(int r) {
    return rows.get(r);
  }

  /**
   * 
   * @param r
   *          The row index.
   * @return The cell matrix for the row at the given index.
   */
  public CellMatrix getMatrix(int r) {
    return matrices.get(r);
  }

  /**
   * 
   * @return A list of column widths.
   */
  public List<Integer> getWidths() {
    return widths;
  }
}