package co.raawr.cah;

import co.raawr.tempest.core.Core;

public class Main extends Core {

    public static void main(String[] args) {
        Main cah = new Main();
    }

    public Main() {

        CAH.addCards();
        setVersion("Cards Against Humanity");
        setNick("Humanity_Bot");
        setName("cah");
        connect("frogbox.es", 7000);
    }

    @Override
    public void onConnection() {
        joinChannel("#coldstorm");
    }

    @Override
    public void onMessage(String nick, String channel, String message) {
        parseMessage(nick, channel, message);
    }

    @Override
    public void onPrivateMessage(String nick, String message) {
        parseMessage(nick, nick, message);
    }

    private void parseMessage(String nick, String channel, String message) {

        if (channel.equals(getNick())) {
            // Handle PM here

        } else {
            // Handle command

        }



    }
}