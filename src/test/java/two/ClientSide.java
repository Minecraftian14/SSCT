package two;

import org.Client;

import java.io.IOException;

public class ClientSide {

    Client client;

    public ClientSide() throws IOException {
        /*client = new Client(9999, "");
        client.addObjectReceivedListener(object -> System.out.println("CLIENT: Object received: " + object.getClass().getName() + ": " + object));
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                client.send(new SamplePack2());
                client.send(new SamplePack1());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();*/
    }

}
