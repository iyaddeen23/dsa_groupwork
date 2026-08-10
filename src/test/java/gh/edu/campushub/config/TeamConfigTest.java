package gh.edu.campushub.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamConfigTest {

    @Test
    void fourteenIndexNumbersAreConfigured() {
        assertEquals(14, TeamConfig.INDEX_NUMBERS.size());
    }

    @Test
    void derivedDigitSumMatchesManualCalculation() {
        long expected = 0;
        for (String index : TeamConfig.INDEX_NUMBERS) {
            for (char c : index.toCharArray()) {
                expected += (c - '0');
            }
        }
        assertEquals(expected, TeamConfig.TEAM_DIGIT_SUM);
    }

    @Test
    void priorityWeight_isWithinDocumentedRange() {
        assertTrue(TeamConfig.PRIORITY_WEIGHT >= 1 && TeamConfig.PRIORITY_WEIGHT <= 10);
    }

    @Test
    void routePenaltyFactor_isWithinDocumentedRange() {
        assertTrue(TeamConfig.ROUTE_PENALTY_FACTOR >= 1.00 && TeamConfig.ROUTE_PENALTY_FACTOR < 1.25);
    }

    @Test
    void hashTableDefaultCapacity_isPrime() {
        int n = TeamConfig.HASH_TABLE_DEFAULT_CAPACITY;
        assertTrue(n >= 31);
        boolean prime = n > 1;
        for (int i = 2; i * i <= n && prime; i++) {
            if (n % i == 0) prime = false;
        }
        assertTrue(prime, n + " must be prime");
    }
}
