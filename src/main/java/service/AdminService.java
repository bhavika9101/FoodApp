package service;

import model.enums.OrderStatus;
import model.order.MenuCategory;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.payment.Discount;
import model.user.DeliveryAgent;
import model.user.User;
import model.enums.DeliveryAgentStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AdminService extends BaseService {
    private MenuComponent menu;
    private final OrderService orderService;
    private final DeliveryAgentService deliveryAgentService;
    private final Queue<Integer> deliveryQueue = new LinkedList<>();
    private Double revenue;
//    map user id and user object
    public AdminService(OrderService orderService, DeliveryAgentService deliveryAgentService) {
        this.orderService = orderService;
        this.deliveryAgentService = deliveryAgentService;
        revenue = 0.0;
    }

    @Override
    public User signUp(String type, String username, String password) {
        if (!type.equalsIgnoreCase("admin")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password);
    }

    public void setMenu(MenuComponent menu) {
        this.menu = menu;
    }

    public MenuComponent getMenu() {
        return menu;
    }
    public void addMenuItemToCategory(Integer categoryId, MenuItem item) {
        if (menu == null) {
            System.out.println("Menu not initialized.");
            return;
        }
        MenuComponent category = findCategory(menu, categoryId);
        if (category != null) {
            category.add(item);
            System.out.println("'" + item.getName() + "' added to '" + categoryId + "' successfully.");
        } else {
            System.out.println("Category '" + categoryId + "' not found.");
        }
    }

    public void addCategory(String categoryName) {
        if (menu == null) {
            System.out.println("Menu not initialized.");
            return;
        }
        MenuComponent newCategory = new MenuCategory(categoryName);
        menu.add(newCategory);
        System.out.println("Category '" + categoryName + "' added to menu.");
    }

    private MenuComponent findCategory(MenuComponent component, Integer categoryId) {
        if (component.isComponent() && component.getId().equals(categoryId)) {
            return component;
        }
        if (component.isComponent() && component.getComponentSet() != null) {
            for (MenuComponent child : component.getComponentSet()) {
                MenuComponent found = findCategory(child, categoryId);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
    public void addDiscount(Double threshold, Double rate) {
        Boolean success = DiscountService.add(new Discount(threshold, rate));
        if (success) {
            System.out.println("Discount added: " + (rate * 100) + "% off for orders above Rs." + threshold);
        } else {
            System.out.println("Discount already exists for threshold Rs." + threshold);
        }
    }

    public void removeDiscount(Double threshold) {
        Boolean success = DiscountService.remove(threshold);
        if (success) {
            System.out.println("Discount removed for threshold Rs." + threshold);
        } else {
            System.out.println("No discount found for threshold Rs." + threshold);
        }
    }

    public List<Order> viewPendingOrders() {
        return orderService.getPendingOrders();
    }

    public List<Order> viewApprovedOrders() {
        return orderService.getApprovedOrders();
    }

    public void approveOrder(Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order #" + orderId + " not found.");
            return;
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            System.out.println("Order #" + orderId + " is not in PLACED status. Current: " + order.getStatus().getDisplayName());
            return;
        }
        orderService.updateOrderStatus(orderId, OrderStatus.APPROVED);
        System.out.println("Order #" + orderId + " approved successfully.");
        revenue += order.getFinalAmount();
        DeliveryAgent availableAgent = deliveryAgentService.findAvailableAgent();
        if (availableAgent != null) {
            assignOrderToAgent(order, availableAgent);
        } else {
            deliveryQueue.add(orderId);
            System.out.println("No delivery agent available. Order #" + orderId + " added to delivery queue.");
            System.out.println("It will be auto-assigned when an agent is free. Queue size: " + deliveryQueue.size());
        }
    }

    public void queueOrderForDelivery(Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("  Order #" + orderId + " not found.");
            return;
        }
        if (order.getStatus() != OrderStatus.APPROVED) {
            System.out.println("  Order #" + orderId + " must be APPROVED first. Current: " + order.getStatus());
            return;
        }

        DeliveryAgent availableAgent = deliveryAgentService.findAvailableAgent();
        if (availableAgent != null) {
            assignOrderToAgent(order, availableAgent);
        } else {
            deliveryQueue.add(orderId);
            orderService.updateOrderStatus(orderId, OrderStatus.READY_FOR_DELIVERY);
            System.out.println(
                    "  Order #" + orderId + " added to delivery queue. Will be assigned when an agent is free.");
            System.out.println("  Current queue size: " + deliveryQueue.size());
        }
    }

    private void assignOrderToAgent(Order order, DeliveryAgent agent) {
        order.setAssignedAgentId(agent.getUserId());
        order.setAssignedAgentName(agent.getUsername());
        agent.setStatus(DeliveryAgentStatus.ON_DELIVERY);
        agent.setCurrentOrderId(order.getOrderId());

        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY_FOR_DELIVERY);
        System.out.println("  Order #" + order.getOrderId() + " assigned to agent: " + agent.getUsername());
    }

    public void processDeliveryQueue() {
        if (deliveryQueue.isEmpty()) {
            return;
        }
        DeliveryAgent availableAgent = deliveryAgentService.findAvailableAgent();
        if (availableAgent == null) {
            return;
        }
        Integer nextOrderId = deliveryQueue.poll();
        Order nextOrder = orderService.getOrderById(nextOrderId);
        if (nextOrder != null) {
            assignOrderToAgent(nextOrder, availableAgent);
            System.out.println("\n[AUTO-ASSIGNED] Queued order #" + nextOrderId
                    + " assigned to agent: " + availableAgent.getUsername());
        }
    }

    public Queue<Integer> getDeliveryQueue() {
        return deliveryQueue;
    }

    public void collectAllMenuItems(MenuComponent component, java.util.List<MenuItem> itemList) {
        if (!component.isComponent()) {
            itemList.add((MenuItem) component);
        } else if (component.getComponentSet() != null) {
            for (MenuComponent child : component.getComponentSet()) {
                collectAllMenuItems(child, itemList);
            }
        }
    }
    public Double getRevenue(){
        return revenue;
    }

    public List<MenuComponent> getCategoryList(MenuComponent menu){
        List<MenuComponent> categories = new ArrayList<>();
        for(MenuComponent menuComponent: menu.getComponentSet()){
            if(menuComponent instanceof MenuCategory){
                categories.add(menuComponent);
                if(menuComponent.getComponentSet() != null){
                    categories.addAll(getCategoryList(menuComponent));
                }
            }
        }
        return categories;
    }
}
