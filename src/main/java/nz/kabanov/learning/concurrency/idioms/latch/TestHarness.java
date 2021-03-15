package nz.kabanov.learning.concurrency.idioms.latch;

import java.util.concurrent.CountDownLatch;

@SuppressWarnings("unused")
public class TestHarness {

    public long timeTask(int nThreads, Runnable task) throws InterruptedException {

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                try {
                    startGate.await();
                    try {
                        task.run();
                    } finally {
                        endGate.countDown();
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
        }

        long start = System.currentTimeMillis();
        startGate.countDown();
        endGate.await();

        return System.currentTimeMillis() - start;
    }
}
