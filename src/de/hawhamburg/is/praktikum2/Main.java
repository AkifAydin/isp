package de.hawhamburg.is.praktikum2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

  private static final List<Object> data = new ArrayList<>(); // Liste für die eingelesenen Daten
  protected static int amountElems; // Anzahl aller Datensatzelemente; wird in loadCSV berechnet

  protected static final boolean USE_ENTROPY = true;   // Flag für Entropie
  protected static final boolean USE_GINI = false;   // Flag für Gini
  // Limit der Anzahl an Datensatzelementen in den Blattknoten; Abbruchbedingung der rekursiven Baumerstellung
  protected static final int MAX_LEAF_ELEMS = 50;

  // FUNKTIONIERT NUR FÜR STRING UND INTEGER DATEN
  public static void main(String[] args) throws IOException {
    loadCSV("src/de/hawhamburg/is/praktikum2/customers.csv", ",");
    calculateAllSubclasses();

    DecisionTree dt = new DecisionTree();
    dt.createDT(data, 4, 0, amountElems); // target 4 -> spendingScore; ignore 0 -> customerID
    dt.traverseTree();
  }

  /**
   * Erstellt und fügt alle Subklassenlisten der data-Liste hinzu.
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
   * Erstellt Subklassen eines Attributes.
   * Integer-Werte werden in 3 Subklassen mit möglichst gleichem Wertebereich aufgeteilt (Gering, Mittel, Hoch).
   * Bei String-Werte wird für alle verschiedenen Strings jeweils eine Subklasse erstellt (z.B. Gender (Male/Female) -> 2 Subklassen).
   * @param list Attributswertliste
   */
  protected static List<List<Integer>> calculateSubclasses(List<String> list) {
    // Ergebnisliste
    List<List<Integer>> result = new ArrayList<>();
    // Die 3 zu erstellenden Subklassen bei Integer-Werten
    ArrayList<Integer> class1 = new ArrayList<>();
    ArrayList<Integer> class2 = new ArrayList<>();
    ArrayList<Integer> class3 = new ArrayList<>();
    // Map mit beliebiger Anzahl an Subklassen bei String-Werten
    Map<String, List<Integer>> map = new HashMap<>();
    // Split-Werte zum Erhalten von 3 Subklassen mit ähnlichem Wertebereich
    int split1 = 0;
    int split2 = 0;
    // Genutzt für Überprüfung auf Integer-Werte
    boolean isInteger = true;

    try {
      // Bei Integer-Werten diese Parsen und in intList eintragen
      List<Integer> intList;
      intList = list.stream().map(Integer::parseInt).collect(Collectors.toList());
      // Optimale Splitwerte ermitteln (für gleichmäßigen Split des Wertebereiches)
      int max = Collections.max(intList);
      int min = Collections.min(intList);
      split1 = min + (max - min) / 3; // 1/3 des Wertebereiches
      split2 = min + ((max - min) / 3) * 2; // 2/3 des Wertebereiches

    } catch (NumberFormatException e) { // bei String-Werten
      isInteger = false;
    }
    for (int i = 0; i < list.size(); i++) {
      if (isInteger) { // Erstellen von Subklassen für Integer-Werte
        int elem = Integer.parseInt(list.get(i));
        if (elem <= split1) {
          class1.add(i);
        } else if (elem <= split2) {
          class2.add(i);
        } else {
          class3.add(i);
        }
      } else { // Erstellen von Subklassen für String-Werte
        String key = list.get(i); // Element der Input-Liste
        //if: Wenn Element schon gefunden, dann Indexstelle in entsprechende Values-Liste einfügen
        //else: Wenn Element noch nicht gefunden, dann dafür ein Key-Value-Paar eintragen
        if (map.containsKey(key)) {
          List<Integer> values = map.get(key);
          values.add(i);
          map.put(key, values);
        } else {
          ArrayList<Integer> values = new ArrayList<>();
          values.add(i);
          map.put(key, values);
        }
      }

    }
    // Ergebnisliste befüllen
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
   * Einlesen eines Datensatzes in einer CSV Datei; Erstellen der data-Liste
   * @param csvfile   Dateiname
   * @param separator Separator-Symbol
   * @return Ob der Methodenaufruf erfolgreich war oder nicht
   */
  public static boolean loadCSV(String csvfile, String separator) throws IOException {
    boolean ret = false;

    amountElems = 0;

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
