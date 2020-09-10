package org.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class FieldIterator {

    private static HashMap<String, Field[]> map = new HashMap<>();

    public static void foreach(Class<?> clazz, Consumer<Field> consumer) {
        Field[] fields = getFrom(clazz);
        for (Field field : fields) consumer.accept(field);
    }

    private static Field[] getFrom(Class<?> clazz) {
        if (!map.containsKey(clazz.getName()))
            map.put(clazz.getName(), refine(clazz));
        return map.get(clazz.getName());
    }

    private static Field[] refine(Class<?> clazz) {
        HashSet<Field> set = new HashSet<>(Arrays.asList(clazz.getDeclaredFields()));
        set.addAll(Arrays.asList(clazz.getFields()));
        return (Field[]) set.toArray();
    }

}
