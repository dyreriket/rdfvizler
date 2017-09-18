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
	    RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI(args);
	    rdfVizlerCLI.execute();

    }

    private RDFVizlerCLI(String[] args) {
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
            formatRDF = l.hasOption(XML) ? "rdf" : "ttl";

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    private void execute() throws IOException {
        //protected static void execute(String dotExec, String pathRDF, String pathRules, String formatRDF, String formatDot, String outputFile) throws


            // default values
            try {

//            Misc.checkURIInput(pathRDF, MaxFileSize);
                //          Misc.checkURIInput(pathRules, MaxFileSize);

                Model model = DotModel.getDotModel(inputPath, formatRDF, rulesPath);
                String dot = RDF2Dot.toDot(model);

                DotProcess dotProcess = new DotProcess(execPath);
                String out;
                String mimetype;

                if (formatDot.equalsIgnoreCase("svg")) {
                    out = dotProcess.runDot(dot, formatDot);
                } else if (formatDot.equals("png")) {
                    out = dotProcess.runDot(dot, formatDot);
                } else if (formatDot.equals("ttl")) {
                    out = Models.writeModel(model, "TTL");
                } else {
                    out = dot;
                }

                FileWriter fw = new FileWriter(outputPath);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(out);
                bw.close();

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

    private static String require(String option, CommandLine l) {
        if (l.hasOption(option))
            return l.getOptionValue(option);
        else
            missing(option);

        return ""; //never reached
    }

    private static void missing(String option) {
	    throw new RuntimeException("Missing value for options " + option);
    }

}
