public class BlackJack extends Observable implements Game {
    private Deck deck;
    private HumanPlayer player;
    private BotPlayer bot;
    private int playerPoints = 0;
    private int botPoints = 0;
    private boolean gameEnded;

    public BlackJack() {
        this.deck = new Deck();
        this.player = new HumanPlayer("Player");
        this.bot = new BotPlayer();
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    @Override
    public void start() {
        this.deck = new Deck();
        deck.shuffle();
        // If someone hit 3 points, reset
        if (playerPoints >= 3 || botPoints >= 3) {
            playerPoints = 0;
            botPoints = 0;
        }
        prepareRound();
    }

    public int getPlayerPoints() {
        return playerPoints;
    }

    public int getBotPoints() {
        return botPoints;
    }

    private void prepareRound() {
        player.cleanHand();
        bot.cleanHand();
        deck.shuffle();
        this.gameEnded = false;

        for (int i = 0; i < 2; i++) {
            player.addCard(deck.drawCard());
            bot.addCard(deck.drawCard());
        }
        notifyObserver("Score: You " + playerPoints + " x " + botPoints + " Bot.");
    }

    public void drawCard() {
        if (gameEnded) return;
        Card c = deck.drawCard();
        player.addCard(c);
        notifyObserver("You received: " + c);

        if (calculateHandValue(player) > 21) {
            botPoints++;
            this.gameEnded = true; // the round end if player pass 21
            notifyObserver("You pass 21! Point for Bot.");
            verifyVictory();
        }
    }

    public void parar() {
        if (gameEnded) return;
        turnBot();
        processWinnerRound();
    }

    private void turnBot() {
        while (calculateHandValue(bot) < 16) {
            bot.addCard(deck.drawCard());
        }
    }

    private void processWinnerRound() {
        int vJ = calculateHandValue(player);
        int vB = calculateHandValue(bot);

        if (vB > 21 || vJ > vB) {
            playerPoints++;
            notifyObserver("You won the round!");
        } else if (vJ < vB) {
            botPoints++;
            notifyObserver("Bot won the round!");
        } else {
            notifyObserver("Draw!");
        }
        this.gameEnded = true;
        verifyVictory();
    }

    private void verifyVictory() {
        if (playerPoints >= 3 || botPoints >= 3) {
            String vencedor = playerPoints >= 3 ? "YOU" : "BOT";
            notifyObserver("GAME ENDED! WINNER: " + vencedor);
        } else {
            notifyObserver("Click in Next Match to continue.");
        }
    }

    public int calculateHandValue(PLayer player) {
        int sum = 0;
        for (Card c : player.getHand()) sum += c.getValue();
        return sum;
    }

    public HumanPlayer getPlayer() { return player; }
    public BotPlayer getBot() { return bot; }
    @Override public void playRound() {}
    @Override public void end() {}
    @Override public void displayResult() {}
}