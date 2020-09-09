package one;

import org.*;

import java.io.IOException;
import java.net.Socket;

import static java.lang.System.*;

public class main {

    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {

        // S
        HostManager server = new HostManager(28354L);
        server.addClientJoinListener(client -> out.println("New Client joined " + client.getSocket().getLocalAddress()));
        server.addObjectReceivedListener(object -> out.println("SR FC: " + object));
        server.addOnInitializationListeners(() -> server.send(new PackageTwo()));
        server.start();

        // C
        ConnectionHandle client = new ConnectionHandle(28354L);
        client.addObjectReceivedListener(object -> out.println("CR FS: " + object));

        PackageOne d = new PackageOne();
        client.send(d);
//        Socket socket = new Socket("192.168.29.18", 50152);

        // B
//        server.close();
//        client.close();

    }

}
