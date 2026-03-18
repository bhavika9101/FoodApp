package service;

import dao.OrderDAO;
import dao.OrderItemDAO;
import model.enums.OrderStatus;
import model.enums.PaymentMode;
import model.order.MenuItem;
import model.order.Order;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Test
    void addOrder_happyPath_returnsCreatedOrder() throws SQLException {
        Map<MenuItem, Integer> items = new LinkedHashMap<>();
        MenuItem burger = new MenuItem(5, "Burger", 120.0);
        items.put(burger, 2);

        try (MockedConstruction<OrderDAO> orderDaoMock = mockConstruction(OrderDAO.class,
                 (mock, ctx) -> when(mock.insertOrder(1, "123 Main St", 240.0, 0.0, 240.0, "CASH", "PLACED"))
                         .thenReturn(101));
             MockedConstruction<OrderItemDAO> orderItemDaoMock = mockConstruction(OrderItemDAO.class,
                 (mock, ctx) -> doNothing().when(mock).insert(101, 5, 2, 120.0))) {

            OrderService orderService = new OrderService();
            Order order = orderService.addOrder(
                    1, "Alice", "123 Main St", items,
                    240.0, 0.0, 240.0, "CASH");

            assertNotNull(order, "Order should be created");
            assertEquals(101, order.getOrderId());
            assertEquals(OrderStatus.PLACED, order.getStatus());
            assertEquals(PaymentMode.CASH, order.getPaymentMode());

            OrderItemDAO constructedItemDao = orderItemDaoMock.constructed().get(0);
            verify(constructedItemDao).insert(101, 5, 2, 120.0);
        }
    }

    @Test
    void addOrder_whenDaoThrows_returnsNull() throws SQLException {
        Map<MenuItem, Integer> items = new LinkedHashMap<>();
        items.put(new MenuItem(5, "Burger", 120.0), 1);

        try (MockedConstruction<OrderDAO> orderDaoMock = mockConstruction(OrderDAO.class,
                 (mock, ctx) -> when(mock.insertOrder(anyInt(), anyString(), anyDouble(),
                         anyDouble(), anyDouble(), anyString(), anyString()))
                         .thenThrow(new SQLException("DB error")));
             MockedConstruction<OrderItemDAO> orderItemDaoMock = mockConstruction(OrderItemDAO.class)) {

            OrderService orderService = new OrderService();
            Order order = orderService.addOrder(1, "Alice", "Street", items, 120.0, 0.0, 120.0, "CASH");

            assertNull(order, "Should return null when DB insertion fails");
        }
    }

    @Test
    void updateOrderStatus_callsDAOWithCorrectStatus() throws SQLException {
        try (MockedConstruction<OrderDAO> orderDaoMock = mockConstruction(OrderDAO.class,
                 (mock, ctx) -> doNothing().when(mock).updateOrderStatus(101, "APPROVED"));
             MockedConstruction<OrderItemDAO> orderItemDaoMock = mockConstruction(OrderItemDAO.class)) {

            OrderService orderService = new OrderService();
            orderService.updateOrderStatus(101, OrderStatus.APPROVED);

            OrderDAO constructedDao = orderDaoMock.constructed().get(0);
            verify(constructedDao).updateOrderStatus(101, "APPROVED");
        }
    }

    @Test
    void updateOrderStatus_toDelivered_updatesCorrectly() throws SQLException {
        try (MockedConstruction<OrderDAO> orderDaoMock = mockConstruction(OrderDAO.class,
                 (mock, ctx) -> doNothing().when(mock).updateOrderStatus(101, "DELIVERED"));
             MockedConstruction<OrderItemDAO> orderItemDaoMock = mockConstruction(OrderItemDAO.class)) {

            OrderService orderService = new OrderService();
            orderService.updateOrderStatus(101, OrderStatus.DELIVERED);

            OrderDAO constructedDao = orderDaoMock.constructed().get(0);
            verify(constructedDao).updateOrderStatus(101, "DELIVERED");
        }
    }

    @Test
    void updateOrderStatus_whenDaoThrows_handlesException() throws SQLException {
        try (MockedConstruction<OrderDAO> orderDaoMock = mockConstruction(OrderDAO.class,
                 (mock, ctx) -> doThrow(new SQLException("DB error"))
                         .when(mock).updateOrderStatus(anyInt(), anyString()));
             MockedConstruction<OrderItemDAO> orderItemDaoMock = mockConstruction(OrderItemDAO.class)) {

            OrderService orderService = new OrderService();
            assertDoesNotThrow(() -> orderService.updateOrderStatus(101, OrderStatus.APPROVED));
        }
    }
}
