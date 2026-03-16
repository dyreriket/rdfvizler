package xyz.dyreriket.rdfvizler.cli;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import org.apache.jena.sys.JenaSystem;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import xyz.dyreriket.rdfvizler.RDFVizler;
import xyz.dyreriket.rdfvizler.util.Models;

@Command(
    name = "java -jar rdfvizler-[version].jar", 
    versionProvider = RDFVizlerCLI.VersionProvider.class,
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
        justification = "Erroneous unread field report, perhaps due to picocli?")
@SuppressWarnings({"PMD.UnusedPrivateField"})
public class RDFVizlerCLI implements Runnable {

    public static final String AppName = "RDFVizler";
    
    private enum ExecutionMode {
        rdf, dot, image
    }
    
    private RDFVizler viz;
    
    @Parameters(paramLabel = "RDF_FILES", 
            arity = "1..*", 
            description = "Input RDF: URIs or file paths")
    private URI[] inFiles = new URI[0];

    @Option(names = { "-x", "--executionMode" }, 
            description = "What output to produce. (legal values: ${COMPLETION-CANDIDATES}; default: ${DEFAULT-VALUE})")
    private ExecutionMode mode = ExecutionMode.image;

    @Option(names = { "-r", "--rules" },
            description = "Input rules: URI or file path (default: bundled rules)")
    private URI rules = null;

    @Option(names = { "--skipRules" }, 
            description = "Skip rule application to input? (default: ${DEFAULT-VALUE})")
    private boolean skipRules = false;

    @Option(names = { "--inputFormatRDF" },
            description = "Format of RDF input (e.g. ttl, rdf, nt; "
                    + "default: guessed by file extension, then Turtle)")
    private String inputFormatRDF = null;

    @Option(names = { "--mergeInput" },
        description = "Merge input files to a single model to visualise?")
    private boolean merge = false;

    @Option(names = { "--outputFormatRDF" },
            description = "Format of RDF output (e.g. ttl, rdf, nt; default: ${DEFAULT-VALUE})")
    private String outputFormatRDF = Models.DEFAULT_RDF_FORMAT_NAME;

    @Option(names = { "-i", "--outputFormatImage" },
            description = "Format of image output (e.g. SVG_STANDALONE, PNG; default: ${DEFAULT-VALUE})")
    private String outputFormatImage = "SVG_STANDALONE";

    @Option(names = { "--version" }, versionHelp = true, description = "Display version info")
    private boolean versionInfoRequested;
    
    @Option(names = {"--help"}, usageHelp = true, description = "Display this help message")
    private boolean usageHelpRequested;
    
    public static void main(String[] args) {
        JenaSystem.init();
        CommandLine.run(new RDFVizlerCLI(), args);
    }
    
    public RDFVizlerCLI() {
        this.viz = new RDFVizler();
    }
    
    private void init() {
        viz.setSkipRules(this.skipRules);
        if (this.rules != null) {
            viz.setRulesPath(this.rules.toString());
        }
        viz.setInputFormat(this.inputFormatRDF);
    }
    
    private void processFile(URI file) {
        String filePath = file.toString();

        // get output
        String output = "";
        if (this.mode == ExecutionMode.rdf) {
            output = viz.writeRDFDotModel(filePath, this.outputFormatRDF);
        } else if (this.mode == ExecutionMode.dot) {
            output = viz.writeDotGraph(filePath);
        } else {
            output = viz.write(filePath, this.outputFormatImage.toString());
        }
        // print output
        System.out.println(output);            
    }
    
    @Override
    public void run() {
        init();
        for (URI file : this.inFiles) {
            processFile(file);
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
