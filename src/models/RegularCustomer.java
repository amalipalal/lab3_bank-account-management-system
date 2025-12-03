package models;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public String displayCustomerDetails() {
        return String.format("%s (%s)", this.getName(), this.getCustomerType());
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}
