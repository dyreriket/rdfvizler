package osl.util.rdf.vocab;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class DOT {

	private static final String root = "http://rdfvizler.gitlab.com/vocab/";
	private static final String ns = root + "core#";

	public static final String NAMESPACE = ns;
	public static final String NAMESPACE_ATTR = root + "attribute#";
	public static final String NAMESPACE_ATTRNODE = root + "attribute/default/node#";
	public static final String NAMESPACE_ATTREDGE = root + "attribute/default/edge#";
	
	public static final String NAMESPACE_PREFIX           = "gv";
	public static final String NAMESPACE_ATTR_PREFIX      = "gva";
	public static final String NAMESPACE_ATTRNODE_PREFIX  = "gvn";
	public static final String NAMESPACE_ATTREDGE_PREFIX  = "gve";

	public static final Resource
	StrictDiRootGraph  = Vocabulary.getResource(ns + "StrictDiRootGraph"),
	StrictRootGraph    = Vocabulary.getResource(ns + "StrictRootGraph"),
	DiRootGraph        = Vocabulary.getResource(ns + "DiRootGraph"),
	RootGraph          = Vocabulary.getResource(ns + "RootGraph"),
	Graph              = Vocabulary.getResource(ns + "Graph"),
	SubGraph           = Vocabulary.getResource(ns + "SubGraph"),
	Node               = Vocabulary.getResource(ns + "Node"),
	Edge               = Vocabulary.getResource(ns + "Edge");

	public static final List<Resource> _Graphs = Arrays.asList(StrictDiRootGraph, StrictRootGraph, DiRootGraph, RootGraph);

	public static final Property
	hasID              = Vocabulary.getProperty(ns + "hasID"),
	hasNode            = Vocabulary.getProperty(ns + "hasNode"),
	hasEdge            = Vocabulary.getProperty(ns + "hasEdge"),
	hasSource          = Vocabulary.getProperty(ns + "hasSource"),
	hasTarget          = Vocabulary.getProperty(ns + "hasTarget"),
	hasSubGraph        = Vocabulary.getProperty(ns + "hasSubGraph");
	//hasAttribute       = Vocabulary.getProperty(ns + "hasAttribute");

	//public static final List<Resource> _hasElements = Arrays.asList(hasNode, hasEdge, hasSubGraph);

	static class Vocabulary {
		public static Resource getResource (String url) {
			return ResourceFactory.createResource(url);
		}
		public static Property getProperty (String url) {
			return ResourceFactory.createProperty(url);
		}
	}
}
