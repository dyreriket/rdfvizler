package osl.rdfvizler.dot;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.Rule;

import osl.rdfvizler.dot.rules.Namespace;
import osl.rdfvizler.dot.rules.ShortValue;
import osl.rdfvizler.dot.rules.TypedValue;
import osl.util.rdf.Models;

public abstract class DotModel {

	static {
		BuiltinRegistry.theRegistry.register(new ShortValue());
		BuiltinRegistry.theRegistry.register(new Namespace());
		BuiltinRegistry.theRegistry.register(new TypedValue());
	}

	// apply rules to input RDF to saturate with DOT vocabulary
	public static Model getDotModel (String pathRDF, String formatRDF, String pathRules) throws IllegalArgumentException, IOException {
		Model model = Models.readModel(pathRDF, formatRDF);
		List<Rule> rules = Rule.rulesFromURL(pathRules);
		Model dotModel = Models.applyRules(model, rules);
		return dotModel;
	}

	// check that (1) URL resolves, (2) with code 200, and (3) content not larger than max limit.
	public static void checkURIInput (String path, int maxSize) throws IOException, IllegalArgumentException {
		HttpURLConnection connection;
		int code;
		try{
			URL u = new URL(path);
			connection = (HttpURLConnection) u.openConnection();
			connection.connect();
			code = connection.getResponseCode();
		} catch (java.net.MalformedURLException ex) {
			throw new IllegalArgumentException ("Error handling URI: '" + path + "': Malformed URL " + ex.getMessage());
		}
		if (code != 200) {
			throw new IllegalArgumentException ("Error retrieving URI: '" + path + "'. URI returned code " + code);
		}
		int size = connection.getContentLength();

		if (size > maxSize) {
			throw new IllegalArgumentException ("Error loading URI: '"+path+"'. File size ("+size+") exceeds the max file size set to: " + maxSize);
		}
	}

}
