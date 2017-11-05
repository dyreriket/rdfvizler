package osl.rdfvizler.ui.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;


import static org.hamcrest.CoreMatchers.hasItems;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class RDFVizlerCLITest {

	//private final String resources = "src/test/resources/";
	private final String file1 =  "test1.ttl";
    private final String input1 = "input1.ttl";
    private final String simpleRulesFile = "simple.jrule";

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();


	@Test public void shouldReadURIXMLformat() throws IOException {
		RDFVizlerCLI.main((
				"-r " + "https://mgskjaeveland.github.io/rdfvizler/rules/rdf.jrule "
				+ "-i" + "http://folk.uio.no/martige/foaf.rdf "
				+ "-x"		
				).split(" "));
	}

	@Test public void shouldInferFilename() {

	    File rulesFile = getResourcePath(simpleRulesFile);
        File inputFile = getResourcePath(input1);

        File root = testFolder.getRoot();
        try {

            //moving the input file to the temp folder, so that the output will appear
            //there also

            FileUtils.copyFileToDirectory(inputFile, root);
            File rInputFile = root.listFiles()[0]; //only one file there

            String[] args = (
                      "-r "
                    + rulesFile.getAbsolutePath()
                    + " -i " + rInputFile.getAbsolutePath()
                    + " -c "
                    + " -e /usr/local/bin/dot") //TODO:remove this line when env vars are in
                    .split(" ");



            RDFVizlerCLI rdfVizlerCLI = new RDFVizlerCLI(System.out);
            if (rdfVizlerCLI.parse(args)) {
                rdfVizlerCLI.execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(Arrays.stream(root.listFiles())
                .map(File::getName)
                .anyMatch("input1.svg"::equals));

    }

    public File getResourcePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL a =  classLoader.getResource(fileName);

        return new File(a.getFile());
    }
}
