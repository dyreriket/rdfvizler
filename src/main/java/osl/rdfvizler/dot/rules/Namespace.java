package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class Namespace extends BaseBuiltin {

    @Override
    public String getName() {
        return "namespace";
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        super.checkArgs(length, context);
        return BuiltInUtils.bindArgNode(args[1], value(args[0], context), context);
        
    }

    protected Node value(Node node, RuleContext context) {
        if (node.isURI()) {
            return ResourceFactory.createPlainLiteral(node.getNameSpace()).asNode();
        } else {
            throw new BuiltinException(this, context, "Illegal node type: " + node);
        }
    }
}
