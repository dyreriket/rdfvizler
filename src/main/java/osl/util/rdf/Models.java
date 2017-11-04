package osl.util.rdf;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;

import osl.util.Strings;

public abstract class Models {

    public static final String DEFAULTFORMAT = "TTL";

    // hiding constructor
    private Models() {
        throw new IllegalStateException("Utility class");
    }

    public static Model readModel(String file) {
        return readModel(file, FileUtils.guessLang(file, DEFAULTFORMAT));
    }

    public static Model readModel(String file, String serialisation) {
        if (serialisation == null) {
            return readModel(file);
        } else {
            return FileManager.get().loadModel(file, serialisation);
        }
    }

    public static String writeModel(Model model, String format) {
        StringWriter str = new StringWriter();
        model.write(str, format);
        String modelString = str.toString();
        str.flush();
        IOUtils.closeQuietly(str);
        return modelString;
    }

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

    public static String shortName(Model model, Resource resource) {
        return model.shortForm(resource.getURI());
    }

    public static String shortName(Model model, Collection<Resource> resources) {
        return "[" + Strings.toString(resources, r -> shortName(model, r), ", ") + "]";
    }

}
