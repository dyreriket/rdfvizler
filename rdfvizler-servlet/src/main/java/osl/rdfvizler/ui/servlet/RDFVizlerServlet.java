package osl.rdfvizler.ui.servlet;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import osl.rdfvizler.dot.DotProcess;
import osl.rdfvizler.ui.RDFVizler;

public class RDFVizlerServlet extends Servlet {

    private static final long serialVersionUID = 7193847752589093476L;

    // URL params
    private static final String pRDF = "rdf";
    private static final String pRules = "rules";
    private static final String pRDFFormat = "in";
    private static final String pDotFormat = "out";
    
    // defaults, also available in web.xml
    private String defaultDotExec;
    private String defaultPathRules;
    private String defaultInputFormat;
    private String defaultOutputFormat;

    
    private static <T> T getValue(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    private static String getInitValue(ServletConfig config, String paramName, String defaultValue) {
        return getValue(config.getInitParameter(paramName), defaultValue);
    }
    
    private static String getURLParamValue(HttpServletRequest request, String paramName, String defaultValue) {
        return getValue(request.getParameter(paramName), defaultValue);
    }

    // possibly overwrite defaults with values from web.xml
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        defaultDotExec = getInitValue(config, "DotExec", DotProcess.DEFAULT_EXEC);
        maxFileSize = Integer.parseInt(getInitValue(config, "MaxInput", "30000"));
        defaultPathRules = getInitValue(config, "DefaultRules", RDFVizler.DEFAULT_RULES);
        defaultInputFormat = getInitValue(config, "DefaultFormatRDF", RDFVizler.DEFAULT_INPUT_FORMAT);
        defaultOutputFormat = getInitValue(config, "DefaultFormatDot", RDFVizler.DEFAULT_OUTPUT_FORMAT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathRDF = null;
        String pathRules = null;
       
        try {
            pathRDF = getURLParamValue(request, pRDF, pathRDF);
            RDFVizler rdfvizler = new RDFVizler(pathRDF);
            rdfvizler.setDotExecutable(defaultDotExec);

            pathRules = getURLParamValue(request, pRules, defaultPathRules);
            rdfvizler.setRulesPath(pathRules);

            super.checkURIInput(pathRDF, OKCodes, maxFileSize);
            super.checkURIInput(pathRules, OKCodes, maxFileSize);
            
            rdfvizler.setInputFormat(getURLParamValue(request, pRDFFormat, defaultInputFormat));
            String outputFormat = getURLParamValue(request, pDotFormat, defaultOutputFormat);

            String output = rdfvizler.writeOutput(outputFormat);
            String mimetype = super.setMimetype(outputFormat);

            super.respond(response, output, mimetype);
        } catch (RuntimeException | IOException e) {
            super.respond(response, getErrorMessage(500, pathRDF, pathRules, e), "text/html");
        }
    }

    private String getErrorMessage(int responseCode, String pathRDF, String pathRules, Exception e) {
        StringBuilder str = new StringBuilder();
        str.append("<html><head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\" />");
        str.append("<title>RDFVizler - Error " + responseCode + "</title>");
        str.append("</head><body>");
        str.append("<h1>RDFVizler - Error " + responseCode + "</h1>");
        str.append("<dl>");
        str.append("<dt>RDF:</dt><dd> " + pathRDF + "</dd>");
        str.append("<dt>Rules:</dt><dd> " + pathRules + "</dd>");
        str.append("<dt>Error message:</dt><dd><code>" + StringEscapeUtils.escapeHtml4(e.getMessage()) + "</code></dd>");
        str.append("<dt>Error stack:</dt>"
                + "<dd><pre>" + StringEscapeUtils.escapeHtml4(Arrays.toString(e.getStackTrace())).replaceAll(",", "<br/>") + "</pre></dd>");
        str.append("</dl>");
        str.append("</body></html>");
        return str.toString();
    }
}