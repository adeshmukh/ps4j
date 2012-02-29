package com.github.adeshmukh.ps4j;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SimpleMetric<V extends Comparable<V>> implements Metric<V> {

    private String name;
    private String description;

    public SimpleMetric(String name, String description) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name cannot be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(description), "description cannot be null or empty");
        Preconditions.checkArgument(!name.contains(" ") && !name.contains("\t") && !name.contains("\n"), "name cannot contain whitespace");

        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Measure<V> newMeasure(V val) {
        return new SimpleMeasure<V>(this, val);
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    static class SimpleMeasure<V extends Comparable<V>> implements Measure<V> {

        private final V val;
        private final Metric<V> metric;

        public SimpleMeasure(SimpleMetric<V> metric, V val) {
            this.val = val;
            this.metric = metric;
        }

        @Override
        public Metric<V> getMetric() {
            return metric;
        }

        @Override
        public V getValue() {
            return val;
        }

        @Override
        public String getDisplayValue() {
            return String.valueOf(val);
        }

        @Override
        public int compareTo(Measure<V> o) {
            Preconditions.checkArgument(o.getMetric().getClass() == this.getMetric().getClass(), "Measures of different Metrics cannot be compared");
            SimpleMeasure<V> that = (SimpleMeasure<V>) o;
            return this.val.compareTo(that.val);
        }

        @Override
        public String toString() {
            return getDisplayValue();
        }
    }

}
