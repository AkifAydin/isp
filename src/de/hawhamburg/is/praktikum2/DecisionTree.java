package de.hawhamburg.is.praktikum2;

import java.util.List;

public class DecisionTree {

  private Node rootNode;

  public DecisionTree createDT() {
    this.rootNode = new Node();


    return new DecisionTree();
  }

  protected double calculateE(List<List<Integer>> subclassesList, List<List<Integer>> targetSubclasses) {
    int nSubclass1 = subclassesList.get(0).size();
    int nSubclass2 = subclassesList.get(1).size();
    int nSubclass3 = subclassesList.get(2).size();
    double nTotal = nSubclass1 + nSubclass2 + nSubclass3;

    double result = 0.0;

    for (int i = 0; i < 3; i++) {
      List<Integer> subclass = subclassesList.get(i);
      double iValue = calculateI(subclass, targetSubclasses);
      switch (i) {
        case 0 -> result += (nSubclass1/nTotal)*iValue;
        case 1 -> result += (nSubclass2/nTotal)*iValue;
        case 2 -> result += (nSubclass3/nTotal)*iValue;
      }
    }

    System.out.println("Gini score: " + result);
    return result;
  }

  private double calculateI(List<Integer> subclass, List<List<Integer>> targetSubclasses) {
    double nTotal = subclass.size();
    int nSubclass1 = 0;
    int nSubclass2 = 0;
    int nSubclass3 = 0;

    for (int i : subclass) {
      if (targetSubclasses.get(0).contains(i)) {
        nSubclass1++;
      } else if (targetSubclasses.get(1).contains(i)) {
        nSubclass2++;
      } else {
        nSubclass3++;
      }
    }
    System.out.println("size subclass: " + nTotal);
    System.out.println("amount in target subclass 1: " + nSubclass1);
    System.out.println("amount in target subclass 2: " + nSubclass2);
    System.out.println("amount in target subclass 3: " + nSubclass3);
    double result = 1.0 - (Math.pow(nSubclass1/nTotal, 2) + Math.pow(nSubclass2/nTotal, 2) + Math.pow(nSubclass3/nTotal, 2));
    System.out.println("I value: " + result);
    System.out.println();

    return result;
  }

}
