/*********************************************************************
 * IntelliRoute
 * Smart Logistics Route Optimizer
 *
 * Console-based Java application demonstrating:
 * - Graph (Adjacency List)
 * - Dijkstra's Shortest Path Algorithm
 * - Breadth First Search (BFS)
 * - Priority Queue for Driver Scheduling
 *
 * Author: Rasika Joshi
 *********************************************************************/
package intelliroute;
import java.util.*;
class Edge {
    String destination;
    int distance;
    boolean blocked;

    Edge(String destination, int distance) {
        this.destination = destination;
        this.distance = distance;
        this.blocked = false;
    }
}

class Driver {
    String name;
    int availableTime;

    Driver(String name) {
        this.name = name;
        this.availableTime = 0;
    }

    @Override
    public String toString() {
        return name + " (Available in " + availableTime + " mins)";
    }
}

class Warehouse {
    String name;
    String location;

    Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public String toString() {
        return name + " - " + location;
    }
}

class Graph {

    private HashMap<String, ArrayList<Edge>> adj;

    Graph() {
        adj = new HashMap<>();
        initializePuneMap();
    }

    // ---------------------- CREATE GRAPH ----------------------

    private void addRoad(String src, String dest, int dist) {

        adj.putIfAbsent(src, new ArrayList<>());
        adj.putIfAbsent(dest, new ArrayList<>());

        adj.get(src).add(new Edge(dest, dist));
        adj.get(dest).add(new Edge(src, dist));
    }

    private void initializePuneMap() {

        addRoad("Shivajinagar", "Deccan", 3);
        addRoad("Shivajinagar", "Aundh", 6);
        addRoad("Deccan", "Kothrud", 5);
        addRoad("Aundh", "Baner", 4);
        addRoad("Baner", "Hinjewadi", 7);
        addRoad("Baner", "Wakad", 5);
        addRoad("Wakad", "Pimpri", 6);
        addRoad("Pimpri", "Hinjewadi", 5);
        addRoad("Shivajinagar", "Swargate", 5);
        addRoad("Swargate", "Hadapsar", 8);
        addRoad("Hadapsar", "Kharadi", 7);
        addRoad("Shivajinagar", "Viman Nagar", 10);
        addRoad("Viman Nagar", "Kharadi", 4);
        addRoad("Aundh", "Kharadi", 12);

    }

    // ---------------------- DISPLAY GRAPH ----------------------

    public void displayRoadNetwork() {

        System.out.println("\n=========== ROAD NETWORK ===========");

        for (String city : adj.keySet()) {

            System.out.print(city + " -> ");

            for (Edge e : adj.get(city)) {

                if (!e.blocked)
                    System.out.print(e.destination + "(" + e.distance + "km)  ");

            }

            System.out.println();
        }
    }

    // ---------------------- BLOCK ROAD ----------------------

    public void blockRoad(String src, String dest) {

        boolean found = false;

        for (Edge e : adj.getOrDefault(src, new ArrayList<>())) {

            if (e.destination.equals(dest)) {
                e.blocked = true;
                found = true;
            }

        }

        for (Edge e : adj.getOrDefault(dest, new ArrayList<>())) {

            if (e.destination.equals(src))
                e.blocked = true;

        }

        if (found)
            System.out.println("Road Blocked Successfully.");
        else
            System.out.println("Road Not Found.");
    }

    // ---------------------- RESTORE ROAD ----------------------

    public void restoreRoad(String src, String dest) {

        boolean found = false;

        for (Edge e : adj.getOrDefault(src, new ArrayList<>())) {

            if (e.destination.equals(dest)) {
                e.blocked = false;
                found = true;
            }

        }

        for (Edge e : adj.getOrDefault(dest, new ArrayList<>())) {

            if (e.destination.equals(src))
                e.blocked = false;

        }

        if (found)
            System.out.println("Road Restored Successfully.");
        else
            System.out.println("Road Not Found.");
    }

 // ---------------------- CHECK REACHABILITY USING BFS ----------------------

