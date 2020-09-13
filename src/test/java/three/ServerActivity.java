package three;

import one.PackageOne;
import org.ConnectionHandle;
import org.HostManager;

import java.io.IOException;

public class ServerActivity {

    HostManager manager;

    public ServerActivity() throws IOException {

        manager = new HostManager(123, 1);
        manager.addClientJoinListener(this::clientJoined);
        manager.addObjectReceivedListener(this::objectReceived);
        manager.start();

    }

    private void clientJoined(ConnectionHandle handle) {
        System.out.println("Client joined " + handle);
    }

    private void objectReceived(Object object, ConnectionHandle sender) {
        System.out.print('s');
        System.out.flush();
        if (object instanceof TheEnd) {
            System.out.print('E');
            manager.send(new TheEnd());
            manager.close();
        }
    }

}
