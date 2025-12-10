package models;

import models.enums.CustomerType;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public String displayCustomerDetails() {
        return String.format("%s (%s)", this.getName(), this.getCustomerType());
    }

    @Override
    public CustomerType getCustomerType() {
        return CustomerType.REGULAR;
    }

    @Override
    public String toCsv() {
        return String.join(",",
                this.getCustomerType().toString(),
                super.getName(),
                String.valueOf(super.getAge()),
                super.getContact(),
                super.getAddress()
        );
    }
}
