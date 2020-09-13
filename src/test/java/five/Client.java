package five;

import org.ConnectionHandle;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Client {

    ConnectionHandle handle;
    GUI gui = new GUI();

    public Client() throws IOException, InterruptedException {
        handle = new ConnectionHandle(125325421235L);

        handle.addOnInitializationListeners(this::init);
        handle.addObjectReceivedListener(this::or);
    }

    private void init() {
        System.out.println("CLIENT: Initialised");
        gui.but.addActionListener(e -> {
            handle.send(new Counter(Integer.parseInt(gui.txt.getText())));
        });
        gui.frame.setVisible(true);
    }

    private void or(Object object) {
        if (object instanceof Counter)
            gui.txt.setText("" + ((Counter) object).value);
    }

}
