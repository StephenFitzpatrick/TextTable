package edu.kestrel.fitzpatrick.texttable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract public class TableRow {
  final List<Object> entries = new ArrayList<>();

  public boolean isSpan() {
    return this instanceof Span;
  }

  public boolean isEntries() {
    return this instanceof Entries;
  }

  public boolean isSkip() {
    return this instanceof Skip;
  }

  public boolean isLine() {
    return this instanceof Line;
  }

  public List<Object> getEntries() {
    return entries;
  }

  public static class Span extends TableRow {

    public Span(Object... entries) {
      this.entries.addAll(Arrays.asList(entries));
    }

    @Override
    public List<Object> getEntries() {
      return entries;
    }
  }

  public static class Skip extends TableRow {
    final int numberOfRows;

    public Skip(int numberOfRows) {
      this.numberOfRows = numberOfRows;
    }

    public Skip() {
      this(1);
    }

    public int getNumberOfRows() {
      return numberOfRows;
    }

  }

  public static class Line extends TableRow {

  }

  public static class Entries extends TableRow {

    public Entries() {
    }

    public Entries(Object... entries) {
      this.entries.addAll(Arrays.asList(entries));
    }

    public void add(Object obj) {
      entries.add(obj.toString());
    }

    public void append(Object entry) {
      entries.add(entry);
    }

    public boolean isEmpty() {
      return entries.isEmpty();
    }

    public int size() {
      return entries.size();
    }

    public Object get(int n) {
      return entries.get(n);
    }

    @Override
    public List<Object> getEntries() {
      return entries;
    }
  }

}