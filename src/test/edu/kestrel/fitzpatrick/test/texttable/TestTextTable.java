package edu.kestrel.fitzpatrick.test.texttable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

import edu.kestrel.fitzpatrick.texttable.TextTable;
import edu.kestrel.fitzpatrick.texttable.TextTable.Alignment;

public class TestTextTable {

  private static final Path KNOWN_RESULTS_DIR = Paths.get("src/test/edu/kestrel/fitzpatrick/test/texttable/tables");

  private static enum People {
    David, Judith, Louie, John
  };

  private static final class Population {
    String continent;
    String country;
    String city;
    int population;

    Population(String continent, String country, String city, int population) {
      this.continent = continent;
      this.country = country;
      this.city = city;
      this.population = population;
    }
  }

  Population[] populations = { //
      new Population("Europe", "UK", "London", 8615246), //
      new Population("Europe", "UK", "Birmingham", 1224136), //
      new Population("Europe", "UK", "Glasgow", 801198), //
      new Population("Europe", "UK", "Leeds", 761481), //
      new Population("Europe", "France", "Paris", 2249975), //
      new Population("Europe", "France", "Marseille", 850636), //
      new Population("Europe", "France", "Lyon", 491268), //
      new Population("Europe", "France", "Toulouse", 447340), //
      new Population("Europe", "Spain", "Madrid", 2824000), //
      new Population("Europe", "Spain", "Barcelona", 1454000), //
      new Population("Europe", "Spain", "Valencia", 736000), //
      new Population("Europe", "Spain", "Sevilla", 695000), //
      new Population("North America", "Canada", "Toronto", 2731579), //
      new Population("North America", "Canada", "Montreal", 1704694), //
      new Population("North America", "Canada", "Calgary", 1239220), //
      new Population("North America", "Canada", "Ottawa", 934243), //
      new Population("North America", "USA", "New York City", 8175133), //
      new Population("North America", "USA", "Los Angeles", 3792621), //
      new Population("North America", "USA", "Chicago", 2695598), //
      new Population("North America", "USA", "Houston", 2099451), //
  };

  // Simple example.
  @Test
  public void test01() {
    TextTable table = new TextTable("Test 1");
    table.setHeaders("", "Monday", "Tuesday", "Wednesday");
    table.row("Breakfast", "cereal", "eggs", "fruit");
    table.row("Lunch", "sandwich", "salad", "sushi");
    table.row("Dinner", "steak", "fish", "pasta");
    compare("test01", table);
  }

  // Example with values other than strings, with lines, with alignment.
  @Test
  public void test02() {
    TextTable table = new TextTable("Test 2");
    table.setHeaders("Who", "When", "How Much");
    table.setAlignments(Alignment.Left, Alignment.Centre, Alignment.Right);
    table.row(People.David, "June 5", 13.55);
    table.addLine();
    table.row(People.Judith, "February 29", 123.45);
    table.addLine();
    table.row(People.Louie, "", 5.55);
    table.addLine();
    table.row(People.John, "ASAP", 99.99);
    compare("test02", table);
  }

  // Example with header, alignment and rows incrementally constructed, with newlines in header.
  @Test
  public void test03() {
    TextTable table = new TextTable("Test 3");
    int minPower = 1;
    int maxPower = 4;
    table.setHeaders("Base");
    table.setAlignments(Alignment.Right);
    for (int p = minPower; p <= maxPower; p++) {
      table.appendHeaders("Power\n" + p);
      table.appendAlignments(Alignment.Right);
    }
    int minBase = 1;
    int maxBase = 7;
    for (int base = minBase; base <= maxBase; base++) {
      table.row(base);
      for (int p = minPower; p <= maxPower; p++) {
        table.append(Math.pow(base, p));
      }
      table.addLine();
    }
    compare("test03", table);
  }

