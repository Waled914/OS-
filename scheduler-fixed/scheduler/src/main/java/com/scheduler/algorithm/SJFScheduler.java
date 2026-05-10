package com.scheduler.algorithm;

import com.scheduler.model.GanttEntry;
import com.scheduler.model.Process;

import java.util.*;

public class SJFScheduler {

    public static List<GanttEntry> schedule(List<Process> processes) {
        List<GanttEntry> gantt = new ArrayList<>();

        for (Process p : processes) p.reset();

        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::getArrivalTime));

        // PriorityQueue ترتب حسب remainingTime (الأقصر أول)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt(Process::getRemainingTime)
        );

        int time = 0;
        int idx  = 0;
        Process current = null;

        // إجمالي وقت الـ burst للحساب
        int totalBurst = 0;
        for (Process p : processes) totalBurst += p.getBurstTime();

        while (idx < sorted.size() || !readyQueue.isEmpty() || current != null) {

            // أضف كل اللي وصلوا حتى الوقت الحالي
            while (idx < sorted.size() && sorted.get(idx).getArrivalTime() <= time) {
                readyQueue.add(sorted.get(idx++));
            }

            // لو مفيش حاجة خالص
            if (readyQueue.isEmpty() && current == null) {
                if (idx < sorted.size()) {
                    int nextArrival = sorted.get(idx).getArrivalTime();
                    gantt.add(new GanttEntry("IDLE", time, nextArrival));
                    time = nextArrival;
                }
                continue;
            }

            // اختار أقصر process متاحة
            if (current == null) {
                current = readyQueue.poll();
            } else {
                // لو في الـ queue حاجة أقصر من الـ current → preempt
                if (!readyQueue.isEmpty() &&
                    readyQueue.peek().getRemainingTime() < current.getRemainingTime()) {
                    readyQueue.add(current);
                    current = readyQueue.poll();
                }
            }

            // سجل startTime لو أول مرة
            if (!current.isStarted()) {
                current.setStartTime(time);
                current.setStarted(true);
            }

            // شغّل لمدة وحدة زمن واحدة (preemptive → unit-step)
            int runUntil = time + 1;

            // لو في process هتوصل قبل ما تخلص → وقف عندها
            if (idx < sorted.size()) {
                int nextArrival = sorted.get(idx).getArrivalTime();
                if (nextArrival <= current.getRemainingTime() + time) {
                    runUntil = Math.min(runUntil, nextArrival);
                }
            }

            int delta = runUntil - time;
            current.setRemainingTime(current.getRemainingTime() - delta);

            // دمج في الـ gantt لو نفس الـ process
            if (!gantt.isEmpty() &&
                gantt.get(gantt.size() - 1).getProcessId().equals(current.getId()) &&
                gantt.get(gantt.size() - 1).getEndTime() == time) {
                GanttEntry last = gantt.remove(gantt.size() - 1);
                gantt.add(new GanttEntry(last.getProcessId(), last.getStartTime(), runUntil));
            } else {
                gantt.add(new GanttEntry(current.getId(), time, runUntil));
            }

            time = runUntil;

            if (current.getRemainingTime() == 0) {
                current.setFinishTime(time);
                current = null;
            }
        }

        return gantt;
    }
}
