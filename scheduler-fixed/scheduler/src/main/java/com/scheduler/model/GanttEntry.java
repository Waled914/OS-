package com.scheduler.model;

/**
 * GanttEntry — بيمثل خانة واحدة في الـ Gantt Chart
 *
 * مثال: لو P1 اشتغلت من t=0 لـ t=4
 *   → processId = "P1"
 *   → startTime = 0
 *   → endTime   = 4
 *
 * لو الـ CPU كانت فاضية (idle) من t=2 لـ t=5
 *   → processId = "IDLE"
 */
public class GanttEntry {

    private String processId;   // اسم الـ process أو "IDLE"
    private int    startTime;
    private int    endTime;

    public GanttEntry(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime   = endTime;
    }

    public String getProcessId() { return processId; }
    public int    getStartTime() { return startTime; }
    public int    getEndTime()   { return endTime; }

    @Override
    public String toString() {
        return "[" + processId + " | " + startTime + " → " + endTime + "]";
    }
}
