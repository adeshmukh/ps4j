package com.github.adeshmukh.ps4j.cli;

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

@SuppressWarnings("restriction")
public class Main {

	private static final String VMID_TEMPLATE = "//%s?mode=r";

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception {
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost("localhost");
        Set<Integer> vmIds = monitoredHost.activeVms();
        for (Integer vmId : vmIds) {
            VmIdentifier vmIdentifier = new VmIdentifier(format(VMID_TEMPLATE, vmId));
            MonitoredVm vm = monitoredHost.getMonitoredVm(vmIdentifier, 0);
            List<Monitor> monitors = vm.findByPattern("sun.rt.*Time");
            for (Monitor o : monitors) {
                if (!"None".equals("" + o.getUnits())) {
                    continue;
                }
                if (false) {
                    if (o.isSupported()) {
                        System.out.println(String.format("%s # %s # %s %s # %s # %s", o.getName(),
                                o.getBaseName(), o.getValue(), o.getUnits(),
                                o.getVariability(), o.getVectorLength()));
                    } else {
                        System.err.println(String.format("%s # %s # %s %s # %s # %s", o.getName(),
                                o.getBaseName(), o.getValue(), o.getUnits(),
                                o.getVariability(), o.getVectorLength()));
                    }
                }
                // System.out.println(o.getName());
                System.out.println(String.format("%s # %s # %s %s # %s # %s", o.getName(),
                        o.getBaseName(), new Date(Long.valueOf(o.getValue().toString())), o.getUnits(),
                        o.getVariability(), o.getVectorLength()));
            }
            System.out.println(vmId + " : cli : " + MonitoredVmUtil.commandLine(vm));
            System.out.println(vmId + " : jvmargs : " + MonitoredVmUtil.jvmArgs(vm));
            System.out.println(vmId + " : jvmflags : " + MonitoredVmUtil.jvmFlags(vm));
            System.out.println(vmId + " : mainargs : " + MonitoredVmUtil.mainArgs(vm));
            System.out.println(vmId + " : maincls : " + MonitoredVmUtil.mainClass(vm, false));
            System.out.println(vmId + " : vmversion : " + MonitoredVmUtil.vmVersion(vm));
            System.out.println(vmId + " : isattachable : " + MonitoredVmUtil.isAttachable(vm));
            System.out.println(vmId + " : kernelvm : " + MonitoredVmUtil.isKernelVM(vm));
            System.out.println("-----------------");
            monitoredHost.detach(vm);
        }

        System.out.println(new Date(1329779424106L));
	}
}
