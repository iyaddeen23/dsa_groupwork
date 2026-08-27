# 🎤 Comprehensive Presentation & Viva Guide
## Ghana Smart Service Operations Optimizer — Campus Service Hub

---

## 📌 1. Project Overview & Architecture

### What is this project about?
The **Ghana Smart Service Operations Optimizer** is an operational logistics and resource management system designed for the **University of Ghana, Legon campus**. It solves real-world campus service problems—such as dispatching emergency medical teams, security officers, maintenance riders, and shuttle vans across 50 campus locations connected by 110 road networks.

### Dual Application Interfaces:
1. **CLI Mode (Command Line Interface)**: An interactive terminal-driven application. Best for live technical presentations because it explicitly displays data structure properties (tree heights, search times, heap extraction order).
   ```bash
   java -jar target/campus-service-hub-jar-with-dependencies.jar --console
   ```
2. **GUI Mode (Graphical User Interface)**: A visual Java Swing application providing interactive dashboards, graph visualisations, search/sort tools, and real-time dispatch management.
   ```bash
   java -jar target/campus-service-hub-jar-with-dependencies.jar
   ```

---

## 🔑 2. Understanding Entity IDs & Input Variables

When presenting, you will enter specific IDs into the console. Here is what every ID prefix means:

| ID Format | Represents | Examples | Purpose in Demo |
|:---|:---|:---|:---|
| `L001` – `L050` | **Campus Location ID** | `L001` (Balme Library), `L002` (Dept of CS), `L050` (Central Mosque) | Used to test Hash Table, AVL Tree, BFS, DFS, Dijkstra, and MST graph algorithms. |
| `V001` – `V030` | **Resource / Vehicle ID** | `V001` (Security Officer), `V002` (Maintenance Rider), `V009` (Shuttle Van) | Used to test B-Tree indexing and Greedy resource dispatching. |
| `Q001` – `Q300` | **Service Request ID** | `Q001` (IT Support request), `Q006` (Urgent Medical request) | Used to test BST lookup, Queue order, Heap dispatch, and Knapsack optimization. |

### 🔍 "What happens if I change the variables/inputs?"
- **Valid Existing ID (e.g. `L001` or `Q001`):** The system searches the underlying data structure and returns the full object record along with structural metadata (e.g., node height, page height).
- **Non-Existent ID (e.g. `L999` or `Q999`):** The system safely catches missing keys and outputs `"Not found."` or `null` without throwing a runtime exception or crashing.
- **Changing Location Names (e.g. `Balme Library` vs `Commonwealth Hall`):** Demonstrates search matching; searching `Balme Library` matches `L001`, while searching an invalid name returns `"Not found."`.

---

## 🧠 3. Data Structures & Algorithms — Quick Explanation & Differences

Every data structure in this codebase was **built from scratch** without using `java.util` collections (`ArrayList`, `HashMap`, `TreeMap`, etc.).

### A. Search & Indexing Structures
- **Hash Table vs Hash Set:**
  - *Hash Table*: Stores Key-Value pairs ($O(1)$ average lookup via hash code). Used for `locationById`.
  - *Hash Set*: Stores unique keys only. Used for tracking visited nodes in graph traversals.
- **AVL Tree vs Unbalanced Binary Search Tree (BST):**
  - *BST (Binary Search Tree)*: Stores elements where Left < Root < Right. **Problem:** If data is inserted in sorted order, it degenerates into a flat line (height = 299 for 300 items, $O(n)$ search).
  - *AVL Tree*: A **self-balancing** BST. Uses tree rotations to keep heights balanced ($\le 1.44 \log_2 n$). For 50 locations, height stays strictly at **6**.
  - *Key Difference:* BST height can grow out of control ($O(n)$ worst case), whereas AVL guarantees $O(\log n)$ worst-case search.
- **B-Tree:** A multi-way self-balancing search tree optimized for page-based storage and block reads. Keeps height extremely low (height = **2** for 30 resources).

### B. Queues & Buffers
- **FIFO Queue:** Standard linear queue (First-In, First-Out). Elements are dequeued in exact arrival order.
- **Circular Queue:** A fixed-capacity ring buffer using `front` and `rear` pointers with modulo arithmetic (`(rear + 1) % capacity`). **Advantage:** Reuses array slots without shifting memory.
- **Deque (Double-Ended Queue):** Allows $O(1)$ insertion at both front and rear. **Usage:** Standard requests go to the rear (`addRear`), but emergency requests jump straight to the front (`addFront`).

### C. Priority & Graph Structures
- **Binary Heap (Priority Queue):** A complete binary tree represented as an array. Max-heap property ensures the root is always the highest-urgency request ($O(1)$ peek, $O(\log n)$ extraction).
- **Graph (Adjacency List):** Represents campus map as 50 vertices (locations) and 110 weighted edges (roads with distances/travel times).

