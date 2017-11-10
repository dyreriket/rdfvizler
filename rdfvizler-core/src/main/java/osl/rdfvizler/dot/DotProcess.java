package osl.rdfvizler.dot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import osl.util.Arrays;

public abstract class DotProcess {

    public static final String ENV_RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";
    public static final String DEFAULT_EXEC = "/usr/bin/dot";
    public static final String[] DOT_FORMATS = { "svg", "png", "pdf" };
    public static final String DEFAULT_FORMAT = "svg";
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String defaultExec = Arrays.getFirstNonEmpty(
            System.getenv(ENV_RDFVIZLER_DOT_EXEC),
            DEFAULT_EXEC);

    // hiding constructor
    private DotProcess() {
        throw new IllegalStateException("Utility class");
    }
    
    public static String runDot(String dot) throws IOException {
        return runDot(defaultExec, dot, DEFAULT_FORMAT);
    }
    
    public static String runDot(String dot, String format) throws IOException {
        return runDot(defaultExec, dot, format);
    }

    // convert dot spec into output format
    public static String runDot(String exec, String dot, String format) throws IOException {
        exec = (exec != null) ? exec : defaultExec;
        Process process = getDotProcess(exec, format);
        writeOutputStream(dot, process.getOutputStream());
        if (process.getErrorStream().available() > 0) {
            throw new IOException("Error parsing dot to " + format + ": " + readInputStream(process.getErrorStream()));
        }
        return readInputStream(process.getInputStream());
    }
    
    private static Process getDotProcess(String exec, String format) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exec 
                + " -Gcharset=" + CHARSET.name() 
                + " -T" + format);
        return process;
    }
    
    private static void writeOutputStream(String dot, OutputStream outputStream) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        out.write(dot.getBytes(CHARSET));
        out.close();
    }

    // convenience method to slurp entire stream into a string
    private static String readInputStream(InputStream stream) throws IOException {
        String s = IOUtils.toString(stream, CHARSET);
        IOUtils.closeQuietly(stream);
        return s;
    } 
}
