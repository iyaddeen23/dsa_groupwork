# Correctness Evidence

**DCIT 204/308 Joint DSA Semester Project — Ghana Smart Service Operations Optimizer**

This document collects the PRD Section 8 correctness deliverables: 6 trace
tables, 3 proof sketches, and the 2 required counterexamples. Every trace
below matches a real, runnable code path — see the cross-referenced test in
`src/test/java/...` for the automated version of the same check.

---

## 1. Trace tables

### 1.1 Binary search

`BinarySearch.search(array, target)` on the sorted array
`[1, 3, 5, 7, 9, 11, 13]` (indices 0–6), target = `7`.

**Precondition** (stated and enforced by test, not by the algorithm itself —
see §3 counterexample): the input array must already be sorted ascending.

| Step | low | high | mid | array[mid] | compare(array[mid], target) | action |
|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | 7 | 0 (equal) | return 3 |

Target found on the first probe (index 3). A miss trace (target = `4`,
not present) takes 3 steps:

| Step | low | high | mid | array[mid] | compare(array[mid], target) | action |
|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | 7 | > 0 | high = 2 |
| 2 | 0 | 2 | 1 | 3 | < 0 | low = 2 |
| 3 | 2 | 2 | 2 | 5 | > 0 | high = 1 |
| — | 2 | 1 | — | — | low > high | return -1 |

Matches `SearchAlgorithmsTest.binarySearch_findsElementInSortedArray` /
`.binarySearch_singleElementArray`.

### 1.2 Insertion sort

`InsertionSort.sort([5, 2, 4, 6, 1, 3])`, ascending.

| i | key | array before shifting | shifts | array after pass |
|---|---|---|---|---|
| 1 | 2 | [5, 2, 4, 6, 1, 3] | 5→idx1 | [2, 5, 4, 6, 1, 3] |
| 2 | 4 | [2, 5, 4, 6, 1, 3] | 5→idx2 | [2, 4, 5, 6, 1, 3] |
| 3 | 6 | [2, 4, 5, 6, 1, 3] | none (6≥5) | [2, 4, 5, 6, 1, 3] |
| 4 | 1 | [2, 4, 5, 6, 1, 3] | 6,5,4,2→right | [1, 2, 4, 5, 6, 3] |
| 5 | 3 | [1, 2, 4, 5, 6, 3] | 6,5,4→right | [1, 2, 3, 4, 5, 6] |

Final: `[1, 2, 3, 4, 5, 6]`. Matches `SortAlgorithmsTest.insertionSort_producesSortedOutput`.

### 1.3 Merge sort (divide and merge)

`MergeSort.sort([5, 2, 9, 1])`.

**Divide:**
```
[5, 2, 9, 1]
  -> [5, 2]        [9, 1]
       -> [5] [2]       -> [9] [1]
```

**Merge (bottom-up as recursion unwinds):**

| Merge step | left run | right run | comparisons | result |
|---|---|---|---|---|
| merge([5],[2]) | [5] | [2] | 5>2 | [2, 5] |
| merge([9],[1]) | [9] | [1] | 9>1 | [1, 9] |
| merge([2,5],[1,9]) | [2,5] | [1,9] | 2>1, 2<9, 5<9 | [1, 2, 5, 9] |

Final: `[1, 2, 5, 9]`. Matches `SortAlgorithmsTest.mergeSort_producesSortedOutput`.

### 1.4 Dijkstra shortest path

Graph (undirected, from `GraphAlgorithmsTest.sampleGraph()`):
`A-B(1)`, `A-C(4)`, `B-C(2)`, `B-D(5)`, `C-D(1)`. Source = `A`.

| Step | vertex popped | dist so far | relaxations applied |
|---|---|---|---|
| 1 | A (dist 0) | A=0 | B: 0+1=1 (new). C: 0+4=4 (new) |
| 2 | B (dist 1) | A=0, B=1 | C: 1+2=3 < 4 (improved). D: 1+5=6 (new) |
| 3 | C (dist 3) | A=0, B=1, C=3 | D: 3+1=4 < 6 (improved) |
| 4 | D (dist 4) | A=0, B=1, C=3, D=4 | (no better relaxations) |

