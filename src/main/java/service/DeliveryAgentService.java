package service;

import model.User;

public class DeliveryAgentService extends BaseService{
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("delivery_agent"))
            return null;
        return super.signUp(type, username, password);
    }
}
