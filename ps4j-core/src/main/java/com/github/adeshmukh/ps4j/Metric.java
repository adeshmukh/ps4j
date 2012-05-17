package com.github.adeshmukh.ps4j;

/**
 * Metric is an attribute of a VM that can be measured by Ps4j.
 * It also serves as a factory for creating instances of {@link Measure} which is basically a value of this metric for a
 * specific VM.
 * <p>
 * e.g. pid, etime, heapUse are examples of a Metric and the actual values for these for a given VM at the time of
 * measurement are the {@link Measure}s.
 *
 * @author adeshmukh
 * @param <V>
 */
public interface Metric<V extends Comparable<V>> {

    /**
     * Name of the metric. This is something that can be used as a display for header.
     * Value must be alphanumeric with no punctuation/whitespace other than "_".
     *
     * @return
     */
    public String getName();

    /**
     * Text that provides a brief description of the Metric. Typically use cases are in providing "help" information on
     * the Metric.
     *
     * @return
     */
    public String getDescription();

    /**
     * Factory method for creating new Measures of this Metric.
     * 
     * @param val
     * @return
     */
    public Measure<V> newMeasure(V val);
}