---

## 🎬 4. Step-by-Step Presentation Script & Demo Workflow

---

### 🟢 Scene 1: System Initialisation & Data Verification
- **Goal:** Prove the system auto-initialises SQLite schema and seeds realistic data.
- **How to execute:**
  1. Start CLI: `java -jar target/campus-service-hub-jar-with-dependencies.jar --console`
  2. Enter option `1` (Data & Database), then `1` (Reload structures).
- **Console Input:** `1` ↵ $\rightarrow$ `1` ↵
- **Expected Output:**
  ```text
  Reloaded: 50 locations, 110 roads, 30 resources, 300 requests.
  ```
  - 📝 **Output Explanation:** Confirms that 50 location nodes, 110 road edges, 30 operational resources, and 300 service requests were successfully loaded from the SQLite database into memory data structures.
- **What to say:** *"On first launch, the system automatically builds our SQLite schema and populates 50 Legon locations, 110 connecting roads, 30 resources, and 300 service requests derived deterministically from our team random seed."*

---

### 🟢 Scene 2: Custom Data Structures Demo

#### 2.1 Hash Table Lookup — $O(1)$ Direct Access
- **Concept:** Instant lookup by location ID.
- **Console Input:** `2` ↵ $\rightarrow$ `1` ↵ $\rightarrow$ `L001` ↵
- **Expected Output:**
  ```text
  Location{L001, Balme Library, Volta Close/Library, (5.608,0.183)}
  ```
  - 📝 **Output Explanation:** Displays the location's ID (`L001`), official name (`Balme Library`), campus zone & type (`Volta Close/Library`), and GPS coordinates `(latitude 5.608, longitude 0.183)`.
- **What if variable is changed?** Entering `L005` returns `Volta Hall`. Entering `L999` outputs `"Not found."`.
- **What to say:** *"Our custom Hash Table uses separate chaining for collision resolution, retrieving location L001 (Balme Library) in constant O(1) time."*

#### 2.2 AVL Tree vs BST Height Comparison
- **Concept:** Demonstrating why self-balancing is crucial.
- **Step A (AVL Tree):** `2` ↵ $\rightarrow$ `2` ↵ $\rightarrow$ `Balme Library` ↵
  - **Output:**
    ```text
    Location{L001, Balme Library, Volta Close/Library, (5.608,0.183)}
    AVL tree height: 6 (n=50, theoretical min ~6)
    ```
    - 📝 **Output Explanation:** `n=50` is the total number of locations; `height: 6` shows the AVL tree balanced itself so searching any location takes at most 6 steps ($\approx \log_2 50$).
- **Step B (Unbalanced BST):** `2` ↵ $\rightarrow$ `4` ↵ $\rightarrow$ `Q001` ↵
  - **Output:**
    ```text
    ServiceRequest{Q001, L046->L026, ITSupport, urgency=2, 2026-07-14T12:26...}
    BST height: 299 (n=300) - unbalanced, so this can be far from log2(n)
    ```
    - 📝 **Output Explanation:** `n=300` is the total number of requests; `height: 299` reveals that sequential insertion degraded the tree into a single line (height $= n-1$), requiring up to 299 comparison steps.
- **What to say:** *"Notice the dramatic contrast: the self-balancing AVL tree maintains a compact height of only 6 for 50 locations (logarithmic O(log n)), while the standard BST degenerates to height 299 for 300 elements because items were inserted sequentially."*

#### 2.3 B-Tree Page Indexing
- **Concept:** Disk-friendly index structure.
- **Console Input:** `2` ↵ $\rightarrow$ `3` ↵ $\rightarrow$ `V001` ↵
- **Expected Output:**
  ```text
  Resource{V001, SecurityOfficer, home=L027, cap=6, AVAILABLE}
  B-tree height (pages): 2
  ```
  - 📝 **Output Explanation:** Returns resource `V001` (Security Officer based at `L027`, capacity 6, status `AVAILABLE`) and shows `height (pages): 2`, meaning any resource record is found in just 2 page reads.
- **What to say:** *"B-Tree indexing keeps page height down to 2, minimizing I/O read operations for resource lookup."*

#### 2.4 Deque Emergency Insertion & Binary Heap Dispatch
- **Step A (Deque):** `2` ↵ $\rightarrow$ `7` ↵
  - **Output:**
    ```text
      normal -> addRear: Q001
      normal -> addRear: Q002
      URGENT -> addFront: Q006
    Deque front-to-rear order now has urgent requests first: front=ServiceRequest{Q006...}
    ```
    - 📝 **Output Explanation:** Normal requests (`Q001`–`Q005`) are appended to the rear of the deque, but urgent request `Q006` (urgency 4) is prepended to the front, placing `Q006` first in line.
  - **What to say:** *"Our Deque allows high-urgency incoming service calls to jump to the front of the queue in O(1) time."*
