package osl.rdfvizler.dot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import osl.rdfvizler.ui.RDFVizler;

import osl.util.Arrays;
import osl.util.rdf.Models;

public class DotTest {

    private final boolean stdout = true; // print files also to stdout?
    
    private final String file1 = "test1.ttl";

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

        RDFVizler rdfvizler = new RDFVizler("http://folk.uio.no/martige/foaf.rdf");

        rdfvizler.setInputFormat("RDF/XML");
        rdfvizler.setRulesPath("../docs/rules/rdf.jrule");


        print("foaf.ttl" + ".dot", rdfvizler.writeOutput("TTL"));
        print("foaf.rdf" + ".dot", rdfvizler.writeOutput("dot"));
        print("foaf.rdf" + ".svg", rdfvizler.writeOutput("svg"));
    }

    ////////////////////////////////////////

    private String runDot(String content, String format) throws IOException {
        return DotProcess.runDot(null, content, format);
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
