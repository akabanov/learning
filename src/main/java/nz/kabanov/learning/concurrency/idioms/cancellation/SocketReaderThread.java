package nz.kabanov.learning.concurrency.idioms.cancellation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketReaderThread extends Thread {

    private final Socket socket;
    private final InputStream source;

    public SocketReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        source = socket.getInputStream();
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[0x4_000];
            while (true) {
                int read = source.read(buf); // <-- blocking, can hang for quite a while
                if (read < 0) {
                    break;
                }
                // process input
            }
        } catch (IOException ignored) { // genuine I/O error or socket was closed in the #interrupt()
        }
    }
}
