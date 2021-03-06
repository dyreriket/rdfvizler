package xyz.dyreriket.rdfvizler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public abstract class DotProcess {

    public enum ImageOutputFormat {
        svg//, png, pdf
    }

    public enum TextOutputFormat {
        dot
    }

    public static final String DEFAULT_DOT_EXEC = "/usr/bin/dot";
    public static final TextOutputFormat DEFAULT_TEXT_FORMAT = TextOutputFormat.dot;
    public static final ImageOutputFormat DEFAULT_IMAGE_FORMAT = ImageOutputFormat.svg;

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String defaultExec = DEFAULT_DOT_EXEC;

    private static Process getDotProcess(String exec, ImageOutputFormat format) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exec + " -Gcharset=" + CHARSET.name() + " -T" + format.toString());
        return process;
    }

    public static String runDot(String dot) throws IOException {
        return runDot(defaultExec, dot, DEFAULT_IMAGE_FORMAT);
    }

    public static String runDot(String dot, ImageOutputFormat format) throws IOException {
        return runDot(defaultExec, dot, format);
    }

    // convert dot spec into output format
    public static String runDot(String exec, String dot, ImageOutputFormat format) throws IOException {
        Process process = getDotProcess(exec, format);
        writeOutputStream(dot, process.getOutputStream());
        if (process.getErrorStream().available() > 0) {
            throw new IOException(
                    "Error parsing dot to " + format + ": " + IOUtils.toString(process.getErrorStream(), CHARSET));
        }
        return IOUtils.toString(process.getInputStream(), CHARSET);
    }

    private static void writeOutputStream(String dot, OutputStream outputStream) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(outputStream)) {
            out.write(dot.getBytes(CHARSET));
        }
    }

    // hiding constructor
    private DotProcess() {
        throw new IllegalStateException("Utility class");
    }
}
