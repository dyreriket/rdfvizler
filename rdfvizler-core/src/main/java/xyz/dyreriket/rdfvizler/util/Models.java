package xyz.dyreriket.rdfvizler.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

public abstract class Models {

    public enum RDFformat {
        rdf(FileUtils.langXMLAbbrev),
        ttl(FileUtils.langTurtle),
        nt(FileUtils.langNTriple),
        guess(null);

        private final String format;

        RDFformat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return this.format;
        }
    }

    public static final RDFformat DEFAULT_RDF_FORMAT = RDFformat.rdf;

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
        return readModel(file, FileUtils.guessLang(file, DEFAULT_RDF_FORMAT.getFormat()));
    }

    public static Model readModel(String file, RDFformat serialisation) {
        return FileManager.get().loadModel(file, serialisation.getFormat());
    }

    private static Model readModel(String file, String format) {
        if (format == null || format.isEmpty()) {
            return readModel(file);
        } else {
            return FileManager.get().loadModel(file, format);
        }
    }

    public static String shortName(Model model, Collection<Resource> resources) {
        return "[" + Strings.toString(resources, r -> shortName(model, r), ", ") + "]";
    }

    public static String shortName(Model model, Resource resource) {
        return model.shortForm(resource.getURI());
    }

    public static String writeModel(Model model, RDFformat format) {
        String modelString = "";
        try (StringWriter str = new StringWriter()) {
            model.write(str, format.getFormat());
            modelString = str.toString();
            str.flush();
        } catch (IOException e) {
            System.err.println("Error writing model to string:");
            e.printStackTrace();
        }
        return modelString;
    }

    // hiding constructor
    private Models() {
        throw new IllegalStateException("Utility class");
    }

}
