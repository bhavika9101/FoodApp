package model.enums;

public enum PaymentMode {
    CASH("Cash"),
    UPI("UPI");

    private final String displayName;

    PaymentMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
