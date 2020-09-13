package seven;

import org.ConnectionHandle;
import org.HostManager;

import java.io.IOException;

public class Server {

    HostManager manager;

    public Server(int expentees) throws IOException {
        manager = new HostManager(125325421235L, expentees);

        manager.addObjectReceivedListener(this::objr);
        manager.addOnInitializationListeners(this::init);
        manager.addClientJoinListener(this::cj);

        manager.start();
    }

    private void cj(ConnectionHandle handle) {
        System.out.println("SERVER: handle joined: " + handle);
    }

    private void init() {
        System.out.println("SERVER: Initialised ");
    }

    private void objr(Object object, ConnectionHandle handle) {
        System.out.println("SERVER: received: " + object);
        if (object instanceof Counter)
//            manager.send(((Counter) object).addOne(), connectionHandle -> !connectionHandle.equals(handle));
            manager.sendToAllExcept(((Counter) object).addOne(), handle);
    }

}