    public boolean isReachable(String start, String end) {

        Queue<String> q = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();

        q.add(start);
        visited.add(start);

        while (!q.isEmpty()) {

            String current = q.poll();

            if (current.equals(end))
                return true;

            for (Edge e : adj.getOrDefault(current, new ArrayList<>())) {

                if (!e.blocked && !visited.contains(e.destination)) {

                    visited.add(e.destination);
                    q.add(e.destination);
                }
            }
        }

        return false;
    }
    
    // ---------------------- DIJKSTRA ----------------------

    public int shortestDistance(String source, String destination) {

        HashMap<String, Integer> distance = new HashMap<>();

        for (String city : adj.keySet())
            distance.put(city, Integer.MAX_VALUE);

        PriorityQueue<Node> pq = new PriorityQueue<>();

        distance.put(source, 0);

        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {

            Node current = pq.poll();

            if (current.city.equals(destination))
                break;

            for (Edge e : adj.get(current.city)) {

                if (e.blocked)
                    continue;

                int newDistance = current.distance + e.distance;

                if (newDistance < distance.get(e.destination)) {

                    distance.put(e.destination, newDistance);

                    pq.add(new Node(e.destination, newDistance));

                }

            }

        }

        return distance.get(destination);

    }

    // ---------------------- PATH ----------------------

    public List<String> getShortestPath(String source, String destination) {

        HashMap<String, Integer> distance = new HashMap<>();
        HashMap<String, String> parent = new HashMap<>();

        for (String city : adj.keySet())
            distance.put(city, Integer.MAX_VALUE);

        PriorityQueue<Node> pq = new PriorityQueue<>();

        distance.put(source, 0);

        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {

            Node current = pq.poll();

            for (Edge e : adj.get(current.city)) {

                if (e.blocked)
                    continue;

                int newDistance = current.distance + e.distance;

                if (newDistance < distance.get(e.destination)) {

                    distance.put(e.destination, newDistance);

                    parent.put(e.destination, current.city);

                    pq.add(new Node(e.destination, newDistance));

                }

            }

        }

        ArrayList<String> path = new ArrayList<>();

        if (distance.get(destination) == Integer.MAX_VALUE)
            return path;

        String curr = destination;

        while (curr != null) {

            path.add(0, curr);

            curr = parent.get(curr);

        }

        return path;

    }

    static class Node implements Comparable<Node> {

        String city;
        int distance;

        Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return this.distance - other.distance;
        }
    }

}

class DeliveryManager {

    private Graph city;
    private ArrayList<Warehouse> warehouses;
    private PriorityQueue<Driver> drivers;

    DeliveryManager(Graph city) {

        this.city = city;

        warehouses = new ArrayList<>();

        drivers = new PriorityQueue<>(
                (d1, d2) -> d1.availableTime - d2.availableTime);

        initializeWarehouses();
        initializeDrivers();
    }

    // -------------------- INITIAL DATA --------------------

    private void initializeWarehouses() {

        warehouses.add(new Warehouse("Warehouse A", "Hinjewadi"));
        warehouses.add(new Warehouse("Warehouse B", "Shivajinagar"));
        warehouses.add(new Warehouse("Warehouse C", "Kharadi"));

    }

    private void initializeDrivers() {

        drivers.add(new Driver("Rahul"));
        drivers.add(new Driver("Sneha"));
        drivers.add(new Driver("Aman"));
        drivers.add(new Driver("Priya"));

    }

    // -------------------- DISPLAY --------------------

    public void displayWarehouses() {

        System.out.println("\n------ Warehouses ------");

        for (Warehouse w : warehouses)
            System.out.println(w);

    }

    public void displayDrivers() {

        System.out.println("\n------ Drivers ------");

        for (Driver d : drivers)
            System.out.println(d);

    }

 // -------------------- BEST WAREHOUSE --------------------

