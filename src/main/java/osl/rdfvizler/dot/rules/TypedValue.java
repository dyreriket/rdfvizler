package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class TypedValue extends BaseBuiltin {
	
	public String getName() {
		return "typedvalue";
	}

	public int getArgLength() {
		return 2;
	}

	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		super.checkArgs(length, context);
		BindingEnvironment env = context.getEnv();
		return env.bind(args[1], value(args[0], context));
	}

	protected Node value(Node node, RuleContext context) {
		String string = "";
		if (node.isBlank() || node.isURI()) {
			string = BuiltInUtils.getTypes(node, context);
			if (node.isURI()) {
				if (!string.isEmpty()) {
					string += "\\n";
				}
				string += BuiltInUtils.getShortForm(node, context);
			}
		} else if (node.isLiteral()) {
			String datatype = node.getLiteralDatatypeURI();
			if (datatype != null && datatype.length() > 0) {
				string = BuiltInUtils.getShortForm(datatype, context) + "\\n";
			}
			string += node.getLiteralLexicalForm();
		} else {
			throw new BuiltinException(this, context, "Illegal node type: " + node);
		}
		return ResourceFactory.createPlainLiteral(string).asNode();
	}
}
