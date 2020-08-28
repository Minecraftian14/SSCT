package org;

import org.util.Condition;
import org.util.listeners.ClientJoinListener;
import org.util.listeners.ObjectReceivedListener;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Server {

    Condition joinCondition;
    ServerSocket socket;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    HashSet<Client> clients = new HashSet<>();
    HashSet<ClientJoinListener> clientJoinEventListeners = new HashSet<>();
    HashSet<ObjectReceivedListener> objectReceivedListeners = new HashSet<>();

    public static Server createServer(int port, Condition condition) throws IOException {
        Server socket = new Server();
        socket.socket = new ServerSocket(port);
        socket.joinCondition = condition;
        return socket;
    }

    public void start() {
        new Thread(() -> {
            Thread join = new Thread(() -> {
                while (!socket.isClosed()) {
                    try {
                        Client client = new Client(socket.accept());
                        clientJoinEventListeners.forEach(clientJoinListener -> clientJoinListener.clientJoined(client));
                        clients.add(client);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            join.start();

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> {
                if (!joinCondition.get()) {
                    try {
                        socket.close();
                        service.shutdownNow();
                        clientJoinEventListeners.clear();
                        clientJoinEventListeners = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);

        }).start();
        executor.scheduleAtFixedRate(this::read, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void send(Object object) {
        clients.forEach(client -> client.send(object));
    }

    public void send(Predicate<Client> choose, Object object) {
        clients.forEach(client -> {
            if (choose.test(client)) client.send(object);
        });
    }

    private void read() {
        clients.forEach(client -> {
            Object object = client.read();
            objectReceivedListeners.forEach(objectReceivedListener -> objectReceivedListener.ObjectReceived(object));
        });
    }

    public void addClientJoinListener(ClientJoinListener listener) {
        clientJoinEventListeners.add(listener);
    }

    public void addObjectReceivedListener(ObjectReceivedListener listener) {
        objectReceivedListeners.add(listener);
    }

    public void close() {
        executor.shutdownNow();
        clients.forEach(Client::close);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients = null;
        objectReceivedListeners = null;
    }
}
