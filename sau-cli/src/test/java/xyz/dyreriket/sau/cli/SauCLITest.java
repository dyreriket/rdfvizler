package xyz.dyreriket.sau.cli;

import static org.junit.Assert.assertTrue;
import static xyz.dyreriket.sau.cli.SauCLI.ENV_RDFVIZLER_RULES_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.TemporaryFolder;

import xyz.dyreriket.sau.cli.SauCLI;

public class SauCLITest {

    // private final String resources = "src/test/resources/";
    // private final String file1 = "test1.ttl";
    private final String input1 = "input1.ttl";
    private final String rdfXmlFile = "input1.xml";
    // private final String simpleRulesFile = "simple.jrule";
    
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    
    @Test
    public void shouldReadUriXmlformat() throws Exception {

        environmentVariables.set(ENV_RDFVIZLER_RULES_PATH, null);

        String input = getResourcePath(rdfXmlFile).getAbsolutePath();
        String args = input; //+ " --inputFormatRDF xml";

        SauCLI.main(args.split(" "));        
    }

    @Test
    public void shouldInferFilename() throws IOException {
        File inputFile = getResourcePath(input1);
        File root = testFolder.getRoot();

        // moving the input file to the temp folder, so that
        // the output will appear there also
        FileUtils.copyFileToDirectory(inputFile, root);
        String rootInputFilePath = root.listFiles()[0].getAbsolutePath(); // only one file there

        String[] args = (rootInputFilePath + " -i svg").split(" ");

        SauCLI.main(args);
        
        //Ensure that we find the output file in the temp folder
        assertTrue(Arrays.stream(root.listFiles()).map(File::getName).anyMatch("input1.ttl.svg"::equals));
    }

    public File getResourcePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource(fileName);

        return new File(url.getFile());
    }
}
