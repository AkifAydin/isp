package de.hawhamburg.is.praktikum2;

import java.util.*;

public class DecisionTree {

  private Node rootNode;

  /**
   * Initialisiert Baumtraversierung mit rootNode
   */
  public void traverseTree() {
    levelOrderTraversal(rootNode);
  }

  /**
   * traversiert den Baum Ebene für Ebene
   *
   * @param node momentane Node
   */
  public void levelOrderTraversal(Node node) {
    // Starten bei Ebene 1, Hochzählen bis zur Tiefe des Baumes
    int level = 1;

    System.out.println("Wurzel:");

    // solange printLevel aufrufen, bis es keine weiteren Nodes mehr gibt (wenn printLevel ==> false)
    while (printLevel(node, level)) {
      level++;
      System.out.println("\n ========== Ebene " + level + ": ==========");
    }
  }

  /**
   * Gibt für jede Ebene im Baum Informationen zu den entsprechenden Nodes aus
   *
   * @param node  Node, für welche Informationen ausgegeben werden
   * @param level Die Ebene, in der wir uns befinden
   * @return true, solange es mindestens eine Node in der momentanen Ebene des Baumes gibt
   */
  public boolean printLevel(Node node, int level) {

    if (level == 1) {
      if (node.getAttribute() == null) { // Blatt gefunden
        System.out.println("Blattknoten");
      } else { // kein Blatt
        System.out.println("Split-Attribut: " + node.getAttribute());
        System.out.println("Attribut-Score: " + node.getAttrValue());
      }
      System.out.println("Elemente: " + node.getSize());
      System.out.println();

      // return true, solange es mindestens eine Node in der momentanen Ebene des Baumes gibt
      return true;
    }

    // true, solange es mindestens eine Node in der momentanen Ebene des Baumes gibt
    // false, wenn es keine Node in der momentanen Ebene gibt
    boolean continueFlag = false;

    // Alle Nodes der momentanen Ebene durchgehen
    for (Node child : node.getChildren()) {
      continueFlag = printLevel(child, level - 1) || continueFlag;
    }

    return continueFlag;
  }

  /**
   * Initialisierung des zu erstellenden Entscheidungsbaumes mithilfe der rootNode
   *
   * @param data        Liste, die die Daten von allen eingelesenen Datensatzelementen enthält
   * @param targetID    ID vom Zielattribut (wird durchgereicht)
   * @param ignoreID    ID vom zu ignorierenden Attribut (wird durchgereicht)
   * @param amountElems Anzahl der Elemente vom gesamten Datensatz
   */
  public void createDT(List<Object> data, int targetID, int ignoreID, int amountElems) {
    this.rootNode = new Node(data);
    createDTRecursive(rootNode, targetID, ignoreID, amountElems);
  }

  /**
   * Methode zur rekursiven Erstellung des Entscheidungsbaumes
   *
   * @param parent      Aktuelle Node, die ggf. gesplittet werden soll
   * @param targetID    ID vom Zielattribut, auf Basis dessen der Baum aufgebaut werden soll
   * @param ignoreID    ID vom Attribut, welches nicht als Split-Attribut verwendet werden soll (z.B. CustomerID)
   * @param amountElems Anzahl der Elemente vom Datensatz, die der momentanen Node zugeordnet werden können
   */
  private void createDTRecursive(Node parent, int targetID, int ignoreID, int amountElems) {
    // alle Informationen zu den Datensatzelementen in der parent Node
    List<String> data0 = (List<String>) parent.getData().get(0);
    List<List<String>> data1 = (List<List<String>>) parent.getData().get(1);
    List<List<List<Integer>>> data2 = (List<List<List<Integer>>>) parent.getData().get(2);

    parent.setSize(data1.get(0).size());
    System.out.println("Size: " + parent.getSize());
    System.out.println(data1);
    System.out.println(data2);

    // Nur Splitten, solange das Limit der Anzahl an Datensatzelementen in der Node nicht unterschritten wurde
    if (data1.get(0).size() > Main.MAX_LEAF_ELEMS) {

      // Map um nachverfolgen zu können, zu welchem Attribut welches Ergebnis gehört
      Map<Double, Integer> attrResults = new HashMap<>();

      // Scores für alle Attribute berechnen und und die Map eintragen
      for (int i = 0; i < data2.size(); i++) {
        // targetID und ignoreID überspringen
        if (i == targetID || i == ignoreID) {
          continue;
        }
        // attrResults.put(Ergebnis von CalculateE, Index in Data0 vom Attribut)
        attrResults.put(calculateE(data2.get(i), data2.get(targetID), amountElems), i);
      }
      // besten Score ermitteln
      Double min = Collections.min(attrResults.keySet());
      int minAttributeIndex = attrResults.get(min);

      // Informationen zum Attribut in die Node eintragen
      parent.setAttribute(data0.get(minAttributeIndex));
      parent.setAttrIndex(minAttributeIndex);
      parent.setAttrValue(min);

      // data0 kopieren
      List<String> data0Copy = new ArrayList<>();
      data0Copy.addAll(data0);

      // Schleife zum Erstellen von den Nodes für jeweils eine Subklasse des Split-Attributs
      for (int i = 0; i < data2.get(minAttributeIndex).size(); i++) {
        // Teilliste von data, die nur noch einen Teil der Datensatzelemente enthält
        List<Object> dataCopy = new ArrayList<>();
        List<List<String>> data1Copy = new ArrayList<>();
        List<List<List<Integer>>> data2Copy = new ArrayList<>();
        // Schleife zum Erstellen von den letzten beiden Teillisten in dataCopy (Attributswertlisten, Subklassenlisten)
        for (int j = 0; j < data1.size(); j++) {
          List<String> data1CopyPart = new ArrayList<>();
          // für jedes Element der momentanen Subklasse vom Split-Attribut
          for (int index : data2.get(minAttributeIndex).get(i)) {
            data1CopyPart.add(data1.get(j).get(index));
          }
          // Attributswertlisten zusammenfügen
          data1Copy.add(data1CopyPart);
          // aus jeder Attributswertliste entsprechende Subklassenliste erstellen
          data2Copy.add(Main.calculateSubclasses(data1CopyPart));
        }
        // dataCopy für Child Node befüllen mit den angesammelten neuen Informationen
        dataCopy.add(data0Copy);
        dataCopy.add(data1Copy);
        dataCopy.add(data2Copy);

        // Child Node erstellen, mit letzten fehlenden Informationen befüllen und in Kindsknotenliste der momentanen Node einfügen
        Node child = new Node(dataCopy);
        child.setParent(parent);
        createDTRecursive(child, targetID, ignoreID, data1Copy.get(0).size());
        parent.addChild(child);
      }
      //System.out.println("Children: " + parent.getChildren());
    }
  }

