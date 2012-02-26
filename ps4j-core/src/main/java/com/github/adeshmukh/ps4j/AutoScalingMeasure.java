package com.github.adeshmukh.ps4j;

import static java.lang.String.format;

import com.google.common.base.Preconditions;

/**
 * Formats the display value by scaling up to the most appropriate suffix for the {@link Number} value.
 *
 * @author adeshmukh
 * @param <V>
 */
public class AutoScalingMeasure<V extends Comparable<V>> extends SimpleMeasure<V> {

    private Number val;

    /**
     * The value must be an instance of {@link Number}, otherwise this method will throw an
     * {@link IllegalArgumentException}.
     *
     * @param name
     * @param val
     */
    public AutoScalingMeasure(String name, V val) {
        super(name, val);
        Preconditions.checkArgument(val != null, "val cannot be null");
        Preconditions.checkArgument(val instanceof Number, "val must be a Number (was: " + val.getClass());
        this.val = (Number) val;
    }

    /**
     * Scales the display value to an integer representation with the approprite suffix.
     * Values less than 10000 are printed without suffix. Values greater than 10,000 are
     * scaled down with appropriate suffix, e.g.
     * 10,000 =&gt; 10m; 10,000,000 => 10g; 10,000,000,000 =&gt; 10t and so on with the
     * last prefix being 'y': 10,000,000,000,000,000,000,000,000,000
     * =&gt; 10y
     */
    @Override
    public String getDisplayValue() {
        double d = val.doubleValue();
        double dAbs = Math.abs(d);
        if (dAbs < 10000d) {
            return format("%d", (int) d);
        }
        if (dAbs < 10000000d) {
            return format("%dk", (int) (d / 1000d));
        }
        if (dAbs < 10000000000d) {
            return format("%dm", (int) (d / 1000000d));
        }
        if (dAbs < 10000000000000d) {
            return format("%dg", (int) (d / 1000000000d));
        }
        if (dAbs < 10000000000000000d) {
            return format("%dt", (int) (d / 1000000000000d));
        }
        if (dAbs < 10000000000000000000d) {
            return format("%dp", (int) (d / 1000000000000d));
        }
        if (dAbs < 10000000000000000000000d) {
            return format("%de", (int) (d / 1000000000000000d));
        }
        if (dAbs < 10000000000000000000000000d) {
            return format("%dz", (int) (d / 1000000000000000000d));
        }
        return format("%dy", (int) (d / 1000000000000000000000d));
    }

    @Override
    public String toString() {
        return getDisplayValue();
    }

}
