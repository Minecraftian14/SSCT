package org;

import org.util.adventurers.Broadcaster;
import org.util.Condition;
import org.util.SessionStart;
import org.util.listeners.ClientJoinListener;
import org.util.listeners.ObjectReceivedListener;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class HostManager {

    private static int port = 50152;
    private static int scanSize = 10;

    private ServerSocket socket;
    private HashSet<ConnectionHandle> connections = new HashSet<>();

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Condition allowClientsUntil;

    private HashSet<ClientJoinListener> clientJoinEventListeners = new HashSet<>();
    private HashSet<ObjectReceivedListener> objectReceivedListeners = new HashSet<>();
    private ArrayList<Runnable> onInitializationListeners = new ArrayList<>();

    public HostManager(long identity) throws IOException {
        this(identity, SimpleConditions.stopAfterSeconds(30));
    }

    public HostManager(long identity, Condition _allowClientsUntil) throws IOException {
        socket = establish(port);
        allowClientsUntil = _allowClientsUntil;
        Broadcaster broadcaster = new Broadcaster(identity, "" + port);
        onInitializationListeners.add(0, () -> {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this::read, 0, 10, TimeUnit.MILLISECONDS);
            send(new SessionStart());
        });
    }

    private static ServerSocket establish(int p) throws IOException {
        ServerSocket socks = null;
        try {
            socks = new ServerSocket(p);
            port = p;
        } catch (BindException e) {
            if (p == port + scanSize)
                throw new IOException("Couldn't establish a server from " + port + " to " + p);
            return establish(p + 1);
        }
        return socks;
    }

    public void start() {
        new Thread(this::accept).start();
        executor.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.SECONDS);
    }

    private void accept() {
        while (!socket.isClosed()) {
            try {
                ConnectionHandle connection = new ConnectionHandle(socket.accept());
                clientJoinEventListeners.forEach(clientJoinListener -> clientJoinListener.clientJoined(connection));
                connections.add(connection);
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (allowClientsUntil.get()) return;
        try {
            socket.close();
            executor.shutdownNow();
            clientJoinEventListeners.clear();
            clientJoinEventListeners = null;
            onInitializationListeners.forEach(Runnable::run);
            onInitializationListeners.clear();
            onInitializationListeners = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object object) {
        connections.forEach(connection -> connection.send(object));
    }

    public void send(Object object, Predicate<ConnectionHandle> choose) {
        connections.forEach(client -> {
            if (choose.test(client)) client.send(object);
        });
    }

    public void sendToOnly(Object object, ConnectionHandle... whitelist) {
        for (ConnectionHandle connection : connections)
            for (ConnectionHandle wlitem : whitelist)
                if (wlitem.equals(connection))
                    connection.send(object);
    }

    public void sendToAllExcept(Object object, ConnectionHandle... blacklist) {
        for (ConnectionHandle connection : connections)
            for (ConnectionHandle wlitem : blacklist)
                if (!wlitem.equals(connection))
                    connection.send(object);
    }

    private void read() {
        connections.forEach(this::read);
    }

    private void read(ConnectionHandle connectionHandle) {
        Object object = connectionHandle.read();
        objectReceivedListeners.forEach(objectReceivedListener -> objectReceivedListener.ObjectReceived(object));
    }

    public void addClientJoinListener(ClientJoinListener listener) {
        clientJoinEventListeners.add(listener);
    }

    public void addObjectReceivedListener(ObjectReceivedListener listener) {
        objectReceivedListeners.add(listener);
    }

    public void addOnInitializationListeners(Runnable listener) {
        onInitializationListeners.add(listener);
    }

    public HashSet<ConnectionHandle> getConnections() {
        return connections;
    }

    public void close() {
        executor.shutdownNow();
        connections.forEach(ConnectionHandle::close);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connections = null;
        objectReceivedListeners = null;
    }

}