package osl.rdfvizler.dot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;

import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.dot.RDF2Dot;
import osl.util.rdf.Models;

public class DotTest {
	
	private final boolean stdout = true; // print files also to stdout?
	
	private final String resources = "src/test/resources/";
	
	private final String file1 = resources + "test1.ttl";
	
	@Test public void shouldOutputDot () throws IOException {
		String dot = toDot(file1);
		print(file1 + ".dot", dot);
	}
	
	@Test public void shouldOutputDotsvg () throws IOException {
		print(file1 + ".svg", runDot(toDot(file1), "svg"));
	}
	
	@ Test public void should () throws IllegalArgumentException, IOException {
		
		Model model = DotModel.getDotModel(
				"http://folk.uio.no/martige/foaf.rdf", "RDF/XML",
				"docs/rules/rdf.jrule");
				//"https://mgskjaeveland.github.io/rdfvizler/rules/rdf.jrule");
		String dotmodel = Models.writeModel(model, "TTL");
		print(resources + "foaf.ttl" + ".dot", dotmodel);
		String dot = RDF2Dot.toDot(model);
		print(resources + "foaf.rdf" + ".dot", dot);
		String out = runDot(dot, "svg");
		print(resources + "foaf.rdf" + ".svg", out);
	}
	
	
	/*
	@Test public void shouldOutputDotpng () throws IOException {
		print(file1 + ".png", runDot(toDot(file1), "png"));
	}*/
	
	////////////////////////////////////////
	
	private String runDot (String content, String format) throws IOException {
		DotProcess exe = new DotProcess();
		return exe.runDot(content, format);
	}
	
	private String toDot (String file) {
		Model model = Models.readModel(file);
		String dot = RDF2Dot.toDot(model);
		return dot;
	}
	
	private void print (String file, String content) throws IOException {
		if (stdout) {
			System.out.println(content);
		}
		FileUtils.writeStringToFile(new File(file), content, "UTF-8");
	}
}
