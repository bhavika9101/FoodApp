package panel;

import exception.EmptyCartException;
import model.enums.PaymentMode;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.payment.Discount;
import model.payment.Payment;
import model.payment.PaymentFactory;
import model.payment.PaymentStrategy;
import model.user.Customer;
import model.user.User;
import observer.CustomerObserver;
import observer.EventManager;
import service.*;

import java.util.*;

public class CustomerPanel {
    private final CustomerService customerService;
    private final AdminService adminService;
    private final OrderService orderService;
    private final EventManager eventManager;
    private final Scanner scanner;
    private Customer loggedInCustomer;
    private final CartService cartService = new CartService();

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
        System.out.println("7. Edit customer profile");
        System.out.println("8. Logout");
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
                    System.err.println(e.getMessage());
                }
                break;
            case "4":
                try {
                    removeFromCart();
                } catch (EmptyCartException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "5":
                try {
                    placeOrder();
                } catch (EmptyCartException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "6":
                viewMyOrders();
                break;
            case "7":
                editCustomerProfile();
                break;
            case "8":
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
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        User user = customerService.signUp("customer", username, password, phone);
        if (user instanceof Customer) {
            loggedInCustomer = (Customer) user;
            System.out.print("Enter your delivery address: ");
            String address = scanner.nextLine().trim();
            loggedInCustomer.setAddress(address);

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
        User user = customerService.login(username, password);
        if (user instanceof Customer) {
            loggedInCustomer = (Customer) user;
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
        customerService.logout(loggedInCustomer);
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

        cartService.addToCart(loggedInCustomer.getUserId(), selectedItem, quantity);
    }

    private void viewCart() throws EmptyCartException {
        if (cartService.isCartEmpty(loggedInCustomer.getUserId())) {
            throw new EmptyCartException("Can't view empty cart.");
        }
        System.out.println("\n--- Your Cart ---");
        Map<MenuItem, Integer> items = cartService.getCartItems(loggedInCustomer.getUserId());
        double total = 0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();
            double lineTotal = item.getPrice() * qty;
            total += lineTotal;
            System.out.printf("  %-25s x%d  Rs.%.2f%n", item.getName(), qty, lineTotal);
        }
        System.out.printf("  Cart Total: Rs.%.2f%n", total);
    }

    private void removeFromCart() throws EmptyCartException {
        if (cartService.isCartEmpty(loggedInCustomer.getUserId())) {
            throw new EmptyCartException("Can't remove from empty cart.");
        }
        Map<MenuItem, Integer> cartMap = cartService.getCartItems(loggedInCustomer.getUserId());
        List<MenuItem> cartItems = new ArrayList<>(cartMap.keySet());
        System.out.println("\nCart Items:");
        for (int i = 0; i < cartItems.size(); i++) {
            MenuItem item = cartItems.get(i);
            System.out.printf("%d. %-25s (Qty: %d)%n", (i + 1), item.getName(), cartMap.get(item));
        }
        System.out.print("Enter item number to remove: ");
        int itemIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (itemIndex < 0 || itemIndex >= cartItems.size()) {
            System.out.println("Invalid item number.");
            return;
        }
        MenuItem toRemove = cartItems.get(itemIndex);
        int currentQty = cartMap.get(toRemove);
        System.out.print("Current quantity: " + currentQty + ". How many to remove? ");
        int removeQty = Integer.parseInt(scanner.nextLine().trim());
        if (removeQty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }
        if (removeQty >= currentQty) {
            cartService.removeFromCart(loggedInCustomer.getUserId(), toRemove.getId());
            System.out.println("Removed all " + toRemove.getName() + " from cart.");
        } else {
            cartService.updateQuantity(loggedInCustomer.getUserId(), toRemove.getId(), currentQty - removeQty);
            System.out.println("Removed " + removeQty + " x " + toRemove.getName() + " from cart. Remaining: "
                    + (currentQty - removeQty));
        }
    }

    private void placeOrder() throws EmptyCartException {
        if (cartService.isCartEmpty(loggedInCustomer.getUserId())) {
            throw new EmptyCartException("No items in cart. Can't place order.");
        }

        Map<MenuItem, Integer> cartItems = cartService.getCartItems(loggedInCustomer.getUserId());

        System.out.println("\n--- Order Summary ---");
        double subtotal = 0;
        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();
            double lineTotal = item.getPrice() * qty;
            subtotal += lineTotal;
            System.out.printf("  %-25s x%d  Rs.%.2f%n", item.getName(), qty, lineTotal);
        }

        Discount discount = DiscountService.getDiscount(subtotal);
        double discountAmount = subtotal * discount.getRate();
        double finalAmount = subtotal - discountAmount;

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
        String paymentIdentifier = null;
        if ("1".equals(paymentChoice)) {
            paymentMode = PaymentMode.CASH;
        } else if ("2".equals(paymentChoice)) {
            System.out.print("Enter UPI ID: ");
            paymentIdentifier = scanner.nextLine().trim();
            paymentMode = PaymentMode.UPI;
        } else {
            System.out.println("Invalid payment method. Order cancelled.");
            return;
        }

        PaymentStrategy paymentStrategy = PaymentFactory.createPayment(paymentMode.toString());
        Payment payment = new Payment(null, finalAmount, paymentMode, paymentStrategy, paymentIdentifier);
        payment.processPayment();

        String address = loggedInCustomer.getAddress() != null ? loggedInCustomer.getAddress() : "N/A";

        Order order = orderService.addOrder(
                loggedInCustomer.getUserId(),
                loggedInCustomer.getUsername(),
                address,
                cartItems,
                subtotal,
                discountAmount,
                finalAmount,
                paymentMode.name());

        if (order != null) {
            InvoiceService invoiceService = new InvoiceService(orderService);
            invoiceService.printInvoice(order.getOrderId());
            cartService.clearCart(loggedInCustomer.getUserId());

            eventManager.notifyObservers("ORDER_PLACED", order);
        }
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

    private void editCustomerProfile() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();
        if (!loggedInCustomer.getPassword().equals(currentPassword)) {
            System.out.println("Wrong password. Attempt to edit profile failed.");
            return;
        }
        System.out.print("New username (enter 0 to keep old one): ");
        String username = scanner.nextLine().trim();
        System.out.print("New password (enter 0 to keep old one): ");
        String password = scanner.nextLine().trim();
        System.out.print("New phone number (enter 0 to keep old one): ");
        String phoneNumber = scanner.nextLine().trim();

        String newUsername = "0".equals(username) ? loggedInCustomer.getUsername() : username;
        String newPassword = "0".equals(password) ? loggedInCustomer.getPassword() : password;
        String newPhone = "0".equals(phoneNumber) ? loggedInCustomer.getPhoneNumber() : phoneNumber;

        customerService.editUserProfile(loggedInCustomer, newUsername, newPassword, newPhone);
    }
}
