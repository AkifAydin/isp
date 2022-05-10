package de.hawhamburg.is.praktikum1;

import java.util.ArrayList;
import java.util.Collections;

public class RBFS2 {

  public static Node rbfsStart(Node start, Node target) {
    Object[] pair = rbfsAlg(start, target, Double.MAX_VALUE);
    return (Node) pair[0];
  }

  private static Object[] rbfsAlg(Node n, Node target, double fLimit) {
    Object[] result = new Object[2];
    if (n == target) {
      result[0] = n;
      return result;
    }

    ArrayList<Node> children = new ArrayList<>();

    for (Node.Edge m: n.neighbors) {
      Node child = m.node;
      children.add(child);
    }

    if (children.isEmpty()) {
      result[0] = null;
      result[1] = Double.MAX_VALUE;
      return result;
    }

    for (Node child: children) {
      child.f = Math.max(child.g + child.h, n.f);
    }

    Collections.sort(children);

    while (true) {
      Node best = children.get(0);
      if (best.f > fLimit) {
        result[0] = null;
        result[1] = best.f;
        return result;
      }

      double alternative = children.get(1).f;
      result = rbfsAlg(n,best,Math.min(fLimit, alternative));
      if (result[0] != null) {
        return result;
      }

    }

  }

}
