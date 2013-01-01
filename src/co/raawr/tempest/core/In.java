package co.raawr.tempest.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Raawr Tempest Labs
 */
public class In extends Thread {

    Socket sock;
    Core core;

    public In(Socket s, Core c) throws IOException {
        sock = s;
        core = c;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s;

            while (core.listening) {
                if ((s = r.readLine()) != null) {
                    core.handleLine(s);
                }
            }
        } catch (IOException ex) {
            core.err("I/O exception.");
        } finally {
            try {
                r.close();
            } catch (IOException ex) {
                core.err("I/O exception.");
            }
        }
    }
}
