# CPU Scheduling Simulator — Round Robin vs SJF

> Operating Systems Course — Part 1 Comparison Project

## Overview
A JavaFX desktop application that simulates and compares two CPU scheduling algorithms:
- **Round Robin (RR)** — time-sliced, fair, rotating queue
- **SJF Preemptive (SRTF)** — shortest remaining time first

## Features
- Dynamic process input (add / remove / edit)
- Time Quantum input with validation
- Preset buttons for all 5 required test scenarios
- Gantt Chart visualization for both algorithms
- Metrics table: WT, TAT, RT per process + averages
- Comparison summary and conclusion panel
- Input validation with error messages
- Reset button

## Requirements
- Java 17+
- Maven 3.8+
- JavaFX 21 (handled by Maven)

## How to Run
```bash
git clone <your-repo-url>
cd cpu-scheduler
mvn javafx:run
```

## Project Structure
```
src/main/java/com/scheduler/
├── model/
│   ├── Process.java
│   ├── GanttEntry.java
│   └── ProcessMetrics.java
├── algorithm/
│   ├── RoundRobinScheduler.java
│   └── SJFScheduler.java
├── metrics/
│   └── MetricsCalculator.java
├── gui/
│   ├── MainApp.java
│   ├── ProcessRow.java
│   └── MetricRow.java
└── util/
    ├── InputValidator.java
    └── ScenarioLoader.java
```

## Test Scenarios

| Scenario | Description | Quantum |
|----------|-------------|---------|
| A | Basic mixed workload | 4 |
| B | Short-job-heavy | 2 |
| C | Fairness — equal burst times | 4 |
| D | Long-job sensitivity | 4 |
| E | Validation — duplicate ID | 4 |

## Team
| # | Name                                |     ID  |  
| 1 |YOUSEF OSAMA ZAHRAN                  |20241148 |
| 2 |YOUSEF SHERIF ABDELMAWGOD            | 20241163 |
| 3 | WALEED ASHRAF GOMAA|                | 20241107 |
| 4 | MOHAMED TAMER MOHAMED ADEL          | 20240809 |
| 5 | MOHAMMED HUSEN MOHAMED              | 20220392 |
|6|  ABDRAHMAN ASHRAF ABDELRAZIK           | 20240525 |


## Analysis Questions
1. Which algorithm gave lower average waiting time?
2. Which gave lower average response time?
3. Did Round Robin appear fairer across all processes?
4. Did SJF complete short jobs more efficiently?
5. How did the chosen quantum affect Round Robin behavior?
6. Which algorithm is recommended for the tested workload, and why?
 
