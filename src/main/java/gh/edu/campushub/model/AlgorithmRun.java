package gh.edu.campushub.model;

import java.time.LocalDateTime;

/** One recorded timing/memory measurement from the performance experiment lab (M10). */
public class AlgorithmRun {
    private final String runId;
    private final String algorithmName;
    private final int inputSize;
    private final double executionTimeMs;
    private final Double memoryKb; // nullable — not every experiment measures memory
    private final LocalDateTime runAt;

    public AlgorithmRun(String runId, String algorithmName, int inputSize,
                         double executionTimeMs, Double memoryKb, LocalDateTime runAt) {
        this.runId = runId;
        this.algorithmName = algorithmName;
        this.inputSize = inputSize;
        this.executionTimeMs = executionTimeMs;
        this.memoryKb = memoryKb;
        this.runAt = runAt;
    }

    public String getRunId() { return runId; }
    public String getAlgorithmName() { return algorithmName; }
    public int getInputSize() { return inputSize; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public Double getMemoryKb() { return memoryKb; }
    public LocalDateTime getRunAt() { return runAt; }

    @Override
    public String toString() {
        return String.format("AlgorithmRun{%s, %s, n=%d, %.4fms, %s}",
                runId, algorithmName, inputSize, executionTimeMs, runAt);
    }
}
