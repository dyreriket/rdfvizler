package osl.rdfvizler.ui.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.lang3.StringUtils;

public abstract class CLI {

    private static final char OPTION_VALUE_SEPARATOR = ',';
   
    protected static Option buildOption(String shortname, String longname, boolean required, int noArgs, String... description) {
        Builder ob = Option.builder(shortname).longOpt(longname).required(required).desc(StringUtils.join(description, ""));
        if (noArgs < 0) {
            ob.hasArgs();
        } else {
            ob.numberOfArgs(noArgs);
            ob.valueSeparator(OPTION_VALUE_SEPARATOR);
        }
        return ob.build();
    }
    
}