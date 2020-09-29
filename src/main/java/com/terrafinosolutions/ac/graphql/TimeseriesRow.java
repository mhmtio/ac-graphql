package com.terrafinosolutions.ac.graphql;

public class TimeseriesRow {

    private Integer date;
    private Double rate;

    public TimeseriesRow(Integer date, Double rate) {
        this.date = date;
        this.rate = rate;
    }

    public Integer getDate() {
        return date;
    }

    public Double getRate() {
        return rate;
    }
}
