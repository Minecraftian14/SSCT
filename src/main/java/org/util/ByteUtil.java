package org.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtil {

    public static byte[] forBoolean(boolean b) {
        return ByteBuffer.allocate(Character.BYTES).putChar(b ? '1' : '0').array();
    }

    public static boolean asBoolean(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar() == '1';
    }

    public static byte[] forByte(byte b) {
        return ByteBuffer.allocate(Byte.BYTES).put(b).array();
    }

    public static byte asByte(byte[] bytes) {
        return ByteBuffer.wrap(bytes).get();
    }

    public static byte[] forChar(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }

    public static char asChar(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar();
    }

    public static byte[] forShort(short s) {
        return ByteBuffer.allocate(Short.BYTES).putShort(s).array();
    }

    public static short asShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static byte[] forInt(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    public static int asInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] forLong(long l) {
        return ByteBuffer.allocate(Long.BYTES).putLong(l).array();
    }

    public static long asLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] forFloat(float f) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(f).array();
    }

    public static float asFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[] forDouble(double d) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(d).array();
    }

    public static double asDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] forString(String s) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES + s.length());
        buf.putInt(s.length());
        buf.put(s.getBytes());
        return buf.array();
    }

    public static String asString(byte[] bytes) {
        int length = asInt(Arrays.copyOfRange(bytes, 0, Integer.BYTES));
        StringBuilder builder = new StringBuilder(length);
        for (int i = Integer.BYTES; i < length + Integer.BYTES; i++) builder.append((char) bytes[i]);
        return builder.toString();
    }

    public static String asString(byte[] bytes, int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) builder.append((char) bytes[i]);
        return builder.toString();
    }

    public static byte[] forShortString(String s) {
        int l = Math.min(Short.MAX_VALUE, s.length());
        ByteBuffer buf = ByteBuffer.allocate(Short.BYTES + l);
        buf.putShort((short) l);
        buf.put(Arrays.copyOfRange(s.getBytes(), 0, l));
        return buf.array();
    }

    public static String asShortString(byte[] bytes) {
        int length = asShort(Arrays.copyOfRange(bytes, 0, Short.BYTES));
        StringBuilder builder = new StringBuilder(length);
        for (int i = Short.BYTES; i < length + Short.BYTES; i++) builder.append((char) bytes[i]);
        return builder.toString();
    }

    public static byte[] forByteString(String s) {
        int l = Math.min(s.length(), 255);
        ByteBuffer buf = ByteBuffer.allocate(Byte.BYTES + l);
        buf.put((byte) l);
        buf.put(Arrays.copyOfRange(s.getBytes(), 0, l));
        return buf.array();
    }

    public static String asByteString(byte[] bytes) {
        int length = asByte(Arrays.copyOfRange(bytes, 0, Byte.BYTES));
        if (length < 0) length += 256;
        StringBuilder builder = new StringBuilder(length);
        for (int i = Byte.BYTES; i < length + Byte.BYTES; i++) builder.append((char) bytes[i]);
        return builder.toString();
    }
}
