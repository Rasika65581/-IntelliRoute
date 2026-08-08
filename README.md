# IntelliRoute 🚚

## Smart Logistics Decision System using Java & DSA

IntelliRoute is a **console-based logistics decision system** that uses data structures and algorithms to make delivery decisions on a simulated Pune road network.

Instead of using DSA algorithms as isolated examples, IntelliRoute combines them into one delivery workflow:

```text
Delivery Request
       ↓
Check Warehouse Reachability
       ↓
BFS
       ↓
Find Shortest Route
       ↓
Dijkstra
       ↓
Select Best Warehouse
       ↓
PriorityQueue
       ↓
Assign Best Available Driver
```

The project is intentionally console-based so that the **DSA, algorithmic decisions, and optimization techniques remain the main focus**.

---

# 🎯 Problem Statement

Consider a logistics company operating across a city.

A delivery request arrives for a particular destination.

The system needs to determine:

1. Which warehouses can actually reach the destination?
2. Which reachable warehouse can deliver it using the shortest route?
3. Which driver should handle the delivery?
4. What happens if some roads are temporarily blocked?

Instead of solving each problem independently, IntelliRoute combines multiple DSA techniques into one decision-making system.

---

# 🏗️ System Architecture

The system has several responsibilities working together:

```text
                         INTELLIROUTE
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
  ROAD NETWORK          ROUTE PLANNING       DELIVERY PLANNING
        │                     │                     │
        │                ┌────┴────┐          ┌─────┴─────┐
        │                │         │          │           │
        ▼                ▼         ▼          ▼           ▼
   Pune Graph          BFS    Dijkstra    Warehouse    Driver
   + Road Status                              Selection   Scheduling
                                                        │
                                                        ▼
                                                  PriorityQueue
```

### Main Components

### 1. Road Network

Represents the Pune city road network as a weighted graph.

It stores:

* Locations
* Roads
* Road distances
* Open/blocked road status

### 2. Route Planning

Two algorithms are used for different purposes:

* **BFS** → checks whether a destination is reachable
* **Dijkstra** → calculates the shortest weighted route

### 3. Warehouse Selection

The system evaluates multiple warehouses and selects the **closest reachable warehouse**.

### 4. Driver Scheduling

Available drivers are maintained using a **PriorityQueue** based on their current simulated availability time.

### 5. Dynamic Road Network

Roads can be temporarily blocked.

The road is not deleted from the graph. Its status is simply changed to blocked.

This allows the road to be restored later.

---

# 🔄 End-to-End Workflow

When a delivery request is created:

```text
                    Delivery Request
                           │
                           ▼
                     Destination
                           │
                           ▼
                  Check all warehouses
                           │
                           ▼
                         BFS
                           │
                    Is it reachable?
                     /             \
                   NO               YES
                   │                 │
                 Skip                ▼
                              Run Dijkstra
                                    │
                                    ▼
                            Calculate distance
                                    │
                                    ▼
                         Compare warehouses
                                    │
                                    ▼
                           Best warehouse
                                    │
                                    ▼
                         Driver PriorityQueue
                                    │
                                    ▼
                       Driver with least workload
                                    │
                                    ▼
                           Assign delivery
```

Each algorithm has a specific responsibility rather than being used only for demonstration.

---

# 🗺️ Pune Road Network

The system creates a predefined Pune road network automatically.

Example locations include:

```text
Baner
Aundh
Wakad
Hinjewadi
Shivajinagar
Kharadi
Viman Nagar
```

Example connections:

```text
Baner ─── Aundh ─── Shivajinagar
  │
  │
Wakad ─── Hinjewadi

Shivajinagar ─── Kharadi
       │
       │
    Viman Nagar
```

Each road has a distance/weight.

For example:

```text
Baner → Aundh = 5 km
Aundh → Shivajinagar = 7 km
Baner → Wakad = 6 km
```

The graph is represented using an **adjacency list**.

Conceptually:

```text
Location → Connected Roads
```

---

# ⚡ HashMap for Fast Lookups

HashMaps are an important part of the implementation.

Locations have names such as:

```text
Baner
Aundh
Wakad
Kharadi
```

The system needs to quickly find the neighbours of a particular location.

Conceptually:

```text
Baner → [Aundh, Wakad]
Aundh → [Baner, Shivajinagar]
```

A HashMap allows direct average **O(1)** lookup.

