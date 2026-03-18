package service;

import dao.AdminDAO;
import dao.MenuCategoryDAO;
import dao.MenuItemDAO;
import model.enums.DeliveryAgentStatus;
import model.enums.OrderStatus;
import model.order.MenuCategory;
import model.order.MenuComponent;
import model.order.MenuItem;
import model.order.Order;
import model.payment.Discount;
import model.user.Admin;
import model.user.DeliveryAgent;
import model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AdminService extends BaseService {
    private MenuComponent menu;
    private final OrderService orderService;
    private final DeliveryAgentService deliveryAgentService;
    private final Queue<Integer> deliveryQueue = new LinkedList<>();
    private final AdminDAO adminDAO = new AdminDAO();
    private final MenuCategoryDAO menuCategoryDAO = new MenuCategoryDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();

    public AdminService(OrderService orderService, DeliveryAgentService deliveryAgentService) {
        this.orderService = orderService;
        this.deliveryAgentService = deliveryAgentService;
    }

    @Override
    public User signUp(String type, String username, String password, String phone) {
        if (!type.equalsIgnoreCase("admin")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password, phone);
    }

    public void setMenu(MenuComponent menu) {
        this.menu = menu;
    }

    public MenuComponent getMenu() {
        return menu;
    }

    public void addMenuItemToCategory(Integer categoryId, MenuItem item) {
        try {
            int itemId = menuItemDAO.insert(item.getName(), item.getPrice(), categoryId);
            MenuItem dbItem = new MenuItem(itemId, item.getName(), item.getPrice());

            if (menu != null) {
                MenuComponent category = findCategory(menu, categoryId);
                if (category != null) {
                    category.add(dbItem);
                    System.out
                            .println("'" + dbItem.getName() + "' added to '" + category.getName() + "' successfully.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding menu item: " + e.getMessage());
        }
    }

    public void addCategory(String categoryName) {
        try {
            if (menuCategoryDAO.existsByName(categoryName)) {
                System.out.println("Category '" + categoryName + "' already exists.");
                return;
            }
            // Integer parentId = (menu != null) ? menu.getId() : null;
            int categoryId = menuCategoryDAO.insert(categoryName, null);
            MenuComponent newCategory = new MenuCategory(categoryId, categoryName);
            if (menu != null) {
                menu.add(newCategory);
            }
            System.out.println("Category '" + categoryName + "' added to menu. ID: " + categoryId);
        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
        }
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
            System.out.println(
                    "Order #" + orderId + " is not in PLACED status. Current: " + order.getStatus().getDisplayName());
            return;
        }
        orderService.updateOrderStatus(orderId, OrderStatus.APPROVED);
        System.out.println("Order #" + orderId + " approved successfully.");

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
        orderService.updateAssignedAgent(order.getOrderId(), agent.getUserId());
        order.setAssignedAgentId(agent.getUserId());
        order.setAssignedAgentName(agent.getUsername());
        deliveryAgentService.updateAgentStatus(agent.getUserId(), DeliveryAgentStatus.ON_DELIVERY);
        agent.setStatus(DeliveryAgentStatus.ON_DELIVERY);
        agent.setCurrentOrderId(order.getOrderId());

        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY_FOR_DELIVERY);
        System.out.println("  Order #" + order.getOrderId() + " assigned to agent: " + agent.getUsername());
    }

    public void processDeliveryQueue() {
        while (!deliveryQueue.isEmpty()) {
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
        List<Order> unassigned = orderService.getUnassignedApprovedOrders();
        for (Order order : unassigned) {
            DeliveryAgent availableAgent = deliveryAgentService.findAvailableAgent();
            if (availableAgent == null) {
                return;
            }
            assignOrderToAgent(order, availableAgent);
            System.out.println("\n[AUTO-ASSIGNED] Order #" + order.getOrderId()
                    + " assigned to agent: " + availableAgent.getUsername());
        }
    }

    public Queue<Integer> getDeliveryQueue() {
        Queue<Integer> combined = new LinkedList<>(deliveryQueue);
        List<Order> unassigned = orderService.getUnassignedApprovedOrders();
        for (Order order : unassigned) {
            if (!combined.contains(order.getOrderId())) {
                combined.add(order.getOrderId());
            }
        }
        return combined;
    }

    public void collectAllMenuItems(MenuComponent component, List<MenuItem> itemList) {
        if (!component.isComponent()) {
            itemList.add((MenuItem) component);
        } else if (component.getComponentSet() != null) {
            for (MenuComponent child : component.getComponentSet()) {
                collectAllMenuItems(child, itemList);
            }
        }
    }

    public Double getRevenue() {
        try {
            return adminDAO.getRevenue();
        } catch (SQLException e) {
            System.out.println("Error getting revenue: " + e.getMessage());
            return 0.0;
        }
    }

    public List<MenuComponent> getCategoryList(MenuComponent menu) {
        List<MenuComponent> categories = new ArrayList<>();
        for (MenuComponent menuComponent : menu.getComponentSet()) {
            if (menuComponent instanceof MenuCategory) {
                categories.add(menuComponent);
                if (menuComponent.getComponentSet() != null) {
                    categories.addAll(getCategoryList(menuComponent));
                }
            }
        }
        return categories;
    }
}
