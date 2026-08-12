# QA Testing Checklist - Ghana Smart Service Operations Optimizer

**Project**: DCIT 204/308 Joint DSA Semester Project
**Repo**: https://github.com/iyaddeen23/dsa_groupwork
**QA Date**: 2026-08-10

---

## How to Build and Run

### 1. Prerequisites

- **JDK 17+** (`java -version`)
- **Maven 3.6+** (`mvn -v`)

> **Note**: The bundled `maven.zip` in the repo root is corrupted. Download Maven
> separately or install via `winget install --id Apache.Maven -e`.

### 2. Build the project

```bash
mvn -q package -DskipTests
```

This produces: `target/campus-service-hub-jar-with-dependencies.jar`

### 3. Run the application

```bash
java -jar target/campus-service-hub-jar-with-dependencies.jar
```

### 4. Run the test suite

```bash
mvn test
```

Expected: **105 tests, 0 failures, 0 errors**

### 5. Fresh start (reset database)

```bash
rm campus_hub.db
java -jar target/campus-service-hub-jar-with-dependencies.jar
```

---

## Checklist

### A. Environment and Build

| # | Item | Status | Notes |
|---|------|--------|-------|
| A1 | JDK 17+ is installed (`java -version`) | PASS | OpenJDK 17.0.20 Temurin |
| A2 | Maven 3.6+ is available | PASS | 3.9.9 extracted locally |
| A3 | `mvn -q package -DskipTests` completes without errors | PASS | Builds in ~20s |
| A4 | Fat jar `campus-service-hub-jar-with-dependencies.jar` is produced | PASS | 13.7 MB |
| A5 | `mvn test` - all 105 tests pass (0 failures, 0 errors) | PASS | 105 run, 0 fail, 0 error, 0 skip, 12.1s |

---

### B. First-Run Database Setup

| # | Item | Status | Notes |
|---|------|--------|-------|
| B1 | On first run, `campus_hub.db` is created automatically | PASS | "No existing database found...creating schema" |
| B2 | Schema has 6 tables: locations, roads, resources, service_requests, algorithm_runs, audit_events | PASS | Confirmed via schema.sql and app startup |
| B3 | Seed CSVs from `data/` are auto-imported | PASS | "importing seed CSVs from ./data" |
| B4 | Record counts match PRD minimums: 50 locations, 110 roads, 30 resources, 300 requests | PASS | Confirmed from startup output |
| B5 | Startup prints loaded counts confirming all data loaded | PASS | "Loaded 50 locations, 110 roads, 30 resources, 300 requests" |
| B6 | Subsequent runs reuse existing `campus_hub.db` without re-seeding | TODO | Manual verification needed |

---

### C. Console Menu - Option 1: Data and Database

| # | Item | Status | Notes |
|---|------|--------|-------|
| C1 | Option 1 then 1: Reload structures from database - prints updated counts | TODO | Manual: type `1` then `1` |
| C2 | Option 1 then 2: Re-import CSV seed files - loads all CSVs successfully | TODO | Manual: type `1` then `2` |

---

### D. Console Menu - Option 2: Data Structures Demo

| # | Item | Status | Notes |
|---|------|--------|-------|
| D1 | Hash Table lookup (enter L001) | TODO | Type `2` then `1` then `L001` |
| D2 | AVL Tree lookup (enter a location name) | TODO | Type `2` then `2` then a name |
| D3 | B-Tree lookup (enter V001) | TODO | Type `2` then `3` then `V001` |
| D4 | BST lookup (enter Q001) | TODO | Type `2` then `4` then `Q001` |
| D5 | FIFO Queue demo | TODO | Type `2` then `5` |
| D6 | Circular Queue wrap-around demo | TODO | Type `2` then `6` |
| D7 | Deque urgent-insertion demo | TODO | Type `2` then `7` |
| D8 | Priority Queue / Heap dispatch | TODO | Type `2` then `8` |

---

### E. Console Menu - Option 3: Search and Sort Lab

