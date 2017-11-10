package osl.rdfvizler.ui.cli;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.ui.RDFVizler;

public class RDFVizlerCLI {

    public static final String ENV_RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";
    protected static final String ENV_RDFVIZLER_RULES_PATH = "RDFVIZLER_RULES_PATH";
    
    // CLI options
    private static final String OPT_IN = "in";
    private static final String OPT_INFORMAT = "inFormat";
    private static final String OPT_RULES = "rules";
    private static final String OPT_OUT = "out";
    private static final String OPT_OUTFORMAT = "outFormat";
    private static final String OPT_OUTEXT = "outExtension";
    private static final String OPT_DOTEXEC = "dotExec";
    
    private PrintStream console;

    private RDFVizler rdfvizler;

    private String outputPath;
    private String outputFormat;
    
    public RDFVizlerCLI(PrintStream out) {
        console = out;
    }

    public static void main(String[] args) throws IOException {
        RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI(System.out);
        if (rdfVizlerCLI.parseOptions(args)) {
            rdfVizlerCLI.execute();
        }
    }

    private Options buildOptions() {
        Options options = new Options();
        options.addOption(CLI.buildOption("i", OPT_IN, true, 1, 
                "Path/URI to RDF file"));
        
        options.addOption(CLI.buildOption("if", OPT_INFORMAT, false,  1, 
                "Input RDF format. Permissible values: ", 
                StringUtils.join(RDFVizler.INPUT_FORMATS, "|"), 
                ". Defaults to: ", RDFVizler.DEFAULT_INPUT_FORMAT));

        options.addOption(CLI.buildOption("r", OPT_RULES, false, 1, 
                "Path/URI to rules file. If omitted, a standard RDF layout is used."));
        
        // Mutual exclusive options group for output options:
        OptionGroup outputGroup = new OptionGroup();
        outputGroup.addOption(CLI.buildOption("o", OPT_OUT, false, 1, 
                "Output file. If omitted, output to stdout"));
        outputGroup.addOption(CLI.buildOption("oe", OPT_OUTEXT, false, 0, 
                "Write output to inputfile extended with ", OPT_OUTFORMAT));
        outputGroup.setRequired(false);
        options.addOptionGroup(outputGroup);

        options.addOption(CLI.buildOption("of", OPT_OUTFORMAT, false, 1, 
                "Output format. Permissible values ",
                " for dot image output: ", StringUtils.join(RDFVizler.DOT_OUTPUT_FORMATS, "|"), 
                "; for dot text output: ", StringUtils.join(RDFVizler.TEXT_OUTPUT_FORMATS, "|"),
                "; for RDF output: ", StringUtils.join(RDFVizler.RDF_OUTPUT_FORMATS, "|"),
                " Defaults to: ", RDFVizler.DEFAULT_OUTPUT_FORMAT));

        options.addOption(CLI.buildOption("x", OPT_DOTEXEC, false, 1, 
                "Path to dot executable. Defaults to: ", DotProcess.DEFAULT_EXEC));

        return options;
    }

    protected boolean parseOptions(String[] args) {
        Options options = buildOptions();
        try {
            CLUtil cl = new CLUtil(new DefaultParser().parse(options, args));
            
            cl.consumeOption(OPT_IN,        v -> rdfvizler = new RDFVizler(v));
            cl.consumeOption(OPT_RULES,     v -> rdfvizler.setRulesPath(v), System.getenv(ENV_RDFVIZLER_RULES_PATH));
            cl.consumeOption(OPT_INFORMAT,  v -> rdfvizler.setInputFormat(v));
            cl.consumeOption(OPT_OUTFORMAT, v -> this.outputFormat = v, RDFVizler.DEFAULT_OUTPUT_FORMAT);
            cl.consumeOption(OPT_OUT,       v -> this.outputPath = v);
            cl.consumeOption(OPT_OUTEXT,    v -> this.outputPath = cl.getOptionValue(OPT_IN) + "." + this.outputFormat);
            cl.consumeOption(OPT_DOTEXEC,   v -> rdfvizler.setDotExecutable(v), System.getenv(ENV_RDFVIZLER_DOT_EXEC));

        } catch (ParseException e) {
            printHelp(options, e);
            return false;
        }
        return true;
    }

    private void printHelp(Options options, Exception e) {
        console.println(e.getMessage());
        
        String str = StringUtils.join(
            " --", OPT_IN, " <rdfFile>", 
            " [--", OPT_RULES, " <rulesFile>]",
            " [--", OPT_INFORMAT, " <", StringUtils.join(RDFVizler.INPUT_FORMATS, "|"), ">]",
            " [--", OPT_OUT, " <outputFile>",
            " --", OPT_OUTEXT, "]",
            " [--", OPT_OUTFORMAT, 
                " <",
                StringUtils.join(RDFVizler.DOT_OUTPUT_FORMATS, "|"),
                "|" + StringUtils.join(RDFVizler.TEXT_OUTPUT_FORMATS, "|"), 
                "|" + StringUtils.join(RDFVizler.RDF_OUTPUT_FORMATS, "|"),
                ">]");

        new HelpFormatter().printHelp(120, str, "java -jar rdfvizler", options, "");
    }

    public void execute() throws IOException {
        try {
            String output = rdfvizler.writeOutput(outputFormat);
            printOutput(output);
        } catch (RuntimeException | IOException e) {
            throw e;
        }
    }

    private void printOutput(String output) throws IOException {
        if (outputPath != null) {
            try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
                out.write(output);
            }
        } else {
            console.println(output);
        }
    }
}
