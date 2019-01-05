package xyz.dyreriket.rdfvizler.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class Namespace extends NodeFunction {

    @Override
    public String getName() {
        return "namespace";
    }

    protected Node value(Node node, RuleContext context) {
        if (node.isURI()) {
            return RuleUtils.stringAsNode(node.getNameSpace());
        } else {
            throw new BuiltinException(this, context, "Illegal node type: " + node);
        }
    }
}
