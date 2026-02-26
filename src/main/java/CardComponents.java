import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;

public class CardComponents extends StackPane {
    private Card cardLogic;
    private ImageView imagemView;
    private Runnable clickAction;
    private boolean facesDown;

    public CardComponents(Card card, boolean facesDown, Runnable action) {
        this.cardLogic = card;
        this.facesDown = facesDown;
        this.clickAction = action;
        this.imagemView = new ImageView();
        
        imagemView.setFitWidth(100);
        imagemView.setPreserveRatio(true);

        // Visual effect when mouse pass by
        this.setOnMouseEntered(e -> this.setStyle("-fx-opacity: 0.8; -fx-cursor: hand;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-opacity: 1.0;"));

        // Record event when clicked
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (clickAction != null) {
                clickAction.run();
            }
        });
        
        loadImage();
        this.getChildren().add(imagemView);
    }

    // Method to show manilha in Truco
    public void setDestaqueManilha() {
        this.setStyle("-fx-effect: dropshadow(three-pass-box, gold, 15, 0.5, 0, 0); -fx-border-color: gold; -fx-border-width: 3;");
    }

    private void loadImage() {
        if (facesDown || cardLogic == null) {
            // When hide shows the verse
            imagemView.setImage(new Image(getClass().getResourceAsStream("/images/card_back.png")));
        } else {
            String fileName = createNameFile(); 
            try {
                imagemView.setImage(new Image(getClass().getResourceAsStream("/images/" + fileName)));
            } catch (Exception e) {
                imagemView.setImage(new Image(getClass().getResourceAsStream("/images/card_back.png")));
            }
        }
    }

    private String createNameFile() {
        String suitS = cardLogic.getSuit().toString().toLowerCase(); 
        if(suitS.equals("paus")) suitS = "clubs";
        else if(suitS.equals("ouros")) suitS = "diamonds";
        else if(suitS.equals("copas")) suitS = "hearts";
        else if(suitS.equals("espadas")) suitS = "spades";

        String rankS = "";
        Object ranking = cardLogic.getRanking();
        if (ranking instanceof Ranking r) rankS = r.name();
        else if (ranking instanceof TrucoRanking rt) rankS = rt.name();

        String sufixo = switch (rankS) {
            case "ACE" -> "A";
            case "JACK" -> "J";
            case "QUEEN" -> "Q";
            case "KING" -> "K";
            case "TWO" -> "02";
            case "THREE" -> "03";
            case "FOUR" -> "04";
            case "FIVE" -> "05";
            case "SIX" -> "06";
            case "SEVEN" -> "07";
            case "EIGHT" -> "08";
            case "NINE" -> "09";
            case "TEN" -> "10";
            default -> "A";
        };
        return String.format("card_%s_%s.png", suitS, sufixo);
    }
}