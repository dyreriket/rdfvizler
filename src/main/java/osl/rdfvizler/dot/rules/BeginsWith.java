package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class BeginsWith extends BaseBuiltin {

	@Override
	public String getName() {
		return "beginsWith";
	}

	@Override
	public int getArgLength() {
		return 2;
	}

	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		if (length != 2)
			throw new BuiltinException(this, context, "Must have exactly 2 arguments to " + getName());

		String arg1 = BuiltInUtils.lex(args[0], this, context);
		String arg2 = BuiltInUtils.lex(args[1], this, context);
		return (arg1.startsWith(arg2));
	}
}
