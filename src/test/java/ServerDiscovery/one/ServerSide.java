package ServerDiscovery.one;

import org.SimpleConditions;
import org.util.Condition;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSide {

    private DatagramSocket socket;

    private Condition searchTill;

    private byte[] identity_packet;
    private final int port = 49152;
    private final int scanSize = 10;

    private DatagramPacket receivedPacket = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);

    public ServerSide(long identity) throws IOException {
        this(identity, SimpleConditions.stopAfterSeconds(60));
    }

    public ServerSide(long identity, Condition _searchTill) throws IOException {
        socket = establish(port);
        socket.setBroadcast(true);
        searchTill = _searchTill;

        identity_packet = ByteBuffer.allocate(Long.BYTES).putLong(identity).array();

        startBroadcast();
    }

    private DatagramSocket establish(int p) throws IOException {
        DatagramSocket socks = null;
        try {
            socks = new DatagramSocket(p, InetAddress.getByName("0.0.0.0"));
        } catch (BindException e) {
            if (p == port + scanSize)
                throw new IOException("Couldn't establish a server from " + port + " to " + p);
            return establish(p + 1);
        }
        return socks;
    }

    private void startBroadcast() {
        new Thread(() -> {
            while (!socket.isClosed()) receive();
        }).start();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            if (!searchTill.get()) {
                socket.close();
//                searchFinishEvent.accept(searchResults);
                service.shutdownNow();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void receive() {
        try {
            socket.receive(receivedPacket);

            System.out.println("Packet received from: " + receivedPacket.getAddress().getHostAddress() + receivedPacket.getPort());
            System.out.println("Packet data: " + ByteBuffer.wrap(receivedPacket.getData()).getLong());

            if (Arrays.equals(receivedPacket.getData(), identity_packet))
                send(receivedPacket.getAddress(), receivedPacket.getPort());
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(InetAddress address, int port) throws IOException {
        socket.send(new DatagramPacket(identity_packet, identity_packet.length, address, port));
        System.out.println("Packet sent to " + address.getHostAddress() + " at " + port);
    }

}
