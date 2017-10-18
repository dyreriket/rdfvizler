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

		return (lex(args[0], context).startsWith(lex(args[1], context)));
	}


	/**
	 * Return the appropriate lexical form of a node
	 */
	protected String lex(Node n, RuleContext context) {
		if (n.isBlank()) {
			return n.getBlankNodeLabel();
		} else if (n.isURI()) {
			return n.getURI();
		} else if (n.isLiteral()) {
			return n.getLiteralLexicalForm();
		} else {
			throw new BuiltinException(this, context, "Illegal node type: " + n);
		}
	}
}
