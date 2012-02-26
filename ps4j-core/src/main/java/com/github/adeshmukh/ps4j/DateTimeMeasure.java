package com.github.adeshmukh.ps4j;

import java.text.DateFormat;
import java.util.Date;

/**
 * For representing Date values. Values are printed with {@link DateFormat#getDateTimeInstance()} formatting.
 *
 * @author adeshmukh
 */
public class DateTimeMeasure extends SimpleMeasure<Date> {

    private static DateFormat FORMATTER = DateFormat.getDateTimeInstance();

    public DateTimeMeasure(String name, Date val) {
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
