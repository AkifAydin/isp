package de.hawhamburg.is.praktikum1;

public class IDA2 {

  public static Node idaStart(Node n) {
    double bound = n.h;
    n.f = n.h;

    Object[] result = new Object[2];

    while (bound != -1) {
      result = idaAlg(n, bound);
      bound = (double) result[0];
    }
    return (Node) result[1];
  }

  private static Object[] idaAlg(Node n, double bound) {
    Object[] result = new Object[2];

    if (n.f > bound) {
      result[0] = n.f;
      return result;
    }
    if (n.h == 0) {
      result[0] = -1;
      result[1] = n;
      return result;
    }

    double lowest = Double.MAX_VALUE;

    for (Node.Edge edge : n.neighbors) {
      Node child = edge.node;
      child.g = n.g + edge.weight;
      child.f = child.g + child.h;
      child.parent = n;
      Object[] result2 = idaAlg(child, bound);
      lowest = Math.min((double) result2[0], lowest);
    }
    result[0] = lowest;
    return result;
  }

}