- **Step B (Priority Heap):** `2` ↵ $\rightarrow$ `8` ↵
  - **Output:**
    ```text
    Dispatch heap size=107. Extracting top 5 (most urgent first):
      ServiceRequest{Q257, ..., urgency=5, ...}
      ServiceRequest{Q060, ..., urgency=5, ...}
      ServiceRequest{Q226, ..., urgency=5, ...}
      ServiceRequest{Q207, ..., urgency=4, ...}
      ServiceRequest{Q116, ..., urgency=4, ...}
    ```
    - 📝 **Output Explanation:** Out of 107 pending dispatch items, the max-heap extracts requests in strict descending order of urgency (all urgency 5 requests first, followed by urgency 4).
  - **What to say:** *"The Binary Max-Heap extracts requests in strict order of urgency level 5 first, regardless of when they were submitted."*

---

### 🟢 Scene 3: Search & Sort Lab

#### 3.1 Linear vs Binary Search
- **Console Input:** `3` ↵ $\rightarrow$ `1` ↵ $\rightarrow$ `Balme Library` ↵
- **Expected Output:**
  ```text
  Linear search: index=0, 1.545 us
  Binary search (on pre-sorted array): index=4, 0.365 us
  ```
  - 📝 **Output Explanation:** `index` is the array position where target was found; `us` means microseconds ($\mu s$). Binary search found the element in 0.365 microseconds—more than $4\times$ faster than linear search (1.545 $\mu s$).
- **What to say:** *"Binary search cuts execution time to ~0.37 microseconds on a sorted array, achieving logarithmic O(log n) performance compared to linear scanning."*

#### 3.2 Sorting Algorithms (Selection, Insertion, Merge, QuickSort)
- **Console Input:** `3` ↵ $\rightarrow$ `2` ↵ $\rightarrow$ `3` ↵ (Merge Sort)
- **Expected Output:**
  ```text
  Sorted 300 requests in 0.306 ms. Top 5 by urgency:
    ServiceRequest{Q009, ..., urgency=5, ...}
    ServiceRequest{Q023, ..., urgency=5, ...}
  ```
  - 📝 **Output Explanation:** `300 requests` is the dataset size; `0.306 ms` is the total execution time in milliseconds; `Top 5 by urgency` shows the requests sorted in descending urgency order.
- **What to say:** *"Merge Sort sorts all 300 requests by urgency in just 0.3ms ($O(n \log n)$ divide-and-conquer), outperforming quadratic $O(n^2)$ selection sort."*

---

### 🟢 Scene 4: Graph Engine — Routing & Minimum Spanning Trees

#### 4.1 Dijkstra Shortest Route Navigation
- **Concept:** Find quickest path between two locations on campus.
- **Console Input:** `4` ↵ $\rightarrow$ `3` ↵ $\rightarrow$ `L001` ↵ $\rightarrow$ `L050` ↵
- **Expected Output:**
  ```text
  Shortest route cost: 13.577
  Path: [L001, L003, L050]
  ```
  - 📝 **Output Explanation:** `Shortest route cost: 13.577` is the total weighted road distance/time; `Path: [L001, L003, L050]` gives the exact route starting from Balme Library (`L001`), passing through Legon Hospital (`L003`), and arriving at Central Mosque (`L050`).
- **What if variable is changed?** Changing origin/destination (e.g., `L001` to `L025`) computes a new shortest path.
- **What to say:** *"Dijkstra's algorithm evaluates road edge weights and finds the optimal path from Balme Library (L001) to Central Mosque (L050) via L003 with total route weight 13.577."*

#### 4.2 Prim vs Kruskal MST Equivalence
- **Prim Input:** `4` ↵ $\rightarrow$ `4` ↵ $\rightarrow$ `L001` ↵
  - **Output:** `MST edges (49)`, `Total network cost: 219.676`
- **Kruskal Input:** `4` ↵ $\rightarrow$ `5` ↵
  - **Output:** `MST edges (49)`, `Total network cost: 219.676`
  - 📝 **Output Explanation:** `49` is the minimum number of edges needed to connect all 50 locations without cycles ($V - 1 = 49$); `219.676` is the total road distance required to link the entire campus.
- **What to say:** *"Both Prim's (greedy vertex-growing) and Kruskal's (disjoint-set edge sorting) algorithms independently construct the exact same minimum spanning tree across all 49 connecting edges with identical cost 219.676."*

---

### 🟢 Scene 5: Optimisation Engine & Audit Trail

