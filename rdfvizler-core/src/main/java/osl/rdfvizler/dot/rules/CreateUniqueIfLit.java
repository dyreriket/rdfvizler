package osl.rdfvizler.dot.rules;

import java.util.UUID;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.reasoner.rulesys.RuleContext;

/**
 * createUniqueIfLit(?node, ?bind) If ?node is a literal, it will bind ?bind to
 * a new literal that is like ?node, but altered to be unique in the graph. This
 * is done by creating a literal that has a GUID as value.
 *
 * This function is useful when you want to have separate nodes for each literal
 * even if two literals have the same value. So instead of getting the graph to
 * the left, you can make the graph to the right.
 *
 * Resource1 Resource2 Resource1 Resource2 | | | | \___________| | | | | |
 * "someValue" "someValue" "someValue"
 *
 * If the ?node is a non-literal, ?bind simply binds to ?node.
 */
public class CreateUniqueIfLit extends NodeFunction {

    @Override
    public String getName() {
        return "createUniqueIfLit";
    }

    @Override
    protected Node value(Node node, RuleContext context) {
        if (node.isLiteral()) {
            UUID uuid = UUID.randomUUID();
            String uniqueLiteral = uuid.toString();
            return NodeFactory.createLiteral(uniqueLiteral);
        } else {
            return node;
        }
    }
}
