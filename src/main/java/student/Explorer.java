package main.java.student;

import main.java.game.EscapeState;
import main.java.game.ExplorationState;
import main.java.game.Node;
import main.java.game.NodeStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Explorer class.
 */
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

  public void explore(final ExplorationState state) {
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
  public void escape(final EscapeState state) {
    running(state);
  }

  /**
   * Method for Smain.java.main algorithm.
   *
   * @param state current Smain.java.game state
   */
  private void running(final EscapeState state) {

    if (state.getCurrentNode().getTile().getGold() > 0) {
      state.pickUpGold();
    }

    while (true) {

      List<GoldInfo> shortList = shortList(state);

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

      if (maxGoldSL != null && !maxGoldSL.getRoute().isEmpty()) {
        walking(maxGoldSL.getRoute(), state);
      } else {

        walking(search(state.getCurrentNode(), state.getExit()), state);
        break;
      }

    }

  }

  /**
   * Method for creating shortlist of reachable nodes.
   *
   * @param state current Smain.java.game state
   * @return list of GoldInfo objects
   */
  private List<GoldInfo> shortList(final EscapeState state) {

    List<GoldInfo> goldNodes = state.getVertices()
        .stream().filter(a -> a.getTile().getGold() > 0)
        .map(b -> new GoldInfo(state.getCurrentNode(), b, state.getExit()))
        .collect(Collectors.toList());

    return goldNodes.stream().
        filter(a -> a.getTotalDistance() < state.getTimeRemaining())
        .collect(Collectors.toList());

  }

  /**
   * Inner class containing info on routes to gold nodes.
   */
  private final class GoldInfo {

    /**
     * Shortest path calculated with A-star.
     */
    private List<Node> route;
    /**
     * Distance to gold node.
     */
    private int distance;
    /**
     * Value of gold on route.
     */
    private int goldValue;
    /**
     * Distance to exit.
     */
    private int distanceToExit;
    /**
     * Distance to gold node and exit.
     */
    private int totalDistance;
    /**
     * Shortest path to exit calculated with A-star.
     */
    private List<Node> routeToExit;

    /**
     * Constructor method.
     *
     * @param from start node
     * @param origin the gold node to reach
     * @param exit the exit
     */
    private GoldInfo(final Node from, final Node origin, final Node exit) {
      this.route = search(from, origin);
      this.distance = routeDistanceCal(route, from);
      this.goldValue = goldCalculator(route, from);
      this.routeToExit = search(origin, exit);
      this.distanceToExit = routeDistanceCal(routeToExit, origin);
      this.totalDistance = distance + distanceToExit;

    }

    /**
     * Route from given start point to gold node.
     *
     * @return list of nodes for route
     */
    public List<Node> getRoute() {
      return route;
    }

    /**
     * Value of all gold on route.
     *
     * @return int value of gold
     */
    public int getGoldValue() {
      return goldValue;
    }

    /**
     * Distance from start node to gold node to exit.
     *
     * @return total distance
     */
    public int getTotalDistance() {
      return totalDistance;
    }

  }

  /**
   * Method for moving and picking up gold.
   *
   * @param route list of nodes
   * @param state current Smain.java.game state
   */
  private void walking(final List<Node> route, final EscapeState state) {

    for (Node i : route) {
      state.moveTo(i);

      if (i.getTile().getGold() > 0) {
        state.pickUpGold();
      }
    }
  }


  /**
   * Method calculates amount of gold on route.
   *
   * @param route list of nodes
   * @param start start value
   * @return int amount of gold
   */
  private int goldCalculator(final List<Node> route, final Node start) {
    int goldToReturn = start.getTile().getGold();
    for (Node i : route) {
      goldToReturn += i.getTile().getGold();
    }
    return goldToReturn;
  }

  /**
   * Distance calculator for route.
   *
   * @param route list to calculate cost of
   * @param start start node
   * @return cost of route
   */
  private int routeDistanceCal(final List<Node> route, final Node start) {
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
   * A-star search algorithm.
   *
   * @param start start node
   * @param end end node
   * @return list of nodes to follow for shortest distance
   */
  private List<Node> search(final Node start, final Node end) {

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
      }
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
   * Inner class for NodeInfo.
   */
  private static final class NodeInfo {

    /**
     * Parent of node.
     */
    private Node parent;
    /**
     * f cost of node.
     */
    private int f;

    /**
     * Constructor.
     *
     * @param parent parent of node.
     * @param f calculated f cost
     */
    private NodeInfo(final Node parent, final int f) {
      this.parent = parent;
      this.f = f;
    }

    /**
     * Returns node in route.
     *
     * @return parent
     */
    private Node getParent() {
      return parent;
    }

    /**
     * Returns best move.
     *
     * @return f
     */
    private int getF() {
      return f;
    }

  }


  /**
   * A-star distance calculator method.
   * Calculates manhattan distance.
   *
   * @param start start node.
   * @param end end node.
   * @return int distance.
   */
  private int distanceCal(final Node start, final Node end) {
    int xN = start.getTile().getColumn();
    int yN = start.getTile().getRow();

    int xEnd = end.getTile().getColumn();
    int yEnd = end.getTile().getRow();

    return (Math.abs((xN - xEnd))) + (Math.abs(yN - yEnd));

  }

}
