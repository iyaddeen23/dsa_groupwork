package gh.edu.campushub.algorithms;

import gh.edu.campushub.algorithms.dp.Knapsack;
import gh.edu.campushub.algorithms.dp.KnapsackItem;
import gh.edu.campushub.algorithms.greedy.GreedyKnapsack;
import gh.edu.campushub.algorithms.greedy.GreedyResourceAssignment;
import gh.edu.campushub.model.AvailabilityStatus;
import gh.edu.campushub.model.RequestStatus;
import gh.edu.campushub.model.Resource;
import gh.edu.campushub.model.ServiceRequest;
import gh.edu.campushub.structures.DynamicArray;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GreedyAndDpTest {

    /**
     * Required counterexample #1 (PRD Section 8: "one greedy failure"). The ratio-greedy
     * knapsack heuristic picks A+B (value 160) and DP finds the true optimum B+C (value 220).
     */
    @Test
    void greedyKnapsack_isProvablySuboptimalOnTheCounterexample() {
        KnapsackItem[] items = GreedyKnapsack.counterexample();
        int capacity = GreedyKnapsack.COUNTEREXAMPLE_CAPACITY;

        GreedyKnapsack.Result greedy = GreedyKnapsack.solve(items, capacity);
        Knapsack.Result dp = Knapsack.solve(items, capacity);

        assertEquals(160, greedy.totalValue());
        assertEquals(220, dp.totalValue());
        assertTrue(dp.totalValue() > greedy.totalValue(), "DP must strictly beat the greedy heuristic here");
    }

    @Test
    void dpKnapsack_reconstructsExactSelectedItems() {
        KnapsackItem[] items = GreedyKnapsack.counterexample();
        Knapsack.Result dp = Knapsack.solve(items, GreedyKnapsack.COUNTEREXAMPLE_CAPACITY);

        assertEquals(2, dp.selected().size());
        boolean hasB = false, hasC = false, hasA = false;
        for (KnapsackItem item : dp.selected()) {
            if (item.id().equals("B")) hasB = true;
            if (item.id().equals("C")) hasC = true;
            if (item.id().equals("A")) hasA = true;
        }
        assertTrue(hasB && hasC);
        assertFalse(hasA, "the optimal solution does not include item A at all");
    }

    @Test
    void dpKnapsack_zeroCapacity_selectsNothing() {
        KnapsackItem[] items = {new KnapsackItem("X", 5, 10)};
        Knapsack.Result result = Knapsack.solve(items, 0);
        assertEquals(0, result.totalValue());
        assertTrue(result.selected().isEmpty());
    }

    @Test
    void dpKnapsack_itemHeavierThanCapacity_isNeverChosen() {
        KnapsackItem[] items = {new KnapsackItem("heavy", 100, 1000), new KnapsackItem("light", 1, 1)};
        Knapsack.Result result = Knapsack.solve(items, 10);
        assertEquals(1, result.totalValue());
    }

    @Test
    void greedyResourceAssignment_assignsMostUrgentFirstAndReportsUnassigned() {
        LocalDateTime now = LocalDateTime.now();
        DynamicArray<ServiceRequest> requests = new DynamicArray<>();
        requests.add(new ServiceRequest("Q1", "L001", "L002", "Medical", 1, now, now.plusHours(5), RequestStatus.NEW));
        requests.add(new ServiceRequest("Q2", "L001", "L002", "Medical", 5, now, now.plusMinutes(30), RequestStatus.NEW));

        DynamicArray<Resource> resources = new DynamicArray<>();
        resources.add(new Resource("V1", "Van", "L001", 4, AvailabilityStatus.AVAILABLE));

        GreedyResourceAssignment.Result result = GreedyResourceAssignment.assign(requests, resources, now);
        assertEquals(1, result.assigned().size());
        assertEquals("Q2", result.assigned().get(0).request().getRequestId(), "the more urgent request must be served first");
        assertEquals(1, result.unassigned().size());
        assertEquals("Q1", result.unassigned().get(0).getRequestId());
    }

    @Test
    void greedyResourceAssignment_noResources_leavesEverythingUnassigned() {
        LocalDateTime now = LocalDateTime.now();
        DynamicArray<ServiceRequest> requests = new DynamicArray<>();
        requests.add(new ServiceRequest("Q1", "L001", "L002", "Medical", 3, now, now.plusHours(1), RequestStatus.NEW));
        GreedyResourceAssignment.Result result = GreedyResourceAssignment.assign(requests, new DynamicArray<>(), now);
        assertEquals(0, result.assigned().size());
        assertEquals(1, result.unassigned().size());
    }
}
