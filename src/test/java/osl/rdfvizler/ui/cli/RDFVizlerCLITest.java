package osl.rdfvizler.ui.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static osl.rdfvizler.ui.cli.RDFVizlerCLI.ENV_RDFVIZLER_RULES_PATH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.jena.n3.turtle.TurtleParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.TemporaryFolder;

public class RDFVizlerCLITest {

    // private final String resources = "src/test/resources/";
    // private final String file1 = "test1.ttl";
    private final String input1 = "input1.ttl";
    private final String simpleRulesFile = "simple.jrule";
    private final String rdfXmlFile = "input1.xml";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void shouldReadURI_XMLformat() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        RDFVizlerCLI cli = new RDFVizlerCLI(new PrintStream(bytes));
        environmentVariables.set(ENV_RDFVIZLER_RULES_PATH, null);

        String input = getResourcePath(rdfXmlFile).getAbsolutePath();
        String args = "-i " + input + " -if RDF/XML";

        cli.parseOptions(args.split(" "));
        try {
            cli.execute();
        } catch (TurtleParseException e) {
            fail("-x toggle should accept RDF/XML format on input file, but doesn't");
        }
    }

    // @Test
    public void shouldInferFilename() {
        File rulesFile = getResourcePath(simpleRulesFile);
        File inputFile = getResourcePath(input1);

        File root = testFolder.getRoot();
        try {
            // moving the input file to the temp folder, so that the output will appear
            // there also

            FileUtils.copyFileToDirectory(inputFile, root);
            File rinputFile = root.listFiles()[0]; // only one file there

            String[] args = ("-r " + rulesFile.getAbsolutePath() + " -i "
                    + rinputFile.getAbsolutePath() + " -oe ").split(" ");

            RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI(System.out);
            if (rdfVizlerCLI.parseOptions(args)) {
                rdfVizlerCLI.execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(Arrays.stream(root.listFiles()).map(File::getName).anyMatch("input1.svg"::equals));
    }

    public File getResourcePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource(fileName);

        return new File(url.getFile());
    }
}
