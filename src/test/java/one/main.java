package one;

import org.*;
import org.util.Condition;
import org.util.adventurers.Discoverer;
import org.util.data.StreamReader;
import org.util.data.StreamWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.*;

import static java.lang.System.*;

public class main {

    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {

//        JavaLibTest();
        MyLibTest();
//        main();

    }

    private static void main() throws IOException {
        out.println("Yo");
        Discoverer d = new Discoverer(123);
        out.println("Yo");
        Discoverer dd = new Discoverer(123);
    }

    public static void MyLibTest() throws IOException, InterruptedException {
        // S
        HostManager server = new HostManager(28354L, 2);
        server.addClientJoinListener(client -> out.println("New Client joined " + client.getSocket().getLocalAddress()));
        server.addObjectReceivedListener((object, sender) -> out.println("SR FC: " + object));
        server.addOnInitializationListeners(() -> server.send(new PackageTwo()));
        server.start();

        // C
        ConnectionHandle client = new ConnectionHandle(28354L);
        client.addObjectReceivedListener(object -> out.println("CR FS: " + object));
        client.addOnInitializationListeners(() -> client.send(new PackageOne()));

        // C2
        ConnectionHandle client2 = new ConnectionHandle(28354L);
        client2.addObjectReceivedListener(object -> out.println("CR FS: " + object));
        client2.addOnInitializationListeners(() -> client2.send(new PackageOne()));
    }

    public static void JavaLibTest() throws IOException {

        ServerSocket ss = new ServerSocket(8080);

        new Thread(() -> {
            try {
                Socket cl1 = ss.accept();
                Socket cl2 = ss.accept();

                new StreamWriter(cl1.getOutputStream()).write(new PackageTwo());
                new StreamWriter(cl2.getOutputStream()).write(new PackageTwo());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Socket client = new Socket("", 8080);

                out.println(new StreamReader(client.getInputStream()).readObject());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Socket client = new Socket("", 8080);

                out.println(new StreamReader(client.getInputStream()).readObject());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
