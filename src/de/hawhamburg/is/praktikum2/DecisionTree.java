package de.hawhamburg.is.praktikum2;

import java.util.*;

public class DecisionTree {

  private Node rootNode;

  public void traverseTree() {
    levelOrderTraversal(rootNode);
  }

  // Function to print all nodes of a given level from left to right
  public boolean printLevel(Node node, int level) {

    if (level == 1) {
      if (node.getAttribute() == null) {
        System.out.println("Blattknoten");
      } else {
        System.out.println("Split-Attribut: " + node.getAttribute());
        System.out.println("Attributswert (Gini/Entropie): " + node.getAttrValue());
      }

      // return true if at least one node is present at a given level
      return true;
    }

    boolean continueFlag = false;

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

      //Map um nachverfolgen zu können zu welchem Attribut welches Ergebnis gehört
      Map<Double, Integer> attrResults = new HashMap<>();

      for (int i = 0; i < data2.size(); i++) {
        if (i == targetID || i == ignoreID) {
          continue;
        }
        //attrResults.put(Ergebnis von CalculateE, Index in Data vom Attribut)
        attrResults.put(calculateE(data2.get(i), data2.get(targetID), amountElems), i);
      }
      //kleinstes Ergebnis wird ermittelt
      Double min = Collections.min(attrResults.keySet());
      int minAttributeIndex = attrResults.get(min);

      parent.setAttribute(data0.get(minAttributeIndex));
      parent.setAttrIndex(minAttributeIndex);
      parent.setAttrValue(min);

      // copy data0
      List<String> data0Copy = new ArrayList<>();
      data0Copy.addAll(data0);

      // Schleife zum Erstellen von den 3 Nodes für die drei Subklassen
      for (int i = 0; i < data2.get(minAttributeIndex).size(); i++) {
        List<Object> dataCopy = new ArrayList<>();
        List<List<String>> data1Copy = new ArrayList<>();
        List<List<List<Integer>>> data2Copy = new ArrayList<>();
        // Schleife für alle Attribute in data1 (5Attribute: Name,Id...)
        for (int j = 0; j < data1.size(); j++) {
          List<String> data1CopyPart = new ArrayList<>();
          // durch jedes Element der Subklasse von minAttribute (67 elemente... Parts)
          for (int index : data2.get(minAttributeIndex).get(i)) {
            data1CopyPart.add(data1.get(j).get(index));
          }
          data1Copy.add(data1CopyPart);
          data2Copy.add(Main.calculateSubclasses(data1CopyPart));
        }
        dataCopy.add(data0Copy);
        dataCopy.add(data1Copy);
        dataCopy.add(data2Copy);

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
   * Calculate E(attribute)
   *
   * @param subclassesList   subclass list of given attribute
   * @param targetSubclasses subclass list of target attribute
   * @param amountElems      total amount of elements in the data set
   * @return gini/entropy score for the entire attribute
   */
  protected double calculateE(List<List<Integer>> subclassesList, List<List<Integer>> targetSubclasses, int amountElems) {
    // total amount of elements
    double result = 0.0;
    for (List<Integer> integers : subclassesList) {
      double subclassSize = integers.size();
      double iValue = calculateI(integers, targetSubclasses);
      result += (subclassSize / amountElems) * iValue;
    }

    //System.out.println("Result: " + result);
    return result;
  }

  /**
   * * Calculate I(Cn)
   *
   * @param subclass         singular subclass of given attribute
   * @param targetSubclasses subclass list of target attribute
   * @return gini/entropy score for the subclass
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
