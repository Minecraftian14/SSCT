package randomtests;

import org.util.data.StreamWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

public class DataStructureSerializeCost {

    public static void main(String[] args) throws IOException {

        TestList();
        TestSet();
        

        TestStreamWriter();

    }


    private static void TestList() throws IOException {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) list.add(i);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(list);

        System.out.println(stream.toByteArray().length);
    }



    private static void TestSet() throws IOException {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < 100; i++) set.add(i);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(set);

        System.out.println(stream.toByteArray().length);
    }


    private static void TestStreamWriter() {
        StreamWriter writer = new StreamWriter();
        for (int i = 0; i < 100; i++) writer.write(i);

        System.out.println(writer.toByteArray().length);
    }
}
