import java.util.ArrayList;
import java.util.List;

public abstract class PLayer {
    private String name;
    private List<Card> hand; 

    public PLayer(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    
    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        this.hand.add(card);
    }
    
    public void cleanHand() {
        this.hand.clear();
    }
}