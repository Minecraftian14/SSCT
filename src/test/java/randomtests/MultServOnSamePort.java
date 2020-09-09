package randomtests;

import java.io.IOException;
import java.net.ServerSocket;

public class MultServOnSamePort {

    public static void main(String[] args) throws IOException {

        new ServerSocket(1);
        new ServerSocket(1);

    }

}
