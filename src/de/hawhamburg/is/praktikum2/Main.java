package de.hawhamburg.is.praktikum2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

public class Main {


    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    /**
     * Sets the class index as the last attribute.
     *
     * @param fileName
     * @return Instances data
     * @throws IOException
     */
    public static Instances loadData(String fileName) throws IOException {
        BufferedReader datafile = readDataFile(fileName);

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    public static void main(String[] args) throws Exception {
        Instances trainingCancer = loadData("src/de/hawhamburg/is/praktikum2/Data/cancer_train.txt");
        Instances testingCancer = loadData("src/de/hawhamburg/is/praktikum2/Data/cancer_test.txt");
        Instances validationCancer = loadData("src/de/hawhamburg/is/praktikum2/Data/cancer_validation.txt");

        // Comparing between Entropy and Gini
        DecisionTree decisionTree = new DecisionTree();
        decisionTree.setSelectionMethod(DecisionTree.SelectionMethod.ENTROPY);
        decisionTree.buildClassifier(trainingCancer);
        double validErrorEntropy = decisionTree.calcAvgError(validationCancer);
        System.out.println("Validation error using Entropy: " + validErrorEntropy);

        decisionTree.setSelectionMethod(DecisionTree.SelectionMethod.GINI);
        decisionTree.buildClassifier(trainingCancer);
        double validErrorGini = decisionTree.calcAvgError(validationCancer);
        System.out.println("Validation error using Gini: " + validErrorGini);

        if (validErrorGini < validErrorEntropy) {
            decisionTree.setSelectionMethod(DecisionTree.SelectionMethod.GINI);
        } else {
            decisionTree.setSelectionMethod(DecisionTree.SelectionMethod.ENTROPY);
        }

        System.out.println("----------------------------------------------------");
        double pValueAlpha = 1.0;
        double minPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 1.0);
        double tempPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 0.75);
        if (minPValue > tempPValue) {
            minPValue = tempPValue;
            pValueAlpha = 0.75;
        }
        tempPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 0.5);
        if (minPValue > tempPValue) {
            minPValue = tempPValue;
            pValueAlpha = 0.5;
        }
        tempPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 0.25);
        if (minPValue > tempPValue) {
            minPValue = tempPValue;
            pValueAlpha = 0.25;
        }
        tempPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 0.05);
        if (minPValue > tempPValue) {
            minPValue = tempPValue;
            pValueAlpha = 0.05;
        }
        tempPValue = pValueTest(decisionTree, trainingCancer, validationCancer, 0.005);
        if (minPValue > tempPValue) {
            minPValue = tempPValue;
            pValueAlpha = 0.005;
        }

        System.out.println("Best validation error at p_value = " + pValueAlpha);
        decisionTree.setpValue(pValueAlpha);
        decisionTree.buildClassifier(trainingCancer);
        System.out.println("Test error with best tree: " + decisionTree.calcAvgError(testingCancer));

        decisionTree.printTree();

    }

    private static double pValueTest(DecisionTree decisionTree, Instances training, Instances validation, double pValue) throws Exception {
        System.out.println("Decision Tree with p_value of: " + pValue);
        decisionTree.setpValue(pValue);
        decisionTree.buildClassifier(training);
        System.out.println("The training error of the decision tree is " + decisionTree.calcAvgError(training));
        decisionTree.calcTreeStats(validation);
        System.out.println("Max height on validation data: " + decisionTree.getTreeMaxHeight());
        System.out.println("Average height on validation data: " + decisionTree.getTreeAvgHeight());

        double validError = decisionTree.calcAvgError(validation);

        System.out.println("The validation error of the decision tree is " + validError);
        System.out.println("----------------------------------------------------");

        return validError;
    }


}
