package xyz.dyreriket.rdfvizler;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import xyz.dyreriket.rdfvizler.rules.RuleRegistrar;
import xyz.dyreriket.rdfvizler.util.Models;
import xyz.dyreriket.rdfvizler.util.Strings;

public class RDF2DotParser {

    static {
        RuleRegistrar.registerRules();
    }

    private static final String STRICT = "strict ";
    private static final String GRAPH = "graph ";
    private static final String DIGRAPH = "digraph ";
    private static final String SUBGRAPH = "subgraph ";
    private static final String NODE = "node ";
    private static final String EDGE = "edge ";

    private static final String TAB = "   ";
    private static final String EMPTY = "";
    private static final String BR = "\n";
    private static final String SC = ";";
    private static final String QT = "\"";
    
    private static final String EDGE_OP_DIGRAPH = " -> ";
    private static final String EDGE_OP_GRAPH   = " -- ";
    
    private Model model;
    private String edgeOperator;
     
    public RDF2DotParser(Model model) {
        this.model = model;
    }

    public String toDot() {
        // get root graph
        List<Resource> rootgraphs = Models.listInstancesOfClass(model, RDFVizlerVocabulary.RootGraph);
        if (rootgraphs.size() != 1) {
            throw new IllegalArgumentException("Error getting root graph. Expected exactly 1 instance, but found " 
                    + rootgraphs.size() + ": " + Models.shortName(model, rootgraphs));
        }
        return parseRootGraph(rootgraphs.get(0));
    }
    
    private String parseRootGraph(Resource rootGraph) {
        StringBuilder str = new StringBuilder();
        
        if (Models.isOfType(model, rootGraph, RDFVizlerVocabulary.StrictGraph)) {
            str.append(STRICT);
        }
        String graphtype;
        if (Models.isOfType(model, rootGraph, RDFVizlerVocabulary.DiGraph)) {
            graphtype = DIGRAPH;
            edgeOperator = EDGE_OP_DIGRAPH;
        } else {
            graphtype = GRAPH;
            edgeOperator = EDGE_OP_GRAPH;
        }
        str.append(parseGraph(graphtype, rootGraph, ""));
        return str.toString();
    }

    private String parseGraph(String graphtype, Resource graph, String tabs) {
        return tabs 
                + graphtype + getID(graph) + " {" + BR // start graph
                + parseGraphAttributes(EMPTY, parseAttributes(graph, RDFVizlerVocabulary.NAMESPACE_ATTR), tabs) 
                + parseGraphAttributes(NODE, parseAttributeList(graph, RDFVizlerVocabulary.NAMESPACE_ATTRNODE), tabs)
                + parseGraphAttributes(EDGE, parseAttributeList(graph, RDFVizlerVocabulary.NAMESPACE_ATTREDGE), tabs)
                + parseGraphElements(graph, tabs + TAB)
                + tabs + "}" + BR; // end graph
    }
    
    private String parseGraphAttributes(String element, String attributes, String tabs) {
        return Strings.processNonEmpty(
            attributes, 
            s -> tabs + element + s + SC + BR);
    }
    
    private String parseGraphElements(Resource graph, String tabs) {
        return parseElements(graph, RDFVizlerVocabulary.hasNode, x -> parseNode(x), tabs)
                + parseElements(graph, RDFVizlerVocabulary.hasEdge, x -> parseEdge(x), tabs)
                + parseElements(graph, RDFVizlerVocabulary.hasSubGraph, x -> parseGraph(SUBGRAPH, x, tabs), tabs);
    }
    
    private String parseElements(Resource resource, Property element, Function<Resource, String> parser, String tabs) {
        return Strings.processNonEmpty(
            Strings.toString(resource.listProperties(element).toList(),
                s -> parser.apply(s.getObject().asResource()), tabs),
            s -> BR + tabs + "// " + getElementType(element) + BR // comment "headline"
                + tabs + s);
    }
    
    private String getElementType(Property element) {
        return element.getLocalName().replaceAll("has", EMPTY).toUpperCase(Locale.ENGLISH) + "S";
    }

    private String parseAttributes(Resource resource, String namespace) {
        return resource.listProperties().toList().stream()
            .filter(s -> s.getPredicate().getNameSpace().equals(namespace))
            .collect(groupingBy(Statement::getPredicate)).entrySet().stream()
            .map(entry ->
                entry.getKey().getLocalName()
                    + " = "
                    + entry.getValue().stream()
                    .map(Statement::getObject)
                    .map(RDFNode::toString)
                    .collect(Collectors.joining(",", QT, QT)))
            .collect(Collectors.joining(SC + " "));
    }

    private String parseAttributeList(Resource resource, String namespace) {
        return Strings.processNonEmpty(
            parseAttributes(resource, namespace), 
            s -> " [ " + s + " ]");
    }
    
    private String parseNode(Resource resource) {
        return getID(resource) // ID
            + parseAttributeList(resource,RDFVizlerVocabulary.NAMESPACE_ATTR)
            + SC + BR;
    }

    private String parseEdge(Resource resource) {
        return getID(resource.getRequiredProperty(RDFVizlerVocabulary.hasSource).getObject().asResource()) // source node
            + edgeOperator
            + getID(resource.getRequiredProperty(RDFVizlerVocabulary.hasTarget).getObject().asResource()) // target node
            + parseAttributeList(resource, RDFVizlerVocabulary.NAMESPACE_ATTR)
            + SC + BR;
    }
    
    private String getID(Resource resource) {
        Statement statement = resource.getProperty(RDFVizlerVocabulary.hasID);
        String id;
        if (statement == null) {
            id = resource.toString();
        } else {
            id = statement.getObject().toString();
        }
        return QT + id + QT;
    }

}
