import model.*;
import service.AdminService;
import service.BaseService;
import service.CustomerService;
import service.DeliveryAgentService;

public class Main {
    public static void main(String[] args) {
        AdminService adminService= new AdminService();
        adminService.signUp("ADMIN", "BHAVIKA", "CHHATBAR");
        adminService.printProfile(1);

        CustomerService customerService = new CustomerService();
        customerService.signUp("customer", "Rahul", "Dave");
        customerService.printProfile(2);

        DeliveryAgentService deliveryAgentService = new DeliveryAgentService();
        deliveryAgentService.signUp("delivery_agent", "Priyank", "Jakhria");
        deliveryAgentService.printProfile(3);

//        User admin = UserFactory.createUser("admin", "Bhavika", "Chhatbar");
//        User customer = UserFactory.createUser("customer", "Rahul", "Dave");
//        User agent = UserFactory.createUser("delivery_agent", "Priyank", "Jakhria");
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
