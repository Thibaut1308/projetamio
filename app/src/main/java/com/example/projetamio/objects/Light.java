package com.example.projetamio.objects;

public class Light {

    private String label;
    private double value;
    private long timestamp;
    private String mote;

    public Light(String label, double value, long timestamp, String mote) {
        this.label = label;
        this.value = value;
        this.timestamp = timestamp;
        this.mote = mote;
    }

    public Light() {
        this.label = "";
        this.value = -1;
        this.timestamp = -1;
        this.mote = "";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMote() {
        return mote;
    }

    public void setMote(String mote) {
        this.mote = mote;
    }
}
