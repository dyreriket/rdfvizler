package osl.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class Arrays {

    @SuppressWarnings("unchecked")
    public static <T> T getFirstNonNull(T... values) {
        return filteredFirstOrNull(values, x -> x != null);
    }
    
    public static String getFirstNonEmpty(String... values) {
        return filteredFirstOrNull(values, x -> x != null && !x.isEmpty());
    }
    
    private static <T> T filteredFirstOrNull(T[] values, Predicate<T> filter) {
        return java.util.Arrays.stream(values).filter(filter).findFirst().orElse(null);
    }
       
    @SuppressWarnings("unchecked")
    public static <E> List<E> toUnmodifiableList(E... objects) {
        return Collections.unmodifiableList(java.util.Arrays.asList(objects));
    }
}
