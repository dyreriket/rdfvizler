package xyz.dyreriket.rdfvizler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.shared.PrefixMapping;

import xyz.dyreriket.rdfvizler.DotProcess.ImageOutputFormat;
import xyz.dyreriket.rdfvizler.util.Models;
import xyz.dyreriket.rdfvizler.util.Models.RDFformat;

public class RDFVizler {

    private String pathRules = DEFAULT_RULES.toString();
    private String pathDotExec = DotProcess.DEFAULT_DOT_EXEC;
    private Models.RDFformat inputFormat = null; // null means we guess format.
    private boolean skipRules = false;
    
    private BiFunction<Enum<?>[], String, Boolean> contains = (es, e) -> {
        return Arrays.stream(es).allMatch(t -> t.name().equals(e));
    };
    
    public enum RDFInputFormat { ttl, rdf, nt, guess }
    
    public static final URI DEFAULT_RULES = makeURI("http://rdfvizler.dyreriket.xyz/rules/rdf.jrule");

    private static void addPrefixes(Model model) {
        model.withDefaultMappings(PrefixMapping.Standard);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_PREFIX, RDFVizlerVocabulary.NAMESPACE);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTR_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTR);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTRNODE_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTRNODE);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTREDGE_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTREDGE);
    }

    private static List<Rule> getRules(String path) {
        return Rule.rulesFromURL(path);
    }

    public static URI makeURI(String urlString) {
        try {
            return new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
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
        if (this.inputFormat == null) {
            return Models.readModel(pathRDF);
        } else {
            return Models.readModel(pathRDF, this.inputFormat);
        }
    }

    public void setDotExecutable(String path) {
        this.pathDotExec = path;
    }

    public void setInputFormat(RDFInputFormat inputFormat) {
        if (RDFInputFormat.guess == inputFormat) {
            this.inputFormat = null;
        } else {
            this.inputFormat = Models.RDFformat.valueOf(inputFormat.toString());    
        }
        
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
