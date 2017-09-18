package osl.rdfviz;

import java.io.StringWriter;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;

import osl.rdfviz.rules.builtin.*;

public abstract class Models {

	static {
		BuiltinRegistry.theRegistry.register(new ShortValue());
		BuiltinRegistry.theRegistry.register(new Namespace());
		BuiltinRegistry.theRegistry.register(new TypedValue());
	}

	public static Model readModel (String file) {
		return readModel(file, FileUtils.guessLang(file, "TTL"));
	}

	public static Model readModel (String file, String serialisation) {
		return FileManager.get().loadModel(file, serialisation);
	}

	public static String writeModel (Model model, String format) {
		StringWriter str = new StringWriter();
		model.write(str, format);
		String modelString = str.toString();
		str.flush();
		return modelString;
	}

	public static Model applyRules (Model model, List<Rule> rules) {	
		Reasoner reasoner = new GenericRuleReasoner(rules);
		Model inf = ModelFactory.createInfModel(reasoner, model);
		inf.setNsPrefixes(model);
		return inf;
	}

}
