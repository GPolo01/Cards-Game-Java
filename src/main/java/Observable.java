// Observado.java
import java.util.ArrayList;
import java.util.List; 

public abstract class Observable {
    private List<Observer> observers = new ArrayList<>();

    // Adds a new observer to the notification list
    public void addObserver(Observer o) {
        observers.add(o);
    }

    // Removes an observer from the list
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    // Notifies all registered observers about a game event
    protected void notifyObserver(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}