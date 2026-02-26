package observer;

import model.order.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<String, List<Observer>> listeners = new HashMap<>();

    public void subscribe(String eventType, Observer observer) {
        List<Observer> observerList = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    public void unsubscribe(String eventType, Observer observer) {
        List<Observer> observerList = listeners.get(eventType);
        if (observerList != null) {
            observerList.remove(observer);
        }
    }

    public void notifyObservers(String eventType, Order order) {
        List<Observer> observerList = listeners.get(eventType);
        if (observerList != null) {
            for (Observer observer : observerList) {
                observer.update(eventType, order);
            }
        }
    }
}
