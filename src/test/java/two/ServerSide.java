package two;

import org.Server;
import org.SimpleConditions;

import java.io.IOException;

public class ServerSide {

    Server server;

    public ServerSide() throws IOException {
        /*server = new Server(9999, SimpleConditions.stopAfterSeconds(5));
        server.addClientJoinListener(client -> System.out.println("SERVER: Client joined: " + client.toString()));
        server.addObjectReceivedListener(object -> System.out.println("SERVER: Object Received: " + object.getClass().getName() + ": " + object));
        server.onReady(this::actSt);
        server.start();*/
    }

    public void actSt() {
//        server.send(new SamplePack2());
    }

}
