package org;

import org.util.ByteUtil;
import org.util.annotations.ByteString;
import org.util.annotations.SendEveryField;
import org.util.annotations.SendField;
import org.util.annotations.ShortString;
import org.util.listeners.ObjectReceivedListener;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    HashSet<ObjectReceivedListener> objectReceivedListeners = new HashSet<>();

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
        executor.scheduleAtFixedRate(this::readForAll, 0, 10, TimeUnit.MILLISECONDS);
    }

    private void readForAll() {
        Object object = read();
        objectReceivedListeners.forEach(objectReceivedListener -> objectReceivedListener.ObjectReceived(object));
    }

    public void send(Object object) {
        try {
            byte[] data = objectToByte(object, 0);
            output.write(ByteUtil.forInt(data.length));
            output.write(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] objectToByte(Object object, int depth) {
        try (ByteArrayOutputStream parcel = new ByteArrayOutputStream()) {

            Class<?> objectClass = Class.forName(object.getClass().getName());
            parcel.write(ByteUtil.forString(objectClass.getName()));

            for (Field field : objectClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(SendField.class) || objectClass.isAnnotationPresent(SendEveryField.class)) {
                    field.setAccessible(true);
                    if (field.getType().equals(int.class)) {
                        parcel.write(ByteUtil.forInt(field.getInt(object)));

                    } else if (field.getType().equals(boolean.class)) {
                        parcel.write(ByteUtil.forBoolean(field.getBoolean(object)));

                    } else if (field.getType().equals(long.class)) {
                        parcel.write(ByteUtil.forLong(field.getLong(object)));

                    } else if (field.getType().equals(double.class)) {
                        parcel.write(ByteUtil.forDouble(field.getDouble(object)));

                    } else if (field.getType().equals(String.class)) {
                        parcel.write(ByteUtil.forString((String) field.get(object)));

                    } else if (depth <= recursionDepth) {
                        byte[] sub_data = objectToByte(field.get(object), depth + 1);
                        parcel.write(ByteUtil.forInt(sub_data.length));
                        parcel.write(sub_data);
                    }

                } else if (field.isAnnotationPresent(ShortString.class)) {
                    field.setAccessible(true);
                    String s = (String) field.get(object);
                    if (s.length() > Short.MAX_VALUE) s = s.substring(0, Short.MAX_VALUE);
                    parcel.write(ByteUtil.forShortString(s));

                } else if (field.isAnnotationPresent(ByteString.class)) {
                    field.setAccessible(true);
                    String s = (String) field.get(object);
                    if (s.length() > 255) s = s.substring(0, 255);
                    parcel.write(ByteUtil.forByteString(s));
                }
            }

            return parcel.toByteArray();
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    Object read() {
        try {

            byte[] buffer = new byte[4]; // to read the header, ie, an int telling how big is incoming data.
            if (input.read(buffer) != 4) throw new RuntimeException("Header mismatch " + Arrays.toString(buffer));
            int length = ByteUtil.asInt(buffer); // and so is this int as the result

            byte[] bytes = new byte[length]; // another buffer for data
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

            byte[] buffer = new byte[4]; // a buffer to read the length of an incoming String (the class name)
            if (pack.read(buffer) != 4) throw new RuntimeException();
            int length = ByteUtil.asInt(buffer);

            byte[] data = new byte[length];
            if (pack.read(data) != length) throw new RuntimeException();
            String classname = ByteUtil.asString(data, length);

            Class<?> objectClass = Class.forName(classname);
            Constructor<?> objectConstructor = objectClass.getConstructors()[0];
            objectConstructor.setAccessible(true);
            Object object = objectConstructor.newInstance();

            for (Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(ShortString.class)) {
                    pack.read(buffer = new byte[Short.BYTES]);
                    length = ByteUtil.asShort(buffer);
                    StringBuilder builder = new StringBuilder(length);
                    for (int i = 0; i < length ; i++) builder.append((char) pack.read());
                    field.set(object, builder.toString());

                } else if (field.isAnnotationPresent(ByteString.class)) {
                    pack.read(buffer = new byte[Byte.BYTES]);
                    length = ByteUtil.asShort(buffer);
                    if (length < 0) length += 256;
                    StringBuilder builder = new StringBuilder(length);
                    for (int i = 0; i < length ; i++) builder.append((char) pack.read());
                    field.set(object, builder.toString());

                } else if (field.isAnnotationPresent(SendField.class) || field.isAnnotationPresent(SendEveryField.class)) {

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
                        length = ByteBuffer.wrap(buffer).getInt();
                        pack.read(buffer = new byte[length]);
                        field.set(object, ByteUtil.asString(buffer, length));

                    } else if (depth <= recursionDepth) {

                        if (pack.read(buffer = new byte[Integer.BYTES]) != 4) throw new RuntimeException();
                        length = ByteUtil.asInt(buffer);

                        byte[] sb_bytes = new byte[length];
                        if (pack.read(sb_bytes) != length) throw new RuntimeException();

                        ByteArrayInputStream sb_pack = new ByteArrayInputStream(sb_bytes);

                        field.set(object, bytesToObject(sb_pack, depth + 1));
                    }
                }
            }

            return object;

        } catch (IOException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
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

    public void addObjectReceivedListener(ObjectReceivedListener listener) {
        objectReceivedListeners.add(listener);
    }

    public void close() {
        executor.shutdownNow();
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        objectReceivedListeners = null;
    }
}
