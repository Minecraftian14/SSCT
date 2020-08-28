import org.*;
import org.util.Registry;
import org.util.SendField;
import org.util.SimpleConditions;

import java.io.IOException;

import static java.lang.System.*;

public class main {

    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {

        // P : in both S and C
        Registry.add(Demo.class);
        Registry.add(D2.class);

        // S
        Server server = Server.createServer(4768, SimpleConditions.stopAfterMilliseconds(5000));
        server.addClientJoinListener(client -> {
            out.println("New main.Client joined " /*+ client.getSocket().getLocalAddress()*/);
            D2 toBeSent = new D2();
            toBeSent.demo = new Demo(123, 234L, 23.8, "kahgd", false);
            client.write(toBeSent);
        });
        server.start();

        // C
        Client client = new Client(4768, "");

        // C
        Object toBeReceived = client.read();
        out.println(toBeReceived);

        // S
        Demo d = new Demo();
        server.send(d);

        // C
        Object dry = client.read();
        out.println(dry);

    }

    public static class Demo {
        @SendField
        public int one = 0;
        @SendField
        public long two = 0L;
        @SendField
        public double three = 0.;
        @SendField
        public String four = "Hello";
        @SendField
        public boolean five = true;

        public Demo() {
        }

        public Demo(int one, long two, double three, String four, boolean five) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.four = four;
            this.five = five;
        }

        @Override
        public String toString() {
            return "Demo{" +
                    "one=" + one +
                    ", two=" + two +
                    ", three=" + three +
                    ", four='" + four + '\'' +
                    ", five=" + five +
                    '}';
        }
    }

    public static class D2 {
        @SendField
        int i = 9;
        @SendField
        Demo demo = new Demo(0, 1L, 2.0, "3", true);

        @Override
        public String toString() {
            return "D2{" +
                    "i=" + i +
                    ", demo=" + demo +
                    '}';
        }
    }

}
