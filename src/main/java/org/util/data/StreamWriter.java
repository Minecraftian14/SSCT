package org.util.data;

import org.util.FieldIterator;
import org.util.Try;
import org.util.annotations.ByteString;
import org.util.annotations.SendEveryField;
import org.util.annotations.SendField;
import org.util.annotations.ShortString;
import org.util.data.adaptors.CloserAdaptor;
import org.util.data.adaptors.WriterAdaptor;
import org.util.data.adaptors.toArrayAdaptor;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class StreamWriter implements Closeable {

    private WriterAdaptor writer;
    private toArrayAdaptor toArray;
    private CloserAdaptor closer;
    private byte[] buffer;
    private Try trier;

    public StreamWriter() {
        this(new ByteArrayOutputStream());
    }

    public StreamWriter(ByteArrayOutputStream stream) {
        this(stream::write, stream::toByteArray, stream::close);
    }

    public StreamWriter(OutputStream stream) {
        this(stream::write, () -> new byte[0], stream::close);
    }

    private StreamWriter(WriterAdaptor writer, toArrayAdaptor adaptor, CloserAdaptor closerAdaptor) {
        this.writer = writer;
        this.toArray = adaptor;
        this.closer = closerAdaptor;
        trier = new Try(Throwable::printStackTrace);
    }

    public void write(boolean val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Character.BYTES).putChar(val ? '1' : '0').array()));
    }

    public void write(byte val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Byte.BYTES).put(val).array()));
    }

    public void write(char val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Character.BYTES).putChar(val).array()));
    }

    public void write(short val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Short.BYTES).putShort(val).array()));
    }

    public void write(int val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Integer.BYTES).putInt(val).array()));
    }

    public void write(long val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Long.BYTES).putLong(val).array()));
    }

    public void write(float val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Float.BYTES).putFloat(val).array()));
    }

    public void write(double val) {
        trier.Catch(() -> writer.write(ByteBuffer.allocate(Double.BYTES).putDouble(val).array()));
    }

    public void writeString(String val) {
        write(val.length());
        trier.Catch(() -> writer.write(val.getBytes()));
    }

    public void writeShortString(String val) {
        String data = val.length() > Short.MAX_VALUE ? val.substring(0, Short.MAX_VALUE + 1) : val;
        write((short) Math.min(val.length(), Short.MAX_VALUE));
        trier.Catch(() -> writer.write(data.getBytes()));
    }

    public void writeByteString(String val) {
        String data = val.length() > 255 ? val.substring(0, 256) : val;
        write((byte) data.length());
        trier.Catch(() -> writer.write(data.getBytes()));
    }

    public void write(Object object) {

        StreamWriter streamWriter = new StreamWriter();

        trier.Catch(() -> streamWriter.write(object, 10));

        byte[] data = streamWriter.toByteArray();
        write(data.length);
        trier.Catch(() -> writer.write(data));

    }

    private void write(Object object, int depth) throws IllegalAccessException {

        Class<?> cls = object.getClass();
        writeString(cls.getName());

        FieldIterator.foreach(cls, field -> trier.Catch(() -> parseField(object, field, depth)));
    }

    private void parseField(Object object, Field field, int depth) throws IllegalAccessException {

        field.setAccessible(true);

        if (field.isAnnotationPresent(ShortString.class))
            writeShortString((String) field.get(object));

        else if (field.isAnnotationPresent(ByteString.class))
            writeByteString((String) field.get(object));

        else if (field.isAnnotationPresent(SendField.class) || object.getClass().isAnnotationPresent(SendEveryField.class)) {

            if (field.getType().equals(boolean.class))
                write(field.getBoolean(object));

            else if (field.getType().equals(byte.class))
                write(field.getByte(object));

            else if (field.getType().equals(char.class))
                write(field.getChar(object));

            else if (field.getType().equals(short.class))
                write(field.getShort(object));

            else if (field.getType().equals(int.class))
                write(field.getInt(object));

            else if (field.getType().equals(long.class))
                write(field.getLong(object));

            else if (field.getType().equals(float.class))
                write(field.getFloat(object));

            else if (field.getType().equals(double.class))
                write(field.getDouble(object));

            else if (field.getType().equals(String.class))
                writeString((String) field.get(object));

            else if (depth >= 0) {

                StreamWriter streamWriter = new StreamWriter();

                trier.Catch(() -> streamWriter.write(field.get(object), depth - 1));

                byte[] data = streamWriter.toByteArray();
                write(data.length);
                trier.Catch(() -> writer.write(data));

            }
        }
    }

    public byte[] toByteArray() {
        return toArray.toByteArray();
    }

    @Override
    public void close() throws IOException {
        closer.close();
    }
}
















