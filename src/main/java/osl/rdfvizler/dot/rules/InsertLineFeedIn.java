package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class InsertLineFeedIn extends BaseBuiltin {

	public String getName() {
		return "insertLineFeedIn";
	}

	public int getArgLength() {
		return 2;
	}

	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		super.checkArgs(length, context);
		BindingEnvironment env = context.getEnv();
		return env.bind(args[1], value(args[0], context));
	}

	protected Node value(Node n, RuleContext context) {
		String val = BuiltInUtils.lex(n, this, context);

		val = val.substring(0,  val.length()/2) + "\n" + val.substring(val.length()/2);

		if (n.isURI()) {
			return ResourceFactory.createPlainLiteral(val).asNode();
		} else {
			throw new BuiltinException(this, context, "Illegal node type: " + n);
		}
	}


}