Final distances: `A=0, B=1, C=3, D=4`. Path to D: `A → B → C → D` (cost 4,
not the direct-looking `A → C → D` = 5, and not `A → B → D` = 6). Matches
`GraphAlgorithmsTest.dijkstra_findsShortestKnownPathAndCost`.

### 1.5 Kruskal MST (with disjoint-set connectivity trace)

Same graph as §1.4. Edges sorted ascending by weight:
`A-B(1)`, `C-D(1)`, `B-C(2)`, `A-C(4)`, `B-D(5)`.

| Edge considered | find(u) | find(v) | same set? | action | disjoint-set state after |
|---|---|---|---|---|---|
| A-B (1) | A | B | no | **accept** | {A,B}, {C}, {D} |
| C-D (1) | C | D | no | **accept** | {A,B}, {C,D} |
| B-C (2) | A (root of A,B) | C (root of C,D) | no | **accept** | {A,B,C,D} |
| A-C (4) | A | A | yes | **reject** (would cycle) | unchanged |
| B-D (5) | — | — | — | not reached, tree already has n−1=3 edges | — |

MST edges: `A-B, C-D, B-C`, total cost = 1+1+2 = **4**, matching Dijkstra's
A→D distance exactly (expected here, since D's shortest path from A and its
MST connection both route through the same cheap B–C–D chain). Matches
`GraphAlgorithmsTest.mstCost_isCorrectForKnownGraph`.

### 1.6 Dynamic programming (0/1 knapsack)

