package three;

import org.ConnectionHandle;

import java.io.IOException;

public class ClientActivity {

    ConnectionHandle connection;

    public ClientActivity(int l) throws IOException, InterruptedException {

        connection = new ConnectionHandle(123);
        connection.addObjectReceivedListener(object -> connection.close());

        connection.addOnInitializationListeners(() -> {
            for (int i = 0; i < l; i++) {
                connection.send(new StringMessage("Hello", 3, true));
                System.out.print('c');
            }
            connection.send(new TheEnd());
        });

    }

}
