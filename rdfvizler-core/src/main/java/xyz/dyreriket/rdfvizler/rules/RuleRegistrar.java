package xyz.dyreriket.rdfvizler.rules;

import org.apache.jena.reasoner.rulesys.Builtin;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;

public class RuleRegistrar {

    public static void registerRules() {
        register(new ShortValue());
        register(new Namespace());
        register(new TypedValue());
        register(new BeginsWith());
        register(new Linewrap());
        register(new ExcludeType());
        register(new CreateUniqueIfLit());
        register(new EqualsSome());
    }
    
    public static void register(Builtin rule) {
        BuiltinRegistry.theRegistry.register(rule);
    }
}
