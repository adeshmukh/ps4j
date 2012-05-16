package com.github.adeshmukh.ps4j;

import static com.google.common.collect.Maps.newTreeMap;

import java.util.Map;
import java.util.SortedMap;

/**
 * Record contains the Measures for a single VM.
 *
 * @author adeshmukh
 */
public class Record {

    /**
     * Special instance indicating that the Record does not contain any useable data.
     */
    public static final Record NOOP = new Record();

    private SortedMap<String, Measure<?>> measures;

	private Record() {
        measures = newTreeMap();
	}

	public static Record create() {
		return new Record();
	}

    public Record addAll(Map<String, ? extends Measure<?>> measures) {
        this.measures.putAll(measures);
        return this;
	}

    public SortedMap<String, ? extends Measure<?>> getMeasures() {
        return newTreeMap(measures);
    }

    @Override
    public String toString() {
        return measures.toString();
    }
}
