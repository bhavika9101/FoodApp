package model.enums;

public enum DeliveryAgentStatus {
    AVAILABLE("Available"),
    ON_DELIVERY("On Delivery");

    private final String displayName;

    DeliveryAgentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