  /**
   * E-Score berechnen (Score für gesamtes Attribut)
   * @param subclassesList   Subklassenliste des Attributs
   * @param targetSubclasses Subklassenliste des Zielattributs
   * @param amountElems      Anzahl der Datensatzelemente in der momentanen Node
   * @return E-Score
   */
  protected double calculateE(List<List<Integer>> subclassesList, List<List<Integer>> targetSubclasses, int amountElems) {
    double result = 0.0;
    for (List<Integer> integers : subclassesList) {
      double subclassSize = integers.size();
      double iValue = calculateI(integers, targetSubclasses);
      // berechnete I-Scores der Subklassen gewichtet aufsummieren
      result += (subclassSize / amountElems) * iValue;
    }

    //System.out.println("Result: " + result);
    return result;
  }

  /**
   * I-Score berechnen (Score für Subklasse des Attributs)
   * @param subclass         eine Subklasse des Attributs
   * @param targetSubclasses Subklassenliste des Zielattributs
   * @return I-Score
   */
  private double calculateI(List<Integer> subclass, List<List<Integer>> targetSubclasses) {
    double nTotal = subclass.size();
    // Liste von Zählern für alle Subklassen des Zielattributs
    int[] counterArr = new int[targetSubclasses.size()];

    // Herausfinden zu welcher Klassifizierung/Zielsubklasse die Elemente der Subklasse gehören + Hochzählen
    for (int i : subclass) {
      for (int j = 0; j < counterArr.length; j++) {
        if (targetSubclasses.get(j).contains(i)) {
          counterArr[j]++;
        }
      }
    }

    //System.out.println("size subclass: " + nTotal);

    double result;

    // Berechnung von Entropie
    if (Main.USE_ENTROPY) {
      result = 0.0;
      for (int i = 0; i < counterArr.length; i++) {
        if (!(counterArr[i] == 0)) {
          result -= (counterArr[i] / nTotal) * log2(counterArr[i] / nTotal);
        } else {
          result -= 0; // if none of the current elements are in this target subclass, don't change result
        }
//        System.out.println("amount in target subclass " + i + ": " + counterArr[i]);
//        System.out.println("Result: " + result);
      }
    } else if (Main.USE_GINI) { // Berechnung von Gini
      result = 1.0;
      for (int i = 0; i < counterArr.length; i++) {
        result -= Math.pow(counterArr[i] / nTotal, 2);
//        System.out.println("amount in target subclass " + i + ": " + counterArr[i]);
//        System.out.println("Result: " + result);
      }
    }

//    System.out.println("I value: " + result);
//    System.out.println();

    return result;
  }

  /**
   * Logarithmus zur Basis 2
   * @param x ein Double Wert
   * @return log2(x)
   */
  public static double log2(double x) {
    return (Math.log(x) / Math.log(2));
  }

}
