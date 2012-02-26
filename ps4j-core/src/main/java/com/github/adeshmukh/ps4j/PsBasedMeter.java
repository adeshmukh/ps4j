package com.github.adeshmukh.ps4j;

import static com.google.common.collect.Lists.transform;
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

/**
 * A {@link Meter} implementation that relies on execution of the <code>ps</code> utility that is generall available on
 * *nix based systems.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class PsBasedMeter implements Meter {
    private static final Logger log = LoggerFactory.getLogger(PsBasedMeter.class);
    private static final String EMPTY_VALUE = "-";
    private static final Function<String, SimpleMeasure<String>> EMPTY_MEASURE_FUNCTION = new Function<String, SimpleMeasure<String>>() {
        @Override
        public SimpleMeasure<String> apply(String name) {
            return new SimpleMeasure<String>(name, EMPTY_VALUE);
        }
    };

    private static List<String> PS_FORMAT_OPTIONS = ImmutableList.<String> of(
            "etime", "lim", "logname", "majflt", "minflt", "msgrcv", "msgsnd",
            "nswap", "ruser", "sess", "user");
    private static final String PS_FORMAT_OPTION = Joiner.on(',').join(PS_FORMAT_OPTIONS);
    private static final Collection<? extends Measure<?>> EMPTY_MEASURES = transform(PS_FORMAT_OPTIONS, EMPTY_MEASURE_FUNCTION);

    @Override
    public Collection<? extends Measure<?>> measures(MonitoredVm vm) {
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
            List<Measure<? extends Comparable<?>>> retval = new ArrayList<Measure<? extends Comparable<?>>>(PS_FORMAT_OPTIONS.size());
            for (int i = 0, iSize = parts.length; i < iSize; i++) {
                retval.add(new SimpleMeasure<String>(PS_FORMAT_OPTIONS.get(i), parts[i]));
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