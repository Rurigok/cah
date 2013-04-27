package co.raawr.cah;

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
                case ".cah":
                    CAH.startGame(Integer.parseInt(parse[1]), CAH.createPlayer(nick));
                    break;
                case ".start":
                    CAH.begin(CAH.lookupPlayer(nick));
                    break;
                case ".end":
                    CAH.endGame(CAH.lookupPlayer(nick));
                    break;
            }
        }

    }
}