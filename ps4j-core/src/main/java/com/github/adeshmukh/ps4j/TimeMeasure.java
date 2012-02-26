package com.github.adeshmukh.ps4j;

/**
 * Measure that formats the display value using Time units of d, h, m, s as appropriate.
 * 
 * @author adeshmukh
 */
public class TimeMeasure extends SimpleMeasure<Long> {

    public TimeMeasure(String name, Long val) {
        super(name, val);
    }

    @Override
    public String getDisplayValue() {
        long v = getValue();
        if (v < 60) {
            return v + "s";
        }
        if (v < 3600) {
            return (v / 60) + "m" + (v % 60) + "s";
        }
        if (v < 86400) {
            return (v / 3600) + "h" + ((v % 3600) / 60) + "m" + v % 60 + "s";
        }
        return (v / 86400) + "d" + ((v % 86400) / 3600) + "h" + ((v % 3600) / 60) + "m" + v % 60 + "s";
    }
}
