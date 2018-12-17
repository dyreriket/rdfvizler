package xyz.dyreriket.sau.dot;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import xyz.dyreriket.sau.DotProcess;
import xyz.dyreriket.sau.RDF2DotParser;
import xyz.dyreriket.sau.Sau;
import xyz.dyreriket.sau.util.Models;



public class DotTest {

    private final boolean stdout = false; // print files also to stdout?
    
    private final String file1 = "test1.ttl";
    private final String simpleRdf = "simple_rdf.ttl";


    /**
     * Attempts to create the dot output for a simple RDF file applying
     * the default rules. This should produce a DOT graph with nodes and edges
     * This is tested by counting the number of polygons in the svg produced.
     * This should be more than 1, as there is always one polygon in an empty svg     *
     *
     * @throws IOException Exception thrown when file is not found
     */
    @Test
    public void shouldProduceNonEmptyGraph() throws IOException {
        Sau rdfvizler = new Sau(simpleRdf);
        String out = rdfvizler.writeOutput("svg");
        int numberOfPolygons = StringUtils.countMatches(out, "polygon");
        assertTrue(numberOfPolygons > 1);
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void shouldOutputDot() throws IOException {
        String dot = toDot(file1);
        print(file1 + ".dot", dot);
    }

    @Test
    public void shouldOutputDotsvg() throws IOException {
        print(file1 + ".svg", runDot(toDot(file1), "svg"));
    }

    @Test
    public void should() throws IllegalArgumentException, IOException {

        Sau rdfvizler = new Sau("http://folk.uio.no/martige/foaf.rdf");

        rdfvizler.setInputFormat("RDF/XML");
        rdfvizler.setRulesPath("../docs/rules/rdf.jrule");


        print("foaf.ttl" + ".dot", rdfvizler.writeOutput("TTL"));
        print("foaf.rdf" + ".dot", rdfvizler.writeOutput("dot"));
        print("foaf.rdf" + ".svg", rdfvizler.writeOutput("svg"));
    }

    ////////////////////////////////////////

    private String runDot(String content, String format) throws IOException {
        return DotProcess.runDot(content, format);
    }

    private String toDot(String file) {
        Model model = Models.readModel(file);
        RDF2DotParser parser = new RDF2DotParser(model);
        String dot = parser.toDot();
        return dot;
    }

    private void print(String file, String content) throws IOException {
        if (stdout) {
            System.out.println(content);
        }
        File tempFile = testFolder.newFile(file);
        FileUtils.writeStringToFile(tempFile, content, "UTF-8");
    }
}
