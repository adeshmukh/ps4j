package com.github.adeshmukh.ps4j.cli;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padStart;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import com.github.adeshmukh.ps4j.Measure;
import com.github.adeshmukh.ps4j.Record;
import com.google.common.collect.ImmutableList;

public class DisplayRecords implements Iterable<String[]> {

    private static final Iterator<String[]> EMPTY_ITERATOR = Collections.<String[]> emptyList().iterator();

    private static final char PADCHAR = ' ';

    private int numRecords;
    private int numCols;
    private int[] colWidths;
    private List<String[]> displayValues;
    private List<String> orderedKeys;

    DisplayRecords(Collection<Record> records) {
        if (records.isEmpty()) {
            return;
        }
        // get the measures in the first record to size the record
        SortedMap<String, ? extends Measure<?>> canonicalMeasures = records.iterator().next().getMeasures();
        orderedKeys = ImmutableList.copyOf(canonicalMeasures.keySet());
        numCols = orderedKeys.size();

        colWidths = new int[numCols];
        numRecords = records.size();
        displayValues = new ArrayList<String[]>(numRecords);

        // populate colWidths
        for (Record record : records) {
            SortedMap<String, ? extends Measure<?>> measures = record.getMeasures();
            int i = 0;
            for (String key : orderedKeys) {
                colWidths[i] = max(max(colWidths[i], key.length()), measures.get(key).getDisplayValue().length());
                i++;
            }
        }

        // populate col headers in displayValues
        String[] headers = new String[numCols];
        int i = 0;
        for (String key : orderedKeys) {
            headers[i] = padAround(key, colWidths[i] + 1, PADCHAR);
            i++;
        }
        displayValues.add(headers);

        // populate vals in displayValues
        for (Record record : records) {
            SortedMap<String, ? extends Measure<?>> measures = record.getMeasures();
            String[] vals = new String[numCols];
            int j = 0;
            for (String key : orderedKeys) {
                vals[j] = padStart(measures.get(key).getDisplayValue(), colWidths[j] + 1, PADCHAR);
                j++;
            }
            displayValues.add(vals);
        }
    }

    private static String padAround(String s, int minLength, char padchar) {
        checkNotNull(s);
        if (s.length() >= minLength) {
            return s;
        }
        int len = s.length();
        int rpad = (minLength - len) / 2;
        int lpad = (minLength - len - rpad);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lpad; i++) {
            sb.append(padchar);
        }
        sb.append(s);
        for (int i = 0; i < rpad; i++) {
            sb.append(padchar);
        }
        return sb.toString();
    }

    @Override
    public Iterator<String[]> iterator() {
        return displayValues == null ? EMPTY_ITERATOR : displayValues.iterator();
    }
}
