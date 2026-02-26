public enum TrucoRanking {
    FOUR(1), 
    FIVE(2), 
    SIX(3), 
    SEVEN(4), 
    QUEEN(5), 
    JACK(6), 
    KING(7), 
    ACE(8), 
    TWO(9), 
    THREE(10);

    private final int valor;

    TrucoRanking(int valor) {
        this.valor = valor;
    }

    public int getTrucoValue() {
        return valor;
    } 
}