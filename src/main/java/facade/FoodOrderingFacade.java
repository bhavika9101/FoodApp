package facade;

import dao.MenuCategoryDAO;
import dao.MenuItemDAO;
import exception.RestaurantClosedException;
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
import util.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        this.orderService = new OrderService();
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
        MenuCategoryDAO menuCategoryDAO = new MenuCategoryDAO();
        MenuItemDAO menuItemDAO = new MenuItemDAO();

        try (ResultSet rs = menuCategoryDAO.getAll()) {
            if (rs.next()) {
                loadMenuFromDB(menuCategoryDAO, menuItemDAO);
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error checking menu: " + e.getMessage());
        }


        try {
            int fastFoodId = menuCategoryDAO.insert("Fast Food", null);
            menuItemDAO.insert("Pizza", 250.0, fastFoodId);
            menuItemDAO.insert("Burger", 150.0, fastFoodId);
            menuItemDAO.insert("French Fries", 100.0, fastFoodId);
            menuItemDAO.insert("Sandwich", 120.0, fastFoodId);

            int beverageId = menuCategoryDAO.insert("Beverages", null);
            menuItemDAO.insert("Tea", 30.0, beverageId);
            menuItemDAO.insert("Coffee", 60.0, beverageId);
            menuItemDAO.insert("Cold Coffee", 90.0, beverageId);
            menuItemDAO.insert("Fresh Juice", 80.0, beverageId);

            int mainCourseId = menuCategoryDAO.insert("Main Course", null);
            menuItemDAO.insert("Biryani", 220.0, mainCourseId);
            menuItemDAO.insert("Dal Rice", 150.0, mainCourseId);
            menuItemDAO.insert("Paneer Tikka", 180.0, mainCourseId);

            System.out.println("Default menu seeded into database.");
        } catch (SQLException e) {
            System.out.println("Error seeding menu: " + e.getMessage());
        }

        loadMenuFromDB(menuCategoryDAO, menuItemDAO);
    }

    private void loadMenuFromDB(MenuCategoryDAO menuCategoryDAO, MenuItemDAO menuItemDAO) {
        try {
            MenuComponent rootMenu = new MenuCategory(0, "--- FOOD MENU ---");

            try (ResultSet catRs = menuCategoryDAO.getAll()) {
                while (catRs.next()) {
                    int catId = catRs.getInt("category_id");
                    String catName = catRs.getString("category_name");
                    MenuComponent category = new MenuCategory(catId, catName);

                    try (ResultSet itemRs = menuItemDAO.getAllWithCategory()) {
                        while (itemRs.next()) {
                            if (itemRs.getInt("category_id") == catId) {
                                category.add(new MenuItem(
                                        itemRs.getInt("item_id"),
                                        itemRs.getString("item_name"),
                                        itemRs.getDouble("item_price")));
                            }
                        }
                    }
                    rootMenu.add(category);
                }
            }

            adminService.setMenu(rootMenu);
            System.out.println("Menu loaded from database.");
        } catch (SQLException e) {
            System.out.println("Error loading menu: " + e.getMessage());
        }
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
                    try {
                        runCustomerPanel();
                    } catch (RestaurantClosedException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case "3":
                    try {
                        runDeliveryAgentPanel();
                    } catch (RestaurantClosedException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("\nThank you for using the Food Ordering System. Goodbye!");
                    DBConnection.closeConnection();
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

    private void runCustomerPanel() throws RestaurantClosedException {
        if (!adminPanel.isAdminLoggedIn()) {
            throw new RestaurantClosedException("Admin must log in first before customers can use the system.");
        }
        Boolean backToMain = Boolean.FALSE;
        while (!backToMain) {
            backToMain = customerPanel.run();
        }
    }

    private void runDeliveryAgentPanel() throws RestaurantClosedException {
        if (!adminPanel.isAdminLoggedIn()) {
            throw new RestaurantClosedException("Admin must log in first before delivery agents can use the system.");
        }
        Boolean backToMain = Boolean.FALSE;
        while (!backToMain) {
            backToMain = deliveryAgentPanel.run();
        }
    }
}
