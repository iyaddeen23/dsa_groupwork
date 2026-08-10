package gh.edu.campushub.algorithms.greedy;

import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.model.ServiceRequest;
import gh.edu.campushub.structures.DynamicArray;
import gh.edu.campushub.structures.HashTable;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Greedy priority-based resource assignment (M8): sort every pending request
 * by dispatch score (most urgent/soonest-deadline first, via the
 * from-scratch {@link MergeSort}), then walk the list once, handing each
 * request the first still-available resource. Locally optimal at each step,
 * cheap to compute (O(n log n) for the sort + O(n*m) for assignment), and
 * good enough for the common case — but see {@link GreedyKnapsack} for a
 * worked example of exactly where a one-pass greedy choice like this stops
 * being globally optimal once a hard capacity budget is involved.
 */
public final class GreedyResourceAssignment {

    private GreedyResourceAssignment() {
    }

    public record Assignment(ServiceRequest request, Resource resource) {
    }

    public record Result(DynamicArray<Assignment> assigned, DynamicArray<ServiceRequest> unassigned) {
    }

    public static Result assign(DynamicArray<ServiceRequest> pendingRequests,
                                 DynamicArray<Resource> availableResources,
                                 LocalDateTime now) {
        ServiceRequest[] sorted = pendingRequests.toArray(ServiceRequest[]::new);
        MergeSort.sort(sorted, Comparator.comparingDouble(r -> r.dispatchScore(now)));

        HashTable<String, Resource> pool = new HashTable<>();
        for (Resource resource : availableResources) {
            pool.put(resource.getResourceId(), resource);
        }
        DynamicArray<String> poolOrder = new DynamicArray<>();
        for (Resource resource : availableResources) {
            poolOrder.add(resource.getResourceId());
        }

        DynamicArray<Assignment> assigned = new DynamicArray<>();
        DynamicArray<ServiceRequest> unassigned = new DynamicArray<>();

        for (ServiceRequest request : sorted) {
            Resource chosen = null;
            int chosenSlot = -1;
            for (int i = 0; i < poolOrder.size(); i++) {
                Resource candidate = pool.get(poolOrder.get(i));
                if (candidate != null) {
                    chosen = candidate;
                    chosenSlot = i;
                    break;
                }
            }
            if (chosen == null) {
                unassigned.add(request);
            } else {
                assigned.add(new Assignment(request, chosen));
                pool.remove(chosen.getResourceId());
                poolOrder.remove(chosenSlot);
            }
        }
        return new Result(assigned, unassigned);
    }
}
