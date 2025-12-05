package models.enums;

public enum AccountType {
    SAVINGS("Savings"),
    CHECKING("Checking");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