| # | Item | Status | Notes |
|---|------|--------|-------|
| E1 | Linear vs binary search (enter a location name) | TODO | Type `3` then `1` then a name |
| E2 | Selection sort by urgency | TODO | Type `3` then `2` then `1` |
| E3 | Insertion sort by urgency | TODO | Type `3` then `2` then `2` |
| E4 | Merge sort by urgency | TODO | Type `3` then `2` then `3` |
| E5 | QuickSort by urgency | TODO | Type `3` then `2` then `4` |

---

### F. Console Menu - Option 4: Graph Engine

| # | Item | Status | Notes |
|---|------|--------|-------|
| F1 | BFS from a location ID (e.g. L001) - visits all 50 locations | TODO | Type `4` then `1` then `L001` |
| F2 | DFS from a location ID - visits all 50 locations | TODO | Type `4` then `2` then `L001` |
| F3 | Dijkstra shortest path (e.g. L001 to L050) | TODO | Type `4` then `3` then IDs |
| F4 | Prim MST (start from L001) - prints 49 edges and total cost | TODO | Type `4` then `4` then `L001` |
| F5 | Kruskal MST - prints 49 edges and total cost | TODO | Type `4` then `5` |
| F6 | Prim and Kruskal produce same total MST cost | TODO | Compare F4 and F5 outputs |

---

### G. Console Menu - Option 5: Optimisation Engine

| # | Item | Status | Notes |
|---|------|--------|-------|
| G1 | Greedy resource assignment - assigns pending requests | TODO | Type `5` then `1` |
| G2 | Assignments persisted to DB (resource BUSY, request ASSIGNED) | TODO | Verify via menu 6 |
| G3 | Audit events recorded (check count printed) | TODO | Check count output |
| G4 | Greedy vs DP knapsack counterexample | TODO | Type `5` then `2` |
| G5 | Greedy=160, DP=220, greedy suboptimal by 60 | TODO | Verify output values |

---

### H. Console Menu - Option 6: Audit / Undo Log

| # | Item | Status | Notes |
|---|------|--------|-------|
| H1 | View stack size and most recent event | TODO | Run option 5-1 first, then `6` then `1` |
| H2 | Undo last action - reverts DB state | TODO | Type `6` then `2` |
| H3 | Repeated undo empties the stack | TODO | Keep undoing |

---

### I. Console Menu - Option 7: Performance Experiment Lab

| # | Item | Status | Notes |
|---|------|--------|-------|
| I1 | Run all six experiments | TODO | Type `7` then `7` |
| I2 | Results written to `results/*.csv` (6 files) | TODO | Check files |
| I3 | Results written to `algorithm_runs` table | TODO | Check DB |
| I4 | Each experiment runs 3x and averages | TODO | Verify from output |

---

### J. Data Structures - All 13 Built from Scratch

Verify none use `java.util` equivalents (no `ArrayList`, `HashMap`, `TreeMap`, etc.):

| # | Structure | Source File | Status |
|---|-----------|-------------|--------|
| J1 | Dynamic Array | `structures/DynamicArray.java` | TODO |
| J2 | Linked List | `structures/LinkedList.java` | TODO |
| J3 | Stack | `structures/Stack.java` | TODO |
| J4 | FIFO Queue | `structures/Queue.java` | TODO |
| J5 | Circular Queue | `structures/CircularQueue.java` | TODO |
| J6 | Deque | `structures/Deque.java` | TODO |
| J7 | Binary Heap | `structures/BinaryHeap.java` | TODO |
| J8 | Binary Search Tree | `structures/BinarySearchTree.java` | TODO |
| J9 | AVL Tree | `structures/AVLTree.java` | TODO |
| J10 | B-Tree | `structures/BTree.java` | TODO |
| J11 | Hash Table | `structures/HashTable.java` | TODO |
| J12 | Hash Set | `structures/HashSet.java` | TODO |
| J13 | Disjoint Set | `structures/DisjointSet.java` | TODO |
| J14 | Graph | `structures/graph/Graph.java` | TODO |

