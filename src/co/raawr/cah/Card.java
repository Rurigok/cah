/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.raawr.cah;

/**
 *
 * @author Andrew
 */
public class Card {

    public String type = "";
    public String content = "";
    public String expansion = "";

    public Card(String type, String content, String expansion) {
        this.type = type;
        this.content = content;
        this.expansion = expansion;
    }

}
