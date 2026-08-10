package gh.edu.campushub.experiments;

import gh.edu.campushub.algorithms.graph.BFS;
import gh.edu.campushub.algorithms.graph.DFS;
import gh.edu.campushub.algorithms.graph.Dijkstra;
import gh.edu.campushub.algorithms.graph.Kruskal;
import gh.edu.campushub.algorithms.graph.Prim;
import gh.edu.campushub.algorithms.search.BinarySearch;
import gh.edu.campushub.algorithms.search.LinearSearch;
import gh.edu.campushub.algorithms.sort.InsertionSort;
import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.algorithms.sort.QuickSort;
import gh.edu.campushub.algorithms.sort.SelectionSort;
import gh.edu.campushub.config.TeamConfig;
import gh.edu.campushub.engine.CampusDataStore;
import gh.edu.campushub.model.AlgorithmRun;
import gh.edu.campushub.structures.AVLTree;
import gh.edu.campushub.structures.BinarySearchTree;
import gh.edu.campushub.structures.BinaryHeap;
import gh.edu.campushub.structures.HashTable;
import gh.edu.campushub.structures.graph.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

/**
 * M10 empirical efficiency lab: times every required algorithm family at the
 * PRD's required input sizes, averages 3 runs each, records every run to the
 * {@code algorithm_runs} table AND to a raw CSV under {@code results/} (kept
 * separately from screenshots, per Section 10: "keep raw timings").
 */
public final class PerformanceLab {

    private static final int[] SEARCH_SORT_SIZES = {100, 500, 1000, 5000, 10000};
    private static final int[] HASH_SIZES = {100, 500, 1000, 5000, 10000, 20000};
    private static final int[] TREE_SIZES = {100, 500, 1000, 5000, 10000};
    private static final int[] HEAP_SIZES = {100, 500, 1000, 5000, 10000, 20000};
    private static final int[] GRAPH_SIZES = {50, 100, 200, 500};
    private static final int REPEATS = 3;
    private static final Path RESULTS_DIR = Path.of("results");

    private PerformanceLab() {
    }

    public static void runInteractive(CampusDataStore store, Scanner in) throws IOException {
        System.out.println("""
                1) Search comparison (linear vs binary)
                2) Sorting comparison (selection/insertion/merge/quicksort)
                3) Hash table load-factor experiment
                4) BST vs AVL tree comparison
                5) Heap priority-dispatch timing
                6) Graph algorithms (BFS/DFS/Dijkstra/Prim/Kruskal)
                7) Run ALL experiments
                Choose:""");
        Files.createDirectories(RESULTS_DIR);
        switch (in.nextLine().trim()) {
            case "1" -> searchComparison(store);
            case "2" -> sortingComparison(store);
            case "3" -> hashLoadFactor(store);
            case "4" -> treeComparison(store);
            case "5" -> heapDispatch(store);
            case "6" -> graphAlgorithms(store);
            case "7" -> {
                searchComparison(store);
                sortingComparison(store);
                hashLoadFactor(store);
                treeComparison(store);
                heapDispatch(store);
                graphAlgorithms(store);
            }
            default -> System.out.println("Unknown option.");
        }
    }

    // ---- helpers ------------------------------------------------------------------------

    private static double timeAvgMs(Runnable action) {
        long total = 0;
        for (int i = 0; i < REPEATS; i++) {
            long start = System.nanoTime();
            action.run();
            total += System.nanoTime() - start;
        }
        return total / (double) REPEATS / 1_000_000.0;
    }

