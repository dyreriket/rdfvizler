package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class ShortValue extends NodeFunction {

    @Override
    public String getName() {
        return "shortvalue";
    }

    protected Node value(Node node, RuleContext context) {
        String string;
        if (node.isBlank() || node.isURI()) {
            string = RuleUtils.getShortForm(node, context);
        } else if (node.isLiteral()) {
            string = node.getLiteralLexicalForm();
            String datattype = node.getLiteralDatatypeURI();
            if (datattype != null && datattype.length() > 0) {
                string += "^^" + RuleUtils.getShortForm(datattype, context);
            }
        } else {
            throw new BuiltinException(this, context, "Illegal node type: " + node);
        }
        return ResourceFactory.createPlainLiteral(string).asNode();
    }
}
