package xyz.dyreriket.sau.cli;

import java.io.IOException;
import java.net.URI;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import xyz.dyreriket.sau.DotProcess;
import xyz.dyreriket.sau.Sau;
import xyz.dyreriket.sau.util.Models;

@Command(
    name = "java -jar sau-[version].jar", 
    version = { "Sau 0.1" }, 
    sortOptions = false, 
    synopsisHeading = "%n", 
    descriptionHeading = "%nDescription:%n", 
    parameterListHeading = "%nParameters:%n", 
    optionListHeading = "%nOptions:%n", 
    header = "%nSau:%n - Visualising RDF.%n"
            + " - Customised formatting via rule saturation.%n"
            + " - Leverages Jena for handling RDF and Graphviz for graph visualisation.%n"
            + " - See https://github.com/dyreriket/sau", 
    description = ""
    )
public class SauCLI implements Runnable {
 
    private enum ExecutionMode {
        rdf, dot, image
    }
    
    public static void main(String[] args) throws IOException {
        CommandLine.run(new SauCLI(), args);
    }

    @Parameters(paramLabel = "RDF_FILES", description = "Input RDF: URIs or file paths")
    private URI[] inFiles = new URI[0];

    @Option(names = { "-x", "--executionMode" }, 
            description = "What output to produce. (legal values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private ExecutionMode mode = ExecutionMode.image;

    @Option(names = { "-r", "--rules" }, 
            description = "Input rules: URI or file path (default: ${DEFAULT-VALUE})")
    private URI rules = Sau.DEFAULT_RULES;

    // TODO
    /*
     * @Option(names = { "--stdin" }, description =
     * "Read input from stdin (default: ${DEFAULT-VALUE})") private boolean stdin =
     * false;
     */

    /* 
    @Option(names = { "--stdout" }, 
            description = "Write output to stdout (default: ${DEFAULT-VALUE})") 
    private boolean stdout = true;

    @Option(names = { "-o", "--out" }, 
            description = "Output file. Defaults to input file + --outExtension value. "
                    + "NB! If this option is set to a specific value when processing multiple inputs, "
                    + "then all output will be written to this single output file.") 
    private File outFile;

    @Option(names = { "-of", "--outFolder" }, 
            description = "Output folder. Defaults to current directory.") 
    private File outFolder;

    @Option(names = { "-oe", "--outExtension" }, 
            description = "Extension appended to output files when written to folder. Defaults to outputFormat value." ) 
    private String outExtension; 
    */
    
    @Option(names = { "--skipRules" }, 
            description = "Skip rule application to input? (default: ${DEFAULT-VALUE})")
    private boolean skipRules = false;

    // TODO: default, guess value based on extension
    @Option(names = { "--inputFormatRDF" }, 
            description = "Format of RDF input (legal values: ${COMPLETION-CANDIDATES}; "
                    + "default: ${DEFAULT-VALUE} -- by file extension as per jena.util.FileUtils, then Turtle)")
    private Sau.RDFInputFormat inputFormatRDF = Sau.RDFInputFormat.guess;

    /*
    @Option(names = { "--outputFormatDot" }, 
            description = "Format of Dot output (legal values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE})") 
    private DotProcess.TextOutputFormat outputFormatDot = DotProcess.DEFAULT_TEXT_FORMAT;
    */
    
    @Option(names = { "--outputFormatRDF" }, 
            description = "Format of RDF output (legal values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private Models.RDFformat outputFormatRDF = Models.DEFAULT_RDF_FORMAT;

    @Option(names = { "-i", "--outputFormatImage" }, 
            description = "Format of image output (legal values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private DotProcess.ImageOutputFormat outputFormatImage = DotProcess.DEFAULT_IMAGE_FORMAT;

    @Option(names = { "--dotExecutable" }, 
            description = "Path to dot executable (default: ${DEFAULT-VALUE})")
    private String dotExec = DotProcess.DEFAULT_DOT_EXEC;

    /*
     * // TODO flag
     * 
     * @Option(names = {"--quiet"}) boolean quiet = false;
     */

    // TODO flag
    @Option(names = { "--dryrun" }, 
            description = "Produces no output other than what is written to console")
    boolean dryrun = false;

    @Option(names = { "--version" }, versionHelp = true, description = "display version info")
    boolean versionInfoRequested;

    public SauCLI() {
    }

    @Override
    public void run() {

        Sau sau = new Sau();

        sau.setSkipRules(this.skipRules);
        sau.setDotExecutable(this.dotExec);
        sau.setRulesPath(this.rules.toString());
        sau.setInputFormat(this.inputFormatRDF);

        for (URI file : this.inFiles) {

            String filePath = file.toString();

            // get output
            String output = "";
            switch (this.mode) {
                case rdf:
                    output = sau.writeRDFDotModel(filePath, this.outputFormatRDF);
                    break;
                case dot:
                    output = sau.writeDotGraph(filePath);
                    break;
                default: // case image:
                    try {
                        output = sau.writeDotImage(filePath, this.outputFormatImage);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }

            // write output
            if (!this.dryrun) {
                System.out.println(output);
            }
        }
    }

}
