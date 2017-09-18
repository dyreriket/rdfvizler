package osl.rdfviz.rules.builtin;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class ShortValue extends BaseBuiltin {

	public String getName() {
		return "shortvalue";
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
		String s;
		if (n.isBlank() || n.isURI()) {
			s = BuiltInUtils.getShortForm(n, context);
		} else if (n.isLiteral()) {
			s = n.getLiteralLexicalForm();
			String dt = n.getLiteralDatatypeURI();
			if (dt != null && dt.length() > 0) {
				s += "^^" + BuiltInUtils.getShortForm(dt, context);
			}
		} else {
			throw new BuiltinException(this, context, "Illegal node type: " + n);
		}
		return ResourceFactory.createPlainLiteral(s).asNode();
	}
}
