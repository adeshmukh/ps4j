package com.github.adeshmukh.ps4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic implementation of Measure that displays the value without any more transformations than provided by
 * {@link String#valueOf(Object)}.
 *
 * @author adeshmukh
 * @param <V>
 */
public class SimpleMeasure<V extends Comparable<V>> implements Measure<V> {

    protected Logger log = LoggerFactory.getLogger(SimpleMeasure.class);

    private V val;
    private String name;

    public SimpleMeasure(String name, V val) {
        this.val = val;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getDisplayValue();
    }

    @Override
    public String getDisplayValue() {
        return String.valueOf(val);
    }

    @Override
    public V getValue() {
        return val;
    }

    @Override
    public int compareTo(Measure<V> o) {
        SimpleMeasure<V> that = (SimpleMeasure<V>) o;
        return this.val.compareTo(that.val);
    }
}
