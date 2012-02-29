package com.github.adeshmukh.ps4j;

import java.util.Collection;

import sun.jvmstat.monitor.MonitoredVm;

/**
 * An interface for capturing data pertaining to a running VM.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public interface Meter {

    Collection<? extends Metric<?>> supportedMetrics();

    Collection<? extends Measure<?>> measureData(MonitoredVm vm);
}