---

### K. Test Coverage - Edge Cases (PRD Required)

| # | Edge Case | Status | Notes |
|---|-----------|--------|-------|
| K1 | Empty structure operations | TODO | dequeue-on-empty, pop-on-empty |
| K2 | Single element | TODO | 1-element search/sort/tree |
| K3 | Duplicate keys | TODO | Hash table and tree tests |
| K4 | Disconnected graph | TODO | BFS/DFS unreachable nodes |
| K5 | Unreachable path (Dijkstra) | TODO | Returns null for disconnected |
| K6 | Full / empty queue | TODO | CircularQueue at capacity |
| K7 | Hash collision | TODO | Separate chaining |
| K8 | Greedy failure counterexample | TODO | GreedyAndDpTest |
| K9 | Invalid binary search precondition | TODO | SearchAlgorithmsTest |

---

### L. Documentation and Reports

| # | Item | Status | Notes |
|---|------|--------|-------|
| L1 | README.md is accurate and complete | PASS | Matches project structure |
| L2 | correctness_evidence.md has 6 trace tables | PASS | Binary search, insertion sort, merge sort, Dijkstra, Kruskal, DP |
| L3 | correctness_evidence.md has 3 proof sketches | PASS | Insertion sort, merge sort, Kruskal |
| L4 | correctness_evidence.md has 2 counterexamples | PASS | Greedy failure, binary search precondition |
| L5 | EVIDENCE_NOTE.md documents dataset generation | PASS | Seed derivation and record counts |
| L6 | TeamConfig.java has 14 index numbers | PASS | 14 students with names |
| L7 | 6 performance CSV files exist in results/ | PASS | All 6 present |

---

### M. TeamConfig Verification

| # | Item | Expected | Status |
|---|------|----------|--------|
| M1 | 14 index numbers listed | 14 students | PASS |
| M2 | TEAM_DIGIT_SUM | 416 | PASS (TeamConfigTest) |
| M3 | TEAM_TAIL_SUM | 5582 | PASS (TeamConfigTest) |
| M4 | RANDOM_SEED | 416,006,830 | PASS (TeamConfigTest) |
| M5 | PRIORITY_WEIGHT | 7 | PASS (TeamConfigTest) |
| M6 | ROUTE_PENALTY_FACTOR | 1.07 | PASS (TeamConfigTest) |
| M7 | HASH_TABLE_DEFAULT_CAPACITY | 59 | PASS (TeamConfigTest) |
| M8 | Dataset deterministic | same seed = same CSVs | TODO |

---

### N. Console Exit and Cleanup

| # | Item | Status | Notes |
|---|------|--------|-------|
| N1 | Option 0 exits with "Goodbye." | PASS | Confirmed |
| N2 | Database connection closed on exit | PASS | Database.close() in Main.java |
| N3 | Invalid menu options show "Unknown option." | TODO | Manual test needed |

---

## Results Summary

| Category | Items | Passed | Remaining | Description |
|----------|-------|--------|-----------|-------------|
| A | 5 | 5 | 0 | Environment and Build |
| B | 6 | 5 | 1 | First-Run Database Setup |
| C | 2 | 0 | 2 | Data and Database Menu |
| D | 8 | 0 | 8 | Data Structures Demo |
| E | 5 | 0 | 5 | Search and Sort Lab |
| F | 6 | 0 | 6 | Graph Engine |
| G | 5 | 0 | 5 | Optimisation Engine |
| H | 3 | 0 | 3 | Audit / Undo Log |
| I | 4 | 0 | 4 | Performance Experiments |
| J | 14 | 0 | 14 | Data Structures Code Review |
| K | 9 | 0 | 9 | Edge Case Test Coverage |
| L | 7 | 7 | 0 | Documentation and Reports |
| M | 8 | 7 | 1 | TeamConfig Verification |
| N | 3 | 2 | 1 | Console Exit and Cleanup |
| **Total** | **85** | **26** | **59** | |
