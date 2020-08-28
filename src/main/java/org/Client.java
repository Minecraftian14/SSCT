package org;

import org.util.Registry;
import org.util.SendField;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private int recursionDepth;

    Client(Socket _socket) {
        socket = _socket;
        try {
            input = _socket.getInputStream();
            output = _socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recursionDepth = 10;
    }

    public Client(int port, String ip) throws IOException {
        this(new Socket(ip, port));
    }

    public void write(Object object) {
        try {

            Registry.check(object.getClass());

            byte[] bytes = objectToByte(object, 0);
            output.write(ByteBuffer.allocate(4).putInt(bytes.length).array()); // header
            output.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] objectToByte(Object object, int depth) {
        try (ByteArrayOutputStream parcel = new ByteArrayOutputStream()) {

            Class<?> objectClass = object.getClass();
            parcel.write(ByteBuffer.allocate(Integer.BYTES).putInt(objectClass.hashCode()).array());

            for (Field field : objectClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(SendField.class)) {
                    field.setAccessible(true);
                    try {
                        if (field.getType().equals(int.class)) {
                            parcel.write(ByteBuffer.allocate(Integer.BYTES).putInt(field.getInt(object)).array());

                        } else if (field.getType().equals(boolean.class)) {
                            parcel.write(ByteBuffer.allocate(Character.BYTES).putChar(field.getBoolean(object) ? '1' : '0').array());

                        } else if (field.getType().equals(long.class)) {
                            parcel.write(ByteBuffer.allocate(Long.BYTES).putLong(field.getLong(object)).array());

                        } else if (field.getType().equals(double.class)) {
                            parcel.write(ByteBuffer.allocate(Double.BYTES).putDouble(field.getDouble(object)).array());

                        } else if (field.getType().equals(String.class)) {
                            byte[] bytes = ((String) field.get(object)).getBytes();
                            parcel.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
                            parcel.write(bytes);

                        } else if (depth <= recursionDepth) {
                            byte[] bytes = objectToByte(field.get(object), depth + 1);
                            parcel.write(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
                            parcel.write(bytes);
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("Inaccessible Field in " + object);
                    }
                }
            }

            return parcel.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];

    }

    public Object read() {
        try {

            byte[] header = new byte[4];
            if (input.read(header) != 4) throw new RuntimeException();
            int length = ByteBuffer.wrap(header).getInt();

            byte[] bytes = new byte[length];
            if (input.read(bytes) != length) throw new RuntimeException();

            ByteArrayInputStream pack = new ByteArrayInputStream(bytes);

            return bytesToObject(pack, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object bytesToObject(ByteArrayInputStream pack, int depth) {
        try {

            byte[] header = new byte[4];

            if (pack.read(header) != 4) throw new RuntimeException();
            int hash = ByteBuffer.wrap(header).getInt();

            Class<?> objectClass = Registry.get(hash);
            Constructor<?> objectConstructor = objectClass.getConstructors()[0];
            objectConstructor.setAccessible(true);
            Object object = objectConstructor.newInstance();

            for (Field field : objectClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(SendField.class)) {
                    byte[] buffer;

                    if (field.getType().equals(int.class)) {
                        pack.read(buffer = new byte[Integer.BYTES]);
                        field.setInt(object, ByteBuffer.wrap(buffer).getInt());

                    } else if (field.getType().equals(boolean.class)) {
                        pack.read(buffer = new byte[Character.BYTES]);
                        field.setBoolean(object, ByteBuffer.wrap(buffer).getChar() == '1');

                    } else if (field.getType().equals(long.class)) {
                        pack.read(buffer = new byte[Long.BYTES]);
                        field.setLong(object, ByteBuffer.wrap(buffer).getLong());

                    } else if (field.getType().equals(double.class)) {
                        pack.read(buffer = new byte[Double.BYTES]);
                        field.setDouble(object, ByteBuffer.wrap(buffer).getDouble());

                    } else if (field.getType().equals(String.class)) {
                        pack.read(buffer = new byte[Integer.BYTES]);
                        int length = ByteBuffer.wrap(buffer).getInt();
                        StringBuilder builder = new StringBuilder(length);
                        for (int i = 0; i < length; i++) builder.append((char) pack.read());
                        field.set(object, builder.toString());

                    } else if (depth <= recursionDepth) {

                        if (pack.read(buffer = new byte[Integer.BYTES]) != 4) throw new RuntimeException();
                        int length = ByteBuffer.wrap(buffer).getInt();

                        byte[] sb_bytes = new byte[length];
                        if (pack.read(sb_bytes) != length) throw new RuntimeException();

                        ByteArrayInputStream sb_pack = new ByteArrayInputStream(sb_bytes);

                        field.set(object, bytesToObject(sb_pack, depth + 1));
                    }
                }
            }

            return object;

        } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public void setRecursionDepth(int recursionDepth) {
        this.recursionDepth = recursionDepth;
    }
}
