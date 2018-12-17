package xyz.dyreriket.sau.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public abstract class NodeFunction extends BaseBuiltin {

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        this.checkArgs(length, context);
        return RuleUtils.bindArgNode(args[1], value(args[0], context), context);
    }

    protected abstract Node value(Node node, RuleContext context);
}