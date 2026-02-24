package service;

import model.Customer;
import model.User;

public class CustomerService extends BaseService{
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("customer"))
            return null;
        return super.signUp(type, username, password);
    }
}
