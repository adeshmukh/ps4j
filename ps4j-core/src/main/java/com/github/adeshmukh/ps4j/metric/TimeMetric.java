package com.github.adeshmukh.ps4j.metric;

import com.github.adeshmukh.ps4j.Measure;


public class TimeMetric extends SimpleMetric<Long> {

    public TimeMetric(String name, String description) {
        super(name, description);
    }

    @Override
    public Measure<Long> newMeasure(Long val) {
        return new TimeMeasure(this, val);
    }

    static class TimeMeasure extends SimpleMeasure<Long> {

        public TimeMeasure(TimeMetric name, Long val) {
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

}
