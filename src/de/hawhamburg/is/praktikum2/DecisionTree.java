package de.hawhamburg.is.praktikum2;


import weka.classifiers.Classifier;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;

import java.util.HashMap;

class TreeStats {
    int maxHeight;
    double avgHeight;

    public TreeStats(int maxHeight, double avgHeight) {
        this.maxHeight = maxHeight;
        this.avgHeight = avgHeight;
    }
}

class Node {
    Node[] children;
    Node parent;
    int attributeIndex; // splitting criterion
    double returnValue; // majority
    int recurrence;
    int noRecurrence;
    String attType;
}

public class DecisionTree implements Classifier {
    private final int NO_RECURRENCE = 0;
    private final int RECURRENCE = 1;
    private final int ALPHA_075 = 0;
    private final int ALPHA_05= 1;
    private final int ALPHA_025 = 2;
    private final int ALPHA_005 = 3;
    private final int ALPHA_0005 = 4;

    private Node rootNode;
    private SelectionMethod selectionMethod = SelectionMethod.GINI;
    private HashMap<Attribute, Integer> attributeToIndex;
    private HashMap<Integer, String> indexToAttributeName;
    double[][] chiSquareTable;
    private TreeStats treeStats;

    private double pValue = 1;

    public void printTree() {
        Node root = getRootNode();
        System.out.println("Root");
        printTree(root, 0);
    }

    private void addTabs(int tabs) {
        for(int i=0;i<tabs;i++) {
            System.out.print("  ");
        }
    }

    private void printTree(Node node, int recursionDepth) {
        addTabs(recursionDepth);
        if(node.children != null) {
            if(node.returnValue == RECURRENCE) {
                System.out.println("Returning value: recurrence-events");

            }
            else {
                System.out.println("Returning value: no-recurrence-events");
            }

            for(int i=0;i<node.children.length;i++) {
                addTabs(recursionDepth);
                System.out.println("if attribute " + indexToAttributeName.get(node.attributeIndex) + " = " + node.children[i].attType);
                printTree(node.children[i], recursionDepth  + 1);
            }
        }
        addTabs(recursionDepth);
        if(node.returnValue == RECURRENCE) {
            System.out.println("Leaf. Returning value: recurrence-events");

        }
        else {
            System.out.println("Leaf. Returning value: no-recurrence-events");
        }
    }

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public void setSelectionMethod(SelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        chiSquareTable = initChiSquareTable();
        attributeToIndex = createAttributeMapping(data);
        indexToAttributeName = createIndexMapping(data);
        this.rootNode = buildTree(data);
        this.treeStats = calcTreeStats(data);
    }


    private Node buildTree(Instances data) throws Exception {
        Node node = new Node();
        node.recurrence = getRecurrenceClass(data).size();
        node.noRecurrence = getNoRecurrenceClass(data).size();
        if(isTheSameClass(data)){
            node.returnValue = majorityClass(data);
            return node;
        }
        if(data.numAttributes() == 0) {
            node.returnValue = majorityClass(data);
            return node;
        }
        Attribute splittingAttribute = findSplittingCriterion(data);
        if(splittingAttribute == null) {
            // 0 gain
            node.children = null;
            return node;
        }
        node.attributeIndex = attributeToIndex.get(splittingAttribute);
        node.returnValue = majorityClass(data);
        Instances[] splitGroups = splitByCriterion(data, splittingAttribute);
        int numOfNonEmptyGroups = getNonEmptyGroups(splitGroups);
        int numOfAttributesToDF = numOfNonEmptyGroups - 2;
        if(pValue == 1 || calcChiSquare(data, splittingAttribute) > chiSquareTable[numOfAttributesToDF][getIndexByAlpha(pValue)]) {
            node.children = new Node[splitGroups.length];
            for(int i=0;i<splitGroups.length;i++) {
                if(splitGroups[i].size() != 0) {
                    node.children[i] = buildTree(splitGroups[i]);
                    node.children[i].parent = node;
                    node.children[i].attType = splittingAttribute.value(i);
                }
                else {
                    Node childnode = new Node();
                    node.children[i] = childnode;
                    childnode.parent = node;
                    childnode.returnValue = node.returnValue;
                    childnode.attType = splittingAttribute.value(i);
                }
            }
        }
        else {
            node.children = null; // the node is a leaf.
        }

        return node;

    }

