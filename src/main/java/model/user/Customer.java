package model.user;

public class Customer extends BaseUser {
    private String address;

    public Customer() {
        super();
    }

    public Customer(String customerName, String password, String phoneNumber) {
        super(customerName, password, phoneNumber);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
