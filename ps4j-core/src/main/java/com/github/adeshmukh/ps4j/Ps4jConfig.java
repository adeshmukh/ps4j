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
}
