package de.hawhamburg.is.praktikum2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecisionTree {

  private Node rootNode;

  public DecisionTree createDT() {
    this.rootNode = new Node();

    return new DecisionTree();
  }

  /**
   * Calculate E(attribute)
   * @param subclassesList subclass list of given attribute
   * @param targetSubclasses subclass list of target attribute
   * @param amountElems total amount of elements in the data set
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
   * @param subclass singular subclass of given attribute
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

    System.out.println("size subclass: " + nTotal);

    double result;
    if (Main.USE_ENTROPY) {
      result = 0.0;
      for (int i = 0; i < counterArr.length; i++) {
        if (!(counterArr[i] == 0)) {
          result -= (counterArr[i]/nTotal) * log2(counterArr[i]/nTotal);
        } else {
          result -= 0; // if none of the current elements are in this target subclass, don't change result
        }
        System.out.println("amount in target subclass " + i + ": " + counterArr[i]);
        System.out.println("Result: " + result);
      }
    } else if (Main.USE_GINI) {
      result = 1.0;
      for (int i = 0; i < counterArr.length; i++) {
        result -= Math.pow(counterArr[i]/nTotal, 2);
        System.out.println("amount in target subclass " + i + ": " + counterArr[i]);
        System.out.println("Result: " + result);
      }
    }

    System.out.println("I value: " + result);
    System.out.println();

    return result;
  }

  public static double log2(double x) {
    return (Math.log(x) / Math.log(2));
  }

}
