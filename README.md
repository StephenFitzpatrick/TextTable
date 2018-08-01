# TextTable
Java class to make it easy to output plain-text tables (must be viewed in a fixed-width font). Useful for viewing collections of complex objects while developing code and debugging.

# Example Code

    TextTable table = new TextTable("Populations");
    table.setHeaders("Continent", "Country", "City", Alignment.Right, "Population");
    table.setAutoSuppress(2);
    for (Population pop : populations) {
      table.row(pop.continent, pop.country, pop.city, pop.population);
    }
    String output = table.toString();

# Example Output

                         Populations                     
    ┌──────────────┬─────────┬───────────────┬───────────┐
    │  Continent   │ Country │     City      │ Population│
    ├──────────────┼─────────┼───────────────┼───────────┤
    │Europe        │ UK      │ London        │    8615246│
    │              │         │ Birmingham    │    1224136│
    │              │         │ Glasgow       │     801198│
    │              │         │ Leeds         │     761481│
    │              │ France  │ Paris         │    2249975│
    │              │         │ Marseille     │     850636│
    │              │         │ Lyon          │     491268│
    │              │         │ Toulouse      │     447340│
    │              │ Spain   │ Madrid        │    2824000│
    │              │         │ Barcelona     │    1454000│
    │              │         │ Valencia      │     736000│
    │              │         │ Sevilla       │     695000│
    ├──────────────┼─────────┼───────────────┼───────────┤
    │North America │ Canada  │ Toronto       │    2731579│
    │              │         │ Montreal      │    1704694│
    │              │         │ Calgary       │    1239220│
    │              │         │ Ottawa        │     934243│
    │              │ USA     │ New York City │    8175133│
    │              │         │ Los Angeles   │    3792621│
    │              │         │ Chicago       │    2695598│
    │              │         │ Houston       │    2099451│
    └──────────────┴─────────┴───────────────┴───────────┘

# Usage
The main classes are TextTable and TextTable.Alignment. The other classes are helpers classes and can be ignored.

Basic usage is:
* Create a table, optionally with a caption: `TextTable table = new TextTable("Caption")`.
* Optionally add column headers: `table.setHeaders("Column A", "Column B")`. A header can be just about any object - it is converted into a string using toString().
* Optionally add column alignments: `table.setAlignments(Alignment.Left, Alignment.Centre, Alignment.Right)`. Left is the default.
* Add as many rows as you like: `table.row(XXX, YYY, ZZZ)`. Each row can have as many entries as you like. Each entry corresponds to a column - the width of the column is determined by all of the entries in that column.
* Convert the table to a string: `table.toString()`.

# Notes

Headers and row entries are objects. They are converted to strings when the table is converted to a string. Their string values are not retained, so converting a table to a string multiple times may produce different outputs if the header/entry objects change. You can avoid this by using Strings instead of Objects when setting the headers and adding rows.

For convenience, Alignments can be interspersed with headers in a call to `setHeaders`. For example: `setHeaders("Item", Alignment.Right, "Quantity", "#Available")` will create headers for 3 columns, with the "Item" column using the default alignment (left), and the other two using right-alignment. An alignment persists until it is replaced by another alignment.

Headers and alignments can also be appended using `appendHeader` and `appendAlignments`.

Appending a row entry, using `append(Object...)`, adds the new entries to the last row created with `row(Object...)`, or a new row if necessary.

A row can also be formed from the elements of a Collection, using `rowFrom(Collection<Object>)`. This may be convenient if the entries need to be gathered and reorganized or sorted prior to addition to the table.

If a header or row entry contains new lines (when it is converted to a string), then the cell in which it appears will have multiple lines, as will sibling cells on the same row. One consequence of this is that a table can appear as a row entry. 

A horizontal line can be drawn across the table using `addLine()`.

A blank line can be added using `addBlank()`.

A single line of text, not split into columns, can be added using `addSpan(Object...)`. The multiple arguments are concatenated (after they are converted into strings) to give a single string which is written across the table ignoring the columns. This can be used to break the table vertically into blocks. The headers are repeated at the start of each block.

Entries in leading columns that are repeated on multiple rows can be suppressed using `setAutoSuppress(int)` where the parameter is the number of columns to suppress. For example, without auto-suppression, the example at the top would output the following:

                         Populations                     
    ┌──────────────┬─────────┬───────────────┬───────────┐
    │  Continent   │ Country │     City      │ Population│
    ├──────────────┼─────────┼───────────────┼───────────┤
    │Europe        │ UK      │ London        │    8615246│
    │Europe        │ UK      │ Birmingham    │    1224136│
    │Europe        │ UK      │ Glasgow       │     801198│
    │Europe        │ UK      │ Leeds         │     761481│
    │Europe        │ France  │ Paris         │    2249975│
    │Europe        │ France  │ Marseille     │     850636│
    │Europe        │ France  │ Lyon          │     491268│
    │Europe        │ France  │ Toulouse      │     447340│
    │Europe        │ Spain   │ Madrid        │    2824000│
    │Europe        │ Spain   │ Barcelona     │    1454000│
    │Europe        │ Spain   │ Valencia      │     736000│
    │Europe        │ Spain   │ Sevilla       │     695000│
    │North America │ Canada  │ Toronto       │    2731579│
    │North America │ Canada  │ Montreal      │    1704694│
    │North America │ Canada  │ Calgary       │    1239220│
    │North America │ Canada  │ Ottawa        │     934243│
    │North America │ USA     │ New York City │    8175133│
    │North America │ USA     │ Los Angeles   │    3792621│
    │North America │ USA     │ Chicago       │    2695598│
    │North America │ USA     │ Houston       │    2099451│
    └──────────────┴─────────┴───────────────┴───────────┘

# Examples

See the test cases in src/test for some simple examples.

The outputs that they produce are in src/test/edu/kestrel/fitzpatrick/test/texttable/tables.

