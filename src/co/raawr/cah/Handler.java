package co.raawr.cah;

import java.util.ArrayList;

public class Handler {

    static Main cah;

    public static void initHandler(Main c) {
        cah = c;
    }

    public static void handleMessage(String nick, String channel, String message) {

        String[] parse = message.split(" ");

        String command = parse[0].toLowerCase();

        if (command.startsWith(".")) {
            // It's a command - handle it
            switch (command) {
                case ".join":
                    CAH.addPlayer(CAH.createPlayer(nick));
                    break;
                case ".leave":
                case ".quit":
                    CAH.removePlayer(CAH.lookupPlayer(nick));
                    break;
            }
        }

    }

}
