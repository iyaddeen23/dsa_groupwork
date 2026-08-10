package gh.edu.campushub.app;

import gh.edu.campushub.algorithms.dp.Knapsack;
import gh.edu.campushub.algorithms.dp.KnapsackItem;
import gh.edu.campushub.algorithms.graph.BFS;
import gh.edu.campushub.algorithms.graph.DFS;
import gh.edu.campushub.algorithms.graph.Dijkstra;
import gh.edu.campushub.algorithms.graph.Kruskal;
import gh.edu.campushub.algorithms.graph.MstEdge;
import gh.edu.campushub.algorithms.graph.MstResult;
import gh.edu.campushub.algorithms.graph.Prim;
import gh.edu.campushub.algorithms.greedy.GreedyKnapsack;
import gh.edu.campushub.algorithms.greedy.GreedyResourceAssignment;
import gh.edu.campushub.algorithms.search.BinarySearch;
import gh.edu.campushub.algorithms.search.LinearSearch;
import gh.edu.campushub.algorithms.sort.InsertionSort;
import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.algorithms.sort.QuickSort;
import gh.edu.campushub.algorithms.sort.SelectionSort;
import gh.edu.campushub.config.TeamConfig;
import gh.edu.campushub.db.CsvDataLoader;
import gh.edu.campushub.engine.AuditLog;
import gh.edu.campushub.engine.CampusDataStore;
import gh.edu.campushub.experiments.PerformanceLab;
import gh.edu.campushub.model.AuditEvent;
import gh.edu.campushub.model.Location;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.model.ServiceRequest;
import gh.edu.campushub.structures.BinaryHeap;
import gh.edu.campushub.structures.CircularQueue;
import gh.edu.campushub.structures.Deque;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.Queue;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Scanner;

/** The console menu — every operational question the PRD asks for is reachable from here without touching code. */
public class App {

    private final CampusDataStore store;
    private final AuditLog auditLog;
    private final Scanner in = new Scanner(System.in);

    public App(CampusDataStore store) {
        this.store = store;
        this.auditLog = new AuditLog(store.auditEventDao());
    }

