package xyz.dyreriket.sau;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;

import xyz.dyreriket.sau.util.Arrays;
import xyz.dyreriket.sau.util.Models;

public class Sau {



    public static final String DEFAULT_RULES = "default.jrule";

    public static final Collection<String> INPUT_FORMATS = Models.RDF_FORMATS;

    public static final List<String> RDF_OUTPUT_FORMATS = Models.RDF_FORMATS;
    public static final List<String> DOT_OUTPUT_FORMATS = DotProcess.DOT_FORMATS;
    public static final List<String> TEXT_OUTPUT_FORMATS = Arrays.toUnmodifiableList("dot", "txt");
    
    public static final String DEFAULT_INPUT_FORMAT = Models.DEFAULT_RDF_FORMAT;
    public static final String DEFAULT_OUTPUT_FORMAT = DotProcess.DEFAULT_FORMAT;

    private String pathRDF;
    private String pathRules = DEFAULT_RULES;
    private String pathDotExec;
    private String inputFormat = DEFAULT_INPUT_FORMAT;

    public Sau(String pathRDF) {
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
        addPrefixes(model);
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
        RDF2DotParser parser = new RDF2DotParser(dotModel);
        String dot = parser.toDot();
        return dot;
    }

    public String writeDotImageOutput(String format) throws IOException {
        String dot = writeDotTextOutput();
        String output;
        if (pathDotExec == null) {
            output = DotProcess.runDot(dot, format);
        } else {
            output = DotProcess.runDot(pathDotExec, dot, format);
        }
        return output;
    }

    public String writeOutput(String format) throws IOException {
        String output = null;
        if (RDF_OUTPUT_FORMATS.contains(format)) {
            output = writeDotModel(format);
        } else if (DOT_OUTPUT_FORMATS.contains(format)) {
            output = writeDotImageOutput(format);
        } else if (TEXT_OUTPUT_FORMATS.contains(format)) {
            output = writeDotTextOutput();
        }
        return output;
    }
    
    private void addPrefixes(Model model) {
        // model.withDefaultMappings(PrefixMapping.Standard);
        model.setNsPrefix(SauVocabulary.NAMESPACE_PREFIX, SauVocabulary.NAMESPACE);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTR_PREFIX, SauVocabulary.NAMESPACE_ATTR);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTRNODE_PREFIX, SauVocabulary.NAMESPACE_ATTRNODE);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTREDGE_PREFIX, SauVocabulary.NAMESPACE_ATTREDGE);
    }
}
