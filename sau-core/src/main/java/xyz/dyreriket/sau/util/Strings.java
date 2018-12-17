package xyz.dyreriket.sau.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Strings {
    
    public static final String EMPTY = "";

    // hiding constructor
    private Strings() {
        throw new IllegalStateException("Utility class");
    }

    public static <E> String toString(Collection<E> objects, Function<E, Object> toString, String glue) {
        return objects.stream().map(object -> toString.apply(object).toString())
                .collect(Collectors.joining(glue));
    }
    
    public static <E> String toString(Collection<E> objects, String glue) {
        return objects.stream().map(object -> object.toString())
                .collect(Collectors.joining(glue));
    }
    
    public static String processNonEmpty(String input, Function<String, String> processor) {
        if (input.isEmpty()) {
            return EMPTY;
        } else {
            return processor.apply(input);
        }
    }
  
}
