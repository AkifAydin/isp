package de.hawhamburg.is.praktikum2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecisionTree {

  private Node rootNode;

  public DecisionTree createDT(List<Object> data, int targetID, int amountElems) { // target ID = index of target attribute
    this.rootNode = new Node(data);

    List<String> data0 = (List<String>) rootNode.getData().get(0);
    List<List<String>> data1 = (List<List<String>>) rootNode.getData().get(1);
    List<List<List<Integer>>> data2 = (List<List<List<Integer>>>) rootNode.getData().get(2);
    System.out.println(data2);

    //Map um nachverfolgen zu können zu welchem Attribut welches Ergebnis gehört
    Map<Double, Integer> attrResults = new HashMap<>();

    for (int i = 0; i < data2.size(); i++) {
      if (i == targetID) {
        continue;
      }
      //attrResults.put(Ergebnis von CalculateE, Index in Data vom Attribut)
      attrResults.put(calculateE(data2.get(i), data2.get(targetID), amountElems), i);
    }
    Double min = Collections.min(attrResults.keySet());
    int minAttributeIndex = attrResults.get(min);
    // Schleife zum Erstellen von den 3 Nodes für die drei Subklassen
    for (int i = 0; i < data2.get(minAttributeIndex).size(); i++) {
      List<Object> dataCopy = new ArrayList<>();  //TODO
      List<String> data0Copy = new ArrayList<>();  //TODO
      List<String> data2Copy = new ArrayList<>();  //TODO
//      Collections.copy(data0Copy, data0);
//      data0Copy.remove(targetID); //TODO data0Copy erstellen
      List<List<String>> data1Copy = new ArrayList<>();
      // durch jedes Element der Subklasse von minAttribute
      for (int j = 0; j < data1.size(); j++) {
        List<String> dataCopy1Part = new ArrayList<>();
        for (int index : data2.get(minAttributeIndex).get(i)) {
          dataCopy1Part.add(data1.get(j).get(index));
          //todo node mit 67 only
        }
        //todo subklassen listen erstellen von main einfach aufrufen (calculateSubclasses) -> in data2Copy einfügen
        //calculateSubclasses(datacopyPart1)
        //System.out.println(dataCopy1Part);
        data1Copy.add(dataCopy1Part);

      }
      // TODO dataCopy erstellen
      // TODO -> daraus neue Node erstellen und in Baum einfügen
    }

    Double max = Collections.max(attrResults.keySet());
    System.out.println(max);
    System.out.println(attrResults.get(max));
    attrResults.remove(min);
    Double min2 = Collections.min(attrResults.keySet());
    System.out.println(min2);
    return new DecisionTree();
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

    System.out.println("Result: " + result);
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
