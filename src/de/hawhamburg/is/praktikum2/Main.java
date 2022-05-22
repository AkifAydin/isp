package de.hawhamburg.is.praktikum2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

  private static final List<Object> data = new ArrayList<>();
  protected static final boolean USE_ENTROPY = false;
  protected static final boolean USE_GINI = true;
  protected static int amountElems = 0;


  public static void main(String[] args) throws IOException {
    loadCSV("src/de/hawhamburg/is/praktikum2/customers.csv", ",");
    calculateAllSubclasses();

    System.out.println(data);
    DecisionTree dt = new DecisionTree();
    dt.createDT(data, 4, amountElems); // target 4 -> spendingScore
  }

  /**
   * Creates and adds all subclass lists to data.
   */
  private static void calculateAllSubclasses() {
    List<List<String>> attrValues = (List<List<String>>) data.get(1);
    List<List<List<Integer>>> listOfAllSubclassLists = new ArrayList<>();
    for (List<String> list : attrValues) {
      listOfAllSubclassLists.add(calculateSubclasses(list));
    }
    data.add(listOfAllSubclassLists);
  }

  /**
   * Creates subclasses of an attribute.
   *
   * @param list attribute list
   */
  private static List<List<Integer>> calculateSubclasses(List<String> list) {
    List<List<Integer>> result = new ArrayList<>();
    ArrayList<Integer> class1 = new ArrayList<>();
    ArrayList<Integer> class2 = new ArrayList<>();
    ArrayList<Integer> class3 = new ArrayList<>();
    Map<String, List<Integer>> map = new HashMap<>();
    int split1 = 0;
    int split2 = 0;
    boolean isInteger = true;

    try {
      // create split values so that we get 3 subclasses of similar value range
      List<Integer> intList;
      intList = list.stream().map(Integer::parseInt).collect(Collectors.toList()); // convert string list to integer list
      int max = Collections.max(intList);
      int min = Collections.min(intList);
      split1 = min + (max - min) / 3;
      split2 = min + ((max - min) / 3) * 2;

    } catch (NumberFormatException e) {
      isInteger = false;
    }
    for (int i = 0; i < list.size(); i++) {
      if (isInteger) { // create subclasses for Integer attribute
        int elem = Integer.parseInt(list.get(i));
        if (elem <= split1) {
          class1.add(i);
        } else if (elem <= split2) {
          class2.add(i);
        } else {
          class3.add(i);
        }
      } else {
        // create subclasses for String attribute
        String key = list.get(i); //Element der Input-Liste
        //if: Wenn Element schon gefunden, dann Indexstelle in Values-Liste einfügen
        //else: wenn Element noch nicht gefunden, dann dafür ein Key-Value-Paar eintragen
        if (map.containsKey(key)) {
          List<Integer> values = map.get(key);
          values.add(i);
          //Element Index stelle in Liste einfügen
          map.put(key, values);
        } else {
          ArrayList<Integer> t = new ArrayList<>();
          t.add(i);
          map.put(key, t);
        }
      }

    }

    if (isInteger) {
      result.add(class1);
      result.add(class2);
      result.add(class3);
    } else {
      result.addAll(map.values());
    }
    return result;
  }

  /**
   * Reads the contents of a csv file and puts them into lists.
   *
   * @param csvfile   file name
   * @param separator separator
   * @return whether method call was successful or not
   */
  public static boolean loadCSV(String csvfile, String separator) throws FileNotFoundException, IOException {
    boolean ret = false;

    File f = new File(csvfile);

    // prüfen, ob Datei existiert
    if (f.exists() && f.isFile()) {
      BufferedReader br = null;
      FileReader fr = null;

      try {
        fr = new FileReader(f);
        br = new BufferedReader(fr);

        String l;

        // [[Attributnamen (String)],[Listen mit Attributwerten],[Subklassenlisten]]

        //[Attributnamen (String)]
        String[] attributes = br.readLine().split(separator); //List with attribute names
        List<String> attrList = Arrays.stream(attributes).toList();
        data.add(attrList);

        //[Listen mit Attributwerten]
        List<List<String>> attrValues = new ArrayList<>();

        // Listen für die Attributwerte
        for (int i = 0; i < attributes.length; i++) {
          List<String> values = new ArrayList<>();
          attrValues.add(values);
        }
        // solange Zeilen in der Datei vorhanden
        while ((l = br.readLine()) != null) {
          amountElems++; // Gesamtanzahl Elemente berechnen
          // Zeile anhand des Separators (z.B. ",") aufsplitten
          String[] col = l.split(separator);
          // Daten in die entsprechenden Listen eintragen
          for (int i = 0; i < col.length; i++) {
            attrValues.get(i).add(col[i]);
          }
        }

        data.add(attrValues);

        ret = true;
      } finally {
        if (br != null) {
          br.close();
        }

        if (fr != null) {
          fr.close();
        }
      }
    }

    return ret;
  }


}
