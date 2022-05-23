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

    // solange printLevel aufrufen, bis es keine weiteren Nodes mehr gibt (wenn printLevel ==> false)
    while (printLevel(node, level)) {
      level++;
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
      if (node.getAttribute() == null) {
        System.out.println("Blattknoten");
      } else {
        System.out.println("Split-Attribut: " + node.getAttribute());
        System.out.println("Attributswert (Gini/Entropie): " + node.getAttrValue());
      }

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

  // Function to print level order traversal of a given binary tree
  public void levelOrderTraversal(Node node) {
    // start from level 1 — till the height of the tree
    int level = 1;

    // run till printLevel() returns false
    while (printLevel(node, level)) {
      level++;
    }
  }


  public void createDT(List<Object> data, int targetID, int ignoreID, int amountElems) {
    this.rootNode = new Node(data);

    createDTRecursive(rootNode, targetID, ignoreID, amountElems);
  }

  private void createDTRecursive(Node parent, int targetID, int ignoreID, int amountElems) { // target ID = index of target attribute

    List<String> data0 = (List<String>) parent.getData().get(0);
    List<List<String>> data1 = (List<List<String>>) parent.getData().get(1);
    List<List<List<Integer>>> data2 = (List<List<List<Integer>>>) parent.getData().get(2);

    System.out.println("Size: " + data1.get(0).size());
    System.out.println(data1);
    System.out.println(data2);

    if (data1.get(0).size() > Main.MAX_LEAF_ELEMS) {

      // Map um nachverfolgen zu können, zu welchem Attribut welches Ergebnis gehört
      Map<Double, Integer> attrResults = new HashMap<>();

      // Scores für alle Attribute berechnen und und die Map eintragen
      for (int i = 0; i < data2.size(); i++) {
        if (i == targetID || i == ignoreID) {
          continue;
        }
        //attrResults.put(Ergebnis von CalculateE, Index in Data vom Attribut)
        attrResults.put(calculateE(data2.get(i), data2.get(targetID), amountElems), i);
      }
      // besten Score ermitteln
      Double min = Collections.min(attrResults.keySet());
      int minAttributeIndex = attrResults.get(min);

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
          data1Copy.add(data1CopyPart);
          // aus jeder Attributswertliste entsprechende Subklassenliste erstellen
          data2Copy.add(Main.calculateSubclasses(data1CopyPart));
        }
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
//    Double max = Collections.max(attrResults.keySet());
//    System.out.println(max);
//    System.out.println(attrResults.get(max));
//    attrResults.remove(min);
//    Double min2 = Collections.min(attrResults.keySet());
//    System.out.println(min2);

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
    // list of counters for each subclass in target attribute
    int[] counterArr = new int[targetSubclasses.size()];

    for (int i : subclass) {
      for (int j = 0; j < counterArr.length; j++) {
        if (targetSubclasses.get(j).contains(i)) {
          counterArr[j]++;
        }
      }
    }

    //System.out.println("size subclass: " + nTotal);

    double result;
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
    } else if (Main.USE_GINI) {
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

  public static double log2(double x) {
    return (Math.log(x) / Math.log(2));
  }

}
