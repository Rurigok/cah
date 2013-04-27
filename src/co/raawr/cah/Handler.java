package co.raawr.cah;

public class Handler {

    static Main cah;

    public static void init(Main c) {
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
                    if (isInteger(parse[1])) {
                        CAH.prepGame(Integer.parseInt(parse[1]), CAH.createPlayer(nick));
                    }
                    break;
                case ".start":
                    CAH.begin(CAH.lookupPlayer(nick));
                    break;
                case ".end":
                case ".stop":
                    CAH.endGame(CAH.lookupPlayer(nick));
                    break;
            }
        } else if (isInteger(command)) {
            // It's the czar picking a card
            CAH.czarPickCard(CAH.lookupPlayer(nick), Integer.parseInt(command));
        }

    }

    public static void handlePM(String nick, String message) {
        if (isInteger(message)) {
            // Player picked their card
            CAH.pickCard(CAH.lookupPlayer(nick), Integer.parseInt(message));
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}