package xyz.dyreriket.sau.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.BuiltinException;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;
import org.apache.jena.vocabulary.RDF;

import xyz.dyreriket.sau.util.Strings;

public abstract class RuleUtils {

    public static final Comparator<Node> stringValueComparator = (Node p1, Node p2) -> p1.toString().compareTo(p2.toString());

    public static boolean bindArgNode(Node arg, Node value, RuleContext context) {
        return context.getEnv().bind(arg, value);
    }

    public static String printSortedTypes(Node node, RuleContext context) {
        List<Node> types = getTypes(node, context);
        Collections.sort(types, stringValueComparator);
        return Strings.toString(types, t -> getShortForm(t, context), ", ");
    }

    public static List<Node> getTypes(Node node, RuleContext context) {
        List<Node> types = new ArrayList<>();
        context.find(node, RDF.type.asNode(), Node.ANY).forEachRemaining(t -> types.add(t.getObject()));
        return types;
    }

    public static String getShortForm(Node node, RuleContext context) {
        if (node.isLiteral()) {
            return getLiteralShortForm(node, context);
        } else {
            return node.toString(context.getGraph().getPrefixMapping());
        }
    }

    public static String getShortForm(String uri, RuleContext context) {
        return context.getGraph().getPrefixMapping().shortForm(uri);
    }

    private static String getLiteralShortForm(Node literal, RuleContext context) {
        String string = literal.getLiteralLexicalForm();
        String datattype = literal.getLiteralDatatypeURI();
        if (!StringUtils.isEmpty(datattype)) {
            string += "^^" + RuleUtils.getShortForm(datattype, context);
        }
        return string;
    }

    public static String lexicalValue(Node node, BaseBuiltin that, RuleContext context) {
        if (node.isBlank()) {
            return node.getBlankNodeLabel();
        } else if (node.isURI()) {
            return node.getURI();
        } else if (node.isLiteral()) {
            return node.getLiteralLexicalForm();
        } else {
            throw new BuiltinException(that, context, "Illegal node type: " + node);
        }
    }

    public static Node stringAsNode(String string) {
        return ResourceFactory.createPlainLiteral(string).asNode();
    }

}
