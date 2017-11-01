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
import java.util.UUID;

/**
 * createUniqueIfLit(?bind, ?node)
 * If ?node is a literal, it will bind ?bind to a new literal
 * that is like ?node, but altered to be unique in the graph. This is done by
 * creating a literal that has a GUID as value.
 *
 * This function is useful when you want to have separate nodes for each literal
 * even if two literals have the same value. So instead of getting the graph
 * to the left, you can make the graph to the right.
 *
 * Resource1     Resource2        Resource1     Resource2
 *    |           |                   |             |
 *    \___________|                   |             |
 *         |                          |             |
 *      "someValue"               "someValue"    "someValue"
 *
 * This requires some alterations to the default .jrule file.
 *
 * If the ?node is a non-literal, ?bind simply binds to ?node.
 */
public class CreateUniqueIfLit extends BaseBuiltin {
	
	public String getName() {
		return "createUniqueIfLit";
	}

	public int getArgLength() {
		return 2;
	}


    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {

        Node nodeToBind = args[1];

        if (nodeToBind.isLiteral()) {
            UUID uuid = UUID.randomUUID();
            String uniqueLiteral = uuid.toString();
            nodeToBind =  NodeFactory.createLiteral(uniqueLiteral);
        }

        return context.getEnv().bind(args[0], nodeToBind);
    }




}
