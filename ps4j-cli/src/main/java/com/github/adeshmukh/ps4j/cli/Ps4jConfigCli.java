package com.github.adeshmukh.ps4j.cli;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;

import java.util.ServiceLoader;

import org.kohsuke.args4j.Option;

import com.github.adeshmukh.ps4j.Meter;
import com.github.adeshmukh.ps4j.Ps4jConfig;
import com.google.common.base.Function;
import com.google.common.base.Strings;

/**
 * @author adeshmukh
 */
public class Ps4jConfigCli {

    private static final Function<String, Meter> CONSTRUCTOR =
            new Function<String, Meter>() {
                @Override
                public Meter apply(String className) {
                    try {
                        return (Meter) Class.forName(className.trim()).newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }
            };

    @Option(name = "-c", aliases = "--concurrency"
            , usage = "concurrency factor in the range (0,1), default=1. Controls the scaling of threads to the number of VMs available.")
    private double concurrencyFactor = 1;

    @Option(name = "-m", aliases = "--meters"
            , usage = "Comma separate list of Meter class names to instantiate. Defaults to using auto-discovery to find all available Meters.")
    private String metersCsv;

    @Option(name = "-o", aliases = "--fields"
            , usage = "Comma separated list of fields to display in output, defaults to all. The specified fields are displayed only if the corresponding Meter is also enabled.")
    private String outputFields;

    @Option(name = "-h", aliases = { "--help", "-?" }, usage = "Help. Specify -m <CSV list of Meters> to get a list of available fields")
    private boolean help = false;

    public Ps4jConfig buildConfig() {
        Ps4jConfig config = new Ps4jConfig();
        config.setConcurrencyFactor(concurrencyFactor);
        if (!Strings.isNullOrEmpty(metersCsv)) {
            config.setMeters(transform(asList(metersCsv.split(",")), CONSTRUCTOR));
        } else { // Auto discover all meters using ServiceLoader
            config.setMeters(ServiceLoader.<Meter> load(Meter.class));
        }
        return config;
    }

    public boolean isHelp() {
        return help;
    }
}
