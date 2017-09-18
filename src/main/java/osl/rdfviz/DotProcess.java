package osl.rdfviz;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class DotProcess {

	private String exec = "/usr/bin/dot";

	public DotProcess () {}
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
