public enum Ranking {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10), // J
    QUEEN(10), // Q
    KING(10);   // K

    private final int valor;

    // Constructor 
    Ranking(int valor) {
        this.valor = valor;
    }

    public int getValue() {
        return valor;
    } 
}