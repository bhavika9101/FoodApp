package model.user;

public class Customer extends BaseUser {
    private String address;

    public Customer() {
        super();
    }

    public Customer(String customerName, String password) {
        super(customerName, password);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
