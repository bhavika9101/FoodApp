package panel;

import model.enums.DeliveryAgentStatus;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.user.Admin;
import model.user.User;
import service.AdminService;
import service.CustomerService;
import service.DeliveryAgentService;
import service.DiscountService;
import service.OrderService;
import service.InvoiceService;

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
            if (!adminCreated)
                System.out.println("2. Sign Up");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    if (adminCreated) {
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
                4. Finances
                5. Manage Profiles
                6. Edit Profile
                7. Logout
                0. Back to main menu
                """);
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
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
                financeMenu();
                break;
            case "5":
                profilesMenu();
                break;
            case "6":
                editAdminProfile();
                break;
            case "7":
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
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE MENU");
            System.out.println("============================================");
            System.out.println("1. View Menu");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Add Category");
            System.out.println("0. Back to admin menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
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
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE DISCOUNTS");
            System.out.println("============================================");
            System.out.println("1. View discounts");
            System.out.println("2. Add discount");
            System.out.println("3. Remove discount");
            System.out.println("0. Back to admin menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
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
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE ORDERS");
            System.out.println("============================================");
            System.out.println("1. View Pending Orders");
            System.out.println("2. Approve Order and add to Queue");
            System.out.println("3. View Order queue");
            System.out.println("4. View All Order History");
            System.out.println("5. View an order (Invoice)");
            System.out.println("0. Back to admin menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
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

    private void financeMenu() {
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE FINANCE");
            System.out.println("============================================");
            System.out.println("1. View revenue");
            System.out.println("2. Manage Delivery partner's finance");
            System.out.println("0. Back to admin menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
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
        // if (!adminCreated) {
        // System.out.println("Create admin first to login.");
        // return;
        // }
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = adminService.login(username, password);
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
        adminService.logout(loggedInAdmin);
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

            Map<MenuItem, Integer> items = orderService.getOrderItems(order.getOrderId());
            System.out.println("  Items:");
            for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
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

    public Boolean isAdminLoggedIn() {
        return loggedInAdmin != null;
    }

    private void viewDeliveryQueue() {
        Queue<Integer> deliveryQueue = adminService.getDeliveryQueue();
        if (deliveryQueue.isEmpty()) {
            System.out.println("No orders in queue.");
            return;
        }
        System.out.printf("%-8s %-20s %-30s %-12s%n", "ID", "Customer", "Address", "Amount");
        System.out.println("-".repeat(70));
        for (Integer orderId : deliveryQueue) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                System.out.printf("%-8d %-20s %-30s Rs.%.2f%n",
                        order.getOrderId(), order.getCustomerName(),
                        order.getCustomerAddress(), order.getFinalAmount());
            }
        }
    }

    private void viewOrderDetails() {
        System.out.print("Enter order id: ");
        Integer orderId = Integer.parseInt(scanner.nextLine().trim());
        InvoiceService invoiceService = new InvoiceService(orderService);
        invoiceService.printInvoice(orderId);
    }

    private void viewRevenue() {
        System.out.println("Total orders: " + orderService.getAllOrders().size());
        System.out.printf("Total Revenue: Rs.%.2f%n", adminService.getRevenue());
    }

    private void displayCategoryList() {
        List<MenuComponent> categories = adminService.getCategoryList(adminService.getMenu());
        int totalWidth = 40;
        System.out.println("+" + "-".repeat(totalWidth - 2) + "+");
        System.out.printf("|%" + (totalWidth - 2) + "s|%n", " CATEGORY LIST ");
        System.out.println("+" + "-".repeat(totalWidth - 2) + "+");

        System.out.printf("| %-6s | %-27s |%n", "ID", "Name");
        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(29) + "+");

        for (MenuComponent category : categories) {
            System.out.printf("| %-6s | %-27s |%n",
                    category.getId(),
                    category.getName());
        }

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(29) + "+");
    }

    private void manageDeliveryAgentFinance() {
        System.out.print("Enter Delivery Agent ID: ");
        int agentId = Integer.parseInt(scanner.nextLine().trim());

        Map<String, Double> financeInfo = deliveryAgentService.getDeliveryAgentFinancials(agentId);
        if (financeInfo.isEmpty()) {
            System.out.println("Agent not found.");
            return;
        }
        System.out.println("Current base salary: Rs." + financeInfo.get("base_salary"));
        System.out.println("Current commission rate: " + (financeInfo.get("commission_rate") * 100) + "%");

        System.out.print("New base salary (Rs): ");
        Double base_salary = Double.parseDouble(scanner.nextLine().trim());
        if (base_salary >= 0)
            deliveryAgentService.setDeliveryAgentBaseSalary(agentId, base_salary);

        System.out.print("New commission rate: ");
        Double commission_rate = Double.parseDouble(scanner.nextLine().trim()) / 100;
        if (commission_rate >= 0)
            deliveryAgentService.setDeliveryAgentCommissionRate(agentId, commission_rate);
        financeInfo = deliveryAgentService.getDeliveryAgentFinancials(agentId);
        if (financeInfo.get("base_salary") > 0 || financeInfo.get("commission_rate") > 0) {
            deliveryAgentService.updateAgentStatus(agentId, DeliveryAgentStatus.AVAILABLE);
            System.out.println("Agent #" + agentId + " is now AVAILABLE.");
            adminService.processDeliveryQueue();
        }
    }

    private void editAdminProfile() {
        System.out.print("Enter current password: ");
        String password = scanner.nextLine().trim();
        if (!loggedInAdmin.getPassword().equals(password)) {
            System.out.println("Wrong password. Attempt to edit profile failed.");
            return;
        }
        System.out.print("New username (enter 0 to keep old one): ");
        String username = scanner.nextLine().trim();
        System.out.print("New password (enter 0 to keep old one): ");
        password = scanner.nextLine().trim();
        System.out.print("New phone number (enter 0 to keep old one): ");
        String phoneNumber = scanner.nextLine().trim();

        String newUsername = "0".equals(username) ? loggedInAdmin.getUsername() : username;
        String newPassword = "0".equals(password) ? loggedInAdmin.getPassword() : password;
        String newPhone = "0".equals(phoneNumber) ? loggedInAdmin.getPhoneNumber() : phoneNumber;

        adminService.editUserProfile(loggedInAdmin, newUsername, newPassword, newPhone);
    }

    private void profilesMenu() {
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          ADMIN PANEL - MANAGE PROFILES");
            System.out.println("============================================");
            System.out.println("1. View All Customers");
            System.out.println("2. View All Delivery Agents");
            System.out.println("3. View a Profile (by username)");
            System.out.println("0. Back to admin menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAllProfiles("CUSTOMER");
                    break;
                case "2":
                    viewAllProfiles("DELIVERY_AGENT");
                    break;
                case "3":
                    viewProfileByUsername();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllProfiles(String userType) {
        List<Map<String, String>> users = adminService.getAllUsersByType(userType);
        if (users.isEmpty()) {
            System.out.println("No " + userType.toLowerCase().replace('_', ' ') + "s found.");
            return;
        }
        System.out.printf("\n%-8s %-20s %-15s%n", "ID", "Username", "Phone");
        System.out.println("-".repeat(45));
        for (Map<String, String> user : users) {
            System.out.printf("%-8s %-20s %-15s%n",
                    user.get("user_id"), user.get("username"), user.get("phone_number"));
        }
    }

    private void viewProfileByUsername() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        for (String type : new String[] { "CUSTOMER", "DELIVERY_AGENT", "ADMIN" }) {
            List<Map<String, String>> users = adminService.getAllUsersByType(type);
            for (Map<String, String> user : users) {
                if (user.get("username").equalsIgnoreCase(username)) {
                    System.out.println("\n--- User Profile ---");
                    System.out.println("User ID  : " + user.get("user_id"));
                    System.out.println("Username : " + user.get("username"));
                    System.out.println("Phone    : " + user.get("phone_number"));
                    System.out.println("Type     : " + type.toLowerCase().replace('_', ' '));
                    return;
                }
            }
        }
        System.out.println("User '" + username + "' not found.");
    }
}
