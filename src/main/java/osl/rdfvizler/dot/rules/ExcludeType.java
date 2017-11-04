package osl.rdfvizler.dot.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;
import org.apache.jena.vocabulary.RDF;

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

    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        if (length != 2) {
            throw new BuiltinException(this, context,
                    "Must have exactly 2 arguments to " + getName());
        }
        return (!nodeHasType(args[0], args[1], context));
    }

    private boolean nodeHasType(Node thing, Node type, RuleContext context) {
        if (thing.isBlank() || thing.isURI()) {
            List<Node> listOfTypes = getTypes(thing, context);
            return listOfTypes.stream().anyMatch(n -> n.getURI().equals(type.getURI()));
        } else {
            return false;
        }
    }

    private List<Node> getTypes(Node node, RuleContext context) {
        List<Node> types = new ArrayList<>();
        context.find(node, RDF.type.asNode(), Node.ANY)
                .forEachRemaining(t -> types.add(t.getObject()));
        return types;
    }
}