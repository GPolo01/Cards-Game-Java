import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Truco extends Observable implements Game {
    private boolean firstTurnDraw = false;
    private boolean gameEnd = false;
    private boolean chooseHand = false;
    private boolean waitBet = false;
    private Integer whoAskedTruco = null;
    private Integer winnerFirstTurn = null;
    private int currentTurn = 1;
    private int playerPoints = 0, botPoints = 0;
    private int roundValue = 1;
    private int turnsWonPlayer = 0, turnsWonBot = 0;
    private int whoStartsRound; // 0 Bot, 1 Jogador
    private int nextToPlay;
    
    private List<Card> playerTable = new ArrayList<>();
    private List<Card> botTable = new ArrayList<>();
    
    private TrucoCard roundVira;
    private TrucoDeck deck;
    private HumanPlayer player;
    private BotPlayer bot;

    public Truco() {
        this.deck = new TrucoDeck();
        this.player = new HumanPlayer("Player");
        this.bot = new BotPlayer();
        this.whoStartsRound = new Random().nextInt(2); // Inicial draw
    }

    public HumanPlayer getPlayer() { return this.player; }
    public BotPlayer getBot() { return this.bot; }
    public int getPlayerPoints() { return playerPoints; }
    public int getBotPoints() { return botPoints; }
    public int getRoundValue() { return this.roundValue; }
    public boolean isGameEnded() { return gameEnd; }
    public boolean isChoosingHandOf11() { return chooseHand; }
    public List<Card> getPlayerTable() { return playerTable; }
    public List<Card> getBotTable() { return botTable; }
    public Card getVira() { return roundVira; }
    public boolean isWaitingBet() { return waitBet; }

    @Override
    public void playRound() {
        if (gameEnd) return;

        this.roundValue = 1;
        this.currentTurn = 1;
        this.turnsWonPlayer = 0; 
        this.turnsWonBot = 0;
        this.firstTurnDraw = false;
        this.winnerFirstTurn = null;
        this.whoAskedTruco = null;
        this.playerTable.clear();
        this.botTable.clear();
        
        player.cleanHand(); 
        bot.cleanHand();
        
        this.deck = new TrucoDeck();
        deck.shuffle();

        for (int i = 0; i < 3; i++) {
            player.addCard(deck.drawCard());
            bot.addCard(deck.drawCard());
        }
        this.roundVira = deck.drawCard();
        
        // Alternate who starts the Round
        this.nextToPlay = whoStartsRound;
        this.whoStartsRound = 1 - whoStartsRound; 

        notifyObserver("New Hand! Vira: " + roundVira);
        
        // Rule of 11 points: If player or bot with 11, each round values 3
        if (playerPoints == 11 || botPoints == 11) {
            this.roundValue = 3;
            this.chooseHand = true;
            if (playerPoints == 11 && botPoints == 11){
                notifyObserver("Both with 11, win the most luckiest!");
                this.chooseHand = false;
                checkTurnBot();
            } else if (playerPoints == 11) {
                notifyObserver("Hand of 11! Play or Give up 1 point.");
            } else {
                decisionHandOf11Bot();
            }
        } else {
            checkTurnBot();
        }
    }

    @Override
    public void end() {
        notifyObserver("Game of Truco finished!");
    }

    @Override
    public void displayResult() {
        if (playerPoints >= 12) { 
            notifyObserver("COngratulations! You made " + playerPoints + " points.");
        } else {
            notifyObserver("The Bot won with " + botPoints + " points.");
        }
    }

    @Override
    public void start() {
        this.playerPoints = 0; 
        this.botPoints = 0;
        this.gameEnd = false;
        this.whoStartsRound = new Random().nextInt(2);
        playRound();
    }

    public int getStrengthSuit(Card c) {
        int suitStrength = switch (c.getSuit()) {
                case CLUBS -> 4;
                case HEARTS -> 3;
                case SPADES -> 2;
                case DIAMONDS -> 1;
                default -> 0;
            };
        return suitStrength;
    }

    private int calculateTotalStrenght(Card c) {
        if (isManilha(c)) {
            int suitStrength = getStrengthSuit(c);
            return 50 + suitStrength;
        }
        return ((TrucoCard)c).getValue();
    }

    public String getTextOfNewValue() {
        return switch (roundValue) {
            case 1 -> "Truco!";
            case 3 -> "Six!";
            case 6 -> "Nine!";
            case 9 -> "Twelve!";
            default -> "Max Bet Made";
        };
    }


    public void decidingHandOfEleven(boolean accepts) {
        this.chooseHand = false;
        if (accepts) {
            notifyObserver("Round of 11 accepted! Round value 3 points");
            checkTurnBot();
        } else {
            notifyObserver("Point give to Bot.");
            if (playerPoints == 11) botPoints += 1;
            else playerPoints += 1;
            
            checkEndOfGame();
            if (!gameEnd) playRound();
        }
    }

    private void decisionHandOf11Bot() {
        boolean accepts = false;
        for (Card c : bot.getHand()) {
            if (isManilha(c) || c.getValue() >= 9) accepts = true;
        }
        decidingHandOfEleven(accepts);
    }

    private void botTrysAskTruco() {
        if (playerPoints == 11 || botPoints == 11) return;
        // It can't be possible ask 2 times
        if (whoAskedTruco != null && whoAskedTruco == 0) return; 

        int strenght = 0;
        for (Card c : bot.getHand()) strenght += isManilha(c) ? 20 : c.getValue();

        // If hand at least with 1 manilha, O Bot ask Truco
        if (strenght > 27 && roundValue < 12) {
                this.waitBet = true;
                notifyObserver("BOT ASK " + getTextOfNewValue().toUpperCase() + "!");
            }
    }

    private void checkTurnBot() {
        if (nextToPlay == 0 && turnsWonPlayer < 2 && turnsWonBot < 2) {
            // before playing, checks if want to ask Truco
            if (!chooseHand && !waitBet) {
                botTrysAskTruco();
            }

            if (!waitBet) {
                Card playBot = bot.getHand().remove(0);
                botTable.add(playBot);
                nextToPlay = 1;
                notifyObserver("Bot choose: " + playBot);
                
                // To avoid ignoring the card already on the table
                if (botTable.size() == playerTable.size()) {
                    compareCardsInTable();
                }
            }
        }
    }

    public void respondTrucoBot(boolean accepts) {
        this.waitBet = false;
        if (accepts) {
            this.roundValue = (roundValue == 1) ? 3 : roundValue + 3;
            this.whoAskedTruco = 0;
            notifyObserver("You accepted! The round value " + roundValue);
            if (botTable.size() < playerTable.size() || (botTable.size() == playerTable.size() && nextToPlay == 0)) {
                checkTurnBot();
            }
            else {
                notifyObserver("Your turn to play a card.");
            }
        } else {
            notifyObserver("You run! Bot won " + roundValue + " poisnt(s).");
            botPoints += roundValue;
            playRound();
        }
    }

    public void chooseCard(Card c) {
        if (nextToPlay != 1 || waitBet || chooseHand) return;

        player.getHand().remove(c);
        playerTable.add(c);
        
        // If Bot played, compare. If not, bot play now.
        if (botTable.size() < playerTable.size()) {
            nextToPlay = 0;
            checkTurnBot();
        }
        
        // We force the comparison if there's a pair of cards on the table
        if (playerTable.size() == botTable.size() && !playerTable.isEmpty()) {
            compareCardsInTable();
        }
    }

    private void compareCardsInTable() {
        if (playerTable.size() == botTable.size() && !playerTable.isEmpty()) {
            Card cJ = playerTable.get(playerTable.size()-1);
            Card cB = botTable.get(botTable.size()-1);
            
            // Manilha Logic - Ranking + 1
            int vJ = calculateTotalStrenght(cJ);
            int vB = calculateTotalStrenght(cB);

            if (vJ > vB) {
                processVictoryTurn(1); // 1 = Jogador
            } else if (vB > vJ) {
                processVictoryTurn(0); // 0 = Bot
            } else {
                // Draw
                notifyObserver("Draw!.");
                if (currentTurn == 1) {
                    firstTurnDraw = true;
                    notifyObserver("Who wins the next turn, wins the Round.");
                } else {
                    if (winnerFirstTurn != null) {
                        if (winnerFirstTurn == 1) turnsWonPlayer = 2;
                        else turnsWonBot = 2;
                        notifyObserver("Draw in " + currentTurn + "º turn! Victory to who won the 1º turn.");
                    }
                    else if (firstTurnDraw && currentTurn == 2) {
                        notifyObserver("Victory to who wins 3º turn.");
                    } 
                    else if (firstTurnDraw && currentTurn == 3) {
                        notifyObserver("Three draws! Comparison suit.");
                        if (getStrengthSuit(cB) > getStrengthSuit(cJ)) turnsWonBot = 2;
                        else turnsWonPlayer = 2;
                    }
                }
                // The turn to play goes to back to who iniciate the round 
                this.nextToPlay = (whoStartsRound == 1) ? 0 : 1; 
            }

            // Cleaning the table
            currentTurn++;
            playerTable.clear();
            botTable.clear();
            verificarFimDaMao();

            if (turnsWonPlayer < 2 && turnsWonBot < 2) {
                checkTurnBot();
            }
        }
    }

    private void processVictoryTurn(int winner) {
        // We record who won the 1º turn
        if (currentTurn == 1) {
            winnerFirstTurn = winner;
        }

        if (winner == 1) {
            turnsWonPlayer++;
            nextToPlay = 1;
            notifyObserver("You won the turn!");
            // If draw the 1º and you won the 2º you win the Round
            if (firstTurnDraw && currentTurn == 2) 
                turnsWonPlayer = 2; 
        } else {
            turnsWonBot++;
            nextToPlay = 0;
            notifyObserver("Bot won the turn!");
            if (firstTurnDraw && currentTurn == 2) 
                turnsWonBot = 2;
        }
    }

    private boolean botDecidesBet(int value) {
        int strenght = 0;
        for (Card c : bot.getHand()) {
            if (isManilha(c)) strenght += 20;
            else strenght += c.getValue();
        }
        
        // If Bot won the 1 turn, more change to do it
        if (turnsWonBot > 0) strenght += 10;

        // Trys to bluff
        int limit = 15 + new Random().nextInt(15);
        return strenght >= limit;
    }
    
    public void askTruco() {
        if (whoAskedTruco != null && whoAskedTruco == 1) {
            notifyObserver("You already raised! Wait Bot raised the value.");
            return;
        }

        if (playerPoints == 11) {
            notifyObserver("Hand of 11: you can't Trucar! You lost the match.");
            botPoints += 12;
            playRound();
            return;
        }

        int newValue = switch (roundValue) {
            case 1 -> 3;
            case 3 -> 6;
            case 6 -> 9;
            case 9 -> 12;
            default -> 12;
        };

        if (botDecidesBet(newValue)) {
            this.whoAskedTruco = 1;
            notifyObserver("You asked " + getTextOfNewValue() + "! The Bot accept.");
            this.roundValue = newValue;
        } else {
            notifyObserver("Bot fold! You won " + roundValue + " point(s).");
            playerPoints += roundValue; // win actual value before raised
            playRound();
        }
    }

    private void checkEndOfGame() {
        if (playerPoints >= 12 || botPoints >= 12) {
            this.gameEnd = true;
            String winner = (playerPoints >= 12) ? "YOU" : "BOT";
            notifyObserver("END OF THE GAME! WINNER: " + winner);
        }
    }

    private void verificarFimDaMao() {
        if (turnsWonPlayer >= 2) {
            playerPoints += roundValue;
            notifyObserver("You won the round!");
            checkEndOfGame();
            if (!gameEnd) playRound();
        } else if (turnsWonBot >= 2) {
            botPoints += roundValue;
            notifyObserver("Bot won the round!");
            checkEndOfGame();
            if (!gameEnd) playRound();
        }
    }

    public boolean isManilha(Card c) {
        if (roundVira == null) return false;
        // the ranking of the manilha is the next after the vira
        int rankManilha = ((TrucoCard)roundVira).getRanking().ordinal();
        int rankCard = ((TrucoCard)c).getRanking().ordinal();
        return rankCard == (rankManilha + 1) % 10;
    }
}