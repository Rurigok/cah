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

    // Bot framework
    private static Main cah;
    // Game constants
    private static final int ROUND_LIMIT_MIN = 3;
    private static final int ROUND_LIMIT_MAX = 10;
    private static final int PLAYER_HAND_MAX = 10;
    // Player and card handling lists
    private static ArrayList<Player> players = new ArrayList<>();
    private static PriorityQueue<Player> playerQueue = new PriorityQueue<>();
    // Black cards are questions, bot shows these
    private static ArrayList<Card> blackDeck = new ArrayList<>();
    // White cards are answers, players use these
    private static ArrayList<Card> whiteDeck = new ArrayList<>();
    // Game variables - do not change
    //private static ArrayList<Card> roundHand = new ArrayList<>();
    private static Card activeCard;
    private static int round = 0;
    private static int rounds = 0;
    private static int czar = 0;
    //private static Player owner;
    // If a game has been initialized with .cah but not started
    private static boolean gamePrepped = false;
    // If the czar is picking a card
    private static boolean pickingCard = false;

    public static void init(Main cah) {
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
        if (blackDeck.isEmpty()) {
            addBlackCards();
        }
        activeCard = blackDeck.remove(0);
        cah.sendMessage("#cah", activeCard.content);

        // Deal white cards to players
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < PLAYER_HAND_MAX; j++) {
                // Make sure we don't run out of cards
                if (whiteDeck.isEmpty()) {
                    // We ran out of white cards! Rescan and shuffle
                    addWhiteCards();
                }
                players.get(i).addCard(whiteDeck.remove(j));
            }
        }

        // Show white cards to players
        String cards = "";
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.get(i).hand.size(); j++) {
                cards += i + 1 + ": [" + players.get(i).hand.get(j).content + "] ";
            }
            // Do not show cards to czar
            if (!players.get(i).isCzar) {
                cah.sendMessage(players.get(i).nick, cards);
                players.get(i).awaitingSubmit = true;
            } else {
                cah.sendMessage(players.get(i).nick, "You are the czar this round!");
            }
        }

        // All cards have been dealt and shown

    }

    public static void czarPickCard(Player p, int card) {
        // Make sure player is actually in game
        if (p == null) {
            cah.sendNotice(p.nick, "You are not currently in the game!");
            return;
        }
        // Make sure the player is actually the czar
        if (!p.isCzar) {
            cah.sendNotice(p.nick, "You are not the czar!");
            return;
        }
        // They are the czar, check if ready to pick a card
        if (pickingCard) {
            // index of player who won
            Player w = players.get(card - 1);
            w.score++;
            cah.sendMessage("#cah", w.nick + " has won this round. Current score: " + w);
            roundTransistion();
        }
    }

    public static void pickCard(Player p, int card) {
        // Make sure that player is actually in-game
        if (p == null) {
            cah.sendMessage(p.nick, "You are not currently in the game!");
            return;
        }
        // Make sure we're actually waiting for a card from this player
        if (p.awaitingSubmit) {
            p.playedCardIndex = card - 1;
            p.awaitingSubmit = false;
        } else {
            cah.sendMessage(p.nick, "You cannot submit a card at this time!");
        }
        // Now check if all players have submitted
        for (Player pl : players) {
            if (!pl.isCzar && pl.awaitingSubmit) {
                return;
            }
        }
        // If we're here, everyone has submitted! Display picks and begin czar picking
        String cards = "";
        for (int i = 0; i < players.size(); i++) {
            cards += "[" + (i + 1) + " : " + players.get(i).hand.get(players.get(i).playedCardIndex) + "] ";
        }
        cah.sendMessage("#cah", "Black card: [" + activeCard.content + "]");
        cah.sendMessage("#cah", cards);
        pickingCard = true;
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
        } else {
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

    public static void roundTransistion() {
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

    public static void prepGame(int rounds, Player owner) {

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

        // Designate game owner and add him to game
        owner.isOwner = true;
        addPlayer(owner);
        //CAH.owner = owner;
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