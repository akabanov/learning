package nz.kabanov.learning.concurrency.idioms;

public class AntiPublishing {

    private static int answer;
    private static boolean ready;

    /*
    There is no proper synchronisation between spinning and main threads.
    That means that the changes made by the main thread AFTER spinning thread started
    may never be visible by the spinning one.
     */
    public static void main(String[] args) {
        new Thread(() -> {
            while (!ready) Thread.yield(); // can take forever
            System.out.println(answer); // can print '0' (zero) even if (ready == true)
        }).start();
        answer = 42;
        ready = true;
    }
}
