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

  public static void main(String[] args) throws IOException {
    loadCSV("src/de/hawhamburg/is/praktikum2/customers.csv", ",");
    genderSubclasses(gender, genderClasses);
    calculateSubclasses(age, ageClasses, 30, 50); // 18-70
    calculateSubclasses(annualIncome, annualIncomeClasses, 50, 90); // 15-137
    calculateSubclasses(spendingScore, spendingScoreClasses, 33, 67); // 1-100

    System.out.println(gender);
    System.out.println(age);
    System.out.println(annualIncome);
    System.out.println(spendingScore);
    System.out.println();
    System.out.println(genderClasses);
    System.out.println(ageClasses);
    System.out.println(annualIncome);
    System.out.println(spendingScoreClasses);
  }

  private static void genderSubclasses(List<String> list, List<List<Integer>> result) {
    ArrayList<Integer> class1 = new ArrayList<>();
    ArrayList<Integer> class2 = new ArrayList<>();

    for (int i = 0; i < list.size(); i++) {
      String elem = list.get(i);
      if (elem.equals("Male")) { // Male
        class1.add(i);
      } else { // Female
        class2.add(i);
      }
    }

    result.add(class1);
    result.add(class2);
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
