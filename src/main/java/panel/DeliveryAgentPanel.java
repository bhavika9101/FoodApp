package panel;

import dao.DeliveryAgentDAO;
import model.enums.DeliveryAgentStatus;
import model.enums.OrderStatus;
import model.order.MenuItem;
import model.order.Order;
import model.user.DeliveryAgent;
import model.user.User;
import observer.DeliveryAgentObserver;
import observer.EventManager;
import service.AdminService;
import service.DeliveryAgentService;
import service.OrderService;

import java.sql.SQLException;
import java.util.*;

public class DeliveryAgentPanel {
    private final DeliveryAgentService deliveryAgentService;
    private final OrderService orderService;
    private final AdminService adminService;
    private final EventManager eventManager;
    private final Scanner scanner;
    private DeliveryAgent activeAgent;
    private final DeliveryAgentDAO deliveryAgentDAO;

    public DeliveryAgentPanel(DeliveryAgentService deliveryAgentService,
            OrderService orderService, AdminService adminService, EventManager eventManager, Scanner scanner) {
        this.deliveryAgentService = deliveryAgentService;
        this.orderService = orderService;
        this.adminService = adminService;
        this.eventManager = eventManager;
        this.scanner = scanner;
        this.deliveryAgentDAO = new DeliveryAgentDAO();
    }

    public Boolean run() {
        System.out.println("\n============================================");
        System.out.println("        DELIVERY AGENT PANEL");
        System.out.println("============================================");

        if (activeAgent == null) {
            int agentCount = deliveryAgentService.getAgentCount();
            if (agentCount < 2) {
                System.out.println("1. Sign Up");
            }
            System.out.println("2. Login");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (agentCount >= 2) {
                        System.out.println("Maximum delivery agents reached.");
                    } else {
                        signUp();
                    }
                    break;
                case "2":
                    login();
                    break;
                case "0":
                    return Boolean.TRUE;
                default:
                    System.out.println("Invalid choice.");
            }
            return Boolean.FALSE;
        }

        DeliveryAgentStatus freshStatus = deliveryAgentService.getAgentStatus(activeAgent.getUserId());
        activeAgent.setStatus(freshStatus);
        Integer freshOrderId = deliveryAgentService.getAgentCurrentOrderId(activeAgent.getUserId());
        activeAgent.setCurrentOrderId(freshOrderId);

