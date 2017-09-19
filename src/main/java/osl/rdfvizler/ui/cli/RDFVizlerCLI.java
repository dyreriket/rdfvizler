package osl.rdfvizler.ui.cli;

import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Model;

import osl.rdfvizler.dot.DotModel;
import osl.rdfvizler.dot.DotProcess;

import osl.rdfvizler.dot.RDF2Dot;
import osl.util.rdf.Models;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;

public class RDFVizlerCLI {

    public static final String RULES = "rules";
    public static final String INPUT = "input";
    public static final String XML = "xml";
    public static final String EXEC = "exec";
    public static final String OUTPUT = "output";
    private static final String FORMATDOT = "dotformat";
    private Options options;

    private String rulesPath, inputPath, outputPath, execPath, formatDot, formatRDF;


	public static void main(String[] args) throws IOException {
	    RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI();

	    if (rdfVizlerCLI.parse(args))
	        rdfVizlerCLI.execute();

    }

    private boolean parse(String[] args) {
        // create Options object
        options = new Options();


        //add options
        options.addOption("r", RULES, true, "Rules file");
        options.addOption("i", INPUT, true, "Input rdf");
        options.addOption("x", XML, false, "Input is RDF/XML. Default is ttl");
        options.addOption("e", EXEC, true, "dot executable");
        options.addOption("o", OUTPUT, true, "output file. If omitted output to stdout");
        options.addOption("d", FORMATDOT, true, "output format for image. defaults to svg");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine l = parser.parse(options, args);

            outputPath = want(OUTPUT, l);
            formatDot = want(FORMATDOT, l, "svg");
            rulesPath = require(RULES, l);
            inputPath = require(INPUT, l);
            execPath = require(EXEC, l);
            formatRDF = l.hasOption(XML) ? "RDF/XML" : null;

        } catch (ParseException | MissingConfigurationException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(110, "java -jar \\ \n          rdfvizler --input <rdfFile> --rules <rulesFile> [--output <outputFile> | -x  | -dotformat <arg>]", "" , options , "\nbe nice!");
            return false;
        }
        return true;
    }




    private void execute() throws IOException {
            try {

                Model model = DotModel.getDotModel(inputPath, formatRDF, rulesPath);
                String dot = RDF2Dot.toDot(model);

                DotProcess dotProcess = new DotProcess(execPath);
                String out;

                if (formatDot.equalsIgnoreCase("ttl"))
                    out = Models.writeModel(model, "TTL");
                else if (formatDot.equalsIgnoreCase("dot"))
                    out = dot;
                else  out = dotProcess.runDot(dot, formatDot);


                if (outputPath!=null) {
                    FileWriter fw = new FileWriter(outputPath);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(out);
                    bw.close();
                }
                else
                    System.out.println(out);

            } catch (RuntimeException | IOException e) {
                throw e;
            }
        }



    private String want(String option, CommandLine l, String dflt) {
        if (l.hasOption(option))
            return l.getOptionValue(option);
        else
            return dflt;
    }


    private String want(String option, CommandLine l) {
        if (l.hasOption(option))
            return l.getOptionValue(option);
        else
            return null;
    }

    private static String require(String option, CommandLine l) throws MissingConfigurationException {
        if (l.hasOption(option))
            return l.getOptionValue(option);
        else
            missing(option);
        return "";
    }

    private static void missing(String option) throws MissingConfigurationException {
	    throw new MissingConfigurationException("Missing value for options " + option);
    }

    private static class MissingConfigurationException extends Exception {
	    public MissingConfigurationException(String msg) {
	        super(msg);
        }
    }
}
