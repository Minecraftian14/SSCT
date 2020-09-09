package ServerDiscovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSide {

    public ServerSide() {

        try {
            DatagramSocket socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        new ServerSide();
    }

}
