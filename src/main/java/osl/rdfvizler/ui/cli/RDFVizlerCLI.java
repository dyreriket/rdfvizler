package osl.rdfvizler.ui.cli;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;

import osl.rdfvizler.dot.DotModel;
import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.dot.RDF2Dot;
import osl.util.rdf.Models;


public class RDFVizlerCLI extends CLI {


    public static final String RDFVIZLER_RULES_PATH = "RDFVIZLER_RULES_PATH";

    private static final String defaultDotFormat = "svg";

    // CLI options
    private static final String OPT_RULES = "rules";
    private static final String OPT_INPUT = "input";
    private static final String OPT_XML = "xml";
    private static final String OPT_EXEC = "exec";
    private static final String OPT_OUTPUT = "output";
    private static final String OPT_FORMATDOT = "dotformat";
    private static final String OPT_COPYNAME = "copyname";

    private String rulesPath;
    private String inputPath;
    private String outputPath;
    private String execPath;
    private String formatDot;
    private String formatRDF;

    public static void main(String[] args) throws IOException {
        RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI();
        if (rdfVizlerCLI.parse(args)) {
            rdfVizlerCLI.execute();
        }
    }

    private boolean parse(String[] args) {
        Options options = new Options();
        options.addOption("r", OPT_RULES, true, "Path to rules file");
        options.addOption("i", OPT_INPUT, true, "Path to RDF file");
        options.addOption("x", OPT_XML, false,
                "RDF format is RDF/XML. Default is ttl");
        options.addOption("e", OPT_EXEC, true,
                "Path to dot executable. Default is " + DotProcess.DEFAULT_EXEC);
        options.addOption("o", OPT_OUTPUT, true, "Output file. If omitted output to stdout");
        options.addOption("d", OPT_FORMATDOT, true,
                "Output format for image. Default is " + defaultDotFormat);
        options.addOption("c", OPT_COPYNAME, false,
                "Copy the name of the input argument for output name. Not with " + OPT_FORMATDOT);

        CommandLineParser parser = new DefaultParser();



        try {
            line = parser.parse(options, args);

            if (line.hasOption(OPT_OUTPUT) && line.hasOption(OPT_COPYNAME)) {
                throw new RuntimeException(OPT_OUTPUT + " and " + OPT_COPYNAME + " cannot both be selected at the same time");
            }

            rulesPath = getRulesPath();
            inputPath = require(OPT_INPUT);
            formatDot = want(OPT_FORMATDOT, defaultDotFormat);
            outputPath = getOutputPath();
            execPath = want(OPT_EXEC);
            formatRDF = line.hasOption(OPT_XML) ? "RDF/XML" : null;

        } catch (RuntimeException | ParseException | MissingConfigurationException e) {
            printHelp(options, e);
            return false;
        }
        return true;
    }

    private void printHelp(Options options, Exception e) {
        System.out.println(e.getMessage());
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(110, "java -jar \\ \n          rdfvizler "
                + "--" + OPT_INPUT + " <rdfFile> --" + OPT_RULES + " <rulesFile> "
                + "[--" + OPT_OUTPUT + " <outputFile> | --" + OPT_XML + "  | -" + OPT_FORMATDOT + " <arg>]", "", options, "");
    }

    private String getOutputPath() {
        if (line.hasOption(OPT_COPYNAME)) {
            return FilenameUtils.removeExtension(inputPath) + "." + formatDot;
        }
        return want(OPT_OUTPUT);
    }

    private String getRulesPath() {
        //List the two alternatives in prioritized order, pick the first non-null value
        //if both are null, return null
        return Arrays.stream(
                new String[]{ want(OPT_RULES), System.getenv(RDFVIZLER_RULES_PATH) })
                    .filter(x -> x !=null)
                    .findFirst()
                    .orElse(null);
    }

	private void execute() throws IOException {
		try {

            DotProcess dotProcess = (execPath != null)
                    ? new DotProcess(execPath)
                    : new DotProcess();

			Model model = (rulesPath == null)
                    ? DotModel.getDotModel(inputPath, formatRDF)
                    : DotModel.getDotModel(inputPath, formatRDF, rulesPath);

			String dot = RDF2Dot.toDot(model);

			String out;
			if (formatDot.equalsIgnoreCase("ttl")) {
				out = Models.writeModel(model, "TTL");
			} else if (formatDot.equalsIgnoreCase("dot")) {
				out = dot;
			} else { 
				out = dotProcess.runDot(dot, formatDot);
			}

			if (outputPath!=null) {
				FileWriter fw = new FileWriter(outputPath);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(out);
				bw.close();
			} else {
				System.out.println(out);
			}

		} catch (RuntimeException | IOException e) {
			throw e;
		}
	}


}
