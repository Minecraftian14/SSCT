package three;

import org.util.data.StreamWriter;

import java.io.IOException;
import java.io.StringWriter;

public class main {

    public static void main(String[] args) throws IOException, InterruptedException {

        StreamWriter w = new StreamWriter();
        w.write( new StringMessage("Hello", 89, true));
        System.out.println(w.toByteArray().length);

        System.out.println("Starting");
        new ServerActivity();
        System.out.println("Server Done");
        new ClientActivity(10);
        System.out.println("Client Done");

    }

}
