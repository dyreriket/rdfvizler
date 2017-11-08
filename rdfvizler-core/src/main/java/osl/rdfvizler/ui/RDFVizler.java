package osl.rdfvizler.ui;

import java.io.IOException;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;

import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.dot.RDF2DotParser;
import osl.util.Arrays;
import osl.util.rdf.Models;

public class RDFVizler {

    public static final String DEFAULT_RULES = "default.jrule";

    public static final String[] INPUT_FORMATS = Models.RDF_FORMATS;

    public static final String[] RDF_OUTPUT_FORMATS = Models.RDF_FORMATS;
    public static final String[] DOT_OUTPUT_FORMATS = DotProcess.DOT_FORMATS;
    public static final String[] TEXT_OUTPUT_FORMATS = { "dot", "txt" };
    
    public static final String DEFAULT_INPUT_FORMAT = Models.DEFAULT_RDF_FORMAT;
    public static final String DEFAULT_OUTPUT_FORMAT = DotProcess.DEFAULT_FORMAT;

    private String pathRDF;
    private String pathRules = DEFAULT_RULES;
    private String pathDotExec = DotProcess.DEFAULT_EXEC;
    private String inputFormat = DEFAULT_INPUT_FORMAT;

    public RDFVizler(String pathRDF) {
        this.pathRDF = pathRDF;
    }

    public void setInputFormat(String inputFormat) {
        if (!Models.isRDFFormat(inputFormat)) {
            throw new IllegalArgumentException("Not a reckognised RDF serialisation format: '" + inputFormat + "'. "
                    + "Expected one of the following formats: " + Models.RDF_FORMATS.toString());
        } else {
            this.inputFormat = inputFormat;
        }
    }

    public void setRulesPath(String path) {
        this.pathRules = path;
    }

    public void setDotExecutable(String path) {
        this.pathDotExec = path;
    }

    public Model getInputModel() {
        return Models.readModel(pathRDF, inputFormat);
    }

    public Model getDotModel() {
        Model model = getInputModel();
        List<Rule> rules = getRules();
        Model dotModel = Models.applyRules(model, rules);
        return dotModel;
    }
    
    public List<Rule> getRules() {
        return Rule.rulesFromURL(pathRules);
    }

    public String writeInputModel(String format) {
        return Models.writeModel(getInputModel(), format);
    }

    public String writeDotModel(String format) {
        return Models.writeModel(getDotModel(), format);
    }

    public String writeDotTextOutput() throws IOException {
        Model dotModel = getDotModel();
        String dot = RDF2DotParser.toDot(dotModel);
        return dot;
    }

    public String writeDotImageOutput(String format) throws IOException {
        String dot = writeDotTextOutput();
        String output = DotProcess.runDot(pathDotExec, dot, format);
        return output;
    }

    public String writeOutput(String format) throws IOException {
        String output = null;
        if (Arrays.inArray(RDF_OUTPUT_FORMATS, format)) {
            output = writeDotModel(format);
        } else if (Arrays.inArray(DOT_OUTPUT_FORMATS, format)) {
            output = writeDotImageOutput(format);
        } else if (Arrays.inArray(TEXT_OUTPUT_FORMATS, format)) {
            output = writeDotTextOutput();
        }
        return output;
    }
}
