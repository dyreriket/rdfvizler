package osl.util;

import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    
    public static <T> boolean inArray(T[] array, T value) {
        return java.util.Arrays.asList(array).contains(value);
    }
    
    public static <E> String toString(E[] objects, String glue) {
        return java.util.Arrays.stream(objects).map(object -> object.toString())
                .collect(Collectors.joining(glue));
    }

}
