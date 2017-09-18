package osl.rdfviz.rules.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.vocabulary.RDF;

import osl.rdfviz.Strings;

public abstract class BuiltInUtils {
	
	public static final Comparator<Node> stringValueComparator = 
			(Node p1, Node p2) -> p1.toString().compareTo(p2.toString());

	public static String getTypes (Node node, RuleContext context) {
		List<Node> types = new ArrayList<>();
		context.find(node, RDF.type.asNode(), Node.ANY).forEachRemaining(
				t -> types.add(t.getObject()));
		Collections.sort(types, stringValueComparator);
		return Strings.toString(types, t -> getShortForm(t, context), ", ");
	}

	public static String getShortForm(Node node, RuleContext context) {
		return node.toString(context.getGraph().getPrefixMapping());
	}
	
	public static String getShortForm(String URI, RuleContext context) {
		return context.getGraph().getPrefixMapping().shortForm(URI);
	}


}
