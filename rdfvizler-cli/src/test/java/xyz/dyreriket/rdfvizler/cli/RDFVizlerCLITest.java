package xyz.dyreriket.rdfvizler.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RDFVizlerCLITest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @Before
    public void captureStdout() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @After
    public void restoreStdout() {
        System.setOut(originalOut);
    }

    // -- format guessed from file extension --

    @Test
    public void shouldGuessRdfXmlFromExtension() throws Exception {
        run(path("input1.rdf"));
        assertOutputIsNotEmpty();
    }

    @Test
    public void shouldGuessTurtleFromExtension() throws Exception {
        run(path("input1.ttl"));
        assertOutputIsNotEmpty();
    }

    // -- --inputFormatRDF overrides file extension --

    @Test
    public void shouldReadRdfXmlEvenThoughItsCalledTtl() throws Exception {
        run(path("input1-rdf.ttl") + " --inputFormatRDF rdf");
        assertOutputIsNotEmpty();
    }

    @Test
    public void shouldReadRdfXmlUsingJenaShortname() throws Exception {
        run(path("input1-rdf.ttl") + " --inputFormatRDF RDFXML");
        assertOutputIsNotEmpty();
    }

    @Test
    public void shouldReadTurtleEvenThoughItsCalledRdfXml() throws Exception {
        run(path("input1-ttl.rdf") + " --inputFormatRDF ttl");
        assertOutputIsNotEmpty();
    }

    @Test
    public void shouldReadNTriplesEvenThoughItsCalledRdfXml() throws Exception {
        run(path("input1-nt.rdf") + " --inputFormatRDF nt");
        assertOutputIsNotEmpty();
    }

    // -- output from same content should be identical regardless of input format --

    @Test
    public void shouldProduceSameOutputRegardlessOfInputFormat() throws Exception {
        // input1.ttl and input1-ttl.rdf contain identical Turtle content
        run(path("input1.ttl") + " --executionMode dot");
        String dotFromTtlExtension = capturedOut.toString();
        capturedOut.reset();

        run(path("input1-ttl.rdf") + " --inputFormatRDF ttl --executionMode dot");
        String dotFromRdfExtensionForcedTtl = capturedOut.toString();

        assertFalse("Expected non-empty DOT output", dotFromTtlExtension.isBlank());
        assertTrue("Expected same DOT output regardless of input format", dotFromTtlExtension.equals(dotFromRdfExtensionForcedTtl));
    }

    // -- --outputFormatRDF controls serialisation of the output RDF model --

    @Test
    public void shouldOutputTurtleByDefault() throws Exception {
        run(path("input1.ttl") + " --executionMode rdf");
        assertTrue("Expected Turtle PREFIX declarations in output", capturedOut.toString().contains("PREFIX"));
    }

    @Test
    public void shouldOutputTurtleWhenRequested() throws Exception {
        run(path("input1.ttl") + " --executionMode rdf --outputFormatRDF ttl");
        assertTrue("Expected Turtle PREFIX declarations in output", capturedOut.toString().contains("PREFIX"));
    }

    @Test
    public void shouldOutputRdfXmlWhenRequested() throws Exception {
        run(path("input1.ttl") + " --executionMode rdf --outputFormatRDF RDFXML");
        assertTrue("Expected RDF/XML opening tag in output", capturedOut.toString().contains("<rdf:RDF"));
    }

    @Test
    public void shouldOutputNTriplesWhenRequested() throws Exception {
        run(path("input1.ttl") + " --executionMode rdf --outputFormatRDF nt");
        String out = capturedOut.toString();
        assertTrue("Expected N-Triples output (lines ending with ' .')", out.contains(" ."));
        assertFalse("Expected no PREFIX declarations in N-Triples output", out.contains("PREFIX"));
    }

    @Test
    public void shouldProduceDifferentOutputForDifferentFormats() throws Exception {
        run(path("input1.ttl") + " --executionMode rdf --outputFormatRDF ttl");
        String turtleOut = capturedOut.toString();
        capturedOut.reset();

        run(path("input1.ttl") + " --executionMode rdf --outputFormatRDF RDFXML");
        String rdfxmlOut = capturedOut.toString();

        assertFalse("Expected non-empty Turtle output", turtleOut.isBlank());
        assertFalse("Expected non-empty RDF/XML output", rdfxmlOut.isBlank());
        assertFalse("Expected Turtle and RDF/XML output to differ", turtleOut.equals(rdfxmlOut));
    }

    // -- --rules loads rules from URI --

    @Test
    public void shouldLoadRulesFromURI() throws Exception {
        String rulesURI = "https://raw.githubusercontent.com/dyreriket/rdfvizler/refs/heads/master/docs/rules/rdf.jrule";

        run(path("input1.ttl") + " --rules " + rulesURI + " --executionMode dot");
        String withURIRules = capturedOut.toString();
        capturedOut.reset();

        run(path("input1.ttl") + " --executionMode dot");
        String withDefaultRules = capturedOut.toString();

        assertFalse("Expected non-empty output when loading rules from URI", withURIRules.isBlank());
        assertTrue("Expected URI rules output to match bundled rules output", withURIRules.equals(withDefaultRules));
    }

    // -- ignored / unfinished --

    @Ignore("writing to files not yet supported")
    @Test
    public void shouldInferFilename() throws Exception {
        run(path("input1.ttl") + " -i svg");
    }

    private void run(String args) throws Exception {
        RDFVizlerCLI.main(args.split(" "));
    }

    private void assertOutputIsNotEmpty() {
        assertFalse("Expected non-empty output", capturedOut.toString().isBlank());
    }

    private String path(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        return new File(url.getFile()).getAbsolutePath();
    }
}
