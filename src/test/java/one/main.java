package one;

import org.Client;
import org.Server;
import org.SimpleConditions;

import java.io.IOException;

import static java.lang.System.*;

public class main {

    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {

        // S
        Server server = Server.createServer(4768, SimpleConditions.stopAfterSeconds(10));
        server.addClientJoinListener(client -> {
            out.println("New one.main.Client joined " /*+ client.getSocket().getLocalAddress()*/);
            PackageTwo toBeSent = new PackageTwo();
            toBeSent.demo = new PackageOne(123, 234L, 23.8, "kahgd", false);
            client.send(toBeSent);
        });
        server.addObjectReceivedListener(object -> out.println("SR FC: " + object));
        server.start();

        // C
        Client client = new Client(4768, "");
        client.addObjectReceivedListener(object -> out.println("CR FS: " + object));

        PackageOne d = new PackageOne();
        client.send(d);

        // B
//        server.close();
//        client.close();

    }

}
