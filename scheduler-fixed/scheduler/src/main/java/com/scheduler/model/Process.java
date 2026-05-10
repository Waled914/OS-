package com.scheduler.model;

/**
 * كلاس Process — بيمثل أي عملية (process) هتدخلها المستخدم
 *
 * كل process عندها:
 *   - id         : اسمها زي P1, P2
 *   - arrivalTime: امتى وصلت للـ CPU
 *   - burstTime  : محتاجة كام وحدة وقت عشان تخلص
 *   - remainingTime: فضل منها كام (بتتغير أثناء التنفيذ في RR)
 *   - startTime  : أول ما الـ CPU بدأت تشتغل عليها
 *   - finishTime : امتى خلصت تماماً
 */
public class Process {

    // ── البيانات الأساسية اللي بيدخلها المستخدم ──────────────────────────
    private String id;
    private int arrivalTime;
    private int burstTime;

    // ── بيانات بتتحسب أثناء تشغيل الخوارزمية ────────────────────────────
    private int remainingTime;   // نفس burstTime في البداية، بتقل مع كل quantum
    private int startTime;       // أول مرة الـ CPU تشتغل على الـ process دي
    private int finishTime;      // الوقت اللي خلصت فيه تماماً
    private boolean started;     // flag: هل بدأت أصلاً ولا لسه

    // ── Constructor: بييجي عند إنشاء process جديدة ────────────────────────
    public Process(String id, int arrivalTime, int burstTime) {
        this.id            = id;
        this.arrivalTime   = arrivalTime;
        this.burstTime     = burstTime;
        this.remainingTime = burstTime;  // في الأول remainingTime = burstTime كامل
        this.startTime     = -1;         // -1 معناها "لسه مبدأتش"
        this.finishTime    = -1;
        this.started       = false;
    }

    /**
     * reset() — بترجع الـ process لحالتها الأصلية
     * محتاجينها عشان نشغّل الخوارزمية التانية على نفس الـ processes
     */
    public void reset() {
        this.remainingTime = this.burstTime;
        this.startTime     = -1;
        this.finishTime    = -1;
        this.started       = false;
    }

    // ── Getters and Setters ───────────────────────────────────────────────

    public String getId()                    { return id; }
    public int    getArrivalTime()           { return arrivalTime; }
    public int    getBurstTime()             { return burstTime; }

    public int    getRemainingTime()         { return remainingTime; }
    public void   setRemainingTime(int t)    { this.remainingTime = t; }

    public int    getStartTime()             { return startTime; }
    public void   setStartTime(int t)        { this.startTime = t; }

    public int    getFinishTime()            { return finishTime; }
    public void   setFinishTime(int t)       { this.finishTime = t; }

    public boolean isStarted()               { return started; }
    public void    setStarted(boolean s)     { this.started = s; }

    // ── toString: بيساعد في الـ debugging ───────────────────────────────
    @Override
    public String toString() {
        return "Process{id='" + id + "', arrival=" + arrivalTime
             + ", burst=" + burstTime + "}";
    }
}
