package student;

import b.e.N;
import game.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
    System.out.println("DistanceToTarget: " + state.getDistanceToTarget() + " CurrentLocation: "
        + state.getCurrentLocation() + " Neighbors: " + state.getNeighbours());

    List<Long> visitedStates = new ArrayList<>();
    Stack<NodeStatus> lastState = new Stack<>();

    NodeStatus move = state.getNeighbours().stream().findFirst().get();
    visitedStates.add(move.getId());
    lastState.push(move);
    state.moveTo(move.getId());

    while (state.getDistanceToTarget() != 0) {

      List<NodeStatus> unvisited = state.getNeighbours()
          .stream().filter(a -> !visitedStates.contains(a.getId()))
          .collect(Collectors.toList());

      if (!unvisited.isEmpty()) {
        NodeStatus sMin = unvisited.stream()
            .min(Comparator.comparingInt(NodeStatus::getDistanceToTarget))
            .get();
        visitedStates.add(sMin.getId());
        lastState.push(sMin);
        state.moveTo(sMin.getId());

      } else {
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

    running(state);
    routeDistanceCal(search(state.getCurrentNode(), state.getExit()), state.getCurrentNode());

  }

  private void running(EscapeState state) {

    // calculating node with gold

    List<GoldInfo> goldNodes = state.getVertices()
        .stream().filter(a -> a.getTile().getGold() > 0)
        .map(b -> new GoldInfo(state.getCurrentNode(), b, state.getExit()))
        .sorted(Comparator.comparingInt(GoldInfo::getTotalDistance))
        .collect(Collectors.toList());

    //System.out.println("time remaining " + state.getTimeRemaining());
    //goldNodes.forEach(a -> System.out.println(
    //    "distance " + a.getDistance() + " gold " + a.getGoldValue() + " total dist " + a.getTotalDistance()));

    List<GoldInfo> shortList = goldNodes.stream().
        filter(a -> a.getTotalDistance() < state.getTimeRemaining()).collect(Collectors.toList());

    //shortList.forEach(a -> System.out.println(
    //  "distance " + a.getDistance() + " gold " + a.getGoldValue() + " total dist " + a.getTotalDistance()));

    GoldInfo maxGoldSL;

    if (shortList.stream()
        .max(Comparator.comparingInt(GoldInfo::getGoldValue))
        .isPresent()) {
      maxGoldSL = shortList.stream()
          .max(Comparator.comparingInt(GoldInfo::getGoldValue))
          .get();
    } else {
      maxGoldSL = null;
    }

    if (maxGoldSL != null) {
      walking(maxGoldSL.getRoute(), state);
      walking(maxGoldSL.routeToExit, state);
    } else {
      walking(search(state.getCurrentNode(), state.getExit()), state);
    }
  }

  /**
   * Inner class containing info on gold status
   */
  private class GoldInfo {

    private Node origin;
    private List<Node> route;
    private int distance;
    private int goldValue;
    private int distanceToExit;
    private int totalDistance;
    private List<Node> routeToExit;

    public GoldInfo(Node from, Node origin, Node exit) {
      this.origin = origin;
      this.route = search(from, origin);
      this.distance = routeDistanceCal(route, from);
      this.goldValue = goldCalculator(route, from);
      this.routeToExit = search(origin, exit);
      this.distanceToExit = routeDistanceCal(routeToExit, origin);
      this.totalDistance = distance + distanceToExit;

    }

    public List<Node> getRoute() {
      return route;
    }

    public int getDistance() {
      return distance;
    }

    public int getGoldValue() {
      return goldValue;
    }

    public Node getOrigin() {
      return origin;
    }

    public int getTotalDistance() {
      return totalDistance;
    }

    public int getDistanceToExit() {
      return distanceToExit;
    }

    public List<Node> getRouteToExit() {
      return routeToExit;
    }
  }

  /**
   * Method for moving and picking up gold
   */
  private void walking(List<Node> route, EscapeState state) {

    for (Node i : route) {
      state.moveTo(i);

      if (i.getTile().getGold() > 0) {
        state.pickUpGold();
      }
    }
  }


  /**
   * calculates gold on route
   */
  private int goldCalculator(List<Node> route, Node start) {
    int goldToReturn = start.getTile().getGold();
    for (Node i : route) {
      goldToReturn += i.getTile().getGold();
    }
    return goldToReturn;
  }

  /**
   * Distance calculator for route
   *
   * @param route list to calculate cost of
   * @param start start node
   * @return cost of route
   */
  private int routeDistanceCal(List<Node> route, Node start) {
    int distanceToReturn;

    if (!route.isEmpty()) {
      distanceToReturn = start.getEdge(route.get(0)).length;
    } else {
      distanceToReturn = 0;
    }

    for (int i = 0; i < route.size() - 1; i++) {
      distanceToReturn += route.get(i).getEdge(route.get(i + 1)).length;
    }

    return distanceToReturn;

  }


  /**
   * A-star search algorithm
   */
  private List<Node> search(Node start, Node end) {

    Map<Node, NodeInfo> openList = new LinkedHashMap<>();
    Map<Node, NodeInfo> closedList = new LinkedHashMap<>();
    List<Node> route = new ArrayList<>();
    boolean running = true;

    openList.put(start, new NodeInfo(start, 0));

    while (running) {

      Map.Entry current = openList.entrySet().stream()
          .min(Comparator.comparingInt(a -> a.getValue().getF())).get();

      openList.remove((Node) current.getKey());

      closedList.put((Node) current.getKey(), (NodeInfo) current.getValue());

      if (((Node) current.getKey()).equals(end)) {
        running = false;
      }

      Set<Node> neighbours = ((Node) current.getKey()).getNeighbours();
      for (Node i : neighbours) {

        int g = ((Node) current.getKey()).getEdge(i).length;
        int h = distanceCal(i, end);
        int f = g + h;

        if (!closedList.containsKey(i)) {

          if (openList.containsKey(i)) {

            if (f < openList.get(i).getF()) {
              openList.put(i, new NodeInfo((Node) current.getKey(),
                  f));
            }

          } else {
            openList.put(i, new NodeInfo((Node) current.getKey(),
                f));
          }
        }
      }//7098088759984582890 // 8613558971011317745
    }

    Node p = end;

    while (p != start) {
      route.add(p);
      p = closedList.get(p).getParent();
    }

    Collections.reverse(route);

    return route;

  }

  /**
   * Class for A-star node info
   */
  private class NodeInfo {

    private Node parent;
    private int f;

    private NodeInfo(Node parent, int f) {
      this.parent = parent;
      this.f = f;
    }

    private Node getParent() {
      return parent;
    }

    private int getF() {
      return f;
    }

  }


  /**
   * distance calculator for A-star algorithm
   */
  private int distanceCal(Node start, Node end) {
    int xN = start.getTile().getColumn();
    int yN = start.getTile().getRow();

    int xEnd = end.getTile().getColumn();
    int yEnd = end.getTile().getRow();

    return (Math.abs((xN - xEnd))) + (Math.abs(yN - yEnd));

  }

}

