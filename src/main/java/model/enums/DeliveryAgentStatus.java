package model.enums;

public enum DeliveryAgentStatus {
    AVAILABLE("Available"),
    ON_DELIVERY("On Delivery"),
    UNAVAILABLE("Unavailable");

    private final String displayName;

    DeliveryAgentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
