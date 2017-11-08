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
            edgeOperator = EDGE_OP_DIGRAPH;
        } else {
            graphtype = GRAPH;
            edgeOperator = EDGE_OP_GRAPH;
        }
        str.append(parseGraph(graphtype, rootGraph, ""));
        return str.toString();
    }

    private String parseGraph(String graphtype, Resource resource, String tabs) {
        return tabs 
                + graphtype + getID(resource) + " {" + BR // start graph
                + parseGraphContents(resource, tabs + TAB)
                + tabs + "}" + BR; // end graph
    }
    
    private String parseGraphContents(Resource graph, String tabs) {
        return parseGraphAttributes(graph, tabs)
             + parseElements(graph, DotVocabulary.hasNode, x -> parseNode(x), tabs)
             + parseElements(graph, DotVocabulary.hasEdge, x -> parseEdge(x), tabs)
             + parseElements(graph, DotVocabulary.hasSubGraph, x -> parseGraph(SUBGRAPH, x, tabs), tabs);
    }
    
    private String parseGraphAttributes(Resource graph, String tabs) {
        return parseGraphAttributes(EMPTY, parseAttributes(graph, DotVocabulary.NAMESPACE_ATTR), tabs)
               + parseGraphAttributes(NODE, parseAttributeList(graph, DotVocabulary.NAMESPACE_ATTRNODE), tabs)
               + parseGraphAttributes(EDGE, parseAttributeList(graph, DotVocabulary.NAMESPACE_ATTREDGE), tabs);
    }
    
    private String parseGraphAttributes(String element, String attributes, String tabs) {
        String out = EMPTY;
        if (!attributes.isEmpty()) {
            out = tabs + element + attributes + SC + BR;
        }
        return out;
    }
    
    private String parseElements(Resource resource, Property element, Function<Resource, String> parser, String tabs) {
        String out = EMPTY;
        String str = Strings.toString(resource.listProperties(element).toList(), 
            s -> parser.apply(s.getObject().asResource()),
            tabs);
        if (!str.isEmpty()) {
            out = BR 
                    + tabs + "// " + getElementType(element) + BR // comment "headline"
                    + tabs + str;
        }
        return out;
    }
    
    private String getElementType(Property element) {
        return element.getLocalName().replaceAll("has", EMPTY).toUpperCase() + "S";
    }

    private String parseAttributes(Resource resource, String namespace) {
        List<Statement> stmts = resource.listProperties().toList();
        stmts.removeIf(s -> !s.getPredicate().getNameSpace().equals(namespace));
        return Strings.toString(stmts, s -> s.getPredicate().getLocalName() + " = " + QT + s.getObject().toString() + QT, SC + " ");
    }

    private String parseAttributeList(Resource resource, String namespace) {
        String attrs = parseAttributes(resource, namespace);
        if (attrs.isEmpty()) {
            return "";
        } else {
            return " [ " + attrs + " ]";
        }
    }

    private String parseNode(Resource resource) {
        return getID(resource) // ID
            + parseAttributeList(resource,DotVocabulary.NAMESPACE_ATTR)
            + SC + BR;
    }

    private String parseEdge(Resource resource) {
        return getID(resource.getRequiredProperty(DotVocabulary.hasSource).getObject().asResource()) // source node
            + edgeOperator
            + getID(resource.getRequiredProperty(DotVocabulary.hasTarget).getObject().asResource()) // target node
            + parseAttributeList(resource, DotVocabulary.NAMESPACE_ATTR)
            + SC + BR;
    }
    
    private String getID(Resource resource) {
        Statement statement = resource.getProperty(DotVocabulary.hasID);
        String id;
        if (statement == null) {
            id = resource.toString();
        } else {
            id = statement.getObject().toString();
        }
        return QT + id + QT;
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
