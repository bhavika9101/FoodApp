package model.enums;

public enum OrderStatus {
    PLACED("Order Placed"),
    APPROVED("Order Approved"),
    READY_FOR_DELIVERY("Ready for Delivery"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
