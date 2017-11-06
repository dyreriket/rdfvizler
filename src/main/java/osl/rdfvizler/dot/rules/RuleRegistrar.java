package osl.rdfvizler.dot.rules;

import org.apache.jena.reasoner.rulesys.BuiltinRegistry;

public class RuleRegistrar {
    
    static {
        BuiltinRegistry.theRegistry.register(new ShortValue());
        BuiltinRegistry.theRegistry.register(new Namespace());
        BuiltinRegistry.theRegistry.register(new TypedValue());
        BuiltinRegistry.theRegistry.register(new BeginsWith());
        BuiltinRegistry.theRegistry.register(new Linewrap());
        BuiltinRegistry.theRegistry.register(new ExcludeType());
        BuiltinRegistry.theRegistry.register(new CreateUniqueIfLit());
    }
}
