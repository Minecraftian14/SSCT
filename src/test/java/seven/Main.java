package seven;

import org.util.Try;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        int c = 2;
        new Thread(() -> Try.JustCatchAndReturn(() -> new Server(c), null)).start();

        for (int i = 0; i < c; i++)
            new Thread(() -> Try.JustCatchAndReturn(Client::new, null)).start();

    }

}
