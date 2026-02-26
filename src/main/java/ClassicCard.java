public class ClassicCard extends Card {
    private final Ranking ranking;

    public ClassicCard(Ranking ranking, Suit suit) {
        super(suit);
        this.ranking = ranking;
    }

    public int getValue() {
        return this.ranking.getValue();
    }
    
    // Implementing Ranking
    public Ranking getRanking() {
        return this.ranking;
    }

    @Override
    public String toString() {
        return this.ranking + " de " + super.getSuit();
    }
}