package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class TypedValue extends BiNodeRuleFunction {

    @Override
    public String getName() {
        return "typedvalue";
    }

    protected Node value(Node node, RuleContext context) {
        String string = "";
        if (node.isBlank() || node.isURI()) {
            string = BuiltInUtils.printSortedTypes(node, context);
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
