public abstract class Card {
    // Suit = naipe
    private final Suit suit;

    public Card(Suit suit) {
        this.suit = suit;
    }

    public Suit getSuit() {
        return this.suit;
    }

    public abstract int getValue();
    
    // Two differents Rankings: "Normal" and one for Truco
    public abstract Object getRanking(); 
    
    // Método universal toString
    @Override
    public abstract String toString();
}