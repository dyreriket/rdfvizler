package osl.rdfvizler.ui.cli;

import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;

import osl.util.Arrays;

public class CLUtil {
    
    private CommandLine line;
    
    public CLUtil(CommandLine line) {
        this.line = line;
    }
        
    public void consumeOption(String option, Consumer<String> setFunction, String... backups) {
        if (line.hasOption(option)) {
            setFunction.accept(getOptionValue(option));
        } else {
            String backup = Arrays.getFirstNonNull(backups);
            if (backup != null) {
                setFunction.accept(backup);
            }
        }
    }
    
    public String getOptionValue(String option) {
        return line.getOptionValue(option);
    }
    
}