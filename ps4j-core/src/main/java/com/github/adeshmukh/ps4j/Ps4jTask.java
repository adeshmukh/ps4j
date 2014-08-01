package com.github.adeshmukh.ps4j;

import java.util.concurrent.Callable;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

import com.google.common.base.Throwables;

/**
 * Task that wraps the invocations to the available {@link Meter} implementations.
 * Allows concurrent execution when measuring multiple VMs.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class Ps4jTask implements Callable<Record> {
    private MonitoredHost monitoredHost;
    private VmIdentifier vmId;
    private Iterable<Meter> meters;

    public Ps4jTask(MonitoredHost monitoredHost, VmIdentifier vmid, Iterable<Meter> meters) {
        this.monitoredHost = monitoredHost;
        this.vmId = vmid;
        this.meters = meters;
    }

    @Override
    public Record call() {
        MonitoredVm vm = null;
        try {
            vm = monitoredHost.getMonitoredVm(vmId);
            if (vm != null) {
				Record record = Record.create();
				for (Meter meter : meters) {
                    // TODO adeshmukh: qualify the map key with the Meter class that contributes it
                    record.addAll(meter.measureData(vm));
				}
				return record;
            }
        } catch (MonitorException me) {
            Throwables.propagate(me);
        } finally {
            detachQuietly(monitoredHost, vm);
        }
        return Record.NOOP;
    }

    private void detachQuietly(MonitoredHost monitoredHost, MonitoredVm vm) {
        try {
            monitoredHost.detach(vm);
        } catch (Exception e) {}
    }

}
