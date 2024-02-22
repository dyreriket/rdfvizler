package xyz.dyreriket.rdfvizler.rules;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class TypedValue extends ShortValue {

    @Override
    public String getName() {
        return "typedvalue";
    }
    
    protected Node value(Node node, RuleContext context) {

        String nodeString = node.isBlank()
            ? ""
            : RuleUtils.getShortForm(node, context);

        String types = RuleUtils.printSortedTypes(node, context);

        if (StringUtils.isNotEmpty(types)) {
            types += "\n";
        }

        return RuleUtils.stringAsNode(types + nodeString);
    }

}
