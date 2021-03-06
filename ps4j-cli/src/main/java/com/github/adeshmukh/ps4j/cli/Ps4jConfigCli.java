package com.github.adeshmukh.ps4j.cli;

import static java.lang.Class.forName;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import com.github.adeshmukh.ps4j.Meter;
import com.github.adeshmukh.ps4j.Ps4jConfig;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

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
                    } catch (InstantiationException e) {
                        throw new MeterLoadingException(e);
                    } catch (IllegalAccessException e) {
                        throw new MeterLoadingException(e);
                    } catch (ClassNotFoundException e) {
                        throw new MeterLoadingException(e);
                    }
                }
            };

    @Option(name = "-c", aliases = "--concurrency"
            , usage = "concurrency factor in the range (0,1), default=1. Controls the scaling of threads to the number of VMs available.")
    private double concurrencyFactor = 1;

    @Option(name = "-m", aliases = "--meters", handler = StringArrayOptionHandler.class
            , usage = "Names of one or more meters to be used. Defaults to using auto-discovery to find all available Meters.")
    private String[] meters;

    @Option(name = "-o", aliases = "--fields", handler = StringArrayOptionHandler.class
            , usage = "List of fields to display in output, defaults to all. The specified fields are displayed only if the corresponding Meter is also enabled.")
    private String[] outputFields;

    @Option(name = "-h", aliases = { "--help", "-?" }, usage = "Help. Specify -m <CSV list of Meters> to get a list of available fields")
    private boolean help = false;

    public Ps4jConfig buildConfig() {
        Ps4jConfig config = new Ps4jConfig();

        config.setConcurrencyFactor(concurrencyFactor);

        if (meters == null) {
            config.setMeters(discoverMeters());
        } else { // Auto discover all meters using ServiceLoader
            config.setMeters(FluentIterable.from(asList(meters)).transform(CONSTRUCTOR));
        }

        if (outputFields != null) {
            config.setMetricNames(Arrays.asList(outputFields));
        }
        return config;
    }

    /**
     * Use reflection to see if ServiceLoader is available (ServiceLoader requires JDK1.6+).
     * If ServiceLoader is not available, then the Meters cannot be autodiscovered.
     *
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Iterable<? extends Meter> discoverMeters() {
        Iterable<Meter> meters = null;
        Class serviceLoader;
        try {
            serviceLoader = forName("java.util.ServiceLoader");
            Method m = serviceLoader.getMethod("load", Class.class);
            meters = (Iterable) m.invoke(serviceLoader, Meter.class);
        } catch (Exception e) {
            throw new MeterLoadingException(e);
        }
        return meters;
    }

    public boolean isHelp() {
        return help;
    }
}
