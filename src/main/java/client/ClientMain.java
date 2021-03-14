package client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ClientMain {

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

        for (int i = 0; i < 10; i++) {
            scheduledExecutorService.submit(new ClientThread());
        }

        scheduledExecutorService.shutdown();
    }
}
