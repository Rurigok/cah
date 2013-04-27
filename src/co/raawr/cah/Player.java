/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.raawr.cah;

import java.util.ArrayList;

/**
 *
 * @author Andrew
 */
public class Player {

    public ArrayList<Card> hand = new ArrayList<>();
    public boolean isCzar = false;
    public String nick;
    public int score = 0;

    public Player(String nick) {
        this.nick = nick;
    }

    public void addCard(Card c) {
        hand.add(c);
    }

}
