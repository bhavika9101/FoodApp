package service;

import dao.*;
import model.order.MenuComponent;
import model.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {
    private AdminService buildAdminService() {
        OrderService mockOrderService = mock(OrderService.class);
        DeliveryAgentService mockAgentService = mock(DeliveryAgentService.class);
        return new AdminService(mockOrderService, mockAgentService);
    }

    @Test
    void signUp_withTypeAdmin_createsUserSuccessfully() throws SQLException {
        try (MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class,
                 (mock, ctx) -> {
                     when(mock.isUsernameTaken("admin1")).thenReturn(false);
                     when(mock.isPhoneNumberTaken("9999999999")).thenReturn(false);
                     when(mock.insertUser("admin1", "pass", "9999999999", "ADMIN")).thenReturn(101);
                 });
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class,
                 (mock, ctx) -> doNothing().when(mock).insertAdmin(101));
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class)) {

            AdminService adminService = buildAdminService();
            User user = adminService.signUp("admin", "admin1", "pass", "9999999999");

            assertNotNull(user, "User should be created successfully");
            assertEquals("admin1", user.getUsername());
        }
    }

    @Test
    void signUp_withWrongType_returnsNull() {
        AdminService adminService = buildAdminService();
        User user = adminService.signUp("customer", "someone", "pass", "1234567890");
        assertNull(user, "signUp should return null for non-admin type");
    }

    @Test
    void signUp_whenUsernameAlreadyTaken_returnsNull() throws SQLException {
        try (MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class,
                 (mock, ctx) -> when(mock.isUsernameTaken("admin1")).thenReturn(true));
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class)) {

            AdminService adminService = buildAdminService();
            User user = adminService.signUp("admin", "admin1", "pass", "9999999999");
            assertNull(user, "Should return null when username is taken");
        }
    }
    @Test
    void addCategory_whenCategoryDoesNotExist_addsSuccessfully() throws SQLException {
        try (MockedConstruction<MenuCategoryDAO> catDaoMock = mockConstruction(MenuCategoryDAO.class,
                 (mock, ctx) -> {
                     when(mock.existsByName("Starters")).thenReturn(false);
                     when(mock.insert("Starters", null)).thenReturn(5);
                 });
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class);
             MockedConstruction<MenuItemDAO> itemDaoMock = mockConstruction(MenuItemDAO.class)) {

            AdminService adminService = buildAdminService();
            MenuComponent rootMenu = mock(MenuComponent.class);
            when(rootMenu.isComponent()).thenReturn(true);
            when(rootMenu.getComponentSet()).thenReturn(new java.util.LinkedHashSet<>());
            adminService.setMenu(rootMenu);

            adminService.addCategory("Starters");

            MenuCategoryDAO constructedDao = catDaoMock.constructed().get(0);
            verify(constructedDao).existsByName("Starters");
            verify(constructedDao).insert("Starters", null);
        }
    }

    @Test
    void addCategory_whenCategoryAlreadyExists_doesNotInsert() throws SQLException {
        try (MockedConstruction<MenuCategoryDAO> catDaoMock = mockConstruction(MenuCategoryDAO.class,
                 (mock, ctx) -> when(mock.existsByName("Starters")).thenReturn(true));
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class);
             MockedConstruction<MenuItemDAO> itemDaoMock = mockConstruction(MenuItemDAO.class)) {

            AdminService adminService = buildAdminService();
            adminService.addCategory("Starters");

            MenuCategoryDAO constructedDao = catDaoMock.constructed().get(0);
            verify(constructedDao, never()).insert(any(), any());
        }
    }


    @Test
    void addDiscount_whenNewDiscount_addsSuccessfully() throws SQLException {
        try (MockedConstruction<DiscountDAO> discDaoMock = mockConstruction(DiscountDAO.class,
                 (mock, ctx) -> when(mock.insert(500.0, 0.10)).thenReturn(true));
             MockedConstruction<MenuCategoryDAO> catDaoMock = mockConstruction(MenuCategoryDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class);
             MockedConstruction<MenuItemDAO> itemDaoMock = mockConstruction(MenuItemDAO.class)) {

            AdminService adminService = buildAdminService();
            adminService.addDiscount(500.0, 0.10);

            DiscountDAO constructedDao = discDaoMock.constructed().get(0);
            verify(constructedDao).insert(500.0, 0.10);
        }
    }

    @Test
    void addDiscount_whenDiscountAlreadyExists_handlesGracefully() throws SQLException {
        try (MockedConstruction<DiscountDAO> discDaoMock = mockConstruction(DiscountDAO.class,
                 (mock, ctx) -> when(mock.insert(500.0, 0.10)).thenReturn(false));
             MockedConstruction<MenuCategoryDAO> catDaoMock = mockConstruction(MenuCategoryDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class);
             MockedConstruction<MenuItemDAO> itemDaoMock = mockConstruction(MenuItemDAO.class)) {

            AdminService adminService = buildAdminService();
            assertDoesNotThrow(() -> adminService.addDiscount(500.0, 0.10));
        }
    }
}