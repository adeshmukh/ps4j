package com.github.adeshmukh.ps4j;

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitoredVm;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A {@link Meter} implementation that uses *nix networking utilities to get port usage
 * by the specified JVM.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class OpenPortsMeter implements Meter {
    private static final Logger log = LoggerFactory.getLogger(PsMeter.class);
    private static final String EMPTY_VALUE = "-";
    private static final List<? extends Metric<String>> SUPPORTED_METRICS =
            singletonList(new SimpleMetric<String>("listenPorts", "ports of type TCP:LISTEN"));
    private static final List<? extends Measure<String>> EMPTY_MEASURES =
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
            pbuilder.command(ImmutableList.<String> of("lsof" // lsof -a -p 7605 -iTCP -sTCP:LISTEN -P -F n
                    , "-a"
                    , "-iTCP", "-sTCP:LISTEN", "-P", "-F", "n"
                    , "-p", valueOf(vmId)));
            Process ps = pbuilder.start();
            is = ps.getInputStream();
            bis = new BufferedInputStream(is);
            isr = new InputStreamReader(bis);
            br = new BufferedReader(isr);

            br.readLine(); // skip process id field
            boolean first = false;
            StringBuilder ports = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                ports.append(line.substring(1));
                if (!first) {
                    ports.append(",");
                } else {
                    first = false;
                }
            }

            if (ports.length() == 0) {
                return EMPTY_MEASURES;
            }
            return Collections.singleton(SUPPORTED_METRICS.iterator().next().newMeasure(ports.toString()));
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
