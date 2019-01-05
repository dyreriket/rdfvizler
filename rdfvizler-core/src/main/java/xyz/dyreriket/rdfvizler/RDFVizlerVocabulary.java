package xyz.dyreriket.rdfvizler;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public abstract class RDFVizlerVocabulary {

    private static final String ROOT = "http://rdfvizler.dyreriket.xyz/vocabulary/";
    private static final String NS = ROOT + "core#";

    // namespaces
    public static final String NAMESPACE = NS;
    public static final String NAMESPACE_ATTR = ROOT + "attribute#";
    public static final String NAMESPACE_ATTRNODE = ROOT + "attribute-default-node#";
    public static final String NAMESPACE_ATTREDGE = ROOT + "attribute-default-edge#";

    // prefixes
    public static final String NAMESPACE_PREFIX = "rvz";
    public static final String NAMESPACE_ATTR_PREFIX = "rvz-a";
    public static final String NAMESPACE_ATTRNODE_PREFIX = "rvz-n";
    public static final String NAMESPACE_ATTREDGE_PREFIX = "rvz-e";

    public static final Resource RootGraph = getResource(NS + "RootGraph");
    public static final Resource StrictGraph = getResource(NS + "StrictGraph");
    public static final Resource DiGraph = getResource(NS + "DiGraph");
    public static final Resource Graph = getResource(NS + "Graph");
    public static final Resource SubGraph = getResource(NS + "SubGraph");
    public static final Resource Node = getResource(NS + "Node");
    public static final Resource Edge = getResource(NS + "Edge");

    public static final Property hasID = getProperty(NS + "hasID");
    public static final Property hasNode = getProperty(NS + "hasNode");
    public static final Property hasEdge = getProperty(NS + "hasEdge");
    public static final Property hasSource = getProperty(NS + "hasSource");
    public static final Property hasTarget = getProperty(NS + "hasTarget");
    public static final Property hasSubGraph = getProperty(NS + "hasSubGraph");

    // hiding constructor
    private RDFVizlerVocabulary() {
        throw new IllegalStateException("Utility class");
    }

    private static Resource getResource(String url) {
        return ResourceFactory.createResource(url);
    }

    private static Property getProperty(String url) {
        return ResourceFactory.createProperty(url);
    }

}
