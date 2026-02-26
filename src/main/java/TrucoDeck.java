import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrucoDeck {
    List<TrucoCard> cards = new ArrayList<>();

    public TrucoDeck() {
        // Iterate through all suits and rankings to build the deck
        for (Suit suit : Suit.values()) {
            for (TrucoRanking ranking : TrucoRanking.values()) {
                TrucoCard newCard = new TrucoCard(ranking, suit);
                cards.add(newCard);
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public TrucoCard drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
}