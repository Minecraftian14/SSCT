package org;

import org.util.adventurers.Discoverer;
import org.util.ConnectionAddress;
import org.util.SessionStart;
import org.util.data.StreamReader;
import org.util.data.StreamWriter;
import org.util.listeners.ObjectReceivedListener;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionHandle {

    private Discoverer discoverer;

    private Socket socket;
    private StreamReader reader;
    private StreamWriter writer;

    private CountDownLatch latch;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private HashSet<ObjectReceivedListener> objectReceivedListeners = new HashSet<>();

    private int recursionDepth = 10;

    ConnectionHandle(Socket _socket) {
        socket = _socket;
        try {
            reader = new StreamReader(_socket.getInputStream());
            writer = new StreamWriter(_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConnectionHandle(long identity) throws IOException, InterruptedException {
        latch = new CountDownLatch(2);

        discoverer = new Discoverer(identity, this::initiate);

        latch.await();
    }

    private void initiate(List<ConnectionAddress> cas) {
        if (cas.size() > 0) {
            try {
                socket = new Socket(cas.get(0).getAddress(), cas.get(0).getPort());
                reader = new StreamReader(socket.getInputStream());
                writer = new StreamWriter(socket.getOutputStream());

                executor.scheduleAtFixedRate(this::readForAll, 0, 10, TimeUnit.MILLISECONDS);

                latch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readForAll() {
        Object object = read();

        if (object.getClass().getName().equals(SessionStart.class.getName())) latch.countDown();
        else objectReceivedListeners.forEach(objectReceivedListener -> objectReceivedListener.ObjectReceived(object));
    }

    public void send(Object object) {
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

    @Override
    public boolean equals(Object obj) {
        return socket.equals(obj);
    }
}
