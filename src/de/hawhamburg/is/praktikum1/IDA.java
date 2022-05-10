package de.hawhamburg.is.praktikum1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IDA {

  public static Node idaStart(Node start, Node target) {

    List<Double> result = new ArrayList<>();
    return idaAlg(start, target, start.calculateHeuristic(target), result);

  }

  private static Node idaAlg(Node current, Node target, double bound, List<Double> result) {

    for (Node.Edge edge: current.neighbors) {
      Node child = edge.node;
      if (child == target) {
        return child;
      }
      child.g = current.g + edge.weight;
      child.f = child.g + child.calculateHeuristic(target);
      child.parent = current;
      if (child.f <= bound) {
        idaAlg(child, target, bound, result);
      } else {
        result.add(child.f);
      }

    }

    Collections.sort(result);


    return idaAlg(current, target, result.get(0), new ArrayList<>());

  }
}
