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
import osl.util.rdf.vocab.DOT;

public abstract class RDF2Dot {

	private final static String 
	STRICT = "strict ",
	GRAPH = "graph ",
	DIGRAPH = "digraph ",
	SUBGRAPH = "subgraph ",
	NODE = "node ",
	EDGE = "edge ";

	private static final String TAB = "   ";

	private static String EDGE_OP;
	private static final String EDGE_OP_DIGRAPH = " -> ";
	private static final String EDGE_OP_GRAPH   = " -- ";
	
	private static void addPrefixes (Model model) {
		model.withDefaultMappings(PrefixMapping.Standard);
		model.setNsPrefix(DOT.NAMESPACE_PREFIX, DOT.NAMESPACE);
		model.setNsPrefix(DOT.NAMESPACE_ATTR_PREFIX, DOT.NAMESPACE_ATTR);
		model.setNsPrefix(DOT.NAMESPACE_ATTRNODE_PREFIX, DOT.NAMESPACE_ATTRNODE);
		model.setNsPrefix(DOT.NAMESPACE_ATTREDGE_PREFIX, DOT.NAMESPACE_ATTREDGE);
	}

	public static String toDot (Model model) {
		addPrefixes(model);
		Resource rootGraph = getRootGraph(model);
		return parseRootGraph(model, rootGraph);
	}

	private static String parseRootGraph(Model model, Resource rootGraph) {
		StringBuffer str = new StringBuffer();

		// strict or not
		if (Models.isOfType(model, rootGraph, DOT.StrictDiRootGraph) 
				|| Models.isOfType(model, rootGraph, DOT.StrictRootGraph)) {
			str.append(STRICT);
		}

		// digraph or not
		String graphtype;
		if (Models.isOfType(model, rootGraph, DOT.StrictDiRootGraph) 
				|| Models.isOfType(model, rootGraph, DOT.DiRootGraph)) {
			graphtype = DIGRAPH;
			EDGE_OP = EDGE_OP_DIGRAPH;
		} else {
			graphtype = GRAPH;
			EDGE_OP = EDGE_OP_GRAPH;
		}
		str.append(parseGraph(graphtype, rootGraph, ""));
		return str.toString();
	}

	private static String parseGraph (String graphtype, Resource r, String space) {
		String indent = space + TAB;
		StringBuffer str = new StringBuffer();
		str
		.append(space)
		.append(graphtype)
		.append(getID(r))
		.append(" {\n"); // start graph

		// graph attributes
		String attr = parseAttributes(r, DOT.NAMESPACE_ATTR);
		if(attr.length() > 0) {
			str.append(indent).append(attr).append(";\n"); }
		
		// default node and edge attributes for graph
		String attrnode = parseAttributeList(r, DOT.NAMESPACE_ATTRNODE);
		if (!attrnode.isEmpty()) {
			str.append(indent).append(NODE).append(attrnode).append(";\n"); }
		String attredge = parseAttributeList(r, DOT.NAMESPACE_ATTREDGE);
		if (!attredge.isEmpty()) {
			str.append(indent).append(EDGE).append(attredge).append(";\n"); }

		// nodes and egdes
		str.append(parseElements(r, DOT.hasNode, x -> parseNode(x), indent));
		str.append(parseElements(r, DOT.hasEdge, x -> parseEdge(x), indent));

		// subgraphs
		for (Statement g : r.listProperties(DOT.hasSubGraph).toList()) {
			str.append("\n").append(parseGraph(SUBGRAPH, g.getObject().asResource(), indent));
		}

		str.append(space).append("}\n"); // end graph
		return str.toString();
	}

	private static String parseElements (Resource r, Property element, Function<Resource, String> parser, String space) {
		String str = Strings.toString(
				r.listProperties(element).toList(), 
				s-> parser.apply(s.getObject().asResource()),
				space);
		if (!str.isEmpty()) {
			return "\n" 
					+ space + "// " + element.getLocalName().replaceAll("has", "").toUpperCase() + "S\n" 
					+ space + str;
		}
		return str;
	}

	private static String parseAttributes(Resource r, String namespace) {
		List<Statement> stmts = r.listProperties().toList();
		stmts.removeIf(s -> !s.getPredicate().getNameSpace().equals(namespace));
		return Strings.toString(stmts, 
				s -> s.getPredicate().getLocalName() + " = \"" + s.getObject().toString() + "\"",
				"; ");
	}

	private static String parseAttributeList (Resource r, String namespace) {
		String attrs = parseAttributes(r, namespace);
		if (attrs.isEmpty()) {
			return "";
		} else {
			return " [ " + attrs + " ]";	
		}
	}

	private static String parseNode(Resource r) {
		StringBuffer str = new StringBuffer();
		str
		.append(getID(r)) // ID
		.append(parseAttributeList(r,DOT.NAMESPACE_ATTR))
		.append(";\n");
		return str.toString();
	}

	private static String parseEdge(Resource r) {
		StringBuffer str = new StringBuffer();
		str
		.append(getID(r.getRequiredProperty(DOT.hasSource).getObject().asResource())) // source node
		.append(EDGE_OP)
		.append(getID(r.getRequiredProperty(DOT.hasTarget).getObject().asResource())) // target node
		.append(parseAttributeList(r, DOT.NAMESPACE_ATTR))
		.append(";\n");
		return str.toString();
	}

	private static String getID (Resource r) {
		String ID;
		Statement s = r.getProperty(DOT.hasID);
		if (s == null) {
			ID = r.toString();
		} else {
			ID = s.getObject().toString();
		}
		return "\"" + ID + "\"";
	}

	private static Resource getRootGraph (Model model) {
		List<Resource> graphs = new ArrayList<>();
		for (Resource g : DOT._Graphs) {
			graphs.addAll(Models.listInstancesOfClass(model, g));
		}
		if (graphs.size() != 1) {
			throw new IllegalArgumentException("Error getting root graph. Expected exactly 1 instance, but found " 
					+ graphs.size() + ": " + Models.shortName(model, graphs));
		}
		return graphs.get(0);		
	}
}
