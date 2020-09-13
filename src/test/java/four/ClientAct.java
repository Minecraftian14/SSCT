package four;

import org.ConnectionHandle;
import three.StringMessage;

import java.io.IOException;

public class ClientAct {

    static int ii = 1;

    ConnectionHandle handle;
    String name;

    public ClientAct() throws IOException, InterruptedException {

        name = "Client number: " + ii;
        ii++;

        handle = new ConnectionHandle(23874591);

        handle.addObjectReceivedListener(this::objReceived);

        handle.addOnInitializationListeners(() -> handle.send(new StrMsg(name)));

    }

    private void objReceived(Object o) {

    }

}
