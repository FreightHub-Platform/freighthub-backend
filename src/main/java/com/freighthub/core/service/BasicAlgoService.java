package com.freighthub.core.service;

import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.dto.PurchaseOrderDto;
import com.freighthub.core.entity.*;
import com.freighthub.core.entity.VehicleType;
import com.freighthub.core.enums.ContainerType;
import com.freighthub.core.repository.*;
import com.google.maps.DirectionsApi;
import com.google.maps.model.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
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
    @Autowired
    private NotificationService notificationService;

    private GoogleDistanceCalculator googleDistanceCalculator;
    private GoogleDirectionsCalculator googleDirectionsCalculator;

    @Autowired
    public BasicAlgoService(@Lazy GoogleDistanceCalculator googleDistanceCalculator) {
        this.googleDistanceCalculator = googleDistanceCalculator;
    }

    @Autowired
    public void setGoogleDirectionsCalculator(@Lazy GoogleDirectionsCalculator googleDirectionsCalculator) {
        this.googleDirectionsCalculator = googleDirectionsCalculator;
    }

    ////////////////////CLASSES LIST////////////////////////////////////////

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

    static class RouteResult {
        String encodedPolyline;
        double totalDistance; // Distance in km
        List<Integer> waypointOrder; // Order of waypoints after optimization

        public RouteResult(String encodedPolyline, double totalDistance, List<Integer> waypointOrder) {
            this.encodedPolyline = encodedPolyline;
            this.totalDistance = totalDistance;
            this.waypointOrder = waypointOrder;
        }
    }
    /////////////////////ROUTE BRANCHING HELPERS///////////////////////////////////////////////

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
        int[][] distances = googleDistanceCalculator.calculateDistanceMatrix(locations);

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
    @Async
    @Transactional
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
        try {
            createRoutesAndAssignToItems(routeBranches, orderDto);
            notificationService.addNotificationRoute("Routes have been assigned for your new order! #" + orderId + " Hang tight till drivers accept the routes.", orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    ////////////HELPER METHODS////////////////////////////////////////////

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

    private PurchaseOrder convertDtoToEntity(PurchaseOrderDto dto) {
        return purchaseOrderRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found: " + dto.getId()));
    }

    public static BigDecimal calculateRouteCost(BigDecimal totalDistance, int vehicleTypeId, BigDecimal dieselPricePerLiter) {
        // Define constants
        BigDecimal fixedCost = new BigDecimal("3000"); // LKR 3000
        BigDecimal driverWagePerKm = new BigDecimal("31.25");

        // Fuel efficiency and maintenance costs based on vehicle type
        BigDecimal fuelEfficiency; // in km/L
        BigDecimal maintenanceCostPerKm;

        switch (vehicleTypeId) {
            case 1: case 2: case 3: // Light-Duty
                fuelEfficiency = new BigDecimal("7"); // Average of 6-8 km/L
                maintenanceCostPerKm = new BigDecimal("3.5"); // Average of 2-5 LKR/km
                break;
            case 10: case 11: case 12: case 13: // Medium-Duty
                fuelEfficiency = new BigDecimal("5"); // Average of 4-6 km/L
                maintenanceCostPerKm = new BigDecimal("5");
                break;
            case 15: case 16: case 17: case 18: case 19: case 20: case 21: case 22: // Heavy-Duty
                fuelEfficiency = new BigDecimal("3"); // Average of 2-4 km/L
                maintenanceCostPerKm = new BigDecimal("8");
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type ID: " + vehicleTypeId);
        }

        // Calculate variable elements
        BigDecimal fuelCost = totalDistance.divide(fuelEfficiency, BigDecimal.ROUND_HALF_UP).multiply(dieselPricePerLiter);
        BigDecimal driverWage = totalDistance.multiply(driverWagePerKm);
        BigDecimal maintenanceCost = totalDistance.multiply(maintenanceCostPerKm);

        // Total cost
        BigDecimal variableCost = fuelCost.add(driverWage).add(maintenanceCost);

        return variableCost.add(fixedCost);
    }

    @Transactional
    public void createRoutesAndAssignToItems(List<RouteBranch> routeBranches, OrderDto orderDto) {
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

                        System.out.println("In assignment 1");

                        // Create a new Route record for each vehicle assignment
                        Route route = new Route();
                        Order order = orderRepository.findByid(orderDto.getId());
                        route.setOrderId(order);
                        route.setContainerType(containerAssignment.containerType);  // Set container type (e.g., "dry")
                        route.setVTypeId(vehicleType);  // Set vehicle type entity

                        // Set other route fields (path, distance, etc.) as necessary.
                        // For now, setting these to null or some dummy values, adjust according to your needs

                        route.setCraneFlag(false);  // Example: Set this based on business logic
                        route.setRefrigFlag(false);  // Example: Set this based on business logic

                        route.setTimeMinutes(null);  // Set actual time if applicable

                        List<PurchaseOrder> purchaseOrdersListForRoute = new ArrayList<>();

                        // get distinct purchaseOrders for items
                        for (Item item : vehicleAssignment.items) {
                            PurchaseOrder po = item.getPoId();
                            if (!purchaseOrdersListForRoute.contains(po)) {
                                System.out.println("Adding PO: " + po.getId());
                                purchaseOrdersListForRoute.add(po);
                            }
                        }

                        List<PurchaseOrderDto> purchaseOrderDtosForRoute = purchaseOrdersListForRoute.stream()
                                .map(this::mapToPurchaseOrderDto)
                                .toList();

                        // Convert source to "latitude,longitude" format
                        String source = orderDto.getPickupLocation().getLat() + "," + orderDto.getPickupLocation().getLng();

                        // Convert destinations to "latitude,longitude" format
                        List<String> destinations = purchaseOrderDtosForRoute.stream()
                                .map(po -> po.getDropLocation().getLat() + "," + po.getDropLocation().getLng())
                                .collect(Collectors.toList());

                        // Fetch the optimized route
                        RouteResult routeResult = googleDirectionsCalculator.getOptimizedRoute(source, destinations);

                        // print route result
                        for (Integer waypointOrder : routeResult.waypointOrder) {
                            System.out.println("Waypoint Order: " + waypointOrder);
                        }

                        // Distance & Paths
                        route.setPath(routeResult.encodedPolyline);
                        route.setDistanceKm(BigDecimal.valueOf(routeResult.totalDistance));
                        route.setActualDistanceKm(BigDecimal.valueOf(routeResult.totalDistance));

                        // Cost Calculation
                        BigDecimal totalDistance = route.getDistanceKm(); // Distance in km
                        int vehicleTypeId = vehicleType.getId(); // Medium-Duty vehicle
                        BigDecimal dieselPricePerLiter = new BigDecimal("350"); // Placeholder for diesel price

                        BigDecimal totalCost = calculateRouteCost(totalDistance, vehicleTypeId, dieselPricePerLiter);
                        System.out.println("Total Route Cost: " + totalCost + " LKR");

                        route.setCost(totalCost);  // Adjust accordingly
                        route.setEstdCost(totalCost);  // Adjust accordingly

                        //calculate 10% of total cost for profit
                        BigDecimal profit = totalCost.multiply(BigDecimal.valueOf(0.10));
                        route.setProfit(profit);

                        route = routeRepository.save(route);

                        Map<Integer, Integer> poToSequenceMap = new HashMap<>();
                        for (int i = 0; i < routeResult.waypointOrder.size(); i++) {
                            int waypointIndex = routeResult.waypointOrder.get(i);
                            PurchaseOrder po = purchaseOrdersListForRoute.get(waypointIndex);
                            System.out.println("Waypoint Index: " + waypointIndex + " -- PO: " + po.getId());
                            poToSequenceMap.put(po.getId(), i + 1);
                        }

                        for (Item item : vehicleAssignment.items) {
                            PurchaseOrder po = item.getPoId();
                            Integer sequenceNumber = poToSequenceMap.get(po.getId());
                            item.setSequenceNumber(sequenceNumber);
                            item.setRouteId(route);
                            itemRepository.save(item);
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

    //////////////////////////GOOGLE API//////////////////////////////////////////////

    // Google Distance Calculator

    @Component
    public class GoogleDistanceCalculator {

        @Value("${google.api.key}")
        private String apiKey;

        private GeoApiContext geoApiContext;

        @PostConstruct
        private void initializeGeoApiContext() {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
        }

        /**
         * Uses Google Maps Distance Matrix API to calculate a distance matrix.
         *
         * @param locations List of locations in "latitude,longitude" format.
         * @return 2D array of distances in meters.
         */
        public int[][] calculateDistanceMatrix(List<String> locations) {
            try {
                DistanceMatrix result = DistanceMatrixApi.getDistanceMatrix(
                        geoApiContext,
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


    @Component
    public class GoogleDirectionsCalculator {

        @Value("${google.api.key}")
        private String apiKey;

        private GeoApiContext geoApiContext;

        @PostConstruct
        private void initializeGeoApiContext() {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
        }

        /**
         * Finds the optimal route using Google Maps Directions API.
         *
         * @param source      The source location in "latitude,longitude" format.
         * @param destinations List of destination locations in "latitude,longitude" format.
         * @return The optimal route and distance.
         */
        public RouteResult getOptimizedRoute(String source, List<String> destinations) {
            try {
                // Convert destinations to waypoints
                String[] waypoints = destinations.toArray(new String[0]);

                DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                        .origin(source)
                        .destination(source) // Round trip to start location
                        .waypoints(waypoints)
                        .optimizeWaypoints(true) // Enable optimization
                        .mode(TravelMode.DRIVING) // Change to WALKING or BICYCLING if needed
                        .await();

                if (result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0]; // Optimal route

                    // Extract the encoded polyline
                    String encodedPolyline = route.overviewPolyline.getEncodedPath();

                    // Calculate total distance from all legs
                    double totalDistance = Arrays.stream(route.legs)
                            .mapToDouble(leg -> leg.distance.inMeters / 1000.0) // Convert meters to km
                            .sum();

                    // Extract waypoint order
                    List<Integer> waypointOrder = Arrays.stream(route.waypointOrder).boxed().toList();

                    return new RouteResult(encodedPolyline, totalDistance, waypointOrder);
                }

                throw new RuntimeException("No routes found in Google Maps response.");

            } catch (Exception e) {
                throw new RuntimeException("Error while fetching optimized route from Google Maps API", e);
            }
        }
    }

}
