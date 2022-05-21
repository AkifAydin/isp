package de.hawhamburg.is.praktikum2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

  // TODO dynamische Listenerstellung
  private static final List<String> gender = new ArrayList<>();
  private static final List<List<Integer>> genderClasses = new ArrayList<>();
  private static final List<Integer> age = new ArrayList<>();
  private static final List<List<Integer>> ageClasses = new ArrayList<>();
  private static final List<Integer> annualIncome = new ArrayList<>();
  private static final List<List<Integer>> annualIncomeClasses = new ArrayList<>();
  private static final List<Integer> spendingScore = new ArrayList<>();
  private static final List<List<Integer>> spendingScoreClasses = new ArrayList<>();

  // 18-70
  private static final int AGE_SPLIT1 = 30;
  private static final int AGE_SPLIT2 = 50;
  // 15-137
  private static final int INCOME_SPLIT1 = 55;
  private static final int INCOME_SPLIT2 = 95;
  // 1-100
  private static final int SPENDING_SPLIT1 = 33;
  private static final int SPENDING_SPLIT2 = 67;


  public static void main(String[] args) throws IOException {
    loadCSV("src/de/hawhamburg/is/praktikum2/customers.csv", ",");
    genderSubclasses();
    calculateSubclasses(age, ageClasses, AGE_SPLIT1, AGE_SPLIT2);
    calculateSubclasses(annualIncome, annualIncomeClasses, INCOME_SPLIT1, INCOME_SPLIT2);
    calculateSubclasses(spendingScore, spendingScoreClasses, SPENDING_SPLIT1, SPENDING_SPLIT2);

    System.out.println(gender);
    System.out.println(age);
    System.out.println(annualIncome);
    System.out.println(spendingScore);
    System.out.println();
    System.out.println(genderClasses);
    System.out.println(ageClasses);
    System.out.println(annualIncomeClasses);
    System.out.println(spendingScoreClasses);
    System.out.println();
    DecisionTree dt = new DecisionTree();
    dt.calculateE(ageClasses, spendingScoreClasses);
  }

  private static void genderSubclasses() {
    ArrayList<Integer> class1 = new ArrayList<>();
    ArrayList<Integer> class2 = new ArrayList<>();
    ArrayList<Integer> class3 = new ArrayList<>();

    for (int i = 0; i < gender.size(); i++) {
      String elem = gender.get(i);
      if (elem.equals("Male")) { // Male
        class1.add(i);
      } else { // Female
        class2.add(i);
      }
    }

    genderClasses.add(class1);
    genderClasses.add(class2);
    genderClasses.add(class3);
  }

  /**
   * Creates 3 subclasses of an attribute.
   * @param list attribute list
   * @param result resulting subclasses list
   * @param split1 first split element (inclusive)
   * @param split2 second split element (inclusive)
   */
  private static void calculateSubclasses(List<Integer> list, List<List<Integer>> result, int split1, int split2) {

    ArrayList<Integer> class1 = new ArrayList<>();
    ArrayList<Integer> class2 = new ArrayList<>();
    ArrayList<Integer> class3 = new ArrayList<>();

    for (int i = 0; i < list.size(); i++) {
      Integer elem = list.get(i);
      if (elem <= split1) {
        class1.add(i);
      } else if (elem <= split2) {
        class2.add(i);
      } else {
        class3.add(i);
      }
    }

    result.add(class1);
    result.add(class2);
    result.add(class3);
  }

  /**
   * Reads the contents of a csv file and puts them into lists.
   * @param csvfile file name
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

        br.readLine(); // TODO dynamische Listenerstellung

        // solange Zeilen in der Datei vorhanden
        while ((l = br.readLine()) != null) {
          // Zeilen anhand des Separators,
          // z.B. ";", aufsplitten
          String[] col = l.split(separator);

          // Daten in die entsprechenden Listen eintragen
          for (int i = 1; i < 5; i++) {
            switch (i) {
              case 1 -> gender.add(col[i]);
              case 2 -> age.add(Integer.parseInt(col[i]));
              case 3 -> annualIncome.add(Integer.parseInt(col[i]));
              case 4 -> spendingScore.add(Integer.parseInt(col[i]));
            }
          }
        }

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
