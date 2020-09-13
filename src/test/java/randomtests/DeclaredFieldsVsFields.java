package randomtests;

import one.PackageOne;
import org.util.FieldIterator;

import java.lang.reflect.Field;

public class DeclaredFieldsVsFields {

    public static void main(String[] args) {

        Class<?> clazz = PackageOne.class;
        for (Field declaredField : clazz.getDeclaredFields()) System.out.println(declaredField);
        System.out.println();
        for (Field field : clazz.getFields()) System.out.println(field);
        System.out.println();

        FieldIterator.foreach(clazz, System.out::println);
        System.out.println();

        clazz = PackageThree.class;
        for (Field declaredField : clazz.getDeclaredFields()) System.out.println(declaredField);
        System.out.println();
        for (Field field : clazz.getFields()) System.out.println(field);
        System.out.println();

        FieldIterator.foreach(clazz, System.out::println);
    }

}
