package osl.rdfvizler.ui.cli;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;

import osl.rdfvizler.dot.DotModel;
import osl.rdfvizler.dot.DotProcess;

import osl.rdfvizler.dot.RDF2Dot;
import osl.util.rdf.Models;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class RDFVizlerCLI {

    private static final String defaultDotFormat = "svg";
    public static final String RDFVIZLER_RULES_PATH = "RDFVIZLER_RULES_PATH";
    // CLI options
    private static final String RULES = "rules";
    private static final String INPUT = "input";
    private static final String XML = "xml";
    private static final String EXEC = "exec";
    private static final String OUTPUT = "output";
    private static final String FORMATDOT = "dotformat";
    private static final String COPYNAME = "copyname";

    private String rulesPath, inputPath, outputPath, execPath, formatDot, formatRDF;
    private CommandLine l;

    public static void main(String[] args) throws IOException {
        RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI();
        if (rdfVizlerCLI.parse(args)) {
            rdfVizlerCLI.execute();
        }
    }

    private boolean parse(String[] args) {
        Options options = new Options();
        options.addOption("r", RULES, true, "Path to rules file");
        options.addOption("i", INPUT, true, "Path to RDF file");
        options.addOption("x", XML, false, "RDF format is RDF/XML. Default is " + Models.DefaultFormat);
        options.addOption("e", EXEC, true, "Path to dot executable. Default is " + DotProcess.DefaultExec);
        options.addOption("o", OUTPUT, true, "Output file. If omitted output to stdout");
        options.addOption("d", FORMATDOT, true, "Output format for image. Default is " + defaultDotFormat);
        options.addOption("c", COPYNAME, false, "Copy the name of the input argument for output name. Not with " + FORMATDOT);

        CommandLineParser parser = new DefaultParser();



        try {
            l = parser.parse(options, args);

            if (l.hasOption(OUTPUT) && l.hasOption(COPYNAME)) {
                throw new RuntimeException(OUTPUT + " and " + COPYNAME + " cannot both be selected at the same time");
            }

            rulesPath = getRulesPath();
            inputPath = require(INPUT);
            formatDot = want(FORMATDOT, defaultDotFormat);
            outputPath = getOutputPath();
            execPath = want(EXEC);
            formatRDF = l.hasOption(XML) ? "RDF/XML" : null;

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
                + "--" + INPUT + " <rdfFile> --" + RULES + " <rulesFile> "
                + "[--" + OUTPUT + " <outputFile> | --" + XML + "  | -" + FORMATDOT + " <arg>]", "", options, "");
    }

    private String getOutputPath() {
        if (l.hasOption(COPYNAME)) {
            return FilenameUtils.removeExtension(inputPath) + "." + formatDot;
        }
        return want(OUTPUT);
    }

    private String getRulesPath() {
        //List the two alternatives in prioritized order, pick the first non-null value
        //if both are null, return null
        return Arrays.stream(
                new String[]{ want(RULES), System.getenv(RDFVIZLER_RULES_PATH) })
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

	private  String want(String option, String defaultValue) {
		if (l.hasOption(option)) {
			return l.getOptionValue(option);
		} else {
			return defaultValue;
		}
	}


	private  String want(String option) {
		if (l.hasOption(option)) {
			return l.getOptionValue(option);
		} else {
			return null;
		}
	}

	private  String require(String option) throws MissingConfigurationException {
		if (l.hasOption(option)) {
			return l.getOptionValue(option);
		}
        throw new MissingConfigurationException("Missing value for option " + option);
	}

	private static class MissingConfigurationException extends Exception {
		private static final long serialVersionUID = 1169386320837465674L;
		MissingConfigurationException(String msg) {
			super(msg);
		}
	}
}
