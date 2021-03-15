package nz.kabanov.learning.concurrency.idioms;

import java.util.concurrent.*;

@SuppressWarnings("unused")
public class TimedRun {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void timedRun(Runnable task, int time, TimeUnit unit) throws InterruptedException {
        Future<?> future = executor.submit(task);
        try {
            future.get(time, unit);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException ignored) {
            // do nothing, task will be cancelled below
        } finally {
            // cancel if still running
            // harmless if already completed
            future.cancel(true);
        }
    }
}
