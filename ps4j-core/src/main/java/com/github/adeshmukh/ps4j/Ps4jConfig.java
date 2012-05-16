package com.github.adeshmukh.ps4j;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

/**
 * @author adeshmukh
 */
public final class Ps4jConfig {

    /**
     * Default instance for convenience.
     */
    public static Ps4jConfig DEFAULT = new Ps4jConfig();

    private double concurrencyFactor = 1;

    private String hostname = "localhost";

    private Iterable<? extends Meter> meters;

    private List<String> metricNames = Collections.emptyList();

    private Set<String> metricNamesSet = Collections.emptySet();

    public Iterable<? extends Meter> getMeters() {
        return meters;
    }

    public void setMeters(@Nonnull Iterable<? extends Meter> meters) {
        checkArgument(meters != null, "meters cannot be null");
        this.meters = meters;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(@Nonnull String hostname) {
        checkArgument(hostname != null, "Hostname cannot be null");
        this.hostname = hostname;
    }

    public double getConcurrencyFactor() {
        return concurrencyFactor;
    }

    /**
     * Value for concurrency factor must be in the range (0,1).
     * 0 implies use single threaded execution. 1 implies one thread per VM.
     * Default is 0.5 (1 thread per 2 VMs).
     *
     * @param cf
     */
    public void setConcurrencyFactor(double cf) {
        checkArgument(cf >= 0 && cf <= 1, "concurrencyFactor must be in the range (0,1)");
        this.concurrencyFactor = cf;
    }

    public List<String> getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(@Nonnull List<String> outputFields) {
        checkArgument(outputFields != null, "outputFields cannot be set to null value");
        this.metricNames = Collections.unmodifiableList(outputFields);
        this.metricNamesSet = Sets.newHashSet(outputFields);
    }

    public boolean hasMetric(String input) {
        return metricNamesSet != null && metricNamesSet.contains(input);
    }
}
