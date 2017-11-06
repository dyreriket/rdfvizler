package osl.rdfvizler.ui.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.ui.RDFVizler;

public class RDFVizlerServlet extends HttpServlet {

    private static final long serialVersionUID = 7193847752589093476L;

    // URL params
    private static final String pRDF = "rdf";
    private static final String pRules = "rules";
    private static final String pRDFFormat = "in";
    private static final String pDotFormat = "out";

    private static final String UTF8 = "UTF-8";

    // defaults, also available in web.xml
    private String defaultDotExec;
    private String defaultPathRules;
    private String defaultFormatRDF;
    private String defaultFormatDot;

    private int maxFileSize;

    private static <T> T getValue(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    // possibly overwrite defaults with values from web.xml
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        defaultDotExec = getValue(config.getInitParameter("DotExec"), DotProcess.DEFAULT_EXEC);
        maxFileSize = Integer.parseInt(getValue(config.getInitParameter("MaxInput"), "30000"));
        defaultPathRules = getValue(config.getInitParameter("DefaultRules"), RDFVizler.DEFAULT_RULES);
        defaultFormatRDF = getValue(config.getInitParameter("DefaultFormatRDF"), RDFVizler.DEFAULT_INPUT_FORMAT);
        defaultFormatDot = getValue(config.getInitParameter("DefaultFormatDot"), RDFVizler.DEFAULT_OUTPUT_FORMAT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathRDF = null;
        String pathRules = null;
        String outputFormat;

        try {
            pathRDF = getValue(request.getParameter(pRDF), pathRDF);
            RDFVizler rdfvizler = new RDFVizler(pathRDF);
            rdfvizler.setDotExecutable(defaultDotExec);

            pathRules = getValue(request.getParameter(pRules), defaultPathRules);
            rdfvizler.setRulesPath(pathRules);

            checkURIInput(pathRDF);
            checkURIInput(pathRules);
            
            rdfvizler.setInputFormat(getValue(request.getParameter(pRDFFormat), defaultFormatRDF));
            outputFormat = getValue(request.getParameter(pDotFormat), defaultFormatDot);

            String output = rdfvizler.writeOutput(outputFormat);
            String mimetype = getMinetype(outputFormat);

            respond(response, output, mimetype);
        } catch (RuntimeException | IOException e) {
            respond(response, getErrorMessage(500, pathRDF, pathRules, e), "text/html");
        }
    }

    private String getMinetype(String format) {
        String mimetype;
        if ("svg".equals(format)) {
            mimetype = "image/svg+xml";
        } else if ("ttl".equals(format)) {
            mimetype = "text/turtle";
        } else {
            mimetype = "text/plain";
        }
        return mimetype;
    }

    // pass content on to response's writer
    private void respond(HttpServletResponse response, String content, String mimetype) throws IOException {
        response.setCharacterEncoding(UTF8);
        response.setContentType(mimetype);
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
        writer.close();
    }

    // check that (1) URL resolves, (2) with code 200, and (3) content not larger
    // than max limit.
    private void checkURIInput(String path) throws IOException, IllegalArgumentException {
        HttpURLConnection connection;
        int httpCode;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            httpCode = connection.getResponseCode();
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException("Error handling URI: '" + path + "': Malformed URL " + ex.getMessage());
        }
        if (httpCode != 200) {
            throw new IllegalArgumentException("Error retrieving URI: '" + path + "'. URI returned code " + httpCode);
        }
        int size = connection.getContentLength();

        if (size > maxFileSize) {
            throw new IllegalArgumentException("Error loading URI: '" + path + "'. " + "File size (" + size
                    + ") exceeds the max file size set to: " + maxFileSize);
        }
    }

    private String getErrorMessage(int responseCode, String pathRDF, String pathRules, Exception e) {
        String error = "<html><head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\" />";
        error += "<title>RDFVizler - Error " + responseCode + "</title>";
        error += "</head><body>";
        error += "<h1>RDFVizler - Error " + responseCode + "</h1>";
        error += "<dl>";
        error += "<dt>RDF:</dt><dd> " + pathRDF + "</dd>";
        error += "<dt>Rules:</dt><dd> " + pathRules + "</dd>";
        error += "<dt>Error message:</dt><dd><code>" + StringEscapeUtils.escapeHtml4(e.getMessage()) + "</code></dd>";
        error += "<dt>Error stack:</dt>"
                + "<dd><pre>" + StringEscapeUtils.escapeHtml4(Arrays.toString(e.getStackTrace())).replaceAll(",", "<br/>") + "</pre></dd>";
        error += "</dl>";
        error += "</body></html>";
        return error;
    }
}