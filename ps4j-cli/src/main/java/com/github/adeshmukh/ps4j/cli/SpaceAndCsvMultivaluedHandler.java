package com.github.adeshmukh.ps4j.cli;

import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class SpaceAndCsvMultivaluedHandler extends OptionHandler<List<String>> {

    protected SpaceAndCsvMultivaluedHandler(CmdLineParser parser, OptionDef option, Setter<? super List<String>> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {

        return 0;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "heapUse etime";
    }

}
