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
        Node shortname = super.value(node, context);
        String types = RuleUtils.printSortedTypes(node, context);
        
        if (StringUtils.isNotEmpty(types)) {
            return RuleUtils.stringAsNode(types + "\n" + shortname.toString());
        } else {
            return shortname;
        }
    }
}
