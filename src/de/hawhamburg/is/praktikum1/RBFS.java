package de.hawhamburg.is.praktikum1;

import java.util.PriorityQueue;

public class RBFS {

  public static Node rbfsStart(Node start, Node target) {
    return rbfsAlg(start, target);
  }

  private static Node rbfsAlg(Node n, Node target) {
    if (n == target) {
      return n;
    }

    PriorityQueue<Node> children = new PriorityQueue<>();
    for (Node.Edge m : n.neighbors) {
      Node child = m.node;
      child.g = n.g + m.weight;
      child.f = child.g + child.calculateHeuristic(target);
      child.parent = n;
      children.add(child);
    }

    if (children.isEmpty()) {
      n.f = Double.MAX_VALUE;
      return n;
    }

    while (!children.isEmpty()) {

      if (children.peek().f > n.fLimit) {
        n.f = children.peek().f;
        return n;
      }

      Node best = children.remove();
      double secondBestF;
      if (!children.isEmpty()) {
        secondBestF = children.peek().f;
      } else {
        secondBestF = Double.MAX_VALUE;
      }
      best.fLimit = Math.min(secondBestF, n.f);
      children.add(rbfsAlg(best, target));
    }

    return n;
  }
}
