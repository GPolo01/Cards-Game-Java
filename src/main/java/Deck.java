import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    List<Card> cards = new ArrayList<>();

    public Deck() {
        // Iterate through all suits and rankings to build the deck
        for (Suit suit : Suit.values()) {
            for (Ranking ranking : Ranking.values()) {
                ClassicCard newCard = new ClassicCard(ranking, suit);
                cards.add(newCard);
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
}