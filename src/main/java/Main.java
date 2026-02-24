import model.*;
import service.AdminService;
import service.BaseService;
import service.CustomerService;
import service.DeliveryAgentService;

public class Main {
    public static void main(String[] args) {
        AdminService deliveryAgentService = new AdminService();
        deliveryAgentService.signUp("admin","Bhavika", "bhavika");
        deliveryAgentService.logout("Bhavika");
        deliveryAgentService.printProfile("Bhavika");
        deliveryAgentService.signUp("admin","Akshay", "akshay");
        deliveryAgentService.printProfile("Akshay");




//        User admin = UserFactory.createUser("admin", "Bhavika", "Chhatbar");
//
//
//        System.out.println(admin.getUsername());
//        System.out.println(admin.getPassword())
//
//        System.out.println(customer.getUsername());
//        System.out.println(customer.getPassword());
//
//        System.out.println(agent.getUsername());
//        System.out.println(agent.getPassword());
    }
}
