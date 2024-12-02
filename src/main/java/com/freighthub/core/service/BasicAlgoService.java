package com.freighthub.core.service;

import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.dto.PurchaseOrderDto;
import com.freighthub.core.entity.*;
import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicAlgoService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RouteRepository routeRepository;




    // Need new class for route splitting here.
    // class will contain a list of PoId and a list of containerAssignment objects

    // For clustering
    class RouteCluster {
        int[] clusterLabels; // Array to hold cluster labels
        List<double[]> centers; // List to hold cluster centers
        List<double[]> outliers;
    }

    // For initial route branching
    class RouteBranch {
        Integer branch;
        List<PurchaseOrder> purchaseOrders;
        List<containerAssignment> containerAssignments;
    }

    // For container bucketing
    class containerAssignment {
        ContainerType containerType;
        Integer containerTypeIndex;
        List<Item> items;
        List<VehicleAssignment> vehicleAssignments;
    }

    // For vehicle assignment on cbm
    class VehicleAssignment {
        VehicleType vehicleType;
        Integer vehicleIndex;
        List<Item> items;
    }

    /////////////////////PATH FINDING HELPERS///////////////////////////////////////////////
    public RouteCluster getTheClusters(String[] points) {
        try {
            // Path to the Python script
//            String pythonScriptPath = "E:\\Documents\\Lecture Materials\\Year 3\\Group Project\\cluster_algorithm_python\\main.py";

            // Load the Python script from the resources folder
            ClassPathResource resource = new ClassPathResource("main.py");
            File pythonScript = resource.getFile();

            // Get the absolute path
            String pythonScriptPath = pythonScript.getAbsolutePath();

            // Build command with arguments
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("python", pythonScriptPath);
            for (String point : points) {
                pb.command().add(point);
            }

            // Start the process
            Process process = pb.start();

            // Capture Python script's stdout
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            RouteCluster routeCluster = new RouteCluster();
            routeCluster.centers = new ArrayList<>();
            routeCluster.outliers = new ArrayList<>();


            // Read the Python script's output
            String line;
            boolean parsingCenters = false;
            boolean parsingOutliers = false;

            while ((line = stdInput.readLine()) != null) {
                // Detect and parse the cluster labels
                if (line.startsWith("Cluster Labels:")) {
                    String[] labels = line.substring(line.indexOf('[') + 1, line.indexOf(']')).trim().split("\\s+");
                    routeCluster.clusterLabels = new int[labels.length];
                    for (int i = 0; i < labels.length; i++) {
                        routeCluster.clusterLabels[i] = Integer.parseInt(labels[i]);
                    }
                }

                // Detect and start parsing centers
                if (line.startsWith("Centers")) {
                    parsingCenters = true;
                    parsingOutliers = false;
                    continue;
                }

                // Detect and start parsing outliers
                if (line.startsWith("Outliers")) {
                    parsingCenters = false;
                    parsingOutliers = true;
                    continue;
                }

                // Parse center points
                if (parsingCenters && !line.isBlank()) {
                    double[] center = parsePoint(line);
                    routeCluster.centers.add(center);
                }

                // Parse outlier points
                if (parsingOutliers && !line.isBlank()) {
                    double[] outlier = parsePoint(line);
                    routeCluster.outliers.add(outlier);
                }
            }

            // Wait for the process to complete and get the exit code
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

            // Remove source from outliers
            double[] pointAtIndex0 = routeCluster.centers.get(0);
            routeCluster.outliers.removeIf(outlier -> Arrays.equals(outlier, pointAtIndex0));

            return routeCluster;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility method to parse a single point string into a double array
    private double[] parsePoint(String pointString) {
        pointString = pointString.replaceAll("[\\[\\]]", ""); // Remove brackets
        String[] coords = pointString.trim().split("\\s+");
        double[] point = new double[coords.length];
        for (int i = 0; i < coords.length; i++) {
            point[i] = Double.parseDouble(coords[i]);
        }
        return point;
    }

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
    public Map<Integer, Set<Integer>> createAdjacencyList(List<List<Integer>> paths) {
        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();
        for (List<Integer> path : paths) {
            for (int i = 0; i < path.size() - 1; i++) {
                adjacencyList
                        .computeIfAbsent(path.get(i), k -> new HashSet<>())
                        .add(path.get(i + 1));
            }
        }
        return adjacencyList;
    }

    // Find branches in a graph starting from a given node
    public List<List<PurchaseOrderDto>> findBranchesWithDtos(
            Map<Integer, Set<Integer>> adjacencyList,
            int startNode,
            Map<Integer, PurchaseOrderDto> nodeToPurchaseOrderDto) {

        List<List<PurchaseOrderDto>> branches = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(startNode));

        while (!queue.isEmpty()) {
            List<Integer> currentPath = queue.poll();
            int lastNode = currentPath.get(currentPath.size() - 1);

            if (!adjacencyList.containsKey(lastNode)) { // End of branch
                List<PurchaseOrderDto> branch = currentPath.stream()
                        .skip(1) // Skip the source node
                        .map(nodeToPurchaseOrderDto::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                branches.add(branch);
            } else {
                for (int neighbor : adjacencyList.get(lastNode)) {
                    List<Integer> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return branches;
    }

    public int[][] generateGraph(OrderDto order, List<double[]> centers, List<double[]> outliers) {
        int size = centers.size() + outliers.size();
        int[][] graph = new int[size][size];

        List<String> locations = new ArrayList<>();
        for (double[] center : centers) {
            locations.add(center[0] + "," + center[1]);
        }
        for (double[] outlier : outliers) {
            locations.add(outlier[0] + "," + outlier[1]);
        }

        // Call Google Maps Distance Matrix API
        int[][] distances = GoogleDistanceCalculator.calculateDistanceMatrix(locations);

        System.out.println("All Distances:");
        //print distances
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(distances[i][j] + " ");
            }
            System.out.println();
        }

        // Fill the graph with the distances
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    graph[i][j] = 0; // No self-loops
                } else {
                    graph[i][j] = distances[i][j]; // Distance from i to j
                }
            }
        }

        return graph;
    }

    ////////////COMPUTE ROUTES////////////////////////////////////////////////////////////////////
    ////////////DRIVER FUNC///////////////////////////////////////////////////////////////////////
    public void computeRoutes(int orderId) {
        // Fetch Order and PurchaseOrders
        Order order = orderRepository.findById((long) orderId) // Example ID
                .orElseThrow(() -> new RuntimeException("Order not found"));
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByOrderId(order);

        // Convert to DTOs
        OrderDto orderDto = convertToOrderDto(order);
        List<PurchaseOrderDto> purchaseOrderDtos = purchaseOrders.stream()
                .map(this::mapToPurchaseOrderDto)
                .toList();

        String[] points = new String[purchaseOrderDtos.size() + 1];
        points[0] = orderDto.getPickupLocation().getLat() + "," + orderDto.getPickupLocation().getLng();
        for (int i = 0; i < purchaseOrderDtos.size(); i++) {
            points[i + 1] = purchaseOrderDtos.get(i).getDropLocation().getLat() + "," + purchaseOrderDtos.get(i).getDropLocation().getLng();
        }
        // Call the clustering algorithm
        RouteCluster routeCluster = getTheClusters(points);
        if (routeCluster == null) {
            throw new RuntimeException("Error while clustering");
        }

        // Print the values in route cluster
        System.out.println("Cluster Labels: " + Arrays.toString(routeCluster.clusterLabels));

        System.out.print("Centers: ");
        for (double[] center : routeCluster.centers) {
            System.out.print(Arrays.toString(center) + " ");
        }
        System.out.println();

        System.out.print("Outliers: ");
        for (double[] outlier : routeCluster.outliers) {
            System.out.print(Arrays.toString(outlier) + " ");
        }
        System.out.println();

        // Segregate PurchaseOrderDto into clusters and outliers based on cluster labels
        Map<Integer, List<PurchaseOrderDto>> clusterToPurchaseOrders = new HashMap<>();
        List<PurchaseOrderDto> outliers = new ArrayList<>();

        // Skip the source (index 0) and process labels
        for (int i = 1; i < routeCluster.clusterLabels.length; i++) {
            int cluster = routeCluster.clusterLabels[i];
            PurchaseOrderDto purchaseOrder = purchaseOrderDtos.get(i - 1); // Adjust index for purchaseOrderDtos

            if (cluster == -1) {
                // Outlier case
                outliers.add(purchaseOrder);
            } else {
                // Add to the corresponding cluster
                clusterToPurchaseOrders
                        .computeIfAbsent(cluster, k -> new ArrayList<>())
                        .add(purchaseOrder);
            }
        }

        // Print the clusters and outliers
        System.out.println("Clusters:");
        for (Map.Entry<Integer, List<PurchaseOrderDto>> entry : clusterToPurchaseOrders.entrySet()) {
            System.out.println("Cluster " + entry.getKey());
            for (PurchaseOrderDto po : entry.getValue()) {
                System.out.println(po.getId());
            }
        }
        // print each outlier
        System.out.println("Outliers:");
        for (PurchaseOrderDto outlier : outliers) {
            System.out.println(outlier.getId());
        }


        // Create graph and compute paths
        int[][] graph = generateGraph(orderDto, routeCluster.centers, routeCluster.outliers);
        Map<String, Object> dijkstraResult = dijkstraDenseWithHeap(graph, 0);
        List<List<Integer>> paths = (List<List<Integer>>) dijkstraResult.get("paths");

        //print paths
        System.out.println("Paths:");
        for (List<Integer> path : paths) {
            System.out.println(path);
        }

        List<RouteBranch> routeBranches = new ArrayList<>();

        for (int i=1; i<paths.size(); i++) {
            List<Integer> path = paths.get(i);

            // Create a new RouteBranch for this path
            RouteBranch routeBranch = new RouteBranch();
            routeBranch.branch = i; // Set branch index

            // List to hold purchase orders for this branch
            List<PurchaseOrder> purchaseOrdersList = new ArrayList<>();

            // Iterate through the path (skipping the source node at index 0)
            for (int j = 1; j < path.size(); j++) {
                int destination = path.get(j);
                int clusterKey = destination - 1; // Adjust index to match clusterToPurchaseOrders

                // Check if this clusterKey exists in clusterToPurchaseOrders
                if (clusterToPurchaseOrders.containsKey(clusterKey)) {
                    List<PurchaseOrderDto> purchaseOrderDtosInCluster = clusterToPurchaseOrders.get(clusterKey);

                    // Convert PurchaseOrderDto to PurchaseOrder and add to purchaseOrders list
                    for (PurchaseOrderDto dto : purchaseOrderDtosInCluster) {
                        PurchaseOrder purchaseOrder = convertDtoToEntity(dto); // Assuming a conversion function
                        purchaseOrdersList.add(purchaseOrder);
                    }
                } else {
                    // This is an outlier
                    PurchaseOrderDto outlier = outliers.get(clusterKey - clusterToPurchaseOrders.size()); // Adjust index for outliers
                    PurchaseOrder purchaseOrder = convertDtoToEntity(outlier); // Assuming a conversion function
                    purchaseOrdersList.add(purchaseOrder);
                }
            }

            // Set the purchase orders in the route branch
            routeBranch.purchaseOrders = purchaseOrdersList;

            // Add the RouteBranch to the list
            routeBranches.add(routeBranch);
        }

//        // Create adjacency list and find branches
//        Map<Integer, Set<Integer>> adjacencyList = createAdjacencyList(paths);
//
//        //print adjacency list
//        System.out.println("Adjacency List:");
//        for (Map.Entry<Integer, Set<Integer>> entry : adjacencyList.entrySet()) {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        }
//

        // Divide container types
        for (RouteBranch routeBranch : routeBranches) {
            System.out.println("\nFor Route Branch: " + routeBranch.branch);
            routeBranch.containerAssignments = divideContainerTypes(routeBranch.purchaseOrders);
        }

        System.out.println("/////////// Whole Assigment ////////////\n");

        // print whole thing
        for (RouteBranch routeBranch : routeBranches) {
            System.out.println("\nBranch: " + routeBranch.branch);
            for (containerAssignment assignment : routeBranch.containerAssignments) {
                System.out.println("\n-Container Assignment: " + assignment.containerType + "_" + assignment.containerTypeIndex);
                for (VehicleAssignment vehicleAssignment : assignment.vehicleAssignments) {
                    System.out.println("--**Vehicle Assignment: " + vehicleAssignment.vehicleType.getId() + "_" + vehicleAssignment.vehicleIndex);
                    for (Item item : vehicleAssignment.items) {
                        System.out.println("---Assigned Item: " + item.getId() + " - " + item.getCbm());
                    }
                }
            }
        }

        // Create routes and assign to items
//        try {
//            createRoutesAndAssignToItems(routeBranches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }



    ////////////CONTAINER TYPE DIVIDE////////////////////////////////////////////////////////////////////

    public List<containerAssignment> divideContainerTypes(List<PurchaseOrder> purchaseOrderList) {

        List<containerAssignment> containerAssignments = new ArrayList<>(); // List of container assignments

        List<Item> items = new ArrayList<>(); // List of items

        for (PurchaseOrder po : purchaseOrderList) {
            items.addAll((List<Item>) itemRepository.getItemsByPoId(po)); // Get items by purchase order
        }

        items.sort(Comparator.comparing(Item::getCbm).reversed()); // Sort items by cbm in descending order

        // Create a map to track compatible groups
        // Key: Pair of container type and compatibility, Value: List of items
        Map<String, List<Item>> compatibilityBuckets = new HashMap<>();

        for (Item item : items) {
            ItemType itemType = item.getITypeId();
            String bucketKey = itemType.getContainerType().name() + "-" + itemType.getCompatibility();

            // Add the item to the corresponding bucket
            compatibilityBuckets.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(item);
        }

        // Create container assignments from the buckets
        for (Map.Entry<String, List<Item>> entry : compatibilityBuckets.entrySet()) {
            String[] parts = entry.getKey().split("-");
            ContainerType containerType = ContainerType.valueOf(parts[0]);
            Integer compatibilityIndex = Integer.valueOf(parts[1]);

            // Create a new container assignment
            containerAssignment assignment = new containerAssignment();
            assignment.containerType = containerType;
            assignment.containerTypeIndex = compatibilityIndex;
            assignment.items = entry.getValue();

            // Add the assignment to the list
            containerAssignments.add(assignment);
        }

        for (containerAssignment assignment : containerAssignments) {
            System.out.println("\nFOR CONTAINER TYPE: " + assignment.containerType + "_" + assignment.containerTypeIndex);
            assignment.vehicleAssignments = getItemsAndDivide(assignment.items);
        }

//        System.out.println("/////////// Whole Assigment ////////////\n");

        // print container assignments with vehicle assignments
//        for (containerAssignment assignment : containerAssignments) {
//            System.out.println("\nContainer Assignment: " + assignment.containerType + "_" + assignment.containerTypeIndex);
//            for (VehicleAssignment vehicleAssignment : assignment.vehicleAssignments) {
//                System.out.println("-Vehicle Assignment: " + vehicleAssignment.vehicleType.getId() + "_" + vehicleAssignment.vehicleIndex);
//                for (Item item : vehicleAssignment.items) {
//                    System.out.println("--Assigned Item: " + item.getId() + " - " + item.getCbm());
//                }
//            }
//        }

        return containerAssignments;

    }

    ////////////CBM DIVIDE////////////////////////////////////////////////////////////////////

    public List<VehicleAssignment> getItemsAndDivide(List<Item> items) {
        // Get vehicle types
        List<VehicleType> vehicleTypes = vehicleTypeRepository.findAll();
        // Sort vehicle types by capacity
        vehicleTypes.sort(Comparator.comparing(VehicleType::getMaxCapacity));

        // Create a new list for this invocation
        List<VehicleAssignment> vehicleAssignments = new ArrayList<>();

//        System.out.println("\n///////////Items to be divided into vehicles////////////");
        if (divideGoods(items, vehicleTypes, vehicleAssignments) == 1) {
            System.out.println("Items cannot be divided into vehicles");
        } else {
            System.out.println("///////////Items divided into vehicles////////////\n\n");
            return vehicleAssignments;
        }
        return List.of();
    }


    public int divideGoods(List<Item> items, List<VehicleType> vehicleTypes, List<VehicleAssignment> vehicleAssignments) {

        // get total of item cbm
        BigDecimal totalCbm = new BigDecimal("0.00");

        for (Item item : items) {
//            System.out.println("-Item: " + item.getId() + " - " + item.getCbm());
            totalCbm = totalCbm.add(item.getCbm());
        }

        for (VehicleType vehicleType : vehicleTypes) {
//            System.out.println("Vehicle Type: " + vehicleType.getId() + " - " + vehicleType.getMaxCapacity() + "......");
            if (totalCbm.compareTo(vehicleType.getMaxCapacity()) <= 0) {
                createVehicleAssignment(vehicleType, items, vehicleAssignments);
//                System.out.println("*Vehicle selected for bulk: " + vehicleType.getId() + " - " + totalCbm);
                return 0;
            }
        }

        BigDecimal currentWeight = vehicleTypes.getLast().getMaxCapacity();
        int i = 0;

        // Iterate through items while currentWeight - item's cbm > 0
        while (i < items.size() && currentWeight.compareTo(items.get(i).getCbm()) >= 0) {
            Item item = items.get(i);
//            System.out.println("*vehicle selected for item (exceeded): " + item.getId() + " - CBM: " + item.getCbm());
            currentWeight = currentWeight.subtract(item.getCbm());
            i++;
        }

        if (i == 0) {
            return 1;
        }

        createVehicleAssignment(vehicleTypes.getLast(), items.subList(0, i), vehicleAssignments);

        // get items in item list after index i
        List<Item> remainingItems = items.subList(i, items.size());
        if (remainingItems.size() > 0) {
            divideGoods(remainingItems, vehicleTypes, vehicleAssignments);
        }

        return 0;
    }

    ////////////FIN////////////////////////////////////////////////////////////////////

    // Google Distance Calculator
    public class GoogleDistanceCalculator {

        private static final String API_KEY = "";

        private static GeoApiContext getGeoApiContext() {
            return new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();
        }

        /**
         * Uses Google Maps Distance Matrix API to calculate a distance matrix.
         *
         * @param locations List of locations in "latitude,longitude" format.
         * @return 2D array of distances in meters.
         */
        public static int[][] calculateDistanceMatrix(List<String> locations) {
            GeoApiContext context = getGeoApiContext();

            try {
                DistanceMatrix result = DistanceMatrixApi.getDistanceMatrix(
                        context,
                        locations.toArray(new String[0]),
                        locations.toArray(new String[0])
                ).await();

                int size = locations.size();
                int[][] distanceMatrix = new int[size][size];

                for (int i = 0; i < result.rows.length; i++) {
                    for (int j = 0; j < result.rows[i].elements.length; j++) {
                        DistanceMatrixElement element = result.rows[i].elements[j];
                        if (element.distance != null) {
                            distanceMatrix[i][j] = (int) element.distance.inMeters;
                        } else {
                            distanceMatrix[i][j] = Integer.MAX_VALUE; // Unreachable
                        }
                    }
                }

                return distanceMatrix;

            } catch (Exception e) {
                throw new RuntimeException("Error while fetching distances from Google Maps API", e);
            }
        }
    }

    // Helper method to create a new VehicleAssignment
    private void createVehicleAssignment(VehicleType vehicleType, List<Item> items, List<VehicleAssignment> vehicleAssignments) {
        // Determine the next index for this vehicle type
        int vehicleIndex = getNextIndexForVehicleType(vehicleType, vehicleAssignments);

        // Create and add the assignment
        VehicleAssignment assignment = new VehicleAssignment();
        assignment.vehicleType = vehicleType;
        assignment.vehicleIndex = vehicleIndex;
        assignment.items = new ArrayList<>(items); // Copy the items list

        vehicleAssignments.add(assignment);
    }

    // Helper method to get the next index for a vehicle type
    private int getNextIndexForVehicleType(VehicleType vehicleType, List<VehicleAssignment> vehicleAssignments) {
        int maxIndex = vehicleAssignments.stream()
                .filter(va -> va.vehicleType.equals(vehicleType))
                .mapToInt(va -> va.vehicleIndex)
                .max()
                .orElse(0); // Start with 0 if no vehicles of this type exist
        return maxIndex + 1;
    }

    private OrderDto convertToOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                RouteService.PointConverter.getLatitude(order.getPickupLocation()),
                RouteService.PointConverter.getLongitude(order.getPickupLocation())
        );
    }

    private PurchaseOrderDto mapToPurchaseOrderDto(PurchaseOrder purchaseOrder) {
        return new PurchaseOrderDto(
                purchaseOrder.getId(),
                RouteService.PointConverter.getLatitude(purchaseOrder.getDropLocation()),
                RouteService.PointConverter.getLongitude(purchaseOrder.getDropLocation())
        );
    }

    public List<List<PurchaseOrder>> convertDtoBranchesToEntities(
            List<List<PurchaseOrderDto>> dtoBranches) {
        return dtoBranches.stream()
                .map(branch -> branch.stream()
                        .map(dto -> purchaseOrderRepository.findById(dto.getId())
                                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found: " + dto.getId())))
                        .toList())
                .toList();
    }

    private PurchaseOrder convertDtoToEntity(PurchaseOrderDto dto) {
        return purchaseOrderRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found: " + dto.getId()));
    }

    @Transactional
    public void createRoutesAndAssignToItems(List<RouteBranch> routeBranches) {
        try {
            // Iterate over each route branch
            for (RouteBranch routeBranch : routeBranches) {
                // Iterate over each container assignment in the route branch
                for (containerAssignment containerAssignment : routeBranch.containerAssignments) {
                    // Iterate over each vehicle assignment within the container assignment
                    for (VehicleAssignment vehicleAssignment : containerAssignment.vehicleAssignments) {
                        // Fetch the VehicleType entity
                        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleAssignment.vehicleType.getId())
                                .orElseThrow(() -> new RuntimeException("VehicleType not found"));

                        // Create a new Route record for each vehicle assignment
                        Route route = new Route();
                        route.setContainerType(containerAssignment.containerType);  // Set container type (e.g., "dry")
                        route.setVTypeId(vehicleType);  // Set vehicle type entity

                        // Set other route fields (path, distance, etc.) as necessary.
                        // For now, setting these to null or some dummy values, adjust according to your needs
                        route.setPath(null);  // You'll need actual logic for path
                        route.setDistanceKm(null);  // Adjust accordingly
                        route.setActualDistanceKm(null);  // Adjust accordingly
                        route.setCost(null);  // Adjust accordingly
                        route.setEstdCost(null);  // Adjust accordingly
                        route.setProfit(null);  // Adjust accordingly
                        route.setCraneFlag(false);  // Example: Set this based on business logic
                        route.setRefrigFlag(false);  // Example: Set this based on business logic
                        route.setTimeMinutes(null);  // Set actual time if applicable

                        // Save the Route entity to generate the route ID
                        route = routeRepository.save(route);

                        // Assign the routeId to each item in the vehicle assignment
                        for (Item item : vehicleAssignment.items) {
                            // Update each item's routeId field with the saved route entity
                            item.setRouteId(route);
                            itemRepository.save(item);  // Save the updated item
                        }

                        // Optionally, print the new route and assigned items for debugging or logging
                        System.out.println("Created route for Vehicle Assignment: "
                                + vehicleAssignment.vehicleType.getId() + "_" + vehicleAssignment.vehicleIndex
                                + " and assigned to items: "
                                + vehicleAssignment.items.stream().map(Item::getId).toList());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while creating routes and assigning to items", e);
        }
    }
}
