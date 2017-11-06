package osl.rdfvizler.ui.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.ui.RDFVizler;
import osl.util.Arrays;

public class RDFVizlerCLI extends CLI {

    protected static final String ENV_RDFVIZLER_RULES_PATH = "RDFVIZLER_RULES_PATH";
    public static final String ENV_RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";

    // CLI options
    private static final String OPT_RULES = "rules";
    private static final String OPT_IN = "in";
    private static final String OPT_INFORMAT = "inFormat";
    private static final String OPT_DOTEXEC = "dotExec";
    private static final String OPT_OUT = "out";
    private static final String OPT_OUTFORMAT = "outFormat";
    private static final String OPT_OUTEXT = "outExtension";
    
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
        options.addOption(buildOption("i", OPT_IN, true, 1, "Path/URI to RDF file"));

        options.addOption(buildOption("r", OPT_RULES, false, 1, "Path/URI to rules file. If omitted, a standard RDF layout is used."));

        options.addOption(buildOption("if", OPT_INFORMAT, false,  1, "Input RDF format. Permissible values: " 
                + Arrays.toString(RDFVizler.INPUT_FORMATS, "|") 
                + ". Defaults to: " + RDFVizler.DEFAULT_INPUT_FORMAT));

        options.addOption(buildOption("of", OPT_OUTFORMAT, false, 1, "Output format. Permissible values "
                + " for dot image output: " + Arrays.toString(RDFVizler.DOT_OUTPUT_FORMATS, "|") 
                + "; for dot text output: " + Arrays.toString(RDFVizler.TEXT_OUTPUT_FORMATS, "|")
                + "; for RDF output: " + Arrays.toString(RDFVizler.RDF_OUTPUT_FORMATS, "|")
                + " Defaults to: " + RDFVizler.DEFAULT_OUTPUT_FORMAT));

        options.addOption(buildOption("x", OPT_DOTEXEC, false, 1, "Path to dot executable. Defaults to: " + DotProcess.DEFAULT_EXEC));

        // Mutual exclusive options group for output options:
        OptionGroup outputGroup = new OptionGroup();
        outputGroup.addOption(buildOption("o", OPT_OUT, false, 1, "Output file. If omitted, output to stdout"));
        outputGroup.addOption(buildOption("oe", OPT_OUTEXT, false, 0, "Write output to inputfile extended with " + OPT_OUTFORMAT));
        outputGroup.setRequired(false);
        options.addOptionGroup(outputGroup);
        return options;
    }

    protected boolean parseOptions(String[] args) {
        Options options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse(options, args);

            String inputPath = line.getOptionValue(OPT_IN);

            rdfvizler = new RDFVizler(inputPath);

            consumeOptionValue(OPT_RULES,     v -> rdfvizler.setRulesPath(v), System.getenv(ENV_RDFVIZLER_RULES_PATH));
            consumeOptionValue(OPT_INFORMAT,  v -> rdfvizler.setInputFormat(v));
            consumeOptionValue(OPT_OUTFORMAT, v -> this.outputFormat = v, RDFVizler.DEFAULT_OUTPUT_FORMAT);
            // set output path
            consumeOptionValue(OPT_OUT,       v -> this.outputPath = v);
            consumeOptionValue(OPT_OUTEXT,    v -> this.outputPath = inputPath + "." + this.outputFormat);
            consumeOptionValue(OPT_DOTEXEC,   v -> rdfvizler.setDotExecutable(v), System.getenv(ENV_RDFVIZLER_DOT_EXEC));

        } catch (ParseException e) {
            printHelp(options, e);
            return false;
        }
        return true;
    }

    private void printHelp(Options options, Exception e) {
        console.println(e.getMessage());
        HelpFormatter formatter = new HelpFormatter();

        StringBuilder str = new StringBuilder();
        str//.append("java -jar rdfvizler")
            .append(" --" + OPT_IN + " <rdfFile>")
            .append(" [--" + OPT_RULES + " <rulesFile>]")
            .append(" [--" + OPT_INFORMAT + " <" + Arrays.toString(RDFVizler.INPUT_FORMATS, "|") + ">]")
            .append(" [--" + OPT_OUT + " <outputFile>")
            .append(" --" + OPT_OUTEXT + "]")
            .append(" [--" + OPT_OUTFORMAT 
                + " <" 
                + Arrays.toString(RDFVizler.DOT_OUTPUT_FORMATS, "|")
                + "|" + Arrays.toString(RDFVizler.TEXT_OUTPUT_FORMATS, "|") 
                + "|" + Arrays.toString(RDFVizler.RDF_OUTPUT_FORMATS, "|")
                + ">]")
        ;

        formatter.printHelp(110, str.toString(), "java -jar rdfvizler", options, "");
    }

    public void execute() throws IOException {
        try {
            String output = rdfvizler.writeOutput(outputFormat);
            writeOutput(output);
        } catch (RuntimeException | IOException e) {
            throw e;
        }
    }

    private void writeOutput(String output) throws IOException {
        if (outputPath != null) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
            bw.write(output);
            bw.close();
        } else {
            console.println(output);
        }
    }
}
