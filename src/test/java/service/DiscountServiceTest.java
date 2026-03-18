package service;

import dao.DiscountDAO;
import model.payment.Discount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiscountServiceTest {

    private static Unsafe UNSAFE;
    private static long DISCOUNT_DAO_OFFSET;

    private DiscountDAO mockDiscountDAO;

    @BeforeAll
    static void setupUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        UNSAFE = (Unsafe) f.get(null);

        Field daoField = DiscountService.class.getDeclaredField("discountDAO");
        DISCOUNT_DAO_OFFSET = UNSAFE.staticFieldOffset(daoField);
    }

    @BeforeEach
    void injectMockDAO() {
        mockDiscountDAO = mock(DiscountDAO.class);
        Field daoField;
        try {
            daoField = DiscountService.class.getDeclaredField("discountDAO");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        // Overwrite the static field with our mock
        UNSAFE.putObject(UNSAFE.staticFieldBase(daoField), DISCOUNT_DAO_OFFSET, mockDiscountDAO);
    }


    @Test
    void add_whenDiscountAlreadyExists_returnsFalse() throws SQLException {
        when(mockDiscountDAO.insert(300.0, 0.15)).thenReturn(false);

        Boolean result = DiscountService.add(new Discount(300.0, 0.15));

        assertFalse(result, "add() should return false when discount already exists");
    }

    @Test
    void add_whenDaoThrows_returnsFalse() throws SQLException {
        when(mockDiscountDAO.insert(anyDouble(), anyDouble()))
                .thenThrow(new SQLException("DB down"));

        Boolean result = DiscountService.add(new Discount(300.0, 0.15));

        assertFalse(result, "add() should return false on DB error, not throw");
    }

    @Test
    void remove_whenDiscountExists_returnsTrue() throws SQLException {
        when(mockDiscountDAO.delete(300.0)).thenReturn(true);

        Boolean result = DiscountService.remove(300.0);

        assertTrue(result, "remove() should return true when discount is deleted");
        verify(mockDiscountDAO).delete(300.0);
    }

    @Test
    void remove_whenDiscountDoesNotExist_returnsFalse() throws SQLException {
        when(mockDiscountDAO.delete(300.0)).thenReturn(false);

        Boolean result = DiscountService.remove(300.0);

        assertFalse(result, "remove() should return false when no discount was found");
    }

    @Test
    void remove_whenDaoThrows_returnsFalse() throws SQLException {
        when(mockDiscountDAO.delete(anyDouble()))
                .thenThrow(new SQLException("DB down"));

        Boolean result = DiscountService.remove(300.0);

        assertFalse(result, "remove() should return false on DB error, not throw");
    }
}
