package facade;

import model.order.MenuCategory;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.payment.Discount;
import observer.AdminObserver;
import observer.EventManager;
import panel.AdminPanel;
import panel.CustomerPanel;
import panel.DeliveryAgentPanel;
import service.*;

import java.util.Scanner;

public class FoodOrderingFacade {
    private final AdminService adminService;
    private final CustomerService customerService;
    private final DeliveryAgentService deliveryAgentService;
    private final OrderService orderService;
    private final EventManager eventManager;

    private final AdminPanel adminPanel;
    private final CustomerPanel customerPanel;
    private final DeliveryAgentPanel deliveryAgentPanel;

    private final Scanner scanner;

    public FoodOrderingFacade() {
        this.scanner = new Scanner(System.in);
        this.eventManager = new EventManager();
        this.deliveryAgentService = new DeliveryAgentService();
        this.orderService = new OrderService(eventManager);
        this.customerService = new CustomerService();
        this.adminService = new AdminService(orderService, deliveryAgentService);

        initializeMenu();
        initializeDiscounts();

        AdminObserver adminObserver = new AdminObserver("Admin");
        eventManager.subscribe("ORDER_PLACED", adminObserver);
        eventManager.subscribe("ORDER_DELIVERED", adminObserver);

        this.adminPanel = new AdminPanel(adminService, orderService, customerService, deliveryAgentService, scanner);
        this.customerPanel = new CustomerPanel(customerService, adminService, orderService, eventManager, scanner);
        this.deliveryAgentPanel = new DeliveryAgentPanel(deliveryAgentService, orderService, adminService, eventManager,
                scanner);
    }

    private void initializeMenu() {
        MenuComponent pizza = new MenuItem("Pizza", 250.0);
        MenuComponent burger = new MenuItem("Burger", 150.0);
        MenuComponent fries = new MenuItem("French Fries", 100.0);
        MenuComponent sandwich = new MenuItem("Sandwich", 120.0);
        MenuComponent fastFood = new MenuCategory("Fast Food");
        fastFood.add(pizza);
        fastFood.add(burger);
        fastFood.add(fries);
        fastFood.add(sandwich);

        MenuComponent tea = new MenuItem("Tea", 30.0);
        MenuComponent coffee = new MenuItem("Coffee", 60.0);
        MenuComponent coldCoffee = new MenuItem("Cold Coffee", 90.0);
        MenuComponent juice = new MenuItem("Fresh Juice", 80.0);
        MenuComponent beverage = new MenuCategory("Beverages");
        beverage.add(tea);
        beverage.add(coffee);
        beverage.add(coldCoffee);
        beverage.add(juice);

        MenuComponent biryani = new MenuItem("Biryani", 220.0);
        MenuComponent dalRice = new MenuItem("Dal Rice", 150.0);
        MenuComponent paneerTikka = new MenuItem("Paneer Tikka", 180.0);
        MenuComponent mainCourse = new MenuCategory("Main Course");
        mainCourse.add(biryani);
        mainCourse.add(dalRice);
        mainCourse.add(paneerTikka);

        MenuComponent menu = new MenuCategory("--- FOOD MENU ---");
        menu.add(fastFood);
        menu.add(beverage);
        menu.add(mainCourse);

        adminService.setMenu(menu);
    }

    private void initializeDiscounts() {
        DiscountService.add(new Discount(500.0, 0.10));
        DiscountService.add(new Discount(1000.0, 0.15));
        DiscountService.add(new Discount(2000.0, 0.20));
    }

    public void run() {
        System.out.println("================================================");
        System.out.println("     WELCOME TO FOOD ORDERING SYSTEM");
        System.out.println("================================================");
        System.out.println("Default discounts loaded:");
        System.out.println("    10% off on orders above Rs.500");
        System.out.println("    15% off on orders above Rs.1000");
        System.out.println("    20% off on orders above Rs.2000");
        System.out.println("================================================\n");

        while (true) {
            System.out.println("--------------------------------------------");
            System.out.println("           MAIN MENU");
            System.out.println("--------------------------------------------");
            System.out.println("1. Admin Panel");
            System.out.println("2. Customer Panel");
            System.out.println("3. Delivery Agent Panel");
            System.out.println("4. Exit");
            System.out.println("--------------------------------------------");
            System.out.print("Choose panel: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runAdminPanel();
                    break;
                case "2":
                    runCustomerPanel();
                    break;
                case "3":
                    runDeliveryAgentPanel();
                    break;
                case "4":
                    System.out.println("\nThank you for using the Food Ordering System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void runAdminPanel() {
        Boolean backToMain = Boolean.FALSE;
        while (!backToMain) {
            backToMain = adminPanel.run();
        }
    }

    private void runCustomerPanel() {
        if (!adminPanel.isAdminLoggedIn()) {
            System.out.println("\n[ERROR] Admin must log in first before customers can use the system.");
            return;
        }
        Boolean backToMain = Boolean.FALSE;
        while (!backToMain) {
            backToMain = customerPanel.run();
        }
    }

    private void runDeliveryAgentPanel() {
        if (!adminPanel.isAdminLoggedIn()) {
            System.out.println("\n[ERROR] Admin must log in first before delivery agents can use the system.");
            return;
        }
        Boolean backToMain = Boolean.FALSE;
        while (!backToMain) {
            backToMain = deliveryAgentPanel.run();
        }
    }
}
