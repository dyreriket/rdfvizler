package osl.rdfviz.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.rulesys.Rule;

import osl.rdfviz.DotProcess;
import osl.rdfviz.Models;
import osl.rdfviz.RDF2Dot;


public class RDFVizlerServlet extends HttpServlet {

	private static final long serialVersionUID = 7193847752589093476L;

	// URL params
	private final static String
	pRDF = "rdf",
	pRules = "rules",
	pRDFFormat = "in",
	pDotFormat = "out";

	// settings available in web.xml
	private String MaxFileSize = "500000";
	private String DotExec = "/usr/bin/dot";
	private String DefaultRule = null;

	private final String UTF8 = "UTF-8";


	public <T> T getValue (T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		DotExec     = getValue(config.getInitParameter("DotExec"), DotExec);
		MaxFileSize = getValue(config.getInitParameter("MaxInput"), MaxFileSize);
		DefaultRule = getValue(config.getInitParameter("DefaultRule"), DefaultRule);
	}

	@Override
	protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {

		// default values
		String 
		pathRDF = null, 
		pathRules = DefaultRule, 
		formatRDF = "TTL",
		formatDot = "svg";

		try {
			pathRDF   = getValue(request.getParameter(pRDF), pathRDF);
			pathRules = getValue(request.getParameter(pRules), pathRules);
			formatRDF = getValue(request.getParameter(pRDFFormat), formatRDF);
			formatDot = getValue(request.getParameter(pDotFormat), formatDot);

			checkURIInput(pathRDF);
			checkURIInput(pathRules);

			Model model = getDotModel(pathRDF, formatRDF, pathRules);
			String dot = RDF2Dot.toDot(model);

			DotProcess dotProcess = new DotProcess(DotExec);
			String out;
			String mimetype;

			if (formatDot.equals("svg")) {
				out = dotProcess.runDot(dot, formatDot);
				mimetype = "image/svg+xml";
			} else if (formatDot.equals("png")) {
				out = dotProcess.runDot(dot, formatDot);
				mimetype = "image/png";
			} else if (formatDot.equals("ttl")) {
				out = Models.writeModel(model, "TTL");
				mimetype = "text/turtle";
			} else {
				out = dot;
				mimetype = "text/plain";
			}
			respond (response, out, mimetype);
		} catch (RuntimeException | IOException | ServletException e) {
			printError(request, response, 500, pathRDF, pathRules, e); 
		}
	}

	// apply rules to input RDF to saturate with DOT vocabulary
	private Model getDotModel (String pathRDF, String formatRDF, String pathRules) {
		Model model = Models.readModel(pathRDF, formatRDF);
		List<Rule> rules = Rule.rulesFromURL(pathRules);
		Model dotModel = Models.applyRules(model, rules);
		return dotModel;
	}

	// pass content on to response's writer
	private void respond (HttpServletResponse response, String content, String mimetype) throws IOException {
		response.setCharacterEncoding(UTF8);
		response.setContentType(mimetype);
		PrintWriter writer = response.getWriter();
		writer.write(content);
		writer.flush();
		writer.close();
	}

	// check that (1) URL resolves, (2) with code 200, (3) content not larger than max limit.
	private void checkURIInput (String path) throws IOException, ServletException {
		HttpURLConnection connection;
		int code;
		try{
			URL u = new URL(path);
			connection = (HttpURLConnection) u.openConnection();
			connection.connect();
			code = connection.getResponseCode();
		} catch (java.net.MalformedURLException ex) {
			throw new IllegalArgumentException ("Error handling URI: '" + path + "': Malformed URL " + ex.getMessage());
		}
		if (code != 200) {
			throw new IllegalArgumentException ("Error retrieving URI: '" + path + "'. URI returned code " + code);
		}
		int size = connection.getContentLength();
		int max = Integer.parseInt(MaxFileSize);
		if (size > max) {
			throw new ServletException("Error loading URI: '"+path+"'. File size ("+size+") exceeds the max file size set to: " + max);
		}
	}

	// nice-ish error page
	private void printError (HttpServletRequest request, HttpServletResponse response, int responseCode,
			String pathRDF, String pathRules, Exception e) throws IOException {

		response.setStatus(responseCode);
		response.setContentType("text/html;charset=\"UTF-8\"");

		PrintWriter writer = response.getWriter();
		String error = "<html><head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\" />";
		error += "<title>RDFVizler - Error " + responseCode + "</title>";
		error += "</head><body>";
		error += "<h1>RDFVizler - Error " + responseCode + "</h1>";
		error += "<dl>";
		error += "<dt>Service:</dt><dd>" + request.getRequestURL() + "</dd>";
		//error += "<dt>Parameters:</dt><dd>" + Arrays.toString(request.getParameterMap().entrySet().toArray()) + "</dd>";
		error += "<dt>RDF:</dt><dd> " + pathRDF + "</dd>";
		error += "<dt>Rules:</dt><dd> " + pathRules + "</dd>";
		error += "<dt>Error message:</dt><dd><code>" + StringEscapeUtils.escapeHtml4(e.getMessage()) + "</code></dd>";
		error += "<dt>Error stack:</dt><dd><pre>" + StringEscapeUtils.escapeHtml4(Arrays.toString(e.getStackTrace())).replaceAll(",", "<br/>") + "</pre></dd>";
		error += "</dl>";
		error += "</body></html>";
		writer.print(error);
		writer.flush();
		writer.close();
	}
}
