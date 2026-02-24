package service;

import model.User;

public class AdminService extends BaseService{
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("admin"))
            return null;
        return super.signUp(type, username, password);
    }
}
