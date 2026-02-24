package service;

import model.user.User;

public class AdminService extends BaseService{
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("admin")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password);
    }
}
