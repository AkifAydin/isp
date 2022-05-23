package de.hawhamburg.is.praktikum2;


import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;

public class Node {

  private Node parent;
  private List<Node> children;
  private String attribute;
  private int attrIndex;
  private double attrValue;
  private List<Object> data;
  private List<Range<Integer>> subclassRanges;

  public Node(List<Object> data) {
    this.parent = null;
    this.attribute = null;
    this.attrIndex = -1;
    this.attrValue = -1.0;
    this.subclassRanges = new ArrayList<>();
    this.children = new ArrayList<>();
    this.data = data;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public List<Node> getChildren() {
    return children;
  }

  public void addChild(Node child) {
    this.children.add(child);
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public List<Object> getData() {
    return data;
  }

  public double getAttrValue() {
    return attrValue;
  }

  public void setAttrValue(double attrValue) {
    this.attrValue = attrValue;
  }

  public int getAttrIndex() {
    return attrIndex;
  }

  public void setAttrIndex(int attrIndex) {
    this.attrIndex = attrIndex;
  }

  public List<Range<Integer>> getSubclassRanges() {
    return subclassRanges;
  }

  public void setSubclassRanges(Range<Integer> range1, Range<Integer> range2, Range<Integer> range3) {
    this.subclassRanges.add(range1);
    this.subclassRanges.add(range2);
    this.subclassRanges.add(range3);
  }
}
