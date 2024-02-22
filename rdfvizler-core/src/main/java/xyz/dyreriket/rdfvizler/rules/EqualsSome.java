package xyz.dyreriket.rdfvizler.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.Util;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * Returns true if the first argument is equal to one of the remaining arguments.
 */
public class EqualsSome extends BaseBuiltin {

    @Override
    public String getName() {
        return "equalssome";
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        if (length < 2) {
            throw new BuiltinException(this, context, "builtin " + this.getName() + " requires at least 2 arguments, but saw " + length);
        }

        for (int i = 1; i < args.length; i += 1) {
            if (isEqual(args[0], args[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isEqual(Node n1, Node n2) {
        if (Util.comparable(n1, n2)) {
            return Util.compareTypedLiterals(n1, n2) == 0;
        } else {
            return n1.sameValueAs(n2);
        }
    }
}
