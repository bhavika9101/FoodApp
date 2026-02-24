package model.user;

import model.user.BaseUser;

public class Customer extends BaseUser {
    public Customer(){
        super();
    }
    public Customer(String customerName, String password){
        super(customerName, password);
    }
}
