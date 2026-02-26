// Observador.java
public interface Observer {
    /**
    Call everytime the observable has some change with
    a description of what happened (ex: "Bot pediu Truco").
     **/
    void update(String mensagem);
} 