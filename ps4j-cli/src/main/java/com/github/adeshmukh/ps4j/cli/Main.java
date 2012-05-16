package com.github.adeshmukh.ps4j.cli;

import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.util.Collection;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.adeshmukh.ps4j.Metric;
import com.github.adeshmukh.ps4j.Ps4j;
import com.github.adeshmukh.ps4j.Ps4jException;
import com.github.adeshmukh.ps4j.Record;

/**
 * @author adeshmukh
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
        Ps4jConfigCli cfg = new Ps4jConfigCli();
        CmdLineParser clip = new CmdLineParser(cfg);
        clip.setUsageWidth(80);

        try {
            clip.parseArgument(args);
            Ps4j ps4j = new Ps4j(cfg.buildConfig());

            if (cfg.isHelp()) {
                clip.printUsage(out);
                displayOptions(ps4j.options());
            } else {
                display(ps4j.measure());
            }
        } catch (CmdLineException cle) {
            log.error("Error parsing cmd line arguments", cle);
            err.println("ERROR: " + cle.getMessage());
            clip.printUsage(err);
            exit(1);
        } catch (MeterLoadingException mle) {
            log.error("Error loading Meters", mle);
            err.println("ERROR: " + mle.getMessage());
            clip.printUsage(err);
            exit(2);
        } catch (Ps4jException pe) {
            log.error("Error executing the Ps4j API", pe);
            err.println("ERROR: " + pe.getMessage());
            clip.printUsage(err);
            exit(3);
        }
	}

    private static void displayOptions(Collection<Metric<?>> metrics) {
        out.println("Output field options applicable for the specified Meters are:");
        for (Metric<?> metric : metrics) {
            out.println("\t" + metric);
        }
    }

    private static void display(Iterable<Record> records) {
        for (String[] values : new DisplayRecords(records)) {
            for (String value : values) {
                out.print(value);
            }
            out.println();
        }
    }
}
