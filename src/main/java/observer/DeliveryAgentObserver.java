package observer;

import model.order.Order;

public class DeliveryAgentObserver implements Observer {
    private final Integer agentId;
    private final String agentName;

    public DeliveryAgentObserver(Integer agentId, String agentName) {
        this.agentId = agentId;
        this.agentName = agentName;
    }

    @Override
    public void update(String eventType, Order order) {
        if ("ORDER_READY_FOR_DELIVERY".equals(eventType)) {
            if (order.getAssignedAgentId() != null && order.getAssignedAgentId().equals(this.agentId)) {
                System.out.println("\n[DELIVERY AGENT NOTIFICATION] Hi " + agentName
                        + ", order #" + order.getOrderId() + " is ready for delivery!");
                System.out.println("  Customer : " + order.getCustomerName());
                System.out.println("  Address  : " + order.getCustomerAddress());
            }
        }
    }

    public Integer getAgentId() {
        return agentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DeliveryAgentObserver obj))
            return false;
        return agentId.equals(obj.agentId);
    }

    @Override
    public int hashCode() {
        return agentId.hashCode();
    }
}
