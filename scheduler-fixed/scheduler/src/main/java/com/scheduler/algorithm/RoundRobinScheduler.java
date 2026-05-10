package com.scheduler.algorithm;

import com.scheduler.model.GanttEntry;
import com.scheduler.model.Process;

import java.util.*;

public class RoundRobinScheduler {

    public static List<GanttEntry> schedule(List<Process> processes, int quantum) {
        List<GanttEntry> gantt = new ArrayList<>();

        // reset كل process
        for (Process p : processes) p.reset();

        // ترتيب حسب arrivalTime
        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        int time = 0;
        int idx  = 0; // اللي جاي من الـ sorted list

        // نضيف اللي وصلوا عند t=0
        while (idx < sorted.size() && sorted.get(idx).getArrivalTime() <= time) {
            readyQueue.add(sorted.get(idx++));
        }

        while (!readyQueue.isEmpty() || idx < sorted.size()) {

            if (readyQueue.isEmpty()) {
                // CPU فاضية — نقفز للـ process الجاية
                int nextArrival = sorted.get(idx).getArrivalTime();
                gantt.add(new GanttEntry("IDLE", time, nextArrival));
                time = nextArrival;
                while (idx < sorted.size() && sorted.get(idx).getArrivalTime() <= time) {
                    readyQueue.add(sorted.get(idx++));
                }
                continue;
            }

            Process current = readyQueue.poll();

            // أول مرة تشتغل؟ نسجل startTime
            if (!current.isStarted()) {
                current.setStartTime(time);
                current.setStarted(true);
            }

            int runTime = Math.min(quantum, current.getRemainingTime());
            gantt.add(new GanttEntry(current.getId(), time, time + runTime));
            current.setRemainingTime(current.getRemainingTime() - runTime);
            time += runTime;

            // نضيف اللي وصلوا خلال الـ quantum ده
            while (idx < sorted.size() && sorted.get(idx).getArrivalTime() <= time) {
                readyQueue.add(sorted.get(idx++));
            }

            if (current.getRemainingTime() > 0) {
                // لسه مخلصتش — ترجع آخر الطابور
                readyQueue.add(current);
            } else {
                current.setFinishTime(time);
            }
        }

        return gantt;
    }
}
