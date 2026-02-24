package model.user;

import model.user.BaseUser;

public class DeliveryAgent extends BaseUser {
    public DeliveryAgent(){
        super();
    }
    public DeliveryAgent(String agentName, String password){
        super(agentName, password);
    }
}
