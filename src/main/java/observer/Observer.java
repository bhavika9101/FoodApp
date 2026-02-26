package observer;

import model.order.Order;

public interface Observer {
    void update(String eventType, Order order);
}