HashMaps are also useful during graph algorithms for storing information such as:

```text
Location → Distance
Location → Parent
Location → Neighbours
```

This avoids repeatedly scanning lists to find information.

---

# 🔎 BFS — Reachability Check

Before calculating a shortest route, IntelliRoute first asks:

> **Can this warehouse reach the destination at all?**

BFS explores the graph level by level.

```text
Warehouse
    ↓
Neighbours
    ↓
Neighbours of neighbours
    ↓
...
    ↓
Destination
```

If the destination cannot be reached, the warehouse is skipped.

### Why BFS?

At this stage we only need to know:

```text
Reachable?
```

We don't care about the exact distance.

BFS is therefore used as a simple connectivity check.

### Important Trade-off

BFS is not necessarily faster than Dijkstra for every case.

If every warehouse is reachable, BFS introduces additional work.

Its main value is when the graph contains disconnected regions, especially after road closures.

---

# 🛣️ Dijkstra — Shortest Route

For warehouses that pass the BFS reachability check, IntelliRoute runs Dijkstra's algorithm.

Dijkstra answers:

> **What is the shortest weighted route from this warehouse to the destination?**

This is important because different roads have different distances.

For example:

```text
Route A:

2 roads × 10 km
= 20 km


Route B:

4 roads × 3 km
= 12 km
```

A simple BFS shortest path would prefer Route A because it has fewer edges.

Dijkstra correctly prefers Route B because:

```text
12 km < 20 km
```

A PriorityQueue is used inside Dijkstra to process the location with the smallest currently known distance.

---

# 🏭 Best Warehouse Selection

The system does not simply choose the first warehouse.

It evaluates every warehouse.

Example:

```text
Warehouse: Baner
       ↓
      BFS
       ↓
   Reachable
       ↓
   Dijkstra
       ↓
    18 km


Warehouse: Hinjewadi
       ↓
      BFS
       ↓
   Reachable
       ↓
   Dijkstra
       ↓
    14 km


Warehouse: Shivajinagar
       ↓
      BFS
       ↓
   Reachable
       ↓
   Dijkstra
       ↓
     8 km
```

The system selects:

```text
Best Warehouse = Shivajinagar
Distance = 8 km
```

### Decision Rule

```text
Choose the reachable warehouse
with the minimum shortest-path distance.
```

---

# 👨‍✈️ Driver Scheduling

After selecting the warehouse, the system needs to assign a driver.

Drivers are maintained using:

```java
PriorityQueue<Driver>
```

Each driver has a simulated:

```text
availableTime
```

This represents how much current workload the driver has before becoming available again.

Example:

```text
Driver A → 20
Driver B → 5
Driver C → 12
```

The PriorityQueue selects:

```text
Driver B
```

because:

```text
5 < 12 < 20
```

After assigning a delivery, the driver's availability time is updated.

### Why PriorityQueue?

Without a PriorityQueue, we would need to repeatedly scan all drivers.

With a PriorityQueue, the driver with the smallest current workload remains at the top.

Approximate operations:

```text
Insert → O(log D)
Remove → O(log D)
```

where `D` is the number of drivers.

---

# 🚧 Dynamic Road Closures

Roads can be temporarily blocked.

Instead of deleting a road:

```java
remove(edge);
```

the system changes its status:

```java
edge.blocked = true;
```

BFS and Dijkstra ignore blocked roads.

### Why keep the road?

A blocked road may reopen later.

Therefore:

```text
Road
 │
 ├── OPEN
 │
 └── BLOCKED
```

Restoring it simply changes the status back:

```java
edge.blocked = false;
```

### Benefits

* Easy restoration
* Original graph structure remains intact
* Temporary closures are easy to represent
* No need to rebuild the graph
* Historical road information is preserved during the program run

---

# 🚧 Multiple Road Closures

The system allows multiple roads to be blocked.

For example:

```text
Baner - Aundh
Baner - Wakad
Baner - Hinjewadi
```

After these changes, BFS may determine that a destination is unreachable.

```text
Warehouse
    ↓
   BFS
    ↓
No path
    ↓
Warehouse skipped
```

This allows the system to adapt its warehouse selection to the current state of the road network.

---

# ⏳ Lazy Route Calculation

The system does not continuously recompute every possible route whenever the graph changes.

Instead:

```text
Road blocked
     ↓
Update road status
     ↓
Wait for delivery request
     ↓
Run BFS
     ↓
Run Dijkstra if necessary
```

