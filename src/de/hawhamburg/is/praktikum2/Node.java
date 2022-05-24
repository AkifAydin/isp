package de.hawhamburg.is.praktikum2;

import java.util.ArrayList;
import java.util.List;

public class Node {

  private Node parent; // Elterknoten
  private List<Node> children; // Liste der Kindknoten
  private String attribute; // Name des Split-Attributs
  private int attrIndex; // Index des Split-Attributs
  private double attrValue; // Gini/Entropie-Wert des Split-Attributs
  private List<Object> data; // Daten der zur Node zugehörigen Datensatzelemente
  private int size; // Anzahl der Datensatzelemente in der Node

  public Node(List<Object> data) {
    this.parent = null;
    this.attribute = null;
    this.attrIndex = -1;
    this.attrValue = -1.0;
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

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
