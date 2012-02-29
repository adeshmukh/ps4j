package com.github.adeshmukh.ps4j;

import com.github.adeshmukh.ps4j.Ps4j.Ps4jAction;
import com.google.common.base.Objects;
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

    private Ps4jAction action = Ps4jAction.OPTIONS;

    private String hostname = "localhost";

    private Iterable<Meter> meters;

    public Iterable<Meter> getMeters() {
        return meters;
    }

    public void setMeters(Iterable<Meter> meters) {
        this.meters = meters;
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

    public Ps4jAction getAction() {
        return action;
    }

    public void setAction(Ps4jAction action) {
        this.action = Objects.firstNonNull(action, Ps4jAction.OPTIONS);
    }

    public String getHostname() {
        return this.hostname;
    }
}
