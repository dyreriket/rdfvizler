package osl.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Strings {
   
	public static <E> String toString (Collection<E> objects, Function<E, Object> toString, String glue) {
		return objects.stream()
				.map(object -> toString.apply(object).toString())
				.collect(Collectors.joining(glue));
	}
}
