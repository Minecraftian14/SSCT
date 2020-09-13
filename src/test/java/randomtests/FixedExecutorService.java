package randomtests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FixedExecutorService {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);

        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);

        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);

        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);
        executorService.submit(FixedExecutorService::that);

        executorService.shutdown();

    }

    static int i = 0;

    private static void that() {
        i++;
        try {
            final int k = i;
            long duration = (long) (Math.random() * 20);
            System.out.println(k + " Yo");
            TimeUnit.SECONDS.sleep(duration);
            System.out.println(k + " Bo");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
