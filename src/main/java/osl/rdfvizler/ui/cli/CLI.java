package osl.rdfvizler.ui.cli;

import org.apache.commons.cli.CommandLine;

public abstract class CLI {

    CommandLine line;

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
}