This is a form of **lazy computation**.

The system performs route calculations when they are actually required.

This avoids unnecessary computation when there are no active delivery requests.

---

# 🔗 Shared Road Network

The Pune graph is created once and reused.

Conceptually:

```text
                    Pune Graph
                        │
          ┌─────────────┼─────────────┐
          │             │             │
          ▼             ▼             ▼
      Manager 1     Manager 2     Manager 3
```

If one manager blocks:

```text
Baner ↔ Wakad
```

the shared graph reflects:

```text
Baner ↔ Wakad = BLOCKED
```

Other managers therefore see the same updated road network.

This provides a **single source of truth** for the city map.

---

# 🧩 Data Structures Used

| Data Structure             | Purpose                              |
| -------------------------- | ------------------------------------ |
| **Graph / Adjacency List** | Represents the Pune road network     |
| **HashMap**                | Fast location and graph lookups      |
| **HashSet**                | Tracks visited locations in BFS      |
| **Queue**                  | BFS traversal                        |
| **PriorityQueue**          | Dijkstra's minimum-distance node     |
| **PriorityQueue**          | Driver scheduling                    |
| **ArrayList**              | Stores roads, drivers and warehouses |

---

# 🧠 Why These Algorithms Together?

The project is not simply a collection of independent algorithms.

They form a complete decision pipeline:

```text
                 Delivery Problem
                       │
                       ▼
                  HashMap
              Fast graph lookup
                       │
                       ▼
                      BFS
               Reachability check
                       │
                       ▼
                   Dijkstra
               Shortest route
                       │
                       ▼
              Warehouse Selection
                       │
                       ▼
                PriorityQueue
                Driver Selection
                       │
                       ▼
                   Delivery
```

Each data structure and algorithm has a specific responsibility.

---

# 🏗️ Important Design Decisions

## 1. Why Adjacency List?

The city graph is relatively sparse.

A location connects to only some other locations rather than every location.

Therefore an adjacency list is more memory-efficient.

Space complexity:

```text
O(V + E)
```

instead of an adjacency matrix:

```text
O(V²)
```

---

## 2. Why HashMap?

Locations are represented by names.

A HashMap provides average:

```text
O(1)
```

lookup.

This makes it convenient to retrieve neighbours, distances and other information associated with a location.

---

## 3. Why BFS Before Dijkstra?

BFS determines whether a destination is reachable.

If a warehouse cannot reach the destination:

```text
Skip warehouse
```

and Dijkstra is not required for that warehouse.

### Trade-off

If almost every warehouse is reachable, BFS adds extra computation.

Therefore, the purpose of BFS is primarily **connectivity validation**, not claiming that it always makes the system faster.

---

## 4. Why Dijkstra?

Roads have different weights.

BFS only minimizes the number of edges.

Dijkstra minimizes the **total weighted distance**.

Because all road distances are non-negative, Dijkstra is appropriate.

---

## 5. Why PriorityQueue for Drivers?

Drivers have different current workloads.

Instead of assigning the first available driver, the system prioritizes the driver with the smallest availability time.

This provides more balanced scheduling.

---

## 6. Why Block Roads Instead of Deleting Them?

Deleting a road would permanently modify the graph structure.

Keeping the road and changing its status allows:

```text
OPEN → BLOCKED → OPEN
```

without rebuilding the graph.

---

## 7. Why a Shared Graph?

The graph represents the city's current road network.

Having one shared graph prevents different delivery managers from working with inconsistent versions of the city map.

---

# ⚠️ Edge Cases

The system considers several edge cases:

* Destination does not exist
* Warehouse does not exist
* No route is available
* All warehouses are unreachable
* No available drivers
* Road does not exist
* Road is already blocked
* Multiple roads are blocked
* Road is restored
* Source and destination are the same
* Negative distance input
* Empty road network
* Invalid delivery request

---

# ⏱️ Complexity Analysis

Let:

```text
V = number of locations
E = number of roads
D = number of drivers
```

| Operation                   | Complexity           |
| --------------------------- | -------------------- |
| HashMap lookup              | **O(1) average**     |
| Graph storage               | **O(V + E)**         |
| BFS                         | **O(V + E)**         |
| Dijkstra with PriorityQueue | **O((V + E) log V)** |
| Driver insertion            | **O(log D)**         |
| Driver removal              | **O(log D)**         |

