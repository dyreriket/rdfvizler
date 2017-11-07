package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public abstract class BiNodeRuleFunction extends BaseBuiltin {

    public BiNodeRuleFunction() {
        super();
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        this.checkArgs(length, context);
        return BuiltInUtils.bindArgNode(args[1], value(args[0], context), context);
    }

    protected abstract Node value(Node node, RuleContext context);
}