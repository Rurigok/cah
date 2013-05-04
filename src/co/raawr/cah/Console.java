package co.raawr.cah;

import java.util.Scanner;

public class Console {

    public static void init(Main c) {
        new ConsoleThread(c).start();
    }

}

class ConsoleThread extends Thread {

    static Scanner sc;
    static Main cah;

    ConsoleThread(Main c) {
        cah = c;
        sc = new Scanner(System.in);
    }

    @Override
    public void run() {
        read();
    }

    private void read() {
        String s;
        while ((s = sc.nextLine()) != null) {
            parseCommand(s);
        }
    }

    private void parseCommand(String line) {
        cah.forceSendLine(line);
    }

}
