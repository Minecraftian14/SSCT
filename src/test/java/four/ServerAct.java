package four;

import org.ConnectionHandle;
import org.HostManager;

import java.io.IOException;

public class ServerAct {

    HostManager manager;

    public ServerAct(int i) throws IOException {

        manager = new HostManager(23874591);
        manager.addObjectReceivedListener(this::objReceived);
        manager.addClientJoinListener(this::clientJoined);
        manager.addOnInitializationListeners(this::initialized);
        manager.start();

    }

    private void initialized() {
        System.out.println("Server Inited");
    }

    private void clientJoined(ConnectionHandle handle) {
        System.out.println("CC JJ: " + handle);
    }

    private void objReceived(Object o, ConnectionHandle sender) {
        System.out.println(o);
    }

}
