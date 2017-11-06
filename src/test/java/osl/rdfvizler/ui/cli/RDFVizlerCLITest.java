package osl.rdfvizler.ui.cli;

import java.io.IOException;

import org.junit.Test;

public class RDFVizlerCLITest {

	//private final String resources = "src/test/resources/";
	//private final String file1 = resources + "test1.ttl";

	@Test public void shouldReadURIXMLformat () throws IOException {
		RDFVizlerCLI.main((
				"-r " + "https://mgskjaeveland.github.io/rdfvizler/rules/rdf.jrule "
				+ "-i" + "http://folk.uio.no/martige/foaf.rdf "
				+ "-if RDF/XML"		
				).split(" "));
	    }
}
