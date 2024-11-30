package com.freighthub.core.service;

import java.util.*;

public class PathFindingService {
  public static Map<String, Object> dijkstraDenseWithHeap(int[][] graph, int source) {
    int n = graph.length;
    int[] dist = new int[n];
    int[] parent = new int[n];
    List<List<Integer>> paths = new ArrayList<>();

    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(parent, -1);
    dist[source] = 0;

    PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    priorityQueue.add(new int[]{0, source}); // {distance, node}

    while (!priorityQueue.isEmpty()) {
      int[] current = priorityQueue.poll();
      int currentDist = current[0];
      int u = current[1];

      if (currentDist > dist[u]) {
        continue; // Skip outdated entries
      }

      for (int v = 0; v < n; v++) {
        if (graph[u][v] > 0) { // Check if there is an edge
          int newDist = dist[u] + graph[u][v];
          if (newDist < dist[v]) {
            dist[v] = newDist;
            parent[v] = u;
            priorityQueue.add(new int[]{newDist, v});
          }
        }
      }
    }

    // Build paths from the parent array
    for (int i = 0; i < n; i++) {
      List<Integer> path = new ArrayList<>();
      for (int current = i; current != -1; current = parent[current]) {
        path.add(current);
      }
      Collections.reverse(path);
      paths.add(path);
    }

    // Create a result map to return distances and paths
    Map<String, Object> result = new HashMap<>();
    result.put("dist", dist);
    result.put("paths", paths);

    return result;
  }

  // Helper method to print distances and paths
  public static void printDistancesAndPaths(int source, int[] dist, List<List<Integer>> paths) {
    System.out.println("Source Node: " + source);
    for (int i = 0; i < dist.length; i++) {
      if (dist[i] == Integer.MAX_VALUE) {
        System.out.println("Node " + i + " is not reachable from node " + source + ".");
      } else {
        System.out.println("Shortest path to node " + i + ": " + paths.get(i) + " with distance " + dist[i]);
      }
    }
  }

  // Create adjacency list from paths
  public static Map<Integer, Set<Integer>> createAdjacencyList(List<List<Integer>> paths) {
    Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();

    for (List<Integer> path : paths) {
      for (int i = 0; i < path.size() - 1; i++) {
        int node1 = path.get(i);
        int node2 = path.get(i + 1);

        adjacencyList.putIfAbsent(node1, new HashSet<>());
        adjacencyList.putIfAbsent(node2, new HashSet<>());

        adjacencyList.get(node1).add(node2);
        adjacencyList.get(node2).add(node1);
      }
    }

    return adjacencyList;
  }

  // Find branches in a graph starting from a given node
  public static List<List<Integer>> findBranches(Map<Integer, Set<Integer>> graph, int startNode) {
    Set<Integer> visited = new HashSet<>();
    List<List<Integer>> branches = new ArrayList<>();

    for (int neighbor : graph.getOrDefault(startNode, new HashSet<>())) {
      if (!visited.contains(neighbor)) {
        List<Integer> branch = new ArrayList<>();
        branch.add(startNode);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(neighbor);
        visited.add(neighbor);

        while (!queue.isEmpty()) {
          int current = queue.poll();
          branch.add(current);

          for (int adj : graph.getOrDefault(current, new HashSet<>())) {
            if (!visited.contains(adj) && adj != startNode) {
              queue.add(adj);
              visited.add(adj);
            }
          }
        }

        branches.add(branch);
      }
    }

    return branches;
  }

  public void findPaths(){
    int[][] graph = {
        {0, 5, 10, 3, 15}, // Node 1
        {5, 0, 7, 3, 8},   // Node 2
        {10, 7, 0, 4, 12}, // Node 3
        {3, 3, 4, 0, 6},   // Node 4
        {15, 8, 12, 6, 0}  // Node 5
    };

    int source = 0;

    // Run Dijkstra's algorithm
    Map<String, Object> dijkstraResult = dijkstraDenseWithHeap(graph, source);
    int[] dist = (int[]) dijkstraResult.get("dist");
    List<List<Integer>> paths = (List<List<Integer>>) dijkstraResult.get("paths");

    // Print distances and paths
    printDistancesAndPaths(source, dist, paths);


    Map<Integer, Set<Integer>> adjacencyList = createAdjacencyList(paths);

    // Find branches starting from node 0
    List<List<Integer>> branches = findBranches(adjacencyList, 0);

    // Print branches
    System.out.println("\nBranches starting from node 0:");
    for (List<Integer> branch : branches) {
      System.out.println(branch);
    }
  }
}
