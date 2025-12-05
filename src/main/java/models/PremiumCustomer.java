package models;

import models.enums.CustomerType;

public class PremiumCustomer extends Customer {

    public PremiumCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public String displayCustomerDetails() {
        return String.format("%s (%s)", this.getName(), this.getCustomerType());
    }

    @Override
    public CustomerType getCustomerType() {
        return CustomerType.PREMIUM;
    }
}
