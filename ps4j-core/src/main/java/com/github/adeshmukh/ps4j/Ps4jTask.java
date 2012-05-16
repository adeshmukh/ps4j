package com.github.adeshmukh.ps4j;

import static com.google.common.collect.Maps.uniqueIndex;

import java.util.Collection;
import java.util.concurrent.Callable;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * Task that wraps the invocations to the available {@link Meter} implementations. Used to allow concurrent execution
 * when measuring multiple VMs.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class Ps4jTask implements Callable<Record> {

    private static final Function<Measure<?>, String> MEASURE_NAME_FUNCTION = new Function<Measure<?>, String>() {

        @Override
        public String apply(Measure<?> m) {
            return m.getMetric().getName();
        }
    };
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
				for (Meter ms : meters) {
                    Collection<? extends Measure<?>> measures = ms.measureData(vm);
					// TODO adeshmukh: qualify the map key with Meter's class
                    record.addAll(uniqueIndex(measures, MEASURE_NAME_FUNCTION));
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
