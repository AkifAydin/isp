package de.hawhamburg.is.praktikum1;

import java.util.ArrayList;
import java.util.List;


public class IDA_Star {
    public static Double iDAStern(Node start,Node target){
        double threshold = start.calculateHeuristic(target);
        while (true){
            System.out.println("Iteration mit threshold von: "  + threshold);
            double distance = iDAStern(start,target, 0,threshold);
            if (distance==Double.MAX_VALUE){
                return null;
            } else if (distance < 0){
                System.out.println("Ziel Knoten gefunden");
                return -distance;
            } else{
                threshold = distance;
            }
        }
    }
    public static Double iDAStern(Node node,Node target,double distance,Double threshold){
        System.out.printf("Knoten " + node.id +" besuchen ------- ");
        if (node == target){
            return -distance;
        }
        double estimate = distance + target.calculateHeuristic(target);
        System.out.println("geschätze kosten: "+estimate);
        if (estimate > threshold){
            System.out.println("threshold mit: "+estimate+ " überschritten");
            return estimate;
        }

        double minEstimate = Double.MAX_VALUE;
        for (int i=0; i<node.neighbors.size();i++){
            double t = iDAStern(node.neighbors.get(i).node,target,distance+node.neighbors.get(i).weight,threshold);
            if(t<0){
                return t;
            }else if (t<minEstimate){
                minEstimate = t;
            }
        }
        return minEstimate;
    }
//    public static List<Node> idaStarAlg(Node start, Node target) {
//        List<Node> resultList = new ArrayList<>();
//        double Limit = root.calculateHeuristic(target);
//
//        while (true) {
//            int temp = search(start, 0, Limit); //function search(node,g score,threshold)
//            if (temp == target.calculateHeuristic(target))                                 //if goal found
//                System.out.println(target);
//            //if (temp == ∞)                               //Threshold larger than maximum possible f value
//            //return;                               //or set Time limit exceeded
//            Limit = temp;
//        }
//        return resultList;
//
//    }
}
