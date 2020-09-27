package com.terrafinosolutions.ac.graphql;

public class TimeseriesRow {

    private Integer date;
    private Double value;

    public TimeseriesRow(Integer date, Double value) {
        this.date = date;
        this.value = value;
    }

    public Integer getDate() {
        return date;
    }

    public Double getValue() {
        return value;
    }
}
