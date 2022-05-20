package de.hawhamburg.is.praktikum2;

public class Node {

  private Node parent;
  private Node[] children;
  private String attribute;

  public Node() {
    this.parent = null;
    this.children = null;
    this.attribute = null;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public Node[] getChildren() {
    return children;
  }

  public void setChildren(Node[] children) {
    this.children = children;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }
}
