package xyz.dyreriket.rdfvizler;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.shared.PrefixMapping;
import xyz.dyreriket.rdfvizler.util.Models;

public class RDFVizler {

    private String pathRules = null; // null means use bundled classpath rules
    private Lang inputFormat = null; // null means we guess format.
    private boolean skipRules = false;

    public static final URI DEFAULT_RULES = makeURI("http://rdfvizler.dyreriket.xyz/rules/rdf.jrule");

    private static URI makeURI(String urlString) {
        try {
            return new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void addPrefixes(Model model) {
        model.withDefaultMappings(PrefixMapping.Standard);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_PREFIX, RDFVizlerVocabulary.NAMESPACE);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTR_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTR);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTRNODE_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTRNODE);
        model.setNsPrefix(RDFVizlerVocabulary.NAMESPACE_ATTREDGE_PREFIX, RDFVizlerVocabulary.NAMESPACE_ATTREDGE);
    }

    public static List<Rule> getRules(String path) {
        return Rule.rulesFromURL(path);
    }

    public static List<Rule> getRules(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return Rule.parseRules(Rule.rulesParserFromReader(reader));
        } catch (IOException e) {
            throw new RuntimeException("Error reading rules from stream", e);
        }
    }


    private Model readModel(String pathRDF) {
        if (this.inputFormat == null) {
            return Models.readModel(pathRDF);
        } else {
            return Models.readModel(pathRDF, this.inputFormat);
        }
    }

    public Model getRDFDotModel(Model model, List<Rule> rules) {
        addPrefixes(model);
        model = Models.applyRules(model, rules);
        return model;
    }

    public Model getRDFDotModel(Model model) {
        List<Rule> rules = this.pathRules == null
            ? getRules(RDFVizler.class.getResourceAsStream("/rdf.jrule"))
            : getRules(this.pathRules);
        return getRDFDotModel(model, rules);
    }

    private Model getRDFDotModel(String pathRDF) {
        return getRDFDotModel(this.readModel(pathRDF));
    }

    public void setInputFormat(String lang) {
        if (lang == null || lang.isEmpty()) {
            this.inputFormat = null;
        } else {
            Lang l = RDFLanguages.shortnameToLang(lang);
            if (l == null) {
                l = RDFLanguages.fileExtToLang(lang);
            }
            if (l == null) {
                throw new IllegalArgumentException("Unknown RDF language/format: '" + lang + "'");
            }
            this.inputFormat = l;
        }
    }

    public void setRulesPath(String path) {
        this.pathRules = path;
    }

    public void setSkipRules(boolean skipRules) {
        this.skipRules = skipRules;
    }

    public String write(String pathRDF, String format) {
        if (RDFLanguages.shortnameToLang(format) != null) {
            return this.writeRDFDotModel(pathRDF, format);
        } else {
            return this.getDotImage(writeDotGraph(pathRDF), Format.valueOf(format));
        }
    }


    public String writeDotGraph(Model model) {
        Model dotModel = this.skipRules ? model : this.getRDFDotModel(model);
        RDF2DotParser parser = new RDF2DotParser(dotModel);
        return parser.toDot();
    }

    public String writeDotGraph(String pathRDF) {
        return writeDotGraph(readModel(pathRDF));
    }

    public String getDotImage(String dot, String format) {
        return getDotImage(dot, Format.valueOf(format));
    }

    public String getDotImage(String dot, Format format) {
        return Graphviz.fromString(dot)
            .render(format)
            .toString();
    }

    public String writeRDFDotModel(String pathRDF, String lang) {
        Model model = this.getRDFDotModel(pathRDF);
        return Models.writeModel(model, lang);
    }
}
