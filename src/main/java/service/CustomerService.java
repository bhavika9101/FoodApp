package service;

import model.user.User;
import model.user.Customer;

public class CustomerService extends BaseService {
    @Override
    public User signUp(String type, String username, String password) {
        if (!type.equalsIgnoreCase("customer")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password);
    }

    public Customer getCustomerByUsername(String username) {
        User user = getUserByUsername(username);
        if (user instanceof Customer) {
            return (Customer) user;
        }
        return null;
    }
}
