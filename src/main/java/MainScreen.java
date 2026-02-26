import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScreen implements Observer {
    private Stage stage;
    private Label lblScoreboard;
    private BorderPane mainLayout;
    private TextArea communicationLog;
    private HBox playerCardArea;
    private HBox botCardArea;
    private HBox tableArea;
    private VBox viraArea;
    private Game currentGame;

    public void displayMainMenu(Stage stage) {
        this.stage = stage;
        this.mainLayout = new BorderPane();
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        
        Label title = new Label("CARD GAME COLLECTION");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btn21 = new Button("Play Blackjack (21)");
        Button btnSummod = new Button("Play Summod");
        Button btnTruco = new Button("Play Truco");

        btn21.setOnAction(e -> startBlackJack());
        btnSummod.setOnAction(e -> startSummod());
        btnTruco.setOnAction(e -> startTruco());

        menu.getChildren().addAll(title, btn21, btnSummod, btnTruco);
        mainLayout.setCenter(menu);

        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void startBlackJack() {
        BlackJack game = new BlackJack();
        this.currentGame = game;
        game.addObserver(this);

        setupGameLayout("Black Jack (21)");
        
        Button btnHit = new Button("Hit (Draw)");
        Button btnStand = new Button("Stop");
        Button btnNext = new Button("Next Round");
        Button btnMenu = new Button("Main Menu");
        
        btnHit.setOnAction(e -> game.drawCard());
        btnStand.setOnAction(e -> game.parar());
        btnNext.setOnAction(e -> game.start());
        btnMenu.setOnAction(e -> displayMainMenu(stage));

        ((HBox)mainLayout.getBottom()).getChildren().addAll(btnHit, btnStand, btnNext, btnMenu);
        game.start();
    }

    private void startSummod() {
        Summod game = new Summod();
        this.currentGame = game;
        game.addObserver(this);

        setupGameLayout("Summod");
        
        Button btnConfirm = new Button("Confirm Play");
        Button btnNext = new Button("Next Round");
        Button btnRestart = new Button("Play Again");
        Button btnMenu = new Button("Main Menu");
        
        btnConfirm.setOnAction(e -> {
            if (!game.isGameEnded()) game.confirmPlay();
        });
        
        btnNext.setOnAction(e -> {
            if (!game.isGameEnded()) game.nextRound();
        });

        // Calls start() to reset tudo everthing and give new cards
        btnRestart.setOnAction(e -> game.start());
        btnMenu.setOnAction(e -> displayMainMenu(stage));

        // Add all the buttons to the bottom in the screen
        ((HBox)mainLayout.getBottom()).getChildren().addAll(
            btnConfirm, btnNext, btnRestart, btnMenu
        );
        
        game.start();
    }

    private void startTruco() {
        Truco game = new Truco();
        this.currentGame = game;
        game.addObserver(this);
        setupGameLayout("Truco");
        game.start();
    }

    private void setupGameLayout(String gameName) {
        mainLayout.setCenter(null);
        VBox tableLayout = new VBox(30);
        tableLayout.setAlignment(Pos.CENTER);
        
        botCardArea = new HBox(10);
        botCardArea.setAlignment(Pos.CENTER);

        HBox centroHBox = new HBox(40);
        centroHBox.setAlignment(Pos.CENTER);
        
        viraArea = new VBox(5);
        viraArea.setAlignment(Pos.CENTER);

        //Central Table
        VBox centroMesaVBox = new VBox(5);
        centroMesaVBox.setAlignment(Pos.CENTER);
        tableArea = new HBox(15);
        tableArea.setAlignment(Pos.CENTER);
        tableArea.setStyle("-fx-background-color: rgba(0, 100, 0, 0.2); -fx-background-radius: 10;");
        tableArea.setPrefHeight(120);
        centroMesaVBox.getChildren().addAll(new Label("CARDS IN GAME"), tableArea);

        centroHBox.getChildren().addAll(viraArea, centroMesaVBox);

        playerCardArea = new HBox(10);
        playerCardArea.setAlignment(Pos.CENTER);
        
        communicationLog = new TextArea();
        communicationLog.setEditable(false);
        communicationLog.setPrefHeight(150);

        lblScoreboard = new Label("SCORE: Player 0 x 0 Bot");
        lblScoreboard.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: #2c3e50; -fx-padding: 10;");

        tableLayout.getChildren().addAll(
                new Label("Bot's Hand"), botCardArea, 
                centroHBox, // Insert Table
                new Label("Your Hand"), playerCardArea
        );

        tableLayout.getChildren().add(0, lblScoreboard);

        mainLayout.setCenter(tableLayout);
        mainLayout.setRight(communicationLog);
        
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        mainLayout.setBottom(controls);
    }

    @Override
    public void update(String message) {
        communicationLog.appendText(message + "\n");
        renderHands();

        HBox controls = (HBox) mainLayout.getBottom();

        // Universal logic to update scoreboard
        if (currentGame instanceof Truco truco) {
            lblScoreboard.setText(String.format("TRUCO - SCORE: YOU %d x %d BOT | Value of the Round: %d", 
                truco.getPlayerPoints(), truco.getBotPoints(), truco.getRoundValue()));
            updateTrucoControls(truco);
        } 
        else if (currentGame instanceof BlackJack v21) {
            lblScoreboard.setText(String.format("|Black Jack - SCORE: YOU %d x %d BOT (Until 3)", 
            v21.getPlayerPoints(), v21.getBotPoints()));
            
            // Define state
            for (javafx.scene.Node node : controls.getChildren()) {
                if (node instanceof Button btn) {
                    if (btn.getText().equals("Next Round")) btn.setDisable(!v21.isGameEnded());
                    if (btn.getText().equals("Ask More Card") || btn.getText().equals("Stop")) 
                        btn.setDisable(v21.isGameEnded());
                }
            }
        }
        else if (currentGame instanceof Summod summod) {
            lblScoreboard.setText("SUMMOD - Game in process");

            for (javafx.scene.Node node : controls.getChildren()) {
                if (node instanceof Button btn ) {
                    if (btn.getText().equals("Next Round"))
                    // Desabilita se estiver esperando o roubo OU se ainda não houve disputa na mesa
                        btn.setDisable(!summod.isWaitingSteal() || !summod.isFaseMesa());
                }
            }
        }
    }

    private void updateTrucoControls(Truco truco) {
        HBox controls = (HBox) mainLayout.getBottom();
        controls.getChildren().clear();

        if (truco.isGameEnded()) {
            Button btnRestart = new Button("Play Again");
            btnRestart.setOnAction(e -> truco.start());
            controls.getChildren().add(btnRestart);
        } else if (truco.isChoosingHandOf11()){
            Button btnPlay = new Button("Play Round (Worth 3)");
            Button btnGiveUp = new Button("Fold (1 point)");

            btnPlay.setOnAction(e -> truco.decidingHandOfEleven(true));
            btnGiveUp.setOnAction(e -> truco.decidingHandOfEleven(false));

            controls.getChildren().addAll(btnPlay, btnGiveUp);
        }
        else if (truco.isWaitingBet()) { // Bot trucou You
            Button btnAccept = new Button("Accept " + truco.getTextOfNewValue());
            Button btnFold = new Button("Fold (Run)");
            
            btnAccept.setOnAction(e -> truco.respondTrucoBot(true));
            btnFold.setOnAction(e -> truco.respondTrucoBot(false));
            
            controls.getChildren().addAll(btnAccept, btnFold);
        }
        // Dinamic button to Trucar
        else if (truco.getRoundValue() < 12) {
            Button btnBet = new Button(truco.getTextOfNewValue());
            btnBet.setOnAction(e -> truco.askTruco());
            controls.getChildren().add(btnBet);
        }


        Button btnMenu = new Button("Main Menu");
        btnMenu.setOnAction(e -> displayMainMenu(stage));
        controls.getChildren().add(btnMenu);
    }

    private void renderHands() {
        playerCardArea.getChildren().clear();
        botCardArea.getChildren().clear();
        tableArea.getChildren().clear();
        viraArea.getChildren().clear();

        if (currentGame instanceof BlackJack v21) {
            // PLayer Cards always visible
            for (Card c : v21.getPlayer().getHand()) {
                playerCardArea.getChildren().add(new CardComponents(c, false, null));
            }
            // Bot: Shows cards when round finished
            boolean revealBot = v21.isGameEnded();
            for (Card c : v21.getBot().getHand()) {
                botCardArea.getChildren().add(new CardComponents(c, !revealBot, null));
            }
        } 
        else if (currentGame instanceof Summod summod) {
            if (!summod.isFaseMesa()) {
                // Inicial Visual
                for (Card c : summod.getPlayer().getHand()) {
                    playerCardArea.getChildren().add(new CardComponents(c, false, () -> summod.processClickInCard(c)));
                }
                for (Card c : summod.getBot().getHand()) {
                    // We see the verse of the bot cards
                    botCardArea.getChildren().add(new CardComponents(c, true, null));
                }
            } else {
                // Cards Reveal
                for (Card c : summod.getPlayerTable()) {
                    CardComponents cp = new CardComponents(c, false, null);
                    // Emphasis if bot stole a card
                    if (c == summod.getBotCardHighlighted()) cp.setDestaqueManilha(); 
                    playerCardArea.getChildren().add(cp);
                }
                for (Card c : summod.getBotTable()) {
                    // If the player won, the cards become now clickable
                    botCardArea.getChildren().add(new CardComponents(c, false, () -> summod.processClickInCard(c)));
                }
            }
        }
        else if (currentGame instanceof Truco truco) {
            if (truco.getVira() != null) {
                Label topLabel = new Label("Round Vira");
                Label baseLabel = new Label("Round Vira");
                topLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");
                baseLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");
                
                CardComponents viraVisual = new CardComponents(truco.getVira(), false, null);
                viraVisual.setStyle("-fx-effect: dropshadow(three-pass-box, white, 10, 0, 0, 0);"); // Brilho leve
                
                viraArea.getChildren().addAll(topLabel, viraVisual, baseLabel);
            }

            // 1. Show cards for player
            for (Card c : truco.getPlayer().getHand()) {
                CardComponents cp = new CardComponents(c, false, () -> truco.chooseCard(c));
                if (truco.isManilha(c)) cp.setDestaqueManilha();
                playerCardArea.getChildren().add(cp);
            }
            
            // 2. Show the verse of the bot cards
            for (Card c : truco.getBot().getHand()) {
                botCardArea.getChildren().add(new CardComponents(c, true, null));
            }

            //3. Cards Played
            for (Card c : truco.getBotTable()) {
                tableArea.getChildren().add(new CardComponents(c, false, null));
            }
            for (Card c : truco.getPlayerTable()) {
                tableArea.getChildren().add(new CardComponents(c, false, null));
            }
        }
    }


}