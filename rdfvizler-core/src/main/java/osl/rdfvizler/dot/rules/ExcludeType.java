package osl.rdfvizler.dot.rules;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * excludeType(?node, ?type) Returns true if ?node does NOT have ?type as an
 * rdf:type.
 */
public class ExcludeType extends BaseBuiltin {

    @Override
    public String getName() {
        return "excludeType";
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        super.checkArgs(length, context);
        return (!nodeHasType(args[0], args[1], context));
    }

    private boolean nodeHasType(Node thing, Node type, RuleContext context) {
        if (thing.isBlank() || thing.isURI()) {
            List<Node> listOfTypes = RuleUtils.getTypes(thing, context);
            return listOfTypes.stream().anyMatch(n -> n.getURI().equals(type.getURI()));
        } else {
            return false;
        }
    }
}