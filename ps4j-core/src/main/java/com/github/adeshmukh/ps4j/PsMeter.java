package com.github.adeshmukh.ps4j;

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.valueOf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitoredVm;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A {@link Meter} implementation that relies on execution of the <code>ps</code> utility that is generall available on
 * *nix based systems.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class PsMeter implements Meter {
    private static final Logger log = LoggerFactory.getLogger(PsMeter.class);
    private static final String EMPTY_VALUE = "-";

    private static List<Metric<String>> SUPPORTED_METRICS = ImmutableList.<Metric<String>> of(
            new SimpleMetric<String>("etime", "elapsed time since the VM process started")
            , new SimpleMetric<String>("lim", "process-level memoryuse limit")
            , new SimpleMetric<String>("logname", "login name of user who started the session")
            , new SimpleMetric<String>("majflt", "total page faults")
            , new SimpleMetric<String>("minflt", "total page reclaims")
            , new SimpleMetric<String>("msgrcv", "total messages received (reads from pipes/sockets)")
            , new SimpleMetric<String>("msgsnd", "total messages sent (writes on pipes/sockets)")
            );

    private static List<String> PS_FORMAT_OPTIONS =
            Lists.transform(SUPPORTED_METRICS, new Function<Metric<String>, String>() {
                @Override
                public String apply(Metric<String> metric) {
                    return metric.getName();
                }
            });
    private static final String PS_FORMAT_OPTION = Joiner.on(',').join(PS_FORMAT_OPTIONS);
    private static final List<Measure<String>> EMPTY_MEASURES =
            Lists.transform(SUPPORTED_METRICS, new Function<Metric<String>, Measure<String>>() {
                @Override
                public Measure<String> apply(Metric<String> metric) {
                    return metric.newMeasure(EMPTY_VALUE);
                }
            });

    @Override
    public Collection<? extends Metric<?>> supportedMetrics() {
        return SUPPORTED_METRICS;
    }

    @Override
    public Collection<? extends Measure<?>> measureData(MonitoredVm vm) {
        int vmId = vm.getVmIdentifier().getLocalVmId();
        InputStream is = null;
        BufferedInputStream bis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            ProcessBuilder pbuilder = new ProcessBuilder();
            pbuilder.command(ImmutableList.<String> of("ps"
                    , "-o", PS_FORMAT_OPTION
                    , "-p", valueOf(vmId)));
            Process ps = pbuilder.start();
            is = ps.getInputStream();
            bis = new BufferedInputStream(is);
            isr = new InputStreamReader(bis);
            br = new BufferedReader(isr);

            br.readLine(); // skip header
            String line = br.readLine();
            String[] parts = line.split("\\s+");
            List<Measure<?>> retval = new ArrayList<Measure<?>>(PS_FORMAT_OPTIONS.size());
            for (int i = 0, iSize = Math.min(parts.length, SUPPORTED_METRICS.size()); i < iSize; i++) {
                retval.add(SUPPORTED_METRICS.get(i).newMeasure(parts[i]));
            }

            return retval;
        } catch (Exception e) {
            log.error("Error executing process", e);
            return EMPTY_MEASURES;
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
            closeQuietly(bis);
            closeQuietly(is);
        }
    }
}