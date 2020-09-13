package four;

import org.util.Try;
import org.util.adventurers.Discoverer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int noofclients = 5;

        new ServerAct(noofclients);

        for (int i = 0; i < noofclients; i++)
            start(ClientAct::new);

    }

    private static void start(Try.Runner runnable) {
        new Thread(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
