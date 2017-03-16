package student;

import b.e.N;
import b.j.L;
import b.j.i.a.E;
import com.google.common.collect.Lists;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.IntObjectLinkedMap;
import game.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByKey;

public class Explorer {

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   * <p>
   * There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.
   * <p>
   * At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).
   * <p>
   * To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.
   * <p>
   * Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.
   * <p>
   * A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.
   *
   * @param state the information available at the current state
   */


  public void explore(ExplorationState state) {
    //TODO:
      System.out.println("DistanceToTarget: "+state.getDistanceToTarget()+" CurrentLocation: "
              +state.getCurrentLocation()+" Neighbors: "+state.getNeighbours());

      List<Long> visitedStates = new ArrayList<>();
      Stack<NodeStatus> lastState = new Stack<>();

      NodeStatus move = state.getNeighbours().stream().findFirst().get();
      visitedStates.add(move.getId());
      lastState.push(move);
      state.moveTo(move.getId());

      while(state.getDistanceToTarget() != 0) {

          // unvisited neighbours
          List<NodeStatus> unvisited = state.getNeighbours()
                  .stream().filter(a -> !visitedStates.contains(a.getId()))
                  .collect(Collectors.toList());

          if(!unvisited.isEmpty()){
              NodeStatus sMin = unvisited.stream()
                      .min(Comparator.comparingInt(NodeStatus::getDistanceToTarget))
                      .get();
              visitedStates.add(sMin.getId());
              lastState.push(sMin);
              state.moveTo(sMin.getId());

              // only visited
          }else{
                // if he gets far away
                lastState.pop();
                NodeStatus n = lastState.pop();
                visitedStates.add(n.getId());
                lastState.push(n);
                state.moveTo(n.getId());
          }
      }
  }


  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   * <p>
   * You now have access to the entire underlying graph, which can be accessed through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.
   * <p>
   * Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.
   * <p>
   * You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.
   * <p>
   * You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    //TODO: Escape from the cavern before time runs out
      /*List<Node> path = new ArrayList<>();
    Map<Node, NodeInfo> closedList = new HashMap<>();
    Map<Node, NodeInfo> openList = new HashMap<>();

    openList.put(state.getCurrentNode(), new NodeInfo());

    while(!openList.isEmpty()){

        Node q  = getMin();
        openList.remove(q); // the node of the returned

        Set<Node> neighbors = q.getNeighbours();
        for(Node i: neighbors){
            if(i.equals(goal )){
                break;
            }
            // set parents to q

            //set i g = i.getEdge(q).length
            // set i.h = calculate distance to goal
            // set i.f = i.g + i.h

            if(openList.containsKey(q) && openList.get(q).getF() < i.f){

            }

            if(closedList.containsKey(q) && openList.get(q).getF() <i.f){

            }else{
                openList.put(i, new NodeInfo());
            }

        }
        closedList.put(q, new NodeInfo());

    }

*/






      //state.getCurrentNode().getNeighbours().forEach(a -> infoMap.put(a, new NodeInfo(a)));

      //System.out.println("distance" + infoMap.get(state.getCurrentNode()).cost(state.getExit()));


      //openList = state.getCurrentNode().getNeighbours().stream().collect(Collectors.toList());

    /*
      Node pointer = state.getCurrentNode();

      while(!pointer.equals(state.getExit())) {
          System.out.println("P");
           Node sMin = getMin(pointer, state.getExit());
          infoMap.put(pointer, sMin);
          pointer = sMin;
      }

      System.out.println(infoMap.size());
    */
    /*
    while(!state.getCurrentNode().equals(state.getExit())) {

        state.getCurrentNode().getNeighbours().forEach(a -> infoMap.put(a,
                cost(state.getCurrentNode(), state.getExit(), a)));

        //infoMap.forEach((a, b) -> System.out.println(b));

        java.util.Map.Entry r = infoMap.entrySet().stream()
                .sorted(Map.Entry.<Node, Double>comparingByValue()).findFirst().get();
        System.out.println(r.getValue());
        state.moveTo((Node) r.getKey());

        //state.getCurrentNode().getExits()

        infoMap.clear();

    }
    */



      /*
      openList.forEach(a -> System.out.println(a.getId()));
      List<Node> toFeed = openList.stream().collect(Collectors.toList());
      toFeed = toFeed.subList(0,toFeed.indexOf(state.getCurrentNode()));
      toFeed = Lists.reverse(toFeed);
      System.out.println("here");
      toFeed.forEach(a -> System.out.println(a.getId()));

      System.out.println(" start " +state.getCurrentNode().getId());
      System.out.println(" exit " +state.getExit().getId());
      System.out.println(openList.size());

     state.getCurrentNode().getNeighbours().forEach(a -> System.out.println("n" + a.getId()));

    */

      search(state.getCurrentNode(), state.getExit());


  }

  private List<Node> search(Node start, Node end) {
      List<Node> path = new ArrayList<>();
      Map<Node, NodeInfo> closedList = new HashMap<>();
      Map<Node, NodeInfo> openList = new HashMap<>();


      openList.put(start, new NodeInfo(null,0, 0, 0));

      boolean running = true;
      while(running){

          //java.util.Map.Entry r = openList.entrySet().stream()
          //        .sorted(Map.Entry.comparingByValue((a, b) -> b.getF())).findFirst().get();

          java.util.Map.Entry r = openList.entrySet().stream()
                  .min(Map.Entry.comparingByValue((a, b) -> b.getF())).get();


          //java.util.Map.Entry r = openList.entrySet().stream()
          //        .min(Map.Entry.comparingByValue((a, b) -> b.getF())).get();

          Node q = (Node) r.getKey();

          System.out.println("value" + "x" + q.getTile().getRow() +"y"+ q.getTile().getColumn());

          //java.util.Map.Entry r = openList.entrySet().stream()
          //        .sorted(Map.Entry.<Node, Double>comparingByValue()).findFirst().get();

          openList.remove(q); // the node of the returned
          System.out.println(openList.size());
          //closedList.put((Node) r.getKey(), (NodeInfo) r.getValue());
          Set<Node> neighbors = q.getNeighbours();
          for (Node i : neighbors) {
              if (i.equals(end)) {
                  System.out.println("stop");
                  running = false;
              }

              int g = i.getEdge(q).length;
              int h = distance(i, end);
              int f = g + h;
              System.out.println("f " + f);
              //set i g = i.getEdge(q).length
              // set i.h = calculate distance to goal
              // set i.f = i.g + i.h

              // set parents to q

              boolean ol = true, cl = true;

              if (openList.containsKey(i)) {
                  System.out.println("yes1");
                  if (openList.get(i).getF() < f) {
                      System.out.println("yes3");
                      ol = false;
                  }
              }

              if (closedList.containsKey(i)) {
                  System.out.println("yes2");
                  if (closedList.get(i).getF() < f) {
                      System.out.println("yes4");
                      cl = false;
                  }
              }

              if (ol && cl) {
                  openList.put(i, new NodeInfo(q, f, g, h));
              }

          }
          closedList.put((Node) r.getKey(), (NodeInfo) r.getValue());

      }
      return path;

  }



   private class NodeInfo{
       private Node parent;
       private int f, g, h;

       public NodeInfo(Node parent, int f, int g, int h) {
           this.parent = parent;
           this.f = f;
           this.g = g;
           this.h = h;
       }

       public Node getParent() {
           return parent;
       }

       public void setParent(Node parent) {
           this.parent = parent;
       }

       public int getF() {
           return f;
       }

       public void setF(int f) {
           this.f = f;
       }

       public int getG() {
           return g;
       }

       public void setG(int g) {
           this.g = g;
       }

       public int getH() {
           return h;
       }

       public void setH(int h) {
           this.h = h;
       }
   }

    private int distance(Node parent, Node target){
        int xN = parent.getTile().getColumn();
        int yN = parent.getTile().getRow();

        int xEnd = target.getTile().getColumn();
        int yEnd = target.getTile().getRow();
        int d = (Math.abs((xN - xEnd))) + (Math.abs(yN -yEnd));

        //Double d = (Math.sqrt((Math.pow((xN-xEnd),2)) - ((Math.pow((yN-yEnd), 2)))));
        System.out.println(d);
        return d;

    }

}