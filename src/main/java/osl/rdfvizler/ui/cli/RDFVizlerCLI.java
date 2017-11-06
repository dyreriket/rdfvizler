package osl.rdfvizler.ui.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.ui.RDFVizler;
import osl.util.Arrays;

public class RDFVizlerCLI extends CLI {

    private static final String ENV_RDFVIZLER_RULES_PATH = "RDFVIZLER_RULES_PATH";
    private static final String ENV_RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";

    // CLI options
    private static final String OPT_RULES = "rules";
    private static final String OPT_IN = "in";
    private static final String OPT_INFORMAT = "inFormat";
    private static final String OPT_DOTEXEC = "dotExec";
    private static final String OPT_OUT = "out";
    private static final String OPT_OUTFORMAT = "outFormat";
    private static final String OPT_OUTEXT = "outExtension";

    private RDFVizler rdfvizler;

    private String outputPath;
    private String outputFormat;

    public static void main(String[] args) throws IOException {
        RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI();
        if (rdfVizlerCLI.parseOptions(rdfVizlerCLI.buildOptions(), args)) {
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

    private boolean parseOptions(Options options, String[] args) {
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
        System.out.println(e.getMessage());
        HelpFormatter formatter = new HelpFormatter();

        StringBuilder str = new StringBuilder();
        str.append("java -jar\n\trdfvizler")
            .append("\n\t\t --" + OPT_IN + " <rdfFile>")
            .append("\n\t\t [--" + OPT_RULES + " <rulesFile>]")
            .append("\n\t\t [--" + OPT_INFORMAT + " <" + Arrays.toString(RDFVizler.INPUT_FORMATS, "|") + ">]")
            .append("\n\t\t [--" + OPT_OUT + " <outputFile>")
            .append(" --" + OPT_OUTEXT + "]")
            .append("\n\t\t [--" + OPT_OUTFORMAT 
                + " <" 
                + Arrays.toString(RDFVizler.DOT_OUTPUT_FORMATS, "|")
                + "|" + Arrays.toString(RDFVizler.TEXT_OUTPUT_FORMATS, "|") 
                + "|" + Arrays.toString(RDFVizler.RDF_OUTPUT_FORMATS, "|")
                + ">]")
        ;

        formatter.printHelp(110, str.toString(), "", options, "");
    }

    private void execute() throws IOException {
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
            System.out.println(output);
        }
    }
}
