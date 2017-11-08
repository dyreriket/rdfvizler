package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class ShortValue extends NodeFunction {

    @Override
    public String getName() {
        return "shortvalue";
    }

    protected Node value(Node node, RuleContext context) {
        if (! (node.isBlank() || node.isURI() || node.isLiteral())) {
            throw new BuiltinException(this, context, "Illegal node type: " + node);
        } 
        String string = RuleUtils.getShortForm(node, context);
        return RuleUtils.stringAsNode(string);
    }
}