Small illustrative instance: items `X(w=2,v=3)`, `Y(w=3,v=4)`, `Z(w=4,v=5)`,
capacity = 5. (The full project counterexample — items A/B/C, capacity 50 —
is validated by `GreedyAndDpTest` instead of hand-traced here, since a
51-column table isn't printable.)

`table[i][c]` = best value using the first `i` items within capacity `c`:

| i \ c | 0 | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|---|
| 0 (none) | 0 | 0 | 0 | 0 | 0 | 0 |
| 1 (X, w2v3) | 0 | 0 | 3 | 3 | 3 | 3 |
| 2 (+Y, w3v4) | 0 | 0 | 3 | 4 | 4 | 7 |
| 3 (+Z, w4v5) | 0 | 0 | 3 | 4 | 5 | 7 |

`table[3][5] = 7`. Reconstruction (backtrack from i=3,c=5): `table[3][5]=7 ==
table[2][5]=7` → Z **not** taken, move to i=2. `table[2][5]=7 !=
table[1][5]=3` → Y **taken**, c becomes 5−3=2. `table[1][2]=3 !=
table[0][2]=0` → X **taken**. Selected = `{X, Y}`, value = 3+4 = **7**
(matches: X+Y fits in weight 5 exactly and beats any other combination).

---

## 2. Proof sketches

### 2.1 Loop invariant — Insertion sort (`InsertionSort.java`)

**Invariant:** at the start of every iteration of the outer `for` loop
(index `i`), the subarray `array[0..i-1]` contains the same elements as the
original `array[0..i-1]`, sorted in ascending order.

- **Initialization:** before the first iteration, `i = 1`, so the subarray
  `array[0..0]` is a single element — trivially sorted.
- **Maintenance:** assume `array[0..i-1]` is sorted. The inner `while` loop
  shifts every element in `array[0..i-1]` that is greater than `key =
  array[i]` one position right, then places `key` in the resulting gap. This
  is exactly a sorted-array insertion, so `array[0..i]` is sorted afterward,
  and no element was lost — only shifted. The invariant holds for `i+1`.
- **Termination:** the loop ends when `i = array.length`, so the invariant
  gives us `array[0..length-1]` sorted — i.e. the whole array is sorted,
  which is what correctness requires. ∎

### 2.2 Induction — Merge sort (`MergeSort.java`)

**Claim:** for any subarray of length `n`, `sort(array, low, high)` (with
`n = high - low + 1`) leaves `array[low..high]` sorted.

- **Base case:** `n ≤ 1` (i.e. `low >= high`). A subarray of 0 or 1 elements
  is trivially sorted, and the method returns immediately without modifying
  it. Correct.
- **Inductive step:** assume the claim holds for every subarray of length
  `< n` (strong induction on length). For a subarray of length `n`, the
  method splits it at `mid` into two subarrays of length `⌈n/2⌉` and
  `⌊n/2⌋`, both strictly less than `n` (since `n ≥ 2` here). By the
  inductive hypothesis, the two recursive calls each leave their half
  sorted. The `merge` step then does a single linear pass taking the
  smaller of the two sorted runs' fronts at each step — a standard argument
  shows this produces a sorted result whenever both inputs are sorted
  (proof by the loop invariant "output-so-far is sorted and ≤ every
  remaining element in both runs", same style as §2.1). So the whole
  subarray of length `n` ends up sorted.
- **Conclusion:** by strong induction, `sort` is correct for every subarray
  length, including the full array (`low=0, high=array.length-1`). ∎

### 2.3 Greedy correctness — Kruskal's MST (cut property)

**Claim:** processing edges in ascending weight order and accepting an edge
iff it connects two different components produces a **minimum** spanning
tree, not just *a* spanning tree.

- **Cut property:** for any partition of the vertices into two non-empty
  sets `(S, V−S)`, the minimum-weight edge crossing that cut is part of
  *some* MST. Sketch: suppose an MST `T` doesn't contain the minimum
  crossing edge `e`. Adding `e` to `T` creates exactly one cycle, and that
  cycle must cross the cut at least one more time via some edge `f` (since
  the cycle has to return from `S` to `V−S`). Since `e` is the minimum
  crossing edge, `weight(e) ≤ weight(f)`. Swapping `f` out for `e` produces
  another spanning tree with total weight ≤ `T`'s — so an MST containing
  `e` exists.
- **Applying it to Kruskal:** every edge Kruskal accepts is, at the moment
  it's considered, the minimum-weight edge crossing the cut between "the
  component containing one endpoint" and "everything else" (because all
  lighter edges were already processed and either accepted — merging
  components — or rejected as within-component). By the cut property, that
  edge belongs to some MST. Repeating this argument for every accepted edge
  (an exchange-argument induction over the `n-1` edges added) shows the
  final tree's total weight cannot exceed any other spanning tree's. ∎
- **Where this argument breaks for 0/1 knapsack:** the cut property relies
  on the *matroid* structure of "spanning forests" — locally optimal
  choices never need to be revisited. The knapsack's subsets do **not**
  form a matroid (choosing item A doesn't preserve the exchange property
  against B+C), which is exactly why the greedy heuristic in
  `GreedyKnapsack` provably fails (see the counterexample in §3.1) where
  Kruskal's identical greedy *shape* provably succeeds.

---

## 3. Required counterexamples

### 3.1 Greedy failure (`GreedyKnapsack` vs `Knapsack` DP)

Items: `A(weight=10,value=60)`, `B(weight=20,value=100)`, `C(weight=30,value=120)`,
capacity = 50.

- Greedy (by value/weight ratio, descending: A=6.0, B=5.0, C=4.0) takes
  A then B (10+20=30 ≤ 50), can't fit C (would need 60). **Total value: 160.**
- Optimal (DP, `Knapsack.solve`): B+C (20+30=50 exactly). **Total value: 220.**

Greedy is 27% below optimal and doesn't even use the highest-ratio item's
absence to its advantage — it locks in A first and never reconsiders.
Verified by `GreedyAndDpTest.greedyKnapsack_isProvablySuboptimalOnTheCounterexample`.

### 3.2 Invalid precondition (binary search on unsorted input)

`BinarySearch.search([5, 1, 9, 2, 8, 3], 8)` — the array is **not** sorted
(precondition violated), and 8 is genuinely present at index 4.

Trace: `mid=2` → `array[2]=9 > 8` → search left half (`high=1`). `mid=0` →
`array[0]=5 < 8` → search right half (`low=1`). `mid=1` → `array[1]=1 < 8` →
`low=2 > high=1`, loop ends. **Returns -1 (not found) — a false negative**,
even though `LinearSearch` finds 8 at index 4 on the same array without
issue. This is why `BinarySearch.isSorted(...)` exists as an explicit,
opt-in precondition check — the algorithm itself cannot detect the
violation without giving up its O(log n) guarantee. Verified by
`SearchAlgorithmsTest.binarySearch_onUnsortedInput_canGiveWrongAnswer`.
