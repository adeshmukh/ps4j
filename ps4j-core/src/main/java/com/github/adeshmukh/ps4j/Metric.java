package com.github.adeshmukh.ps4j;

public interface Metric<V extends Comparable<V>> {

    public String getName();

    public String getDescription();

    public Measure<V> newMeasure(V val);
}
