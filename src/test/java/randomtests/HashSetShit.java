package randomtests;

import java.util.HashMap;
import java.util.HashSet;

public class HashSetShit {

    public static void main(String[] args) {

        HashSet<String> set = new HashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        set.add("D");
        set.add("E");
        set.forEach(System.out::println);

    }

}