    private int getNonEmptyGroups(Instances[] splitGroups) {
        int counter = 0;
        for(int i=0;i<splitGroups.length;i++) {
            if(splitGroups[i].size() > 0) {
                counter++;
            }
        }

        return counter;
    }

    /**
     *
     * Calculates the chi square statistic of splitting
     * the data according to the splitting attribute as learned in class.
     *
     */

    private double calcChiSquare(Instances data, Attribute splitingAttribute)  throws Exception {
        int recurrence = getRecurrenceClass(data).size();
        int noRecurrence = getNoRecurrenceClass(data).size();
        double numOfInstances = (double) data.size();
        double recProb = recurrence / numOfInstances;
        double noRecProb = noRecurrence / numOfInstances;
        Instances[] splitDataByAttribute = splitByCriterion(data, splitingAttribute);
        double chiSquare = 0;
        for (int i = 0; i < splitingAttribute.numValues(); i++) {
            int	numOfDataFeature = splitDataByAttribute[i].size();
            double	noRecExpect = numOfDataFeature * noRecProb;
            double	recExpect = numOfDataFeature * recProb;
            int numOfRec = getRecurrenceClass(splitDataByAttribute[i]).size();
            int numOfNoRec = getNoRecurrenceClass(splitDataByAttribute[i]).size();
            chiSquare += chiSquareTestFormula(numOfRec, recExpect) + chiSquareTestFormula(numOfNoRec, noRecExpect);
        }
        return chiSquare;
    }

    private double chiSquareTestFormula(int observed , double expected){
        if( expected == 0 ){
            return 0;
        }
        return Math.pow(observed - expected, 2) / expected;
    }

    /**
     * Calculate the average error on a given instances set (could be the training, test or validation set).
     * The average error is the total number of classification mistakes on the input instances set divided by the number of instances in the input set.
     * @param data
     * @return Average error (double).
     */

    public double calcAvgError(Instances data) {
        double numOfMistakes = 0.0;
        for (int i = 0; i < data.size(); i++) {
            if (classifyInstance(data.get(i)) == 1) {
                if (data.get(i).classValue() + 1 != 1) {
                    numOfMistakes++;
                }
            } else {
                if (data.get(i).classValue() + 1 != 2) {
                    numOfMistakes++;
                }
            }
        }
        return numOfMistakes/data.size();
    }

    private HashMap<Attribute, Integer> createAttributeMapping(Instances data) {
        HashMap<Attribute, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            hashMap.put(data.attribute(i), i);
        }

