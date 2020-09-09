package randomtests;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTests {

    public static void main(String[] args) throws InterruptedException {

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    }

    public static void act1() {
        System.out.print('a');
    }

    public static void act2() {
        System.out.print('b');
    }

}