  // Example with tables within a table, with blank lines.
  @Test
  public void test04() {
    int min = 1;
    int max = 19;
    TextTable evens = new TextTable("Even");
    evens.setHeaders(Alignment.Right, "Base", "Square");
    TextTable odds = new TextTable("Odd");
    odds.setHeaders(Alignment.Right, "Base", "Square");
    for (int i = min; i <= max; i++) {
      TextTable whichTable;
      if (i % 2 == 0) {
        whichTable = evens;
      } else {
        whichTable = odds;
      }
      whichTable.row(i, i * i);
      whichTable.addBlank();
    }
    TextTable table = TextTable.horizontal(odds, evens);
    compare("test04", table);
  }

  // Example using populate to add rows.
  @Test
  public void test05() {
    Integer[] primes = { 2, 3, 5, 7, 11 };

    TextTable table1 = new TextTable("Array");
    table1.setHeaders(Alignment.Right, "Base", "Squared", "Cubed");
    table1.populate(primes, p -> new Object[] { p, p * p, p * p * p });

    TextTable table2 = new TextTable("Collection");
    table2.setHeaders(Alignment.Right, "Base", "Squared", "Cubed");
    table2.populate(Arrays.asList(primes), p -> new Object[] { p, p * p, p * p * p });

    TextTable table3 = new TextTable("Iterator");
    table3.setHeaders(Alignment.Right, "Base", "Squared", "Cubed");
    table3.populate(Arrays.asList(primes).iterator(), p -> new Object[] { p, p * p, p * p * p });

    TextTable table = TextTable.vertical(table1, table2, table3);
    table.setTopCaption("Test 5");
    compare("test05", table);
  }

  private TextTable makeTable6() {
    TextTable table = new TextTable();
    table.setIndent(2);
    String lastCountry = "";
    for (Population pop : populations) {
      if (lastCountry.equals(pop.country)) {
      } else {
        lastCountry = pop.country;
        table.addSpan(pop.country);
      }
      table.row(pop.city, pop.population);
    }
    return table;
  }

  // Example with/without caption and with/without header, with spans, with indentation.
  @Test
  public void test06() {
    TextTable tableA = makeTable6();
    tableA.setAlignments(Alignment.Left, Alignment.Right);

    TextTable tableB = makeTable6();
    tableB.setTopCaption("Populations");
    tableB.setAlignments(Alignment.Left, Alignment.Right);

    TextTable tableC = makeTable6();
    tableC.setHeaders("City", Alignment.Right, "Population");

    TextTable tableD = makeTable6();
    tableD.setTopCaption("Populations");
    tableD.setHeaders("City", Alignment.Right, "Population");

    compare("test06", //
            "no caption, no header", tableA, //
            "\n\ncaption, no header", tableB, //
            "\n\nno caption, header", tableC, //
            "\n\ncaption, header", tableD);
  }

  // Example with auto-suppressed lead columns.
  @Test
  public void test07() {
    TextTable table = new TextTable("Populations,\nwith no auto-suppress");
    table.setHeaders("Continent", "Country", "City", Alignment.Right, "Population");
    table.setAutoSuppress(0);
    NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    for (Population pop : populations) {
      table.row(pop.continent, pop.country, pop.city, format.format(pop.population));
    }
    String output = table.toString();

    table.setTopCaption("Populations,\nwith auto-suppress columns = 1,\nno auto-lines");
    table.setAutoSuppress(1);
    table.setAddLineWhenAutoSuppressing(false);
    output += "\n" + table.toString();

    table.setTopCaption("Populations,\nwith auto-suppress columns = 2,\nwith auto-lines (default)");
    table.setAddLineWhenAutoSuppressing(true);
    table.setAutoSuppress(2);
    output += "\n" + table.toString();

    compare("test07", output);
  }

  private void compare(String testName, String value) {
    Path path = KNOWN_RESULTS_DIR.resolve(testName + ".txt");
    try {
      // Uncomment to overwrite a file/create a new file.
      // Files.write(path, value.getBytes());
      String expected = new String(Files.readAllBytes(path));
      assertEquals(expected, value);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Exception " + e);
    }
  }

  private void compare(String testName, Object... tables) {
    StringBuilder s = new StringBuilder();
    for (Object table : tables) {
      s.append(table.toString());
      s.append("\n");
    }
    compare(testName, s.toString());
  }

}
