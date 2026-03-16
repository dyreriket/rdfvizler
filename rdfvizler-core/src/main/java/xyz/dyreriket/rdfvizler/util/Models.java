package xyz.dyreriket.rdfvizler.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;

public abstract class Models {


    public static final Lang DEFAULT_RDF_FORMAT = Lang.TURTLE;
    public static final String DEFAULT_RDF_FORMAT_NAME = DEFAULT_RDF_FORMAT.getName();

    public static Model applyRules(Model model, List<Rule> rules) {
        Reasoner reasoner = new GenericRuleReasoner(rules);
        Model inf = ModelFactory.createInfModel(reasoner, model);
        inf.setNsPrefixes(model);
        return inf;
    }

    public static boolean isOfType(Model model, Resource instance, Resource klass) {
        return model.contains(instance, RDF.type, klass);
    }

    public static List<Resource> listInstancesOfClass(Model model, Resource cls) {
        return model.listResourcesWithProperty(RDF.type, cls).toList();
    }

    public static Model readModel(String file) {
        return readModel(file, RDFLanguages.filenameToLang(file, DEFAULT_RDF_FORMAT));
    }

    public static Model readModel(String file, Lang lang) {
        Model model = ModelFactory.createDefaultModel();
        RDFParser.source(file)
             .forceLang(lang)
             .parse(model);
        return model;
    }

    private static Model readModel(String file, String format) {
        if (format == null || format.isEmpty()) {
            return readModel(file);
        } else {
            return readModel(file, RDFLanguages.shortnameToLang(format));
        }
    }

    public static String shortName(Model model, Collection<Resource> resources) {
        return "[" + Strings.toString(resources, r -> shortName(model, r), ", ") + "]";
    }

    public static String shortName(Model model, Resource resource) {
        return model.shortForm(resource.getURI());
    }

    public static String writeModel(Model model, String lang) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFWriter.create()
             .source(model)
             .lang(RDFLanguages.shortnameToLang(lang))
             .output(out);
        return out.toString(StandardCharsets.UTF_8);
    }

    // hiding constructor
    private Models() {
        throw new IllegalStateException("Utility class");
    }

}
