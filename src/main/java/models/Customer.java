package models;

import models.enums.CustomerType;

public abstract class Customer {
    private final String customerId;
    private final String name;
    private final int age;
    private final String contact;
    private final String address;
    private static int customerCounter = 0;

    public Customer(String name, int age, String contact, String address) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.age = age;
        
        Customer.increaseCustomerCount();
        this.customerId = generateCustomerId();
    }

    public abstract String displayCustomerDetails();

    public abstract CustomerType getCustomerType();

    public static int getCustomerCounter() {
        return customerCounter;
    }

    private static void increaseCustomerCount() {
        Customer.customerCounter += 1;
    }

    private String generateCustomerId() {
        return "CUS" + customerCounter;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }
}
