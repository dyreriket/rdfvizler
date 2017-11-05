package osl.rdfvizler.dot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class DotProcess {

    public static final String ENV_RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";

    public static final String DEFAULT_EXEC = "/usr/bin/dot";

    private String exec = DEFAULT_EXEC;

    public DotProcess() {
        String newExec = System.getenv(ENV_RDFVIZLER_DOT_EXEC);
        if (newExec != null && !newExec.isEmpty()) {
            exec = newExec;
        }
        else {
            exec = DEFAULT_EXEC;
        }
    }

    public DotProcess(String execpath) {
        this.exec = execpath;
    }

    // convert dot spec into output format
    public String runDot(String dot, String format) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exec + " -Gcharset=UTF-8 -T" + format);
        BufferedOutputStream outputStream = new BufferedOutputStream(process.getOutputStream());
        outputStream.write(dot.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        if (process.getErrorStream().available() > 0) {
            throw new IOException("Error parsing dot to " + format + ": " + readStream(process.getErrorStream()));
        }
        return readStream(process.getInputStream());
    }

    // convenience method to slurp entire stream into a string
    private String readStream(InputStream stream) throws IOException {
        String s = IOUtils.toString(stream, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(stream);
        return s;
    }
}
