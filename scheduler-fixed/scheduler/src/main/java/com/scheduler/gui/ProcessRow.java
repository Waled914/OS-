package com.scheduler.gui;

import javafx.beans.property.SimpleStringProperty;

/**
 * ProcessRow — بيمثل صف واحد في جدول الإدخال في الـ UI
 * JavaFX TableView محتاج كل column تبقى SimpleStringProperty
 */
public class ProcessRow {
    private SimpleStringProperty id;
    private SimpleStringProperty arrival;
    private SimpleStringProperty burst;

    public ProcessRow(String id, String arrival, String burst) {
        this.id      = new SimpleStringProperty(id);
        this.arrival = new SimpleStringProperty(arrival);
        this.burst   = new SimpleStringProperty(burst);
    }

    public String getId()      { return id.get(); }
    public String getArrival() { return arrival.get(); }
    public String getBurst()   { return burst.get(); }

    public void setId(String v)      { id.set(v); }
    public void setArrival(String v) { arrival.set(v); }
    public void setBurst(String v)   { burst.set(v); }

    public SimpleStringProperty idProperty()      { return id; }
    public SimpleStringProperty arrivalProperty() { return arrival; }
    public SimpleStringProperty burstProperty()   { return burst; }
}
