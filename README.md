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
