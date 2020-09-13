package six;

import org.ConnectionHandle;
import org.HostManager;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

//        serverToClient(5);
        clientToServer(5);
//        both(2);

    }

    public static void serverToClient(int cc) throws IOException, InterruptedException {

        HostManager manager = new HostManager(1, cc);
        manager.addOnInitializationListeners(() -> Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> manager.send(new pck()), 0, 1, TimeUnit.SECONDS));
        manager.start();

        for (int i = 0; i < cc; i++) {
            ConnectionHandle handle = new ConnectionHandle(1);
            int finalI = i;
            handle.addObjectReceivedListener(object -> System.out.println(finalI + " " + ((pck) object).k));
        }

    }

    public static void clientToServer(int cc) throws IOException, InterruptedException {

        HostManager manager = new HostManager(1, cc);
        manager.addObjectReceivedListener((object, sender) -> System.out.println(sender + "\t " + ((pck) object).k));
        manager.start();

        for (int i = 0; i < cc; i++) {
            ConnectionHandle handle = new ConnectionHandle(1);
            int finalI = i;
            if (i % 2 == 0)
                handle.addOnInitializationListeners(() -> Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    pck p = new pck();
                    p.s = finalI;
                    handle.send(p);
                }, 0, 1, TimeUnit.SECONDS));
        }

    }

    public static void both(int cc) throws IOException, InterruptedException {
        HostManager manager = new HostManager(1, cc);
        manager.addObjectReceivedListener((object, sender) -> System.out.println(sender + "\t " + ((pck) object).k));
        manager.addOnInitializationListeners(() -> Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> manager.send(new pck()), 0, 3141, TimeUnit.MILLISECONDS));
        manager.start();

        for (int i = 0; i < cc; i++) {
            ConnectionHandle handle = new ConnectionHandle(1);
            int finalI = i;
            handle.addOnInitializationListeners(() -> Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                pck p = new pck();
                p.s = finalI;
                handle.send(p);
            }, 0, 3, TimeUnit.SECONDS));
            handle.addObjectReceivedListener(object -> System.out.println(finalI + " " + ((pck) object).k));
        }
    }

    public static class pck {
        int s = 0;
        double k = Math.random();
    }

}
