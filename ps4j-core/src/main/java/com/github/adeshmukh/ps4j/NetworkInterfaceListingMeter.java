package com.github.adeshmukh.ps4j;

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.valueOf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitoredVm;

import com.google.common.collect.ImmutableList;

/**
 * A {@link Meter} implementation that uses *nix networking utilities to get port usage
 * by the specified JVM.
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class NetworkInterfaceListingMeter implements Meter {
    private static final Logger log = LoggerFactory.getLogger(PsBasedMeter.class);
    private static final String EMPTY_VALUE = "-";
    private static final Collection<? extends Measure<?>> EMPTY_MEASURES = Collections.singleton(new SimpleMeasure<String>("portList", EMPTY_VALUE));

    @Override
    public Collection<? extends Measure<?>> measures(MonitoredVm vm) {
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
                ports.append(EMPTY_VALUE);
            }
            return Collections.singleton(new SimpleMeasure<String>("portList", ports.toString()));
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