    private static Integer[] randomArray(int size, long seedOffset) {
        Random random = new Random(TeamConfig.RANDOM_SEED + seedOffset);
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 10);
        }
        return array;
    }

    private static void record(CampusDataStore store, PrintWriter csv, String algorithmName, int inputSize, double timeMs) {
        AlgorithmRun run = new AlgorithmRun(UUID.randomUUID().toString(), algorithmName, inputSize, timeMs, null, LocalDateTime.now());
        store.algorithmRunDao().insert(run);
        csv.printf("%s,%s,%d,%.4f,%s%n", run.getRunId(), algorithmName, inputSize, timeMs, run.getRunAt());
        System.out.printf("  %-22s n=%-7d %.4f ms%n", algorithmName, inputSize, timeMs);
    }

    private static PrintWriter openCsv(String name) throws IOException {
        PrintWriter writer = new PrintWriter(Files.newBufferedWriter(RESULTS_DIR.resolve(name)));
        writer.println("run_id,algorithm_name,input_size,execution_time_ms,run_at");
        return writer;
    }

    // ---- 1. search comparison -------------------------------------------------------------

    public static void searchComparison(CampusDataStore store) throws IOException {
        System.out.println("Search comparison (linear vs binary):");
        try (PrintWriter csv = openCsv("search_comparison.csv")) {
            for (int size : SEARCH_SORT_SIZES) {
                Integer[] data = randomArray(size, 1);
                Integer target = data[data.length - 1]; // worst case for linear search

                double linearMs = timeAvgMs(() -> LinearSearch.search(data, target, Comparator.naturalOrder()));
                record(store, csv, "LinearSearch", size, linearMs);

                Integer[] sorted = data.clone();
                MergeSort.sort(sorted);
                double binaryMs = timeAvgMs(() -> BinarySearch.search(sorted, target, Comparator.naturalOrder()));
                record(store, csv, "BinarySearch", size, binaryMs);
            }
        }
    }

    // ---- 2. sorting comparison --------------------------------------------------------------

    public static void sortingComparison(CampusDataStore store) throws IOException {
        System.out.println("Sorting comparison (selection/insertion/merge/quicksort):");
        try (PrintWriter csv = openCsv("sorting_comparison.csv")) {
            for (int size : SEARCH_SORT_SIZES) {
                Integer[] base = randomArray(size, 2);

                record(store, csv, "SelectionSort", size, timeAvgMs(() -> SelectionSort.sort(base.clone())));
                record(store, csv, "InsertionSort", size, timeAvgMs(() -> InsertionSort.sort(base.clone())));
                record(store, csv, "MergeSort", size, timeAvgMs(() -> MergeSort.sort(base.clone())));
                record(store, csv, "QuickSort", size, timeAvgMs(() -> QuickSort.sort(base.clone())));
            }
        }
    }

    // ---- 3. hash table load factor ------------------------------------------------------------

    /**
     * Two passes, both using RANDOM (not sequential) keys — sequential integer keys under
     * mod-hashing land in unique buckets almost by construction whenever the table has
     * already grown past the key range, which hides collisions entirely and defeats the
     * point of this experiment.
     */
    public static void hashLoadFactor(CampusDataStore store) throws IOException {
        System.out.println("Hash table load-factor experiment:");
        try (PrintWriter csv = openCsv("hash_load_factor.csv")) {
            csv.println("# pass 1: auto-growing table, increasing key count");
            for (int keyCount : HASH_SIZES) {
                Integer[] keys = randomArray(keyCount, 5);
                double insertMs = timeAvgMs(() -> {
                    HashTable<Integer, Integer> table = new HashTable<>();
                    for (Integer key : keys) table.put(key, key);
                });
                HashTable<Integer, Integer> sample = new HashTable<>();
                for (Integer key : keys) sample.put(key, key);
                record(store, csv, "HashTable.put", keyCount, insertMs);
                System.out.printf("    load_factor=%.3f collisions=%d%n", sample.loadFactor(), sample.getCollisionCount());
            }

            csv.println("# pass 2: fixed key count (20000, random), varying FIXED initial table capacity");
            int fixedKeyCount = HASH_SIZES[HASH_SIZES.length - 1];
            Integer[] fixedKeys = randomArray(fixedKeyCount, 6);
            int[] capacities = {capacityFor(fixedKeyCount, 8), capacityFor(fixedKeyCount, 2),
                    capacityFor(fixedKeyCount, 1), capacityFor(fixedKeyCount, 1) * 4};
            for (int capacity : capacities) {
                HashTable<Integer, Integer> table = new HashTable<>(capacity);
                for (Integer key : fixedKeys) table.put(key, key);
                System.out.printf("    initial_capacity=%-7d keys=%d final_load_factor=%.3f collisions=%d%n",
                        capacity, fixedKeyCount, table.loadFactor(), table.getCollisionCount());
                csv.printf("capacity-%d,HashTable.capacityVariation,%d,%d,%s%n",
                        capacity, fixedKeyCount, table.getCollisionCount(), LocalDateTime.now());
            }
        }
    }

    /** capacity = keyCount / divisor, so smaller divisors mean a roomier (less collision-prone) starting table. */
    private static int capacityFor(int keyCount, int divisor) {
        return Math.max(11, keyCount / divisor);
    }

    // ---- 4. BST vs AVL -------------------------------------------------------------------------

    public static void treeComparison(CampusDataStore store) throws IOException {
        System.out.println("BST vs AVL tree comparison:");
        try (PrintWriter csv = openCsv("tree_comparison.csv")) {
            for (int size : TREE_SIZES) {
                Integer[] data = randomArray(size, 3);

                double bstMs = timeAvgMs(() -> {
                    BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
                    for (Integer key : data) bst.insert(key, key);
                });
                BinarySearchTree<Integer, Integer> bstSample = new BinarySearchTree<>();
                for (Integer key : data) bstSample.insert(key, key);
                record(store, csv, "BST.insert", size, bstMs);
                System.out.println("    BST height=" + bstSample.height());

                double avlMs = timeAvgMs(() -> {
                    AVLTree<Integer, Integer> avl = new AVLTree<>();
                    for (Integer key : data) avl.insert(key, key);
                });
                AVLTree<Integer, Integer> avlSample = new AVLTree<>();
                for (Integer key : data) avlSample.insert(key, key);
                record(store, csv, "AVLTree.insert", size, avlMs);
                System.out.println("    AVL height=" + avlSample.height()
                        + " (theoretical min ~" + (int) Math.ceil(Math.log(size + 1) / Math.log(2)) + ")");
            }
        }
    }

    // ---- 5. heap dispatch ----------------------------------------------------------------------

    public static void heapDispatch(CampusDataStore store) throws IOException {
        System.out.println("Heap priority-dispatch timing:");
        try (PrintWriter csv = openCsv("heap_dispatch.csv")) {
            for (int size : HEAP_SIZES) {
                Integer[] data = randomArray(size, 4);

                double insertMs = timeAvgMs(() -> {
                    BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
                    for (Integer value : data) heap.insert(value);
                });
                record(store, csv, "BinaryHeap.insert", size, insertMs);

                double extractMs = timeAvgMs(() -> {
                    BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
                    for (Integer value : data) heap.insert(value);
                    while (!heap.isEmpty()) heap.extractRoot();
                });
                record(store, csv, "BinaryHeap.extractAll", size, extractMs);
            }
        }
    }

    // ---- 6. graph algorithms ------------------------------------------------------------------

    public static void graphAlgorithms(CampusDataStore store) throws IOException {
        System.out.println("Graph algorithm timing (synthetic connected graphs):");
        try (PrintWriter csv = openCsv("graph_algorithms.csv")) {
            for (int vertexCount : GRAPH_SIZES) {
                Graph<Integer> graph = randomConnectedGraph(vertexCount, 5);

                record(store, csv, "BFS", vertexCount, timeAvgMs(() -> BFS.traverse(graph, 0)));
                record(store, csv, "DFS", vertexCount, timeAvgMs(() -> DFS.traverse(graph, 0)));
                record(store, csv, "Dijkstra", vertexCount, timeAvgMs(() -> Dijkstra.run(graph, 0)));
                record(store, csv, "Prim", vertexCount, timeAvgMs(() -> Prim.run(graph, 0)));
                record(store, csv, "Kruskal", vertexCount, timeAvgMs(() -> Kruskal.run(graph)));
            }
        }
    }

    /** Builds a random connected graph (spanning tree + extra random edges) for graph-algorithm benchmarking. */
    private static Graph<Integer> randomConnectedGraph(int vertexCount, long seedOffset) {
        Random random = new Random(TeamConfig.RANDOM_SEED + seedOffset + vertexCount);
        Graph<Integer> graph = new Graph<>(false);
        for (int i = 0; i < vertexCount; i++) {
            graph.addVertex(i);
        }
        for (int i = 1; i < vertexCount; i++) {
            int parent = random.nextInt(i);
            graph.addEdge(parent, i, 1 + random.nextDouble() * 10);
        }
        int extraEdges = vertexCount * 2;
        for (int i = 0; i < extraEdges; i++) {
            int a = random.nextInt(vertexCount);
            int b = random.nextInt(vertexCount);
            if (a != b) {
                graph.addEdge(a, b, 1 + random.nextDouble() * 10);
            }
        }
        return graph;
    }
}
