package com.scheduler.gui;

import javafx.beans.property.SimpleStringProperty;

/**
 * MetricRow — صف في جداول النتائج (WT, TAT, RT)
 */
public class MetricRow {
    private SimpleStringProperty id;
    private SimpleStringProperty wt;
    private SimpleStringProperty tat;
    private SimpleStringProperty rt;

    public MetricRow(String id, String wt, String tat, String rt) {
        this.id  = new SimpleStringProperty(id);
        this.wt  = new SimpleStringProperty(wt);
        this.tat = new SimpleStringProperty(tat);
        this.rt  = new SimpleStringProperty(rt);
    }

    public String getId()  { return id.get(); }
    public String getWt()  { return wt.get(); }
    public String getTat() { return tat.get(); }
    public String getRt()  { return rt.get(); }

    public SimpleStringProperty idProperty()  { return id; }
    public SimpleStringProperty wtProperty()  { return wt; }
    public SimpleStringProperty tatProperty() { return tat; }
    public SimpleStringProperty rtProperty()  { return rt; }
}
