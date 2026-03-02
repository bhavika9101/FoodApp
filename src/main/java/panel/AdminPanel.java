package panel;


import exception.UserNotFoundException;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.user.Admin;
import model.user.Customer;
import model.user.DeliveryAgent;
import model.user.User;
import service.AdminService;
import service.CustomerService;
import service.DeliveryAgentService;
import service.DiscountService;

import service.OrderService;

import java.util.*;

public class AdminPanel {
    private final AdminService adminService;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final DeliveryAgentService deliveryAgentService;
    private final Scanner scanner;
    private Admin loggedInAdmin;
    private Boolean adminCreated;

    public AdminPanel(AdminService adminService, OrderService orderService,
            CustomerService customerService, DeliveryAgentService deliveryAgentService,
            Scanner scanner) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.customerService = customerService;
        this.deliveryAgentService = deliveryAgentService;
        adminCreated = false;
        this.scanner = scanner;
    }

    public Boolean run() {
        System.out.println("\n============================================");
        System.out.println("          ADMIN PANEL");
        System.out.println("============================================");

        if (loggedInAdmin == null) {
            System.out.println("Admin is not logged in.");
            System.out.println("1. Login");
            if(!adminCreated)
                System.out.println("2. Sign Up");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    if(adminCreated){
                        System.out.println("Admin signed up already. Please log in.");
                        break;
                    }
                    adminCreated = signUp();
                    break;
                case "0":
                    return Boolean.TRUE;
                default:
                    System.out.println("Invalid choice.");
            }
            return Boolean.FALSE;
        }

        System.out.println("Logged in as: " + loggedInAdmin.getUsername());
        System.out.println("""
                1. Manage menu
                2. Manage discounts
                3. Manage orders
                4. Manage profiles
                5. Finances
                6. Logout
                0. Back to main menu
                """);
        System.out.println("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1":
                manageMenu();
                break;
            case "2":
                discountsMenu();
                break;
            case "3":
                ordersMenu();
                break;
            case "4":
                profilesMenu();
                break;
            case "5":
                financeMenu();
                break;
            case "6":
                logout();
                break;
            case "0":
                return true;
            default:
                System.out.println("Invalid choice.");
        }
        return Boolean.FALSE;
    }

    private void manageMenu() {
        while (true){
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE MENU");
            System.out.println("============================================");
            System.out.println("1. View Menu");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Add Category");
            System.out.println("0. Back to admin menu");
            System.out.println("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    viewMenu();
                    break;
                case "2":
                    addMenuItem();
                    break;
                case "3":
                    addCategory();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private void discountsMenu() {

        while (true){
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE DISCOUNTS");
            System.out.println("============================================");
            System.out.println("1. View discounts");
            System.out.println("2. Add discount");
            System.out.println("3. Remove discount");
            System.out.println("0. Back to admin menu");
            System.out.println("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    viewDiscounts();
                    break;
                case "2":
                    addDiscount();
                    break;
                case "3":
                    removeDiscount();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private void ordersMenu() {

        while (true){
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE ORDERS");
            System.out.println("============================================");
            System.out.println("1. View Pending Orders");
            System.out.println("2. Approve Order and add to Queue");
            System.out.println("3. View Order queue");
            System.out.println("4. View All Order History");
            System.out.println("5. View an order");
            System.out.println("0. Back to main menu");
            System.out.println("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    viewPendingOrders();
                    break;
                case "2":
                    approveOrder();
                    break;
                case "3":
                    viewDeliveryQueue();
                    break;
                case "4":
                    viewAllOrderHistory();
                    break;
                case "5":
                    viewOrderDetails();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private void profilesMenu() {
        while (true){
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE PROFILES");
            System.out.println("============================================");
            System.out.println("1. View All Profiles");
            System.out.println("2. View a Profile");
            System.out.println("3. Edit admin profile");
            System.out.println("0. Back to main menu");
            System.out.println("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    viewAllProfiles();
                    break;
                case "2":
                    try {
                        viewAProfile();
                    } catch (UserNotFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case "3":
                    editAdminProfile();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private void financeMenu() {
        while (true){
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE FINANCE");
            System.out.println("============================================");
            System.out.println("1. View revenue");
            System.out.println("2. Manage Delivery partner's finance");
            System.out.println("0. Back to main menu");
            System.out.println("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    viewRevenue();
                    break;
                case "2":
                    manageDeliveryAgentFinance();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void login() {
        if(!adminCreated){
            System.out.println("Create admin first to login.");
            return;
        }
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = null;
        try {
            user = adminService.login(username, password);
        }catch (UserNotFoundException e){
            System.out.println(e.getMessage());
        }
        if (user instanceof Admin) {
            loggedInAdmin = (Admin) user;
            System.out.println("Admin logged in successfully!");
        }
    }

    private boolean signUp() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        User user = adminService.signUp("admin", username, password, phone);
        if (user instanceof Admin) {
            loggedInAdmin = (Admin) user;
            System.out.println("Admin account created and logged in!");
            return true;
        }
        return false;
    }

    private void logout() {
        adminService.logout(loggedInAdmin.getUsername());
        loggedInAdmin = null;
    }

    private void viewMenu() {
        MenuComponent menu = adminService.getMenu();
        if (menu != null) {
            menu.print();
        } else {
            System.out.println("Menu not initialized.");
        }
    }

    private void addMenuItem() {
        displayCategoryList();
        System.out.print("Category ID: ");
        Integer categoryId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Item name: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Item price: ");
        Double price = Double.parseDouble(scanner.nextLine().trim());
        MenuItem item = new MenuItem(itemName, price);
        adminService.addMenuItemToCategory(categoryId, item);
    }

    private void addCategory() {
        System.out.print("New category name: ");
        String categoryName = scanner.nextLine().trim();
        adminService.addCategory(categoryName);
    }

    private void viewDiscounts() {
        DiscountService.printAllDiscounts();
    }

    private void addDiscount() {
        System.out.print("Price threshold (Rs): ");
        Double threshold = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Discount percentage: ");
        Double percentage = Double.parseDouble(scanner.nextLine().trim());
        Double rate = percentage / 100.0;
        adminService.addDiscount(threshold, rate);
    }

    private void removeDiscount() {
        System.out.print("Price threshold to remove: ");
        Double threshold = Double.parseDouble(scanner.nextLine().trim());
        adminService.removeDiscount(threshold);
    }

    private void viewPendingOrders() {
        List<Order> pendingOrders = adminService.viewPendingOrders();
        List<Order> approvedOrders = adminService.viewApprovedOrders();

        if (pendingOrders.isEmpty() && approvedOrders.isEmpty()) {
            System.out.println("No pending or approved orders.");
            return;
        }
        if (!pendingOrders.isEmpty()) {
            System.out.println("\n--- PLACED Orders ---");
            for (Order order : pendingOrders) {
                printOrderSummary(order);
            }
        }
        if (!approvedOrders.isEmpty()) {
            System.out.println("\n--- APPROVED Orders ---");
            for (Order order : approvedOrders) {
                printOrderSummary(order);
            }
        }
    }

    private void printOrderSummary(Order order) {
        System.out.println("Order #" + order.getOrderId()
                + " | Customer: " + order.getCustomerName()
                + " | Amount: Rs." + String.format("%.2f", order.getFinalAmount())
                + " | Status: " + order.getStatus().getDisplayName());
    }

    private void approveOrder() {
        System.out.print("Enter Order ID to approve: ");
        Integer orderId = Integer.parseInt(scanner.nextLine().trim());
        adminService.approveOrder(orderId);
    }

    private void viewAllOrderHistory() {
        List<Order> allOrders = orderService.getAllOrders();
        if (allOrders.isEmpty()) {
            System.out.println("No orders in the system yet.");
            return;
        }
        System.out.println("\n=============================================");
        System.out.println("          ALL ORDER HISTORY");
        System.out.println("=============================================");
        System.out.printf("Total Orders: %d%n%n", allOrders.size());

        for (Order order : allOrders) {
            System.out.println("---------------------------------------------");
            System.out.println("Order #" + order.getOrderId());
            System.out.println("Customer      : " + order.getCustomerName());
            System.out.println("Address        : " + order.getCustomerAddress());
            System.out.println("Status         : " + order.getStatus().getDisplayName());
            System.out.println("Payment Mode   : "
                    + (order.getPaymentMode() != null ? order.getPaymentMode().getDisplayName() : "N/A"));
            System.out.println("Delivery Agent : "
                    + (order.getAssignedAgentName() != null ? order.getAssignedAgentName() : "Not Assigned"));

            System.out.println("  Items:");
            for (Map.Entry<MenuItem, Integer> entry : order.getItems().entrySet()) {
                MenuItem item = entry.getKey();
                Integer qty = entry.getValue();
                System.out.printf("    - %-20s x%-3d = Rs.%.2f%n",
                        item.getName(), qty, item.getPrice() * qty);
            }
            System.out.printf("  Subtotal       : Rs.%.2f%n", order.getSubtotal());
            System.out.printf("  Discount       : Rs.%.2f%n", order.getDiscountAmount());
            System.out.printf("  Final Amount   : Rs.%.2f%n", order.getFinalAmount());
        }
        System.out.println("=============================================");
    }
    private void viewAllProfiles() {
        System.out.println("\n=============================================");
        System.out.println("           ALL USER PROFILES");
        System.out.println("=============================================");

        System.out.println("\n--- ADMIN ---");
        if (adminService.getAllUserMap().isEmpty()) {
            System.out.println("No admin users.");
        } else {
            java.util.Set<User> uniqueAdmins = new java.util.LinkedHashSet<>(adminService.getAllUserMap().values());
            for (User user : uniqueAdmins) {
                System.out.printf("ID: %-5d | Username: %-15s | Phone Number: %-15s | Status: %s%n",
                        user.getUserId(), user.getUsername(),
                        user.getPhoneNumber(),
                        adminService.findLoggedInUser(user.getUsername()) != null ? "Online" : "Offline");
            }
        }

        System.out.println("\n--- CUSTOMERS ---");
        if (customerService.getAllUserMap().isEmpty()) {
            System.out.println("No customers registered.");
        } else {
            for (User user : customerService.getAllUserMap().values()) {
                Customer customer = (Customer) user;
                Integer orderCount = orderService.getOrdersByCustomerId(customer.getUserId()).size();
                System.out.printf("ID: %-5d | Username: %-15s |  Phone Number: %-15s | Address: %-20s | Orders: %d | Status: %s%n",
                        customer.getUserId(), customer.getUsername(),
                        customer.getPhoneNumber(),
                        customer.getAddress() != null ? customer.getAddress() : "N/A",
                        orderCount,
                        customerService.findLoggedInUser(customer.getUsername()) != null ? "Online" : "Offline");
            }
        }

        System.out.println("\n--- DELIVERY AGENTS ---");
        if (deliveryAgentService.getAllUserMap().isEmpty()) {
            System.out.println("No delivery agents registered.");
        } else {
            for (User user : deliveryAgentService.getAllUserMap().values()) {
                DeliveryAgent agent = (DeliveryAgent) user;
                System.out.printf(
                        "ID: %-5d | Username: %-15s | Phone Number: %-15s | Delivery Status: %-12s | Current Order: %s | Status: %s%n",
                        agent.getUserId(), agent.getUsername(),
                        agent.getPhoneNumber(),
                        agent.getStatus().getDisplayName(),
                        agent.getCurrentOrderId() != null ? "#" + agent.getCurrentOrderId() : "None",
                        deliveryAgentService.findLoggedInUser(agent.getUsername()) != null ? "Online" : "Offline");
            }
        }
        System.out.println("\n=============================================");
    }

    public Boolean isAdminLoggedIn() {
        return loggedInAdmin != null;
    }

//    yet to test
    private void viewDeliveryQueue(){
        Queue<Integer> deliveryQueue = adminService.getDeliveryQueue();
        if(deliveryQueue.isEmpty()){
            System.out.println("No orders in queue.");
            return;
        }
        for(Integer orderId: deliveryQueue){
            System.out.printf("%-5s %-20s %-50s %-10s %-5s", "ID", "Customer", "Address", "Amount", "Payment Mode\n");
            System.out.println(orderService.getOrderInfo(orderId));
        }
    }

//    yet to test
    private void viewOrderDetails(){
        System.out.print("Enter order id: ");
        Integer orderId = Integer.parseInt(scanner.nextLine().trim());
        String details = orderService.getOrderDetails(orderId);
        if(details == null){
            System.out.println("No such order");
            return;
        }
        System.out.println(details);
    }
//incomplete
    private void viewAProfile() throws UserNotFoundException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        adminService.printProfile(username);
    }
    private void viewRevenue(){
        System.out.println("Total orders: " + orderService.getAllOrders().size());
        System.out.println("Total Revenue: " + adminService.getRevenue());
    }

    private void displayCategoryList() {
        List<MenuComponent> categories =
                adminService.getCategoryList(adminService.getMenu());
        int totalWidth = 40;
        System.out.println("+" + "-".repeat(totalWidth - 2) + "+");
        System.out.printf("|%"+(totalWidth-2)+"s|\n", " CATEGORY LIST ");
        System.out.println("+" + "-".repeat(totalWidth - 2) + "+");

        System.out.printf("| %-6s | %-27s |\n", "ID", "Name");
        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(29) + "+");

        for (MenuComponent category : categories) {
            System.out.printf("| %-6s | %-27s |\n",
                    category.getId(),
                    category.getName());
        }

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(29) + "+");
    }
    private void manageDeliveryAgentFinance(){
        System.out.println("Username: ");
        DeliveryAgent agent = deliveryAgentService.getAgentByUsername(scanner.nextLine().trim());
        if(agent == null){
            System.out.println("No such agent in system.");
            return;
        }
        Map<String, Double> financeInfo = deliveryAgentService.getDeliveryAgentFinanceInfo(agent);
        System.out.println("Current base salary: " + financeInfo.get("base_salary"));
        System.out.println("Current commission rate: " + financeInfo.get("commission_rate"));

        System.out.println("Base salary: ");
        deliveryAgentService.setDeliveryAgentBaseSalary(agent, Double.parseDouble(scanner.nextLine().trim()));
        System.out.println("Commission rate: ");
        deliveryAgentService.setDeliveryAgentCommissionRate(agent, Double.parseDouble(scanner.nextLine().trim()));
    }

    private void editAdminProfile(){
        System.out.println("Enter current password: ");
        String password = scanner.nextLine().trim();
        if(!loggedInAdmin.getPassword().equals(password)){
            System.out.println("Wrong password. Attempt to edit profile failed.");
            return;
        }
        System.out.println("New username (enter 0 to keep old one): ");
        String username = scanner.nextLine().trim();
        System.out.println("New password (enter 0 to keep old one): ");
        password = scanner.nextLine().trim();
        System.out.println("New phone number (enter 0 to keep old one): ");
        String phoneNumber = scanner.nextLine().trim();

        Map<String, String> updateInfo = new HashMap<>();
        updateInfo.put("username", username);
        updateInfo.put("password", password);
        updateInfo.put("phone_number", phoneNumber);

        adminService.editUserProfile(loggedInAdmin, updateInfo);
    }
}
