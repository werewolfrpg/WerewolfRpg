package net.aesten.wwrpg.utilities;

import java.util.Collection;

public class GeneralUtil {
    public static <T> Collection<T> addIf(Collection<T> collection, T object, boolean expression) {
        if (expression) {
            collection.add(object);
        }
        return collection;
    }
}
