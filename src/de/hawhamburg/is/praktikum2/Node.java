package de.hawhamburg.is.praktikum2;

import java.util.ArrayList;
import java.util.List;

public class Node {

  private Node parent;
  private Node[] children;
  private String attribute;
  private List<Object> data;

  public Node(List<Object> data) {
    this.parent = null;
    this.children = null;
    this.attribute = null;
    this.data = data;
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

  public List<Object> getData() {
    return data;
  }
}
