package service;

import model.Customer;
import model.User;

public class CustomerService extends BaseService{
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("customer")){
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password);
    }
}
