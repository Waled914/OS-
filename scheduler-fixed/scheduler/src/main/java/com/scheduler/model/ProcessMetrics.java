package com.scheduler.model;

/**
 * ProcessMetrics — بيحفظ النتائج المحسوبة لكل process
 *
 * المعادلات:
 *   TAT (Turnaround Time) = finishTime - arrivalTime
 *   WT  (Waiting Time)    = TAT - burstTime
 *   RT  (Response Time)   = startTime  - arrivalTime
 */
public class ProcessMetrics {

    private String processId;
    private int    waitingTime;      // WT
    private int    turnaroundTime;   // TAT
    private int    responseTime;     // RT

    public ProcessMetrics(String processId, int wt, int tat, int rt) {
        this.processId      = processId;
        this.waitingTime    = wt;
        this.turnaroundTime = tat;
        this.responseTime   = rt;
    }

    public String getProcessId()      { return processId; }
    public int    getWaitingTime()    { return waitingTime; }
    public int    getTurnaroundTime() { return turnaroundTime; }
    public int    getResponseTime()   { return responseTime; }
}
