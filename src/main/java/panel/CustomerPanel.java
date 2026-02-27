package panel;

import exception.EmptyCartException;
import exception.UserNotFoundException;
import model.enums.PaymentMode;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.payment.Payment;
import model.payment.PaymentFactory;
import model.payment.PaymentStrategy;
import model.user.Customer;
import model.user.User;
import observer.CustomerObserver;
import observer.EventManager;
import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomerPanel {
    private final CustomerService customerService;
    private final AdminService adminService;
    private final OrderService orderService;
    private final EventManager eventManager;
    private final Scanner scanner;
    private Customer loggedInCustomer;
    private CartService cartService;

    public CustomerPanel(CustomerService customerService, AdminService adminService,
            OrderService orderService, EventManager eventManager, Scanner scanner) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.orderService = orderService;
        this.eventManager = eventManager;
        this.scanner = scanner;
    }

    public Boolean run() {
        System.out.println("\n============================================");
        System.out.println("           CUSTOMER PANEL");
        System.out.println("============================================");

        if (loggedInCustomer == null) {
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    signUp();
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

        System.out.println("Logged in as: " + loggedInCustomer.getUsername());
        System.out.println("1. View Menu");
        System.out.println("2. Add Item to Cart");
        System.out.println("3. View Cart");
        System.out.println("4. Remove Item from Cart");
        System.out.println("5. Place Order");
        System.out.println("6. View My Orders");
        System.out.println("7. Logout");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewMenu();
                break;
            case "2":
                addToCart();
                break;
            case "3":
                try {
                    viewCart();
                } catch (EmptyCartException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "4":
                try {
                    removeFromCart();
                } catch (EmptyCartException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "5":
                try {
                    placeOrder();
                }catch (EmptyCartException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "6":
                viewMyOrders();
                break;
            case "7":
                logout();
                break;
            case "0":
                return Boolean.TRUE;
            default:
                System.out.println("Invalid choice.");
        }
        return Boolean.FALSE;
    }

    private void signUp() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = customerService.signUp("customer", username, password);
        if (user instanceof Customer) {
            loggedInCustomer = (Customer) user;
            System.out.print("Enter your delivery address: ");
            String address = scanner.nextLine().trim();
            loggedInCustomer.setAddress(address);
            cartService = new CartService();

            CustomerObserver observer = new CustomerObserver(loggedInCustomer.getUserId(),
                    loggedInCustomer.getUsername());
            eventManager.subscribe("ORDER_PLACED", observer);
            eventManager.subscribe("ORDER_APPROVED", observer);
            eventManager.subscribe("ORDER_READY_FOR_DELIVERY", observer);
            eventManager.subscribe("ORDER_OUT_FOR_DELIVERY", observer);
            eventManager.subscribe("ORDER_DELIVERED", observer);

            System.out.println("Customer account created and logged in!");
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = null;
        try {
            user = customerService.login(username, password);
        }catch (UserNotFoundException e){
            System.out.println(e.getMessage());
        }
        if (user instanceof Customer) {
            loggedInCustomer = (Customer) user;
            if (cartService == null) {
                cartService = new CartService();
            }
            if (loggedInCustomer.getAddress() == null || loggedInCustomer.getAddress().isEmpty()) {
                System.out.print("Enter your delivery address: ");
                String address = scanner.nextLine().trim();
                loggedInCustomer.setAddress(address);
            }

            CustomerObserver observer = new CustomerObserver(loggedInCustomer.getUserId(),
                    loggedInCustomer.getUsername());
            eventManager.subscribe("ORDER_PLACED", observer);
            eventManager.subscribe("ORDER_APPROVED", observer);
            eventManager.subscribe("ORDER_READY_FOR_DELIVERY", observer);
            eventManager.subscribe("ORDER_OUT_FOR_DELIVERY", observer);
            eventManager.subscribe("ORDER_DELIVERED", observer);

            System.out.println("Customer logged in successfully!");
        }
    }

    private void logout() {
        customerService.logout(loggedInCustomer.getUsername());
        loggedInCustomer = null;
    }

    private void viewMenu() {
        MenuComponent menu = adminService.getMenu();
        if (menu != null) {
            System.out.println("\n========== MENU ==========");
            menu.print();
            System.out.println("==========================");
        } else {
            System.out.println("Menu not available.");
        }
    }

    private void addToCart() {
        MenuComponent menu = adminService.getMenu();
        if (menu == null) {
            System.out.println("Menu not available.");
            return;
        }
        viewMenu();

        List<MenuItem> allItems = new ArrayList<>();
        adminService.collectAllMenuItems(menu, allItems);

        if (allItems.isEmpty()) {
            System.out.println("No items available.");
            return;
        }

        java.util.Map<Integer, MenuItem> itemMap = new java.util.HashMap<>();
        for (MenuItem item : allItems) {
            itemMap.put(item.getId(), item);
        }

        System.out.print("\nEnter item ID from menu: ");
        Integer itemId = Integer.parseInt(scanner.nextLine().trim());
        MenuItem selectedItem = itemMap.get(itemId);
        if (selectedItem == null) {
            System.out.println("Invalid item ID.");
            return;
        }
        System.out.print("Enter quantity: ");
        Integer quantity = Integer.parseInt(scanner.nextLine().trim());
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        cartService.addToCart(selectedItem, quantity);
        System.out.println("Added " + quantity + " x " + selectedItem.getName() + " to cart.");
    }

    private void viewCart() throws EmptyCartException {
        if (cartService.isEmpty()) {
            throw new EmptyCartException("Can't view empty cart.");
        }
        System.out.println("\n--- Your Cart ---");
        cartService.printCart();
    }

    private void removeFromCart() throws EmptyCartException {
        Map<MenuItem, Integer> cartMap = cartService.getCartItemMap();
        if (cartService.isEmpty()) {
            throw new EmptyCartException("Can't remove from empty cart.");
        }
        List<MenuItem> cartItems = new ArrayList<>(cartMap.keySet());
        System.out.println("\nCart Items:");
        for (Integer i = 0; i < cartItems.size(); i++) {
            MenuItem item = cartItems.get(i);
            System.out.printf("%d. %-25s (Qty: %d)%n", (i + 1), item.getName(), cartMap.get(item));
        }
        System.out.print("Enter item number to remove: ");
        Integer itemIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (itemIndex < 0 || itemIndex >= cartItems.size()) {
            System.out.println("Invalid item number.");
            return;
        }
        MenuItem toRemove = cartItems.get(itemIndex);
        Integer currentQty = cartMap.get(toRemove);
        System.out.print("Current quantity: " + currentQty + ". How many to remove? ");
        Integer removeQty = Integer.parseInt(scanner.nextLine().trim());
        if (removeQty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }
        if (removeQty >= currentQty) {
            cartService.removeFromCart(toRemove);
            System.out.println("Removed all " + toRemove.getName() + " from cart.");
        } else {
            cartService.reduceQuantity(toRemove, removeQty);
            System.out.println("Removed " + removeQty + " x " + toRemove.getName() + " from cart. Remaining: "
                    + (currentQty - removeQty));
        }
    }

    private void placeOrder() throws EmptyCartException {
        if (cartService.isEmpty()) {
            throw new EmptyCartException("No items in. Can't place order.");
        }

        System.out.println("\n--- Order Summary ---");
        cartService.printCart();

        Double subtotal = cartService.calculateTotalValue();
        Double discountAmount = cartService.findDiscount(subtotal);
        Double finalAmount = cartService.findFinalAmount(subtotal, discountAmount);

        System.out.printf("Subtotal     : Rs.%.2f%n", subtotal);
        System.out.printf("Discount     : Rs.%.2f%n", discountAmount);
        System.out.printf("Final Amount : Rs.%.2f%n", finalAmount);

        System.out.print("\nConfirm order? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Order cancelled.");
            return;
        }

        System.out.println("Choose Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        System.out.print("Choose: ");
        String paymentChoice = scanner.nextLine().trim();
        PaymentMode paymentMode;
        PaymentStrategy paymentStrategy;
        String paymentIdentifier = null;
        if ("1".equals(paymentChoice)) {
            paymentMode = PaymentMode.CASH;
        } else if ("2".equals(paymentChoice)) {
            System.out.println("Enter upi id: ");
            paymentIdentifier = scanner.nextLine().trim();
            paymentMode = PaymentMode.UPI;
        } else {
            System.out.println("Invalid payment method. Order cancelled.");
            return;
        }
        paymentStrategy = PaymentFactory.createPayment(paymentMode.toString());

        Payment payment = new Payment(null, finalAmount, paymentMode, paymentStrategy, paymentIdentifier);
        payment.processPayment();

        Order order = new Order(
                loggedInCustomer.getUserId(),
                loggedInCustomer.getUsername(),
                loggedInCustomer.getAddress(),
                cartService.getCartItemMap(),
                subtotal,
                discountAmount,
                finalAmount);
        order.setPaymentMode(paymentMode);

        orderService.addOrder(order);

        InvoiceService.printInvoice(order);
        cartService.clearCart();
    }

    private void viewMyOrders() {
        List<Order> myOrders = orderService.getOrdersByCustomerId(loggedInCustomer.getUserId());
        if (myOrders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }
        System.out.println("\n--- Your Orders ---");
        for (Order order : myOrders) {
            System.out.println("Order #" + order.getOrderId()
                    + " | Amount: Rs." + String.format("%.2f", order.getFinalAmount())
                    + " | Status: " + order.getStatus().getDisplayName()
                    + (order.getAssignedAgentName() != null ? " | Agent: " + order.getAssignedAgentName() : ""));
        }
    }
}
