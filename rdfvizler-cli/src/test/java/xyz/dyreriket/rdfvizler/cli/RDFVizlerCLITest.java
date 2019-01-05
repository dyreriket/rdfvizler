package xyz.dyreriket.rdfvizler.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import xyz.dyreriket.rdfvizler.cli.RDFVizlerCLI;

public class RDFVizlerCLITest {

    //@Rule
    //public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
        
    @Test
    public void shouldReadUriXmlformat() throws Exception {
        String input = getResourcePath("input1.rdf").getAbsolutePath();
        String args = input;
        RDFVizlerCLI.main(args.split(" "));        
    }
    
    @Test
    public void shouldReadTurtleEvenThoughItsCalledRdfXml() throws Exception {
        String input = getResourcePath("input1-ttl.rdf").getAbsolutePath();
        String args = input + " --inputFormatRDF ttl";
        RDFVizlerCLI.main(args.split(" "));        
    }
    
    @Test
    public void shouldReadRdfXmlEvenThoughItsCalledTtl() throws Exception {
        String input = getResourcePath("input1-rdf.ttl").getAbsolutePath();
        String args = input + " --inputFormatRDF rdf";
        RDFVizlerCLI.main(args.split(" "));        
    }

    
    @Ignore // we don't write to files yet, just stdout
    @Test
    public void shouldInferFilename() throws IOException {
        File inputFile = getResourcePath("input1.ttl");
        File root = testFolder.getRoot();

        // moving the input file to the temp folder, so that
        // the output will appear there also
        FileUtils.copyFileToDirectory(inputFile, root);
        String rootInputFilePath = root.listFiles()[0].getAbsolutePath(); // only one file there

        String[] args = (rootInputFilePath + " -i svg").split(" ");

        RDFVizlerCLI.main(args);
        
        //Ensure that we find the output file in the temp folder
        assertTrue(Arrays.stream(root.listFiles()).map(File::getName).anyMatch("input1.ttl.svg"::equals));
    }

    private File getResourcePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource(fileName);

        return new File(url.getFile());
    }
}
