# Java - Card Game Collection

This project was designed to learn with practice all that I learned with paradigms of computer programming, also to understand the Object-Oriented Programming (OOP) concepts, Maven, design patterns, and graphical user interface (GUI) management using JavaFX.

## Language and Library

Language: Java 17+

Library: JavaFX (GUI Management)

## Architecture Pattern: Observer Pattern.

Using Observer (Observado) from the interface (TelaPrincipal). This allows the game engine to notify the UI of changes without needing to know how the UI is rendered.

Interfaces & Abstract Classes:  Used the Jogo interface to enforce a strict contract across different game rules.

Used the Observado abstract class to provide reusable notification logic for all games.

## Games and Features

Vinte Um (21): A classic sum-based game against a smart dealer.

Summod: A tactical game involving suit precedence and a card-stealing mechanic.

Truco: A full-featured implementation of the famous Brazilian card game, including bluffing, dynamic betting (Truco, 6), and the "Hand of 11" logic.

AI Opponent: Bot opponents with decision-making capabilities based on hand strength.

Dynamic UI: Real-time scoreboards, card animations (visual highlights), and event logs.

## How to Play

1. Vinte Um (21)

Objective: Get closer to 21 than the bot without exceeding it.

Rules: Aces are worth 1, face cards (J, Q, K) are worth 10. First to reach 3 points wins the match.

2. Summod

Objective: Leave your opponent without cards.

Suit Precedence: Diamonds  < Spades  < Hearts < Clubs  < Diamonds .

Mechanic: Play 1-3 cards of the same suit. If you win the round (by suit or higher sum), you steal a card from the opponent's hand.

3. Truco

Objective: Reach 12 points first.

The "Vira" & "Manilhas": One card is flipped; the next card in rank becomes the strongest (Manilha).

Betting: Players can raise the stakes (Truco, 6, 9, 12).

Tie-breaking : If turns tie, the winner of the previous turn wins the round. If all tie, the precedence of the cards of the final round decides.

## Project Structure

├── pom.xml
├──src
    ├── main
        ├── java        
        │   ├── BlackJack.java
        │   ├── Main.java
        │   ├── MainScreen.java
        │   ├── Summod.java
        │   ├── Truco.java       
        └── resources
            License, author of the images used

## Architecture Overview

The project follows a View-Observer model:

Model: The Game classes (Truco, Summod, VinteUm) manage the state, deck, and AI.

Notification: When a card is played, the game calls notificar().

View: TelaPrincipal receives the update and re-renders the HBoxes/Labels to show the new state of the table.

# Build and Run

## Requirements

Java JDK 17 or higher.
Maven.

## Instructions

1. Clone the repository:

Bash

git clone 


2. Navigate to the folder and build:

Bash

mvn clean compile


3. Run the application:

Bash

mvn exec:java -Dexec.mainClass="Main"



