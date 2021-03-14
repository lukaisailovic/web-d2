package client;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMain {

    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {

        int playerCount = 20;

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

        for (int i = 0; i < playerCount; i++) {
            scheduledExecutorService.schedule(new ClientThread(),random.nextInt(1001), TimeUnit.MILLISECONDS);
        }

        scheduledExecutorService.shutdown();
    }
}
