import java.util.ArrayList;
import java.util.List;

public class Summod extends Observable implements Game {
    private boolean gameEnded = false;
    private boolean waitingSteal = false;
    private boolean faseTable = false;

    private Card botCardHighlighted = null;
    private Deck deck;
    private HumanPlayer player;
    private BotPlayer bot;
    
    private List<Card> temporarilySelected = new ArrayList<>();
    private List<Card> waitList = new ArrayList<>();
    private List<Card> playerTable = new ArrayList<>();
    private List<Card> botTable = new ArrayList<>();

    public Summod() {
        this.deck = new Deck();
        this.player = new HumanPlayer("Player");
        this.bot = new BotPlayer();
    }

    @Override
    public void start() {
        player.cleanHand();
        bot.cleanHand();
        waitList.clear();
        playerTable.clear();
        botTable.clear();
        faseTable = false;
        waitingSteal = false;
        gameEnded = false;
        deck = new Deck();
        deck.shuffle();

        for (int i = 0; i < 5; i++) {
            player.addCard(deck.drawCard());
            bot.addCard(deck.drawCard());
        }
        notifyObserver("Summod: Choose between 1 to 3 cards of same suit.");
    }

    public void processClickInCard(Card c) {
        if (waitingSteal) {
            playerSteal(c);
            return;
        }
        if (faseTable) return; // Don't select cards while visualizing table

        if (temporarilySelected.contains(c)) {
            temporarilySelected.remove(c);
        } else if (temporarilySelected.size() < 3) {
            if (temporarilySelected.isEmpty() || temporarilySelected.get(0).getSuit() == c.getSuit()) {
                temporarilySelected.add(c);
            } else {
                notifyObserver("WRONG: Pick cards of same suit!");
            }
        }
        notifyObserver("Selected: " + temporarilySelected.size());
    }

    public void confirmPlay() {
        if (temporarilySelected.isEmpty() || faseTable) return;
        
        // Move to table
        playerTable = new ArrayList<>(temporarilySelected);
        player.getHand().removeAll(playerTable);
        temporarilySelected.clear();

        // Use the first card
        botTable.clear();
        botTable.add(bot.getHand().remove(0));

        faseTable = true;
        processWinner();
    }

    private void processWinner() {
        Suit nH = playerTable.get(0).getSuit();
        Suit nB = botTable.get(0).getSuit();

        if (wonBySuit(nH, nB)) {
            notifyObserver("You won by Suit! Pick a card to steal from Bot.");
            waitingSteal = true;
        } else if (wonBySuit(nB, nH)) {
            notifyObserver("Bot won and Stole one card.");
            botSteal();
        } else {
            int sumP = sum(playerTable);
            int sumB = sum(botTable);
            if (sumP > sumB) {
                notifyObserver("You won by Total SUM! Pick a card to steal from Bot.");
                waitingSteal = true;
            } else if (sumB > sumP) {
                notifyObserver("Bot won by Total SUM and Stole one card.");
                botSteal();
            } else {
                waitList.addAll(playerTable);
                waitList.addAll(botTable);
                notifyObserver("Draw! Cards in waitList. Click in 'Next Round'.");
            }
        }
    }

    private boolean wonBySuit(Suit nA, Suit nB) {
        // Same logic as rock, paper, scissors 
        // Diamonds -> Spades -> Hearts -> Clubs -> Diamonds
        return (nA == Suit.SPADES && nB == Suit.DIAMONDS) ||
               (nA == Suit.HEARTS && nB == Suit.SPADES) ||
               (nA == Suit.CLUBS && nB == Suit.HEARTS) ||
               (nA == Suit.DIAMONDS && nB == Suit.CLUBS);
    }

    private void playerSteal(Card choosen) {
        // Here I take just one card from Bot
        if (!botTable.contains(choosen)) return;
        
        player.getHand().addAll(playerTable);
        player.addCard(choosen);
        botTable.remove(choosen);
        bot.getHand().addAll(botTable);
        
        player.getHand().addAll(waitList);
        waitList.clear();
        
        finishTurn();
    }

    private void botSteal() {
        // Bot steals the card with less value
        Card weakCard = playerTable.get(0);
        for(Card c : playerTable) if(c.getValue() < weakCard.getValue()) weakCard = c;
        
        bot.getHand().addAll(botTable);
        bot.addCard(weakCard);
        playerTable.remove(weakCard);
        player.getHand().addAll(playerTable);
        
        bot.getHand().addAll(waitList);
        waitList.clear();
        
        // Notification about wich card was stolen
        notifyObserver("Bot stole: " + weakCard + ". Click in 'Next Round'.");
        waitingSteal = true;
        finishTurn();
    }

    public void nextRound() {
        faseTable = false;
        waitingSteal = false;
        botCardHighlighted = null;
        botTable.clear();
        playerTable.clear();
        verifyEndMatch();
        notifyObserver("New Round!");
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    private void verifyEndMatch() {
        if (player.getHand().isEmpty() || bot.getHand().isEmpty()) {
            this.gameEnded = true;
            String winner = (player.getHand().size() > bot.getHand().size()) ? "YOU WON!" : "Bot WON!";
            notifyObserver("GAME ENDED! " + winner + " Click in 'Play Again'.");
        }
    }

    private int sum(List<Card> lista) {
        int sum = 0;
        for (Card c : lista) sum += c.getValue();
        return sum;
    }

    private void finishTurn() {
        // Verify if someone is without cards after draw of steal
        verifyEndMatch(); 
        notifyObserver("Turn Finished. Click in 'Next Round' to continue.");
    }

    // Getters
    public List<Card> getPlayerTable() { return playerTable; }
    public List<Card> getBotTable() { return botTable; }
    public boolean isFaseMesa() { return faseTable; }
    public boolean isWaitingSteal() { return waitingSteal; }
    public Card getBotCardHighlighted() { return botCardHighlighted; }
    public HumanPlayer getPlayer() { return player; }
    public BotPlayer getBot() { return bot; }
    @Override public void playRound() {}
    @Override public void end() {}
    @Override public void displayResult() {}
}