package com.github.adeshmukh.ps4j;

import com.google.common.base.Preconditions;

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

    private String[] measureNames;

    public Iterable<? extends Meter> getMeters() {
        return meters;
    }

    public void setMeters(Iterable<? extends Meter> meters) {
        this.meters = meters;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
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
        Preconditions.checkArgument(cf >= 0 && cf <= 1, "concurrencyFactor must be in the range (0,1)");
        this.concurrencyFactor = cf;
    }

    public String[] getMetricNames() {
        return measureNames;
    }

    public void setMeasureNames(String[] measureNames) {
        this.measureNames = measureNames;
    }
}
