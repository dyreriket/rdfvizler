package xyz.dyreriket.rdfvizler.cli;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.URI;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import xyz.dyreriket.rdfvizler.DotProcess;
import xyz.dyreriket.rdfvizler.RDFVizler;
import xyz.dyreriket.rdfvizler.util.Models;

@Command(
    name = "java -jar rdfvizler-[version].jar", 
    versionProvider = xyz.dyreriket.rdfvizler.cli.RDFVizlerCLI.VersionProvider.class,
    sortOptions = false, 
    synopsisHeading = "Usage:%n", 
    descriptionHeading = "%nDescription:%n", 
    parameterListHeading = "%nParameters:%n", 
    optionListHeading = "%nOptions:%n",
    header = "%n" + RDFVizlerCLI.AppName + ": RDF visualisation%n",
    description = "RDFVizler visualises RDF by parsing a designated RDF RDFVizler "
            + "vocabulary into Graphviz syntax and processing this to a graph using "
            + "Graphviz' dot software. For more details, see http://rdfvizler.dyreriket.xyz."
    )
@SuppressFBWarnings(
        value = "URF_UNREAD_FIELD", 
        justification = "Errornous unread field report, perhaps due to picocli?")
@SuppressWarnings({"PMD.UnusedPrivateField"})
public class RDFVizlerCLI implements Runnable {

    public static final String AppName = "RDFVizler";
    
    private enum ExecutionMode {
        rdf, dot, image
    }
    
    @Parameters(paramLabel = "RDF_FILES", 
            arity = "1..*", 
            description = "Input RDF: URIs or file paths")
    private URI[] inFiles = new URI[0];

    @Option(names = { "-x", "--executionMode" }, 
            description = "What output to produce. (legal values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private ExecutionMode mode = ExecutionMode.image;

    @Option(names = { "-r", "--rules" }, 
            description = "Input rules: URI or file path (default: ${DEFAULT-VALUE})")
    private URI rules = RDFVizler.DEFAULT_RULES;

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

    @Option(names = { "--inputFormatRDF" }, 
            description = "Format of RDF input (legal values: ${COMPLETION-CANDIDATES}; "
                    + "default: ${DEFAULT-VALUE} -- by file extension as per jena.util.FileUtils, then Turtle)")
    private RDFVizler.RDFInputFormat inputFormatRDF = RDFVizler.RDFInputFormat.guess;

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
     * HINTS: 
     * if (line.hasOption(quiet)) {
           org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
             rootLogger.setLevel(Level.OFF);
       }
     */

    /*
    // TODO flag
    @Option(names = { "--dryrun" }, 
            description = "Produces no output other than what is written to console")
    boolean dryrun = false;
    */

    @Option(names = { "--version" }, versionHelp = true, description = "Display version info")
    private boolean versionInfoRequested;
    
    @Option(names = {"--help"}, usageHelp = true, description = "Display this help message")
    private boolean usageHelpRequested;
    
    public static void main(String[] args) {
        CommandLine.run(new RDFVizlerCLI(), args);
    }
    
    @Override
    public void run() {

        RDFVizler theViz = new RDFVizler();

        theViz.setSkipRules(this.skipRules);
        theViz.setDotExecutable(this.dotExec);
        theViz.setRulesPath(this.rules.toString());
        theViz.setInputFormat(this.inputFormatRDF);

        for (URI file : this.inFiles) {

            String filePath = file.toString();

            // get output
            String output = "";
            if (this.mode == ExecutionMode.rdf) {
                output = theViz.writeRDFDotModel(filePath, this.outputFormatRDF);
            } else if (this.mode == ExecutionMode.dot) {
                output = theViz.writeDotGraph(filePath);
            } else {
                try {
                    output = theViz.writeDotImage(filePath, this.outputFormatImage);
                } catch (IOException e) {
                    System.err.println("Error running dot process:");
                    e.printStackTrace();
                }
            }
            // print output
            System.out.println(output);            
        }
    }

    static class VersionProvider implements IVersionProvider {
        public String[] getVersion() throws Exception {
            return new String[] { 
                RDFVizlerCLI.AppName + " " + RDFVizlerCLI.class.getPackage().getImplementationVersion(),
                "",
                "",
                "  **,.*..  ..(.    Bæææ                ",
                "   .**..., %(/    /                    ",
                "      ... .,.    /                     ",
                "     **.*,*(,,.                        ",
                "     *,,(#/**..**                      ",
                "     .,.. .,..  .*..,. ,... .., ,      ",
                "     ,,.... .,........   .  .. ..,,    ",
                "     ,.     .. ...          . .. .,,   ",
                "     ...    ....     .     .. . .*,    ",
                "       ..    ...  .      ..... . ,.    ",
                "      .. **   ....     ...,,.  .,,     ",
                "       *,  /  ..*.,,*,,,(*,, .., ,     ",
                "        *.*.*,    ... .   ,,(*. ..     ",
                "        * ,. .              .(*(,/     ",
                "         , *.               *,,,       ",
                "            *        .      (/         ",
                "        /,*.,              .,,/.       ",
                "",
                ""
                };
        }
    }
}
