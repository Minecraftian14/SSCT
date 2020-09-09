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

    private DatagramSocket socket;

    private Condition searchTill;
    private ArrayList<ConnectionAddress> searchResults = new ArrayList<>();
    private Consumer<List<ConnectionAddress>> searchFinishEvent;

    private byte[] identity_packet;
    private final int port = 49152;
    private final int scanSize = 10;

    private DatagramPacket receivedPacket = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);

    public Discoverer(long identity) throws IOException {
        this(identity, null);
    }

    public Discoverer(long identity, Consumer<List<ConnectionAddress>> consumer) throws IOException {
        this(identity, SimpleConditions.stopAfterSeconds(10), consumer);
    }

    public Discoverer(long identity, Condition _searchTill, Consumer<List<ConnectionAddress>> consumer) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        searchTill = _searchTill;
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
        for (int p = port; p < port + scanSize; p++) {
            socket.send(new DatagramPacket(identity_packet, identity_packet.length, address, p));
//            System.out.println("Packet sent to " + address.getHostAddress() + " at " + p);
        }
    }

    private void prepareToReceive() throws IOException {
        new Thread(() -> {
            while (!socket.isClosed()) receive();
        }).start();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            if (!searchTill.get()) {
                socket.close();
                searchFinishEvent.accept(searchResults);
                service.shutdownNow();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void receive() {
        try {
            socket.receive(receivedPacket);
            String message = new String(receivedPacket.getData());
//            System.out.println("Response received " + receivedPacket.getAddress().getHostAddress() + " at " + receivedPacket.getPort() + " saying " + message);
            searchResults.add(new ConnectionAddress(receivedPacket.getAddress(), Integer.parseInt(message.trim())));
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
