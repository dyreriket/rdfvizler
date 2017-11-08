package osl.rdfvizler.ui.cli;

import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import osl.util.Arrays;

public abstract class CLI {

    private static final String[] EMPTY_ARRAY = {};
    private static final char OPTION_VALUE_SEPARATOR = ',';
    
    protected CommandLine line;
    
    protected void consumeOptionValue(String option, Consumer<String> setFunction) {
        consumeOptionValue(option, setFunction, EMPTY_ARRAY); // call without backups
    }
    
    protected void consumeOptionValue(String option, Consumer<String> setFunction, String... backups) {
        if (line.hasOption(option)) {
            setFunction.accept(line.getOptionValue(option));
        } else {
            String backup = Arrays.getFirstNonNull(backups);
            if (backup != null) {
                setFunction.accept(backup);
            }
        }
    }
    
    protected static Option buildOption(String shortname, String longname, boolean required, int noArgs, String... description) {
        Builder ob = Option.builder(shortname).longOpt(longname).required(required).desc(Arrays.toString(description, ""));
        if (noArgs < 0) {
            ob.hasArgs();
        } else {
            ob.numberOfArgs(noArgs);
            ob.valueSeparator(OPTION_VALUE_SEPARATOR);
        }
        return ob.build();
    }
}