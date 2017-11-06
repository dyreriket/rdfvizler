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
    
    protected static Option buildOption(String shortName, String longname, boolean required, int noArgs, String description) {
        Builder ob = Option.builder(shortName).longOpt(longname).required(required).desc(description);
        if (noArgs < 0) {
            ob.hasArgs();
        } else {
            ob.numberOfArgs(noArgs);
            ob.valueSeparator(OPTION_VALUE_SEPARATOR);
        }
        return ob.build();
    }

    /*
    public String want(String option, String defaultValue) {
        if (line.hasOption(option)) {
            return line.getOptionValue(option);
        } else {
            return defaultValue;
        }
    }

    public  String want(String option) {
        if (line.hasOption(option)) {
            return line.getOptionValue(option);
        } else {
            return null;
        }
    }

    public String require(String option)
            throws MissingConfigurationException {
        if (line.hasOption(option)) {
            return line.getOptionValue(option);
        } else {
            missing(option);
        }
        return "";
    }

    private static void missing(String option) throws MissingConfigurationException {
        throw new MissingConfigurationException("Missing value for option " + option);
    }

    public static class MissingConfigurationException extends Exception {
        private static final long serialVersionUID = 1169386320837465674L;

        MissingConfigurationException(String msg) {
            super(msg);
        }
    }

    public static class IllegalOptionCombinationException extends Exception {
        private static final long serialVersionUID = 6299858126990714386L;

        IllegalOptionCombinationException(String msg) {
            super(msg);
        }
    }
    */
}