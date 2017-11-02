package osl.rdfvizler.dot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class DotProcess {
	
	public static final String DefaultExec = "/usr/bin/dot";
	public static final String RDFVIZLER_DOT_EXEC = "RDFVIZLER_DOT_EXEC";

	private String exec = DefaultExec;

	public DotProcess () {
		String newExec = System.getenv(RDFVIZLER_DOT_EXEC);
		if (newExec != null && !newExec.isEmpty()) {
			exec = newExec;
		}
		else {
			exec = DefaultExec;
		}
	}

	public DotProcess (String execpath) {
		this.exec = execpath;
	}

	// convert dot spec into output format
	public String runDot (String dot, String format) throws IOException {	
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(exec +" -Gcharset=UTF-8 -T"+format);
		BufferedOutputStream outStreamProcess = new BufferedOutputStream(process.getOutputStream());
		outStreamProcess.write(dot.getBytes(StandardCharsets.UTF_8));
		outStreamProcess.close();

		if (process.getErrorStream().available() > 0) {
			throw new IOException ("Error parsing dot to " + format + ": " + 
					readStream(process.getErrorStream()));
		}
		return readStream(process.getInputStream());
	}

	// convenience method to slurp entire stream into a string 
	private String readStream (InputStream stream) throws IOException {
		String s = IOUtils.toString(stream, StandardCharsets.UTF_8);
		IOUtils.closeQuietly(stream);
		return s;
	}
}
