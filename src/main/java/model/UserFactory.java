package model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class UserFactory {
    private static final Map<String, BiFunction<String,String,User>> registry = new HashMap<>();

    static {
        registry.put("ADMIN", Admin::new);
        registry.put("CUSTOMER", Customer::new);
        registry.put("DELIVERY_AGENT", DeliveryAgent::new);
    }

//    add limit of two for agent
    public static User createUser(String type, String username, String password){
        BiFunction<String, String,User> constructor = registry.get(type);
//        handler constructor null
        return constructor.apply(username, password);
    }

//    ocp maintained
    public static void registerNewUserType(String type, BiFunction<String,String,User> constructor){
        registry.put(type, constructor);
    }
}
