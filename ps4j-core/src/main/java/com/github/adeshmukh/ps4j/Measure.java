package com.github.adeshmukh.ps4j;

/**
 * Basic interface for measured quantities.
 *
 * @author adeshmukh
 * @param <V>
 */
public interface Measure<V extends Comparable<V>> extends Comparable<Measure<V>> {

    public V getValue();

    public String getDisplayValue();

    Metric<V> getMetric();

}
