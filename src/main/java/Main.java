import model.order.MenuCategory;
import model.order.MenuComponent;
import model.order.MenuItem;

public class Main {
    public static void main(String[] args) {

        MenuComponent pizza = new MenuItem("pizza", 100.0);
        MenuComponent burger = new MenuItem("burger", 50.0);
        MenuComponent fastFood = new MenuCategory("Fast food");
        fastFood.add(pizza);
        fastFood.add(burger);

        MenuComponent tea = new MenuItem("tea", 20.0);
        MenuComponent coffee = new MenuItem("coffee", 120.0);
        MenuComponent beverage = new MenuCategory("Beverages");
        beverage.add(tea);
        beverage.add(coffee);

        MenuComponent menu = new MenuCategory("Menu");
        menu.add(fastFood);
        menu.add(beverage);

        menu.print();


//        AdminService deliveryAgentService = new AdminService();
//        deliveryAgentService.signUp("admin","Bhavika", "bhavika");
//        deliveryAgentService.logout("Bhavika");
//        deliveryAgentService.printProfile("Bhavika");
//        deliveryAgentService.signUp("admin","Akshay", "akshay");
//        deliveryAgentService.printProfile("Akshay");





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
