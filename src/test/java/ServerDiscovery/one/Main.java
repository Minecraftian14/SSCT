package ServerDiscovery.one;

import org.SimpleConditions;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

//        new ServerSide(100L);
//        new ServerSide(100L);
        System.out.println(1);
        new ClientSide(100L, SimpleConditions.stopAfterSeconds(10), l -> l.forEach(System.out::println));
System.out.println(2);

    }

}
