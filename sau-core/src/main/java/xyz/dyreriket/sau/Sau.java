package xyz.dyreriket.sau;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;

import xyz.dyreriket.sau.DotProcess.ImageOutputFormat;
import xyz.dyreriket.sau.util.Models;
import xyz.dyreriket.sau.util.Models.RDFformat;

public class Sau {

    public static final String DEFAULT_RULES = "default.jrule";

    private static void addPrefixes(Model model) {
        // model.withDefaultMappings(PrefixMapping.Standard);
        model.setNsPrefix(SauVocabulary.NAMESPACE_PREFIX, SauVocabulary.NAMESPACE);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTR_PREFIX, SauVocabulary.NAMESPACE_ATTR);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTRNODE_PREFIX, SauVocabulary.NAMESPACE_ATTRNODE);
        model.setNsPrefix(SauVocabulary.NAMESPACE_ATTREDGE_PREFIX, SauVocabulary.NAMESPACE_ATTREDGE);
    }

    private static List<Rule> getRules(String path) {
        return Rule.rulesFromURL(path);
    }

    private String pathRules = DEFAULT_RULES;
    private String pathDotExec = DotProcess.DEFAULT_DOT_EXEC;

    private Models.RDFformat inputFormat = Models.DEFAULT_RDF_FORMAT;

    private boolean skipRules = false;

    private BiFunction<Enum<?>[], String, Boolean> contains = (es, e) -> {
        return Arrays.stream(es).allMatch(t -> t.name().equals(e));
    };

    public Sau() {
    }

    private Model getRDFDotModel(String pathRDF) {
        Model model = this.readModel(pathRDF);
        addPrefixes(model);
        if (!this.skipRules) {
            List<Rule> rules = getRules(this.pathRules);
            model = Models.applyRules(model, rules);
        }
        return model;
    }

    private Model readModel(String pathRDF) {
        return Models.readModel(pathRDF, this.inputFormat);
    }

    public void setDotExecutable(String path) {
        this.pathDotExec = path;
    }

    public void setInputFormat(Models.RDFformat inputFormat) {
        this.inputFormat = inputFormat;
    }

    public void setRulesPath(String path) {
        this.pathRules = path;
    }

    public void setSkipRules(boolean skipRules) {
        this.skipRules = skipRules;
    }

    public String write(String pathRDF, String format) throws IOException {
        if (this.contains.apply(RDFformat.values(), format)) {
            return this.writeRDFDotModel(pathRDF, Models.RDFformat.valueOf(format));
        } else if (this.contains.apply(DotProcess.TextOutputFormat.values(), format)) {
            return this.writeDotGraph(pathRDF);
        } else if (this.contains.apply(DotProcess.ImageOutputFormat.values(), format)) {
            return this.writeDotImage(pathRDF, ImageOutputFormat.valueOf(format));
        } else {
            throw new IllegalArgumentException("Error processing: " + pathRDF + ". Unexpected format: " + format);
        }
    }

    public String writeDotGraph(String pathRDF) {
        Model model = this.getRDFDotModel(pathRDF);
        RDF2DotParser parser = new RDF2DotParser(model);
        return parser.toDot();
    }

    public String writeDotImage(String pathRDF, DotProcess.ImageOutputFormat format) throws IOException {
        String dot = this.writeDotGraph(pathRDF);
        return DotProcess.runDot(this.pathDotExec, dot, format);
    }

    public String writeRDFDotModel(String pathRDF, Models.RDFformat format) {
        Model model = this.getRDFDotModel(pathRDF);
        return Models.writeModel(model, format);
    }
}
