package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class Namespace extends BaseBuiltin {
	
	public String getName() {
		return "namespace";
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
		if (n.isURI()) {
			return ResourceFactory.createPlainLiteral(n.getNameSpace()).asNode();
		} else {
			throw new BuiltinException(this, context, "Illegal node type: " + n);
		}
	}
}