#### 5.1 Greedy Resource Assignment
- **Console Input:** `5` ↵ $\rightarrow$ `1` ↵
- **Expected Output:**
  ```text
  Assigned 15 of 72 pending requests:
    Q226 -> V001
    ...
  (30 audit events recorded — see menu 6 to inspect/undo.)
  ```
  - 📝 **Output Explanation:** Out of 72 pending requests, 15 available resources were assigned (`Q226` to `V001`); `30 audit events` means 2 state changes were logged per assignment (1 for resource `AVAILABLE` $\rightarrow$ `BUSY` and 1 for request `NEW` $\rightarrow$ `ASSIGNED`).
- **What to say:** *"The greedy optimizer matches available resources to pending service requests and records undoable audit log entries."*

#### 5.2 Greedy vs Dynamic Programming (Knapsack) Counterexample
- **Console Input:** `5` ↵ $\rightarrow$ `2` ↵
- **Expected Output:**
  ```text
  Items: A(w10,v60) B(w20,v100) C(w30,v120), capacity=50
  Greedy (by value/weight ratio) picks: [Item A, Item B] total value=160
  DP (0/1 knapsack, optimal) picks:     [Item B, Item C] total value=220
  => Greedy is suboptimal here by 60 value points.
  ```
  - 📝 **Output Explanation:** Under capacity 50, Greedy chose items A & B by density (weight 10+20=30, value 60+100=160), whereas Dynamic Programming evaluated all subproblems and selected B & C (weight 20+30=50, value 100+120=220), gaining 60 more value points.
- **What to say:** *"This counterexample proves why local greedy heuristics fail for 0/1 knapsack problems: Greedy selects items A & B (value 160), whereas Dynamic Programming finds global optimum B & C (value 220, beating greedy by 60 points)."*

#### 5.3 Audit Log & State Undo
- **Console Input:** `6` ↵ $\rightarrow$ `1` ↵ (View stack) $\rightarrow$ `6` ↵ $\rightarrow$ `2` ↵ (Undo)
- **Expected Output:**
  ```text
  Stack size: 30
  Most recent: AuditEvent{30, ASSIGN_REQUEST on service_requests/Q139...}
  Undid: AuditEvent{30, ASSIGN_REQUEST on service_requests/Q139...}
  ```
  - 📝 **Output Explanation:** `Stack size: 30` shows 30 recorded undo steps; `Undid:` confirms the top audit event was popped from the stack and request `Q139`'s status was reverted back to `NEW` in the SQLite database.
- **What to say:** *"Our Audit Log acts as a LIFO stack tracking state changes. Invoking undo pops the stack and safely reverts DB entity records."*

---

## 📊 Summary Cheat Sheet for Presentation

| Command Pathway | Input Values | Meaning of Inputs | Expected Result | Output Explanation | Key Concept / Talking Point |
|:---|:---|:---|:---|:---|:---|
| `1` $\rightarrow$ `1` | None | Reload DB | 50 loc, 110 roads, 30 res, 300 req | Confirms SQLite records loaded into memory | Verifies DB schema and persistence |
| `2` $\rightarrow$ `1` | `L001` | Location ID (Balme Library) | Returns Balme Library object | Shows location name, category & GPS coords | Hash Table $O(1)$ constant lookup |
| `2` $\rightarrow$ `2` | `Balme Library` | Location Name | Height = `6` | Max 6 comparisons to find any location | AVL self-balancing tree height $\approx \log_2 n$ |
| `2` $\rightarrow$ `4` | `Q001` | Request ID | Height = `299` | Degenerated single-line tree of 300 items | Unbalanced BST degenerates to $O(n)$ line |
| `2` $\rightarrow$ `8` | None | Priority Dispatch | Top 5 urgency=5 requests | Extracts highest-urgency requests first | Max-Heap prioritizes high-urgency jobs |
| `3` $\rightarrow$ `1` | `Balme Library` | Target Name | Linear ~1.5$\mu s$ vs Binary ~0.3$\mu s$ | `us` = microseconds; Binary is $>4\times$ faster | Binary Search is logarithmic $O(\log n)$ |
| `4` $\rightarrow$ `3` | `L001` $\rightarrow$ `L050` | Origin $\rightarrow$ Destination | Path `[L001, L003, L050]`, cost `13.577` | Path via Hospital (`L003`) to Mosque (`L050`) | Dijkstra shortest path algorithm |
| `4` $\rightarrow$ `4` / `5`| `L001` | Start Location | MST Cost = `219.676` (49 edges) | 49 edges link 50 locations at min cost | Prim & Kruskal algorithm equivalence |
| `5` $\rightarrow$ `2` | None | Knapsack Budget | Greedy=160 vs DP=220 | DP picks items B+C (val 220) vs A+B (160) | DP achieves global optimum over greedy |
| `6` $\rightarrow$ `2` | None | Undo Action | Reverts last DB mutation | Pops stack & resets request status to `NEW` | LIFO Stack-based transaction undo |
