package osl.rdfvizler.ui.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;

import osl.util.Arrays;

public abstract class Servlet extends HttpServlet {
    
    protected static int[] OKCodes = { 200 };
    protected int maxFileSize;

    private static final long serialVersionUID = -7780985876220754149L;
    
    private static final String UTF8 = "UTF-8";
    private static final Map<String, String> MIMETYPE;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("svg", "image/svg+xml");
        m.put("TTL", "text/turtle");
        m.put("RDF/XML", "application/rdf+xml");
        m.put("N3", "text/n3");
        m.put("N-TRIPLES", "application/rdf+xml");
        MIMETYPE = Collections.unmodifiableMap(m);
    }
    
    public static String setMimetype(String format) {
        return MIMETYPE.getOrDefault(format, "text/plain");
    }

    // check that 
    //  (1) URL resolves, 
    //  (2) with code 200
    //  (3) content not larger than max limit.
    protected void checkURIInput(String path, int[] codes, int maxSize) throws IOException {
        HttpURLConnection connection = getHttpConnection(path);
        checkHttpCode(connection, codes);
        checkHttpContentSize(connection, maxSize);
    }

    protected static HttpURLConnection getHttpConnection(String path) throws IOException {
        HttpURLConnection connection;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            throw new IOException("Error handling URI: '" + path + "': Malformed URL " + ex.getMessage());
        }
        return connection;
    }

    protected boolean checkHttpCode(HttpURLConnection connection, int... legalCodes) throws IOException {
        int httpCode = connection.getResponseCode();
        Integer[] intObjects = ArrayUtils.toObject(legalCodes);
        if (Arrays.inArray(intObjects, httpCode)) {
            throw new IOException("Error retrieving URI: '" + connection.getURL().toString() 
                    + "'. URI returned code " + httpCode
                    + ", expected: " + Arrays.toString(intObjects, ", "));
        }
        return true;
    }

    protected boolean checkHttpContentSize(HttpURLConnection connection, int maxSize) {
        int size = connection.getContentLength();
        if (size > maxSize) {
            throw new IllegalArgumentException("Error loading URI: '" + connection.getURL().toString() + "'. " 
                    + "File size (" + size + ") exceeds the max file size set to: " + maxSize);
        }
        return true;
    }

    // pass content on to response's writer
    protected static void respond(HttpServletResponse response, String content, String mimetype) throws IOException {
        response.setCharacterEncoding(UTF8);
        response.setContentType(mimetype);
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
        writer.close();
    }

}
