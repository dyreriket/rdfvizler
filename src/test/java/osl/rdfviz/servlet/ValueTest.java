package osl.rdfviz.servlet;

import org.junit.Test;

import osl.rdfvizler.ui.servlet.RDFVizlerServlet;

public class ValueTest {
	
	@Test public void shouldAssign () {
		
		RDFVizlerServlet t = new RDFVizlerServlet();
		
		String c = "asdf";
		c = t.getValue(null, c);
		print(c);
		
		c= t.getValue("new", c);
		print(c);
	}
	
	private void print (String s) {
		System.out.println(s);
	}
}
