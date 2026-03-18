package service;

import dao.CartDAO;
import dao.CartItemDAO;
import model.order.MenuItem;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Test
    void addToCart_happyPath_insertsItemIntoCart() throws SQLException {
        /*
         * CartService creates CartDAO and CartItemDAO internally.
         * We intercept both constructors so no real DB is touched.
         */
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getOrCreateCart(1)).thenReturn(10));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class,
                 (mock, ctx) -> doNothing().when(mock).insert(10, 5, 2))) {

            CartService cartService = new CartService();
            MenuItem item = new MenuItem(5, "Burger", 120.0);

            // Should NOT throw
            assertDoesNotThrow(() -> cartService.addToCart(1, item, 2));

            CartItemDAO constructedItemDao = cartItemDaoMock.constructed().get(0);
            verify(constructedItemDao).insert(10, 5, 2);
        }
    }

    @Test
    void addToCart_whenDaoThrows_handlesException() throws SQLException {
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getOrCreateCart(1)).thenThrow(new SQLException("DB error")));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class)) {

            CartService cartService = new CartService();
            MenuItem item = new MenuItem(5, "Burger", 120.0);

            // Must NOT propagate the exception to caller
            assertDoesNotThrow(() -> cartService.addToCart(1, item, 2));
        }
    }

    // ─────────────────────────────────────────────
    // removeFromCart Tests
    // ─────────────────────────────────────────────

    @Test
    void removeFromCart_whenCartExists_deletesItem() throws SQLException {
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getCartIdByCustomerId(1)).thenReturn(10));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class,
                 (mock, ctx) -> doNothing().when(mock).delete(10, 5))) {

            CartService cartService = new CartService();
            cartService.removeFromCart(1, 5);

            CartItemDAO constructedItemDao = cartItemDaoMock.constructed().get(0);
            verify(constructedItemDao).delete(10, 5);
        }
    }

    @Test
    void removeFromCart_whenCartDoesNotExist_doesNothing() throws SQLException {
        // getCartIdByCustomerId returns -1 → no delete should happen
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getCartIdByCustomerId(1)).thenReturn(-1));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class)) {

            CartService cartService = new CartService();
            cartService.removeFromCart(1, 5);

            CartItemDAO constructedItemDao = cartItemDaoMock.constructed().get(0);
            verify(constructedItemDao, never()).delete(anyInt(), anyInt());
        }
    }

    // ─────────────────────────────────────────────
    // clearCart Tests
    // ─────────────────────────────────────────────

    @Test
    void clearCart_whenCartExists_deletesAllItems() throws SQLException {
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getCartIdByCustomerId(1)).thenReturn(10));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class,
                 (mock, ctx) -> doNothing().when(mock).deleteByCartId(10))) {

            CartService cartService = new CartService();
            cartService.clearCart(1);

            CartItemDAO constructedItemDao = cartItemDaoMock.constructed().get(0);
            verify(constructedItemDao).deleteByCartId(10);
        }
    }

    @Test
    void clearCart_whenCartDoesNotExist_doesNothing() throws SQLException {
        try (MockedConstruction<CartDAO> cartDaoMock = mockConstruction(CartDAO.class,
                 (mock, ctx) -> when(mock.getCartIdByCustomerId(1)).thenReturn(-1));
             MockedConstruction<CartItemDAO> cartItemDaoMock = mockConstruction(CartItemDAO.class)) {

            CartService cartService = new CartService();
            cartService.clearCart(1);

            CartItemDAO constructedItemDao = cartItemDaoMock.constructed().get(0);
            // deleteByCartId should never be called if cart doesn't exist
            verify(constructedItemDao, never()).deleteByCartId(anyInt());
        }
    }
}
