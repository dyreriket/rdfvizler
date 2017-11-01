package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * linewrap(?bind, ?literal, ?integer)
 * Will bind a new literal to ?bind that is like ?literal but with a
 * linefeed at position ?integer. 
 */
public class Linewrap extends BaseBuiltin {

	public String getName() {
		return "linewrap";
	}

	public int getArgLength() {
		return 3;
	}

	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		super.checkArgs(length, context);
		BindingEnvironment env = context.getEnv();
		return env.bind(args[0], value(args[1], args[2], context));
	}

	protected Node value(Node literal, Node length, RuleContext context) {
		String val = BuiltInUtils.lex(literal, this, context);

		int splitPoint;
		if (length.isLiteral()) {
		    String splitStr = length.getLiteralLexicalForm();
		    splitPoint = Integer.parseInt(splitStr);
        }
        else {
		    throw new BuiltinException(this, context, "Third argument must be a number");
        }

		val = val.substring(0, splitPoint) + "\n" + val.substring(splitPoint);


		return ResourceFactory.createPlainLiteral(val).asNode();

	}


}
