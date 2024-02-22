package xyz.dyreriket.rdfvizler.servlet;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import xyz.dyreriket.rdfvizler.RDFVizler;
import xyz.dyreriket.rdfvizler.util.Models;

public class RDFVizlerServlet extends Servlet {

    private static final long serialVersionUID = 7193847752589093476L;

    // URL params
    private static final String pRDF = "rdf";
    private static final String pRules = "rules";
    private static final String pRDFFormat = "in";
    private static final String pDotFormat = "out";
    
    // defaults, also available in web.xml
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
        maxFileSize = Integer.parseInt(getInitValue(config, "MaxInput", "30000"));
        defaultPathRules = getInitValue(config, "DefaultRules", RDFVizler.DEFAULT_RULES.toString());
        defaultInputFormat = getInitValue(config, "DefaultFormatRDF", Models.DEFAULT_RDF_FORMAT.toString());
        defaultOutputFormat = getInitValue(config, "DefaultFormatDot", Models.DEFAULT_RDF_FORMAT.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathRDF = null;
        String pathRules = null;
       
        try {
            RDFVizler rdfvizler = new RDFVizler();

            pathRDF = request.getParameter(pRDF);
            pathRules = getURLParamValue(request, pRules, defaultPathRules);
            rdfvizler.setRulesPath(pathRules);
            
            super.checkURIInput(pathRDF, maxFileSize);
            super.checkURIInput(pathRules, maxFileSize);

            rdfvizler.setInputFormat(Models.RDFformat.valueOf(getURLParamValue(request, pRDFFormat, defaultInputFormat)));
            String outputFormat = getURLParamValue(request, pDotFormat, defaultOutputFormat);

            String output = rdfvizler.write(pathRDF, outputFormat);
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
