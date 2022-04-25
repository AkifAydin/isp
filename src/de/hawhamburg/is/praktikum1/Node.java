package de.hawhamburg.is.praktikum1;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
  // ID for readability of result purposes
  private static int idCounter = 0;
  public int id;

  // Parent in the path
  public Node parent = null;

  public List<Edge> neighbors;

  // Evaluation functions
  public double f = Double.MAX_VALUE;
  public double g = Double.MAX_VALUE;
  // Hardcoded heuristic
  public double h;

  Node(double h){
    this.h = h;
    this.id = idCounter++;
    this.neighbors = new ArrayList<>();
  }

  // necessary to sort Open List
  @Override
  public int compareTo(Node n) {
    return Double.compare(this.f, n.f);
  }

  public static class Edge {
    public int weight;
    public Node node;

    Edge(int weight, Node node){
      this.weight = weight;
      this.node = node;
    }
  }

  public void addBranch(int weight, Node node){
    Edge newEdge = new Edge(weight, node);
    neighbors.add(newEdge);
  }

  public double calculateHeuristic(Node target){
    return this.h;
  }
}