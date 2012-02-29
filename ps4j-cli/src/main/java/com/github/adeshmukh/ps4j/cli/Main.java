package com.github.adeshmukh.ps4j.cli;

import java.util.Collection;
import java.util.SortedMap;

import org.kohsuke.args4j.CmdLineParser;

import com.github.adeshmukh.ps4j.Measure;
import com.github.adeshmukh.ps4j.Metric;
import com.github.adeshmukh.ps4j.Ps4j;
import com.github.adeshmukh.ps4j.Record;

/**
 * @author adeshmukh
 */
public class Main {
	public static void main(String[] args) throws Exception {
        Ps4jConfigCli cliConfig = new Ps4jConfigCli();
        CmdLineParser cliParser = new CmdLineParser(cliConfig);
        cliParser.setUsageWidth(80);
        cliParser.parseArgument(args);
        Ps4j ps4j = new Ps4j(cliConfig.buildConfig());
        if (cliConfig.isHelp()) {
            cliParser.printUsage(System.out);
            displayOptions(ps4j.options());
        } else {
            display(ps4j.measure());
        }
	}

    private static void displayOptions(Collection<Metric<?>> metrics) {
        System.out.println("Output field options applicable for the specified Meters are:");
        for (Metric<?> metric : metrics) {
            System.out.println("\t" + metric);
        }
    }

    // HACK adeshmukh: replace this with cli module
    private static void display(Collection<Record> records) {
        boolean header = true;
        for (Record record : records) {
            SortedMap<String, ? extends Measure<?>> measures = record.getMeasures();
            if (header) {
                header = false;
                boolean first = true;
                for (String key : measures.keySet()) {
                    if (!first) {
                        System.out.print("\t");
                    }
                    System.out.print(key);
                    first = false;
                }
                System.out.println();
            }
            boolean first = true;
            for (String key : measures.keySet()) {
                if (!first) {
                    System.out.print("\t");
                }
                System.out.print(measures.get(key));
                first = false;
            }
            System.out.println();
        }
    }
}