    public void run() {
        System.out.println("\n=== Ghana Smart Service Operations Optimizer - University Campus Service Hub ===");
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> dataMenu();
                    case "2" -> structuresMenu();
                    case "3" -> searchSortMenu();
                    case "4" -> graphMenu();
                    case "5" -> optimisationMenu();
                    case "6" -> auditMenu();
                    case "7" -> PerformanceLab.runInteractive(store, in);
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMainMenu() {
        System.out.println("""

                1) Data & Database
                2) Data Structures Demo (hash/AVL/B-tree/BST/queues/heap)
                3) Search & Sort Lab
                4) Graph Engine (BFS/DFS/Dijkstra/Prim/Kruskal)
                5) Optimisation Engine (Greedy vs DP)
                6) Audit / Undo Log
                7) Performance Experiment Lab
                0) Exit
                Choose an option:""");
    }

    // ---- 1. Data & Database ----------------------------------------------------------

    private void dataMenu() {
        System.out.println("""
                1) Reload structures from database
                2) Re-import CSV seed files from ./data
                Choose:""");
        String choice = in.nextLine().trim();
        if (choice.equals("1")) {
            store.loadFromDatabase();
            System.out.println("Reloaded: " + store.locations().size() + " locations, " + store.roads().size()
                    + " roads, " + store.resources().size() + " resources, " + store.requests().size() + " requests.");
        } else if (choice.equals("2")) {
            CsvDataLoader loader = new CsvDataLoader(store.connection());
            System.out.println(loader.loadAll(Path.of("data")));
            store.loadFromDatabase();
        }
    }

    // ---- 2. Data structures demo -------------------------------------------------------

    private void structuresMenu() {
        System.out.println("""
                1) Location lookup by ID (Hash Table)
                2) Location lookup by name (AVL Tree, shows tree height)
                3) Resource lookup by ID (B-Tree, shows page height)
                4) Service request lookup by ID (unbalanced BST, shows search path length)
                5) FIFO queue walk-through
                6) Circular queue wrap-around demo
                7) Deque urgent-insertion demo
                8) Priority queue / heap dispatch order
                Choose:""");
        switch (in.nextLine().trim()) {
            case "1" -> {
                System.out.print("Location ID (e.g. L001): ");
                Location loc = store.locationById().get(in.nextLine().trim());
                System.out.println(loc != null ? loc : "Not found.");
            }
            case "2" -> {
                System.out.print("Location name: ");
                Location loc = store.locationByName().search(in.nextLine().trim());
                System.out.println(loc != null ? loc : "Not found.");
                System.out.println("AVL tree height: " + store.locationByName().height()
                        + " (n=" + store.locationByName().size() + ", theoretical min ~"
                        + (int) Math.ceil(Math.log(store.locationByName().size() + 1) / Math.log(2)) + ")");
            }
            case "3" -> {
                System.out.print("Resource ID (e.g. V001): ");
                Resource r = store.resourceIndex().search(in.nextLine().trim());
                System.out.println(r != null ? r : "Not found.");
                System.out.println("B-tree height (pages): " + store.resourceIndex().height());
            }
            case "4" -> {
                System.out.print("Request ID (e.g. Q001): ");
                ServiceRequest r = store.requestByIdBst().search(in.nextLine().trim());
                System.out.println(r != null ? r : "Not found.");
                System.out.println("BST height: " + store.requestByIdBst().height()
                        + " (n=" + store.requestByIdBst().size() + ") - unbalanced, so this can be far from log2(n)");
            }
            case "5" -> {
                Queue<ServiceRequest> queue = store.buildFifoQueue();
                System.out.println("FIFO queue size=" + queue.size() + ". Dequeuing first 5 in submission order:");
                for (int i = 0; i < 5 && !queue.isEmpty(); i++) {
                    System.out.println("  " + queue.dequeue());
                }
            }
            case "6" -> {
                CircularQueue<ServiceRequest> cq = store.buildCircularQueue();
                System.out.println("Circular queue capacity=" + cq.capacity() + ", size=" + cq.size());
                System.out.println("Dequeue 3, then enqueue 3 more to show front/rear wrap-around:");
                for (int i = 0; i < 3; i++) System.out.println("  dequeued: " + cq.dequeue());
                for (int i = 0; i < 3 && i < store.requests().size(); i++) cq.enqueue(store.requests().get(i));
                System.out.println("  new size=" + cq.size() + ", peek=" + cq.peek());
            }
            case "7" -> {
                Deque<ServiceRequest> deque = new Deque<>();
                int shown = 0;
                for (ServiceRequest r : store.requests()) {
                    if (shown++ >= 6) break;
                    if (r.getUrgency() >= 4) {
                        deque.addFront(r);
                        System.out.println("  URGENT -> addFront: " + r.getRequestId());
                    } else {
                        deque.addRear(r);
                        System.out.println("  normal -> addRear: " + r.getRequestId());
                    }
                }
                System.out.println("Deque front-to-rear order now has urgent requests first: front=" + deque.peekFront());
            }
            case "8" -> {
                BinaryHeap<ServiceRequest> heap = store.buildDispatchHeap(store.demoReferenceTime());
                System.out.println("Dispatch heap size=" + heap.size() + ". Extracting top 5 (most urgent first):");
                for (int i = 0; i < 5 && !heap.isEmpty(); i++) {
                    System.out.println("  " + heap.extractRoot());
                }
            }
            default -> System.out.println("Unknown option.");
        }
    }

    // ---- 3. Search & sort lab -----------------------------------------------------------

    private void searchSortMenu() {
        System.out.println("""
                1) Linear vs binary search for a location name
                2) Sort requests by urgency (pick an algorithm)
                Choose:""");
        switch (in.nextLine().trim()) {
            case "1" -> {
                String[] names = namesOf();
                System.out.print("Search for name: ");
                String target = in.nextLine().trim();

                long t0 = System.nanoTime();
                int linearIndex = LinearSearch.search(names, target, Comparator.naturalOrder());
                long t1 = System.nanoTime();

                String[] sortedNames = names.clone();
                MergeSort.sort(sortedNames);
                long t2 = System.nanoTime();
                int binaryIndex = BinarySearch.search(sortedNames, target, Comparator.naturalOrder());
                long t3 = System.nanoTime();

                System.out.printf("Linear search: index=%d, %.3f us%n", linearIndex, (t1 - t0) / 1000.0);
                System.out.printf("Binary search (on pre-sorted array): index=%d, %.3f us%n", binaryIndex, (t3 - t2) / 1000.0);
            }
            case "2" -> {
                System.out.println("1) Selection  2) Insertion  3) Merge  4) Quicksort");
                String alg = in.nextLine().trim();
                ServiceRequest[] requests = store.requests().toArray(ServiceRequest[]::new);
                Comparator<ServiceRequest> byUrgencyDesc = Comparator.comparingInt(ServiceRequest::getUrgency).reversed();
                long t0 = System.nanoTime();
                switch (alg) {
                    case "1" -> SelectionSort.sort(requests, byUrgencyDesc);
                    case "2" -> InsertionSort.sort(requests, byUrgencyDesc);
                    case "3" -> MergeSort.sort(requests, byUrgencyDesc);
                    case "4" -> QuickSort.sort(requests, byUrgencyDesc);
                    default -> System.out.println("Unknown algorithm.");
                }
                long t1 = System.nanoTime();
                System.out.printf("Sorted %d requests in %.3f ms. Top 5 by urgency:%n", requests.length, (t1 - t0) / 1_000_000.0);
                for (int i = 0; i < 5 && i < requests.length; i++) {
                    System.out.println("  " + requests[i]);
                }
            }
            default -> System.out.println("Unknown option.");
        }
    }

    private String[] namesOf() {
        String[] names = new String[store.locations().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = store.locations().get(i).getName();
        }
        return names;
    }

    // ---- 4. Graph engine ------------------------------------------------------------------

    private void graphMenu() {
        System.out.println("""
                1) BFS from a location
                2) DFS from a location
                3) Dijkstra shortest path between two locations
                4) Prim MST
                5) Kruskal MST
                Choose:""");
        switch (in.nextLine().trim()) {
            case "1" -> {
                System.out.print("Source location ID: ");
                DynamicArray<String> order = BFS.traverse(store.roadNetwork(), in.nextLine().trim());
                System.out.println("BFS visited " + order.size() + " locations: " + order);
            }
            case "2" -> {
                System.out.print("Source location ID: ");
                DynamicArray<String> order = DFS.traverse(store.roadNetwork(), in.nextLine().trim());
                System.out.println("DFS visited " + order.size() + " locations: " + order);
            }
            case "3" -> {
                System.out.print("From location ID: ");
                String from = in.nextLine().trim();
                System.out.print("To location ID: ");
                String to = in.nextLine().trim();
                Dijkstra.Result<String> result = Dijkstra.run(store.roadNetwork(), from);
                Double distance = result.distances.get(to);
                if (distance == null) {
                    System.out.println("No path found from " + from + " to " + to);
                } else {
                    System.out.printf("Shortest route cost: %.3f%n", distance);
                    System.out.println("Path: " + result.pathTo(to));
                }
            }
            case "4" -> {
                System.out.print("Start location ID: ");
                MstResult<String> result = Prim.run(store.roadNetwork(), in.nextLine().trim());
                printMst(result);
            }
            case "5" -> {
                MstResult<String> result = Kruskal.run(store.roadNetwork());
                printMst(result);
            }
            default -> System.out.println("Unknown option.");
        }
    }

    private void printMst(MstResult<String> result) {
        System.out.println("MST edges (" + result.edges.size() + "):");
        for (MstEdge<String> edge : result.edges) {
            System.out.println("  " + edge);
        }
        System.out.printf("Total network cost: %.3f%n", result.totalCost);
    }

    // ---- 5. Optimisation engine -------------------------------------------------------------

    private void optimisationMenu() {
        System.out.println("""
                1) Greedy priority-based resource assignment
                2) Greedy vs DP knapsack counterexample (request selection under a budget)
                Choose:""");
        switch (in.nextLine().trim()) {
            case "1" -> {
                DynamicArray<ServiceRequest> pending = new DynamicArray<>();
                for (ServiceRequest r : store.requests()) {
                    if (r.getStatus() == gh.edu.campushub.model.RequestStatus.NEW) {
                        pending.add(r);
                    }
                }
                DynamicArray<Resource> available = new DynamicArray<>();
                for (Resource r : store.resources()) {
                    if (r.getAvailabilityStatus() == gh.edu.campushub.model.AvailabilityStatus.AVAILABLE) {
                        available.add(r);
                    }
                }
                GreedyResourceAssignment.Result result = GreedyResourceAssignment.assign(pending, available, store.demoReferenceTime());
                System.out.println("Assigned " + result.assigned().size() + " of " + pending.size() + " pending requests:");
                for (int i = 0; i < result.assigned().size(); i++) {
                    var a = result.assigned().get(i);
                    // Persist the assignment (resource goes BUSY, request goes ASSIGNED) and log both
                    // as undoable audit events — this is what makes the undo stack in menu 6 non-empty.
                    store.resourceDao().updateAvailability(a.resource().getResourceId(), gh.edu.campushub.model.AvailabilityStatus.BUSY);
                    auditLog.record("ASSIGN_RESOURCE", "resources", a.resource().getResourceId(), "AVAILABLE", "BUSY");
                    store.serviceRequestDao().updateStatus(a.request().getRequestId(), gh.edu.campushub.model.RequestStatus.ASSIGNED);
                    auditLog.record("ASSIGN_REQUEST", "service_requests", a.request().getRequestId(), "NEW", "ASSIGNED");
                    if (i < 10) {
                        System.out.println("  " + a.request().getRequestId() + " -> " + a.resource().getResourceId());
                    }
                }
                System.out.println("Unassigned: " + result.unassigned().size());
                if (!result.assigned().isEmpty()) {
                    store.loadFromDatabase();
                    System.out.println("(" + result.assigned().size() * 2 + " audit events recorded — see menu 6 to inspect/undo.)");
                }
            }
            case "2" -> {
                KnapsackItem[] items = GreedyKnapsack.counterexample();
                int capacity = GreedyKnapsack.COUNTEREXAMPLE_CAPACITY;
                GreedyKnapsack.Result greedy = GreedyKnapsack.solve(items, capacity);
                Knapsack.Result dp = Knapsack.solve(items, capacity);
                System.out.println("Items: A(w10,v60) B(w20,v100) C(w30,v120), capacity=" + capacity);
                System.out.println("Greedy (by value/weight ratio) picks: " + greedy.selected() + " total value=" + greedy.totalValue());
                System.out.println("DP (0/1 knapsack, optimal) picks:     " + dp.selected() + " total value=" + dp.totalValue());
                System.out.println("=> Greedy is suboptimal here by " + (dp.totalValue() - greedy.totalValue()) + " value points.");
            }
            default -> System.out.println("Unknown option.");
        }
    }

    // ---- 6. Audit / undo log -----------------------------------------------------------------

    private void auditMenu() {
        System.out.println("""
                1) View undo stack size / most recent event
                2) Undo last action (pops the stack)
                Choose:""");
        switch (in.nextLine().trim()) {
            case "1" -> {
                System.out.println("Stack size: " + auditLog.size());
                if (auditLog.canUndo()) {
                    System.out.println("Most recent: " + auditLog.peekLast());
                }
            }
            case "2" -> {
                if (!auditLog.canUndo()) {
                    System.out.println("Nothing to undo.");
                } else {
                    AuditEvent undone = auditLog.undoLast();
                    revertEntityState(undone);
                    store.loadFromDatabase();
                    System.out.println("Undid: " + undone);
                }
            }
            default -> System.out.println("Unknown option.");
        }
    }

    /** Applies an audit event's {@code beforeState} back onto the actual entity — the "undo" half of undo. */
    private void revertEntityState(AuditEvent event) {
        switch (event.getEntityTable()) {
            case "resources" -> store.resourceDao().updateAvailability(
                    event.getEntityId(), gh.edu.campushub.model.AvailabilityStatus.valueOf(event.getBeforeState()));
            case "service_requests" -> store.serviceRequestDao().updateStatus(
                    event.getEntityId(), gh.edu.campushub.model.RequestStatus.valueOf(event.getBeforeState()));
            default -> System.out.println("(no revert handler registered for table " + event.getEntityTable() + ")");
        }
    }

    static {
        // Touch TeamConfig once at class-load so its derived-parameter formulas run and fail fast if ever broken.
        assert TeamConfig.PRIORITY_WEIGHT >= 1;
    }
}
