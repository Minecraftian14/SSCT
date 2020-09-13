package org;

import org.util.adventurers.Discoverer;
import org.util.ConnectionAddress;
import org.util.tokens.SessionStart;
import org.util.data.StreamReader;
import org.util.data.StreamWriter;
import org.util.listeners.ObjectReceivedListener;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionHandle {

    private Discoverer discoverer;

    private Socket socket;
    private StreamReader reader;
    private StreamWriter writer;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private HashSet<ObjectReceivedListener> objectReceivedListeners = new HashSet<>();
    private ArrayList<Runnable> onInitializationListeners = new ArrayList<>();

    private int refreshRate = 10;
    private boolean isInitialised = false;

    ConnectionHandle(Socket _socket) {
        socket = _socket;
        try {
            reader = new StreamReader(_socket.getInputStream());
            writer = new StreamWriter(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        isInitialised = true;
    }

    public ConnectionHandle(long identity) throws IOException, InterruptedException {
        discoverer = new Discoverer(identity, null, this::initiate);
    }

    private void initiate(List<ConnectionAddress> cas) {
        if (cas.size() > 0) {
            try {
                socket = new Socket(cas.get(0).getAddress(), cas.get(0).getPort());
                reader = new StreamReader(socket.getInputStream());
                writer = new StreamWriter(socket.getOutputStream());

                executor.scheduleAtFixedRate(this::readForAll, 0, refreshRate, TimeUnit.MILLISECONDS);
            } catch (ConnectException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO replace SES with new Thread anyplace which involves reading

    private void readForAll() {
        Object object = read();

        if (!isInitialised && object.getClass().getName().equals(SessionStart.class.getName())) {
            isInitialised = true;
            onInitializationListeners.forEach(Runnable::run);

        } else objectReceivedListeners.forEach(objectReceivedListener -> objectReceivedListener.ObjectReceived(object));
    }

    public void send(Object object) {
        if (!isInitialised) {
            System.out.println("Premature Method Calling: Client not yet initialised");
            return;
        }
        writer.write(object);
    }

    Object read() {
        return reader.readObject();
    }

    public Socket getSocket() {
        return socket;
    }

    public void addObjectReceivedListener(ObjectReceivedListener listener) {
        objectReceivedListeners.add(listener);
    }

    public void addOnInitializationListeners(Runnable listener) {
        onInitializationListeners.add(listener);
    }

    public void close() {
        discoverer.close();
        executor.shutdownNow();
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        objectReceivedListeners = null;
    }

//    @Override
//    public boolean equals(Object obj) {
//        return socket.equals(obj);
//    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }
}
