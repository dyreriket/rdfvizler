package osl.rdfvizler.dot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;

import osl.util.Strings;
import osl.util.rdf.Models;
import osl.util.rdf.vocab.DotVocabulary;

public abstract class RDF2Dot {

	private static final String STRICT = "strict ";
	private static final String GRAPH = "graph ";
	private static final String DIGRAPH = "digraph ";
	private static final String SUBGRAPH = "subgraph ";
	private static final String NODE = "node ";
	private static final String EDGE = "edge ";

	private static final String TAB = "   ";

	private static final String EDGE_OP_DIGRAPH = " -> ";
	private static final String EDGE_OP_GRAPH   = " -- ";
	private static String EDGE_OP;
	
	private static void addPrefixes (Model model) {
		model.withDefaultMappings(PrefixMapping.Standard);
		model.setNsPrefix(DotVocabulary.NAMESPACE_PREFIX, DotVocabulary.NAMESPACE);
		model.setNsPrefix(DotVocabulary.NAMESPACE_ATTR_PREFIX, DotVocabulary.NAMESPACE_ATTR);
		model.setNsPrefix(DotVocabulary.NAMESPACE_ATTRNODE_PREFIX, DotVocabulary.NAMESPACE_ATTRNODE);
		model.setNsPrefix(DotVocabulary.NAMESPACE_ATTREDGE_PREFIX, DotVocabulary.NAMESPACE_ATTREDGE);
	}

	public static String toDot (Model model) {
		addPrefixes(model);
		Resource rootGraph = getRootGraph(model);
		return parseRootGraph(model, rootGraph);
	}

	private static String parseRootGraph(Model model, Resource rootGraph) {
		StringBuilder str = new StringBuilder();

		// strict or not
		if (Models.isOfType(model, rootGraph, DotVocabulary.StrictDiRootGraph) 
				|| Models.isOfType(model, rootGraph, DotVocabulary.StrictRootGraph)) {
			str.append(STRICT);
		}

		// digraph or not
		String graphtype;
		if (Models.isOfType(model, rootGraph, DotVocabulary.StrictDiRootGraph) 
				|| Models.isOfType(model, rootGraph, DotVocabulary.DiRootGraph)) {
			graphtype = DIGRAPH;
			EDGE_OP = EDGE_OP_DIGRAPH;
		} else {
			graphtype = GRAPH;
			EDGE_OP = EDGE_OP_GRAPH;
		}
		str.append(parseGraph(graphtype, rootGraph, ""));
		return str.toString();
	}

	private static String parseGraph (String graphtype, Resource resource, String space) {
		String indent = space + TAB;
		StringBuilder str = new StringBuilder();
		str
		.append(space)
		.append(graphtype)
		.append(getID(resource))
		.append(" {\n"); // start graph

		// graph attributes
		String attr = parseAttributes(resource, DotVocabulary.NAMESPACE_ATTR);
		if(attr.length() > 0) {
			str.append(indent).append(attr).append(";\n"); }
		
		// default node and edge attributes for graph
		String attrnode = parseAttributeList(resource, DotVocabulary.NAMESPACE_ATTRNODE);
		if (!attrnode.isEmpty()) {
			str.append(indent).append(NODE).append(attrnode).append(";\n"); }
		String attredge = parseAttributeList(resource, DotVocabulary.NAMESPACE_ATTREDGE);
		if (!attredge.isEmpty()) {
			str.append(indent).append(EDGE).append(attredge).append(";\n"); }

		// nodes and egdes
		str.append(parseElements(resource, DotVocabulary.hasNode, x -> parseNode(x), indent));
		str.append(parseElements(resource, DotVocabulary.hasEdge, x -> parseEdge(x), indent));

		// subgraphs
		for (Statement subgraph : resource.listProperties(DotVocabulary.hasSubGraph).toList()) {
			str.append("\n").append(parseGraph(SUBGRAPH, subgraph.getObject().asResource(), indent));
		}

		str.append(space).append("}\n"); // end graph
		return str.toString();
	}

	private static String parseElements (Resource resource, Property element, Function<Resource, String> parser, String space) {
		String str = Strings.toString(
				resource.listProperties(element).toList(), 
				s-> parser.apply(s.getObject().asResource()),
				space);
		if (!str.isEmpty()) {
			return "\n" 
					+ space + "// " + element.getLocalName().replaceAll("has", "").toUpperCase() + "S\n" 
					+ space + str;
		}
		return str;
	}

	private static String parseAttributes(Resource resource, String namespace) {
		List<Statement> stmts = resource.listProperties().toList();
		stmts.removeIf(s -> !s.getPredicate().getNameSpace().equals(namespace));
		return Strings.toString(stmts, 
				s -> s.getPredicate().getLocalName() + " = \"" + s.getObject().toString() + "\"",
				"; ");
	}

	private static String parseAttributeList (Resource resource, String namespace) {
		String attrs = parseAttributes(resource, namespace);
		if (attrs.isEmpty()) {
			return "";
		} else {
			return " [ " + attrs + " ]";	
		}
	}

	private static String parseNode(Resource resource) {
	    StringBuilder str = new StringBuilder();
		str
		.append(getID(resource)) // ID
		.append(parseAttributeList(resource,DotVocabulary.NAMESPACE_ATTR))
		.append(";\n");
		return str.toString();
	}

	private static String parseEdge(Resource resource) {
	    StringBuilder str = new StringBuilder();
		str
		.append(getID(resource.getRequiredProperty(DotVocabulary.hasSource).getObject().asResource())) // source node
		.append(EDGE_OP)
		.append(getID(resource.getRequiredProperty(DotVocabulary.hasTarget).getObject().asResource())) // target node
		.append(parseAttributeList(resource, DotVocabulary.NAMESPACE_ATTR))
		.append(";\n");
		return str.toString();
	}

	private static String getID (Resource resource) {
		String id;
		Statement statement = resource.getProperty(DotVocabulary.hasID);
		if (statement == null) {
			id = resource.toString();
		} else {
			id = statement.getObject().toString();
		}
		return "\"" + id + "\"";
	}

	private static Resource getRootGraph (Model model) {
		List<Resource> graphs = new ArrayList<>();
		for (Resource g : DotVocabulary._Graphs) {
			graphs.addAll(Models.listInstancesOfClass(model, g));
		}
		if (graphs.size() != 1) {
			throw new IllegalArgumentException("Error getting root graph. Expected exactly 1 instance, but found " 
					+ graphs.size() + ": " + Models.shortName(model, graphs));
		}
		return graphs.get(0);		
	}
}