For the current Pune graph, these operations are easily fast enough.

---

# 💡 Optimization Considerations

One optimization considered for a larger system is **route caching**.

Frequently requested routes could be stored:

```text
Source + Destination
        ↓
      Cache
        ↓
   Existing Route?
      /      \
    YES      NO
     │        │
 Return     Dijkstra
 Route        │
              ▼
         Store Route
```

However, caching introduces another problem:

### Cache Invalidation

Suppose:

```text
A → B → C
```

is cached as the shortest route.

Then:

```text
B → C
```

gets blocked.

The cached route is no longer valid.

Therefore, a production implementation would need to invalidate affected cached routes when road status changes.

This is an example of an important engineering trade-off:

> **Caching improves speed but makes maintaining correct data more difficult.**

---

# 🚀 Future Scope

The current implementation intentionally focuses on DSA.

Possible extensions include:

### Live Traffic

Road weights could change according to traffic conditions.

```text
Normal traffic → 5 km cost
Heavy traffic  → higher effective cost
```

### Vehicle Capacity

Drivers could have maximum package capacities.

### Delivery Time Windows

Example:

```text
Delivery must arrive before 5 PM
```

### Multiple Deliveries

A driver could handle several packages in one trip.

This would introduce more complex route optimization.

### Electric Vehicles

Drivers could have battery constraints.

### Warehouse Inventory

The system could select only warehouses that actually contain the required product.

### Database Persistence

Roads, drivers and deliveries could be stored in a database.

### Real Maps

The predefined Pune graph could eventually be replaced with real map data through an external mapping API.

### Automatic Rerouting

If a road closes while a driver is already travelling, the system could recalculate the driver's remaining route.

---

# 🧪 Example Scenario

Suppose the destination is:

```text
Kharadi
```

Available warehouses:

```text
Baner
Hinjewadi
Shivajinagar
```

The system evaluates them:

```text
Baner
 ↓
BFS
 ↓
Reachable
 ↓
Dijkstra
 ↓
18 km


Hinjewadi
 ↓
BFS
 ↓
Reachable
 ↓
Dijkstra
 ↓
14 km


Shivajinagar
 ↓
BFS
 ↓
Reachable
 ↓
Dijkstra
 ↓
8 km
```

Therefore:

```text
Best Warehouse
       ↓
Shivajinagar

Shortest Distance
       ↓
8 km
```

The driver PriorityQueue then selects the driver with the lowest current workload.

---

# 🛠️ Technologies

* **Java**
* **Java Collections Framework**
* **Graph Algorithms**
* **BFS**
* **Dijkstra**
* **HashMap**
* **HashSet**
* **Queue**
* **PriorityQueue**
* **Object-Oriented Programming**

No database or external API is required.

---

# 📁 Project Structure

```text
IntelliRoute/
│
├── src/
│   └── IntelliRoute.java
│
├── README.md
│
└── .gitignore
```

The implementation is intentionally kept in a **single Java file** so that the DSA logic remains easy to understand and explain during technical interviews.

---

# ▶️ Running the Project

## Eclipse

1. Create/import the Java project.
2. Open `IntelliRoute.java`.
3. Run the `main()` method.
4. Follow the console instructions.

## Terminal

Compile:

```bash
javac IntelliRoute.java
```

Run:

```bash
java IntelliRoute
```

---

# 🎯 Project Scope

IntelliRoute is intentionally a **DSA-focused console application**, not a production logistics platform.

The primary goal is to demonstrate how multiple data structures and algorithms can work together to solve a realistic problem.

The core system is:

```text
Graph
   +
HashMap
   +
BFS
   +
Dijkstra
   +
PriorityQueue
   +
Dynamic Road Network
   +
Warehouse Selection
   +
Driver Scheduling
```

The project prioritizes **clarity, algorithmic reasoning, and explainability** over unnecessary application complexity.

---

# 👩‍💻 Author

**Rasika Joshi**

Computer Engineering Student
DSA-focused Java project for technical interview preparation.

---

## ⭐ Key Takeaway

The main idea behind IntelliRoute is:

> **Don't just find a shortest path. Use algorithms to make a sequence of delivery decisions.**

```text
Can I reach the destination?
          ↓
         BFS

What is the shortest route?
          ↓
       Dijkstra

Which warehouse should I use?
          ↓
   Minimum distance

Which driver should handle it?
          ↓
     PriorityQueue
```
