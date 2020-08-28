package org.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Registry {

    private static HashMap<Integer, Class<?>> readableObjects = new HashMap<>();

    public static void add(Class<?> objectClass) {
        readableObjects.put(objectClass.hashCode(), objectClass);
    }

    public static Class<?> get(int hash) {
        return readableObjects.get(hash);
    }

    public static void check(Class<?> objectClass) {
        if (!readableObjects.containsValue(objectClass))
            throw new RuntimeException("Object not registered!!!");
        else if (objectClass.getConstructors().length == 0)
            throw new RuntimeException("There are no constructors to respond to!!!");
        else {
            boolean is = false;
            for (Constructor<?> constructor : objectClass.getConstructors())
                if (constructor.getParameterCount() == 0) {
                    is = true;
                    break;
                }
            if (!is) throw new RuntimeException("There is no 'no-args' constructor!!!");
            is = false;
            for (Field field : objectClass.getDeclaredFields())
                if (field.isAnnotationPresent(SendField.class)) {
                    is = true;
                    break;
                }
            if (!is) throw new RuntimeException("There are no fields to write!!!");
            if (objectClass.isAnnotationPresent(SendField.class)) {
                for (Field field : objectClass.getDeclaredFields())
                    if (field.getType().equals(objectClass) && field.isAnnotationPresent(SendField.class))
                        throw new RuntimeException("You cant send a class containing a field of it's own type to be sent!!!");
            }
        }
    }
}
