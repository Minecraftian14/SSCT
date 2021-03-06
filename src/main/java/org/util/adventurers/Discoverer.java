package org.util.adventurers;

import org.SimpleConditions;
import org.util.Condition;
import org.util.ConnectionAddress;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Discoverer {

    private final DatagramSocket socket;

    private ScheduledExecutorService executor;
    private Condition searchTill;
    private final ArrayList<ConnectionAddress> searchResults = new ArrayList<>();
    private final Consumer<List<ConnectionAddress>> searchFinishEvent;

    private final byte[] identity_packet;
    private final int port = 49152;
    private final int scanSize = 10;

    private final DatagramPacket receivedPacket = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);

    public Discoverer(long identity) throws IOException {
        this(identity, null);
    }

    public Discoverer(long identity, Consumer<List<ConnectionAddress>> consumer) throws IOException {
        this(identity, SimpleConditions.stopAfterSeconds(10), consumer);
    }

    public Discoverer(long identity, Condition _searchTill, Consumer<List<ConnectionAddress>> consumer) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        searchTill = _searchTill == null ? (() -> searchResults.size() < 1) : _searchTill;
        searchFinishEvent = consumer;

        identity_packet = ByteBuffer.allocate(Long.BYTES).putLong(identity).array();

        sendToDefaultAddress();
        sendToExistingAddresses();

        prepareToReceive();
    }

    private void sendToDefaultAddress() throws IOException {
        send(InetAddress.getByName("255.255.255.255"));
    }

    private void sendToExistingAddresses() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = address.getBroadcast();
                if (broadcast != null) send(broadcast);
            }
        }
    }

    private void send(InetAddress address) throws IOException {
        for (int p = port; p < port + scanSize; p++)
            socket.send(new DatagramPacket(identity_packet, identity_packet.length, address, p));
    }

    private void prepareToReceive() {
        new Thread(this::receive).start();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!searchTill.get()) {
                socket.close();
                searchFinishEvent.accept(searchResults);
                executor.shutdownNow();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void receive() {
        while (!socket.isClosed()) {
            try {
                socket.receive(receivedPacket);
                String message = new String(receivedPacket.getData());

                searchResults.add(new ConnectionAddress(receivedPacket.getAddress(), Integer.parseInt(message.trim())));
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        executor.shutdownNow();
        if (!socket.isClosed())
            socket.close();
    }

}
