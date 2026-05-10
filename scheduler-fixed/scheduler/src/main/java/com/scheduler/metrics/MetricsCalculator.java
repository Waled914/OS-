package com.scheduler.metrics;

import com.scheduler.model.Process;
import com.scheduler.model.ProcessMetrics;

import java.util.ArrayList;
import java.util.List;

public class MetricsCalculator {

    public static List<ProcessMetrics> calculate(List<Process> processes) {
        List<ProcessMetrics> result = new ArrayList<>();

        for (Process p : processes) {
            int tat = p.getFinishTime()  - p.getArrivalTime();   // TAT = finish - arrival
            int wt  = tat - p.getBurstTime();                     // WT  = TAT - burst
            int rt  = p.getStartTime()   - p.getArrivalTime();   // RT  = start - arrival
            result.add(new ProcessMetrics(p.getId(), wt, tat, rt));
        }

        return result;
    }

    public static double avgWT(List<ProcessMetrics> metrics) {
        return metrics.stream().mapToInt(ProcessMetrics::getWaitingTime).average().orElse(0);
    }

    public static double avgTAT(List<ProcessMetrics> metrics) {
        return metrics.stream().mapToInt(ProcessMetrics::getTurnaroundTime).average().orElse(0);
    }

    public static double avgRT(List<ProcessMetrics> metrics) {
        return metrics.stream().mapToInt(ProcessMetrics::getResponseTime).average().orElse(0);
    }
}
