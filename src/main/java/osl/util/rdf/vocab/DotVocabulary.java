package osl.util.rdf.vocab;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public abstract class DotVocabulary {
    
	private static final String ROOT = "http://rdfvizler.gitlab.com/vocab/";
	private static final String NS = ROOT + "core#";

	// namespaces
	public static final String NAMESPACE = NS;
	public static final String NAMESPACE_ATTR = ROOT + "attribute#";
	public static final String NAMESPACE_ATTRNODE = ROOT + "attribute/default/node#";
	public static final String NAMESPACE_ATTREDGE = ROOT + "attribute/default/edge#";
	
	// prefixes
	public static final String NAMESPACE_PREFIX           = "gv";
	public static final String NAMESPACE_ATTR_PREFIX      = "gva";
	public static final String NAMESPACE_ATTRNODE_PREFIX  = "gvn";
	public static final String NAMESPACE_ATTREDGE_PREFIX  = "gve";

	public static final Resource StrictDiRootGraph  = Vocabulary.getResource(NS + "StrictDiRootGraph");
	public static final Resource StrictRootGraph    = Vocabulary.getResource(NS + "StrictRootGraph");
	public static final Resource DiRootGraph        = Vocabulary.getResource(NS + "DiRootGraph");
	public static final Resource RootGraph          = Vocabulary.getResource(NS + "RootGraph");
	public static final Resource Graph              = Vocabulary.getResource(NS + "Graph");
	public static final Resource SubGraph           = Vocabulary.getResource(NS + "SubGraph");
	public static final Resource Node               = Vocabulary.getResource(NS + "Node");
	public static final Resource Edge               = Vocabulary.getResource(NS + "Edge");

	public static final List<Resource> _Graphs = Arrays.asList(StrictDiRootGraph, StrictRootGraph, DiRootGraph, RootGraph);

	public static final Property hasID              = Vocabulary.getProperty(NS + "hasID");
	public static final Property hasNode            = Vocabulary.getProperty(NS + "hasNode");
	public static final Property hasEdge            = Vocabulary.getProperty(NS + "hasEdge");
	public static final Property hasSource          = Vocabulary.getProperty(NS + "hasSource");
	public static final Property hasTarget          = Vocabulary.getProperty(NS + "hasTarget");
	public static final Property hasSubGraph        = Vocabulary.getProperty(NS + "hasSubGraph");

    // hiding constructor
    private DotVocabulary() {
        throw new IllegalStateException("Utility class");
    }
	
	private static class Vocabulary {
	 
	    // hiding constructor
	    private Vocabulary() {
	        throw new IllegalStateException("Utility class");
	    }
	    
		public static Resource getResource (String url) {
			return ResourceFactory.createResource(url);
		}
		
		public static Property getProperty (String url) {
			return ResourceFactory.createProperty(url);
		}
	}
}
