package com.github.adeshmukh.ps4j;

import java.text.DateFormat;
import java.util.Date;

public class DateTimeMetric extends SimpleMetric<Date> {

    public DateTimeMetric(String name, String description) {
        super(name, description);
    }

    @Override
    public Measure<Date> newMeasure(Date val) {
        return new DateTimeMeasure(this, val);
    }

    static class DateTimeMeasure extends SimpleMeasure<Date> {

        private static DateFormat FORMATTER = DateFormat.getDateTimeInstance();

        public DateTimeMeasure(DateTimeMetric name, Date val) {
            super(name, val);
        }

        /**
         * Values printed with {@link DateFormat#getDateTimeInstance()} formatting.
         */
        @Override
        public String getDisplayValue() {
            return FORMATTER.format(getValue());
        }
    }
}
