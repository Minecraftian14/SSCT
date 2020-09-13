package org.util.data;

import org.util.FieldIterator;
import org.util.Try;
import org.util.annotations.ByteString;
import org.util.annotations.SendEveryField;
import org.util.annotations.SendField;
import org.util.annotations.ShortString;
import org.util.data.adaptors.CloserAdaptor;
import org.util.data.adaptors.ReaderAdaptor;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class StreamReader implements Closeable {

    private ReaderAdaptor reader;
    private CloserAdaptor closer;
    private byte[] buffer;
    private Try trier;

    public StreamReader(byte[] data) {
        this(new ByteArrayInputStream(data));
    }

    public StreamReader(InputStream stream) {
        this(stream::read, stream::close);
    }

    private StreamReader(ReaderAdaptor reader, CloserAdaptor adaptor) {
        this.reader = reader;
        this.closer = adaptor;
        trier = new Try(Throwable::printStackTrace);
    }

    public boolean readBoolean() {
        trier.Catch(() -> reader.read(buffer = new byte[Character.BYTES]));
        return ByteBuffer.wrap(buffer).getChar() == '1';
    }

    public byte readByte() {
        trier.Catch(() -> reader.read(buffer = new byte[Byte.BYTES]));
        return ByteBuffer.wrap(buffer).get();
    }

    public char readChar() {
        trier.Catch(() -> reader.read(buffer = new byte[Character.BYTES]));
        return ByteBuffer.wrap(buffer).getChar();
    }

    public short readShort() {
        trier.Catch(() -> reader.read(buffer = new byte[Short.BYTES]));
        return ByteBuffer.wrap(buffer).getShort();
    }

    public int readInt() {
        trier.Catch(() -> reader.read(buffer = new byte[Integer.BYTES]));
        return ByteBuffer.wrap(buffer).getInt();
    }

    public long readLong() {
        trier.Catch(() -> reader.read(buffer = new byte[Long.BYTES]));
        return ByteBuffer.wrap(buffer).getLong();
    }

    public float readFloat() {
        trier.Catch(() -> reader.read(buffer = new byte[Float.BYTES]));
        return ByteBuffer.wrap(buffer).getFloat();
    }

    public double readDouble() {
        trier.Catch(() -> reader.read(buffer = new byte[Double.BYTES]));
        return ByteBuffer.wrap(buffer).getDouble();
    }

    public String readString() {
        int length = readInt();
        trier.Catch(() -> reader.read(buffer = new byte[length]));
        return new String(buffer);
    }

    public String readShortString() {
        int length = readShort();
        trier.Catch(() -> reader.read(buffer = new byte[length]));
        return new String(buffer);
    }

    public String readByteString() {
        int length = readByte();
        if (length < 0) length += 256;
        int finalLength = length;
        trier.Catch(() -> reader.read(buffer = new byte[finalLength]));
        return new String(buffer);
    }

    public Object readObject() {
        trier.Catch(() -> reader.read(buffer = new byte[readInt()]));
        StreamReader streamReader = new StreamReader(buffer);

        return trier.CatchAndReturn(() -> streamReader.readObject(10), null);
    }

    private Object readObject(int depth) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {

        Class<?> cls = Class.forName(readString());
        Object object = Arrays.stream(cls.getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findFirst().orElseThrow(NoSuchMethodException::new).newInstance();

        FieldIterator.foreach(cls, field -> trier.Catch(() -> parseField(object, field, depth)));

        return object;
    }

    private void parseField(Object object, Field field, int depth) throws IllegalAccessException {

        if (field.isAnnotationPresent(ShortString.class))
            field.set(object, readShortString());

        else if (field.isAnnotationPresent(ByteString.class))
            field.set(object, readByteString());

        else if (field.isAnnotationPresent(SendField.class) || object.getClass().isAnnotationPresent(SendEveryField.class)) {

            if (field.getType().equals(boolean.class))
                field.setBoolean(object, readBoolean());

            else if (field.getType().equals(byte.class))
                field.setByte(object, readByte());

            else if (field.getType().equals(char.class))
                field.setChar(object, readChar());

            else if (field.getType().equals(short.class))
                field.setShort(object, readShort());

            else if (field.getType().equals(int.class))
                field.setInt(object, readInt());

            else if (field.getType().equals(long.class))
                field.setLong(object, readLong());

            else if (field.getType().equals(float.class))
                field.setFloat(object, readFloat());

            else if (field.getType().equals(double.class))
                field.setDouble(object, readDouble());

            else if (field.getType().equals(String.class))
                field.set(object, readString());

            else if (depth > 0) {
                trier.Catch(() -> reader.read(buffer = new byte[readInt()]));
                StreamReader streamReader = new StreamReader(buffer);

                trier.Catch(() -> field.set(object, streamReader.readObject(depth - 1)));
            }

        }

    }

    @Override
    public void close() throws IOException {
        closer.close();
    }

}
