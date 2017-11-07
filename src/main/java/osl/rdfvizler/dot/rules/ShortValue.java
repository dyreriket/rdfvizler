package osl.rdfvizler.dot.rules;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class ShortValue extends BaseBuiltin {

    @Override
    public String getName() {
        return "shortvalue";
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
        String string;
        if (node.isBlank() || node.isURI()) {
            string = BuiltInUtils.getShortForm(node, context);
        } else if (node.isLiteral()) {
            string = node.getLiteralLexicalForm();
            String datattype = node.getLiteralDatatypeURI();
            if (datattype != null && datattype.length() > 0) {
                string += "^^" + BuiltInUtils.getShortForm(datattype, context);
            }
        } else {
            throw new BuiltinException(this, context, "Illegal node type: " + node);
        }
        return ResourceFactory.createPlainLiteral(string).asNode();
    }
}
