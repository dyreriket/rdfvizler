package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * Returns true if the literal in arg1 begins with the literal in arg2
 */
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
        if (length != 2) {
            throw new BuiltinException(this, context, "Must have exactly 2 arguments to " + getName());
        }
        String arg1 = BuiltInUtils.lexicalValue(args[0], this, context);
        String arg2 = BuiltInUtils.lexicalValue(args[1], this, context);
        return arg1.startsWith(arg2);
    }
}
