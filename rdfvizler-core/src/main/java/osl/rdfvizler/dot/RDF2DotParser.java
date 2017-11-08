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

public class RDF2DotParser {

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
    
    private Model model;
    
    public RDF2DotParser(Model model) {
        this.model = model;
    }

    private void addPrefixes() {
        model.withDefaultMappings(PrefixMapping.Standard);
        model.setNsPrefix(DotVocabulary.NAMESPACE_PREFIX, DotVocabulary.NAMESPACE);
        model.setNsPrefix(DotVocabulary.NAMESPACE_ATTR_PREFIX, DotVocabulary.NAMESPACE_ATTR);
        model.setNsPrefix(DotVocabulary.NAMESPACE_ATTRNODE_PREFIX, DotVocabulary.NAMESPACE_ATTRNODE);
        model.setNsPrefix(DotVocabulary.NAMESPACE_ATTREDGE_PREFIX, DotVocabulary.NAMESPACE_ATTREDGE);
    }

    public String toDot() {
        addPrefixes();
        Resource rootGraph = getRootGraph();
        return parseRootGraph(rootGraph);
    }
    
    private String parseRootGraph(Resource rootGraph) {
        StringBuilder str = new StringBuilder();
        
        if (isStrictRootGraph(rootGraph)) {
            str.append(STRICT);
        }
        String graphtype;
        if (isDiRootGraph(rootGraph)) {
            graphtype = DIGRAPH;
            EDGE_OP = EDGE_OP_DIGRAPH;
        } else {
            graphtype = GRAPH;
            EDGE_OP = EDGE_OP_GRAPH;
        }
        str.append(parseGraph(graphtype, rootGraph, ""));
        return str.toString();
    }

    private String parseGraph(String graphtype, Resource resource, String space) {
        String indent = space + TAB;
        StringBuilder str = new StringBuilder();
        str.append(space)
            .append(graphtype)
            .append(getID(resource))
            .append(" {\n") // start graph
            .append(parseGraphContents(resource, indent))
            .append(space).append("}\n"); // end graph
        return str.toString();
    }
    
    private String parseGraphContents(Resource graph, String indent) {
        StringBuilder str = new StringBuilder();
        str
            .append(parseGraphAttributes(graph, indent))
            .append(parseGraphNodes(graph, indent))
            .append(parseGraphEdges(graph, indent))
            .append(parseGraphSubgraphs(graph, indent));
        return str.toString();
    }
    
    private String parseGraphAttributes(Resource graph, String indent) {
        StringBuilder str = new StringBuilder();
        String attr = parseAttributes(graph, DotVocabulary.NAMESPACE_ATTR);
        if (!attr.isEmpty()) {
            str.append(indent).append(attr).append(";\n");
        }

        // default node and edge attributes for graph
        String attrnode = parseAttributeList(graph, DotVocabulary.NAMESPACE_ATTRNODE);
        if (!attrnode.isEmpty()) {
            str.append(indent).append(NODE).append(attrnode);
        }
        String attredge = parseAttributeList(graph, DotVocabulary.NAMESPACE_ATTREDGE);
        if (!attredge.isEmpty()) {
            str.append(indent).append(EDGE).append(attredge);
        }
        return str.toString();
    }
    
    private String parseGraphNodes(Resource graph, String indent) {
        return parseElements(graph, DotVocabulary.hasNode, x -> parseNode(x), indent);
    }
    
    private String parseGraphEdges(Resource graph, String indent) {
        return parseElements(graph, DotVocabulary.hasEdge, x -> parseEdge(x), indent);
    }

    private String parseGraphSubgraphs(Resource graph, String indent) {
        StringBuilder str = new StringBuilder();
        for (Statement subgraph : graph.listProperties(DotVocabulary.hasSubGraph).toList()) {
            str.append("\n").append(parseGraph(SUBGRAPH, subgraph.getObject().asResource(), indent));
        }
        return str.toString();
    }
    
    private String parseElements(Resource resource, Property element, Function<Resource, String> parser, String space) {
        String str = Strings.toString(
            resource.listProperties(element).toList(), 
            s -> parser.apply(s.getObject().asResource()),
            space);
        if (!str.isEmpty()) {
            return "\n" 
                    + space + "// " + element.getLocalName().replaceAll("has", "").toUpperCase() + "S\n" 
                    + space + str;
        }
        return str;
    }

    private String parseAttributes(Resource resource, String namespace) {
        List<Statement> stmts = resource.listProperties().toList();
        stmts.removeIf(s -> !s.getPredicate().getNameSpace().equals(namespace));
        return Strings.toString(stmts, 
            s -> s.getPredicate().getLocalName() + " = \"" + s.getObject().toString() + "\"", "; ");
    }

    private String parseAttributeList(Resource resource, String namespace) {
        String attrs = parseAttributes(resource, namespace);
        if (attrs.isEmpty()) {
            return ";\n";
        } else {
            return " [ " + attrs + " ];\n";
        }
    }

    private String parseNode(Resource resource) {
        StringBuilder str = new StringBuilder();
        str.append(getID(resource)) // ID
            .append(parseAttributeList(resource,DotVocabulary.NAMESPACE_ATTR));
        return str.toString();
    }

    private String parseEdge(Resource resource) {
        StringBuilder str = new StringBuilder();
        str.append(getID(resource.getRequiredProperty(DotVocabulary.hasSource).getObject().asResource())) // source node
            .append(EDGE_OP)
            .append(getID(resource.getRequiredProperty(DotVocabulary.hasTarget).getObject().asResource())) // target node
            .append(parseAttributeList(resource, DotVocabulary.NAMESPACE_ATTR));
        return str.toString();
    }
    
    private String getID(Resource resource) {
        String id;
        Statement statement = resource.getProperty(DotVocabulary.hasID);
        if (statement == null) {
            id = resource.toString();
        } else {
            id = statement.getObject().toString();
        }
        return "\"" + id + "\"";
    }

    private Resource getRootGraph() {
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
    
    private boolean isStrictRootGraph(Resource rootGraph) {
        return (Models.isOfType(model, rootGraph, DotVocabulary.StrictDiRootGraph) 
            || Models.isOfType(model, rootGraph, DotVocabulary.StrictRootGraph));
    }
    
    private boolean isDiRootGraph(Resource rootGraph) {
        return (Models.isOfType(model, rootGraph, DotVocabulary.StrictDiRootGraph) 
            || Models.isOfType(model, rootGraph, DotVocabulary.StrictRootGraph));
    }
}
