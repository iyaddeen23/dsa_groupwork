package gh.edu.campushub.engine;

import gh.edu.campushub.db.AlgorithmRunDao;
import gh.edu.campushub.db.AuditEventDao;
import gh.edu.campushub.db.LocationDao;
import gh.edu.campushub.db.ResourceDao;
import gh.edu.campushub.db.RoadDao;
import gh.edu.campushub.db.ServiceRequestDao;
import gh.edu.campushub.model.Location;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.model.Road;
import gh.edu.campushub.model.ServiceRequest;
import gh.edu.campushub.structures.AVLTree;
import gh.edu.campushub.structures.BTree;
import gh.edu.campushub.structures.BinarySearchTree;
import gh.edu.campushub.structures.BinaryHeap;
import gh.edu.campushub.structures.CircularQueue;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashTable;
import gh.edu.campushub.structures.Queue;
import gh.edu.campushub.structures.graph.Graph;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Loads every table out of the database and rebuilds the custom, from-scratch
 * data structures the rest of the app runs on (M2: "reload into custom
 * structures"). Nothing downstream of this class touches JDBC directly.
 */
public class CampusDataStore {

    private final Connection connection;
    private final LocationDao locationDao;
    private final RoadDao roadDao;
    private final ResourceDao resourceDao;
    private final ServiceRequestDao serviceRequestDao;
    private final AlgorithmRunDao algorithmRunDao;
    private final AuditEventDao auditEventDao;

    private DynamicArray<Location> locations = new DynamicArray<>();
    private DynamicArray<Road> roads = new DynamicArray<>();
    private DynamicArray<Resource> resources = new DynamicArray<>();
    private DynamicArray<ServiceRequest> requests = new DynamicArray<>();

    // M6 indexing engine: three different structures indexing the same data, on purpose.
    private HashTable<String, Location> locationById = new HashTable<>();
    private HashTable<String, Resource> resourceById = new HashTable<>();
    private HashTable<String, ServiceRequest> requestById = new HashTable<>();
    private AVLTree<String, Location> locationByName = new AVLTree<>();
    private BinarySearchTree<String, ServiceRequest> requestByIdBst = new BinarySearchTree<>();
    private BTree<String, Resource> resourceIndex = new BTree<>(3);

    // M7 graph engine.
    private Graph<String> roadNetwork = new Graph<>(false);

    public CampusDataStore(Connection connection) {
        this.connection = connection;
        this.locationDao = new LocationDao(connection);
        this.roadDao = new RoadDao(connection);
        this.resourceDao = new ResourceDao(connection);
        this.serviceRequestDao = new ServiceRequestDao(connection);
        this.algorithmRunDao = new AlgorithmRunDao(connection);
        this.auditEventDao = new AuditEventDao(connection);
    }

    /** Re-reads every table from the database and rebuilds all in-memory structures from scratch. */
    public void loadFromDatabase() {
        locations = locationDao.findAll();
        roads = roadDao.findAll();
        resources = resourceDao.findAll();
        requests = serviceRequestDao.findAll();

        locationById = new HashTable<>();
        locationByName = new AVLTree<>();
        for (Location location : locations) {
            locationById.put(location.getLocationId(), location);
            locationByName.insert(location.getName(), location);
        }

        resourceById = new HashTable<>();
        resourceIndex = new BTree<>(3);
        for (Resource resource : resources) {
            resourceById.put(resource.getResourceId(), resource);
            resourceIndex.insert(resource.getResourceId(), resource);
        }

        requestById = new HashTable<>();
        requestByIdBst = new BinarySearchTree<>();
        for (ServiceRequest request : requests) {
            requestById.put(request.getRequestId(), request);
            requestByIdBst.insert(request.getRequestId(), request);
        }

        roadNetwork = new Graph<>(false);
        for (Location location : locations) {
            roadNetwork.addVertex(location.getLocationId());
        }
        for (Road road : roads) {
            roadNetwork.addEdge(road.getFromLocationId(), road.getToLocationId(), road.routeCost());
        }
    }

    // ---- Accessors ----------------------------------------------------------------

    public Connection connection() { return connection; }
    public LocationDao locationDao() { return locationDao; }
    public RoadDao roadDao() { return roadDao; }
    public ResourceDao resourceDao() { return resourceDao; }
    public ServiceRequestDao serviceRequestDao() { return serviceRequestDao; }
    public AlgorithmRunDao algorithmRunDao() { return algorithmRunDao; }
    public AuditEventDao auditEventDao() { return auditEventDao; }

    public DynamicArray<Location> locations() { return locations; }
    public DynamicArray<Road> roads() { return roads; }
    public DynamicArray<Resource> resources() { return resources; }
    public DynamicArray<ServiceRequest> requests() { return requests; }

    public HashTable<String, Location> locationById() { return locationById; }
    public HashTable<String, Resource> resourceById() { return resourceById; }
    public HashTable<String, ServiceRequest> requestById() { return requestById; }
    public AVLTree<String, Location> locationByName() { return locationByName; }
    public BinarySearchTree<String, ServiceRequest> requestByIdBst() { return requestByIdBst; }
    public BTree<String, Resource> resourceIndex() { return resourceIndex; }
    public Graph<String> roadNetwork() { return roadNetwork; }

    /** Builds a fresh min-heap over every currently-active (NEW/ASSIGNED) request, most urgent first. */
    public BinaryHeap<ServiceRequest> buildDispatchHeap(LocalDateTime now) {
        Comparator<ServiceRequest> byDispatchScore = Comparator.comparingDouble(r -> r.dispatchScore(now));
        BinaryHeap<ServiceRequest> heap = new BinaryHeap<>(byDispatchScore);
        for (ServiceRequest request : requests) {
            if (request.getStatus() == gh.edu.campushub.model.RequestStatus.NEW
                    || request.getStatus() == gh.edu.campushub.model.RequestStatus.ASSIGNED) {
                heap.insert(request);
            }
        }
        return heap;
    }

    /**
     * A "now" for dispatch-scoring demos, anchored to the dataset itself (the latest
     * time_submitted across every loaded request) rather than the wall clock. The seed
     * dataset's dates are fixed at generation time, so scoring against the real
     * {@code LocalDateTime.now()} degrades over time: every deadline eventually falls
     * further and further into the past, and that growing "how overdue" gap swamps the
     * much smaller urgency-weight signal that this demo is supposed to showcase.
     */
    public LocalDateTime demoReferenceTime() {
        LocalDateTime latest = null;
        for (ServiceRequest request : requests) {
            if (latest == null || request.getTimeSubmitted().isAfter(latest)) {
                latest = request.getTimeSubmitted();
            }
        }
        return latest != null ? latest : LocalDateTime.now();
    }

    /** FIFO view of every request in submission order (M5 evidence). */
    public Queue<ServiceRequest> buildFifoQueue() {
        Queue<ServiceRequest> queue = new Queue<>();
        for (ServiceRequest request : requests) {
            queue.enqueue(request);
        }
        return queue;
    }

    /** Same requests through the circular-buffer queue, to show wrap-around under repeated dequeue/enqueue. */
    public CircularQueue<ServiceRequest> buildCircularQueue() {
        CircularQueue<ServiceRequest> queue = new CircularQueue<>(16);
        for (ServiceRequest request : requests) {
            queue.enqueue(request);
        }
        return queue;
    }
}
