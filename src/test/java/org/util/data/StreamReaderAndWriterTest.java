package org.util.data;

import one.PackageOne;
import one.PackageTwo;
import randomtests.PackageThree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class StreamReaderAndWriterTest {

    public static void main(String[] args) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamWriter writer = new StreamWriter(outputStream);

        writer.write(true);
        writer.write(Byte.MAX_VALUE);
        writer.write(Character.MAX_VALUE);
        writer.write(Short.MAX_VALUE);
        writer.write(Integer.MAX_VALUE);
        writer.write(Long.MAX_VALUE);
        writer.write(Float.MAX_VALUE);
        writer.write(Double.MAX_VALUE);
        writer.writeString("abcdefghijklmnopqrstuvwxyz");
        writer.writeShortString("abcdefghijklmnopqrstuvwxyz");
        writer.writeByteString("abcdefghijklmnopqrstuvwxyz");
        writer.write(new PackageOne(34, 34, 26, "345", true));
        writer.write(new PackageTwo());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        StreamReader reader = new StreamReader(inputStream);

        System.out.println(reader.readBoolean());
        System.out.println(reader.readByte());
        System.out.println(reader.readChar());
        System.out.println(reader.readShort());
        System.out.println(reader.readInt());
        System.out.println(reader.readLong());
        System.out.println(reader.readFloat());
        System.out.println(reader.readDouble());
        System.out.println(reader.readString());
        System.out.println(reader.readShortString());
        System.out.println(reader.readByteString());
        System.out.println(reader.readObject());
        System.out.println(reader.readObject());
    }

}