    private Warehouse findNearestWarehouse(String destination) {

        Warehouse best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Warehouse w : warehouses) {

            // First check if destination is reachable
            if (!city.isReachable(w.location, destination))
                continue;

            // Run Dijkstra only for reachable warehouses
            int distance =
                    city.shortestDistance(w.location, destination);

            if (distance < minDistance) {

                minDistance = distance;
                best = w;
            }
        }

        return best;
    }

    // -------------------- PLACE DELIVERY --------------------

    public void placeDelivery(Scanner sc) {

        System.out.println("\nAvailable Destinations");

        System.out.println("Shivajinagar");
        System.out.println("Deccan");
        System.out.println("Kothrud");
        System.out.println("Aundh");
        System.out.println("Baner");
        System.out.println("Hinjewadi");
        System.out.println("Wakad");
        System.out.println("Pimpri");
        System.out.println("Swargate");
        System.out.println("Hadapsar");
        System.out.println("Kharadi");
        System.out.println("Viman Nagar");

        System.out.print("\nEnter Destination : ");
        String destination = sc.nextLine();

        Warehouse warehouse = findNearestWarehouse(destination);

        if (warehouse == null) {

            System.out.println("No warehouse can reach destination.");
            return;

        }

        Driver driver = drivers.poll();

        if (driver == null) {

            System.out.println("No drivers available.");
            return;

        }

        List<String> path =
                city.getShortestPath(warehouse.location, destination);

        int distance =
                city.shortestDistance(warehouse.location, destination);

        if (path.isEmpty()) {

            System.out.println("No route available.");

            drivers.add(driver);

            return;

        }

        System.out.println("\n========== DELIVERY DETAILS ==========");

        System.out.println("Warehouse : " + warehouse.name);

        System.out.println("Location  : " + warehouse.location);

        System.out.println("Driver    : " + driver.name);

        System.out.println("Distance  : " + distance + " km");

        System.out.println("Route     : " + path);

        driver.availableTime += distance;

        drivers.add(driver);

    }

    // -------------------- ROAD BLOCK --------------------

    public void blockRoad(Scanner sc) {

        System.out.print("Source : ");
        String src = sc.nextLine();

        System.out.print("Destination : ");
        String dest = sc.nextLine();

        city.blockRoad(src, dest);

    }

    // -------------------- RESTORE ROAD --------------------

    public void restoreRoad(Scanner sc) {

        System.out.print("Source : ");
        String src = sc.nextLine();

        System.out.print("Destination : ");
        String dest = sc.nextLine();

        city.restoreRoad(src, dest);

    }

    // -------------------- MENU --------------------

    public void start() {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n======================================");
            System.out.println("          INTELLIROUTE");
            System.out.println("======================================");
            System.out.println("1. View Warehouses");
            System.out.println("2. View Drivers");
            System.out.println("3. View Road Network");
            System.out.println("4. Place Delivery");
            System.out.println("5. Block Road");
            System.out.println("6. Restore Road");
            System.out.println("7. Exit");

            System.out.print("\nEnter Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    displayWarehouses();
                    break;

                case 2:
                    displayDrivers();
                    break;

                case 3:
                    city.displayRoadNetwork();
                    break;

                case 4:
                    placeDelivery(sc);
                    break;

                case 5:
                    blockRoad(sc);
                    break;

                case 6:
                    restoreRoad(sc);
                    break;

                case 7:
                    System.out.println("\nThank you for using IntelliRoute.");

                    return;

                default:

                    System.out.println("Invalid Choice.");

            }

        }

    }

}
//======================================================
//MAIN CLASS
//======================================================

public class IntelliRoute {

 public static void main(String[] args) {

     System.out.println("========================================");
     System.out.println("          INTELLIROUTE");
     System.out.println("   Smart Logistics Route Optimizer");
     System.out.println("========================================");

     System.out.println("\nLoading Pune City Map...");

     Graph city = new Graph();

     System.out.println("✓ Road Network Loaded");
     System.out.println("✓ Warehouses Loaded");
     System.out.println("✓ Drivers Loaded");

     DeliveryManager manager = new DeliveryManager(city);

     manager.start();
 }
}
