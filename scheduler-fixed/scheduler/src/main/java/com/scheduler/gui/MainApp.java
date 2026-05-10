package com.scheduler.gui;

import com.scheduler.algorithm.RoundRobinScheduler;
import com.scheduler.algorithm.SJFScheduler;
import com.scheduler.metrics.MetricsCalculator;
import com.scheduler.model.GanttEntry;
import com.scheduler.model.Process;
import com.scheduler.model.ProcessMetrics;
import com.scheduler.util.InputValidator;
import com.scheduler.util.ScenarioLoader;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    // ── ألوان ثابتة للـ processes في الـ Gantt ──────────────────────────
    private static final Color[] PROCESS_COLORS = {
        Color.web("#4A90D9"), Color.web("#E67E22"), Color.web("#27AE60"),
        Color.web("#8E44AD"), Color.web("#E74C3C"), Color.web("#16A085"),
        Color.web("#F39C12"), Color.web("#2C3E50")
    };

    // ── حالة البرنامج ────────────────────────────────────────────────────
    private ObservableList<ProcessRow> processRows = FXCollections.observableArrayList();
    private int quantum = 4;

    // ── مكونات الـ UI اللي محتاج أوصلها من أماكن مختلفة ────────────────
    private TextField quantumField;
    private Label     errorLabel;
    private Canvas    rrGanttCanvas;
    private Canvas    sjfGanttCanvas;
    private TableView<MetricRow> rrTable;
    private TableView<MetricRow> sjfTable;
    private Label     rrAvgLabel;
    private Label     sjfAvgLabel;
    private TextArea  comparisonArea;

    // ────────────────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        stage.setTitle("CPU Scheduling — Round Robin vs SJF");

        // الـ Root: ScrollPane يحتوي على VBox رئيسي
        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1E1E2E;");

        root.getChildren().addAll(
            buildTitle(),
            buildScenarioButtons(),
            buildInputSection(),
            buildRunButton(),
            buildErrorLabel(),
            buildGanttSection(),
            buildMetricsSection(),
            buildComparisonSection()
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #1E1E2E; -fx-background-color: #1E1E2E;");

        Scene scene = new Scene(scroll, 1100, 800);
        stage.setScene(scene);
        stage.show();
    }

    // ── العنوان الرئيسي ──────────────────────────────────────────────────
    private Label buildTitle() {
        Label lbl = new Label("⚙  CPU Scheduling Simulator — Round Robin vs SJF");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.web("#CDD6F4"));
        return lbl;
    }

    // ── أزرار السيناريوهات ───────────────────────────────────────────────
    private HBox buildScenarioButtons() {
        Label lbl = new Label("Preset Scenarios:");
        lbl.setTextFill(Color.web("#A6ADC8"));
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Button a = scenarioBtn("A: Mixed",      () -> loadScenario(ScenarioLoader.scenarioA(), 4));
        Button b = scenarioBtn("B: Short Jobs", () -> loadScenario(ScenarioLoader.scenarioB(), 2));
        Button c = scenarioBtn("C: Fairness",   () -> loadScenario(ScenarioLoader.scenarioC(), 4));
        Button d = scenarioBtn("D: Long Job",   () -> loadScenario(ScenarioLoader.scenarioD(), 4));
        Button e = scenarioBtn("E: Validation", () -> loadScenario(ScenarioLoader.scenarioE_invalid(), 4));

        HBox box = new HBox(10, lbl, a, b, c, d, e);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Button scenarioBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #313244; -fx-text-fill: #CDD6F4; " +
                     "-fx-border-color: #585B70; -fx-border-radius: 6; -fx-background-radius: 6;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void loadScenario(List<Process> processes, int q) {
        processRows.clear();
        for (Process p : processes) {
            processRows.add(new ProcessRow(p.getId(),
                String.valueOf(p.getArrivalTime()),
                String.valueOf(p.getBurstTime())));
        }
        quantumField.setText(String.valueOf(q));
        errorLabel.setText("");
    }

    // ── قسم الإدخال ───────────────────────────────────────────────────────
    private VBox buildInputSection() {
        // Quantum
        Label qLbl = new Label("Time Quantum:");
        qLbl.setTextFill(Color.web("#A6ADC8"));

        quantumField = new TextField("4");
        quantumField.setPrefWidth(80);
        styleTextField(quantumField);

        // جدول الـ processes
        TableView<ProcessRow> table = new TableView<>(processRows);
        table.setEditable(true);
        table.setPrefHeight(200);
        table.setStyle("-fx-background-color: #313244; -fx-text-fill: #CDD6F4;");

        table.getColumns().addAll(
            makeCol("Process ID", "id", 120, true),
            makeCol("Arrival Time", "arrival", 130, true),
            makeCol("Burst Time", "burst", 130, true)
        );

        // أزرار Add / Remove / Reset
        Button addBtn    = actionBtn("+ Add Process", () -> processRows.add(new ProcessRow("P" + (processRows.size() + 1), "0", "1")));
        Button removeBtn = actionBtn("− Remove Last",  () -> { if (!processRows.isEmpty()) processRows.remove(processRows.size() - 1); });
        Button resetBtn  = actionBtn("↺ Reset All",    () -> { processRows.clear(); errorLabel.setText(""); clearOutputs(); });

        HBox btnRow = new HBox(10, addBtn, removeBtn, resetBtn);

        HBox quantumRow = new HBox(10, qLbl, quantumField);
        quantumRow.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(10, sectionLabel("📋 Process Input"), quantumRow, table, btnRow);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableColumn<ProcessRow, String> makeCol(String title, String prop, int width, boolean editable) {
        TableColumn<ProcessRow, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        if (editable) {
            col.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(e -> {
                switch (prop) {
                    case "id"      -> e.getRowValue().setId(e.getNewValue());
                    case "arrival" -> e.getRowValue().setArrival(e.getNewValue());
                    case "burst"   -> e.getRowValue().setBurst(e.getNewValue());
                }
            });
        }
        return col;
    }

    // ── زرار Run ─────────────────────────────────────────────────────────
    private Button buildRunButton() {
        Button btn = new Button("▶  Run Simulation");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: #89B4FA; -fx-text-fill: #1E1E2E; " +
                     "-fx-background-radius: 8; -fx-padding: 10 30;");
        btn.setOnAction(e -> runSimulation());
        return btn;
    }

    private Label buildErrorLabel() {
        errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#F38BA8"));
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        return errorLabel;
    }

    // ── تشغيل المحاكاة ────────────────────────────────────────────────────
    private void runSimulation() {
        errorLabel.setText("");

        // تحويل الـ rows لـ Process objects
        List<Process> processes = new ArrayList<>();
        try {
            int q = Integer.parseInt(quantumField.getText().trim());
            quantum = q;
            for (ProcessRow row : processRows) {
                processes.add(new Process(
                    row.getId().trim(),
                    Integer.parseInt(row.getArrival().trim()),
                    Integer.parseInt(row.getBurst().trim())
                ));
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("❌ Error: Be Sure All Values Are Right.");
            return;
        }

        // Validate
        String err = InputValidator.validate(processes, quantum);
        if (err != null) {
            errorLabel.setText("❌ " + err);
            return;
        }

        // ── تشغيل Round Robin ──
        List<GanttEntry> rrGantt = RoundRobinScheduler.schedule(processes, quantum);
        List<ProcessMetrics> rrMetrics = MetricsCalculator.calculate(processes);
        drawGantt(rrGanttCanvas, rrGantt);
        fillMetricsTable(rrTable, rrAvgLabel, rrMetrics);

        // ── تشغيل SJF ──
        List<GanttEntry> sjfGantt = SJFScheduler.schedule(processes);
        List<ProcessMetrics> sjfMetrics = MetricsCalculator.calculate(processes);
        drawGantt(sjfGanttCanvas, sjfGantt);
        fillMetricsTable(sjfTable, sjfAvgLabel, sjfMetrics);

        // ── الخلاصة ──
        buildComparison(rrMetrics, sjfMetrics);
    }

    // ── قسم الـ Gantt Charts ──────────────────────────────────────────────
    private VBox buildGanttSection() {
        rrGanttCanvas  = new Canvas(1050, 90);
        sjfGanttCanvas = new Canvas(1050, 90);

        VBox rrBox  = ganttBox("Gantt Chart — Round Robin", rrGanttCanvas);
        VBox sjfBox = ganttBox("Gantt Chart — SJF (Preemptive)", sjfGanttCanvas);

        VBox section = new VBox(12, sectionLabel("📊 Gantt Charts"), rrBox, sjfBox);
        section.setPadding(new Insets(12));
        section.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        return section;
    }

    private VBox ganttBox(String title, Canvas canvas) {
        Label lbl = new Label(title);
        lbl.setTextFill(Color.web("#89DCEB"));
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ScrollPane sp = new ScrollPane(canvas);
        sp.setPrefHeight(110);
        sp.setStyle("-fx-background: #1E1E2E; -fx-background-color: #1E1E2E;");
        return new VBox(6, lbl, sp);
    }

    private void drawGantt(Canvas canvas, List<GanttEntry> gantt) {
        if (gantt.isEmpty()) return;

        int totalTime = gantt.get(gantt.size() - 1).getEndTime();
        double pixelPerUnit = Math.max(40, 1000.0 / totalTime);
        double canvasWidth  = totalTime * pixelPerUnit + 60;
        canvas.setWidth(canvasWidth);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#1E1E2E"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double y = 10, h = 50;

        // نبني map: processId → color index
        java.util.Map<String, Integer> colorMap = new java.util.HashMap<>();
        int colorIdx = 0;
        for (GanttEntry e : gantt) {
            if (!e.getProcessId().equals("IDLE") && !colorMap.containsKey(e.getProcessId())) {
                colorMap.put(e.getProcessId(), colorIdx++ % PROCESS_COLORS.length);
            }
        }

        for (GanttEntry entry : gantt) {
            double x = entry.getStartTime() * pixelPerUnit;
            double w = (entry.getEndTime() - entry.getStartTime()) * pixelPerUnit;

            Color fill = entry.getProcessId().equals("IDLE")
                ? Color.web("#45475A")
                : PROCESS_COLORS[colorMap.get(entry.getProcessId())];

            gc.setFill(fill);
            gc.fillRoundRect(x + 1, y, w - 2, h, 8, 8);

            // اسم الـ process
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.fillText(entry.getProcessId(), x + w / 2 - 10, y + h / 2 + 5);

            // الأرقام تحت
            gc.setFill(Color.web("#A6ADC8"));
            gc.setFont(Font.font("Arial", 10));
            gc.fillText(String.valueOf(entry.getStartTime()), x, y + h + 14);
        }

        // آخر رقم
        GanttEntry last = gantt.get(gantt.size() - 1);
        GraphicsContext gc2 = canvas.getGraphicsContext2D();
        gc2.setFill(Color.web("#A6ADC8"));
        gc2.setFont(Font.font("Arial", 10));
        gc2.fillText(String.valueOf(last.getEndTime()),
            last.getEndTime() * pixelPerUnit, y + h + 14);
    }

    // ── قسم جداول المقاييس ───────────────────────────────────────────────
    private HBox buildMetricsSection() {
        rrTable   = makeMetricsTable();
        sjfTable  = makeMetricsTable();
        rrAvgLabel  = avgLabel();
        sjfAvgLabel = avgLabel();

        VBox rrBox  = new VBox(6, subLabel("Round Robin — Metrics"), rrTable, rrAvgLabel);
        VBox sjfBox = new VBox(6, subLabel("SJF — Metrics"),         sjfTable, sjfAvgLabel);

        HBox section = new HBox(20, rrBox, sjfBox);
        section.setPadding(new Insets(12));
        section.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        HBox.setHgrow(rrBox, Priority.ALWAYS);
        HBox.setHgrow(sjfBox, Priority.ALWAYS);
        return section;
    }

    @SuppressWarnings("unchecked")
    private TableView<MetricRow> makeMetricsTable() {
        TableView<MetricRow> table = new TableView<>();
        table.setPrefHeight(180);
        table.setStyle("-fx-background-color: #1E1E2E;");

        TableColumn<MetricRow, String> idCol  = new TableColumn<>("Process");
        TableColumn<MetricRow, String> wtCol  = new TableColumn<>("WT");
        TableColumn<MetricRow, String> tatCol = new TableColumn<>("TAT");
        TableColumn<MetricRow, String> rtCol  = new TableColumn<>("RT");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        wtCol.setCellValueFactory(new PropertyValueFactory<>("wt"));
        tatCol.setCellValueFactory(new PropertyValueFactory<>("tat"));
        rtCol.setCellValueFactory(new PropertyValueFactory<>("rt"));

        for (TableColumn<MetricRow,String> col : List.of(idCol, wtCol, tatCol, rtCol))
            col.setPrefWidth(100);

        table.getColumns().addAll(idCol, wtCol, tatCol, rtCol);
        return table;
    }

    private void fillMetricsTable(TableView<MetricRow> table, Label avgLbl,
                                   List<ProcessMetrics> metrics) {
        ObservableList<MetricRow> rows = FXCollections.observableArrayList();
        for (ProcessMetrics m : metrics) {
            rows.add(new MetricRow(m.getProcessId(),
                String.valueOf(m.getWaitingTime()),
                String.valueOf(m.getTurnaroundTime()),
                String.valueOf(m.getResponseTime())));
        }
        table.setItems(rows);

        avgLbl.setText(String.format(
            "Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f",
            MetricsCalculator.avgWT(metrics),
            MetricsCalculator.avgTAT(metrics),
            MetricsCalculator.avgRT(metrics)));
    }

    // ── قسم المقارنة والخلاصة ────────────────────────────────────────────
    private VBox buildComparisonSection() {
        comparisonArea = new TextArea();
        comparisonArea.setEditable(false);
        comparisonArea.setPrefHeight(200);
        comparisonArea.setStyle("-fx-control-inner-background: #1E1E2E; -fx-text-fill: #CDD6F4; " +
                                "-fx-font-family: monospace; -fx-font-size: 13;");
        comparisonArea.setText("Press  Run Simulation To Veiw the Comparison.");

        VBox box = new VBox(8, sectionLabel("📝 Comparison Summary & Conclusion"), comparisonArea);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");
        return box;
    }

    private void buildComparison(List<ProcessMetrics> rr, List<ProcessMetrics> sjf) {
        double rrWT  = MetricsCalculator.avgWT(rr);
        double sjfWT = MetricsCalculator.avgWT(sjf);
        double rrTAT = MetricsCalculator.avgTAT(rr);
        double sjfTAT= MetricsCalculator.avgTAT(sjf);
        double rrRT  = MetricsCalculator.avgRT(rr);
        double sjfRT = MetricsCalculator.avgRT(sjf);

        String better_wt  = rrWT  <= sjfWT  ? "Round Robin" : "SJF";
        String better_tat = rrTAT <= sjfTAT ? "Round Robin" : "SJF";
        String better_rt  = rrRT  <= sjfRT  ? "Round Robin" : "SJF";

        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════\n");
        sb.append("               COMPARISON SUMMARY\n");
        sb.append("══════════════════════════════════════════════\n\n");
        sb.append(String.format("  Avg WT   →  RR: %.2f   |   SJF: %.2f   →  Better: %s\n", rrWT,  sjfWT,  better_wt));
        sb.append(String.format("  Avg TAT  →  RR: %.2f   |   SJF: %.2f   →  Better: %s\n", rrTAT, sjfTAT, better_tat));
        sb.append(String.format("  Avg RT   →  RR: %.2f   |   SJF: %.2f   →  Better: %s\n", rrRT,  sjfRT,  better_rt));
        sb.append("\n──────────────────────────────────────────────\n");
        sb.append("  CONCLUSION\n");
        sb.append("──────────────────────────────────────────────\n");
        sb.append("  • Round Robin distributes CPU time fairly across all processes.\n");
        sb.append("  • SJF (Preemptive) favors shorter jobs → lower avg WT & TAT.\n");
        sb.append("  • Long processes suffer in SJF (starvation risk).\n");
        sb.append(String.format("  • Quantum = %d affects RR response time directly.\n", quantum));
        sb.append("    A smaller quantum → fairer but more context switches.\n");
        sb.append("══════════════════════════════════════════════\n");

        comparisonArea.setText(sb.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void clearOutputs() {
        GraphicsContext gc1 = rrGanttCanvas.getGraphicsContext2D();
        gc1.clearRect(0, 0, rrGanttCanvas.getWidth(), rrGanttCanvas.getHeight());
        GraphicsContext gc2 = sjfGanttCanvas.getGraphicsContext2D();
        gc2.clearRect(0, 0, sjfGanttCanvas.getWidth(), sjfGanttCanvas.getHeight());
        if (rrTable  != null) rrTable.getItems().clear();
        if (sjfTable != null) sjfTable.getItems().clear();
        if (rrAvgLabel  != null) rrAvgLabel.setText("");
        if (sjfAvgLabel != null) sjfAvgLabel.setText("");
        if (comparisonArea != null) comparisonArea.setText("Press Run The Simulation To Veiw The Comparison.");
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web("#CDD6F4"));
        return lbl;
    }

    private Label subLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#89DCEB"));
        return lbl;
    }

    private Label avgLabel() {
        Label lbl = new Label("");
        lbl.setTextFill(Color.web("#A6E3A1"));
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        return lbl;
    }

    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-background-color: #1E1E2E; -fx-text-fill: #CDD6F4; " +
                    "-fx-border-color: #585B70; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private Button actionBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #45475A; -fx-text-fill: #CDD6F4; " +
                     "-fx-background-radius: 6;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    public static void main(String[] args) { launch(args); }
}
