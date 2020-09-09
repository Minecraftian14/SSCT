package org.util;

import java.net.InetAddress;

public class ConnectionAddress {
    private InetAddress address;
    private int port;

    public ConnectionAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ConnectionAddress{" +
                "address=" + address +
                ", port=" + port +
                '}';
    }
}
