package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;
import org.apache.jena.shared.JenaException;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateUniqueIfLit extends BaseBuiltin {
	
	public String getName() {
		return "createUniqueIfLit";
	}

	public int getArgLength() {
		return 2;
	}

	public static int counter =0;
	private static String unlikelyString = "--------";

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        StringBuilder key = new StringBuilder();
        

        Node n = args[1];
        Node nodeToBind = n;

        if (n.isLiteral()) {
            key.append("L"); key.append(n.getLiteralLexicalForm());
            if (n.getLiteralLanguage() != null) key.append("@" + n.getLiteralLanguage());
            if (n.getLiteralDatatypeURI() != null) key.append("^^" + n.getLiteralDatatypeURI());
            key.append(counter++);

            String newLitValue = n.getLiteralLexicalForm()
                    + unlikelyString
                    + counter
                    + unlikelyString;
            nodeToBind = NodeFactory.createLiteral(newLitValue);

        }

        return context.getEnv().bind(args[0], nodeToBind);
    }




}
