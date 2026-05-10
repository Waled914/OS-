package com.scheduler.util;

import com.scheduler.model.Process;
import java.util.Arrays;
import java.util.List;

public class ScenarioLoader {

    // Scenario A: Basic mixed workload
    public static List<Process> scenarioA() {
        return Arrays.asList(
            new Process("P1", 0, 10),
            new Process("P2", 1, 4),
            new Process("P3", 2, 6),
            new Process("P4", 3, 3),
            new Process("P5", 4, 8)
        );
    }

    // Scenario B: Short-job-heavy
    public static List<Process> scenarioB() {
        return Arrays.asList(
            new Process("P1", 0, 2),
            new Process("P2", 0, 1),
            new Process("P3", 1, 3),
            new Process("P4", 1, 2),
            new Process("P5", 2, 1)
        );
    }

    // Scenario C: Fairness — many processes with similar burst
    public static List<Process> scenarioC() {
        return Arrays.asList(
            new Process("P1", 0, 6),
            new Process("P2", 0, 6),
            new Process("P3", 0, 6),
            new Process("P4", 0, 6),
            new Process("P5", 0, 6)
        );
    }

    // Scenario D: Long-job sensitivity
    public static List<Process> scenarioD() {
        return Arrays.asList(
            new Process("P1", 0, 30),
            new Process("P2", 1, 3),
            new Process("P3", 2, 4),
            new Process("P4", 3, 2)
        );
    }

    // Scenario E: Validation — invalid input (returned as null to trigger error)
    public static List<Process> scenarioE_invalid() {
        return Arrays.asList(
            new Process("P1", 0, 5),
            new Process("P1", 1, 3)   // ID مكرر — هيطلع error
        );
    }

    public static String scenarioEDescription() {
        return "Scenario E: يحتوي على ID مكرر (P1) — لاختبار الـ Validation";
    }

    public static int defaultQuantum() { return 4; }
}
