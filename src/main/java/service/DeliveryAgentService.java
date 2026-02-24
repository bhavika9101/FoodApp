package service;

import model.User;

public class DeliveryAgentService extends BaseService{
    static private Integer deliveryAgentCount = 0;
    static private Integer deliveryAgentCountLimit = 2;
    @Override
    public User signUp(String type, String username, String password) {
        if(!type.equalsIgnoreCase("delivery_agent")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        if(deliveryAgentCount.equals(deliveryAgentCountLimit)){
            System.out.println("Can't create more than two Delivery agent.");
            return null;
        }
        User user = super.signUp(type, username, password);
        if(user == null)
            return null;
        deliveryAgentCount++;
        return user;
    }
}
