package panel;

import exception.UserNotFoundException;
import model.user.DeliveryAgent;
import model.user.User;
import observer.DeliveryAgentObserver;
import observer.EventManager;
import service.AdminService;
import service.DeliveryAgentService;
import service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeliveryAgentPanel {
    private final DeliveryAgentService deliveryAgentService;
    private final OrderService orderService;
    private final AdminService adminService;
    private final EventManager eventManager;
    private final Scanner scanner;
    private DeliveryAgent activeAgent;
    private Integer deliveryAgentCount;

    public DeliveryAgentPanel(DeliveryAgentService deliveryAgentService,
            OrderService orderService, AdminService adminService, EventManager eventManager, Scanner scanner) {
        this.deliveryAgentService = deliveryAgentService;
        this.orderService = orderService;
        this.adminService = adminService;
        this.eventManager = eventManager;
        this.deliveryAgentCount = 0;
        this.scanner = scanner;
    }

    public Boolean run() {
        System.out.println("\n============================================");
        System.out.println("        DELIVERY AGENT PANEL");
        System.out.println("============================================");

        if (activeAgent == null) {
            List<DeliveryAgent> loggedInAgents = getLoggedInAgents();
            if (!loggedInAgents.isEmpty()) {
                System.out.println("Logged-in agents:");
                for (Integer i = 0; i < loggedInAgents.size(); i++) {
                    DeliveryAgent agent = loggedInAgents.get(i);
                    System.out.println("    " + (i + 1) + ". " + agent.getUsername()
                            + " | Status: " + agent.getStatus().getDisplayName());
                }
                System.out.println("----------");
            }
            if(deliveryAgentCount < 2){
                System.out.println("1. Sign Up");
            }
            System.out.println("2. Login");
            if (!loggedInAgents.isEmpty()) {
                System.out.println("3. Switch to logged-in agent");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    signUp();
//                    if(deliveryAgentCount >= 2){
//                        System.out.println("Can't have more than 2 delivery partners.");
//                        break;
//                    }
//                    if(signUp())
//                        deliveryAgentCount += 1;
//                    break;
                case "2":
                    login();
                    break;
                case "3":
                    if (!loggedInAgents.isEmpty()) {
                        switchAgent(loggedInAgents);
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case "0":
                    return Boolean.TRUE;
                default:
                    System.out.println("Invalid choice.");
            }
            return Boolean.FALSE;
        }

        System.out.println("Active agent: " + activeAgent.getUsername()
                + " | Status: " + activeAgent.getStatus().getDisplayName());
        System.out.println("1. View Assigned Order");
        System.out.println("2. Start Delivery (Pick Up Order)");
        System.out.println("3. Mark Order as Delivered");
        System.out.println("4. Switch Agent");
        System.out.println("5. Logout");
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
                switchToOtherAgent();
                break;
            case "5":
                logout();
                break;
            case "0":
                return Boolean.TRUE;
            default:
                System.out.println("Invalid choice.");
        }
        return Boolean.FALSE;
    }

    private Boolean signUp() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = deliveryAgentService.signUp("delivery_agent", username, password);
        if (user instanceof DeliveryAgent) {
            activeAgent = (DeliveryAgent) user;
            DeliveryAgentObserver observer = new DeliveryAgentObserver(activeAgent.getUserId(),
                    activeAgent.getUsername());
            eventManager.subscribe("ORDER_READY_FOR_DELIVERY", observer);

            System.out.println("Delivery agent account created and logged in!");

            adminService.processDeliveryQueue();
            return true;
        }
        return false;
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = null;
        try {
            user = deliveryAgentService.login(username, password);
        }catch (UserNotFoundException e){
            System.out.println(e.getMessage());
        }
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
        deliveryAgentService.logout(activeAgent.getUsername());
        activeAgent = null;
    }

    private void switchAgent(List<DeliveryAgent> loggedInAgents) {
        System.out.print("Enter agent number to switch to: ");
        Integer index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= loggedInAgents.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        activeAgent = loggedInAgents.get(index);
        System.out.println("Switched to agent: " + activeAgent.getUsername()
                + " | Status: " + activeAgent.getStatus().getDisplayName());
    }

    private void switchToOtherAgent() {
        List<DeliveryAgent> loggedInAgents = getLoggedInAgents();
        if (loggedInAgents.size() <= 1) {
            System.out.println("No other agent is logged in. Sign up or login another agent first.");
            activeAgent = null;
            return;
        }
        System.out.println("Available agents:");
        for (Integer i = 0; i < loggedInAgents.size(); i++) {
            DeliveryAgent agent = loggedInAgents.get(i);
            String marker = agent.getUserId().equals(activeAgent.getUserId()) ? " (current)" : "";
            System.out.println("    " + (i + 1) + ". " + agent.getUsername()
                    + " | Status: " + agent.getStatus().getDisplayName() + marker);
        }
        switchAgent(loggedInAgents);
    }

    private List<DeliveryAgent> getLoggedInAgents() {
        List<DeliveryAgent> agents = new ArrayList<>();
        for (User user : deliveryAgentService.getLoggedInUsers()) {
            if (user instanceof DeliveryAgent) {
                agents.add((DeliveryAgent) user);
            }
        }
        return agents;
    }

    private void viewAssignedOrder() {
        deliveryAgentService.viewAssignedOrder(activeAgent, orderService);
    }

    private void startDelivery() {
        deliveryAgentService.startDelivery(activeAgent, orderService);
    }

    private void markDelivered() {
        deliveryAgentService.markOrderAsDelivered(activeAgent, orderService, adminService);
    }
}
