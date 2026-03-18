package model.user;

import model.enums.DeliveryAgentStatus;

public class DeliveryAgent extends BaseUser {
    private DeliveryAgentStatus status;
    private Integer currentOrderId;
    private Double grossEarning;
    private Double baseSalary;
    private Double commissionRate;

    public DeliveryAgent() {
        super();
        this.status = DeliveryAgentStatus.AVAILABLE;
    }

    public DeliveryAgent(String agentName, String password, String phoneNumber) {
        super(agentName, password, phoneNumber);
        this.status = DeliveryAgentStatus.UNAVAILABLE;
        this.grossEarning = 0.0;
        this.baseSalary = 0.0;
        this.commissionRate = 0.0;
    }

    public DeliveryAgent(int userId, String username, String password, String phoneNumber,
            DeliveryAgentStatus status, double grossEarning, double baseSalary, double commissionRate) {
        super(userId, username, password, phoneNumber);
        this.status = status;
        this.grossEarning = grossEarning;
        this.baseSalary = baseSalary;
        this.commissionRate = commissionRate;
    }

    public DeliveryAgentStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryAgentStatus status) {
        if (baseSalary == 0 && commissionRate == 0) {
            System.out.println("Pay your delivery partner base salary or commission rate.");
            this.status = DeliveryAgentStatus.UNAVAILABLE;
            return;
        }
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

    public void incrementGrossEarning(Double amount) {
        this.grossEarning += amount;
    }

    public Double getGrossEarning() {
        return grossEarning;
    }

    public void setBaseSalary(Double baseSalary) {
        this.status = DeliveryAgentStatus.AVAILABLE;
        this.baseSalary = baseSalary;
        this.grossEarning += baseSalary;
    }

    public void setCommissionRate(Double commissionRate) {
        this.status = DeliveryAgentStatus.AVAILABLE;
        this.commissionRate = commissionRate;
    }

    public Double getBaseSalary() {
        return baseSalary;
    }

    public Double getCommissionRate() {
        return commissionRate;
    }
}
