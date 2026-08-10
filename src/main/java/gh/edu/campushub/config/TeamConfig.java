package gh.edu.campushub.config;

import java.util.Collections;
import java.util.List;

/**
 * Central home for the team's index numbers and the algorithm parameters
 * derived from them (PRD Section 7: "at least 3 algorithm parameters ...
 * must be derived from team members' index numbers").
 *
 * Every derived value below is computed from {@link #INDEX_NUMBERS} by a
 * documented formula rather than hand-copied, so the whole chain is
 * auditable and re-derives itself correctly if a member's number changes.
 */
public final class TeamConfig {

    /** Team members' index numbers, one per student. */
    public static final List<String> INDEX_NUMBERS = Collections.unmodifiableList(List.of(
            "22069364", // Iyad
            "22382269", // Paa Kweku Tawiah
            "11365448", // Anu
            "22012722", // Barbara
            "22033196", // Maame
            "22378009", // Thelma
            "22414967", // Augustina
            "22378080", // Victor
            "22368253", // Rachel Nhyira
            "22300477", // Safo Ankomah
            "22393457", // Kwadwo Darko
            "22328509", // Perrita N.A. Safo
            "22264044", // Denteh Doreen
            "22033787"  // Chris Larbi
    ));

    /** Sum of every digit of every index number. The root seed all other parameters derive from. */
    public static final long TEAM_DIGIT_SUM = computeDigitSum();

    /** Sum of the last three digits of each index number — a second, independent mix of the same source data. */
    public static final long TEAM_TAIL_SUM = computeTailSum();

    // ---- Derived algorithm parameters -------------------------------------------------

    /**
     * Priority-queue urgency multiplier used by the dispatch engine (M5) when
     * combining request urgency with wait time into a single dispatch score.
     * Formula: 1 + (TEAM_DIGIT_SUM mod 10), so it always lands in [1, 10].
     */
    public static final int PRIORITY_WEIGHT = 1 + (int) (TEAM_DIGIT_SUM % 10);

    /**
     * Route-penalty multiplier applied to a road's condition weight in the
     * graph engine (M7) when scoring edges for Dijkstra/Prim/Kruskal.
     * Formula: 1.00 + (TEAM_TAIL_SUM mod 25) / 100.0, landing in [1.00, 1.24].
     */
    public static final double ROUTE_PENALTY_FACTOR = 1.0 + (TEAM_TAIL_SUM % 25) / 100.0;

    /**
     * Default initial capacity for the custom hash table (M3/M6).
     * Formula: nextPrime(31 + (TEAM_DIGIT_SUM mod 64)) — a prime table size
     * derived from the seed, kept in a sane range for a starting capacity.
     */
    public static final int HASH_TABLE_DEFAULT_CAPACITY = nextPrime(31 + (int) (TEAM_DIGIT_SUM % 64));

    /**
     * Seed for every reproducible-random operation in the project (dataset
     * generation, shuffle-based benchmarks). Same seed in, same dataset out.
     */
    public static final long RANDOM_SEED = TEAM_DIGIT_SUM * 1_000_003L + TEAM_TAIL_SUM;

    private TeamConfig() {
    }

    private static long computeDigitSum() {
        long total = 0;
        for (String index : INDEX_NUMBERS) {
            for (char c : index.toCharArray()) {
                if (Character.isDigit(c)) {
                    total += (c - '0');
                }
            }
        }
        return total;
    }

    private static long computeTailSum() {
        long total = 0;
        for (String index : INDEX_NUMBERS) {
            String tail = index.substring(Math.max(0, index.length() - 3));
            total += Long.parseLong(tail);
        }
        return total;
    }

    private static int nextPrime(int from) {
        int candidate = Math.max(2, from);
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n % 2 == 0) {
            return n == 2;
        }
        for (int i = 3; (long) i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
