# Dataset Evidence Note

## How this dataset was built

All four seed CSVs (`locations.csv`, `roads.csv`, `resources.csv`,
`service_requests.csv`) are produced by
[`DatasetGenerator`](../src/main/java/gh/edu/campushub/tools/DatasetGenerator.java),
run with:

```
mvn -q compile
java -cp target/classes gh.edu.campushub.tools.DatasetGenerator
```

No manual data entry was used, and no real individual's personal data
appears anywhere in the dataset — every location name is a real, public
landmark on the University of Ghana, Legon campus (halls, departments,
libraries, shuttle stops, etc.); every road, resource and service request is
a fabricated record.

## Reproducibility & index-number derivation

Generation is driven by a single seeded `java.util.Random`, seeded from
[`TeamConfig.RANDOM_SEED`](../src/main/java/gh/edu/campushub/config/TeamConfig.java),
which is itself derived from the 14 team members' index numbers:

- `TEAM_DIGIT_SUM` = sum of every digit across all 14 index numbers = **416**
- `TEAM_TAIL_SUM` = sum of the last 3 digits of each index number = **5582**
- `RANDOM_SEED` = `TEAM_DIGIT_SUM * 1_000_003 + TEAM_TAIL_SUM` = **416006830**

Running the generator again with the same source (`TeamConfig.INDEX_NUMBERS`)
reproduces an identical dataset byte-for-byte. The same two sums also drive
three algorithm parameters used throughout the engines (`PRIORITY_WEIGHT`,
`ROUTE_PENALTY_FACTOR`, `HASH_TABLE_DEFAULT_CAPACITY`) — see `TeamConfig` for
the exact formulas.

## Record counts (meets PRD Section 7 minimums)

| Entity | Records | Minimum required |
|---|---|---|
| Locations | 50 | 50 |
| Roads | 110 | 100 |
| Service requests | 300 | 300 |
| Resources | 30 | 30 |
| Algorithm runs | populated live by the M10 performance lab (`algorithm_runs` table) | 30 |

## Data-quality choices worth noting

- **Location type matches name**: each of the 50 curated location names is
  paired 1:1 with a semantically correct `location_type` (e.g. "UG Legon
  Hospital" → `Health`), not assigned at random.
- **Connectivity guaranteed**: roads are generated as a random spanning tree
  over all 50 locations first (49 edges, guaranteeing every location is
  reachable from every other one — required for BFS/DFS/Dijkstra/MST
  evidence), then topped up with random extra edges to 110 total for
  realistic path redundancy. No duplicate road pairs.
- **Urgency distribution is skewed, not uniform**: 35% low, 30% medium-low,
  20% medium, 10% high, 5% critical — mirrors a real dispatch queue where
  most tickets aren't emergencies, which matters for the priority-queue and
  greedy/DP demonstrations.
- **Deadlines scale with urgency**: a critical (urgency 5) request gets a
  30–60 minute window; a routine (urgency 1) request gets 8–16 hours —
  avoids a dataset where the priority queue and FIFO queue would produce
  suspiciously identical orderings.