        return hashMap;
    }

    private HashMap<Integer, String> createIndexMapping(Instances data) {
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            hashMap.put(i, data.attribute(i).name());
        }

        return hashMap;
    }

    private int majorityClass (Instances data) throws Exception
    {
        double [] p = getProbabilties(data);
        return (p[0] > p[1]) ? NO_RECURRENCE : RECURRENCE;
    }

    private Instances[] splitByCriterion (Instances data, Attribute criterion) throws Exception {
        Instances [] instances = new Instances[criterion.numValues()];
        for(int i=0;i<instances.length;i++) {
            instances[i] = filterByAttributeValue(data, criterion, new int[] { i + 1 });
            instances[i] = removeAttribute(instances[i], criterion);
        }

        return instances;
    }
    private boolean isTheSameClass(Instances data) throws Exception {
        double[] p = getProbabilties(data);
        boolean isHomogeneous = false;
        if (p[0] == 1 || p[1] == 1) {
            isHomogeneous = true;
        }
        return isHomogeneous;
    }

    private Instances removeAttribute(Instances data, Attribute attribute) throws Exception {
        Remove remove = new Remove();

        remove.setAttributeIndices("" + (attribute.index() + 1));
        remove.setInvertSelection(false);
        remove.setInputFormat(data);
        Instances newData = Filter.useFilter(data, remove);
        return newData;

    }

    private Attribute findSplittingCriterion(Instances data) throws Exception {
        int maxIndex = 0;
        double maxGain = 0;
        for (int i=0;i<data.numAttributes() - 1;i++) {
            double gain = calcGain(data, i);
            if(maxGain < gain) {
                maxIndex = i;
                maxGain = gain;
            }
        }

        if(maxGain == 0) {
            return null;
        }

        return data.attribute(maxIndex);
    }

    public Node getRootNode() {
        return this.rootNode;
    }

    public double getTreeAvgHeight()  {
        return this.treeStats.avgHeight;
    }

    public int getTreeMaxHeight() {
        return this.treeStats.maxHeight;
    }

    public TreeStats calcTreeStats(Instances data) {
        double sum = 0;
        int maxHeight = 0;
        int numOfInstances = data.size();

        for (int i = 0; i < numOfInstances; i++) {
            int height = heightOfInstanceClassification(data.get(i));
            if(height > maxHeight) {
                maxHeight = height;
            }
            sum += height;
        }

        return new TreeStats(maxHeight, sum / (double) numOfInstances);
    }

    private int heightOfInstanceClassification(Instance instance) {
        Node traverseNode = getRootNode();
        int height = 0;
        while (traverseNode.children != null) {
            traverseNode = findNextNode(instance, traverseNode);
            height++;
        }

        return height;
    }

    private Node findNextNode(Instance instance, Node currectNode) {
        Attribute attribute = instance.attribute(currectNode.attributeIndex);
        String instanceValueOfAttribute = instance.stringValue(currectNode.attributeIndex);
        int indexOfNodeByAttribute = attribute.indexOfValue(instanceValueOfAttribute);

        return currectNode.children[indexOfNodeByAttribute];
    }

    public double classifyInstance(Instance instance) {
        Node traverseNode = getRootNode();
        while (traverseNode.children != null) {
            traverseNode = findNextNode(instance, traverseNode);
        }
        return traverseNode.returnValue;
    }


    private double calcGain(Instances data, int attributeIndex) throws Exception {
        return calcMeasure(data) - calcMeasureAttribute(data, data.attribute(attributeIndex));
    }

    /**
     * Calculate Gini Index
     * @param p - A set of probabilities
     * @return The Gini index of p
     */
    private double calcGini(double[] p) {
        double sum = 0.0;
        for (int i = 0; i < p.length; i++) {
            sum += p[i] * p[i];
        }
        return 1 - sum;
    }

    /**
     * Calculate Entropy
     * @param p - A set of probabilities
     * @return The Entropy of p
     */
    private double calcEntropy(double[] p) {
        double sum = 0.0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] != 0) {
                sum += p[i] * Math.log(p[i]);
            }
        }

        return -sum;
    }

    private double calcMeasure(Instances data) throws Exception {
        if (selectionMethod == SelectionMethod.ENTROPY) {
            return calcEntropy(getProbabilties(data));
        } else {
            return calcGini(getProbabilties(data));
        }
    }

    private double calcMeasureAttribute(Instances data, Attribute attribute) throws Exception {
        double sum = 0.0;

        int attributeDiscreteValues = attribute.numValues();

        for(int i=0;i<attributeDiscreteValues;i++) {
            Instances filteredData = filterByAttributeValue(data, attribute, new int[] {(i + 1)});
            if(filteredData.size() != 0) {
                double weight = (filteredData.size() / (double) data.size());
                if(selectionMethod == SelectionMethod.ENTROPY) {
                    sum += weight * calcEntropy(getProbabilties(filteredData));
                }
                else
                {
                    sum += weight * calcGini(getProbabilties(filteredData));
                }
            }
        }

        return sum;
    }

    private Instances getNoRecurrenceClass(Instances data) throws Exception {
        int noRecurrenceClassIndex = 2;
        return filterByAttributeValue(data, data.attribute(data.classIndex()), new int[] {( noRecurrenceClassIndex )});
    }

    private Instances getRecurrenceClass(Instances data) throws Exception {
        int recurrenceClassIndex = 1;
        return filterByAttributeValue(data, data.attribute(data.classIndex()), new int[] {( recurrenceClassIndex )});
    }

    private double[] getProbabilties(Instances data) throws Exception{
        double[] probabilities = new double[2];
        Instances test = getNoRecurrenceClass(data);
        probabilities[NO_RECURRENCE] = getNoRecurrenceClass(data).size() / (double) data.size();
        probabilities[RECURRENCE] = getRecurrenceClass(data).size() / (double) data.size();
        return probabilities;
    }

    private String flatArrayValues(int [] array) {
        String string = "";
        for(int i=0;i<array.length - 1;i++) {
            string += array[i] + ",";
        }

        string += array[array.length - 1];

        return string;
    }

    private Instances filterByAttributeValue(Instances dataToFilter, Attribute attribute, int[] valueIndecies) throws Exception {
        RemoveWithValues filter = new RemoveWithValues();
        String[] options = new String[5];
        options[0] = "-C";   // attribute index
        options[1] = "" + (attribute.index() + 1);
        options[2] = "-L" ;
        options[3] = flatArrayValues(valueIndecies);
        options[4] = "-V";
        filter.setOptions(options);

        filter.setInputFormat(dataToFilter);
        Instances newData = Filter.useFilter(dataToFilter, filter);
        return newData;
    }

    private double[][] initChiSquareTable() {
        double[][] chiSquareTable = new double[12][5];
        chiSquareTable[0] =  new double []{ 0.102, 0.455, 1.323, 3.841, 7.879 };
        chiSquareTable[1] = new double []{ 0.575, 1.386, 2.773, 5.991, 10.597 };
        chiSquareTable[2] = new double []{ 1.213, 2.366, 4.108, 7.815, 12.838 };
        chiSquareTable[3] = new double []{ 1.923, 3.357, 5.385, 9.488, 14.860 };
        chiSquareTable[4] = new double []{ 2.675, 4.351, 6.626, 11.070, 16.750 };
        chiSquareTable[5] = new double []{ 3.455, 5.348, 7.841, 12.592, 18.548 };
        chiSquareTable[6] = new double []{ 4.255, 6.346, 9.037, 14.067, 20.278 };
        chiSquareTable[7] = new double []{ 5.071, 7.344, 10.219, 15.507, 21.955 };
        chiSquareTable[8] = new double []{ 5.899, 8.343, 11.389, 16.919, 23.589 };
        chiSquareTable[9] = new double []{ 6.737, 9.342, 12.549, 18.307, 25.188 };
        chiSquareTable[10] = new double []{ 7.584, 10.341, 13.701, 19.675, 26.757 };
        chiSquareTable[11] = new double []{ 8.438, 11.340, 14.845, 21.026, 28.300 };

        return chiSquareTable;

    }

    private int getIndexByAlpha(double alpha) {
        if (alpha == 0.75) {
            return ALPHA_075;
        } else if (alpha == 0.5) {
            return ALPHA_05;
        } else if (alpha == 0.25) {
            return ALPHA_025;
        } else if (alpha == 0.05) {
            return ALPHA_005;
        } else if (alpha == 0.005) {
            return ALPHA_0005;
        }

        return -1;
    }

    private double chiSquareTableValue(int degreeOfFreedom, double alpha) {
        if(alpha == -1) {
            System.out.println("Alpha value invalid");
            return -1;
        }

        return chiSquareTable[degreeOfFreedom - 1][getIndexByAlpha(alpha)];
    }

    @Override
    public double[] distributionForInstance(Instance arg0) throws Exception {
        // Don't change
        return null;
    }

    @Override
    public Capabilities getCapabilities() {
        // Don't change
        return null;
    }

    public enum SelectionMethod {
        GINI,
        ENTROPY
    }




}