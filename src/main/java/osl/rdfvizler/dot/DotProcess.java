package osl.rdfvizler.dot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public abstract class DotProcess {
    
    public static final String DEFAULT_EXEC = "/usr/bin/dot";
    public static final String[] DOT_FORMATS = { "svg", "png", "pdf" };
    public static final String DEFAULT_FORMAT = "svg";
 
    // hiding constructor
    private DotProcess() {
        throw new IllegalStateException("Utility class");
    }
    
    public static String runDot(String dot) throws IOException {
        return runDot(DEFAULT_EXEC, dot, DEFAULT_FORMAT);
    }
    
    public static String runDot(String dot, String format) throws IOException {
        return runDot(DEFAULT_EXEC, dot, format);
    }

    // convert dot spec into output format
    public static String runDot(String exec, String dot, String format) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exec + " -Gcharset=UTF-8 -T" + format);
        
        writeOutputStream(dot, process.getOutputStream());
        
        if (process.getErrorStream().available() > 0) {
            throw new IOException("Error parsing dot to " + format + ": " + readInputStream(process.getErrorStream()));
        }
        return readInputStream(process.getInputStream());
    }
    
    private static void writeOutputStream(String dot, OutputStream outputStream) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        out.write(dot.getBytes(StandardCharsets.UTF_8));
        out.close();
    }

    // convenience method to slurp entire stream into a string
    private static String readInputStream(InputStream stream) throws IOException {
        String s = IOUtils.toString(stream, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(stream);
        return s;
    } 
}