        try {
            System.out.println("Active agent: " + activeAgent.getUsername()
                    + " | Status: " + deliveryAgentDAO.getStatus(activeAgent.getUserId()));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("1. View Assigned Order");
        System.out.println("2. Start Delivery (Pick Up Order)");
        System.out.println("3. Mark Order as Delivered");
        System.out.println("4. Show gross earning");
        System.out.println("5. Edit delivery agent profile");
        System.out.println("6. Logout");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewAssignedOrder();
                break;
            case "2":
                startDelivery();
                break;
            case "3":
                markDelivered();
                break;
            case "4":
                showGrossEarning();
                break;
            case "5":
                editDeliveryAgentProfile();
                break;
            case "6":
                logout();
                break;
            case "0":
                return Boolean.TRUE;
            default:
                System.out.println("Invalid choice.");
        }
        return Boolean.FALSE;
    }

    private void showGrossEarning() {
        double earnings = deliveryAgentService.getDeliveryAgentGrossEarning(activeAgent.getUserId());
        System.out
                .println("Gross earning for " + activeAgent.getUsername() + ": Rs." + String.format("%.2f", earnings));
    }

    private void signUp() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        User user = deliveryAgentService.signUp("delivery_agent", username, password, phone);
        if (user instanceof DeliveryAgent) {
            activeAgent = (DeliveryAgent) user;
            DeliveryAgentObserver observer = new DeliveryAgentObserver(activeAgent.getUserId(),
                    activeAgent.getUsername());
            eventManager.subscribe("ORDER_READY_FOR_DELIVERY", observer);

            System.out.println("Delivery agent account created and logged in!");
            adminService.processDeliveryQueue();
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = deliveryAgentService.login(username, password);
        if (user instanceof DeliveryAgent) {
            activeAgent = (DeliveryAgent) user;

            DeliveryAgentObserver observer = new DeliveryAgentObserver(activeAgent.getUserId(),
                    activeAgent.getUsername());
            eventManager.subscribe("ORDER_READY_FOR_DELIVERY", observer);

            System.out.println("Delivery agent logged in successfully!");
            adminService.processDeliveryQueue();
        }
    }

    private void logout() {
        deliveryAgentService.logout(activeAgent);
        activeAgent = null;
    }

    private void viewAssignedOrder() {
        if (activeAgent.getCurrentOrderId() == null) {
            System.out.println("No order currently assigned.");
            return;
        }
        Order order = orderService.getOrderById(activeAgent.getCurrentOrderId());
        if (order == null) {
            System.out.println("Assigned order not found.");
            return;
        }
        System.out.println("\n--- Assigned Order ---");
        System.out.println("Order #" + order.getOrderId());
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Address: " + order.getCustomerAddress());
        System.out.println("Status: " + order.getStatus().getDisplayName());
        System.out.printf("Amount: Rs.%.2f%n", order.getFinalAmount());

        Map<MenuItem, Integer> items = orderService.getOrderItems(order.getOrderId());
        System.out.println("Items:");
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            System.out.printf("  %-20s x%d%n", entry.getKey().getName(), entry.getValue());
        }
    }

    private void startDelivery() {
        if (activeAgent.getCurrentOrderId() == null) {
            System.out.println("No order assigned to start delivery.");
            return;
        }
        Order order = orderService.getOrderById(activeAgent.getCurrentOrderId());
        if (order == null || order.getStatus() != OrderStatus.READY_FOR_DELIVERY) {
            System.out.println("Order is not ready for delivery.");
            return;
        }
        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.OUT_FOR_DELIVERY);
        System.out.println("Delivery started for Order #" + order.getOrderId());

        eventManager.notifyObservers("ORDER_OUT_FOR_DELIVERY", order);
    }

    private void markDelivered() {
        if (activeAgent.getCurrentOrderId() == null) {
            System.out.println("No order to mark as delivered.");
            return;
        }
        Order order = orderService.getOrderById(activeAgent.getCurrentOrderId());
        if (order == null || order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            System.out.println("Order is not out for delivery.");
            return;
        }
        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED);

        Map<String, Double> financials = deliveryAgentService.getDeliveryAgentFinancials(activeAgent.getUserId());
        double commissionRate = financials.getOrDefault("commission_rate", 0.0);
        double commission = order.getFinalAmount() * commissionRate;
        if (commission > 0) {
            deliveryAgentService.payDeliveryAgent(activeAgent.getUserId(), commission);
        }

        deliveryAgentService.updateAgentStatus(activeAgent.getUserId(), DeliveryAgentStatus.AVAILABLE);
        activeAgent.setStatus(DeliveryAgentStatus.AVAILABLE);
        activeAgent.setCurrentOrderId(null);

        System.out.println("Order #" + order.getOrderId() + " marked as delivered!");
        if (commission > 0) {
            System.out.printf("Commission earned: Rs.%.2f%n", commission);
        }

        eventManager.notifyObservers("ORDER_DELIVERED", order);

        adminService.processDeliveryQueue();
    }

    private void editDeliveryAgentProfile() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();
        if (!activeAgent.getPassword().equals(currentPassword)) {
            System.out.println("Wrong password. Attempt to edit profile failed.");
            return;
        }
        System.out.print("New username (enter 0 to keep old one): ");
        String username = scanner.nextLine().trim();
        System.out.print("New password (enter 0 to keep old one): ");
        String password = scanner.nextLine().trim();
        System.out.print("New phone number (enter 0 to keep old one): ");
        String phoneNumber = scanner.nextLine().trim();

        String newUsername = "0".equals(username) ? activeAgent.getUsername() : username;
        String newPassword = "0".equals(password) ? activeAgent.getPassword() : password;
        String newPhone = "0".equals(phoneNumber) ? activeAgent.getPhoneNumber() : phoneNumber;

        deliveryAgentService.editUserProfile(activeAgent, newUsername, newPassword, newPhone);
    }
}
