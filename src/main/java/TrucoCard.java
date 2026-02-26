public class TrucoCard extends Card {
    private final TrucoRanking ranking;

    public TrucoCard(TrucoRanking ranking, Suit suit) {
        super(suit); 
        this.ranking = ranking;
    }

    @Override
    public int getValue() {
        return this.ranking.getTrucoValue();
    }

    @Override
    public TrucoRanking getRanking() {
        return this.ranking;
    }

    @Override
    public String toString() {
        return this.ranking + " de " + super.getSuit();
    }
}