package nz.kabanov.learning.concurrency.idioms;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PutTakeTest {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // medium-quality RNG, suitable for testing
    static int xorShift(int x) {
        x ^= (x << 6);
        x ^= (x >>> 21);
        x ^= (x << 7);
        return x;
    }

    private final AtomicInteger putSum = new AtomicInteger();
    private final AtomicInteger takeSum = new AtomicInteger();

    private final BlockingQueue<Integer> queue;

    private final BarrierTimer timer;
    private final CyclicBarrier barrier;
    private final int nTrials, nPairs;

    public static void main(String[] args) {
        new PutTakeTest(10, 400_000, 10).test();
        executor.shutdown();
    }

    public PutTakeTest(int capacity, int nTrials, int nPairs) {

//        queue = new LinkedBlockingQueue<>(capacity); // 1952ms
        queue = new ArrayBlockingQueue<>(capacity); // 12326ms

        timer = new BarrierTimer();
        barrier = new CyclicBarrier(nPairs * 2 + 1, timer); // n * put + n * take + starter thread

        this.nTrials = nTrials;
        this.nPairs = nPairs;
    }

    public void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                executor.submit(new Producer());
                executor.submit(new Consumer());
            }
            barrier.await(); // wait for all to be ready
            barrier.await(); // wait for all to terminate
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        if (putSum.get() != takeSum.get()) {
            throw new AssertionError();
        }
        System.out.printf("Finished in %sms%n", timer.getTime());
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int seed = this.hashCode() ^ (int) System.nanoTime();
                int sum = 0; // thread-local until the very end
                barrier.await(); // wait for others to be ready
                for (int i = 0; i < nTrials; i++) {
                    queue.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await(); // release others & main thread
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = 0; i < nTrials; i++) {
                    sum += queue.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class BarrierTimer implements Runnable {

        private boolean started;
        private long startTime, endTime;

        @Override
        public synchronized void run() {
            long t = System.currentTimeMillis();
            if (started) {
                endTime = t;
            } else {
                started = true;
                startTime = t;
            }
        }

        public synchronized long getTime() {
            return endTime - startTime;
        }
    }
}
