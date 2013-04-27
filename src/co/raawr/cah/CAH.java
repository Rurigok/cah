package co.raawr.cah;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CAH {

    // Game constants
    private static final int ROUND_LIMIT_MIN = 3;
    private static final int ROUND_LIMIT_MAX = 10;
    private static final int PLAYER_HAND_MAX = 10;

    private static Main cah;

    private static ArrayList<Player> players = new ArrayList<>();
    private static PriorityQueue<Player> playerQueue = new PriorityQueue<>();
    // Black cards are questions, bot picks these
    private static ArrayList<Card> blackDeck = new ArrayList<>();
    // White cards are answers, players use these
    private static ArrayList<Card> whiteDeck = new ArrayList<>();

    private static int round = 0;
    private static int rounds = 0;
    private static int czar = 0;
    private static Player owner;
    private static boolean gamePrepped = false;

    public static void initHandler(Main cah) {
        CAH.cah = cah;
    }

    public static void addCards() {
        // Scans and adds cards to decks
        // Should only be called once on startup
        addWhiteCards();
        addBlackCards();
    }

    public static void addBlackCards() {
        try {
            File f = new File("black.dat");
            Scanner sc = new Scanner(f);

            String line;
            String expansion = "default";
            String content;

            // Scan and add black cards
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.matches("\\{.*\\}")) {
                    expansion = line.replace("{", "").replace("}", "");
                    continue;
                }
                content = line;
                Card c = new Card("black", content, expansion);
                blackDeck.add(c);
            }
            // Shuffle
            Collections.shuffle(blackDeck);
            cah.print("Added " + blackDeck.size() + " black cards and shuffled.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CAH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addWhiteCards() {
        try {
            File f = new File("white.dat");
            Scanner sc = new Scanner(f);

            String line;
            String expansion = "default";
            String content;

            // Scan and add white cards
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.matches("\\{.*\\}")) {
                    expansion = line.replace("{", "").replace("}", "");
                    continue;
                }
                content = line;
                Card c = new Card("white", content, expansion);
                whiteDeck.add(c);
            }
            // Shuffle
            Collections.shuffle(whiteDeck);
            cah.print("Added " + whiteDeck.size() + " white cards and shuffled.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CAH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addPlayer(Player p) {

        if (p == null) {
            return;
        }

        if (round == 0 && gamePrepped) {
            // A game has been prepped and not started
            players.add(p);
        } else if (!gamePrepped) {
            // A game has not been prepped yet
            cah.sendMessage("#cah", "A game has not been started yet! Use .cah [rounds] to start one.");
        } else {
            // Game is in progress, add at next round
            playerQueue.add(p);
        }

    }

    public static void beginRound() {
        round++;
        // Deal white cards to players
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < PLAYER_HAND_MAX; i++) {
                players.get(i).addCard(whiteDeck.remove(j));
            }
        }
        cah.sendMessage("#cah", "[Round: " + round + "]");

        // Handle czar
        if (czar < players.size()) {
            // Set czar
            players.get(czar).isCzar = true;
            cah.sendMessage("#cah", "It is Czar " + players.get(czar).nick + "'s turn.");
        }
        // If czar is at max index, set to 0. Otherwise, increment
        czar = (czar == players.size() - 1) ? 0 : czar + 1;

        // Deal black card
        Card c = blackDeck.remove(0);
        cah.sendMessage("#cah", c.content);

        // Wait for players to submit cards
        // TODO

    }

    public static Player createPlayer(String nick) {
        return new Player(nick);
    }

    public static Player lookupPlayer(String nick) {
        for (Player p : players) {
            if (p.nick.equalsIgnoreCase(nick)) {
                return p;
            }
        }
        return null;
    }

    public static void removePlayer(Player p) {

        if (p == null) {
            // Player was not found
            cah.sendMessage("#cah", "You are not currently in a game.");
            return;
        }

        if (!gamePrepped) {
            // No game has been started; therefore you cannot leave the game
        } else if (round == 0) {
            // A game has been started but still in joining period
            // Remove player with no ill effects
            players.remove(p);
            cah.sendMessage("#cah", p.nick + " has left the game.");
        }else {
            // Check if there are enough players to continue the game
            if (players.size() < 3) {
                // Not enough players, end game
                endGame();
            }

            if (p.isCzar) {
                // Player was the czar, restart round
                // TODO
            } else {
                // Remove player and add his cards into the deck
                // TODO
            }

        }
    }

    // Called at the end of every round
    public static void roundTransition() {
        if (!playerQueue.isEmpty()) {
            // Add players that are waiting to join
            for (Player p : playerQueue) {
                players.add(p);
            }
        }
        // Onto the next round
        if (round < rounds) {
            beginRound();
        } else {
            // The game is over
            endGame();
        }
    }

    private static void endGame() {

    }

    public static void endGame(Player p) {
        if (p.isOwner) {
            endGame();
            cah.sendMessage("#cah", "The owner " + p.nick + " has ended the game.");
        } else {
            cah.sendMessage("#cah", "You may not end the game.");
        }
    }

    public static void startGame(int rounds, Player owner) {

        if (!(round == 0)) {
            // Game is already in progress
            cah.sendMessage("#cah", "There is already a game in progress.");
            return;
        }

        if (!(rounds >= ROUND_LIMIT_MIN && rounds <= ROUND_LIMIT_MAX)) {
            // Invalid rounds
            cah.sendMessage("#cah", "Number of rounds must range from " + ROUND_LIMIT_MIN + " to " + ROUND_LIMIT_MAX + ".");
            return;
        }

        owner.isOwner = true;
        addPlayer(owner);
        CAH.owner = owner;
        CAH.rounds = rounds;

        // Wait for players to join
        gamePrepped = true;

        //beginRound();
    }

    public static void begin(Player p) {
        if (p.isOwner) {
            beginRound();
            gamePrepped = false;
            cah.sendMessage("#cah", "The game has started!");
        } else {
            cah.sendMessage("#cah", "You cannot start the game because you are not the owner.");
        }
    }

}