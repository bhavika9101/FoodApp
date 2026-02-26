package model.user;

import model.enums.DeliveryAgentStatus;

public class DeliveryAgent extends BaseUser {
    private DeliveryAgentStatus status;
    private Integer currentOrderId;

    public DeliveryAgent() {
        super();
        this.status = DeliveryAgentStatus.AVAILABLE;
    }

    public DeliveryAgent(String agentName, String password) {
        super(agentName, password);
        this.status = DeliveryAgentStatus.AVAILABLE;
    }

    public DeliveryAgentStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryAgentStatus status) {
        this.status = status;
    }

    public Integer getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(Integer currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public Boolean isAvailable() {
        return this.status == DeliveryAgentStatus.AVAILABLE;
    }
}
