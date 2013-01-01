package co.raawr.tempest.core;

import java.util.ArrayList;

/**
 *
 * @author Raawr Tempest Labs
 */
public class User {

    private ArrayList<String> channels = new ArrayList<>();
    private Rank rank = Rank.NONE;
    private String nick;
    private String hostmask;

    public User(String nick) {
        this.nick = nick;
    }

    public void addChannel(String channel) {
        channels.add(channel);
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setHostmask(String hostmask) {
        this.hostmask = hostmask;
    }

    public String getHostmask() {
        return hostmask;
    }

    public ArrayList<String> getChannels() {
        return channels;
    }

    public void removeChannel(String channel) {
        channels.remove(channel);
    }

    public void setRank(int r) {
        switch (r) {
            case 0:
                rank = Rank.NONE;
                break;
            case 1:
                rank = Rank.VOICE;
                break;
            case 2:
                rank = Rank.OP;
                break;
            case 3:
                rank = Rank.SUPEROP;
                break;
            case 4:
                rank = Rank.OWNER;
                break;
            default:
                rank = Rank.NONE;
        }
    }

    public int getRank() {
        switch (rank) {
            case NONE:
                return 0;
            case HALFOP:
                return 1;
            case OP:
                return 2;
            case SUPEROP:
                return 3;
            case OWNER:
                return 4;
            default:
                return 0;
        }
    }
}
