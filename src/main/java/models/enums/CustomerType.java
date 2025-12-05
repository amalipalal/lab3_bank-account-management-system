package models.enums;

public enum CustomerType {
    PREMIUM("Premium"),
    REGULAR("Regular");

    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
