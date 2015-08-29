package com.galenframework.reports.nodes;

public abstract class ReportExtra<T> {

    public ReportExtra(T value) {
        setValue(value);
    }

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


    public abstract String getType();
}
